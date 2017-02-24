package us.asu.emitlab.clustering.datacluster;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import us.asu.emitlab.clustering.ClusteringInterface;
import us.asu.emitlab.datastructure.JsonList;
import us.asu.emitlab.metric.MetricInterface;



public class ModelClusterOpentsdbEnergy implements ClusteringInterface{
	List <String> modellist = new ArrayList<String>();
	List <ArrayList<JSONObject>> listofobject= new ArrayList<ArrayList<JSONObject>>();
//	private LoggerInt log =new Logger("AdvClusterEnergyClassLog1.txt");
	//ArrayList<MetricCluster> lista =  new ArrayList<MetricCluster>();
	//ArrayList<MetricCluster1> lista =  new ArrayList<MetricCluster1>();
	ArrayList<MetricClusterOpentsdbEnergy> lista =  new ArrayList<MetricClusterOpentsdbEnergy>();
	  public static double MinValue=Double.MAX_VALUE;
	  public static double MaxValue=Double.MIN_VALUE;
	
	  public String collName="Root";
	int k=4;
	
	public ModelClusterOpentsdbEnergy(String collName) {
		// TODO Auto-generated constructor stub
		this.collName = collName;
	}
	@Override
	public JsonList getObjectClustered(int k, JSONArray Resultset,MetricInterface metric, String features) {
		
		MinValue=Double.MAX_VALUE;
        MaxValue=Double.MIN_VALUE;
		this.k=k;
		//log.debug(""+Resultset.length());
		String []parameters= features.split(":");
		// TODO Auto-generated method stub
		
		for (int i = 0; i < Resultset.length(); i++) {
			try {
			if(modellist.isEmpty()){
				
					modellist.add(Resultset.getJSONObject(i).getJSONObject("Meta").getString("model"));
					listofobject.add( new ArrayList<JSONObject>());
					listofobject.get(0).add(Resultset.getJSONObject(i));
				
			}else if(IhaveThismodel(modellist,Resultset.getJSONObject(i).getJSONObject("Meta").getString("model"))){
	//				System.out.println(i+" : "+Resultset.getJSONObject(i));
					listofobject.get(indexOfThemodel(modellist,Resultset.getJSONObject(i).getJSONObject("Meta").getString("model"))).add(Resultset.getJSONObject(i));
			}else{
				//	System.out.println("different model");
					modellist.add(Resultset.getJSONObject(i).getJSONObject("Meta").getString("model"));
					listofobject.add( new ArrayList<JSONObject>());
					listofobject.get(listofobject.size()-1).add(Resultset.getJSONObject(i));
				}
			} catch (JSONException e) {
					// TODO Auto-generated catch block
					//log.debug(e.getLocalizedMessage());
			}
		}//splitted the data for different model
		
//		System.out.println(listofobject.get(modellist.size()-1).size());
		
		for(int i=0;i<modellist.size();i++){
			lista.add(new MetricClusterOpentsdbEnergy(k,listofobject.get(i)));
		}
		
			
			
	//	System.out.println(toString());
		JSONArray a=null;
		try {
			a	=  new JSONArray("["+toString()+"]");
		} catch (JSONException e) {
			// 	TODO Auto-generated catch block
			//log.debug(e.getLocalizedMessage());
		}
		
		return new JsonList(a);
	}
	
