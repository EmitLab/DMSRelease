package defaults;

public class Constants {
    public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static String DATE_ZONE   = "UTC";
    
    public static String BASEX_HOST = "localhost";
    public static int BASEX_PORT    = 1984;
    public static String BASEX_USER = "admin";
    public static String BASEX_PASS = "admin";
    
    public static String MONGO_LOCAL_NAME = "localhost";
    public static int MONGO_LOCAL_PORT = 27017;
    
    public static String MONGO_SERVER_NAME = "localhost"; 
    public static int MONGO_SERVER_PORT = 27017;
    public static int BASEX_SERVER_PORT = 1984;
    
    // Mongo Time Series Collection Name
    public static String TS_DB_NAME  = "tsData";
    
    /* MONOG KEYS*/
    
    public static String MONGO_SIMID  = "SimID";
    public static String MONGO_METRIC = "Metric";
    public static String MONGO_ZONE   = "zone";
    public static String MONGO_MODEL  = "Model";
    public static String MONGO_DATE   = "Date";
    public static String MONGO_VALUE  = "Value";
    public static String MONGO_DATA   = "dps";
}
