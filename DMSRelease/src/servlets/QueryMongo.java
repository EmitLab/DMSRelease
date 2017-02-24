package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import connections.*;
import defaults.Constants;
import defaults.SessionVariableList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import us.asu.emitlab.clustering.comparator.MetadataComparator;
import us.asu.emitlab.connection.timeseriesdb.ConnectionImplMongoDB;
import us.asu.emitlab.datastructure.JsonList;
import us.asu.emitlab.datastructure.ModelPosition;
import us.asu.emitlab.query.CompleteQuery;

@WebServlet("/QueryMongo")
public class QueryMongo extends HttpServlet {
    private static final long serialVersionUID = 1L;
    List <ModelPosition> modelposlist = new ArrayList<ModelPosition>();
    public QueryMongo() {
        super();
    }

    private Object getParameters(String query) {
        String queryArr[]  = query.split("#");
        String returned = "";
        for (int i = 0; i < queryArr.length; i++) {
            if(queryArr[i].contains("Where")){
                String pluto [] =  queryArr[i].split("Where");
                String topolino[] =  pluto[1].split(" ");
                for(int j = 0; j < topolino.length; j++){
                    if(!topolino[j].equalsIgnoreCase("and") || !topolino[j].equalsIgnoreCase("or")){
                        if(j != topolino.length - 1)
                            returned = returned + topolino[j] + ":";
                    }else{
                        returned = returned + topolino[j];
                    }
                }
            }
        }
        return returned;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
        	System.out.println("We are querying Mongo");
            HttpSession session = request.getSession();
            JSONObject jData = new JSONObject(request.getParameter("jData"));
            String query     = jData.getString("query"); 
            query            = query.replace("^", " #");
            query            = query.replace("\\(par\\)", "");
            String[] app     = query.split("\\(par\\)");
            query            = "";
            /*
            String[] query = request.getParameterValues("query");
            query[0]       = query[0].replace("^", " #");
            query[0]       = query[0].replace("\\(par\\)", "");
            String[] app   = query[0].split("\\(par\\)");
            query[0]       = "";
            */
            // TODO : TO make it dynamic
            String dbName = session.getAttribute(SessionVariableList.CURRENT_PROJECT_NAME).toString();

            for(int i = 0; i < app.length; i++){
                query = query + app[i];
            }

            request.setAttribute("Parameters", getParameters(query));

            CompleteQuery querycomplete = new CompleteQuery(query);
            DatabaseConnection conn     = null;
            JsonList rs = null;
            String collectionName = ((query.split("#")[0]).split("'"))[1];
            
            String project = session.getAttribute(SessionVariableList.CURRENT_PROJECT_NAME).toString().toLowerCase();
            int uid        = Integer.parseInt(session.getAttribute(SessionVariableList.USER_ID).toString());
            int pid        = Integer.parseInt(session.getAttribute(SessionVariableList.CURRENT_PROJECT_ID).toString());
            
            MySqlConnector mysql = new MySqlConnector();
            mysql.getConnection();
            
            String queryDBID = "SELECT id FROM ensembles WHERE pid = " + pid + " AND name = '" + dbName + "' AND id in ( SELECT eid FROM ensembleuser WHERE uid = " + uid + ")";
            ResultSet resultSet = mysql.executeQuery(queryDBID);
            int dbid;
            if (resultSet.next()){
                dbid = resultSet.getInt("id");
            } else {
                dbid = -1; // Flag to check no db exists
            }
            
            mysql.closeConnection();

            MongoConnector ci = null;
            conn = new BaseXConnector("admin", "admin", "localhost", dbName, 3308);
            if(project.equals("energy")){
                rs = conn.executeQuery(querycomplete.flowr.toString1(), conn.getConnection(), collectionName);
                // collectionName = ((query.split("#")[0]).split("'"))[1].replace("1", "");
            }
            else if(project.equals("epidemic")){
                rs = conn.executeQuery(querycomplete.flowr.toString(), conn.getConnection(), collectionName);
            }

            JsonList result = new JsonList();
            //List<StringBuilder> mongoquery = buildMongoQuery(rs, collectionName,  querycomplete.output.getDataforOpentsdb());
            List<StringBuilder> mongoquery = buildMongoQuery(rs, collectionName,  querycomplete.output.getQueryList());
            ci = new ConnectionImplMongoDB();// new ConnectionImplMongoDB_old();
            ci.getInstance("localhost",Constants.MONGO_LOCAL_PORT);
            if(project.equals("energy")){
                Collections.sort(rs, new MetadataComparator());
                mongoquery = buildMongoQueryEnergy(rs,  collectionName, querycomplete.output.getQueryList());
                result = IntegrationEnergy(mongoquery, ci, rs); 
            } else if(project.equals("epidemic")){
                result = IntegrationEpidemic(mongoquery, ci, rs);
            }
            // request.getSession(true).setAttribute("TimeseriesQuery", result.get(0).toString());
            session.setAttribute("queryResult", result);
            session.setAttribute("On", "Metadata");
            session.setAttribute("Parameters", "transmissionRate:recoveryRate");
            session.setAttribute("CollName", collectionName);
            
            response.sendRedirect("/DMS/Clustering");
            
            //Clustering clustering = new Clustering();
            //clustering.doPost(request, response);
            
            /*
            DataClustering dc = new DataClustering();
            JSONObject jObject = dc.clustering(request, response);
            PrintWriter writer = response.getWriter();
            writer.println();
            writer.flush();
            */
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private JsonList IntegrationEnergy(List<StringBuilder> mongoquery , MongoConnector ci, JsonList rs ){
        JsonList result = new JsonList();
        int index=0;    
        //long timestart = System.currentTimeMillis();
        Cursor resultsetMongo = ci.aggregate(mongoquery, ci.getDbFromName("Energy"));//Cursor resultsetMongo = ci.aggregate(mongoquery.get(i).toString(), ci.getDbFromName("TimeSeriesDB"));
        //  System.out.println("Time for Cursor: "+ (System.currentTimeMillis()-timestart));
        for(int i=0;i<mongoquery.size();i++){
            int indexintegration = modelposlist.get(i).getSpos();

            //      timestart = System.currentTimeMillis();
            long timedps =0;
            long timeJson=0;
            while(resultsetMongo.hasNext()){
                DBObject dataobj=null;
                BasicDBObject obj =(BasicDBObject) resultsetMongo.next();
                BasicDBObject metadata = (BasicDBObject) obj.get("_id");
                JSONObject actual = rs.get(indexintegration);

                try {
                    if(metadata.getString("SimID").equals(""+actual.get("simid")) && metadata.getString("Model").equals(actual.get("model"))){//id model and simidare equal add element
                        Iterator<String> keys = rs.get(i).keys();
                        while(keys.hasNext()){
                            String key = keys.next();
                            if(!key.equals("simid")&&!key.equals("model")){ //add all metadata
                                metadata.put(key, actual.get(key));
                            }
                        }
                        metadata.put("Index", index); //add index to this element
                        BasicDBList data = ((BasicDBList) obj.get("zones"));
                        String m="[";
                        long locts = System.currentTimeMillis();
                        for (int k = 0; k < data.size(); k++) { //format zone and dps
                            m=m+"{zone:\""+((BasicDBObject)data.get(k)).getString("zone")+"\", dps:";
                            String dpsloc = ((BasicDBObject)data.get(k)).get("dps").toString().replace("{", "").replace("}", "").replace("[", "{").replace("]", "}").replace(":", "\":\"");
                            if(k<data.size()-1){
                                m=m+dpsloc+"},";
                            }else{
                                m=m+dpsloc+"}";
                            }
                        }
                        timedps = timedps+(System.currentTimeMillis()-locts);

                        locts = System.currentTimeMillis();
                        dataobj= new BasicDBObject("Data",JSON.parse(m+"]"));

                        dataobj.put("Meta", metadata);
                        result.add(new JSONObject(dataobj.toString()));
                        timeJson = timeJson+(System.currentTimeMillis()-locts);
                        //  index++;
                    }else{
                        indexintegration++;
                        Iterator<String> keys = rs.get(i).keys();
                        while(keys.hasNext()){
                            String key = keys.next();
                            if(!key.equals("simid")&&!key.equals("model")){
                                metadata.put(key, actual.get(key));
                            }
                        }
                        metadata.put("Index", index);
                        BasicDBList data = ((BasicDBList) obj.get("zones"));//.toString();
                        String m="[";
                        long locts = System.currentTimeMillis();
                        for (int k = 0; k < data.size(); k++) {
                            m=m+"{zone:\""+((BasicDBObject)data.get(k)).getString("zone")+"\", dps:";
                            String dpsloc = ((BasicDBObject)data.get(k)).get("dps").toString().replace("{", "").replace("}", "").replace("[", "{").replace("]", "}").replace(":", "\":\"");
                            if(k<data.size()-1){
                                m=m+dpsloc+"},";
                            }else{
                                m=m+dpsloc+"}";
                            }
                        }
                        timedps = timedps+(System.currentTimeMillis()-locts);
                        locts = System.currentTimeMillis();
                        dataobj= new BasicDBObject("Data",JSON.parse(m+"]"));

                        dataobj.put("Meta", metadata);

                        result.add(new JSONObject(dataobj.toString()));
                        timeJson = timeJson+(System.currentTimeMillis()-locts);
                    }
                }catch(JSONException e){}
                index++;
            }
        }   
        ci.closeDb();
        return result;
    }

    private JsonList IntegrationEpidemic(List<StringBuilder> mongoquery , MongoConnector ci, JsonList rs ){
        JsonList result = new JsonList();

        int index=0;
        for(int i=0;i<mongoquery.size();i++){
            Cursor resultsetMongo = ci.aggregate(mongoquery.get(i).toString(), ci.getDbFromName("Epidemic"));
            while (resultsetMongo.hasNext()) {

                BasicDBObject obj =(BasicDBObject) resultsetMongo.next();
                BasicDBObject metadata = (BasicDBObject) obj.get("_id");
                DBObject dataobj=null;
                boolean integrated=false;

                for(int j=0 ; (j<rs.size()) && (!integrated);j++ ){
                    try {
                        JSONObject actual = rs.get(j);
                        if(metadata.getString("SimID").equals(""+actual.get("simid")) && metadata.getString("Model").equals(actual.get("model"))){

                            Iterator<String> keys = rs.get(i).keys();
                            while(keys.hasNext()){
                                String key = keys.next();
                                if(!key.equals("simid")&&!key.equals("model")){
                                    metadata.put(key, actual.get(key));
                                }
                            }


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
                            dataobj.put("Meta", metadata);
                            integrated=true;
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }
                try {
                    ((BasicDBObject)dataobj.get("Meta")).put("Index", index++);

                    result.add(new JSONObject(dataobj.toString()));

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
        ci.closeDb();//Silv added this to close the connection on mongodb
        return result;
    }
    private int modelinList(List<String> modelInput,String model) {
        // TODO Auto-generated method stub
        int idx= -1;
        boolean esito=false;
        for (int i = 0; (i < modelInput.size())&&(!esito); i++) {
            if(modelInput.get(i).equals(model)){
                esito =true;
                idx=i;
            }
        }
        return idx;
    }

    JsonList functionToTryFlow(JsonList list){
        JsonList p= new JsonList();
        for (int i = 0; i < list.size(); i++) {
            try {
                if(i<list.size()-1)
                    p.add(new JSONObject("{\"Metadata\":"+list.get(i).toString()+"},"));
                else
                    p.add(new JSONObject("{\"Metadata\":"+list.get(i).toString()+"}"));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }//list.get(i).toString());
        }
        return p;
    }

    private List<StringBuilder> buildMongoQuery(JsonList rs, String coll,String input) {
        // TODO Auto-generated method stub
        //Ts=MM/dd/yyyy HH:mm:ss,Te=MM/dd/yyyy HH:mm:ss,Model=SIR,SimID=1|2|3,Metric=Deaths|Incidence,Zones=AZ|CA|NV,CN=Epidemic
        //System.out.println("in mongoqueryBuilder");

        List<StringBuilder> q =new ArrayList<StringBuilder>();
        List <String> modelList =  new ArrayList<String>();
        String start = "Ts=";//01/01/2012 12:00:00

        String end = "Te=";//07/31/2012 12:00:00
        String DS = "DS=";
        String stateInput="";
        String metricInput ="";
        //String modelInput[] = null;
        String[] tempInput = input.trim().split(";");
        //System.out.println("Input: " +input);
        for(int i=0; i<tempInput.length; i++){

            String[] temp = tempInput[i].split("=");
            //  System.out.println(temp[0]+" : "+ temp[1]);
            //System.out.println("TEmoo[0]: "+temp[0]);
            if(temp[0].equalsIgnoreCase("state")||temp[0].equalsIgnoreCase("zone")){
                stateInput = (temp[1].substring(1, temp[1].length()-1)).replace(",", "|");
                if(stateInput.equals("*")){
                    //      System.out.println("state all");
                    stateInput="";
                }
            }
            if(temp[0].equalsIgnoreCase("measure")||temp[0].equalsIgnoreCase("properties")){//"metrics")){
                metricInput = (temp[1].substring(1, temp[1].length()-1)).replace(",", "|");
                //  System.out.println("MetricInput: "+ metricInput);
                if(metricInput.equals("*")){
                    metricInput="";
                }
            }
            if(temp[0].contains("From")){
                //  System.out.println("from:"+temp[1].substring(1, temp[1].length()-1).equals(""));
                if(!temp[1].substring(1, temp[1].length()-1).equals(""))
                    start = start+temp[1].substring(1, temp[1].length()-1);
            }
            if(temp[0].equals("To")){
                //  System.out.println("to:"+temp[1].substring(1, temp[1].length()-1).equals(""));
                if(!temp[1].substring(1, temp[1].length()-1).equals(""))
                    end = end+temp[1].substring(1, temp[1].length()-1);
            }   
            if(temp[0].equals("By")){
                //  System.out.println("by:"+temp[1].substring(1, temp[1].length()-1).equals(""));
                if(!temp[1].substring(1, temp[1].length()-1).equals(""))
                    DS = DS+(temp[1].substring(1, temp[1].length()-1));
            }
            if(temp[0].equals("function")){
                //  System.out.println("function:"+temp[1].substring(1, temp[1].length()-1).equals(""));
                if(!temp[1].substring(1, temp[1].length()-1).equals(""))
                    DS = DS+"-"+(temp[1].substring(1, temp[1].length()-1));
            }


            /*  if(temp[0].equals("model")){
            String mod= (temp[1].substring(1, temp[1].length()-1));
            if (!mod.equals("*")) {
                    modelInput = mod.split(",");
                }
            }*/
        }
        List<String>  simIDs = new ArrayList<String>();
        try {
            for(int i =0;i<rs.size();i++){
                String actualmodel=null;

                actualmodel = rs.get(i).get("model").toString();

                int idx= modelinList(modelList, actualmodel);

                if(idx!=-1){
                    simIDs.set(idx,  simIDs.get(idx)+"|"+rs.get(i).get("simid").toString());
                }else{
                    modelList.add(actualmodel);
                    simIDs.add(rs.get(i).get("simid").toString());
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for(int i=0;i<modelList.size();i++){
            q.add(new StringBuilder());
            q.get(i).append(start);
            q.get(i).append(","+end);
            q.get(i).append(",Model="+modelList.get(i));
            q.get(i).append(",SimID="+simIDs.get(i));
            q.get(i).append(",Metric="+metricInput);
            q.get(i).append(",Zones="+stateInput);
            q.get(i).append(","+DS);
            q.get(i).append(",CN="+coll);
        }
        return q;
    }

    private List<StringBuilder> buildMongoQueryEnergy(JsonList rs, String coll,String input){
        List<StringBuilder> q =new ArrayList<StringBuilder>();
        List <String> modelList =  new ArrayList<String>();
        String start = "Ts=";//01/01/2012 12:00:00

        String end = "Te=";//07/31/2012 12:00:00
        String DS = "DS=";
        String stateInput="";
        String metricInput ="";
        //String modelInput[] = null;
        String[] tempInput = input.trim().split(";");
        //System.out.println("Input: " +input.replace(" ",""));
        for(int i=0; i<tempInput.length; i++){
            String[] temp = tempInput[i].split("=");
            //System.out.println("TEmoo[0]: "+temp[0]);
            if(temp[0].equalsIgnoreCase("zones")||temp[0].equalsIgnoreCase("state")){
                stateInput = (temp[1].substring(1, temp[1].length()-1)).replace(",", "|").replace("_", "-");
                if(stateInput.equals("*")){
                    //      System.out.println("state all");
                    stateInput="";
                }
            }
            if(temp[0].equalsIgnoreCase("measure")||temp[0].equalsIgnoreCase("properties")){//"metrics")){
                metricInput = (temp[1].substring(1, temp[1].length()-1)).replace(",", "|");
                if(metricInput.equals("*")){
                    metricInput="";
                }
            }
            if(temp[0].contains("From")){
                //  System.out.println("from:"+temp[1].substring(1, temp[1].length()-1).equals(""));
                if(!temp[1].substring(1, temp[1].length()-1).equals(""))
                    start = start+temp[1].substring(1, temp[1].length()-1);
            }
            if(temp[0].contains("To")){
                //  System.out.println("to:"+temp[1].substring(1, temp[1].length()-1).equals(""));
                if(!temp[1].substring(1, temp[1].length()-1).equals(""))
                    end = end+temp[1].substring(1, temp[1].length()-1);
            }   
            if(temp[0].contains("By")){
                //  System.out.println("by:"+temp[1].substring(1, temp[1].length()-1).equals(""));
                System.out.println("out from if: "+ temp[1].substring(1, temp[1].length()-1));
                if(!temp[1].substring(1, temp[1].length()-1).equals("")){
                    //  System.out.println("day: "+ temp[1].substring(1, temp[1].length()-1));  
                    DS = DS+(temp[1].substring(1, temp[1].length()-1));
                }
            }
            if(temp[0].equals("function")){
                //  System.out.println("function:"+temp[1].substring(1, temp[1].length()-1).equals(""));
                if(!temp[1].substring(1, temp[1].length()-1).equals(""))
                    DS = DS+"-"+(temp[1].substring(1, temp[1].length()-1));
            }
        }
        List<String>  simIDs = new ArrayList<String>();
        List<String>  zoneList = new ArrayList<String>(); 
        try {
            boolean newmodel = true;

            for(int i = 0;i<rs.size();i++){
                String actualmodel=null;
                actualmodel = rs.get(i).get("model").toString();

                int idx= modelinList(modelList, actualmodel);

                if(idx!=-1){
                    //  System.out.println("Same model");
                    simIDs.set(idx,  simIDs.get(idx)+"|"+rs.get(i).get("simid").toString());
                }else{
                    //  System.out.println("new Model: "+ actualmodel+" - "+i );
                    modelList.add(actualmodel);
                    modelposlist.add(new ModelPosition(actualmodel, i));
                    simIDs.add(rs.get(i).get("simid").toString());
                    if(rs.get(i).has("zones")){
                        String zome = rs.get(i).getString("zones").toUpperCase();
                        if(zome.contains(" ")){
                            zome=zome.replace(" ", "-");
                        }
                        if(zome.contains("_")){
                            zome=zome.replace("_", "-");
                        }
                        zoneList.add(zome);//.replace("-", ""));
                    }
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for(int i=0;i<modelList.size();i++){
            q.add(new StringBuilder());
            q.get(i).append(start);
            q.get(i).append(","+end);
            q.get(i).append(",Model="+modelList.get(i));
            q.get(i).append(",SimID="+simIDs.get(i));
            q.get(i).append(",Metric="+metricInput);
            q.get(i).append(",Zones="+stateInput);//zoneList.get(i));//stateInput);
            //q.get(i).append(",Zones="+zoneList.get(i));//stateInput);
            q.get(i).append(","+DS);
            q.get(i).append(",CN="+coll);
        }
        return q;
    }
    private void goToPage(String address, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        //getNamedDispatcher
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(address);
        //System.out.println(dispatcher.toString());
        dispatcher.forward(request, response);
    }
}
