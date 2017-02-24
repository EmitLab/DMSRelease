package us.asu.emitlab.lucene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;

public class QuerySim {
	public HashMap<String, Double> executeQuery(Directory index, int option, String strQueryFile, String strWeightFile) throws IOException, ParseException{
		HashMap<String, Double> results = new HashMap<String, Double>();
	//	System.out.println(strQueryFile);
		String query = strQueryFile;//readData(strQueryFile);
		//System.out.println("Your query...."+query);
		
		ArrayList<String> queryList = readQueryFromInput(query);
		IndexReader reader = DirectoryReader.open(index);		
		// key is the class name, value is the score of the class
		HashMap<String, Double> classWeightMap = readClassWeights(strWeightFile);
		
		if(option==0){			// TF-IDF
			System.out.println("Using TFIDF...");
		}else if(option==1){	// IDF
			System.out.println("Using IDF...");
		}else{					// Lucene scores
			System.out.println("Using Lucene scores...");
		}
		
		for(int i=0; i<queryList.size(); i++)
		{
			HashMap<String, Double> tmpresults = new HashMap<String, Double>();
			String query1 = queryList.get(i);
			double weight = classWeightMap.get(query1.substring(query1.lastIndexOf("_")+1, query1.length()));
			if(option==0){			// TF-IDF
				HashMap<String, Double> querytf = buildQueryTF(query1);
				tmpresults = TFIDFscoreCalculator(reader, "wordList", query1, weight, querytf);
			}else if(option==1){	// IDF
				tmpresults = IDFscoreCalculator(reader, "wordList", query1, weight);
			}else{				
				tmpresults = LucenescoreCalculator(reader, query1);
			}
			
			if(i==0){
				results = tmpresults;
			}else{
			    Iterator<Entry<String, Double>> it = tmpresults.entrySet().iterator();
			    while (it.hasNext()) {
			    	Entry<String, Double> pair = (Entry<String, Double>)it.next();
			    	String strKey = pair.getKey();
			    	double tmp = 0.0;
			    	if(results.containsKey(strKey)){
			    		tmp = results.get(strKey);
			    		results.remove(strKey);			    		
			    	}
			    	results.put(strKey, tmp + pair.getValue());
			    	it.remove();
			    }
			}
		}
		reader.close();
		
		return results;
	}
	
	public HashMap<String, Double> LucenescoreCalculator(IndexReader reader, String query1) throws ParseException, IOException{
		HashMap<String, Double> results = new HashMap<String, Double>();
		
		StandardAnalyzer analyzer = new StandardAnalyzer();
		IndexSearcher searcher = new IndexSearcher(reader);
		
		// the "title" arg specifies the default field to use
		// when no field is explicitly specified in the query.
		Query q = new QueryParser("wordList", analyzer).parse(query1);

		// THE ONE I CODED WAS HERE...are you seeing this?YES
		// and i dont remember where you changed it but let me try to see
		
		// 3. search
		int topK = 100;					// TODO: topK parameter is here!!!
		TopScoreDocCollector collector = TopScoreDocCollector.create(topK);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		for(int j=0; j<hits.length; ++j) 
		{
			int docId = hits[j].doc;
			Document d = searcher.doc(docId);
			String fullfilename = d.get("fileName");
			String docname = fullfilename.substring(fullfilename.lastIndexOf("\\")+1, fullfilename.length());
			double score = hits[j].score;
			results.put(docname, score);
		}
		return results;
	}
	
