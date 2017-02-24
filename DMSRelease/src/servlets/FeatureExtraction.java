package servlets;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mathworks.toolbox.javabuilder.MWCellArray;
import com.mathworks.toolbox.javabuilder.MWCharArray;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.util.JSON;

import FeatureQuery.SymbolsFeatures;
import us.asu.emitlab.clustering.comparator.VariateComparator;
import us.asu.emitlab.clustering.utility.TimeVariate;
import us.asu.emitlab.clustering.utility.Utility;
import us.asu.emitlab.connection.timeseriesdb.ConnectionImplMongoDB;
import connections.MongoConnector;
import connections.RetrieveInfoFromMongo;
import defaults.SessionVariableList;
import us.asu.emitlab.datastructure.JsonList;
import us.asu.emitlab.lucene.CreateLuceneIndex;
import us.asu.emitlab.lucene.QuerySim;

/**
 * Servlet implementation class FeatureExtraction
 */
@WebServlet("/FeatureExtraction")
public class FeatureExtraction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//  private String documents ="";
	private String documentsList=""; //path of the folder containing centroid-word index
	protected boolean flag;
	protected Map<String, Integer> variateMap;
	protected HashMap<String, Integer> matchPrune;

	// For Linux Path:
	String folderName=File.separator+"home"+File.separator+"sliu104"+File.separator+"DOCS";//

	// For Windows Test
	// String folderName="E:"+File.separator+"Sicong"+File.separator+"DOCS";

	// C:\Users\sliu104\Desktop\DOCS
	// String folderName="C:"+File.separator+"Users"+File.separator+"sliu104" + File.separator + "Desktop" + File.separator + "DOCS";

	CreateLuceneIndex luindex = new CreateLuceneIndex();
	Directory index = null;

	public FeatureExtraction() {
		super();
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("We are in Feature Extraction Service");
		String typeofIndex="Score_Lucene";
		String typeData ="";
		String documents="";		

		//Metadata
		String model = "SIR";			//  now testing SIR model
		String type = "Epidemic"; 		//  now it is Epidemic project
		String metric = "Incidence"; 
		String queryVariate = "";		// subset of variates[states] for query
		String querySimID = "";
		String pruneKey="";
		int numofresults = 0;
		String tempNum = "";

		String matchingCondition="";
		String pruningCondition="";

		// parameter
		int DeOctTime=3;//2;
		int DeOctDepd=3;//2;
		int DeLevelTime=3;//6;
		int DeLevelDepd=3;//6;
		double DeSigmaTime=5.6569;//0.9;
		double DeSigmaDepd=0.3;
		int DeSpatialBins=4;
		int DeGaussianThres=3;//6;
		int r=100;	

		HttpServletRequest requestcopy= request;
		HttpSession currSession = request.getSession();
		JSONObject jData;
		try {
			jData = new JSONObject(requestcopy.getParameter("jData"));
			model = jData.getString("model");
			metric = jData.getString("metric");
			querySimID = jData.getString("querySimID");
			type = currSession.getAttribute(SessionVariableList.CURRENT_PROJECT_NAME).toString();
			String kindofinput= jData.getString("searchType");
			System.out.println("kindofinput : " + kindofinput);

			if(kindofinput.equals("jsonfile")){
				queryVariate = jData.getString("zones");
				// testing queryVariate
				System.out.println("Query type: " + type);
				if(type.toLowerCase().equals("energy")){
					queryVariate = "PLENUM-1;SPACE1-1;SPACE2-1;SPACE3-1;SPACE4-1;SPACE5-1";
					System.out.println(queryVariate);
				}else{
					queryVariate = "US_CA;US_AZ;US_NM";
				}
				System.out.println("FeatreuExtraqction query variate from json stream: " + queryVariate);

				
				// type = jData.getString("eid"); //project name (String)request.getSession(true).getAttribute("project");//
				// matchingCondition = (String)request.getParameter("matchingCondition");
				// pruningCondition = (String)request.getParameter("pruningCondition");
				// pruneKey = (String)request.getParameter("pruneKey");


				// System.out.println("matchingCondition: "+matchingCondition);
				// System.out.println("pruningCondition: "+pruningCondition);
				// System.out.println("pruneKey: "+pruneKey);
				typeData="json";
				/**
				 * In here you have to read the  variable of the session in with you saved the querytimeseries
				 * */
				// documents= (String)request.getSession(true).getAttribute("TimeseriesQuery"); // data with query states only
				/* Sicong, change zones only to state of interest*/
				String MongoQueryZone="Zones=,";

				String ts= "Ts=,";
				String te= "Te=,";
				//var ds = "DS=,";
				String ds = "";
				if(type.toLowerCase().equals("energy"))
					ds = "DS=1-D-avg,";
				else
					ds = "DS=,";

				String collection = "CN=" + type;//"Epidemic";
				String MongoQuery = ts + te + "Model=" + model + ","+ "SimID=" + querySimID +"," + "Metric=" + metric+"," + MongoQueryZone + ds + collection; //miss zones


				if(type.toLowerCase().equals("energy"))
					documents = executeEnergyMongo(MongoQuery); // data with all states
				else
					documents = executeEpidemicMongo(MongoQuery); // data with all states



			}else{
				typeData = "csv";
				queryVariate = jData.getString("queryVariate");
				System.out.println("FeatreuExtraqction query variate from csv: " + queryVariate);

				model = jData.getString("model");
				metric = jData.getString("metric");
				type = jData.getString("project"); 
			}
		}
		catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		System.out.println("Query type: " + type + " query model: " + model);
		initHashMapUS(type, model); 
		intiMatchPruneHashSet(); 
		numofresults = 5;


		/**
		 * @author Sicong Liu
		 * Read the input interesting variates/states from input field of similairty search page
		 * */
		String[] queryVariateStates = variateToIndexUS(queryVariate, variateMap, type);
		int qLength = queryVariateStates.length;
		int[] variatesMatlab = new int[qLength];
		for(int qq = 0;qq<qLength;qq++){
			variatesMatlab[qq] = Integer.parseInt(queryVariateStates[qq].trim());
		}

		//PREPARING DOCUMENTS FROM CLIENT
		String inputdata[]=null;
		if(typeData.equals("csv")){
			//inputdata =documents.split("\\n");
			inputdata = (String[])request.getSession(true).getAttribute("CSVFileData");
		}else if(typeData.equals("json")){
			JSONArray variatelist = null;
			try {
				// System.out.println("json object documets : " + documents);
				JSONObject timeserie  = new JSONObject(documents);
				variatelist = timeserie.getJSONArray("Data");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			String[] variates = getVariateList(variatelist);
			// System.out.println("Actual query data: " + variatelist);
			inputdata = JsontoCSV(variatelist, variates);
		}
		String[] LocMStream = null;
		String[] MyLocMStream = null;

		// set up location matrix
		if(type.equals("Epidemic")){
			// read MyLocationMatrix from servlet GraphUpload, as csv format
			MyLocMStream = (String[])request.getSession(true).getAttribute("MyLocationMatrix");
			if(LocMStream == null || LocMStream.length == 0)
				LocMStream = (Utility.getInstance().readFile1("Graph", "LocationMatrix.csv",this.getServletContext()));//readFile1(new File("/Graph/locationMatrixEpidemic.csv"));//
			else
				LocMStream = MyLocMStream;
		}else{
			// energy projet use Clique graph
			System.out.println("Setting up energy cilque graph");
			int variateNum = variateMap.entrySet().size();
			LocMStream = new String[variateNum];
		}
		//Parameter
		double[] para = new double[9];
		if(type.equals("Epidemic")){
			DeOctTime=3;//2;
			para[0] = DeOctTime;
			DeOctDepd=3;//2;
			para[1] = DeOctDepd;
			DeLevelTime=3;//6;
			para[2] = DeLevelTime;
			DeLevelDepd=3;//6;
			para[3] = DeLevelDepd;
			DeSigmaTime=5.6569;//0.9;
			para[4] = DeSigmaTime;
			DeSigmaDepd=0.3;
			para[5] = DeSigmaDepd;
			DeSpatialBins=4;
			para[6] = DeSpatialBins;
			DeGaussianThres=3;//6;
			para[7] = DeGaussianThres;
			r=100;	
			para[8] = r;
			//double[] para = {DeOctTime, DeOctDepd, DeLevelTime, DeLevelDepd, DeSigmaTime, DeSigmaDepd,DeSpatialBins,DeGaussianThres, r};
		}
		else{
			DeOctTime=1;//2;
			para[0] = DeOctTime;
			DeOctDepd=1;//2;
			para[1] = DeOctDepd;
			DeLevelTime=3;//6;
			para[2] = DeLevelTime;
			DeLevelDepd=3;//6;
			para[3] = DeLevelDepd;
			DeSigmaTime=4*5.6569;//0.9;
			para[4] = DeSigmaTime;
			DeSigmaDepd=0.3;
			para[5] = DeSigmaDepd;
			DeSpatialBins=4;
			para[6] = DeSpatialBins;
			DeGaussianThres=3;//6;
			para[7] = DeGaussianThres;
			r=100;	
			para[8] = r;	
			//double[] para = {DeOctTime, DeOctDepd, DeLevelTime, DeLevelDepd, DeSigmaTime, DeSigmaDepd,DeSpatialBins,DeGaussianThres, r};
		}
		//Input preparation
		MWNumericArray MWpara = new MWNumericArray(para);
		MWNumericArray MWInput = convertInputData(inputdata);
		MWNumericArray MWLocM = null;
		if(type.equals("Epidemic"))
			MWLocM = convertLocMEpidemic(LocMStream);
		else
			MWLocM = convertLocMEnergy(LocMStream);

		/*check point by Sicong*/
		System.out.println("type: " + type);
		System.out.println("model: " + model);
		System.out.println("variate size: " + LocMStream.length);


		featureExtraction.FeatureExtraction C= null;
		Object[] featureFrame = null;
		try {
			C = new featureExtraction.FeatureExtraction();
			featureFrame = C.featureExtraction(1, MWInput, MWLocM, MWpara);
		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		MWNumericArray feature =null;
		if((""+featureFrame[0].getClass()).equals("class com.mathworks.toolbox.javabuilder.MWNumericArray")){
			feature=(MWNumericArray)featureFrame[0];
		}
		System.out.println("size of the features array");
		System.out.println(feature.getDimensions()[0]+"    "+ feature.getDimensions()[1]);


		//INITIALIZE THE INDEX
		System.out.println("preparing lucene");
		//String type = Character.toString(type.charAt(0)).toUpperCase()+type.substring(1);
		//System.out.println(type);
		String strTSData=folderName +File.separator+ type+File.separator+model+File.separator+metric; //+"docs";
		//String strWeightFile=folderName + File.separator+type+File.separator+ "weight"+File.separator+model+File.separator+metric+ File.separator+"Weight.txt";//+"SIR_Incidence_CentWeigh"+ File.separator+"ClassWeights.txt";
		String strWeightFile=folderName +File.separator+ type+File.separator+model+File.separator+metric+ File.separator+"Weight.txt";//+"SIR_Incidence_CentWeigh"+ File.separator+"ClassWeights.txt";
		//String centroidPath=folderName + File.separator+type+File.separator+ "weight"+File.separator+model+File.separator+metric+ File.separator+"Centroids.txt";//csv";
		String centroidPath=folderName +File.separator+ type+File.separator+model+File.separator+metric+ File.separator+"Centroids.txt";//csv";
		try {
			Path indexDirectoryPath = Paths.get(strTSData + "index");
			index = FSDirectory.open(indexDirectoryPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Create Query Quantized.
		/*
		 * Input: Numeric Array of the features from the query, path of the file containing the mapping from featureCentroids and words.
		 * 1. read the  centroid-words list
		 * 2. if a feature is near to the i-centroid more than to the i+1 then the feature is associated to the i-word 
		 * */

		System.out.println("centroidPath:" + centroidPath);
		try {
			SymbolsFeatures sy = new SymbolsFeatures();
			//MWCharArray centroidPath1 = new MWCharArray(centroidPath);
			featureFrame = sy.FeatureQuery(3, feature, centroidPath);
		} catch (MWException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String strQueryFile="";
		int len = ((MWNumericArray)featureFrame[0]).getDimensions()[0];
		System.out.println("len: "+ len);
		for(int i =1;i<len+1;i++){
			int [] loc = {i,1};//{1, i};//
			String variates = ((MWCellArray)featureFrame[2]).getCell(loc).toString();
			if(variates.contains(".")){
				String[] variateArray = variates.split("\\.");

				/**
				 * @author Sicong Liu  
				 * Intersect queryVariate with variateArray to get actual variate needed for Lucene
				 * **/
				List<String> variateState = intersectVariate(queryVariateStates, variateArray);

				for(int kk=0;kk<variateState.size();kk++){
					if(i<len)
						strQueryFile =  strQueryFile+ ((MWNumericArray)featureFrame[0]).getInt(loc)+"."+variateState.get(kk).trim()+"_"+((MWNumericArray)featureFrame[1]).getInt(loc)+" ";
					else
						strQueryFile =  strQueryFile+ ((MWNumericArray)featureFrame[0]).getInt(loc)+"."+variateState.get(kk).trim()+"_"+((MWNumericArray)featureFrame[1]).getInt(loc);
				}
			}
			else{
				if(i<len)
					strQueryFile =  strQueryFile+ ((MWNumericArray)featureFrame[0]).getInt(loc)+"."+variates+"_"+((MWNumericArray)featureFrame[1]).getInt(loc)+" ";
				else
					strQueryFile =  strQueryFile+ ((MWNumericArray)featureFrame[0]).getInt(loc)+"."+variates+"_"+((MWNumericArray)featureFrame[1]).getInt(loc);
			}
		}

		System.out.println(strQueryFile);
		//combine words and class

		//Execute query:
		QuerySim q = new QuerySim();
		int i = -1;//0: TFIDF, 1: IDF, 2: LuceneScores
		if(typeofIndex.equals("Score_Lucene")){
			i=2;
		}else if(typeofIndex.equals("IDF")){
			i=1;
		}else{
			i=0;
		}
		if(i==-1){
			System.out.println("ERROR: Parameter not setted.");
		}
		HashMap<String, Double> results=null;
		try {
			results = q.executeQuery(index, i, strQueryFile, strWeightFile);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("ended query");
		Map<String, Double> sortedMapDesc=null;
		if(results!=null){
			sortedMapDesc = sortByComparator(results, false); //false: DESC
		}	
		System.out.println("ended sorted: "+ sortedMapDesc);

		//.... PRUNING STEP ....
		//System.out.println(mapToString(sortedMapDesc));
		String [] result = mapToString(sortedMapDesc).split("\\(end\\)");


		/**
		 * Output results to console for double check
		 * */
		System.out.print("Before RMT-based Pruning: ");
		outputResult(result);
		StringBuilder sb = new StringBuilder();

		/*Use RMT feature based pruning below*/
		if(matchingCondition.length()!=0||pruningCondition.length()!=0){
			System.out.println("Using RMT Pruning...");
			String MatchPruneString = matchingCondition + pruningCondition;
			int conditionSize = 17;

			System.out.println("MatchingPruneString: " + MatchPruneString);
			double[] matchPruneArray = MatchPruneToArray(conditionSize, MatchPruneString);
			Map<String, Double> pruneResults = new HashMap<String, Double>();
			/* Sicong: Based on the returned result, get the features and the raw time series data for pruning input*/
			/* Sicong's pruning step, then you have to sort again. */

			MWNumericArray MWFeature1 = feature; // query feature
			MWNumericArray rawInput1 = MWInput; // query raw data
			//outputMatchingScoreDescrAmpPruning.RMTPrune R = null;
			outputMatchingPruning.RMTPrune P = null;
			try {
				//R = new outputMatchingScoreDescrAmpPruning.RMTPrune();
				P = new outputMatchingPruning.RMTPrune();
			} catch (MWException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			for(int ii=0;ii<result.length;ii++){	
				// result array contains path to database features
				String id=result[ii].split(" Value")[0].split("Key : ")[1];//id.split("//")[id.split("//").length-1]
				String SimID = id.split("/")[id.split("/").length -1].replace(".txt", "");//result[i]

				// Linux feature path
				String baseFolder=File.separator+"home"+File.separator+"sliu104";//
				String featureFolder=baseFolder + File.separator+"Features" + File.separator + type+File.separator+model
						+File.separator+SimID+File.separator+metric+".csv";
				String dataFolder=baseFolder  +  File.separator+type+File.separator+model
						+File.separator+SimID+File.separator+metric+".csv";


				// for windows
				// String baseFolder = "E:" +File.separator+"Sicong"+File.separator+"2016ProjectMatlab";
				//C:\Users\sliu104\Desktop\Features
				/*String baseFolder = "C:" +File.separator+"Users"+File.separator+"sliu104" + File.separator + "Desktop";

				String featureFolder=baseFolder + File.separator+"Features" + File.separator + type+File.separator+model
						+File.separator+SimID+File.separator+metric+".csv";

				String dataFolder=baseFolder + File.separator + type+File.separator+model
						+File.separator+SimID+File.separator+metric+".csv";*/
				//System.out.println("feature folder: " + featureFolder);
				//System.out.println("dataFolder folder: " + dataFolder);

				String inputID = model+"_"+metric+"_"+SimID;
				MWCharArray MWFeature2Path = new MWCharArray(featureFolder); // feature from database, csv format
				MWCharArray rawInput2Path = new MWCharArray(dataFolder); // data from database, csv format

				Object[] simObject = null;


				/**
				 * @author Sicong Liu
				 * Convert input into the type of Matlab and parse them into functions
				 * Feature-Based pruning only use features that intersect with queryVariates from input field
				 * **/
				try {
					int option  = Integer.parseInt(pruneKey);
					matchPruneArray[matchPruneArray.length-1] = option;

					MWNumericArray matchPruneArrayPara = new MWNumericArray(matchPruneArray);

					MWNumericArray variatesMatlab1 = new MWNumericArray(variatesMatlab);
					//MWCharArray optionMatlab = new MWCharArray(option);
					//simObject = R.outputMatchingScoreDescrAmpPruning(1,MWFeature1, MWFeature2Path, rawInput1, rawInput2Path, variatesMatlab1, optionMatlab);
					simObject = P.outputMatchingPruning(1,MWFeature1, MWFeature2Path, rawInput1, rawInput2Path, variatesMatlab1, matchPruneArrayPara);
				} catch (MWException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				MWNumericArray temp = null;
				if((""+simObject[0].getClass()).equals("class com.mathworks.toolbox.javabuilder.MWNumericArray")){
					temp=(MWNumericArray)simObject[0];
				}
				double sim = temp.getDouble();
				pruneResults.put(inputID, sim);
			}
			Map<String, Double> pruneSortedMapDesc=null;
			if(pruneResults!=null){
				pruneSortedMapDesc = sortByComparator(pruneResults, false); //false: DESC
			}	
			String[] pruneResult = mapToString(pruneSortedMapDesc).split("\\(end\\)");

			System.out.print("After RMT-based Pruning: ");
			outputResult(pruneResult);

			for( i =0;i<pruneResult.length;i++){
				String SimIDTemp = pruneResult[i].split("_")[2];
				String SimID = SimIDTemp.split(" ")[0]+".txt";

				String Name = "Model: "+model+", Metric:"+ metric+", SimID: "+ SimIDTemp.split(" ")[0];
				String idcomposed= type+"_"+model+"_"+metric+"_"+SimIDTemp.split(" ")[0];
				sb.append(i + "$" + Name + "$" + idcomposed + ";");
			}
			response.getOutputStream().print(CreateClusterRepresentation(pruneResult, 5, model, metric, type));//print(sb.toString());//
		}else{
			System.out.println("Using Euclidean distance after Lucene index ranking...");
			Map<String, Double> pruneResults = new HashMap<String, Double>();

			MWNumericArray rawInput1 = MWInput; // query raw data
			EuclideanPruning.DistPrune P = null;
			//outputMatchingPruning.RMTPrune p = null;
			try {
				P = new EuclideanPruning.DistPrune();
				//P = new outputMatchingPruning.RMTPrune();
			} catch (MWException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			for(int ii=0;ii<result.length;ii++){	
				// result array contains path to database features
				String id=result[ii].split(" Value")[0].split("Key : ")[1];//id.split("//")[id.split("//").length-1]
				String SimID = id.split("/")[id.split("/").length -1].replace(".txt", "");//result[i]

				// Linux Feature Path
				String baseFolder=File.separator+"home"+File.separator+"sliu104";//
				String featureFolder=baseFolder + File.separator+"Features" + File.separator + type+File.separator+model
						+File.separator+SimID+File.separator+metric+".csv";
				String dataFolder=baseFolder  +  File.separator+type+File.separator+model
						+File.separator+SimID+File.separator+metric+".csv";

				// for windows
				// String baseFolder = "E:" +File.separator+"Sicong"+File.separator+"2016ProjectMatlab";
				// C:\Users\sliu104\Desktop\Features
				/*String baseFolder = "C:" +File.separator+"Users"+File.separator+"sliu104" + File.separator + "Desktop";
				String featureFolder=baseFolder + File.separator+"Features" + File.separator + type+File.separator+model
						+File.separator+SimID+File.separator+metric+".csv";
				String dataFolder=baseFolder + File.separator + type+File.separator+model
						+File.separator+SimID+File.separator+metric+".csv";*/

				String inputID = model+"_"+metric+"_"+SimID;
				MWCharArray rawInput2Path = new MWCharArray(dataFolder); // data from database, csv format
				Object[] simObject = null;

				/**
				 * @author Sicong Liu
				 * Use Euclidean Distance 
				 * **/
				try {
					MWNumericArray variatesMatlab1 = new MWNumericArray(variatesMatlab);
					//System.out.println("Folder: " + rawInput2Path);
					simObject = P.EuclideanPruning(1, rawInput1, rawInput2Path, variatesMatlab1);
				} catch (MWException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MWNumericArray temp = null;
				if((""+simObject[0].getClass()).equals("class com.mathworks.toolbox.javabuilder.MWNumericArray")){
					temp=(MWNumericArray)simObject[0];
				}
				double distanceValue = temp.getDouble();
				pruneResults.put(inputID, distanceValue);
			}
			Map<String, Double> pruneSortedMapDesc=null;
			if(pruneResults!=null){
				pruneSortedMapDesc = sortByComparator(pruneResults, true); //true: ASEND
			}	
			String[] pruneResult = mapToString(pruneSortedMapDesc).split("\\(end\\)");

			System.out.print("After RMT-based Pruning: ");
			outputResult(pruneResult);
			response.getOutputStream().print(CreateClusterRepresentation(pruneResult, 5, model, metric, type));//print(sb.toString());//
		}
	}


	/**
	 * @author Sicong
	 * @param  size - size of the matching + pruning conditions
	 * @param  matchPrune - the string of matching + pruning conditions
	 * @return Integer array of 0 or 1
	 * */
	public double[] MatchPruneToArray(int size, String matchPruneString){
		double[] result = new double[size];
		String[] str = matchPruneString.split(",");
		for(int i=0; i<str.length;i++){
			if(this.matchPrune.keySet().contains(str[i])){
				int value = matchPrune.get(str[i]);
				result[value] = 1.0f;
			}
		}
		return result;
	}

	/**
	 * @author Sicong
	 * @param type name of project, epidemic/energy, or other project name
	 * @param model name of the model of interest
	 * @return indexToHashMap mapping according to different projects
	 * */
	public void initHashMapUS(String type, String model){
		variateMap = new HashMap<String, Integer>();
		MongoConnector ci =null;
		ci = new ConnectionImplMongoDB();
		/**
		 * @author Sicong and Reece
		 *	In each if/else-if statement, the code was modified to 
		 *	accommodate dynamic data received from mongoDB
		 * */
		if(type.toLowerCase().equals("epidemic")){
			List zoneList = RetrieveInfoFromMongo.buildZoneList(type, type, ci);

			for(int i = 0; i < zoneList.size(); i++)
			{
				variateMap.put(zoneList.get(i).toString(), (i+1));
			}

		}else if(model.equals("5ZoneAirCooled")){
			System.out.println("We are here at energy model");
			List zoneList = RetrieveInfoFromMongo.buildSplitZoneList(type, type, model, ci);
			for(int i = 0; i < zoneList.size(); i++)
			{
				if(!zoneList.get(i).toString().equals( "CENTRAL-CHILLER"))
				{
					// variateMap.put(zoneList.get(i).toString(), (i+1));
					System.out.println(zoneList.get(i).toString() + " " + i);
					variateMap.put(zoneList.get(i).toString(), (i));
				}
			}
		}else if(model.equals("SmallOffice")){

			List zoneList = RetrieveInfoFromMongo.buildSplitZoneList(type, type, model, ci);
			for(int i = 0; i < zoneList.size(); i++)
			{
				variateMap.put(zoneList.get(i).toString(), (i+1));
			}
		}else if(model.equals("MultiStory")){

			List zoneList = RetrieveInfoFromMongo.buildSplitZoneList(type, type, model, ci);
			for(int i = 0; i < zoneList.size(); i++)
			{
				variateMap.put(zoneList.get(i).toString(), (i+1));
			}
		}

	}

	/**
	 * @author Sicong
	 * @return Match/Prune Condition To Index
	 * */
	public void intiMatchPruneHashSet(){
		matchPrune = new HashMap<String, Integer>();
		matchPrune.put("MDA", 0);
		matchPrune.put("MAA", 1);
		matchPrune.put("MTO", 2);
		matchPrune.put("MTP", 3);
		matchPrune.put("MTA", 4);
		matchPrune.put("MTS", 5);
		matchPrune.put("MVS", 6);
		matchPrune.put("MVA", 7);


		matchPrune.put("PDA", 8);
		matchPrune.put("PAA", 9);
		matchPrune.put("PTO", 10);
		matchPrune.put("PTP", 11);
		matchPrune.put("PTA", 12);
		matchPrune.put("PTS", 13);
		matchPrune.put("PVS", 14);
		matchPrune.put("PVA", 15);
	}

	public String CreateClusterRepresentation(String [] results, int grupsize,String model,String metric, String type){

		StringBuilder sb = new StringBuilder();
		int count=0;
		int toplevel=0;
		String collectionName = Character.toString(type.charAt(0)).toUpperCase()+type.substring(1);
		//open the root cluster
		sb.append("{\"name\":\""+collectionName+"\",\"cluster\":[");//project level
		sb.append("{\"name\":\""+model+"\",\"cluster\":[");//model level
		sb.append("{\"name\":\""+metric+"\",\"cluster\":[");//metric level
		//Error is here//System.out.println(results.length);
		for(int i =0;i<=results.length;i++){
			if(count ==0 ){
				toplevel++;
				String SimIDTemp = results[i].split(" Value")[0].split("Key : ")[1];
				String SimID = SimIDTemp.split(" ")[0]+".txt";
				//System.out.println("SimIDif: " + SimID);
				//The index here refer to where to save this timeseries data in the array created in teh javascript 
				sb.append("{"
						+ "\"Index\":\""+i+"\",\"metric\":\""+metric+"\",\"name\":\"centroid_"+SimIDTemp.split("\\.")[0]+"\","
						+ "\"member\":[{\"Index\":\""+i+"\",\"metric\":\""+metric+"\",\"name\":\""+SimIDTemp.split("\\.")[0]+"\"}");
				//open the  member cluster and add the firs element name the cluster "toplevel" 
				count++;
			} else if(count< grupsize && i<results.length){
				//add element in the cluster i
				String SimIDTemp = results[i].split(" Value")[0].split("Key : ")[1];
				String SimID = SimIDTemp.split(" ")[0]+".txt";
				//System.out.println("SimIDelseif: " + SimID);
				sb.append(",{\"Index\":\""+i+"\",\"metric\":\""+metric+"\",\"name\":\""+SimIDTemp.split("\\.")[0]+"\"}");
				count++;
			}else{

				// end the member cluster
				sb.append("]}");
				if(i<results.length){
					sb.append(",");
					//i do a for cycle  without save the element.
					i=i-1;
				}

				count=0;//reset the count
				//open the new cluster
			}
		}
		sb.append("]}]}]}");
		//close the root cluster
		return sb.toString();
	}

	public String mapToString(Map<String, Double> map)
	{
		StringBuffer sb = new StringBuffer();
		for (Entry<String, Double> entry : map.entrySet())
		{
			sb.append("Key : " + entry.getKey() + " Value : "+ entry.getValue()+"(end)");
		}
		return sb.toString();
	}

	private  Map<String, Double> sortByComparator(Map<String, Double> unsortMap, final boolean order)
	{

		List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(unsortMap.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<String, Double>>()
		{
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2)
			{
				if (order)
				{
					return o1.getValue().compareTo(o2.getValue());
				}
				else
				{
					return o2.getValue().compareTo(o1.getValue());

				}
			}
		});

		// Maintaining insertion order with the help of LinkedList
		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Entry<String, Double> entry : list)
		{
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	private String[] getVariateList(JSONArray simulation) {
		// TODO Auto-generated method stub
		String [] variates = new String[simulation.length()];
		// System.out.println(simulation);
		JsonList listVariates =  orderedVariate(simulation);
		for(int i =0;i<listVariates.size();i++){
			try {
				variates[i]=listVariates.get(i).getString("zone");
				//		System.out.println(variates[i]);
			} catch (JSONException e) {
			}
		}
		return variates;
	}

	private JsonList orderedVariate(JSONArray timeserie) {
		// TODO Auto-generated method stub
		JsonList list =  new JsonList(timeserie);
		Collections.sort(list, new VariateComparator());


		return list;
	}

	private String[] JsontoCSV(JSONArray timeserie, String []zones) {
		// TODO Auto-generated method stub

		String header ="iteration,time";
		for(int i=0;i<zones.length;i++){
			header = header+","+zones[i];
		}
		String timeNames []=null;
		JsonList listVariates =  orderedVariate(timeserie);
		try {
			timeNames  = JSONObject.getNames(listVariates.get(0).getJSONObject("dps"));
		} catch (JSONException e) {
		}

		List<TimeVariate> listMap= new ArrayList<TimeVariate>();
		for(int i=0;i<timeNames.length;i++){
			String concvariate="";
			for(int j=0;j<zones.length;j++){
				try {
					//+((JSONObject)timeserie.get(j)).getJSONObject("dps").getString(timeNames[i]);
					//		System.out.println(i+": "+ timeNames[i]+": "+zones[j]+": "+((JSONObject)timeserie.get(j)).getJSONObject("dps").getString(timeNames[i]));
					concvariate = concvariate+","+ listVariates.get(j).getJSONObject("dps").getString(timeNames[i]);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			listMap.add(new TimeVariate(Long.parseLong(timeNames[i]),concvariate));
		}
		//- Collections.sort(list, new Comparator());
		Collections.sort(listMap,new TimeVariate());
		//listMap.sort(new TimeVariate());

		String [] returned = new String[listMap.size()+1];
		//String [] returned = new String[listMap.size()];

		returned[0]=header;
		for(int i=0;i<listMap.size();i++){
			returned[i+1] = (i+1)+","+listMap.get(i).toString();
			// returned[i] = (i+1)+","+listMap.get(i).toString();
			// System.out.println((i+1)+","+listMap.get(i).toString());
		}
		return returned;
	}

	private void outputResult(String[] results){
		for(int i=0;i<results.length;i++){
			System.out.print(results[i] + " ");
		}
		System.out.println();
	}

	/**
	 * @author Sicong Liu
	 *  to map the variate/state list to Integer index
	 * 	returned as String
	 * @param variateList string separated by ";"
	 * @param map mapping from state name to integer id
	 * @return string array integer as string, for Lucene sub-variate-set computation
	 * */
	private String[] variateToIndexUS(String variateList, Map<String, Integer> map, String type){
		String[] temp = variateList.split(";");
		int length = temp.length;
		String[] variateIndex = new String[length];
		if(type.toLowerCase().equals("energy")){
			for(int i=0;i<length;i++){
				String variateName = temp[i].trim();
				System.out.println("variateName: "+ variateName + "  variate index match: " + variateIndex[i]);
				variateIndex[i] = map.get(variateName).toString();
				
			}
		}
		else{
			for(int i=0;i<length;i++){
				String variateName = temp[i].trim();
				System.out.println(variateName);
				variateName = variateName.split("_")[1];
				variateIndex[i] = map.get(variateName).toString();
			}
		}

		return variateIndex;
	}

	/**
	 * @author Sicong Liu
	 * @param queryVariate variates/states from query input
	 * @param featureVariate variates/states from feature 
	 * @return List<String> list of common variates/states between queryVariate and featureVariate 
	 */

	private List<String> intersectVariate(String[] queryVariate, String[] featureVariate){
		for(int i = 0;i<featureVariate.length;i++){
			featureVariate[i] = featureVariate[i].trim();
		}
		List<String> qList =  new ArrayList<String>(Arrays.asList(queryVariate));
		List<String> fList =  new ArrayList<String>(Arrays.asList(featureVariate));
		qList.retainAll(fList);
		return qList;
	}

	public double[][] convertMWNumericAtrray(MWNumericArray feature){
		int size[]=feature.getDimensions();
		double[][] output=new double[size[0]][size[1]];
		for(int i=0;i<size[0];i++){
			for(int j=0;j<size[1];j++){
				int [] loc = {i+1, j+1};

				output[i][j]= feature.getDouble(loc);
				//System.out.println("i: " +i+" j: " + j + " output " + output[i][j]);
			}
		}
		return output;
	}



	public  MWNumericArray convertLocMEpidemic(String[] inputLocM)
	{
		String csvSplit=",";
		int row = inputLocM.length;
		double[][] output = new double[row][];

		for(int i=0;i<inputLocM.length;i++)
		{
			String[] splitString = inputLocM[i].split(csvSplit);// skip first line
			int column = splitString.length;

			output[i] = new double[column];
			for(int j=0;j<column;j++)
			{
				output[i][j] = Double.parseDouble(splitString[j]);// skip first two columns
			}
		}
		MWNumericArray MWoutput = new MWNumericArray(output);
		return MWoutput;
	}
	/**
	 * @author Sicong
	 * @param inputLocM clique graph location matrix for energy dataset
	 * @return matlab readable location matrix
	 */
	public  MWNumericArray convertLocMEnergy(String[] inputLocM)
	{
		int row = inputLocM.length;
		double[][] output = new double[row][];

		for(int i=0;i<inputLocM.length;i++)
		{
			int column = row;

			output[i] = new double[column];
			for(int j=0;j<column;j++)
			{
				output[i][j] = 1;
			}
		}
		MWNumericArray MWoutput = new MWNumericArray(output);
		return MWoutput;
	}

	public MWNumericArray convertInputData(String[] inputStream)
	{
		String csvSplit=",";
		int row = inputStream.length;
		double[][] output = new double[row-1][];


		for(int i=0;i<inputStream.length-1;i++)
		{
			String[] splitString = inputStream[i+1].split(csvSplit);// skip first line
			int column = splitString.length-2;

			output[i] = new double[column];
			for(int j=0;j<column;j++)
			{
				output[i][j] = Double.parseDouble(splitString[j+2]);// skip first two columns
			}
		}
		MWNumericArray MWoutput = new MWNumericArray(output);
		return MWoutput;
	}
	private static String executeEpidemicMongo(String query) {
		MongoConnector ci =null;
		ci = new ConnectionImplMongoDB();
		ci.getInstance("localhost",3309); //no if-else needed they use the same port

		Cursor resultsetMongo =  ci.aggregate(query, ci.getDbFromName("Epidemic"));
		BasicDBObject obj =null;
		BasicDBObject dataobj=null;
		if (resultsetMongo.hasNext()) {
			obj=(BasicDBObject) resultsetMongo.next();

			BasicDBList data = ((BasicDBList) obj.get("zones"));
			String m="[";
			for (int k = 0; k < data.size(); k++) {
				m=m+"{zone:\""+((BasicDBObject)data.get(k)).getString("zone")+"\", dps:";
				String dpsloc = ((BasicDBObject)data.get(k)).get("dps").toString().replace("{", "").replace("}", "").replace("[", "{").replace("]", "}").replace(":", "\":\"");
				if(k<data.size()-1){
					m=m+dpsloc+"},";
				}else{
					m=m+dpsloc+"}";
				}
			}
			dataobj= new BasicDBObject("Data",JSON.parse(m+"]"));
			BasicDBObject metadata = (BasicDBObject) obj.get("_id");
			dataobj.put("Meta", metadata);
		}

		//close DB Connection
		ci.closeDb();
		return dataobj.toString();//obj.toString(); The result from the query is null
	}
	private static String executeEnergyMongo(String query) {
		MongoConnector ci =null;
		ci = new ConnectionImplMongoDB();
		ci.getInstance("localhost",3309); //no if-else needed they use the same port

		Cursor resultsetMongo =  ci.aggregate(query, ci.getDbFromName("Energy"));
		BasicDBObject obj =null;
		BasicDBObject dataobj=null;
		if (resultsetMongo.hasNext()) {
			obj=(BasicDBObject) resultsetMongo.next();

			BasicDBList data = ((BasicDBList) obj.get("zones"));
			String m="[";
			for (int k = 0; k < data.size(); k++) {
				m=m+"{zone:\""+((BasicDBObject)data.get(k)).getString("zone")+"\", dps:";
				String dpsloc = ((BasicDBObject)data.get(k)).get("dps").toString().replace("{", "").replace("}", "").replace("[", "{").replace("]", "}").replace(":", "\":\"");
				if(k<data.size()-1){
					m=m+dpsloc+"},";
				}else{
					m=m+dpsloc+"}";
				}
			}
			dataobj= new BasicDBObject("Data",JSON.parse(m+"]"));
			BasicDBObject metadata = (BasicDBObject) obj.get("_id");
			dataobj.put("Meta", metadata);
		}

		//close DB Connection
		ci.closeDb();
		return dataobj.toString();//obj.toString(); The result from the query is null
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
