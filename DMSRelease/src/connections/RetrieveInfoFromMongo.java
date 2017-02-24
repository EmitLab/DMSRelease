package connections;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import us.asu.emitlab.connection.timeseriesdb.ConnectionImplMongoDB;
import us.asu.emitlab.datastructure.JsonList;

/**
 * @author Sicong
 * This a light weight information retrieval class used to pull zone/metric information from MongoDB
 * */
public class RetrieveInfoFromMongo {
	/**
	 * These 4 build list functions return a list of each of the 
	 * 4 parameters: SimId, Model, Metric, and Zones; These are
	 * to be called in the query builder function.
	 * */
	public static List buildZoneList(String dbName, String collectionName, MongoConnector ci)
	{
		ci.getInstance("localhost",3309);
		List zoneList = (List) ci.getDbFromName(dbName).getCollection(collectionName).distinct("zone");
		return zoneList;

	}
	public static List buildSplitZoneList(String dbName, String collectionName, String modelFilter, MongoConnector ci)
	{
		ci.getInstance("localhost",3309);
		BasicDBObject listFilter = new BasicDBObject("Model", modelFilter);
		List zoneList = (List) (ci.getDbFromName(dbName).getCollection(collectionName).distinct("zone", listFilter));
		return zoneList;

	}
	private static List buildMetricList(String dbName, String collectionName, String modelFilter, MongoConnector ci)
	{
		ci.getInstance("localhost",3309);
		BasicDBObject listFilter = new BasicDBObject("Model", modelFilter);
		List metricList = (List) ci.getDbFromName(dbName).getCollection(collectionName).distinct("Metric", listFilter);
		return metricList;
	}
	private static List buildModelList(String dbName, String collectionName, MongoConnector ci)
	{
		ci.getInstance("localhost",3309);
		List modelList = (List) ci.getDbFromName(dbName).getCollection(collectionName).distinct("Model");
		return modelList;
	}
	private static List buildSimIdList(String dbName, String collectionName, String modelFilter, MongoConnector ci)
	{
		ci.getInstance("localhost",3309);
		BasicDBObject listFilter = new BasicDBObject("Model", modelFilter);
		List simIdList = (List) ci.getDbFromName(dbName).getCollection(collectionName).distinct("SimID", listFilter);
		return simIdList;
	}
	private static JsonList extractDPS(String dbName, String query, MongoConnector ci) throws JSONException{
		JsonList result = new JsonList();
		int index=0;    
		//long timestart = System.currentTimeMillis();
		Cursor resultsetMongo = ci.aggregate(query, ci.getDbFromName(dbName));//Cursor resultsetMongo = ci.aggregate(mongoquery.get(i).toString(), ci.getDbFromName("TimeSeriesDB"));
		while(resultsetMongo.hasNext()){
			DBObject dataobj=null;
			BasicDBObject obj =(BasicDBObject) resultsetMongo.next();
			BasicDBObject metadata = (BasicDBObject) obj.get("_id");
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
			locts = System.currentTimeMillis();
			dataobj= new BasicDBObject("Data",JSON.parse(m+"]"));

			result.add(new JSONObject(dataobj.toString()));
		}
		return result;
	}
	
	/**
	 * @param args
	 * @throws JSONException
	 */
	public static void main(String[] args) throws JSONException {
		MongoConnector ci =null;
		ci = new ConnectionImplMongoDB();
		String project = "Energy";
		String collection = "Energy";

		/**
		 * @author Reece and Sicong 
		 * No need for if-else statement, both use the same port
		 */
		ci.getInstance("localhost",3309);
		List modelList = buildModelList(project, collection, ci);
		
		for(int i = 0; i < modelList.size(); i++){
			String model = modelList.get(i).toString();
			List metricList = buildMetricList(project, collection, model, ci);
			List simIDList = buildSimIdList(project, collection, model, ci);
			for (int j = 0; j < metricList.size(); j++){
				String metric = metricList.get(j).toString();
				for(int n = 0; n < simIDList.size(); n++){
					String simID = simIDList.get(n).toString();
					String query = "Ts=,Te=,Model="+model+",SimID="+simID+",Metric="+metric+
							",Zones=,DS=,CN="+project;
					System.out.println("Reece and Sicong Query: " + query);
					// send query to MongoDB to retrieve data
					JsonList result = new JsonList();
					result = extractDPS(project, query, ci);
					System.out.println(result.toString());
					System.out.println("Test Done");
					// test retrieved result

				}
			}
		}
		ci.closeDb(); // close DB connection
	}
}
