package us.asu.emitlab.clustering.comparator;

import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

public class MetadataComparator implements Comparator<JSONObject>{

	@Override
	public int compare(JSONObject o1, JSONObject o2) {
		// TODO Auto-generated method stub
		int val = 0;
		String SimID="";
		String Model="";
		 try {//check Model
			val= o1.get("model").toString().compareTo( o2.get("model").toString());
		
		if (val ==1){//check SimID
			val= o1.get("simid").toString().compareTo( o2.get("simid").toString());
		}
		 } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return val;
	}

}
