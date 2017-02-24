package servlets.admin.team;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import connections.*;

public class TeamList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public TeamList() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			MySqlConnector mysql = new MySqlConnector();
			mysql.getConnection();
			
			String query = "SELECT * FROM team;";
			
			ResultSet resultSet = mysql.executeQuery(query);
			JSONArray jArray = new JSONArray();
			
			while(resultSet.next()){
				JSONObject jObject = new JSONObject();
				jObject.put("id",          resultSet.getString("id"));
				jObject.put("name",        resultSet.getString("name"));
				jObject.put("designation", resultSet.getString("designation"));
				jObject.put("institute",   resultSet.getString("institute"));
				jObject.put("linkedin",    resultSet.getString("linkedin"));
				jObject.put("scholar",     resultSet.getString("scholar"));
				jObject.put("twitter",	   resultSet.getString("twitter"));
				jObject.put("homepage",    resultSet.getString("homepage"));
				jObject.put("image", 	   resultSet.getString("imageurl"));
				jObject.put("status",      resultSet.getString("status"));
				jObject.put("type",        resultSet.getString("type"));
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
