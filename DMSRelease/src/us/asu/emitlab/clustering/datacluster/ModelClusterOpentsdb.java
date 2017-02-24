package us.asu.emitlab.clustering.datacluster;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import us.asu.emitlab.clustering.ClusteringInterface;
import us.asu.emitlab.datastructure.JsonList;
import us.asu.emitlab.metric.MetricInterface;

public class ModelClusterOpentsdb implements ClusteringInterface{
	
	List <String> modellist = new ArrayList<String>();
	List <ArrayList<JSONObject>> listofobject= new ArrayList<ArrayList<JSONObject>>();
	ArrayList<MetricClusterOpentsdb> lista =  new ArrayList<MetricClusterOpentsdb>();
	public static double MinValue=Double.MAX_VALUE;
	public static double MaxValue=Double.MIN_VALUE;
	
	public String collName="Root";
	int k=4;
	
	
	public ModelClusterOpentsdb(String collName) {
		// TODO Auto-generated constructor stub
		this.collName = collName;
	}


	@Override
	public JsonList getObjectClustered(int k, JsonList Resultset,
			MetricInterface metric, String features) {
		MinValue=Double.MAX_VALUE;
        MaxValue=Double.MIN_VALUE;
		this.k=k;

		for (int i = 0; i < Resultset.size(); i++) {
			try {
				String locmodel= Resultset.get(i).getJSONObject("Meta").getString("model");
			if(modellist.isEmpty()){
				
					modellist.add(locmodel);
					listofobject.add( new ArrayList<JSONObject>());
					listofobject.get(0).add(Resultset.get(i));
				
			}else if(IhaveThismodel(modellist,locmodel)){
	
					listofobject.get(indexOfThemodel(modellist,locmodel)).add(Resultset.get(i));
			}else{
	
					modellist.add(locmodel);
					listofobject.add( new ArrayList<JSONObject>());
					listofobject.get(listofobject.size()-1).add(Resultset.get(i));
				}
			} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
		}//splitted the data for different model
		
		for(int i=0;i<modellist.size();i++){
			lista.add(new MetricClusterOpentsdb(k,listofobject.get(i)));
		}

		JSONArray a=null;
		try {
			a	=  new JSONArray("["+toString()+"]");
		} catch (JSONException e) {
			// 	TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new JsonList(a);
	}
	

	@Override
	public JsonList getObjectClustered(int k, JSONArray Resultset,
			MetricInterface metric, String features) {
		// TODO Auto-generated method stub
		
		
		return null;
	}
	
	@Override
	public String toString() {
		String Returnedbase="";
		String Clustered="";
		String ReturnedEnd="";
		if(modellist.size()==0){
			
		}else{
		 Returnedbase="{\"name\":"+"\""+collName+"\",\"cluster\":[";		
		for(int i=0;i<modellist.size();i++){
			if(i<modellist.size()-1){
				Clustered= Clustered+"{\"name\":\""+modellist.get(i)+"\",\"cluster\":["+this.lista.get(i).toString()+"]},";
			}else{
				Clustered= Clustered+"{\"name\":\""+modellist.get(i)+"\",\"cluster\":["+this.lista.get(i).toString()+"]}";
			}
		}
		 ReturnedEnd="]}";
		}
		return Returnedbase+Clustered+ReturnedEnd;
	}


	private boolean IhaveThismodel(List<String> modellist, String string) {
		// TODO Auto-generated method stub
		for(int i =0;i<modellist.size();i++){
			if(modellist.get(i).equalsIgnoreCase(string))
				return true;
		}
		
		return false;
	}

	private int indexOfThemodel(List<String> modellist,String string) {
		// TODO Auto-generated method stub
		for(int i =0;i<modellist.size();i++){
			if(modellist.get(i).equalsIgnoreCase(string))
				return i;
		}
		return 0;
	}
	
}



