package us.asu.emitlab.connection.timeseriesdb;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import com.mongodb.AggregationOptions;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import connections.MongoConnector;
import defaults.Constants;

public class ConnectionImplMongoDB implements MongoConnector{
	MongoClient mongoInstance = null;

	public MongoClient getInstance(String address, int port) {
		if(this.mongoInstance == null){
			if(address == null){
				address = Constants.MONGO_LOCAL_NAME;
			}
			if(port == 0){
				port = Constants.MONGO_LOCAL_PORT;
			}
			this.mongoInstance = new MongoClient(address, port);
		}
		return mongoInstance;
	}

	/**
	 * Get database. If the database doesn't exist, MongoDB will create it for you.
	 * @author Silvestro Roberto-Poccia
	 */
	public DB getDbFromName(String namedb) {
	    
		DB db = this.mongoInstance.getDB(namedb);
		return db;
	}

	public List<String> getListDbName() {
		List<String> dbs = mongoInstance.getDatabaseNames();
		return dbs;
	}

	/**
	 * View all the database on the specified server
	 * @author Silvestro Roberto-Poccia
	 */
	public void visualizzaListaDb(List<String> dbs) {
		for(String db : dbs){
			System.out.println(db);
		}

	}

	public boolean closeDb() {
		mongoInstance.close();
		return true;
	}

	/**
	 * Get a Collection from the selected database of the specified name
	 * @author Silvestro Roberto-Poccia
	 * */
	public DBCollection getCollection(DB database,String nameCollection) {
		DBCollection mongocollection= database.getCollection(nameCollection);
		return mongocollection;
	}

	/**Display all collections from selected database.
	 * @author Silvestro Roberto-Poccia
	 * */
	public void displayAllCollection(DB database){

		Set<String> tables = database.getCollectionNames();

		for(String coll : tables){
			System.out.println(coll);
		}

	}

	/**
	 * @input: DB instance; Name of the collection; DBObject The document in a DBObject Format
	 * 
	 * If the name of the collection does not exist a new collection will be created.
	 * The mongoDBObject  can contains the _id if it is not specified it is assigned one by the system.
	 * In the inserted document if we don't specify the _id parameter, then MongoDB assigns an unique ObjectId for this document.
	 * _id is 12 bytes hexadecimal number unique for every document in a collection. 12 bytes are divided as follows:
	 * _id: ObjectId(4 bytes timestamp, 3 bytes machine id, 2 bytes process id, 3 bytes incrementer)
	 * 
	 * @author silvestro
	 * */
	public void insetIntoCollection(DB database, String nomeCollection, DBObject dbObject) {
		DBCollection table = database.getCollection(nomeCollection);
		table.insert(dbObject);
	}

