package servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mathworks.toolbox.javabuilder.external.org.json.JSONArray;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import connections.MongoConnector;
import defaults.Constants;
import defaults.SessionVariableList;
import us.asu.emitlab.connection.timeseriesdb.ConnectionImplMongoDB;

@WebServlet("/GetSimulationByIdWithZones")
public class GetSimulationByIdWithZones extends HttpServlet {
	private static final long serialVersionUID = 1L;
	MongoConnector mongo = new ConnectionImplMongoDB();
    MongoClient mongoClient = mongo.getInstance(Constants.MONGO_LOCAL_NAME, Constants.MONGO_LOCAL_PORT);
    
    public GetSimulationByIdWithZones() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
		    HttpSession session = request.getSession(true);
		    String projectName = session.getAttribute(SessionVariableList.CURRENT_PROJECT_NAME).toString();
		    
		    String nodeID    = request.getParameter("nodeID").toString();
		    nodeID.replace("#", "");
		    String[] nodeArr = nodeID.split("_");
		    String dbName    = nodeArr[0];
		    String Model     = nodeArr[0];
		    String Metric    = nodeArr[0];
		    String SimID     = nodeArr[nodeArr.length - 1];
		    
		    String zones  = request.getParameter("zones").toString();

            String[] zoneArr = zones.split(",");
            JSONArray sim = new JSONArray();
            for (int i = 0; i < zoneArr.length; i++){
                BasicDBObject simFilter = new BasicDBObject("SimID", SimID);
                simFilter.append("Model",Model);
                simFilter.append("Metric",Metric);
                simFilter.append("zones",zoneArr[i]);
                DBCursor cursor = mongo.getDbFromName(dbName).getCollection(dbName).find(simFilter);
            }
           // List zones = mongo.getDbFromName(dbName).getCollection(Constants.TS_DB_NAME).distinct("zone", propertyFilter);
		    
		    //System.out.println("Proj Name - " + projectName);
		} catch (Exception e){
		    e.printStackTrace();
		}
	}

}
