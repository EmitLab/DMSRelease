package servlets.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import connections.MySqlConnector;

public class ProjectUpdate extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public ProjectUpdate() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			JSONObject jsonObject = new JSONObject(request.getParameter("jData"));
			int projectId             = jsonObject.getInt("id");
			String projectName        = jsonObject.getString("name");
			String projectAbbr        = jsonObject.getString("abbr");
			String projectTitle       = jsonObject.getString("title");
			String projectDescription = jsonObject.getString("description");
			int projectStatus         = jsonObject.getInt("status");
			
			String query = "UPDATE project SET "
					+ "name='" + projectName + "',"
					+ "abbr='" + projectAbbr + "',"
					+ "title='" + projectTitle + "',"
					+ "description='" + projectDescription + "',"
					+ "status=" + projectStatus + " "
					+ "WHERE projectid=" + projectId;
			
			MySqlConnector mysql = new MySqlConnector();
			mysql.getConnection();
			mysql.updateQuery(query);
			
			query = "SELECT * from project";
			ResultSet resultSet = mysql.executeQuery(query);
			JSONArray jArray = new JSONArray();
			while(resultSet.next()){
				JSONObject jObject = new JSONObject();
				jObject.put("id", resultSet.getString("projectid"));
				jObject.put("name", resultSet.getString("name"));
				jObject.put("title", resultSet.getString("title"));
				jObject.put("abbr", resultSet.getString("abbr"));
				jObject.put("description", resultSet.getString("description"));
				jObject.put("status", resultSet.getString("status"));
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
