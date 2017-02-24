package ensembles;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.basex.api.client.ClientSession;
import org.basex.core.Context;

public class QueryBaseX {

    /**
     * @author Reece
     * @param dbName
     * @param fileID
     * 
     * delete a data object from BaseX database
     * */
    public QueryBaseX(){

    }
    public static void delete(String dbName, String fileID) {
        /*Context context = new Context();
        try {
            new Open(dbName).execute(context);
            new Delete(fileID).execute(context);
        } catch (BaseXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }
    /**
     * @author Reece
     * @param dbName
     * @param filePath
     * 
     * insert a data object into BaseX database
     * */
    public static void insert(String dbName, String filePath){
        /*Context context = new Context();
        try {
            new Open(dbName).execute(context);
            new Add("", filePath).execute(context);
        } catch (BaseXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }

    public static void insertDoc(String dbName, String id, String doc) throws IOException{
        //BaseXClient session = new BaseXClient("localhost", 1984, "admin", "admin");
        

        try {
            Context context = new Context();
            //new Open(dbName).execute(context);
            // ClientSession session = new ClientSession(context, "admin", "admin");
            ClientSession session = new ClientSession("localhost", 1984, "admin", "admin");
            session.execute("OPEN " + dbName);
            //session.execute("db open(Epidemic)");

            InputStream input = new ByteArrayInputStream(doc.getBytes());
            String path = id + ".xml";
            session.add(path, input);
            session.close();
            context.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    public static void deleteDoc(String dbName, String id, String doc) throws IOException{
        //BaseXClient session = new BaseXClient("localhost", 1984, "admin", "admin");
        
        try {
            Context context = new Context();
            //new Open(dbName).execute(context);
            // ClientSession session = new ClientSession(context, "admin", "admin");
            ClientSession session = new ClientSession("localhost", 1984, "admin", "admin");
            session.execute("OPEN " + dbName);
            //session.execute("db open(Epidemic)");

            // InputStream input = new ByteArrayInputStream(doc.getBytes());
            String path = id + ".xml";
            //session.add(path, input);
            //System.out.println(path);
            session.execute("DELETE " + path);
            session.close();
            context.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException{
        // test below

        String dbName = "Epidemic";
        String dataNum = "100001";
        String filePath = "/home/emitlab/Desktop/"+ dataNum + ".xml";
        // insert(dbName, filePath);

        // read XML into string
        InputStream is = new FileInputStream(filePath); 
        BufferedReader buf = new BufferedReader(new InputStreamReader(is)); 
        String line = buf.readLine(); 
        StringBuilder sb = new StringBuilder(); 
        while(line != null){ 
            sb.append(line).append("\n"); 
            line = buf.readLine(); 
        } 
        String doc = sb.toString();

        // System.out.println("Doc as String: "+ doc);
        insertDoc(dbName, dataNum, doc);
        //myDelete(dbName, dataNum, doc);
    }

}

