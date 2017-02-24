package servlets.admin.users;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import connections.MySqlConnector;

public class GetNumberOfUsers extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetNumberOfUsers() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//TODO : Error handling on front end.
		String query = "SELECT count(userid) as userCount from dmsuser;";
		
		MySqlConnector mysql = new MySqlConnector();
		PrintWriter writer = response.getWriter();
		JSONObject jObject = new JSONObject();
		try {
			mysql.getConnection();
		
			ResultSet resultset = mysql.executeQuery(query);
			resultset.next();
			
			int userCount = resultset.getInt("userCount");
			jObject.put("userCount", userCount);
			jObject.put("servletState","1");
			
			writer.println(jObject);
			writer.flush();
			
			mysql.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				jObject.put("servletState","0");
				
				writer.println(jObject);
				writer.flush();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
		}
		
	}

}
