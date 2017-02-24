package grant;

import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet("/GetGrantById")
public class GetGrantById extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public GetGrantById() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		    JSONObject jData = new JSONObject(request.getParameter("jData"));
		    
		    int gid = jData.getInt("gid");
		    
		    String query = "SELECT name, title, link, pid FROM grants WHERE id = " + gid;
		    
		    MySqlConnector mysql = new MySqlConnector();
		    mysql.getConnection();
		    ResultSet rs = mysql.executeQuery(query);
		    JSONArray jArray = ParseRStoJSON.parseRS(rs);
		    mysql.closeConnection();
		    
		    PrintWriter writer  = response.getWriter();
            writer.println(jArray);
            writer.flush();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}

}
