package servlets.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import connections.MySqlConnector;
import defaults.ParseRStoJSON;

public class ProjectAdd extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public ProjectAdd() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			JSONObject jsonObject = new JSONObject(request.getParameter("jData"));
			String name        = jsonObject.getString("projectName");
			String abbr        = jsonObject.getString("projectAbbr");
			String title       = jsonObject.getString("projectTitle");
			String description = jsonObject.getString("projectDescription");
			System.out.println(description);
			long timed         = System.currentTimeMillis();
			
			String query = "INSERT INTO project (name, abbr, title, description, status, ontime) VALUES (?,?,?,?,1," + timed + ")";
			
			MySqlConnector mysql = new MySqlConnector();
			mysql.getConnection();
			
			PreparedStatement statement = mysql.db.prepareStatement(query);
			statement.setString(1, name);
			statement.setString(2, abbr);
			statement.setString(3, title);
			statement.setString(4, description);
			//mysql.insertQuery(query);
			statement.executeUpdate();
			query = "SELECT projectid as id, name, title,abbr, description,status, ontime from project";
			ResultSet resultSet = mysql.executeQuery(query);
			JSONArray jArray = ParseRStoJSON.parseRS(resultSet);
			PrintWriter writer = response.getWriter();
			writer.println(jArray);
			writer.flush();
			
			mysql.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