	/**
	 * This Method provide the query from a string format creating a Document-Object for the Query.
	 * */
	@Override
	public DBCursor find(String query,DB database) {
		String [] querypart = query.split(",");
		String model = querypart[2].split("=")[1];
		String simIds= querypart[3];
		String metric = querypart[4];
		String zones = querypart[5];
		String coll = querypart[6];
		DBCursor cursor=null;
		//System.out.println("in the find");
		if(coll.split("=").length == 1){
			System.err.println("Please Insert at least one Valid Colletion");  
		}else{
			DBObject searchQuery = new BasicDBObject();
			searchQuery.put("Model", model);

			if(simIds.split("=").length>1){
				if((simIds.split("=")[1]).split("\\|").length !=0){
					searchQuery.put("SimID", new BasicDBObject("$in",combineTagValue(simIds)));
				}		
			}

			if(metric.split("=").length>1){
				if((metric.split("=")[1]).split("\\|").length !=0){
					searchQuery.put("Metric", new BasicDBObject("$in",combineTagValue(metric)));
				}
			}
			
			if(zones.split("=").length>1){
				if((zones.split("=")[1]).split("\\|").length !=0){
					searchQuery.put("zone", new BasicDBObject("$in",combineTagValue(zones)));
				}
			}

			System.out.println("QueryBulded: "+ searchQuery.toString());
			DBCollection table = database.getCollection(model);

			long ts = System.currentTimeMillis();
			cursor = table.find(searchQuery);

			ts= System.currentTimeMillis();
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				//System.out.println(obj.toString());
			}
		}
		return cursor;
	}

	public Cursor aggregate (List<StringBuilder> querys,DB database){
		boolean setTime = false;
		AggregationOutput output = null;
		Cursor aggregateOutputoutput = null;

		long offset = (long )TimeZone.getDefault().getRawOffset();
		String ts     		= null;
		String te     		= null;
		String coll   		= null;
		String model  		= null;
		String simIds 		= null;
		String metric 		= null;
		String zones        = null;
		String downsampling = null;

		DBObject match  	  = new BasicDBObject();// query
		DBObject unwind 	  = null;
		DBObject match1 	  = new BasicDBObject();//metch the DPS element of interest
		DBObject group1		  = new BasicDBObject();// groupby zone
		DBObject group  	  = new BasicDBObject(); //group the DPS
		DBObject down   	  = null;
		BasicDBObject Dateobj = new BasicDBObject();

		String [] querypart = querys.get(0).toString().split(",");
		coll = querypart[7];
		if(querypart[0].split("=").length != 1){//Time Start
			ts = querypart[0].split("=")[1];
		}
		
		if(querypart[1].split("=").length != 1){
			te= querypart[1].split("=")[1];
		}

		if(coll.split("=").length == 1){
			System.err.println("Please Insert at least one Valid Colletion");  
		} else {
			coll = coll.split("=")[1];
			BasicDBList OR = new BasicDBList();	
			downsampling = querypart[6];

			if(ts != null){
				Dateobj.put("$gte", new Date(DocumentReader_Converter.changeDateFormat(ts)).getTime()+offset);
				setTime = true;
			}

			if(te != null){
				Dateobj.put("$lte",new Date(DocumentReader_Converter.changeDateFormat(te)).getTime()+offset);
				setTime = true;
			}

			if(setTime){
				BasicDBObject elemmatch = new BasicDBObject();
				elemmatch.put("Date", Dateobj);

				BasicDBObject o = new BasicDBObject();
				o.put("$elemMatch", elemmatch);
				match.put("dps", o);

				//set the match on time
				elemmatch = new BasicDBObject();
				elemmatch.put("dps.Date", Dateobj);
				match1.put("$match", elemmatch);	
			}

			String matchquery = "[";
			for(int i = 0; i < querys.size(); i++){

				querypart = querys.get(i).toString().split(",");
				model = querypart[2];
				simIds = querypart[3];
				metric = querypart[4];
				zones = querypart[5];
				List<DBObject> localquery =  new ArrayList<DBObject>();//new BasicDBObject();
				localquery.add(new BasicDBObject());
				int idx = localquery.size()-1;
				localquery.set(idx, match); 
				if(model.split("=").length>1){
					if((model.split("=")[1]).split("\\|").length != 0){
						localquery.get(idx).put("Model", new BasicDBObject("$in",combineTagValue(model)));
					}		
				}
				if(simIds.split("=").length>1){
					if((simIds.split("=")[1]).split("\\|").length != 0){
						localquery.get(idx).put("SimID", new BasicDBObject("$in",combineTagValue(simIds)));
					}		
				}
				if(metric.split("=").length>1){
					if((metric.split("=")[1]).split("\\|").length != 0){
						localquery.get(idx).put("Metric", new BasicDBObject("$in",combineTagValue(metric)));
					}
				}
				if(zones.split("=").length>1){
					if((zones.split("=")[1]).split("\\|").length != 0){
						localquery.get(idx).put("zone", new BasicDBObject("$in",combineTagValue(zones)));
					}
				}
				System.out.println("i: " + i + "  " + localquery.get(idx).toString());
				matchquery =  matchquery+localquery.get(idx).toString()+",";
			}	

			match = new BasicDBObject();
			match.put("$or", (DBObject) JSON.parse(matchquery.substring(0, matchquery.length() - 1) + "]"));
			System.out.println("OR: "+ match.toString());
			int value = 0;
			if(downsampling.split("=").length > 1){

				value = Integer.parseInt(downsampling.split("=")[1].split("-")[0]);
				String granularity = downsampling.split("=")[1].split("-")[1];
				String function =  downsampling.split("=")[1].split("-")[2];
				
				if (function.equals("")){
					function = "avg";
				}

				if(granularity.equalsIgnoreCase("s")){
					value = value*1000;
				} else if(granularity.equals("m")){
					value = value*1000*60;
				} else if(granularity.equalsIgnoreCase("h")){
					value = value*1000*60*60;
				} else if(granularity.equalsIgnoreCase("d")){
					value = value*1000*60*60*24;
				} else if(granularity.equals("M")){
					value = value*1000*60*60*24*30;
				} else if(granularity.equalsIgnoreCase("Y")){
					value = value*1000*60*60*24*30*12;
				}
				String downSampling = "{\"$group\":{\"_id\":{\"SimID\":\"$SimID\",\"Metric\":\"$Metric\",\"Model\":\"$Model\",\"zone\":\"$zone\", date:{\"$subtract\":[{\"$subtract\":[\"$dps.Date\","+new Date(01/01/1970).getTime()+"]},{\"$mod\":[{\"$subtract\":[\"$dps.Date\","+new Date(01/01/1970).getTime()+"]},"+ value  +"]}]}},\"Value\":{\"$"+function+"\":\"$dps.Value\"}}}";
				down = (DBObject) JSON.parse(downSampling);

			}
			unwind = new BasicDBObject();//select DPS
			unwind.put("$unwind", "$dps");

			String groupstring = "{$group:{_id:{SimID:\"$SimID\",Model:\"$Model\",Metric:\"$Metric\",zone:\"$zone\"},\"dps\":{\"$push\":{\"$concat\":[{\"$substr\":[\"$dps.Date\",0,-1]},\":\",{\"$substr\":[\"$dps.Value\",0,-1]}]}}}}";//dps:{$push:{\"Date\":\"$_id.date\",\"Value\":\"$Value\"}}}}";

			if(down != null){
				System.out.println("down");
				groupstring = "{$group:{_id:{SimID:\"$_id.SimID\",Model:\"$_id.Model\",Metric:\"$_id.Metric\",zone:\"$_id.zone\"},\"dps\":{\"$push\":{\"$concat\":[{\"$substr\":[\"$_id.date\",0,-1]},\":\",{\"$substr\":[\"$Value\",0,-1]}]}}}}";

				//	groupstring="{$group:{_id:{SimID:\"$_id.SimID\",Model:\"$_id.Model\",Metric:\"$_id.Metric\",zone:\"$_id.zone\"},\"dps\":{\"$push\":{d:\"$_id.date\",v:\"$Value\"}}}}";
				//    		
				//groupstring="{$group:{_id:{SimID:\"$_id.SimID\",Model:\"$_id.Model\",Metric:\"$_id.Metric\",zone:\"$_id.zone\"},\"dps\":{\"$push\":{\"$concat\":[{\"$substr\":[{\"$multiply\":[\"$_id.date\","+value/1000+"]},0,-1]},\":\",{\"$substr\":[\"$Value\",0,-1]}]}}}}";//

			}
			//String groupstring="{$group:{_id:{SimID:\"$SimID\",Model:\"$Model\",Metric:\"$Metric\",zone:\"$zone\"},dps:{$push:{$concat:[{$dateToString:{format:\"%d/%m/%Y %H:%M:%S'\" ,date:\"$dps.Date\"}},\":\",\"$dps.Value\"]}}}}";
			//String groupstring="{$group:{_id:{SimID:\"$SimID\",Model:\"$Model\",Metric:\"$Metric\",zone:\"$zone\"},dps:{$push:{{$dateToString:{format:\"%d/%m/%Y %H:%M:%S'\" ,date:\"$dps.Date\"}}:$dps.Value}}}}";

			group = (DBObject) JSON.parse(groupstring);
			groupstring = "{$group:{_id:{SimID:\"$_id.SimID\",Model:\"$_id.Model\",Metric:\"$_id.Metric\"},zones:{$addToSet:{zone:\"$_id.zone\",dps:\"$dps\"}}}}";
			//groupstring = "{$group:{_id:{SimID:\"$_id.SimID\",Model:\"$_id.Model\",Metric:\"$_id.Metric\"},{\"$push\":{zones:{\"$each\": [{zone:\"$_id.zone\",dps:\"$dps\"}],\"$sort\"{\"$_id.zone\":1}}}}}}";
			group1= (DBObject) JSON.parse(groupstring);
			DBObject sort = (DBObject) JSON.parse("{ $sort : {Model:1,Metric:1,SimID:1,zone:1,\"dps.Date\":1} }");
			// 	DBObject sort1 = (DBObject) JSON.parse("{ $sort : {_id.Model:-1,_id.Metric:-1,_id.SimID:-1} }");
			DBObject sort1 = (DBObject) JSON.parse("{ $sort : {_id.Model:1,_id.Metric:1,_id.SimID:1} }");

			DBCollection table = database.getCollection(coll);

			DBObject obj = new BasicDBObject("$match", match); 

			AggregationOptions options = AggregationOptions.builder().outputMode(AggregationOptions.OutputMode.CURSOR).allowDiskUse(true).build();

			//System.out.println("Options: "+ options.toString());

			if(setTime && down == null){
				List<DBObject> aggregationQuery = Arrays.<DBObject>asList(
						//obj,unwind,match1,group,group1,sort1
						obj,unwind,match1,group,group1,sort1
						);

				System.out.println("Aggregation Query time range no downsampling: \n"+aggregationQuery);

				aggregateOutputoutput=table.aggregate(aggregationQuery,options);


			} else if(setTime && down != null){
				List<DBObject> aggregationQuery = Arrays.<DBObject>asList(
						obj,unwind,match1,down,group,group1,sort1 //match1 is to match the timeperiod of interest 
						// obj,unwind,down,group,group1,sort1 //possible if the data are stored by timepoint 
						);

				System.out.println("Aggregation Query set time and downsampling: \n" + aggregationQuery);
				aggregateOutputoutput=table.aggregate(aggregationQuery,options);//,AggregationOptions.builder().allowDiskUse(true));

			} else if(down != null ) {
				List<DBObject> aggregationQuery = Arrays.<DBObject>asList(
					obj,
					unwind,
					down,
					group,
					group1,
					sort1 //possible if the data are stored by timepoint 
				);
				System.out.println("Aggregation Query no set time but downsampling: \n" + aggregationQuery);
				aggregateOutputoutput = table.aggregate(aggregationQuery,options);
			} else {
				List<DBObject> aggregationQuery = Arrays.<DBObject>asList(
					obj,
					unwind,
					group,
					group1,
					sort1
				);
				aggregateOutputoutput = table.aggregate(aggregationQuery, options);
			}
		}
		return aggregateOutputoutput;
	}

	@SuppressWarnings("deprecation")
	public Cursor aggregate(String query, DB database){//AggregationOutput aggregate(String query,DB database){
		boolean setTime = false;
		AggregationOutput output = null;
		Cursor aggregateOutputoutput = null;
		String [] querypart = query.split(",");

		String ts = null;
		if(querypart[0].split("=").length != 1){
			ts = querypart[0].split("=")[1];
		}

		String te = null;
		if(querypart[1].split("=").length != 1){
			te = querypart[1].split("=")[1];
		}

		String coll = querypart[7];
		if(coll.split("=").length == 1){
			System.err.println("Please Insert at least one Valid Colletion");  
		}else{
			coll = coll.split("=")[1];

			String model = querypart[2];
			String simIds = querypart[3];
			String metric = querypart[4];
			String zones = querypart[5];
			String downsampling = querypart[6];

			DBObject match = new BasicDBObject();// query
			DBObject unwind = null;
			DBObject match1 = new BasicDBObject();//metch the DPS element of interest
			DBObject group1 = new BasicDBObject();// groupby zone
			DBObject group = new BasicDBObject(); //group the DPS
			DBObject down = null;
			BasicDBObject Dateobj = new BasicDBObject();
			long offset = (long )TimeZone.getDefault().getRawOffset();//LONG;

			if(ts != null){
				Dateobj.put("$gte", new Date(DocumentReader_Converter.changeDateFormat(ts)).getTime() + offset);
				setTime = true;
			}

			if(te != null){
				Dateobj.put("$lte", new Date(DocumentReader_Converter.changeDateFormat(te)).getTime() + offset);
				setTime = true;
			}

			if(setTime){
				BasicDBObject elemmatch = new BasicDBObject();
				elemmatch.put("Date", Dateobj);

				BasicDBObject o = new BasicDBObject();
				o.put("$elemMatch", elemmatch);
				match.put("dps", o);

				//set the match on time
				elemmatch = new BasicDBObject();
				elemmatch.put("dps.Date", Dateobj);
				match1.put("$match", elemmatch);	
			}
			if(model.split("=").length > 1){
				if((model.split("=")[1]).split("\\|").length != 0){
					match.put("Model", new BasicDBObject("$in", combineTagValue(model)));
				}		
			}
			if(simIds.split("=").length > 1){
				if((simIds.split("=")[1]).split("\\|").length != 0){
					match.put("SimID", new BasicDBObject("$in", combineTagValue(simIds)));
				}		
			}
			if(metric.split("=").length > 1){
				if((metric.split("=")[1]).split("\\|").length != 0){
					match.put("Metric", new BasicDBObject("$in", combineTagValue(metric)));
				}
			}
			if(zones.split("=").length > 1){
				if((zones.split("=")[1]).split("\\|").length != 0){
					match.put("zone", new BasicDBObject("$in", combineTagValue(zones)));
				}
			}
			int value = 0;
			if(downsampling.split("=").length > 1){

				value = Integer.parseInt(downsampling.split("=")[1].split("-")[0]);
				String granularity = downsampling.split("=")[1].split("-")[1];
				String function =  downsampling.split("=")[1].split("-")[2];
				if (function.equals("")){
					function = "avg";
				}

				if(granularity.equalsIgnoreCase("s")){
					value = value*1000;
				}
				else if(granularity.equals("m")){
					value = value*1000*60;
				}
				else if(granularity.equalsIgnoreCase("h")){
					value = value*1000*60*60;
				}else if(granularity.equalsIgnoreCase("d")){
					value = value*1000*60*60*24;
				}else if(granularity.equals("M")){
					//System.out.println("Aggegation by month");
					value = value*1000*60*60*24*30;
				}
				else if(granularity.equalsIgnoreCase("Y")){
					value = value*1000*60*60*24*30*12;
				}

				//	    			String downSampling = "{\"$group\":{\"_id\":{\"SimID\":\"$SimID\",\"Metric\":\"$Metric\",\"Model\":\"$Model\",\"zone\":\"$zone\", date:{\"$subtract\":[{\"$divide\":[\"$dps.Date\","+value+"]},{\"$mod\":[{\"$divide\":[\"$dps.Date\","+value+"]},1]}]}},\"Value\":{\"$"+function+"\":\"$dps.Value\"}}}";
				String downSampling = "{\"$group\":{\"_id\":{\"SimID\":\"$SimID\",\"Metric\":\"$Metric\",\"Model\":\"$Model\",\"zone\":\"$zone\", date:{\"$subtract\":[{\"$subtract\":[\"$dps.Date\","+new Date(01/01/1970).getTime()+"]},{\"$mod\":[{\"$subtract\":[\"$dps.Date\","+new Date(01/01/1970).getTime()+"]},"+ value  +"]}]}},\"Value\":{\"$"+function+"\":\"$dps.Value\"}}}";
				down = (DBObject) JSON.parse(downSampling);

			}
			//set unwind
			unwind = new BasicDBObject();//select DPS
			unwind.put("$unwind", "$dps");
			//Group of times:
			/*	BasicDBObject _id = new BasicDBObject();
	    	_id.put("SimID", "$SimID");
	    	_id.put("Model", "$Model");
	    	_id.put("Metric", "$Metric");
	    	_id.put("zone", "$zone");
	    	BasicDBList l =  new BasicDBList();
	    	l.add("NumberLong($dps.Date).toString()");
	    	l.add("\":\"");
	    	l.add("\"$dps.Value\"");
	    	_id.put("dps", new BasicDBObject("$push",new BasicDBObject("$concat",l)));
	    	group= new BasicDBObject("$group",new BasicDBObject("_id",_id ));*/
			//String downSampling = "{\"$group\":{\"_id\":{\"SimID\":\"$SimID\",\"Metric\":\"$Metric\",\"Model\":\"$Model\",\"zone\":\"$zone\", date:{\"$subtract\":[{\"$divide\":[\"$dps.Date\",3600]},{\"$mod\":[{\"$divide\":[\"$dps.Date\",3600]},1]}]}},\"Value\":{\"$sum\":\"$dps.Value\"}}}";
			//	System.out.println(new Date(01/01/1970).getTime());
			String groupstring="{$group:{_id:{SimID:\"$SimID\",Model:\"$Model\",Metric:\"$Metric\",zone:\"$zone\"},\"dps\":{\"$push\":{\"$concat\":[{\"$substr\":[\"$dps.Date\",0,-1]},\":\",{\"$substr\":[\"$dps.Value\",0,-1]}]}}}}";//dps:{$push:{\"Date\":\"$_id.date\",\"Value\":\"$Value\"}}}}";
			if(down != null){
				//	"$concat":[{"$substr":[{"$multiply":["$_id.date",7.2]},0,-1]},":",{"$substr":["$Value",0,-1]}]
				groupstring="{$group:{_id:{SimID:\"$_id.SimID\",Model:\"$_id.Model\",Metric:\"$_id.Metric\",zone:\"$_id.zone\"},\"dps\":{\"$push\":{\"$concat\":[{\"$substr\":[\"$_id.date\",0,-1]},\":\",{\"$substr\":[\"$Value\",0,-1]}]}}}}";
				//	    		groupstring="{$group:{_id:{SimID:\"$_id.SimID\",Model:\"$_id.Model\",Metric:\"$_id.Metric\",zone:\"$_id.zone\"},\"dps\":{\"$push\":{\"$concat\":[{\"$substr\":[{\"$multiply\":[\"$_id.date\","+value/1000+"]},0,-1]},\":\",{\"$substr\":[\"$Value\",0,-1]}]}}}}";//
			}
			//String groupstring="{$group:{_id:{SimID:\"$SimID\",Model:\"$Model\",Metric:\"$Metric\",zone:\"$zone\"},dps:{$push:{$concat:[{$dateToString:{format:\"%d/%m/%Y %H:%M:%S'\" ,date:\"$dps.Date\"}},\":\",\"$dps.Value\"]}}}}";
			//String groupstring="{$group:{_id:{SimID:\"$SimID\",Model:\"$Model\",Metric:\"$Metric\",zone:\"$zone\"},dps:{$push:{{$dateToString:{format:\"%d/%m/%Y %H:%M:%S'\" ,date:\"$dps.Date\"}}:$dps.Value}}}}";

			group = (DBObject) JSON.parse(groupstring);
			//	System.out.println("group: " +group.toString());
			groupstring = "{$group:{_id:{SimID:\"$_id.SimID\",Model:\"$_id.Model\",Metric:\"$_id.Metric\"},zones:{$addToSet:{zone:\"$_id.zone\",dps:\"$dps\"}}}}";
			group1= (DBObject) JSON.parse(groupstring);
			DBObject sort = (DBObject) JSON.parse("{ $sort : {Model:1,Metric:1,SimID:1,zone:1,\"dps.Date\":1} }");
			// 	DBObject sort1 = (DBObject) JSON.parse("{ $sort : {_id.Model:-1,_id.Metric:-1,_id.SimID:-1} }");
			DBObject sort1 = (DBObject) JSON.parse("{ $sort : {_id.Model:1,_id.Metric:1,_id.SimID:1} }");
			//	System.out.println(coll);
			DBCollection table = database.getCollection(coll);

			DBObject obj =new BasicDBObject("$match", match); 

			AggregationOptions options = AggregationOptions.builder().outputMode(AggregationOptions.OutputMode.CURSOR).allowDiskUse(true).build();
			//options.builder().allowDiskUse(false).build();
			//System.out.println("Options: "+ options.toString());

			if(setTime && down==null){
				List<DBObject> aggregationQuery = Arrays.<DBObject>asList(
						obj,unwind,match1,group,group1,sort1
						);

				//System.out.println("Aggregation Query if: \n"+aggregationQuery);
				aggregateOutputoutput=table.aggregate(aggregationQuery,options);//,AggregationOptions.builder().allowDiskUse(true));


				//	output = table.aggregate(obj,unwind,match1,group,group1,sort);//,AggregationOptions.builder().allowDiskUse(true).build());//,sort

			}else if(setTime){
				List<DBObject> aggregationQuery = Arrays.<DBObject>asList(
						obj,unwind,match1,down,group,group1,sort1
						);

				//System.out.println("Aggregation Query else if: \n"+aggregationQuery);
				aggregateOutputoutput=table.aggregate(aggregationQuery,options);//,AggregationOptions.builder().allowDiskUse(true));

			}
			else if(down!=null){
				List<DBObject> aggregationQuery = Arrays.<DBObject>asList(
						obj,unwind,down,group,group1,sort1
						);

				//System.out.println("Aggregation Query if else: \n"+aggregationQuery);
				aggregateOutputoutput=table.aggregate(aggregationQuery,options);//,AggregationOptions.builder().allowDiskUse(true));
				//output= table.aggregate(obj,unwind,group,group1,sort);//,sort
			}
			else{

				List<DBObject> aggregationQuery = Arrays.<DBObject>asList(
						obj,unwind,group,group1,sort1
						);
				//System.out.println("Aggregation Query else: \n"+aggregationQuery);
				aggregateOutputoutput=table.aggregate(aggregationQuery,options);
			}
		}
		return aggregateOutputoutput;//output;
	}

	private BasicDBList combineTagValue(String string){
		BasicDBList o = new BasicDBList();
		String []sid = (string.split("=")[1]).split("\\|");
		for(int i=0;i<sid.length;i++){
			o.add(sid[i]);
		}
		return o;
	}


}
