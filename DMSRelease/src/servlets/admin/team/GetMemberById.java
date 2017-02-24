package servlets.admin.team;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import connections.MySqlConnector;

public class GetMemberById extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public GetMemberById() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			JSONObject jObject = new JSONObject(request.getParameter("jData"));
			int memberId = jObject.getInt("id");
			
			MySqlConnector mysql = new MySqlConnector();
			mysql.getConnection();
			
			String query = "SELECT * FROM team where id = " + memberId;
			ResultSet resultSet = mysql.executeQuery(query);
			
			resultSet.last();
			int total = resultSet.getRow();
			if (total == 1){
				resultSet.beforeFirst();
				resultSet.next();  
				/* next() must be called before result is read, as beforefirst() 
				 * takes the pointer to header, not to current row. 
				 */
				jObject = new JSONObject();
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
				
				PrintWriter writer = response.getWriter();
				writer.println(jObject);
				writer.flush();
			} else {

			}
			mysql.closeConnection();
			
		} catch (Exception e){
			
		}
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

}
