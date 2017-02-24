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

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

import connections.MongoConnector;
import defaults.Constants;
import us.asu.emitlab.connection.timeseriesdb.ConnectionImplMongoDB;

@WebServlet("/GetPropertyList")
public class GetPropertyList extends HttpServlet {
	private static final long serialVersionUID = 1L;
	MongoConnector mongo = new ConnectionImplMongoDB();
    MongoClient mongoClient = mongo.getInstance(Constants.MONGO_LOCAL_NAME, Constants.MONGO_LOCAL_PORT);
    
    public GetPropertyList() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    try {
            JSONObject jData = new JSONObject(request.getParameter("jData"));
            
            String model = jData.getString("model");
            
            int eid = jData.getInt("eid");
            String dbName = Integer.toString(eid); 
            
            BasicDBObject modelFilter = new BasicDBObject("Model", model);
            List properties = mongo.getDbFromName(dbName).getCollection(Constants.TS_DB_NAME).distinct("Metric", modelFilter);
            
            //String json = new Gson().toJson(models);
            JSONArray jArray = new JSONArray();
            
            for (int i = 0; i < properties.size(); i++){
                jArray.put(properties.get(i).toString());
            }
            
            PrintWriter writer = response.getWriter();
            writer.println(jArray);
            writer.flush();
            
        } catch (Exception e){
            e.printStackTrace();
        }
	}

}
