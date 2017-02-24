package us.asu.emitlab.clustering.datacluster;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import us.asu.emitlab.clustering.ClusteringInterface;
import us.asu.emitlab.datastructure.JsonList;
import us.asu.emitlab.metric.MetricInterface;



public class MetricClusterOpentsdbEnergy implements ClusteringInterface{
	List <String> metricList = new ArrayList<String>();
	List <ArrayList<JSONObject>> listofobject= new ArrayList<ArrayList<JSONObject>>();
	ArrayList<DataSimilarityInterface>lista =  new ArrayList<DataSimilarityInterface>();
	//ArrayList<DataSimilarityOpentsdbEnergyMatlab>lista =  new ArrayList<DataSimilarityOpentsdbEnergyMatlab>();

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
		//		log.debug(e.getLocalizedMessage());
			}
		return new JsonList(a);
	}
	
	public MetricClusterOpentsdbEnergy(int k, ArrayList<JSONObject> arrayList) {
		// TODO Auto-generated constructor stub
		this.k=k;
		//creo la lista dele meetriche presenti
		
		try {
			String simId= arrayList.get(0).getJSONObject("Meta").getString("simid");
			for(int j=0;j<arrayList.size();j++){
			if(simId.equals( arrayList.get(j).getJSONObject("Meta").getString("simid")))
				for(int i=0;i<arrayList.get(j).getJSONArray("Data").length();i++){
				if(!isInMetric(arrayList.get(j).getJSONArray("Data").getJSONObject(i).getString("metric"))){
					metricList.add(arrayList.get(j).getJSONArray("Data").getJSONObject(i).getString("metric"));
				//	log.debug("Metrica: "+ metricList.get(metricList.size()-1));
				}
			}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		//	log.debug(e.getLocalizedMessage());
		}
		
		
		
		for(int i =0;i<metricList.size();i++){
			listofobject.add( new ArrayList<JSONObject>());
		}
		
		for(int i= 0 ; i<arrayList.size();i++){
			listofobject.get(correctmodelindex(arrayList.get(i))).add(arrayList.get(i));
		}
		
		
			

			 for(int i=0;i<metricList.size();i++){
			//	 log.debug("listofobj : "+ listofobject.get(i).size());
				 if(listofobject.get(i).size()>100){
						matlab=true;
					}
				 else{
					 matlab=false;
				 }
				 if(matlab)
					 lista.add(new DataSimilarityOpentsdbEnergyMatlab(null, k, listofobject.get(i), null, features,metricList.get(i)));
				 else
					 lista.add(new DataSimilarityOpentsdbEnergy(null, k, listofobject.get(i), null, features,metricList.get(i)));
				 //lista.add(new DataNodeSimilarity1(null, k, listofobject.get(i), null, features,metricList.get(i)));
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
		//	log.debug(e.getLocalizedMessage());
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
				}
				else{
					d=d+"{\"name\":\""+jsonListlocal.get(i).getJSONObject("Meta").getInt("simid")+"\",\"metric\":\""+jsonListlocal.get(i).getJSONArray("Data").getJSONObject(0).getString("metric")+"\",\"Index\":"+jsonListlocal.get(i).getJSONObject("Meta").getInt("Index")+"}";

				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		//	log.debug(e.getLocalizedMessage());
		}
		return d;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub

		String Returnedbase="";
		String Clustered="";
		for(int i=0;i<metricList.size();i++){
			if(i<metricList.size()-1){

				Clustered= Clustered+"{\"Index\":\""+this.lista.get(i).getIndex()+"\",\"name\":\""+metricList.get(i)+"\",\"cluster\":["+this.lista.get(i).toString()+"]},";
			}else{
		
					Clustered= Clustered+"{\"Index\":\""+this.lista.get(i).getIndex()+"\",\"name\":\""+metricList.get(i)+"\",\"cluster\":["+this.lista.get(i).toString()+"]}";
	
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