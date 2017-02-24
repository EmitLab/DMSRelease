package us.asu.emitlab.metric;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class EuclidianDistance1 implements MetricInterface{
	
	public String name ="Euclidian";
	
	public String getName(){
		return name;
	}
	
	public double valueMetric(double[] p1, double[] p2){
		//System.out.println("call the function");
		return pointDist(p1,p2);
	}
	
	
	protected double pointDist(double p1, double p2){
		
		return Math.sqrt( Math.abs(p1-p2));
		
		
	}

	public double pointDist0(List <String> parameters, JSONObject obj){
		double points[]=null;
		try {
			points = new double[obj.getJSONArray("Data").length()*parameters.size()];
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			int k=0;
			for(int j=0;j<obj.getJSONArray("Data").length();j++)
			
				for(int i=0; i<parameters.size(); i++){
				try {
					points[k]=obj.getJSONArray("Data").getJSONObject(j).getJSONObject("dps").getDouble(parameters.get(i));
					k++;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		double d= valueMetric(null, points);
		
		return d;
		
	}
    public double pointDist(double[] p1, double[] p2) {
        double d = 0;
        
      //  System.out.println("Point Distance");
     if(p1!=null){
    	// System.out.println("meic between 2 points");
        for (int i = 0; i < p1.length; i++) {
            double diff = Math.pow((p1[i] - p2[i]),2);
            if (!Double.isNaN(diff)) {
                d += diff * diff;
            }
        }
       }
     else{
    	 //System.out.println("metric is between 0 and the point");
    	 for (int i = 0; i < p2.length; i++){
    		 double diff = Math.pow((p2[i]),2);
             if (!Double.isNaN(diff)) {
                 d += diff * diff;
             }
    	 }
     }
  //   System.out.println(d);
        d= Math.sqrt(d/p2.length);//aggiunto ora
        return d;
    }

    protected double pointRegionDist(double[] point, double[] min, double[] max) {
        double d = 0;

        for (int i = 0; i < point.length; i++) {
            double diff = 0;
            if (point[i] > max[i]) {
                diff = (point[i] - max[i]);
            }
            else if (point[i] < min[i]) {
                diff = (point[i] - min[i]);
            }

            if (!Double.isNaN(diff)) {
                d += diff * diff;
            }
        }

        return d;
    }

	@Override
	public double valueMetric(double p1, double p2) {
		// TODO Auto-generated method stub
		return  pointDist(p1,p2);
	}
}
