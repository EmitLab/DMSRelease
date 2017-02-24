package us.asu.emitlab.clustering.comparator;

import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

public class VariateComparator implements Comparator<JSONObject>{

	@Override
	public int compare(JSONObject o1, JSONObject o2) {
		// TODO Auto-generated method stub
		int val = 0;
		
		try {
			val =  o1.get("zone").toString().compareTo( o2.get("zone").toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return val;
	}

}
