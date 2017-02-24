package ensembles;

import defaults.*;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

import us.asu.emitlab.connection.timeseriesdb.ConnectionImplMongoDB;
import connections.MongoConnector;

@WebServlet("/GetMap")
public class GetMap extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public GetMap() {
		super();
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String[] query = request.getParameterValues("graph");
		String stateinterested = request.getParameter("intState");
		HttpSession session = request.getSession(true);
		String Project = session.getAttribute(SessionVariableList.CURRENT_PROJECT_NAME).toString();


		String statelist [] = stateinterested.split("_");

		MongoConnector ci = new ConnectionImplMongoDB();
		ci.getInstance("localhost",3309);

		BasicDBObject qobj= new BasicDBObject("name",query[0].toLowerCase());
		
		Project = Project.substring(0,1).toUpperCase() + Project.substring(1);
		DBCursor resultset = ci.getDbFromName(Project).getCollection("Graph").find(qobj);
		BasicDBObject r=null;
		if(resultset.hasNext())
			r= (BasicDBObject) resultset.next();
		else{
			System.out.println("error");
		}
		String XMLGraphdata= r.get("data").toString();
		JSONObject graph= null;
		try {
			graph = new JSONObject(XMLGraphdata);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		org.json.JSONArray arr =null;
		try {
			arr = graph.getJSONArray("statejson");
			for(int i =0 ;i< arr.length();i++){
				for(int j =0; j<statelist.length;j++){
					if(((JSONObject)arr.get(i)).getString("name").contains(statelist[j])){
						((JSONObject)arr.get(i)).remove("interest");
						((JSONObject)arr.get(i)).append("interest", 1);
					}
				}
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		graph.remove("statejson");
		try {
			graph.put("statejson1", arr);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		response.getOutputStream().print(graph.toString());
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		doGet(request, response);
	}

}
