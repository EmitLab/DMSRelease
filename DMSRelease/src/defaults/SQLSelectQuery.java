package defaults;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSetMetaData;

import connections.MySqlConnector;

public class SQLSelectQuery extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SQLSelectQuery() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    try{
	        JSONObject jData = new JSONObject(request.getParameter("jData"));
	        String query = jData.getString("query");
	        MySqlConnector mysql = new MySqlConnector();
	        mysql.getConnection();
	        
	        ResultSet rs           = mysql.executeQuery(query);
	        ResultSetMetaData rsmd = rs.getMetaData();
	        int colCount           = rsmd.getColumnCount();
	        
	        JSONArray jArray = new JSONArray();
	        while(rs.next()){
	            JSONObject jObject = new JSONObject();
	            for(int col = 1; col <= colCount; col++){
	                jObject.put(rsmd.getColumnName(col), rs.getObject(col).toString());
	            }
	            jArray.put(jObject);
	        }
	        PrintWriter writer = response.getWriter();
            writer.println(jArray);
            writer.flush();
	        mysql.closeConnection();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}
