package defaults;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.json.JSONArray;
import org.json.JSONObject;

public class ParseRStoJSON {
    public static JSONArray parseRS(ResultSet rs){
        JSONArray jArray = new JSONArray();
        try{
            ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();
            while(rs.next()){
                JSONObject jObject = new JSONObject();
                for(int col = 1; col <= colCount; col++){
                    jObject.put(rsmd.getColumnName(col), rs.getObject(col).toString());
                }
                jArray.put(jObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jArray;
    }
}
