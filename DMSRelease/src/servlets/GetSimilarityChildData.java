package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.util.JSON;

import connections.MongoConnector;
import defaults.SessionVariableList;
import us.asu.emitlab.connection.timeseriesdb.ConnectionImplMongoDB;

@WebServlet("/GetSimilarityChildData")
public class GetSimilarityChildData extends HttpServlet {
	private static final long serialVersionUID = 1L; 
	public String project = "";
	public String dbName = "";
	public GetSimilarityChildData() {
		super();
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String returned ="";
		String propertyModelZone = "";
		String type ="";
		type=type+request.getParameter("type");
		project = session.getAttribute(SessionVariableList.CURRENT_PROJECT_NAME).toString().toLowerCase();
		// String system = request.getParameter("System");
		String query = request.getParameter("query");

		/**
		 * @author Reece and Sicong 
		 *	Dynamically name the database based on input
		 */
		dbName = project.substring(0, 1).toUpperCase() + project.substring(1);
		/*if(project.equals("epidemic")){ 
			dbName = "Epidemic";
		}else if(project.equals("energy")){
			dbName = "Energy";
		}*/
		if(type.equals("complete")){
			//request.getSession(true).removeAttribute("TimeseriesQuery", returned);
			//System.out.println("query: "+ query);
			returned = executeEpidemicMongo(query);

		}else if(type.equals("completeQ")){
			System.out.println("timeseries query: "+ query);
			returned = executeEpidemicMongo(query);
			request.getSession(true).setAttribute("TimeseriesQuery", returned);
		}
		returned = returned + "@@@" + propertyModelZone;
		//System.out.println("++++++++++++++++"+ propertyModelZone);
		response.getOutputStream().print(returned);
	}
	private String executeEpidemicMongo(String query) {
		MongoConnector ci =null;
        ci = new ConnectionImplMongoDB();
        ci.getInstance("localhost",3309); //no if-else needed they use the same port
        System.out.println("query: "+ query);
        System.out.println("Current Proj Name: " + dbName);
        Cursor resultsetMongo = ci.aggregate(query, ci.getDbFromName(dbName));
        BasicDBObject obj =null;
        BasicDBObject dataobj=null;
        if (resultsetMongo.hasNext()) {
            obj=(BasicDBObject) resultsetMongo.next();

            BasicDBList data = ((BasicDBList) obj.get("zones"));
            String m="[";
            for (int k = 0; k < data.size(); k++) {
                m=m+"{zone:\""+((BasicDBObject)data.get(k)).getString("zone")+"\", dps:";
                String dpsloc = ((BasicDBObject)data.get(k)).get("dps").toString().replace("{", "").replace("}", "").replace("[", "{").replace("]", "}").replace(":", "\":\"");
                if(k<data.size()-1){
                    m=m+dpsloc+"},";
                }else{
                    m=m+dpsloc+"}";
                }
            }
            dataobj= new BasicDBObject("Data",JSON.parse(m+"]"));
            BasicDBObject metadata = (BasicDBObject) obj.get("_id");
            dataobj.put("Meta", metadata);

        }

        //close DB Connection
        ci.closeDb();
        return dataobj.toString();//obj.toString(); The result from the query is null
	}
}
