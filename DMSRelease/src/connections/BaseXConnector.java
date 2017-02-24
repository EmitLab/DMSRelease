package connections;

import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQResultSequence;

import net.xqj.basex.BaseXXQDataSource;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Node;

import us.asu.emitlab.datastructure.JsonList;


public class BaseXConnector extends DatabaseConnection{

	public BaseXConnector(String username, String password, String url,String dbName,int port) {
		super(username, password, url, dbName, port);
	}

	public JsonList executeQueryEnergy(String query, XQConnection conn) {
		//	System.out.println("in query basex connector");
			XQExpression xqe =null;
			XQResultSequence rs = null;
			JsonList list = new JsonList();
			try {
				xqe = conn.createExpression();
			} catch (XQException e) {
				e.printStackTrace();
			} //create a connection 
				
			try {
				rs = xqe.executeQuery(query);
				String sim   = null;
			    String model = null;
			    String areas = "";
			    int i=0;
				  while(rs.next()){
					  try {
						  Node n = rs.getNode();
							
							if(i==0){
								sim=n.getTextContent();
								i++;
							}else if(i==1){
								model=n.getTextContent();
								i++;
							}
							else if(n.getNodeName().equalsIgnoreCase("ATTR")){
								//System.out.println("simid: " +sim +" model: " + model+" zone: "+n.getTextContent());
								areas=areas+n.getTextContent()+"|";
								//list.add(new JSONObject("{"+"simid: " +sim.split("\\|")[0] +", model: " + model+", zone: "+n.getTextContent()+"}"));
								//list.add(new JSONObject("{"+"simid: " +sim +", model: " + model+", zone: "+n.getTextContent()+"}"));
							}
							else{
								//System.out.println("else");
								areas =  areas.substring(0, areas.length()-1);
								//System.out.println("{"+"simid: " +sim.split("\\|")[0] +", model: " + model+", zone: "+areas+"}");
								list.add(new JSONObject("{"+"simid: " +sim.split("\\|")[0] +", model: " + model+", zones: "+areas+"}"));
								i=1;
								areas ="";
								sim=n.getTextContent();
							}
						//list.add(new JSONObject("{"+rs.getItemAsString(null)+"}"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				  }
				      //System.out.println(rs.getItemAsString(null));
			} catch (XQException e) {
				e.printStackTrace();
			}
			return list;
		}

	
	public JsonList executeQuery(String query, XQConnection conn) {
		
		XQExpression xqe =null;
		XQResultSequence rs = null;
		JsonList list = new JsonList();
		try {
			xqe = conn.createExpression();
		} catch (XQException e) {
			e.printStackTrace();
		} //create a connection 
		
		System.out.println(query);
		try {
			rs = xqe.executeQuery(query);
			  while(rs.next()){
				  try {
					list.add(new JSONObject("{"+rs.getItemAsString(null)+"}"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			  }
			      //System.out.println(rs.getItemAsString(null));
		} catch (XQException e) {
			e.printStackTrace();
		}
		return list;
	}

	public XQConnection getConnection() {
		// TODO Auto-generated method stub
		XQDataSource xqs = new BaseXXQDataSource();
	    XQConnection conn= null;
	    try {
	    	xqs.setProperty("serverName", "localhost");
		    xqs.setProperty("port", "3308");// local port for BaseX
			 conn = xqs.getConnection("admin", "admin");	 
		} catch (XQException e) {
			e.printStackTrace();
		}
		return conn;
	}
	@Override
	public JsonList executeQuery(String query, XQConnection conn, String type) {
		XQExpression xqe =null;
		XQResultSequence rs = null;
		JsonList list = new JsonList();
		//log.debug("query: "+query);
		if(type.toLowerCase().equals("epidemic")){
			try {
				xqe = conn.createExpression();
			} catch (XQException e) {
				e.printStackTrace();
			} //create a connection 
			try {
				rs = xqe.executeQuery(query);
				while(rs.next()){
				  try {
					//  System.out.println(rs.getItemAsString(null));
					list.add(new JSONObject("{"+rs.getItemAsString(null)+"}"));
				  } catch (JSONException e) {
					e.printStackTrace();
				  }
				}
			} catch (XQException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(type.toLowerCase().equals("energy")){
			try {
				xqe = conn.createExpression();
			} catch (XQException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //create a connection	
			try {
				rs = xqe.executeQuery(query);
				String sim   = null;
			    String model = null;
			    String areas="";
			    int i=0;
				  while(rs.next()){
					  try {
						  Node n = rs.getNode();
							if(i==0){
								sim=n.getTextContent();
								i++;
							}else if(i==1){
								model=n.getTextContent();
								i++;
							}
							else if(n.getNodeName().equalsIgnoreCase("ATTR")){
								areas=areas+n.getTextContent()+"|";
							}
							else{
								areas =  areas.substring(0, areas.length()-1);
								list.add(new JSONObject("{"+"simid: " +sim.split("\\|")[0] +", model: " + model+", zones: "+areas+"}"));
								i=1;
								sim=n.getTextContent();
								areas ="";
							}
					} catch (JSONException e) {
					}
				}
			} catch (XQException e) {
			}
		}
		
		return list;
	}

}