	@Override
	public JsonList getObjectClustered(int k, JsonList Resultset,
			MetricInterface metric, String features) {
		
		MinValue=Double.MAX_VALUE;
        MaxValue=Double.MIN_VALUE;
		this.k=k;
		//log.debug(""+Resultset.size());
		String []parameters= features.split(":");
		// TODO Auto-generated method stub
		
		for (int i = 0; i < Resultset.size(); i++) {
			try {
			if(modellist.isEmpty()){
				
					modellist.add(Resultset.get(i).getJSONObject("Meta").getString("model"));
					listofobject.add( new ArrayList<JSONObject>());
					listofobject.get(0).add(Resultset.get(i));
				
			}else if(IhaveThismodel(modellist,Resultset.get(i).getJSONObject("Meta").getString("model"))){
	//				System.out.println(i+" : "+Resultset.getJSONObject(i));
					listofobject.get(indexOfThemodel(modellist,Resultset.get(i).getJSONObject("Meta").getString("model"))).add(Resultset.get(i));
			}else{
				//	System.out.println("different model");
					modellist.add(Resultset.get(i).getJSONObject("Meta").getString("model"));
					listofobject.add( new ArrayList<JSONObject>());
					listofobject.get(listofobject.size()-1).add(Resultset.get(i));
				}
			} catch (JSONException e) {
					// TODO Auto-generated catch block
					//log.debug(e.getLocalizedMessage());
			}
		}//splitted the data for different model
		
//		System.out.println(listofobject.get(modellist.size()-1).size());
		
		for(int i=0;i<modellist.size();i++){
			lista.add(new MetricClusterOpentsdbEnergy(k,listofobject.get(i)));
		}
		
			
			
	//	System.out.println(toString());
		JSONArray a=null;
		try {
			a	=  new JSONArray("["+toString()+"]");
		} catch (JSONException e) {
			// 	TODO Auto-generated catch block
			//log.debug(e.getLocalizedMessage());
		}
		
		return new JsonList(a);
	}
	
	private String getData(List<JSONObject> jsonListlocal) {
		String d="";
		try {
		for (int i = 0; i < jsonListlocal.size(); i++) {
			if(i<jsonListlocal.size()-1){
					d=d+"{\"name\":\""+jsonListlocal.get(i).getJSONObject("Meta").getInt("simid")+"\"},";
					//System.out.println("metto dentro il dato con SimID= "+this.data.get(i).getJSONObject(features.split(":")[1]).getInt("simid"));
				}
				else{
					d=d+"{\"name\":\""+jsonListlocal.get(i).getJSONObject("Meta").getInt("simid")+"\"}";
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//log.debug(e.getLocalizedMessage());
		}
		return d;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		//{"name":"Epidemic","cluster":[{"name":"SEIR","cluster":[{"name":"TR 0.25","member":["sim1","sim5","sim10"]},{"name":"TR 0.30","member":["sim1","sim5","sim10"]}]},{"name":"SIR","member":["sim1","sim5","sim10"]}]
		//}';
		String Returnedbase="";
		String Clustered="";
		String ReturnedEnd="";
		if(modellist.size()==0){
			
		}else{
			
		
		
		 Returnedbase="{\"name\":"+"\"Energy\",\"cluster\":[";
		
		for(int i=0;i<modellist.size();i++){
			if(i<modellist.size()-1){
				/*if(listofobject.get(i).size()<k)
					Clustered=Clustered+"{\"name\":\""+modellist.get(i)+"\",\"member\":["+getData(this.listofobject.get(i))+"]},";
				else*/
				Clustered= Clustered+"{\"name\":\""+modellist.get(i)+"\",\"cluster\":["+this.lista.get(i).toString()+"]},";
			}else{
				/*if(listofobject.get(i).size()<k)
					Clustered=Clustered+"{\"name\":\""+modellist.get(i)+"\",\"member\":["+getData(this.listofobject.get(i))+"]},";
				else*/
				Clustered= Clustered+"{\"name\":\""+modellist.get(i)+"\",\"cluster\":["+this.lista.get(i).toString()+"]}";
			}
		}
		 ReturnedEnd="]}";
		}
		return Returnedbase+Clustered+ReturnedEnd;
	}
	private int indexOfThemodel(List<String> modellist,String string) {
		// TODO Auto-generated method stub
		for(int i =0;i<modellist.size();i++){
			if(modellist.get(i).equalsIgnoreCase(string))
				return i;
		}
		return 0;
	}

	private boolean IhaveThismodel(List<String> modellist, String string) {
		// TODO Auto-generated method stub
		for(int i =0;i<modellist.size();i++){
			if(modellist.get(i).equalsIgnoreCase(string))
				return true;
		}
		
		return false;
	}

}

