package us.asu.emitlab.clustering.datacluster;




import Jama.Matrix;
import Kmeanslib.Class1;
//import SpectralKMeansLoop.Class1;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;
import servlets.Clustering;
import us.asu.emitlab.clustering.Cluster;
import us.asu.emitlab.metric.EuclidianDistance;
import us.asu.emitlab.metric.MetricInterface;

/*import opentsdb.cluster.metric.EuclidianDistance;
import opentsdb.cluster.metric.MetricInterface;
import us.asu.silv.clustering.Cluster;
*/
import org.json.JSONException;
import org.json.JSONObject;

import com.mathworks.toolbox.javabuilder.MWArray;
import com.mathworks.toolbox.javabuilder.MWCellArray;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DataSimilarityOpentsdbEnergyMatlab implements DataSimilarityInterface{
	DataSimilarityOpentsdbEnergyMatlab parent= null;
	List <ArrayList<JSONObject>> l = new ArrayList<ArrayList<JSONObject>>();
	List<String> timeSerieslabel=null;
	List<ArrayList<Double>> matrixDistance= new ArrayList<ArrayList<Double>>();
	
	
//////shengyu modified
	double[][] datamatrix;
	
	List<JSONObject> data = null;
	ArrayList<DataSimilarityOpentsdbEnergyMatlab> childs = new ArrayList<DataSimilarityOpentsdbEnergyMatlab>();
	int level=0;
	double max[];
	String name[];
	private String index="";
	String metrica ;
	//Cluster cluster= null;
	String features="";
	boolean isleaf=false;
	Cluster Root = new Cluster("Root");

	int k=4;
	public DataSimilarityOpentsdbEnergyMatlab(DataSimilarityOpentsdbEnergyMatlab parent){
		this.parent= parent;
	}
	
	public DataSimilarityOpentsdbEnergyMatlab(DataSimilarityOpentsdbEnergyMatlab parent, int k,List<JSONObject> Resultset,MetricInterface metric, boolean isleaf){

		this.data=Resultset;
		this.parent=parent;
		this.isleaf=isleaf;
		
	}
	public DataSimilarityOpentsdbEnergyMatlab(DataSimilarityOpentsdbEnergyMatlab parent, int k,List<JSONObject> Resultset,MetricInterface metric, String features, String currentMetric){
		
/*		System.out.println("Node Similarity start");
		for (int i = 0; i < Resultset.size(); i++) {
			System.out.println(Resultset.get(i).toString());
			
		}
		
		System.out.println("************************************");
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		*/
//		System.out.println("datanodesim: "+ Resultset.size());
			try {
				
				index=""+ Resultset.get(0).getJSONObject("Meta").getInt("Index");
		
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		try {
			this.metrica=Resultset.get(0).getJSONArray("Data").getJSONObject(0).getString("metric");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long t1= Calendar.getInstance().getTimeInMillis();
	//	System.out.println("size: "+ Calendar.getInstance().getTimeInMillis());
		//this.k=k;
		//name = new String[k];
		//int index=0;
		//this.data=Resultset;
		this.parent=parent;
		//this.data= new ArrayList<JSONObject>();
		timeSerieslabel = getTimeLabel(Resultset.get(0));
		String[] names= inputforMatlab(Resultset);
		 Object[] DistMatRes = null; 
		 double[][] currentDistances = new double[this.matrixDistance.size()][this.matrixDistance.size()];
		long diststarttime=System.currentTimeMillis();
		try {
			Class1 caldist=new Class1();
			DistMatRes=caldist.CreateDistMatrix(1, this.datamatrix);
			Object a=((MWArray)DistMatRes[0]).toArray();
			if(a instanceof double[][])
			{
				currentDistances=(double[][])a;
			}
			MWArray.disposeArray(DistMatRes);
			MWArray.disposeArray(a);
			int loc[] = {1,1};
			MWNumericArray M =((MWNumericArray)caldist.getMax(1, this.datamatrix)[0]);
			double max= M.getDouble(loc);
			if(Clustering.MaxValue<max){
				System.out.println("Max: "+max);
				Clustering.MaxValue=max;
			}
			
			MWNumericArray m =((MWNumericArray)caldist.getMin(1, this.datamatrix)[0]);
			double min = m.getDouble(loc);
			if(Clustering.MinValue>min){
				System.out.println("Min: "+min);
				Clustering.MinValue= min;
			}
		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long distendtime=System.currentTimeMillis();
		System.out.println("create distance matrix time is: "+(distendtime-diststarttime));
		/*
		String[] names = createMatrixDistance(Resultset);
		 
		 double[][] currentDistances = new double[this.matrixDistance.size()][this.matrixDistance.size()];
		 
		 for(int i=0; i < this.matrixDistance.size();i++){
			 //System.out.println();
			 for(int j=0;j<this.matrixDistance.get(i).size();j++){
				 //System.out.print(""+this.matrixDistance.get(i).get(j)+"; ");
				 currentDistances[i][j] = matrixDistance.get(i).get(j);
			 }
		 }
		 */
			//List<Integer> sid = new ArrayList<Integer>();
		 List<String> sid = new ArrayList<String>();
			Root.setData(currentDistances);
			for (int i = 0; i != names.length; i++){
				sid.add(names[i]);//(Integer.parseInt(names[i]));
		//	System.out.println("sid: "+ sid.get(i));
			}
			Root.setSimID(sid);
			
			diststarttime=System.currentTimeMillis();
			
			double[][] Data = Root.getData();
			Object[] kmeansResult = null; 
			double[][] representative=null;
			double[][] parentindex=null;
			ArrayList<double[][]> result=new ArrayList<double[][]>();
			//double[][] level=null;
			try {
				Class1 spectralkmeans=new Class1();
				kmeansResult=spectralkmeans.SpectralKMeansLoop(3, Data);
						
				Object res1=((MWArray)kmeansResult[0]).toArray();
				if(res1 instanceof double[][])
				{
					representative=(double[][])res1;
				}
				
				
				for(int i=1;i<((MWCellArray)kmeansResult[1]).numberOfElements()+1;i++)
				{
					Object res2=((MWCellArray)kmeansResult[1]).get(i);
					if(res2 instanceof double[][])
					{
						result.add((double[][])res2);
					}
					MWArray.disposeArray(res2);
				}
				
				
				Object res3=((MWArray)kmeansResult[2]).toArray();
				if(res3 instanceof double[][])
				{
					parentindex=(double[][])res3;
				}
				//Object res4=((MWArray)kmeansResult[3]).toArray();
				//if(res4 instanceof double[][])
				//{
				//	level=(double[][])res4;
				//}
				MWArray.disposeArray(kmeansResult);
				MWArray.disposeArray(res1);
				
				MWArray.disposeArray(res3);
				//MWArray.disposeArray(res4);
			} catch (MWException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int level[]=new int[parentindex[0].length];
			level[0]=0;
			int previousLevel=0;
			int LevelIndex=0;
			int previousParentindex=0;
			Cluster currentNode=Root;
			Root.setClusterRepresentative( Root.getSimID().get((int)representative[0][0]));
			//Queue<XilunClusterEpidemic> queue = new LinkedList<XilunClusterEpidemic>();
			//queue.add(null);
			//queue.p
			ArrayList<Cluster> nodelist = new ArrayList<Cluster>();
			nodelist.add(Root);
			for (int i=1;i<parentindex[0].length;i++)
			{
				level[i]=level[(int)parentindex[0][i]-1]+1;
				if((int)parentindex[0][i]==previousParentindex)
				{
					LevelIndex++;
				}else
				{
					LevelIndex=0;
				}
				currentNode=nodelist.get((int)parentindex[0][i]-1);
				
				//if(result.get(i)[0].length<8)
				//{
				//	for (int j = 0; i != currentNode.getSimID().size(); i++) {
				//		XilunClusterEpidemic child = new XilunClusterEpidemic(currentNode.getSimID().get(i));//(Integer.toString(currentNode.getSimID().get(i)));
				//		currentNode.addChild(child);
				//	}
				//}
				//else
				//{
					List<String> SimIDs = currentNode.getSimID();
					double[][] currentData = currentNode.getData();
					Matrix A = new Matrix(currentData);
					
					int[] selectIDX = new int[result.get(i).length];
					Cluster child =new Cluster("Lv#"+(level[i])+"_"+LevelIndex);
					List<Integer> childIDs = new ArrayList<Integer>();
					List<String> childSimIDs = new ArrayList<String>();
					for(int j=0;j<result.get(i).length;j++)
					{
						int subindex=(int)result.get(i)[j][0]-1;
						childIDs.add(subindex);
						//childSimIDs.add(SimIDs.get(i));
						childSimIDs.add(SimIDs.get(subindex));
						selectIDX[j]=subindex;
					}
					child.setSimID(childSimIDs);
					child.setData(A.getMatrix(selectIDX, selectIDX).getArray());	
					child.setClusterRepresentative(SimIDs.get((int) representative[0][i]));					
					if(result.get(i).length<8)
					{
						for (int j = 0; j != child.getSimID().size(); j++) {
							Cluster grandchild = new Cluster(child.getSimID().get(j));//(Integer.toString(currentNode.getSimID().get(i)));
							child.addChild(grandchild);
						}
					}
					currentNode.addChild(child);
					//System.out.println("i number:"+i);
					//nodelist.add(currentNode.getChildren().get(LevelIndex));
					nodelist.add(child);
				//}
				previousLevel=level[i];
				previousParentindex=(int)parentindex[0][i];
				
			}
			
			
			//SpectralKMeans(Root, 0);
			distendtime=System.currentTimeMillis();
			System.out.println("kmeans time is : "+(distendtime-diststarttime));
			System.out.println("finished");
		long endtime=System.currentTimeMillis();
			
			//SpectralKMeans(Root, 0);
		
		// System.out.println("toconsole: ["+Root.toConsole1()+"]");
		 
	}
	
	
/*	public static void SpectralKMeans (Cluster currentNode, int currentLevel) {
		
		System.out.println("nel kmeans");
		Dataset dataset = new DefaultDataset();
		double[][] Data = currentNode.getData();
		List<String> SimIDs = currentNode.getSimID();
	//	List<Integer> SimIDs = currentNode.getSimID();
		// If data is too few for another level, then create leafs and stop working on this brunch
		
		if (Data.length <= 5) {
			
			for (int i = 0; i != currentNode.getSimID().size(); i++) {
				Cluster child = new Cluster(currentNode.getSimID().get(i));//(Integer.toString(currentNode.getSimID().get(i)));
				currentNode.addChild(child);
			}
			if (currentNode.getName().equals("Root")) {
							
				currentNode.setClusterRepresentative(SimIDs.get(0));
			}
			return;
		}
		
		// Turn double 2d array into Matrix and do eigen decomposition
		Matrix A = new Matrix(Data);
		Matrix V = A.eig().getV();
		Matrix D = A.eig().getD();
		
		// Find number of clusters K
		int NumOfClusters = 1;
		int MaxNumOfClusters = 3;
		List<Integer> columnIDX = new ArrayList<Integer>();
		columnIDX.add(NumOfClusters);
		for (int i = 1; i != D.getRowDimension(); i++) {
			if (D.get(i-1, i-1)/D.get(i, i) < 1.2 && NumOfClusters < MaxNumOfClusters) {
				NumOfClusters++;
				columnIDX.add(NumOfClusters);
			}
			
			else
				break;
		}
		
		int[] cidx = new int[columnIDX.size()];		
		for (int i = 0; i != columnIDX.size(); i++) {
			cidx[i] = columnIDX.get(i)-1;
		}
		
		// Get top eigen vectors for kmeans
		double[][] topEigen = V.getMatrix(0, V.getRowDimension()-1, cidx).getArray();
		
		for (int i = 0; i != topEigen.length; i++) {
			Instance instance = new DenseInstance(topEigen[i], i);
			dataset.add(instance);
		}
		
		// If it is the root node, find its representative before clustering
		if (currentNode.getName().equals("Root")) {
			Instance Centroid = dataset.get(0);
			DistanceMeasure dm = new EuclideanDistance();
			double minDistance = dm.measure(Centroid, dataset.get(0));
			int representative = 0;
			for (int j = 1; j != dataset.size(); j++) {
				double dist = dm.measure(Centroid, dataset.get(j));
				if (dm.compare(dist, minDistance)) {
                    minDistance = dist;
                    representative = j;
                }
			}			
			currentNode.setClusterRepresentative(SimIDs.get(representative));
		}
		
		Clusterer km = new KMeans(NumOfClusters+1);
		Dataset[] clusters = km.cluster(dataset);
		
		//Set children of this cluster here
		for (int i = 0; i != clusters.length; i++) {
			
			//Set up a child
			Cluster child = new Cluster("Lv#"+(currentLevel+1)+"_"+i);
			List<Integer> childIDs = new ArrayList<Integer>();
			List<String> childSimIDs = new ArrayList<String>();
			//List<Integer> childSimIDs = new ArrayList<Integer>();
			
			//System.out.println("Cluster " + i + ": ");
			Dataset clusterChild = clusters[i];

			Instance Centroid = clusterChild.get(0);
			Centroid = Centroid.minus(clusterChild.get(0));
			// Store ids 
			for (int j = 0; j != clusterChild.size(); j++) {
				childIDs.add((Integer) clusterChild.get(j).classValue());
				//childSimIDs.add(SimIDs.get(i));
				childSimIDs.add(SimIDs.get((Integer) clusterChild.get(j).classValue()));
				//System.out.print(SimIDs.get((Integer) clusterChild.get(j).classValue()) + "\t");
				Centroid = Centroid.add(clusterChild.get(j));

			}
			child.setSimID(childSimIDs);

			Centroid = Centroid.divide(clusterChild.size());

			DistanceMeasure dm = new EuclideanDistance();
			double minDistance = dm.measure(Centroid, clusterChild.get(0));
			int representative = 0;
			for (int j = 1; j != clusterChild.size(); j++) {
				double dist = dm.measure(Centroid, clusterChild.get(j));
				if (dm.compare(dist, minDistance)) {
					minDistance = dist;
					representative = j;
				}
			}

			//System.out.println();
			
			int[] selectIDX = new int[childIDs.size()];
			for (int j = 0; j != childIDs.size(); j++)
				selectIDX[j] = childIDs.get(j);
			
			//store sub data 
			child.setData(A.getMatrix(selectIDX, selectIDX).getArray());	
			currentNode.addChild(child);
			
			child.setClusterRepresentative(SimIDs.get((Integer) clusterChild.get(representative).classValue()));
	//		System.out.println("Representative: "+child.getClusterRepresentative());
			SpectralKMeans(child, currentLevel+1);
			
		}
		
	}
	*/
	
	
	private int getCorrectList(int[] indexCentroid,int idx, List<JSONObject> a) {
		// TODO Auto-generated method stub
	//	System.out.println("adding : "+a.get(idx).toString());
		int ri=0;
		int idmin=0;
		double min = this.matrixDistance.get(idx).get(0);
		for(int i=0;i<indexCentroid.length;i++){
			if(this.matrixDistance.get(idx).get(indexCentroid[i])<min){
		//		System.out.println("value new old : " + this.matrixDistance.get(idx).get(indexCentroid[i])+" , "+ min);//+" l'elemento: "+ a.get(idx).toString());
				min= this.matrixDistance.get(idx).get(indexCentroid[i]);
				idmin=i;
			/*	try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
		}
//		System.out.println("added to: "+ this.name[idmin] );
		return idmin;
	}

	//Creo la matrice delle distanze
	
	private String[] createMatrixDistance(List<JSONObject> resultset) {
		// TODO Auto-generated method stub
		
		ArrayList<String> nameList = new ArrayList<String>();
		for(int i =0 ;i<resultset.size();i++){
			this.matrixDistance.add(new ArrayList<Double>());
		}
		
		for(int i=0;i<resultset.size();i++){
			
			//xilun find names here
			String simID;
			try {
				simID = resultset.get(i).getJSONObject("Meta").getString("simid");//Integer.toString(resultset.get(i).getJSONObject("Metadata").getInt("simid"));
				simID = simID + "^"+resultset.get(i).getJSONObject("Meta").getInt("Index");
				simID = simID +"^" +this.metrica; 
				if (!nameList.contains(simID))
					nameList.add(simID);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		//	this.matrixDistance.add(new ArrayList<Double>());
			for(int j=i;j<resultset.size();j++){
				if(i==j){
					this.matrixDistance.get(i).add(0.0);
				}
				else{
			//		System.out.println("i: "+i+" j: "+j+ " : "+(new EuclidianDistance()).pointDist1(this.timeSerieslabel, resultset.get(i),resultset.get(j)));
					this.matrixDistance.get(i).add( (new EuclidianDistance()).pointDist1(this.timeSerieslabel, resultset.get(i),resultset.get(j)));
					this.matrixDistance.get(j).add(this.matrixDistance.get(i).get(j));
				}
			}
		}
		
		Object[] objectList = nameList.toArray();
		String[] names = Arrays.copyOf(objectList,objectList.length,String[].class);
		
		return names;
		
	}
	
public static void SpectralKMeans (Cluster currentNode, int currentLevel) {
		
		Dataset dataset = new DefaultDataset();
		double[][] Data = currentNode.getData();
		List<String> SimIDs = currentNode.getSimID();
	//	List<Integer> SimIDs = currentNode.getSimID();
				
		// Turn double 2d array into Matrix and do eigen decomposition
		Matrix A = new Matrix(Data);
		Matrix V = A.eig().getV();
		Matrix D = A.eig().getD();
		
		// Find number of clusters K
		int NumOfClusters = 1;
		int MaxNumOfClusters = 3;
		List<Integer> columnIDX = new ArrayList<Integer>();
		//columnIDX.add(NumOfClusters);
		
		int eigenGap = 0;
		double maxGap = 0;
		for (int i = 1; i < D.getRowDimension()-1; i++) {
			if (maxGap < D.get(i+1, i+1)/D.get(i, i)) {
				maxGap = D.get(i+1, i+1)/D.get(i, i);
				eigenGap = i;
			}	
		}
		
		if (eigenGap+1 > MaxNumOfClusters)
			NumOfClusters = MaxNumOfClusters;
		else
			NumOfClusters = eigenGap+1;
		
		/*
		for (int i = 1; i != D.getRowDimension(); i++) {
			if (D.get(i-1, i-1)/D.get(i, i) < 1.2 && NumOfClusters < MaxNumOfClusters) {
				NumOfClusters++;
				columnIDX.add(NumOfClusters);
			}
			
			else
				break;
		}
		*/
		int[] cidx = new int[NumOfClusters];		
		for (int i = 0; i != NumOfClusters; i++) {
			cidx[i] = i;
		}
		
		// Get top eigen vectors for kmeans
		double[][] topEigen = V.getMatrix(0, V.getRowDimension()-1, cidx).getArray();
		
		for (int i = 0; i != topEigen.length; i++) {
			Instance instance = new DenseInstance(topEigen[i], i);
			dataset.add(instance);
		}
		
		// If it is the root node, find its representative before clustering
				if (currentNode.getName().equals("Root")) {
					Instance Centroid = dataset.get(0);
					DistanceMeasure dm = new EuclideanDistance();
					double minDistance = dm.measure(Centroid, dataset.get(0));
					int representative = 0;
					for (int j = 1; j != dataset.size(); j++) {
						double dist = dm.measure(Centroid, dataset.get(j));
						if (dm.compare(dist, minDistance)) {
		                    minDistance = dist;
		                    representative = j;
		                }
					}			
					currentNode.setClusterRepresentative(SimIDs.get(representative));
				}
				
				// If data is too few for another level, then create leafs and stop working on this brunch
				if (Data.length <= 5) {
					
					for (int i = 0; i != currentNode.getSimID().size(); i++) {
						Cluster child = new Cluster(currentNode.getSimID().get(i));//(Integer.toString(currentNode.getSimID().get(i)));
						currentNode.addChild(child);
					}
					
					return;
				}			
		
								
		Clusterer km = new KMeans(NumOfClusters+1);
		Dataset[] clusters = km.cluster(dataset);
		
		//Set children of this cluster here
		for (int i = 0; i != clusters.length; i++) {
			
			//Set up a child
			Cluster child = new Cluster("Lv#"+(currentLevel+1)+"_"+i);
			List<Integer> childIDs = new ArrayList<Integer>();
			List<String> childSimIDs = new ArrayList<String>();
			//List<Integer> childSimIDs = new ArrayList<Integer>();
			
			//System.out.println("Cluster " + i + ": ");
			Dataset clusterChild = clusters[i];
			
			Instance Centroid = clusterChild.get(0);
			Centroid = Centroid.minus(clusterChild.get(0));
			// Store ids 
			for (int j = 0; j != clusterChild.size(); j++) {
				childIDs.add((Integer) clusterChild.get(j).classValue());
				//childSimIDs.add(SimIDs.get(i));
				childSimIDs.add(SimIDs.get((Integer) clusterChild.get(j).classValue()));
				//System.out.print(SimIDs.get((Integer) clusterChild.get(j).classValue()) + "\t");
				Centroid = Centroid.add(clusterChild.get(j));

			}
			child.setSimID(childSimIDs);
			
			Centroid = Centroid.divide(clusterChild.size());

			DistanceMeasure dm = new EuclideanDistance();
			double minDistance = dm.measure(Centroid, clusterChild.get(0));
			int representative = 0;
			for (int j = 1; j != clusterChild.size(); j++) {
				double dist = dm.measure(Centroid, clusterChild.get(j));
				if (dm.compare(dist, minDistance)) {
					minDistance = dist;
					representative = j;
				}
			}

			//System.out.println();
			
			int[] selectIDX = new int[childIDs.size()];
			for (int j = 0; j != childIDs.size(); j++)
				selectIDX[j] = childIDs.get(j);
			
			//store sub data 
			child.setData(A.getMatrix(selectIDX, selectIDX).getArray());	
			currentNode.addChild(child);
			child.setClusterRepresentative(SimIDs.get((Integer) clusterChild.get(representative).classValue()));

			SpectralKMeans(child, currentLevel+1);
			
		}
		
	}


	private List<String> getTimeLabel(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		List<String> list= null;
		try {
			String obj= jsonObject.getJSONArray("Data").getJSONObject(0).get("dps").toString();
			obj=obj.replace("{", "");
			obj = obj.replace("}","");
			obj= obj.replace("\"", "");
			String times[] = obj.split(",");
	//		System.out.println(obj);
			
		/*	try {
				new Thread().sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			list= new ArrayList<String>();
			for(int i =0;i<times.length;i++){
			list.add(times[i].split(":")[0]);	
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}
	
	
	
	
	@Override
	public String toString() {
		
		
		
		//{"name":"SEIR","cluster":[{"name":"TR 0.25","member":["sim1","sim5","sim10"]},{"name":"TR 0.30","member":["sim1","sim5","sim10"]}
		String pippo=Root.toConsole1();
	/*	if(!this.isleaf){
			for(int i=0;i<childs.size();i++){
				if(i<childs.size()-1 ){
					
						if(!this.childs.get(i).isleaf){//getCentroid(this.data)
							pippo=pippo+"{\"name\":\""+this.name[i]+"\",\"cluster\":["+this.childs.get(i).toString()+"]},";
						}else{//getCentroid(this.childs.get(i).data)
							if(!this.childs.get(i).getData().equals(""))
							pippo=pippo+"{\"name\":\""+this.name[i]+"\",\"member\":["+this.childs.get(i).getData()+"]},";
						}
					
				}else{
					
							if(!this.childs.get(i).isleaf){//getCentroid(this.data)
								pippo=pippo+"{\"name\":\""+this.name[i]+"\",\"cluster\":["+this.childs.get(i).toString()+"]}";
							}else{//getCentroid(this.childs.get(i).data)
								if(!this.childs.get(i).getData().equals(""))
								pippo=pippo+"{\"name\":\""+this.name[i]+"\",\"member\":["+this.childs.get(i).getData()+"]}";
							}
				
				}
			}
		}*/
		return pippo;
	}

	
	String getCentroid(List<JSONObject> data2){
		
		String r= null;
		try {
			r = "Centroid " +data2.get(data2.size()/2).getJSONObject("Meta").getInt("simid");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return r;
	}
	
	private String getData() {
		String d="";
		try {
		for (int i = 0; i < this.data.size(); i++) {
			if(i<this.data.size()-1)
				d=d+"{\"name\":\""+this.data.get(i).getJSONObject("Meta").getInt("simid")+"\",\"metric\":\""+this.data.get(i).getJSONArray("Data").getJSONObject(0).getString("metric")+"\"},";
			/*	for(int j=0;j<this.data.get(i).getJSONArray("Data").length();j++){
				d=d+"{\"name\":\""+this.data.get(i).getJSONObject("Metadata").getInt("simid")+"\",\"metric\":\""+this.data.get(i).getJSONArray("Data").getJSONObject(j).getString("metric")+"\"},";
				}*/
			else{
				d=d+"{\"name\":\""+this.data.get(i).getJSONObject("Meta").getInt("simid")+"\",\"metric\":\""+this.data.get(i).getJSONArray("Data").getJSONObject(0).getString("metric")+"\"}";
			}
		}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return d;
	}

	private String getfeaturesfromLevel(String features){
		String [] d = features.split(":");
		
		
		return d[2+this.level];
		
	}
	
	//Shengyu Added
		private String[] inputforMatlab(List<JSONObject> resultset){
			ArrayList<String> nameList = new ArrayList<String>();
			//ArrayList<double[]>datamatrix=new ArrayList<double[]>();
			this.datamatrix=new double[resultset.size()][];
			for(int i=0;i<resultset.size();i++){
				
				//xilun find names here
				String simID;
				double points1[] = null;
				try {
					simID = resultset.get(i).getJSONObject("Meta").getString("simid");//Integer.toString(resultset.get(i).getJSONObject("Metadata").getInt("simid"));
					simID = simID + "^"+resultset.get(i).getJSONObject("Meta").getInt("Index");
					simID = simID +"^" +this.metrica; 
					if (!nameList.contains(simID))
						nameList.add(simID);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					points1 = new double[resultset.get(i).getJSONArray("Data").length()*this.timeSerieslabel.size()];
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					int k=0;
					for(int j=0;j<resultset.get(i).getJSONArray("Data").length();j++){
						
						
						for(int z=0; z<this.timeSerieslabel.size(); z++){
						try {
							//System.out.println(obj.getJSONArray("Data").getJSONObject(j).getJSONObject("dps").toString());
							points1[k]=resultset.get(i).getJSONArray("Data").getJSONObject(j).getJSONObject("dps").getDouble(this.timeSerieslabel.get(z));
							k++;
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
					}}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
				}
				
				//this.datamatrix.add(points1);
				this.datamatrix[i]=points1;
			}
			
			Object[] objectList = nameList.toArray();
			String[] names = Arrays.copyOf(objectList,objectList.length,String[].class);//names is SimIDs
			return names;
			
		}

	@Override
	public String getIndex() {
		// TODO Auto-generated method stub
		return null;
	}
		
}