	public HashMap<String, Double> IDFscoreCalculator(IndexReader reader,String field,String term, double weight) throws IOException 
	{
		HashMap<String, Double> results = new HashMap<String, Double>();
		TFIDFSimilarity tfidfSIM = new DefaultSimilarity();
		Bits liveDocs = MultiFields.getLiveDocs(reader);		
		String[] terms = term.split(" ");
		ArrayList<String> docs = new ArrayList<String>();
		ArrayList<Double> querytfidfs = new ArrayList<Double>();
		ArrayList<HashMap<String, Double>> doctfidfs = new ArrayList<HashMap<String, Double>>();
		
		for(int i=0; i<terms.length; i++){
			double idf = 0.0;
			double queryvalue = 0.0;
			TermsEnum termEnum = MultiFields.getTerms(reader, field).iterator();			
	        BytesRef bytesRef;
	        HashMap<String, Double> hm = new HashMap<String, Double>();
	        
	        while ((bytesRef = termEnum.next()) != null) {   
	        	String word = terms[i].trim();
	        	if(bytesRef.utf8ToString().trim().equals(word)){                  
	        		if (termEnum.seekExact(bytesRef)){
	        			if(idf==0){
	        				idf = tfidfSIM.idf(termEnum.docFreq(), reader.numDocs()) * weight;  // IDF for a keyword
	        				if(queryvalue==0){
	        					queryvalue = idf; 	// Query TF-IDF	        					
	        				}
	        			}
	        			
	        			DocsEnum docsEnum = termEnum.docs(liveDocs, null);
	        			if (docsEnum != null){
	        				int doc; 
	        				while((doc = docsEnum.nextDoc())!=DocIdSetIterator.NO_MORE_DOCS) {
	        					String fullfilename = reader.document(doc).get("fileName");
		                        String docname = fullfilename.substring(fullfilename.lastIndexOf("\\")+1, fullfilename.length());
		                        double doctfidf = idf; 
		                        hm.put(docname, doctfidf);
		                        if(!docs.contains(docname)){
		                        	docs.add(docname);
		                        }
	        				}
	        			} 
	        		} 
	        	}
	        }
	        querytfidfs.add(queryvalue);
	        doctfidfs.add(hm);	        
		}
		
		for(int i=0; i<docs.size(); i++){
			ArrayList<Double> doctfidf = new ArrayList<Double>();
			double querymagnitude = calculateMagnitude(querytfidfs);
			String docname = docs.get(i);
			
			for(int j=0; j<doctfidfs.size(); j++){
				double tmp = 0.0;
				if(doctfidfs.get(j).containsKey(docname)){
					tmp = doctfidfs.get(j).get(docname);
				}
				doctfidf.add(tmp);
			}			
			results.put(docname, calculateCosineSimialrity(querytfidfs, doctfidf, querymagnitude));
		}		
		return results;
	}
	   
	public HashMap<String, Double> TFIDFscoreCalculator(IndexReader reader,String field,String term, double weight, HashMap<String, Double> querytf) throws IOException 
	{
		HashMap<String, Double> results = new HashMap<String, Double>();
		TFIDFSimilarity tfidfSIM = new DefaultSimilarity();
		Bits liveDocs = MultiFields.getLiveDocs(reader);		
		String[] terms = term.split(" ");
		ArrayList<String> docs = new ArrayList<String>();
		ArrayList<Double> querytfidfs = new ArrayList<Double>();
		ArrayList<HashMap<String, Double>> doctfidfs = new ArrayList<HashMap<String, Double>>();
		
		for(int i=0; i<terms.length; i++){
			double idf = 0.0;
			double queryvalue = 0.0;
			TermsEnum termEnum = MultiFields.getTerms(reader, field).iterator();			
	        BytesRef bytesRef;
	        HashMap<String, Double> hm = new HashMap<String, Double>();
	        
	        while ((bytesRef = termEnum.next()) != null) {   
	        	String word = terms[i].trim();
	        	if(bytesRef.utf8ToString().trim().equals(word)){                  
	        		if (termEnum.seekExact(bytesRef)){
	        			if(idf==0){
	        				idf = tfidfSIM.idf(termEnum.docFreq(), reader.numDocs()) * weight;  // IDF for a keyword
	        				if(queryvalue==0){
	        					queryvalue = querytf.get(word) * idf; 	// Query TF-IDF	        					
	        				}
	        			}
	        			
	        			DocsEnum docsEnum = termEnum.docs(liveDocs, null);
	        			if (docsEnum != null){
	        				int doc; 
	        				while((doc = docsEnum.nextDoc())!=DocIdSetIterator.NO_MORE_DOCS) {
	        					String fullfilename = reader.document(doc).get("fileName");
		                        String docname = fullfilename.substring(fullfilename.lastIndexOf("\\")+1, fullfilename.length());
		                        double doctf = docsEnum.freq() / (double)reader.document(doc).get("wordList").split(" ").length;
		                        
		                        double doctfidf = doctf * idf; 
		                        hm.put(docname, doctfidf);
		                        if(!docs.contains(docname)){
		                        	docs.add(docname);
		                        }
	        				}
	        			} 
	        		} 
	        	}
	        }
	        querytfidfs.add(queryvalue);
	        doctfidfs.add(hm);	        
		}
		
		for(int i=0; i<docs.size(); i++){
			ArrayList<Double> doctfidf = new ArrayList<Double>();
			double querymagnitude = calculateMagnitude(querytfidfs);
			String docname = docs.get(i);
			
			for(int j=0; j<doctfidfs.size(); j++){
				double tmp = 0.0;
				if(doctfidfs.get(j).containsKey(docname)){
					tmp = doctfidfs.get(j).get(docname);
				}
				doctfidf.add(tmp);
			}			
			results.put(docname, calculateCosineSimialrity(querytfidfs, doctfidf, querymagnitude));
		}		
		return results;
	}
	
