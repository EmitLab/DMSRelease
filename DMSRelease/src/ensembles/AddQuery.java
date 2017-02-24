package ensembles;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import connections.MySqlConnector;
import defaults.SessionVariableList;

/**
 * Servlet implementation class AddQuery
 */
@WebServlet("/AddQuery")
public class AddQuery extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public AddQuery() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		    HttpSession session = request.getSession(true);
		    
		    JSONObject jData = new JSONObject(request.getParameter("jData"));
		    String name        = jData.getString("name");
		    String description = jData.getString("description");
		    String content     = jData.getString("query");
		    
		    //long timed = jData.getLong("timed");
		    
		    int pid    = Integer.parseInt(session.getAttribute(SessionVariableList.CURRENT_PROJECT_ID).toString());
            int uid    = Integer.parseInt(session.getAttribute(SessionVariableList.USER_ID).toString());

            long timed = System.currentTimeMillis();
            
            MySqlConnector mysql = new MySqlConnector();
            mysql.getConnection();
            
            // Inserting Query
            String query = "INSERT INTO query (name, description, query, uid, pid, created, accessed, action) VALUES (?,?,?," 
                            + uid + "," + pid + "," + timed + "," + timed + ",'Created')";
            PreparedStatement statement = mysql.db.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setString(3, content);
            statement.executeUpdate();

            // Getting Last inserted query id.
            query = "SELECT LAST_INSERT_ID() as qid";
            ResultSet rs = mysql.executeQuery(query);
            rs.next();
            int qid = rs.getInt("qid");
            
            // Preparing log entry.
            query = "INSERT INTO querylog (qid, action, timed) VALUES (" + qid + ",'Created'," + timed + ")";
            statement = mysql.db.prepareStatement(query);
            statement.executeUpdate();
            
            mysql.closeConnection();
            
		} catch(Exception e) {
		    e.printStackTrace();
		}
	}

}
