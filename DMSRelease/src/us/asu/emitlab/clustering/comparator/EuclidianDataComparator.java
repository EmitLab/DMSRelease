package us.asu.emitlab.clustering.comparator;

import java.util.Comparator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import us.asu.emitlab.metric.EuclidianDistance1;
import us.asu.emitlab.metric.MetricInterface;



public class EuclidianDataComparator implements Comparator<JSONObject> {
	List <String> parameters;
	MetricInterface metric;
	
	public EuclidianDataComparator(List <String> parametri,MetricInterface metrica) {
		// TODO Auto-generated constructor stub
    	if (metrica==null){
    		metrica= new EuclidianDistance1();
    	}
    	
    	this.metric=metrica;
    	this.parameters=parametri;
    /*	for (int i = 0; i < parametri.length; i++) {
			System.out.println("Metrica: "+parametri[i]);
		}
    	*/
	}
	
	public double[] valueOfPoint(JSONObject ogg){
		// the value 0 is metadata or data
		
	//	System.out.println("Sono nel valueof point");
		
		double[] points= null;
		try {
			points = new double[parameters.size()*ogg.getJSONArray("Data").length()];
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			for(int j=0;j<ogg.getJSONArray("Data").length();j++)
			
				for(int i=0; i<parameters.size(); i++){
				try {
					points[i]=ogg.getJSONArray("Data").getJSONObject(j).getJSONObject("dps").getDouble(parameters.get(i));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return points;
	}
	
	@Override
	public int compare(JSONObject o1, JSONObject o2) {
		// TODO Auto-generated method stub
		double a=0;
		double b=0;
		int comparato=0;
		//if(this.level==-1){
		//	System.out.println("sono a -1");
			a=metric.valueMetric(null, valueOfPoint(o1));
			b=metric.valueMetric(null, valueOfPoint(o2));
		//}		
/*		else{
		//	System.out.println("son qui");
			a=metric.valueMetric((double)0, valueOfPoint(parameters,metric, o1, this.level));
			b=metric.valueMetric((double)0, valueOfPoint(parameters,metric, o2, this.level));
		}
	*/	
		/*	try {
				System.out.println(o1.getJSONObject("Metadata").getDouble("simid")+"a= "+a + " "+o2.getJSONObject("Metadata").getDouble("simid")+"    b="+b);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		if(a<b)
			comparato=-1;
		else if (a>b)
			comparato=1;
		return comparato;
		
		
	}

}
