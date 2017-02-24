package publication;

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

@WebServlet("/AddPublication")
public class AddPublication extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public AddPublication() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		    JSONObject jsonObject = new JSONObject(request.getParameter("jData")); 
            String title = jsonObject.getString("title");
            String pids  = jsonObject.getString("pids");
            String tags  = jsonObject.getString("tags");
            
            String query = "INSERT INTO publications (title,tags) VALUES (?,?)";
            
            MySqlConnector mysql = new MySqlConnector();
            mysql.getConnection();
            
            PreparedStatement statement = mysql.db.prepareStatement(query);
            statement.setString(1, title);
            statement.setString(2, tags);
            statement.executeUpdate();

            query = "SELECT LAST_INSERT_ID() as id";
            ResultSet rs = mysql.executeQuery(query);
            rs.next();
            int pubid = rs.getInt("id");
            
            String[] pidArr = pids.split(",");
            for (int i = 0; i < pidArr.length; i++){
                query = "INSERT INTO pubproj (pubid, projid) VALUES (" + pubid + "," + Integer.parseInt(pidArr[i]) + ")";
                statement = mysql.db.prepareStatement(query);
                statement.executeUpdate();
            }
            mysql.closeConnection();
            
            PrintWriter writer  = response.getWriter();
            writer.flush();
		} catch (Exception e){
		    e.printStackTrace();
		}
	}
}
