package connections;

import javax.xml.xquery.XQConnection;

import us.asu.emitlab.datastructure.JsonList;
import us.asu.emitlab.datastructure.User;



public abstract class DatabaseConnection {
    
    User user= null;
    String url="localhost";
    String dbName= "";
    int port;
    
    public DatabaseConnection(String username,String password,String url,String dbName,int port) {
        this.url   = url;
        this.user = new User(username, password);
        this.dbName=dbName;
        this.port= port;
        
    }
    
    
    
    public abstract JsonList executeQuery(String query,XQConnection conn);
    public abstract JsonList executeQuery(String query,XQConnection conn,String type);
    public abstract XQConnection getConnection();
    
}
