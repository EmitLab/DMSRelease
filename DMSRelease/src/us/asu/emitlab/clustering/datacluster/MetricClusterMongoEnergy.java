package us.asu.emitlab.clustering.datacluster;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import us.asu.emitlab.clustering.ClusteringInterface;
import us.asu.emitlab.clustering.utility.Utility;
import us.asu.emitlab.datastructure.JsonList;
import us.asu.emitlab.metric.MetricInterface;


public class MetricClusterMongoEnergy implements ClusteringInterface{
	List <String> metricList = new ArrayList<String>();
	List <ArrayList<JSONObject>> listofobject= new ArrayList<ArrayList<JSONObject>>();
	ArrayList<DataSimilarityInterface>lista =  new ArrayList<DataSimilarityInterface>();
	//ArrayList<DataNodeMedium> lista =  new ArrayList<DataNodeMedium>();
	//ArrayList<DataSimilarityMongoEnergyMatlab>lista =  new ArrayList<DataSimilarityMongoEnergyMatlab>();
	//ArrayList<DatanodeSim1>lista =  new ArrayList<DatanodeSim1>();
//	private LoggerInt log =new Logger("ClusterLogMetricCluster1.txt");
	String metric="";
	String features;
	int k=4;
	boolean matlab=false;
	@Override
	public JsonList getObjectClustered(int k, JSONArray Resultset,MetricInterface metric, String features) {
	
		
		 JSONArray a=null;
			try {
				
				a	=  new JSONArray("["+toString()+"]");
			} catch (JSONException e) {
				// 	TODO Auto-generated catch block
				e.printStackTrace();
			}
		return new JsonList(a);
	}

