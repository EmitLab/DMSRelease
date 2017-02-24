package servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DBCursor;
import com.mongodb.util.JSON;

import connections.MongoConnector;
import us.asu.emitlab.connection.timeseriesdb.ConnectionImplMongoDB;

@WebServlet("/RetrieveDataFromMongo")
public class RetrieveDataFromMongo extends HttpServlet {
    private static final long serialVersionUID = 1L;
    String project = "";
    
    public RetrieveDataFromMongo() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String returned ="";
        String propertyModelZone = "";
        String type ="";
        type=type+request.getParameter("type");
        project = request.getParameter("project");
        String query = request.getParameter("query");
        /**
         * @author Reece and Sicong 
         *  Dynamically name the database based on input
         */
        String dbName = "";
        dbName = project.substring(0, 1).toUpperCase() + project.substring(1);
        System.out.println("testing type: "  + type);
        if(type.equals("complete")){
            returned = executeEpidemicMongo(query);
        }else if(type.equals("completeQ")){
            System.out.println("timeseries query: "+ query);
            returned = executeEpidemicMongo(query);
            request.getSession(true).setAttribute("TimeseriesQuery", returned);
        }else if(type.equals("Property")){
            /**
             * @author Sicong
             * Query for model and properties, for front end similar search options
             * */
            String model = request.getParameter("myModel");
            propertyModelZone = executePropertyQueryMongo(model, dbName); 
        }else if(type.equals("ModelZone")){
            /**
             * @author Sicong
             * Query for collection and zones, for front end similar search options
             * */
            propertyModelZone = executeModelZoneQueryMongo(dbName); // get model list and zone list from certain collection
        }else
            // returned = executeEnergyMongo(query);
            returned = executeEpidemicMongo(query);
        returned = returned + "@@@" + propertyModelZone;
        response.getOutputStream().print(returned);
    }
    private String executeEnergyMongo(String query) {
        MongoConnector ci =null;
        ci = new ConnectionImplMongoDB();
        ci.getInstance("localhost",3309);  //No if-else needed, all projects will use same port
        Cursor resultsetMongo = ci.aggregate(query, ci.getDbFromName("Energy"));
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
        }
        ci.closeDb();
        return dataobj.toString();
    }
    private String executeModelZoneQueryMongo(String myDB) {
        String resultString = "";
        MongoConnector ci =null;
        ci = new ConnectionImplMongoDB();
        ci.getInstance("localhost",3309);
        myDB = project.substring(0, 1).toUpperCase() + project.substring(1);    //Dynamically gets DB name and uses the same port. No if-else needed
        List modelNameResult = ci.getDbFromName(myDB).getCollection(myDB).distinct("Model"); // return the array
        BasicDBObject tempModelName = null;
        for(int i = 0; i < modelNameResult.size(); i++){
            resultString += modelNameResult.get(i).toString()+",";
        }
        resultString += "#";
        List zoneResult = ci.getDbFromName(myDB).getCollection(myDB).distinct("zone"); // return the array
        BasicDBObject objZones = null;

        for (int i = 0; i < zoneResult.size(); i++) {
            resultString  = resultString + zoneResult.get(i).toString() + "#";
        }
        ci.closeDb(); // close DB connection
        return resultString; // return all models and zones from current Collection
    }

    private String executePropertyQueryMongo(String model, String myDB) {
        String resultString = "";
        MongoConnector ci =null;
        ci = new ConnectionImplMongoDB();
        /**
         * @author Reece and Sicong 
         * No need for if-else statement, both use the same port
         */
        ci.getInstance("localhost",3309);
        System.out.println("In Function executePropertyQueryMongo, quering model: " + model);

        BasicDBObject qobj= new BasicDBObject("name",model);
        DBCursor modelResult = ci.getDbFromName(myDB).getCollection("Model").find(qobj); // return the array
        /**
         * @author Reece and Sicong 
         * Use distinct method to get unique properties
         * @Todo Query models in order to get Model dependent metric list
         */
        BasicDBObject myFilter = new BasicDBObject("Model", model);
        List list = ci.getDbFromName(myDB).getCollection(myDB).distinct("Metric", myFilter); //Changed "Epidemic" to the dynamic myDB
        for(int i = 0; i < list.size(); i++)
        {
            if(i == 0)
            {
                resultString = (String) list.get(i);
            }
            else
            {
                resultString = resultString + "@@" + list.get(i);
            }
        }
        System.out.println("Property resultString: " + resultString);
        ci.closeDb(); // close DB connection
        return resultString;

    }
    private String executeEpidemicMongo(String query) {
        MongoConnector ci =null;
        ci = new ConnectionImplMongoDB();
        ci.getInstance("localhost",3309); //no if-else needed they use the same port
        System.out.println("query: "+ query);
        Cursor resultsetMongo = ci.aggregate(query, ci.getDbFromName("Epidemic"));
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
