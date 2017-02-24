package ensembles;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
// import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import connections.MongoConnector;
import defaults.Constants;
//import jdk.nashorn.internal.parser.JSONParser;
import us.asu.emitlab.connection.timeseriesdb.ConnectionImplMongoDB;

@WebServlet("/GetModelList")
public class GetModelList extends HttpServlet {
	private static final long serialVersionUID = 1L;
	MongoConnector mongo = new ConnectionImplMongoDB();
    MongoClient mongoClient = mongo.getInstance(Constants.MONGO_LOCAL_NAME, Constants.MONGO_LOCAL_PORT);
    
    public GetModelList() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		    JSONObject jData = new JSONObject(request.getParameter("jData"));
            int eid = jData.getInt("eid");
            String dbName = Integer.toString(eid);
            //@SuppressWarnings("deprecation")
            // DB db = mongoClient.getDB(dbName); 
            
            List models = mongo.getDbFromName(dbName).getCollection(Constants.TS_DB_NAME).distinct("Model");
            
            //String json = new Gson().toJson(models);
            JSONArray jArray = new JSONArray();
            
            for (int i = 0; i < models.size(); i++){
                jArray.put(models.get(i).toString());
            }
            
            PrintWriter writer = response.getWriter();
            writer.println(jArray);
            writer.flush();
            
		} catch (Exception e){
		    e.printStackTrace();
		}
	}

}
