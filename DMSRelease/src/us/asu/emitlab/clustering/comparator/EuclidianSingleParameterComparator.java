package us.asu.emitlab.clustering.comparator;

import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

import us.asu.emitlab.metric.MetricInterface;

public class EuclidianSingleParameterComparator implements Comparator<JSONObject> {
String parameter="";
String onWhat="";
MetricInterface metrica=null;

	public EuclidianSingleParameterComparator(String parametri,MetricInterface metrica,String onwhat){
	 if(onwhat==null)
		 this.onWhat="Metadata";
	 else
		 this.onWhat=onwhat;
	 this.metrica=metrica;
		this.parameter=parametri;
		
	//	System.out.println(this.onWhat+"   "+this.parameter);
	}
	
	@Override
	public int compare(JSONObject o1, JSONObject o2) {
		// TODO Auto-generated method stub
		double a=0;
		double b=0;
		int comparato=0;
		
		//	System.out.println("sono a -1");
			a= valueOfPoint(o1);
			b= valueOfPoint(o2);
		
		
		if(a<b)
			comparato=-1;
		else if (a>b)
			comparato=1;
		return comparato;
		
		
	}

	
	
	public double valueOfPoint( JSONObject ogg){
		double value=0;
		try {
			//System.out.println("ask a comparison");
			if(ogg.getJSONObject(onWhat).has(this.parameter)){
				value= ogg.getJSONObject(onWhat).getDouble(this.parameter);
			//	System.out.println(value);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return value;
	}

}
