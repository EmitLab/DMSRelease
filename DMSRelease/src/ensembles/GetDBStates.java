package ensembles;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import connections.MongoConnector;
import connections.MySqlConnector;
import defaults.Constants;
import defaults.SessionVariableList;
import us.asu.emitlab.connection.timeseriesdb.ConnectionImplMongoDB;

@WebServlet("/GetDBStates")
public class GetDBStates extends HttpServlet {
	private static final long serialVersionUID = 1L;
	MongoConnector mongo = new ConnectionImplMongoDB();
    MongoClient mongoClient = mongo.getInstance(Constants.MONGO_LOCAL_NAME, Constants.MONGO_LOCAL_PORT);
    
    public GetDBStates() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		    HttpSession session = request.getSession();
		    int pid             = Integer.parseInt(session.getAttribute(SessionVariableList.CURRENT_PROJECT_ID).toString());
            int uid             = Integer.parseInt(session.getAttribute(SessionVariableList.USER_ID).toString());
            
            String query = "SELECT id, name FROM ensembles WHERE pid = " + pid + 
                            " AND id IN (SELECT eid FROM ensembleuser WHERE uid = " + uid + ")";
            
            MySqlConnector mysql = new MySqlConnector();
            mysql.getConnection();
            ResultSet rs = mysql.executeQuery(query);
            
            JSONArray jArray = new JSONArray();
            JSONObject jObject;
            if (rs.next()){
                do {
                    @SuppressWarnings("deprecation")
                    DB db = mongoClient.getDB(rs.getString("id"));
                    CommandResult cr = db.getStats();
                    jObject = new JSONObject(cr.toString());
                    jObject.put("dbname", rs.getString("name"));
                    jArray.put(jObject);
                } while (rs.next());
            } else {
                // Do Nothing
            }        
            mysql.closeConnection();
            PrintWriter writer = response.getWriter();
            writer.println(jArray);
            writer.flush();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}

}
