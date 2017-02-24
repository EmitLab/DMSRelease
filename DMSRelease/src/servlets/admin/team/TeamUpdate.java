package servlets.admin.team;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import connections.MySqlConnector;

public class TeamUpdate extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public TeamUpdate() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			JSONObject jObject = new JSONObject(request.getParameter("jData"));
			String id          = jObject.getString("id");
			String name        = jObject.getString("name");
			String designation = jObject.getString("designation");
			String institute   = jObject.getString("institute");
			String image       = jObject.getString("image");
			String homepage    = jObject.getString("homepage");
			String linkedin    = jObject.getString("linkedin");
			String scholar     = jObject.getString("scholar");
			String twitter     = jObject.getString("twitter");
			String type        = jObject.getString("type");
			String projectAssociationListStr        = jObject.getString("projectAssociationList");
			int status         = jObject.getInt("status");
			
			System.out.println("\n*********projectAssociationListStr ="+projectAssociationListStr);
			String projectAssociationListStr1 = projectAssociationListStr.replaceAll(", ", "\",\"");
			String projectAssociationListStr2 = "\"" + projectAssociationListStr1 + "\"";
			System.out.println("\n*********projectAssociationListStr2 ="+projectAssociationListStr2);
			
			
			String query = "UPDATE team SET "
							+ "name='"        + name        + "',"
							+ "designation='" + designation + "',"
							+ "institute='"   + institute   + "',"
							+ "linkedin='"    + linkedin    + "',"
							+ "homepage='"    + homepage    + "',"
							+ "scholar='"     + scholar     + "',"
							+ "twitter='"     + twitter     + "',"
							+ "imageurl='"    + image       + "',"
							+ "type='"        + type        + "',"
							+ "status="       + status      + " "
							+ "WHERE id=" + id;
			
			System.out.println("query==>"+query);
			
			MySqlConnector mysql = new MySqlConnector();
			mysql.getConnection();
			mysql.updateQuery(query);
			
			// Deletes all the project ids associated with that team member 
			String deleteTeamIdQuery = "DELETE from dms.teamproj where teamid = "+ id  ;
			mysql.updateQuery(deleteTeamIdQuery);
			
			// START: Search all the projects from project names and then insert each project id along with teamid in teamproj table
			String projectQuery = "SELECT * from project where  name IN(" + projectAssociationListStr2 + ")";
			ResultSet resultSet1 = mysql.executeQuery(projectQuery);
			String teamprojInsertQuery = "";
			while(resultSet1.next()){
				teamprojInsertQuery = "INSERT into dms.teamproj(teamid, projid)"
										+ "values ("+ id +", "+ resultSet1.getString("projectid") +")";
				mysql.insertQuery(teamprojInsertQuery);
			}
			// END
			
			query = "SELECT * from project";
			ResultSet resultSet = mysql.executeQuery(query);
			JSONArray jArray = new JSONArray();
			while(resultSet.next()){
				jObject = new JSONObject();
				jObject.put("id", resultSet.getString("projectid"));
				jObject.put("name", resultSet.getString("name"));
				jObject.put("title", resultSet.getString("title"));
				jObject.put("abbr", resultSet.getString("abbr"));
				jObject.put("description", resultSet.getString("description"));
				jObject.put("status", resultSet.getString("status"));
				jArray.put(jObject);
			}
			
			mysql.closeConnection();
			
			PrintWriter writer = response.getWriter();
			writer.println();
			writer.flush();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
