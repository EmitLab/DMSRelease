package us.asu.emitlab.clustering.comparator;

import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

import us.asu.emitlab.metric.EuclidianDistance1;
import us.asu.emitlab.metric.MetricInterface;


public class EuclidParameterComparator implements Comparator<JSONObject>{
	String [] parameters;
	MetricInterface metric;
	int level=-1;

    public EuclidParameterComparator(String[] parametri,MetricInterface metrica) {
		// TODO Auto-generated constructor stub
    	if (metrica==null)
    		metrica= new EuclidianDistance1();
    	
    	this.metric=metrica;
    	this.parameters=parametri;
    	for (int i = 0; i < parametri.length; i++) {
			System.out.println("Metrica: "+parametri[i]);
		}
    	
    	
    	
	}
    
	public EuclidParameterComparator(String[] parametri,MetricInterface metrica,
			int level) {
		// TODO Auto-generated constructor stub
		this.metric=metrica;
    	this.parameters=parametri;
    	this.level=level;
	}

	public double[] valueOfPoint(String[] parameters,MetricInterface metric, JSONObject ogg){
		// the value 0 is metadata or data
		// the other value are the name of the field to cluster
		double[] points= new double[parameters.length-1];
		for(int i=2; i<parameters.length; i++){
			try {
				points[i-1]=ogg.getJSONObject(parameters[1]).getDouble(parameters[i]);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return points;
	}
	public double valueOfPoint(String[] parameters,MetricInterface metric, JSONObject ogg,int level){
		double value=0;
		try {
			//System.out.println("ask a comparison");
			if(ogg.getJSONObject(parameters[1]).has(this.parameters[2+level])){
				value= ogg.getJSONObject(parameters[1]).getDouble(this.parameters[2+level]);
			//	System.out.println(value);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return value;
	}

	
	@Override
	public int compare(JSONObject o1, JSONObject o2) {
		// TODO Auto-generated method stub
		double a=0;
		double b=0;
		int comparato=0;
		if(this.level==-1){
		//	System.out.println("sono a -1");
			a=metric.valueMetric(null, valueOfPoint(parameters,metric, o1));
			b=metric.valueMetric(null, valueOfPoint(parameters,metric, o2));
		}		
		else{
		//	System.out.println("son qui");
			a=metric.valueMetric((double)0, valueOfPoint(parameters,metric, o1, this.level));
			b=metric.valueMetric((double)0, valueOfPoint(parameters,metric, o2, this.level));
		}
		
		if(a<b)
			comparato=-1;
		else if (a>b)
			comparato=1;
		return comparato;
		
		
	}


}