	private double calculateCosineSimialrity(ArrayList<Double> query, ArrayList<Double> doc, double querymagnitude){
		return (calculateDotProduct(query, doc) / (querymagnitude*calculateMagnitude(doc)));
	}
	
	private double calculateDotProduct(ArrayList<Double> doc1, ArrayList<Double> doc2){
		double product = 0.0;		
		for(int i=0; i<doc1.size(); i++){
			product = product + (doc1.get(i) * doc2.get(i));
		}		
		return product;
	}
	
	private double calculateMagnitude(ArrayList<Double> doc){
		double magnitude = 0.0;		
		for(int i=0; i<doc.size(); i++){
			magnitude = magnitude + (doc.get(i) * doc.get(i));
		}
		return Math.pow(magnitude, (1.0/2.0));
	}
	
	private HashMap<String, Double> buildQueryTF(String words){
		String[] wordlist = words.split(" "); 
		HashMap<String, Double> querytf = new HashMap<String, Double>(); 
		int tfcount = 0;
		
		for(int i=0; i<wordlist.length; i++){
			String word = wordlist[i];
			if(querytf.containsKey(word)){
				double value = querytf.get(word);
				querytf.remove(word);
				querytf.put(word, value+1.0);
			}else{
				querytf.put(word, 1.0);
			}
			tfcount++;
		}
		
		for (Map.Entry<String, Double> entry : querytf.entrySet()) {
		    entry.setValue(entry.getValue() / (double) tfcount);
		}
	    
	    return querytf;
	}	
	
	private ArrayList<String> readQueryFromInput(String query)
	{
		ArrayList<String> queryList = new ArrayList<String>();

		// key is the class number, value is the string of words with the same class 
		// number, but stored in the original format of word_className
		HashMap<String, String> wordsMap = new HashMap<String, String>();

		// the format of the input is word_className
		String[] documentTokens = query.split(" ");
		for(int i=0; i<documentTokens.length; i++)
		{
			String sCurrentLine = documentTokens[i];
			String[] wordTokens = sCurrentLine.split("_");
			//String word = wordTokens[0];
			String classNumber = wordTokens[1];

			if(wordsMap.containsKey(classNumber)){
				String classWord = wordsMap.get(classNumber) + " " + sCurrentLine;
				wordsMap.put(classNumber, classWord);
			}
			else{
				wordsMap.put(classNumber, sCurrentLine);
			}
		}
		
		// add the hashmap values to the queryList
		for(String classNumber : wordsMap.keySet())
		{
			queryList.add(wordsMap.get(classNumber));
		}

		return queryList;
	}
	
	private HashMap<String, Double> readClassWeights(String dataFileName)
	{
		// the hashmap returned contains the metadata of the class and the weights, the class number is stored as a string, the value is the weight
		HashMap<String, Double> classWeightMap = new HashMap<String, Double>();
		
		BufferedReader br = null;
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(dataFileName));
			//int i=0;
			sCurrentLine = br.readLine();
			String[] wordTokens = sCurrentLine.split(",");
			for (int i=0; i<wordTokens.length;i++){
				if(!wordTokens[i].equals("NaN"))
					classWeightMap.put(""+(i+1), Double.parseDouble(wordTokens[i]));
			}
		/*	
            while ((sCurrentLine = br.readLine()) != null) 
			{
				if(sCurrentLine.equals("")||sCurrentLine==null){
				//	System.out.println("empty i: "+ i);
				}else{
					System.out.println(sCurrentLine);
					String[] wordTokens = sCurrentLine.split(",");
					// TODO: hardcoding done here! assuming the file format is className weight
					if()
					classWeightMap.put(wordTokens[0], Double.parseDouble(wordTokens[1]));
				//	i++;
				}
			}			*/
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return classWeightMap;
	}
		
	private String readData(String dataFileName) throws IOException
	{
		BufferedReader br = null;
		String wordList = ""; 

		String sCurrentLine;
		System.out.println(dataFileName);
		br = new BufferedReader(new FileReader(dataFileName));

		boolean flag = false;
		while ((sCurrentLine = br.readLine()) != null) {
			if(!flag)
				wordList = sCurrentLine;
			else{
				wordList = wordList + " " + sCurrentLine;
			}
			flag = true;
		}
		br.close();

		return wordList;
	}
}
