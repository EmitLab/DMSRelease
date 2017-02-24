package defaults;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import connections.MySqlConnector;

public class SQLUpdateQuery extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public SQLUpdateQuery() {
        super();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
		    
		    JSONObject jData = new JSONObject(request.getParameter("jData"));
		    String query = jData.getString("query");
		    
		    MySqlConnector mysql = new MySqlConnector();
		    mysql.getConnection();
		    
		    mysql.updateQuery(query);
		    
		    mysql.closeConnection();
		    
		    PrintWriter writer = response.getWriter();
            writer.println();
            writer.flush();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}

}
