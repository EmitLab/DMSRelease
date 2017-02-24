package us.asu.emitlab.connection.timeseriesdb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

//import org.bson.types.BSONTimestamp;
import org.json.JSONException;
import org.json.JSONObject;

import connections.MongoConnector;
import defaults.Constants;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class DocumentReader_Converter {
    String folder="";
    List <String> FileList = new ArrayList<String>();;
    MongoConnector ci = new ConnectionImplMongoDB();
    DB database;
    static final String DATEFORMAT = Constants.DATE_FORMAT;

    public DocumentReader_Converter(){
        MongoClient mc = ci.getInstance(Constants.MONGO_LOCAL_NAME, Constants.MONGO_LOCAL_PORT);
        database =  ci.getDbFromName(Constants.TS_DB_NAME);
    }

    public DocumentReader_Converter(String folder) {

        this.folder = folder;
        FolderReader(new File(folder));

        for(int i = 0; i < this.FileList.size(); i++){
            System.out.println(FileList.get(i));
        }
        
        MongoClient mc = ci.getInstance(Constants.MONGO_LOCAL_NAME, Constants.MONGO_LOCAL_PORT);
        database =  ci.getDbFromName(Constants.TS_DB_NAME);
    }

    public DocumentReader_Converter(String folder, String filename){
        this.folder = folder;
        this.FileList =  new ArrayList<String>();
        this.FileList.add(filename);
        MongoClient mc = ci.getInstance(Constants.MONGO_LOCAL_NAME, Constants.MONGO_LOCAL_PORT);
        database =  ci.getDbFromName(Constants.TS_DB_NAME);
    }

    public void FolderReader(File folder){

        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                FolderReader(fileEntry);
            } else {
                FileList.add(fileEntry.getAbsolutePath());
            }
        }
    }

    public void insertDocument(){
        this.readFiles(true);
    }

    private String readFiles(boolean check){

        BufferedReader br = null;
        for(int i =0;i<this.FileList.size();i++){
            List<String[]>list = new ArrayList<String []>();
            try {
                if(this.FileList.get(i).endsWith("csv")){
                    if(!this.FileList.get(i).contains("(1)")){
                        if(check)
                            br =  new BufferedReader(new FileReader(this.folder+File.separator+this.FileList.get(i)));
                        else
                            br = new BufferedReader(new FileReader(this.FileList.get(i)));

                        String leggo= null;

                        while((leggo = br.readLine())!=null){
                            list.add(leggo.split(","));
                        }
                        if(list.size()!=0){
                            insertFileToMongo(list, this.folder+File.separator+this.FileList.get(i));
                            //insertFileintoMongo(list, this.folder+File.separator+this.FileList.get(i));
                        }else{
                            System.err.println("Empty File: "+ this.folder+File.separator+this.FileList.get(i));
                        }
                        br.close();
                    }else{
                        System.out.println("Not considered: "+this.FileList.get(i));	
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    private String readFiles(boolean check, String coll){

        BufferedReader br = null;
        for(int i =0;i<this.FileList.size();i++){
            List<String[]>list = new ArrayList<String []>();
            try {
                if(this.FileList.get(i).endsWith("csv")){
                    if(!this.FileList.get(i).contains("(1)")){
                        if(check)
                            br =  new BufferedReader(new FileReader(this.folder+File.separator+this.FileList.get(i)));
                        else
                            br = new BufferedReader(new FileReader(this.FileList.get(i)));

                        String leggo= null;

                        while((leggo = br.readLine())!=null){
                            list.add(leggo.split(","));
                        }
                        if(list.size()!=0){
                            insertFileToMongo(list, this.folder+File.separator+this.FileList.get(i),coll);
                            //insertFileintoMongo(list, this.folder+File.separator+this.FileList.get(i));
                        }else{
                            System.err.println("Empty File: "+ this.folder+File.separator+this.FileList.get(i));
                        }
                        br.close();
                    }else{
                        System.out.println("Not considered: "+this.FileList.get(i));	
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return null;
    }
    
    private void insertFileToMongo(List<String[]> file, String path){
        DBObject dbobj= null;
        String [] parameters = path.replace("\\","/").split("/");
        for(int i=2 ; i<file.get(0).length;i++){
            dbobj =  new BasicDBObject();
            dbobj.put("SimID", parameters[parameters.length-2]);
            dbobj.put("Metric", parameters[parameters.length-1].split("\\.")[0]);
            dbobj.put("Model", parameters[parameters.length-3]);
            if(file.get(0)[i].contains("_")){
                dbobj.put("zone",file.get(0)[i].split("_")[1]);
                //temp.append("\"zone\":\""+file.get(0)[i].split("_")[1]);
            }else if(file.get(0)[i].contains("-")){
                dbobj.put("zone",file.get(0)[i].split("-")[1]);
                //temp.append("\"zone\":\""+file.get(0)[i].split("-")[1]);
            }
            BasicDBList dpsobj =  new BasicDBList();
            for(int j=0;j<file.size();j++){
                if(j==0){
                    //temp.append("\",\"DPS\":[");
                }else {
                    DBObject dateobj = new BasicDBObject();
                    dateobj.put("Date", new Date(changeDateFormat(file.get(j)[1])) );
                    dateobj.put("Value", new BasicDBObject(""+new Date(changeDateFormat(file.get(j)[1])).getTime(),file.get(j)[i]));//file.get(j)[i]);
                    dpsobj.add(dateobj);
                }	
            }
            dbobj.put("dps", dpsobj);
            System.out.println(dbobj.toString());
            ci.insetIntoCollection(database, "EpidemicTest", dbobj);
        }
    }

    private void insertFileToMongo(List<String[]> file, String path,String coll){
        DBObject dbobj= null;
        String [] parameters = path.replace("\\","/").split("/");
        for(int i=1 ; i<file.get(0).length;i++){
            dbobj =  new BasicDBObject();
            System.out.println(parameters[parameters.length-2]+" - "+parameters[parameters.length-1].split("\\.")[0]+"-"+parameters[parameters.length-3]+"-");
            dbobj.put("SimID", parameters[parameters.length-2]);
            dbobj.put("Metric", parameters[parameters.length-1].split("\\.")[0]);
            dbobj.put("Model", parameters[parameters.length-3]);
            if(file.get(0)[i].contains("_")){
                //	System.out.println(file.get(0)[i]);
                dbobj.put("zone",file.get(0)[i].split("_")[1]);//Ebola
                //temp.append("\"zone\":\""+file.get(0)[i].split("_")[1]);
            }else if(file.get(0)[i].contains("-")){
                //	System.out.println(file.get(0)[i]);
                dbobj.put("zone",file.get(0)[i].split("-")[1]); //Ebola
                //temp.append("\"zone\":\""+file.get(0)[i].split("-")[1]);
            }
            else{ //this else is for Energy the zone has no split
                //	System.out.println("zone:"+file.get(0)[i]);
                dbobj.put("zone",file.get(0)[i]);
            }
            BasicDBList dpsobj =  new BasicDBList();
            for(int j=0;j<file.size();j++){
                if(j==0){
                    //temp.append("\",\"DPS\":[");
                }else {//&& i<file.get(0).length-1){
                    DBObject dateobj = new BasicDBObject();

                    try{
                        dateobj.put("Date",new Date(changeDateFormat(file.get(j)[0])).getTime());//Ebola 1 -- Energy 0
                        dateobj.put("Value",Double.parseDouble((file.get(j)[i])));//new BasicDBObject(""+new Date(changeDateFormat(file.get(j)[0])).getTime(),Double.parseDouble((file.get(j)[i]))));// Double.parseDouble((file.get(j)[i])));//
                        dpsobj.add(dateobj);
                    }//temp.append("{Date:\""+formatter.format(new Date(changeDateFormat(file.get(j)[1])))+"\", Value:"+file.get(j)[i]+"}]}");
                    catch (Exception e) {
                        // TODO Auto-generated catch block
                        System.err.println("value not added: "+ file.get(j)[0]+" --- "+ file.get(j)[i]);
                    }

                }	
            }//D:\\EnergyDataCleaned\\5ZoneAirCooled\\EPG000000000

            dbobj.put("dps", dpsobj);
            //	System.out.println(dbobj.toString());
            ci.insetIntoCollection(database, coll, dbobj);
        }
    }
    
    private void insertFileintoMongo(List<String[]> file, String path) {
        // TODO Auto-generated method stub

        //System.out.println("path: "+ path);

        String [] parameters = path.replace("\\","/").split("/");
        //System.out.println("size: "+ parameters.length);
        DateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
        formatter.setTimeZone(TimeZone.getTimeZone(Constants.DATE_ZONE));

        StringBuilder temp;

        for(int i=2 ; i<file.get(0).length;i++){
            temp =new StringBuilder();
            temp.append("{\"Metric\":\""+parameters[parameters.length-1].split("\\.")[0]+"\", \"SimID\":\""+parameters[parameters.length-2]+"\",\"Model\":\""+ parameters[parameters.length-3]+"\",");
            /*BasicDBObject document = new BasicDBObject();
	document.put("Metric", parameters[parameters.length-1].split("\\.")[0]);
	document.put("SimID", parameters[parameters.length-2]);
	document.put("Model", parameters[parameters.length-3]);
             */
            //System.out.println("Inserting: "+parameters[parameters.length-2]+ " zone: "+ file.get(0)[i]);
            if(file.get(0)[i].contains("_")){
                temp.append("\"zone\":\""+file.get(0)[i].split("_")[1]);
            }else if(file.get(0)[i].contains("-")){
                temp.append("\"zone\":\""+file.get(0)[i].split("-")[1]);
            }


            for(int j=0;j<file.size();j++){
                if(j==0){

                    temp.append("\",\"DPS\":[");
                }else if(j==file.size()-1){//&& i<file.get(0).length-1){
                    temp.append("{Date:\""+formatter.format(new Date(changeDateFormat(file.get(j)[1])))+"\", Value:"+file.get(j)[i]+"}]}");
                    //temp.append(new Date(changeDateFormat(file.get(j)[1])).getTime()+":"+file.get(j)[i]+"}}");
                    /*
				temp.append("{\"Date\":\""+new Date(file.get(j)[1]).getTime()+"\",");
				temp.append("\"Val\":\""+file.get(j)[i]+"\"}]},");*/
                }/*else if(j==file.size()-1 && i==file.get(0).length-1){
				temp.append("{"+new Date(changeDateFormat(file.get(j)[1])).getTime()+":"+file.get(j)[i]+"}]}]}");*/
                /*
				temp.append("{\"Date\":\""+new Date(file.get(j)[1]).getTime()+"\",");
				temp.append("\"Val\":\""+file.get(j)[i]+"\"}]}]}");*/
                //}
                else{
                    //System.out.println("Date: "+ file.get(j)[1]);
                    temp.append("{Date:\""+formatter.format(new Date(changeDateFormat(file.get(j)[1])))+"\", Value:"+file.get(j)[i]+"},");
                    //temp.append(new Date(changeDateFormat(file.get(j)[1])).getTime()+":"+file.get(j)[i]+",");
                    /*
				temp.append("{\"Date\":\""+new Date(file.get(j)[1]).getTime()+"\",");
				temp.append("\"Val\":\""+file.get(j)[i]+"\"},");
                     */
                }
            }
            //	System.out.println(temp.toString());
            JSONObject j= null;
            try {
                j = new JSONObject(temp.toString());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //System.out.println(j.toString());
            DBObject obj = (DBObject) JSON.parse(temp.toString());
            //System.out.println(obj.toString());
            ci.insetIntoCollection(database, obj.get("Model").toString(), obj);
        }

        /*   System.out.println(temp);
	JSONObject ob= null;
	try {
		ob = new JSONObject(temp.toString());
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	System.out.println(ob.toString());*/
        /*
	DBObject obj = (DBObject) JSON.parse(temp.toString());
	ci.insetIntoCollection(database, obj.get("Model").toString(), obj);
         */
    }

    public static String changeDateFormat(String string) {
        // TODO Auto-generated method stubString 

        if(string.contains("-")){
            String []data = string.split(" ");
            String[] datapart = data[0].split("-");
            string = datapart[1]+"/"+datapart[2]+"/"+datapart[0]+" "+data[1];
        }
        return string;
    }

    public void insertDocuments() {
        // TODO Auto-generated method stub
        this.readFiles(false);
    }
    
    public void insertDocuments(String coll) {
        // TODO Auto-generated method stub
        if (coll==null || coll.equals("")){
            System.out.println("Add a valid collectionName");
        }else{
            this.readFiles(false,coll);
        }
    }

    public DBCursor find (String query){
        return ci.find(query, database);
    }

    public Cursor aggregate (String query){//AggregationOutput aggregate (String query){
        return ci.aggregate(query, database);
    }

}
