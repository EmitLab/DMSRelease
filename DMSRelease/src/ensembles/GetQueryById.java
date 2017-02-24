package ensembles;

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

/**
 * Servlet implementation class GetQueryById
 */
@WebServlet("/GetQueryById")
public class GetQueryById extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public GetQueryById() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    try {
	        JSONObject jData = new JSONObject(request.getParameter("jData"));
	        
	        int queryId = jData.getInt("qid");
	        
	        String query = "SELECT id, name, description, query FROM query WHERE id = " + queryId;
	        
	        MySqlConnector mysql = new MySqlConnector();
	        mysql.getConnection();
	        ResultSet rs = mysql.executeQuery(query);
	        JSONArray jArray = ParseRStoJSON.parseRS(rs);
	        mysql.closeConnection();
	        
	        PrintWriter writer = response.getWriter();
            writer.println(jArray);
            writer.flush();
            
	    } catch (Exception e) {
	       e.printStackTrace(); 
	    }
	}

}
