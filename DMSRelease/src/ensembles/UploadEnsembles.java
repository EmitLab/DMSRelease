package ensembles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import connections.MongoConnector;
import defaults.Constants;
import us.asu.emitlab.connection.timeseriesdb.*;

@WebServlet("/UploadEnsembles")
public class UploadEnsembles extends HttpServlet {
    private static final long serialVersionUID = 1L;
    MongoConnector mongo = new ConnectionImplMongoDB();
    MongoClient mongoClient = mongo.getInstance(Constants.MONGO_LOCAL_NAME, Constants.MONGO_SERVER_PORT);

    DB db; 

    public UploadEnsembles() {
        super();
    }

    public static String changeDateFormat(String string) {

        if(string.contains("-")){
            String[]     data = string.split(" ");
            String[] datapart = data[0].split("-");
            string = datapart[1] + "/" + datapart[2] + "/" + datapart[0] + " " + data[1];
        }
        return string;
    }

    @SuppressWarnings("deprecation")
    private void insertFileToMongo(List<String[]> file, String dbName, String simid, String model, String metric){
        //System.out.println(dbName);
        DBObject dbobj = null;

        for(int i = 2 ; i < file.get(0).length; i++){
            dbobj = new BasicDBObject();

            dbobj.put(Constants.MONGO_SIMID, simid);         // Simulation ID
            dbobj.put(Constants.MONGO_METRIC, metric);       // Metric
            dbobj.put(Constants.MONGO_MODEL, model);         // Model
            dbobj.put(Constants.MONGO_ZONE, file.get(0)[i]); // Zone

            BasicDBList dpsobj = new BasicDBList();    // Holds Date-Value Pair(s)
            for(int j = 0; j < file.size(); j++){
                if(j == 0){ 
                    //Ignore Header Line
                } else {
                    DBObject dateobj = new BasicDBObject(); // Create Date-value Pair
                    // dateobj.put(Constants.MONGO_DATE, new Date(changeDateFormat(file.get(j)[1])) );
                    // dateobj.put(Constants.MONGO_VALUE, new BasicDBObject("" + new Date(changeDateFormat(file.get(j)[1])).getTime(), file.get(j)[i]));
                    dateobj.put(Constants.MONGO_DATE, new Date(changeDateFormat(file.get(j)[1])).getTime());
                    dateobj.put(Constants.MONGO_VALUE, file.get(j)[i]);
                    dpsobj.add(dateobj);
                }   
            }
            dbobj.put(Constants.MONGO_DATA, dpsobj);
            mongo.insetIntoCollection(db, Constants.TS_DB_NAME, dbobj);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            JSONObject jData = new JSONObject(request.getParameter("jData"));
            
            String dbName  = jData.getString("dbName");
            String metric  = jData.getString("metric");
            String simId   = jData.getString("simName");
            String content = jData.getString("content");
            String model   = jData.getString("model");

            System.out.println("DB Name : " + dbName);
            
            db = mongo.getDbFromName(dbName);

            String[] lines = content.split("\\r?\\n");
            List<String[]> list = new ArrayList<String[]>();

            // Number of Rows in CSV File (1 Header and time-stamps)
            int nLines = lines.length;

            for (int line = 0; line < nLines; line++){
                if (lines[line].length() != 0){
                    list.add(lines[line].split(","));
                }
            }

            if (list.size() == 0){
                System.err.println("File Empty");
            } else {
                insertFileToMongo(list, dbName, simId, model, metric);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

}
