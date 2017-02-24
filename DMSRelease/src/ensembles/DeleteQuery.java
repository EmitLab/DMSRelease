package ensembles;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import connections.MySqlConnector;

@WebServlet("/DeleteQuery")
public class DeleteQuery extends HttpServlet {
	private static final long serialVersionUID = 1L;
 
    public DeleteQuery() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		    JSONObject jData = new JSONObject(request.getParameter("jData"));
            String qid       = jData.getString("qid");
            
            MySqlConnector mysql = new MySqlConnector();
            mysql.getConnection();
            
            String query = "UPDATE query SET visibility = 0 WHERE id = " + qid;
            mysql.updateQuery(query);
            
            query = "UPDATE querylog SET visibility = 0 WHERE qid = " + qid;
            mysql.updateQuery(query);
            
            mysql.closeConnection();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}

}
