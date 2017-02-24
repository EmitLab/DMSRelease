package us.asu.emitlab.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

public class CreateLuceneIndex {
	
	
	/**
	 * This method creates a Lucene index for all the model folders in 
	 * the folder "folderName"
	 * @param folderName
	 */
	public void createLuceneIndexes(String folderName)
	{		
		File file = new File(folderName);
		String[] names = file.list();

		for(String name : names)
		{
			// TODO: HARDCODING! Assuming that the model folder names 
			// are starting with S
			String internalFolderName = folderName +File.separator+ name;
			
		    if (new File(internalFolderName).isDirectory() && name.startsWith("S"))
		    {
		    	// now it comes under the SEIR, SIR, etc folder
		    	File file2 = new File(internalFolderName);
				String[] names2 = file2.list();
				for(String name2 : names2)
				{
					// here it will come under each incidence folder
					// in the model folder, and we want to build the index
					// on each one of these folders
					System.out.println("the input folder to the index: " + internalFolderName + File.separator+ name2);
					try {
						buildIndex(internalFolderName +File.separator+  name2, internalFolderName + File.separator+ name2 + "index");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		    }
		}
	}
	
	
	/**
	 * This method builds and stores the Lucene index to a specified folder
	 * @param folderName - the name of the folder where the files to be
	 * added to the lucene index are stored
	 * @param indexStorageFolder - the path where the index for the current folderName files
	 * is stored
	 * @return
	 * @throws IOException
	 */
	public void buildIndex(String folderName, String indexStorageFolder) throws IOException{
		// 0. Specify the analyzer for tokenizing text.
		//    The same analyzer should be used for indexing and searching
		StandardAnalyzer analyzer = new StandardAnalyzer();

		// 1. create the index
		/*Store the index on file*/
		
		// earlier way of creating the index in the memory
		//Directory index = new RAMDirectory();
		
		Path indexDirectoryPath = Paths.get(indexStorageFolder);
		Directory index = FSDirectory.open(indexDirectoryPath);

		
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter w;

		w = new IndexWriter(index, config);	
		
		// reading the files in the folder "folderName" to add to the index
		String dataFolderName = folderName;
		System.out.println(folderName);
		List<String> filelist = FolderReader(new File(dataFolderName));

		for(int i = 0 ;i<filelist.size();i++){
			String fileName = filelist.get(i);
			// because there are two filenames in the folder named Centroids and Weights
			// that shouldn't be added to the index
			if(!fileName.contains("Cen") && !fileName.contains("Wei"))
			{
				String wordList = readData(fileName);
				addDoc(w, wordList, filelist.get(i));					
			}
		}
		
		w.close();
	}



	private List<String> FolderReader(File folder){
		List <String> FileList =new ArrayList<String>();
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				FolderReader(fileEntry);
			} else {
				FileList.add(fileEntry.getAbsolutePath());
			}
		}
		return FileList;
	}

	private String readData(String dataFileName) throws IOException
	{
		BufferedReader br = null;
		String wordList = ""; 

		String sCurrentLine;
		br = new BufferedReader(new FileReader(dataFileName));

		boolean flag = false;
		while ((sCurrentLine = br.readLine()) != null) 
		{
			if(sCurrentLine.contains("."))
			{
				// assuming that the string is of the format 75.3.7_2
				String[] tokens1 = sCurrentLine.split("_");		
				String[] tokens2 = tokens1[0].split("\\.");
				
				String wordID = tokens2[0];
				for(int i=1; i<tokens2.length; i++)
				{
					String temp = tokens2[i].trim();
					String wordListTemp = wordID + "." + temp + "_" + tokens1[1];	// assuming that there is only one _ in a word 
					if(!flag)
						wordList = wordListTemp;
					else{
						wordList = wordList + " " + wordListTemp;
					}
					flag = true;
				}
			}
			else
			{
				if(!flag)
					wordList = sCurrentLine;
				else{
					wordList = wordList + " " + sCurrentLine;
				}
				flag = true;
			}
		}
		br.close();

		return wordList;
	}

	/**
	 * @param w
	 * @param wordList
	 * @param fileName - should be the document name (i.e. the same as the fileName on the disk)
	 * @throws IOException
	 */
	private void addDoc(IndexWriter w, String wordList, String fileName) throws IOException {
		Document doc = new Document();

		doc.add(new TextField("fileName", fileName, Field.Store.YES));  // TODO: hardcoding!
		doc.add(new TextField("wordList", wordList, Field.Store.YES)); // because the classname is the second field

		w.addDocument(doc);
	}
	
	
	public static void main(String[] args){
		// testing and store index on file only
		System.out.println("Testing Lucene ");
		String folderName = "E:"+File.separator+"Sicong"+File.separator+"DOCS"+
		File.separator+"Energy";
		
		CreateLuceneIndex C = new CreateLuceneIndex();
		C.createLuceneIndexes(folderName);
		
	}
}