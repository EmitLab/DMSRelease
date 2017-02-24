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

@WebServlet("/AddGrant")
public class AddGrant extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public AddGrant() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		    JSONObject jsonObject = new JSONObject(request.getParameter("jData"));
            String name  = jsonObject.getString("name");
            String title = jsonObject.getString("title");
            String link  = jsonObject.getString("link");
            int pid      = jsonObject.getInt("pid");
            
            String query = "INSERT INTO grants (name, title, link, pid) VALUES (?,?,?," + pid + ")";
            
            MySqlConnector mysql = new MySqlConnector();
            mysql.getConnection();
            
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
		} catch (Exception e){
		    e.printStackTrace();
		}
	}
}
