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

public class TeamAdd extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public TeamAdd() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			JSONObject jObject = new JSONObject(request.getParameter("jData"));
			
			String name        = jObject.getString("name");
			String designation = jObject.getString("designation");
			String institute   = jObject.getString("institute");
			String image       = jObject.getString("image");
			String homepage    = jObject.getString("homepage");
			String linkedin    = jObject.getString("linkedin");
			String scholar     = jObject.getString("scholar");
			String twitter     = jObject.getString("twitter");
			String type        = jObject.getString("type");
			int status         = jObject.getInt("status");
			String projectAssociationListStr        = jObject.getString("projectAssociationList");
			
			String projectAssociationListStr1 = projectAssociationListStr.replaceAll(", ", "\",\"");
			String projectAssociationListStr2 = "\"" + projectAssociationListStr1 + "\"";
			System.out.println("\n*********projectAssociationListString ="+projectAssociationListStr2);
			
			
			String query = "INSERT INTO team " + 
						   "(name,designation,institute,linkedin,scholar,homepage,twitter,imageurl,type,status)" +
						   " VALUES ('" + 
						   name         + "','" +  
						   designation  + "','" +
						   institute    + "','" +
						   linkedin     + "','" +
						   scholar      + "','" +
						   homepage     + "','" +
						   twitter      + "','" +
						   image        + "','" +
						   type         + "',"  +
						   status       + ")";
			MySqlConnector mysql = new MySqlConnector();
			mysql.getConnection();
			mysql.insertQuery(query);
			
			//Find id of the inserted record
			int id=0;
			String findQuery = "SELECT id FROM dms.team "
								+ "where name like '"+ name +"' and "
								+ "designation like '"+ designation +"' and "
										+ "institute like '"+institute+"'";
			ResultSet resultSet = mysql.executeQuery(findQuery);
			resultSet.next();
			id = resultSet.getInt("id");
			
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
			
			
			
			
			
			mysql.closeConnection();
			
			PrintWriter writer = response.getWriter();
			writer.println();
			writer.flush();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
