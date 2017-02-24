package us.asu.emitlab.metric;

import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import servlets.Clustering;
import us.asu.emitlab.clustering.comparator.StateComparator;
import us.asu.emitlab.clustering.comparator.ZoneComparator;
import us.asu.emitlab.datastructure.JsonList;

 
public class EuclidianDistance implements MetricInterface{
	
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

	
	public double pointDist2(List <String> parameters, JSONObject obj,JSONObject obj1){
		JsonList point1=null;
		JsonList point2=null;
		double points1[]=null;
		double points2[] =null;
		try {
			
			point1 = new JsonList(obj.getJSONArray("Data"));
			point2 = new JsonList(obj1.getJSONArray("Data"));
			
			 if (point1.size() > 0){ 
				 if(Clustering.system.equals("epidemic")){
			        Collections.sort(point1, new StateComparator()) ;
				 }else{
					 Collections.sort(point1, new ZoneComparator()) ;
				 }
			 }
			 if(point2.size()>0){
				 if(Clustering.system.equals("epidemic")){     
					 Collections.sort(point1, new StateComparator()) ;
				 }else{
					Collections.sort(point1, new ZoneComparator()) ;
				}  
			 }
			points1 = new double[point1.size()*parameters.size()];
			points2 = new double[point2.size()*parameters.size()];
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			int k=0;
		//	System.out.println("size a: "+obj.toString());
		//	System.out.println("size b: "+obj1.toString());
			/*try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			for(int j=0;j<obj.getJSONArray("Data").length();j++){
				int i=0;
				
				for( i=0; i<parameters.size(); i++){
				try {
				//	System.out.println(obj.toString());
					//System.out.println(obj.getJSONArray("Data").getJSONObject(j).getJSONObject("dps").toString());
					points1[k]=point1.get(j).getJSONObject("dps").getDouble(parameters.get(i));
					//points1[k]=obj.getJSONArray("Data").getJSONObject(j).getJSONObject("dps").getDouble(parameters.get(i));
				
				//	System.out.println(obj1.toString());
					//System.out.println(obj1.getJSONArray("Data").getJSONObject(j).getJSONObject("dps").toString());
					points2[k]=point2.get(j).getJSONObject("dps").getDouble(parameters.get(i));
					//points2[k]=obj1.getJSONArray("Data").getJSONObject(j).getJSONObject("dps").getDouble(parameters.get(i));
					
					k++;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
				}
			}}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		}
		
		double d= valueMetric(points1, points2);
		
		return d;
		
	}

	
	public double pointDist1(List <String> parameters, JSONObject obj,JSONObject obj1){
		double points1[]=null;
		double points2[] =null;
		try {
			points1 = new double[obj.getJSONArray("Data").length()*parameters.size()];
			points2 = new double[obj1.getJSONArray("Data").length()*parameters.size()];
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			int k=0;
	//		System.out.println("size a: "+obj.toString());
	//		System.out.println("size b: "+obj1.toString());
			/*try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			for(int j=0;j<obj.getJSONArray("Data").length();j++){
				int i=0;
			//	System.out.println(obj.toString());
			//	System.out.println(obj1.toString());
				for( i=0; i<parameters.size(); i++){
				try {
					
					//System.out.println(obj.getJSONArray("Data").getJSONObject(j).getJSONObject("dps").toString());
					points1[k]=obj.getJSONArray("Data").getJSONObject(j).getJSONObject("dps").getDouble(parameters.get(i));
					//System.out.println(obj1.getJSONArray("Data").getJSONObject(j).getJSONObject("dps").toString());
					
					points2[k]=obj1.getJSONArray("Data").getJSONObject(j).getJSONObject("dps").getDouble(parameters.get(i));
					k++;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
		//			e.printStackTrace();
				}
			}}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		}
		
		double d= valueMetric(points1, points2);
		
		return d;
		
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
    //	 System.out.println("meic between 2 points");
        for (int i = 0; i < p1.length; i++) {
        	if(Clustering.MaxValue<p1[i])
        		Clustering.MaxValue=p1[i];
        	if(Clustering.MaxValue<p2[i])
        		Clustering.MaxValue=p2[i];
        	if(Clustering.MinValue>p1[i])
        		Clustering.MinValue=p1[i];
        	if(Clustering.MinValue>p2[i])
        		Clustering.MinValue=p2[i];
            
        	double diff = Math.pow((p1[i] - p2[i]),2);
            if (!Double.isNaN(diff)) {
            	
                d += diff * diff;
            }
        }
       }
     else{
    	 //System.out.println("metric is between 0 and the point");
    	 for (int i = 0; i < p2.length; i++){
    		 if(Clustering.MaxValue<p1[i])
         		Clustering.MaxValue=p1[i];
         	if(Clustering.MaxValue<p2[i])
         		Clustering.MaxValue=p2[i];
         	if(Clustering.MinValue>p1[i])
         		Clustering.MinValue=p1[i];
         	if(Clustering.MinValue>p2[i])
         		Clustering.MinValue=p2[i];
    		 
         	double diff = Math.pow((p2[i]),2); 
             if (!Double.isNaN(diff)) {
                 d += diff * diff;
             }
    	 }
     }
  //System.out.println("this distance: "+d);
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
