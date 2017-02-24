package us.asu.emitlab.datastructure;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;
public class UserProfile implements Serializable{
/**
	 * 
	 */
private static final long serialVersionUID = -8572172386886045635L;
private String name;
private String id;
private String message;
private String site;
public UserProfile() {
	// TODO Auto-generated constructor stub
	name="podsoi";
	id="dsda";
	message="dasda";
	site=null;
}
//"Message:Welcome;Name:"+name+";locUID:"+localuid;
public UserProfile(String message){
   try {
	JSONObject o =new JSONObject(message);
	setName(o.getString("Name"));
	setId(""+o.getInt("locUID"));
	setMessage(o.getString("Message"));
} catch (JSONException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}	
}
public UserProfile(String message, boolean esito){
	JSONObject o;
	try {
		o = new JSONObject(message);
System.out.println(o.getString("Message"));
		setMessage(o.getString("Message"));
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	setId("");
	setName("");
	setSite("");
}


public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}

public String getSite() {
	return site;
}
public void setSite(String site) {
	this.site = site;

}}
