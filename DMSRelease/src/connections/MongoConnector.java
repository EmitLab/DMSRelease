package connections;

import java.util.List;

import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public interface MongoConnector {
    public MongoClient getInstance(String address, int port);
    public DB getDbFromName(String namedb);
    public List<String> getListDbName();
    public void visualizzaListaDb(List<String> dbs);
    public boolean closeDb();
    public void insetIntoCollection(DB database, String nomeCollection, DBObject dbObject);
    //public DBCollection getCollection(DB database);
    public void displayAllCollection(DB database);
    
    public DBCursor find(String query,DB database);
    public Cursor aggregate(String query,DB database);//AggregationOutput aggregate(String query,DB database);
    public Cursor aggregate(List<StringBuilder> mongoquery,DB database);

}
