package ensembles;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;

import connections.MySqlConnector;
import defaults.ParseRStoJSON;
import defaults.SessionVariableList;

@WebServlet("/GetQueryLog")
public class GetQueryLog extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public GetQueryLog() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		    HttpSession session = request.getSession();
		    int uid = Integer.parseInt(session.getAttribute(SessionVariableList.USER_ID).toString());
            int pid = Integer.parseInt(session.getAttribute(SessionVariableList.CURRENT_PROJECT_ID).toString());
		    
		    String query = "SELECT ql.qid AS id, q.name, ql.action AS action, ql.timed AS timed " + 
		                    " FROM querylog AS ql LEFT JOIN query AS q ON ql.qid = q.id " + 
		                    " WHERE q.uid = " + uid + " AND q.pid = " + pid + " AND q.visibility = 1";
		    
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
