package us.asu.emitlab.datastructure;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonList extends ArrayList<JSONObject>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7194509647361009851L;
	
	public JsonList(){
		super();
	}
	public JsonList(JSONArray array) {
		// TODO Auto-generated constructor stub
		for(int i=0;i<array.length();i++){
			try {
				this.add(array.getJSONObject(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String jsonstring = "";
		for (int i=0;i<this.size();i++){
			if(i==0){
				jsonstring=""+this.get(i);
			}else{
				jsonstring=jsonstring+",\n"+this.get(i);
			}
		}
		return "["+jsonstring+"]";
	}

}
