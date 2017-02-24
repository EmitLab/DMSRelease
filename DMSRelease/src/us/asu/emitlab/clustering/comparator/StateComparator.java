package us.asu.emitlab.clustering.comparator;

import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

public class StateComparator implements Comparator<JSONObject>{

	@Override
	public int compare(JSONObject o1, JSONObject o2) {
		// TODO Auto-generated method stub
		int val=0;
	  try {
		  val= o1.getJSONObject("tags").getString("state").compareTo( o2.getJSONObject("tags").getString("state"));
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		return val;
	}

}
