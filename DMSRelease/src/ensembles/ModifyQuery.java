package ensembles;

import java.io.IOException;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import connections.MySqlConnector;

@WebServlet("/ModifyQuery")
public class ModifyQuery extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public ModifyQuery() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		    JSONObject jData = new JSONObject(request.getParameter("jData"));
		    String qid         = jData.getString("qid");
		    String name        = jData.getString("name");
            String description = jData.getString("description");
            String content     = jData.getString("query");
            //long timed         = jData.getLong("timed");
            long timed         = System.currentTimeMillis(); 
            MySqlConnector mysql = new MySqlConnector();
            mysql.getConnection();
            // Updating Query
            String query = "UPDATE query SET name=?,description=?,query=?,accessed=" + timed + ", action='Edited' WHERE id = " + qid;
            PreparedStatement statement = mysql.db.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setString(3, content);
            statement.executeUpdate();
            
            // Inserting Query Log
            query = "INSERT INTO querylog (qid, action, timed) VALUES (" + qid + ",'Edited'," + timed + ")";
            statement = mysql.db.prepareStatement(query);
            statement.executeUpdate();
            
            mysql.closeConnection();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}

}
