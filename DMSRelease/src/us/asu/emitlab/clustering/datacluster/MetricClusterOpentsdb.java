package us.asu.emitlab.clustering.datacluster;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import us.asu.emitlab.clustering.ClusteringInterface;
import us.asu.emitlab.datastructure.JsonList;
import us.asu.emitlab.metric.MetricInterface;


public class MetricClusterOpentsdb implements ClusteringInterface{
	List <String> metricList = new ArrayList<String>();
	List <ArrayList<JSONObject>> listofobject= new ArrayList<ArrayList<JSONObject>>();
	ArrayList<DataSimilarityInterface>lista =  new ArrayList<DataSimilarityInterface>();
	//ArrayList<DataSimilarityOpentsdbMatlab>lista =  new ArrayList<DataSimilarityOpentsdbMatlab>();
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
	
	public MetricClusterOpentsdb(int k, ArrayList<JSONObject> arrayList) {
		// TODO Auto-generated constructor stub
		this.k=k;
		
		//	System.out.println("Using matlab: "+ arrayList.size());
	//	}else{
	//		System.out.println("using java: "+arrayList.size());
	//	}
		try {
			int simId= arrayList.get(0).getJSONObject("Meta").getInt("simid");
			for(int j=0;j<arrayList.size();j++){
			if(simId == arrayList.get(j).getJSONObject("Meta").getInt("simid"))
				for(int i=0;i<arrayList.get(j).getJSONArray("Data").length();i++){
				if(!isInMetric(arrayList.get(j).getJSONArray("Data").getJSONObject(i).getString("metric"))){
					metricList.add(arrayList.get(j).getJSONArray("Data").getJSONObject(i).getString("metric"));
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
				 if(listofobject.get(i).size()>100){
						matlab=true;
					}
				 else{
					 matlab=false;
				 }
				 if(matlab)
					 lista.add(new DataSimilarityOpentsdbMatlab(null, k, listofobject.get(i), null, features,metricList.get(i)));
				 else
					 lista.add(new DataSimilarityOpentsdb(null, k, listofobject.get(i), null, features,metricList.get(i)));
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
				d=d+"{\"name\":\""+jsonListlocal.get(i).getJSONObject("Meta").getInt("simid")+"\",\"metric\":\""+jsonListlocal.get(i).getJSONArray("Data").getJSONObject(0).getString("metric")+"\",\"Index\":"+jsonListlocal.get(i).getJSONObject("Meta").getInt("Index")+"},";
				//	d=d+"{\"name\":\""+jsonListlocal.get(i).getJSONObject("Metadata").getInt("simid")+"\"},";
					//System.out.println("metto dentro il dato con SimID= "+this.data.get(i).getJSONObject(features.split(":")[1]).getInt("simid"));
				}
				else{
					d=d+"{\"name\":\""+jsonListlocal.get(i).getJSONObject("Meta").getInt("simid")+"\",\"metric\":\""+jsonListlocal.get(i).getJSONArray("Data").getJSONObject(0).getString("metric")+"\",\"Index\":"+jsonListlocal.get(i).getJSONObject("Meta").getInt("Index")+"}";
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
		System.out.println("sono nel toString del metriccluster "+ listofobject.get(0).size());
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
				System.out.println(i+":njfnenviu:"+this.lista.size());
					Clustered= Clustered+"{\"Index\":\""+this.lista.get(i).getIndex()+"\",\"name\":\""+metricList.get(i)+"\",\"cluster\":["+this.lista.get(i).toString()+"]}";
				//}
			}
		}
		String ReturnedEnd="";
		
		return Returnedbase+Clustered+ReturnedEnd;
	}


/*public static void main(String[] args) {
	 String path= "JsonFile";
	 String nameFile= "log.json";
	String json_string = Utility.getInstance().readFile(path, nameFile);
	JSONArray jsonarray=null;
	try {
	//	jsonObj=new JSONObject(json_string);
		jsonarray= new JSONArray(json_string); //we already recive a jsonarray from the top layer
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	ArrayList<JSONObject> array = new ArrayList<JSONObject>();
	
	for (int i = 0; i < jsonarray.length(); i++) {
		try {
			array.add(jsonarray.getJSONObject(i));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//splitted the data for different model
	MetricClusterOpentsdb c= new MetricClusterOpentsdb(4, array);
	c.print();
}
*/
@Override
public JsonList getObjectClustered(int k, JsonList Resultset,
		MetricInterface metric, String features) {
	// TODO Auto-generated method stub
	return null;
}

}