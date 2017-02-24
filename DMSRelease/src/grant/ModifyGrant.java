package grant;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import connections.MySqlConnector;
import defaults.ParseRStoJSON;

@WebServlet("/ModifyGrant")
public class ModifyGrant extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public ModifyGrant() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
		    JSONObject jData = new JSONObject(request.getParameter("jData"));
		    int id = jData.getInt("id");
		    int pid = jData.getInt("pid");
		    String name = jData.getString("name");
		    String title = jData.getString("title");
		    String link = jData.getString("link");
		    
		    MySqlConnector mysql = new MySqlConnector();
            mysql.getConnection();
            // Updating Query
            String query = "UPDATE grants SET name=?,title=?,link=?,pid = " + pid + " WHERE id = " + id;
            PreparedStatement statement = mysql.db.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, title);
            statement.setString(3, link);
            statement.executeUpdate();
		    
            query = "SELECT g.id as gid, g.name as gname, g.pid as pid, p.name as pname FROM grants as g LEFT JOIN project as p ON g.pid = p.projectid ";
            ResultSet resultSet = mysql.executeQuery(query);
            JSONArray jArray    = ParseRStoJSON.parseRS(resultSet);
            mysql.closeConnection();
            
            PrintWriter writer  = response.getWriter();
            writer.println(jArray);
            writer.flush();
		    
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}

}