	public MetricClusterMongoEnergy(int k, ArrayList<JSONObject> arrayList,boolean p) {
		this.k=k;
		
		System.out.println("Clustering on mongodb Energy");
		System.out.println("Size: "+arrayList.size());
		
		if(arrayList.size()>300){
			matlab=true;
		}/*	
			System.out.println("Using matlab: "+ arrayList.size());
		}else{
			System.out.println("using java: "+arrayList.size());
		}*/
		/*if(matlab)
			System.out.println("Cluster on Matlab");
		else
			System.out.println("Cluster on Java");*/
		long ts = System.currentTimeMillis();
		for(int i=0;i<arrayList.size();i++){
			try {
				String metric = arrayList.get(i).getJSONObject("Meta").getString("Metric");
			
				if(!isInMetric(metric)&&!listofobject.isEmpty()){
				//	System.out.println("metric is different");
					if(matlab)
						lista.add(new DataSimilarityMongoEnergyMatlab(null, k, listofobject.get(listofobject.size()-1), null, features,metricList.get(metricList.size()-1)));
					else
						lista.add(new DataSimilarityMongoEnergy(null, k, listofobject.get(listofobject.size()-1), null, features,metricList.get(metricList.size()-1)));
					metricList.add(metric);
					listofobject.add( new ArrayList<JSONObject>());
					listofobject.get(listofobject.size()-1).add(arrayList.get(i));
				}else if(!isInMetric(metric)){
				//	System.out.println("Metric is empty");
					metricList.add(metric);
					listofobject.add( new ArrayList<JSONObject>());
					listofobject.get(listofobject.size()-1).add(arrayList.get(i));
				}else if(i==arrayList.size()-1){
					listofobject.get(listofobject.size()-1).add(arrayList.get(i));
					if(matlab)
						lista.add(new DataSimilarityMongoEnergyMatlab(null, k, listofobject.get(listofobject.size()-1), null, features,metricList.get(metricList.size()-1)));
					else
						lista.add(new DataSimilarityMongoEnergy(null, k, listofobject.get(listofobject.size()-1), null, features,metricList.get(metricList.size()-1)));
				}
				else{
					listofobject.get(listofobject.size()-1).add(arrayList.get(i));
				}
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Time Required: "+ (System.currentTimeMillis() - ts));
	//	System.out.println("Fine Costruttore: " + metricList.size()+" +++ "+ listofobject.size());
	}
	
	public MetricClusterMongoEnergy(int k, ArrayList<JSONObject> arrayList) {
		// TODO Auto-generated constructor stub
		this.k=k;
		//creo la lista dele meetriche presenti
	//	log.debug("create the MetricCluster");
		try {
			
			int simId= arrayList.get(0).getJSONObject("Metadata").getInt("simid");
			for(int j=0;j<arrayList.size();j++){
			if(simId == arrayList.get(j).getJSONObject("Metadata").getInt("simid"))
				for(int i=0;i<arrayList.get(j).getJSONArray("Data").length();i++){
				if(!isInMetric(arrayList.get(j).getJSONArray("Data").getJSONObject(i).getString("metric"))){
					metricList.add(arrayList.get(j).getJSONArray("Data").getJSONObject(i).getString("metric"));
				//	System.out.println("Metrica: "+ metricList.get(metricList.size()-1));
				}
			}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		for(int i =0;i<metricList.size();i++){
			listofobject.add( new ArrayList<JSONObject>());
		}
		
		for(int i= 0 ; i<arrayList.size();i++){
			listofobject.get(correctmodelindex(arrayList.get(i))).add(arrayList.get(i));
		}
		
		
			

			 for(int i=0;i<metricList.size();i++){
				// System.out.println("listofobj : "+ listofobject.get(i).size());
				 if(listofobject.get(i).size()>100){
						matlab=true;
					}
				 else{
					 matlab=false;
				 }
				 if(matlab)
					 lista.add(new DataSimilarityMongoEnergyMatlab(null, k, listofobject.get(i), null, features,metricList.get(i)));
				 else
					 lista.add(new DataSimilarityMongoEnergy(null, k, listofobject.get(i), null, features,metricList.get(i)));
				 //lista.add(new DatanodeSim1(null, k, listofobject.get(i), null, features,0));//new DataNodeSimilarity(null, k, listofobject.get(i), null, features));//(new DataNodeSimilarity(null, k, listofobject.get(i), null, features));//new DataNodeMedium(null, k, listofobject.get(i), null, features));new DatanodeSim1(null, k, listofobject.get(i), null, features,0));//new DataNodeMedium(null, k, listofobject.get(i), null, features));//(new DataNodeSimilarity(null, k, listofobject.get(i), null, features));//new DataNodeMedium(null, k, listofobject.get(i), null, features));
				 //lista.add(new DataNodeSimilarity(null, k, listofobject.get(i), null, features));
			 }
			
		
	}
	
	private int correctmodelindex(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		int index=0;
		String Metric=null;
		boolean esito = false;
		try {
			Metric = jsonObject.getJSONArray("Data").getJSONObject(0).getString("metric");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0;i<metricList.size() && !esito ;i++){
			if (Metric.equals(metricList.get(i))){
				index=i;
				esito = true;
			}
		}
		
		return index;
	}

	public void print(){
		for(int i =0;i<listofobject.size();i++){
			for(int j=0;j<listofobject.get(i).size();j++){
				System.out.println("Stampa: "+i+" "+j+"  " +listofobject.get(i).get(j).toString());
			}
		}
	}

	private boolean isInMetric(String string) {
		// TODO Auto-generated method stub
		for(int i =0;i<metricList.size();i++){
			if(metricList.get(i).equalsIgnoreCase(string))
				return true;
		}
		
		return false;
	}

	private String getData(List<JSONObject> jsonListlocal) {
		String d="";
		try {
		for (int i = 0; i < jsonListlocal.size(); i++) {
			if(i<jsonListlocal.size()-1){
				d=d+"{\"name\":\""+jsonListlocal.get(i).getJSONObject("Metadata").getInt("simid")+"\",\"metric\":\""+jsonListlocal.get(i).getJSONArray("Data").getJSONObject(0).getString("metric")+"\",\"Index\":"+jsonListlocal.get(i).getJSONObject("Metadata").getInt("Index")+"},";
				//	d=d+"{\"name\":\""+jsonListlocal.get(i).getJSONObject("Metadata").getInt("simid")+"\"},";
					//System.out.println("metto dentro il dato con SimID= "+this.data.get(i).getJSONObject(features.split(":")[1]).getInt("simid"));
				}
				else{
					d=d+"{\"name\":\""+jsonListlocal.get(i).getJSONObject("Metadata").getInt("simid")+"\",\"metric\":\""+jsonListlocal.get(i).getJSONArray("Data").getJSONObject(0).getString("metric")+"\",\"Index\":"+jsonListlocal.get(i).getJSONObject("Metadata").getInt("Index")+"}";
					//d=d+"{\"name\":\""+jsonListlocal.get(i).getJSONObject("Metadata").getInt("simid")+"\"}";
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return d;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		//{"name":"Epidemic","cluster":[{"name":"SEIR","cluster":[{"name":"TR 0.25","member":["sim1","sim5","sim10"]},{"name":"TR 0.30","member":["sim1","sim5","sim10"]}]},{"name":"SIR","member":["sim1","sim5","sim10"]}]
		//}';
	//	System.out.println("sono nel toString del metriccluster "+ listofobject.get(0).size());
		String Returnedbase="";
		String Clustered="";
		for(int i=0;i<metricList.size();i++){
			if(i<metricList.size()-1){
			/*	if(listofobject.get(i).size()<k)
					Clustered=Clustered+"{\"name\":\""+metricList.get(i)+"\",\"member\":["+getData(this.listofobject.get(i))+"]},";
				else*/
				Clustered= Clustered+"{\"Index\":\""+this.lista.get(i).getIndex()+"\",\"name\":\""+metricList.get(i)+"\",\"cluster\":["+this.lista.get(i).toString()+"]},";
			}else{
				/*if(listofobject.get(i).size()<k)
					Clustered=Clustered+"{\"name\":\""+metricList.get(i)+"\",\"member\":["+getData(this.listofobject.get(i))+"]},";
				else{*/
			//System.out.println(i+":njfnenviu:"+this.lista.size());
					Clustered= Clustered+"{\"Index\":\""+this.lista.get(i).getIndex()+"\",\"name\":\""+metricList.get(i)+"\",\"cluster\":["+this.lista.get(i).toString()+"]}";
				//}
			}
		}
		String ReturnedEnd="";
		
		return Returnedbase+Clustered+ReturnedEnd;
	}

@Override
public JsonList getObjectClustered(int k, JsonList Resultset,
		MetricInterface metric, String features) {
	// TODO Auto-generated method stub
	return null;
}

}