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

import org.basex.api.client.ClientSession;
import org.json.JSONObject;
import connections.MySqlConnector;
import defaults.Constants;
import defaults.SessionVariableList;

@WebServlet("/CreateEnsembles")
public class CreateEnsembles extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public CreateEnsembles() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doGet(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    try {
	        HttpSession session = request.getSession();
	        JSONObject jData    = new JSONObject(request.getParameter("jData"));
	        JSONObject jResult  = new JSONObject();
	        
	        String ensembleName = jData.getString("name");
	        int status          = jData.getInt("status");
	        int pid             = Integer.parseInt(session.getAttribute(SessionVariableList.CURRENT_PROJECT_ID).toString());
	        int uid             = Integer.parseInt(session.getAttribute(SessionVariableList.USER_ID).toString());
	        
	        /*
	         * SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	         * Date date = new Date(); 
	         * String strDate = dateFormat.format(date);
	         */
	        long timed = System.currentTimeMillis();
	        MySqlConnector mysql = new MySqlConnector();
	        mysql.getConnection();
	        
	        String query = "SELECT id FROM ensembles WHERE name = '" + ensembleName + "' AND pid = " + pid + 
	                        " AND id IN (SELECT eid FROM ensembleuser WHERE uid = " + uid + ");";
	        
	        ResultSet rs = mysql.executeQuery(query);
	        rs.last();
	        int total = rs.getRow();
	        if(total == 0){
	            //query = "INSERT INTO ensembles (name,pid,status,ontime)" + " VALUES ('" + ensembleName + "'," + pid + "," + status + ",'" + strDate + "')";
	            query = "INSERT INTO ensembles (name,pid,status,ontime)" + " VALUES ('" + ensembleName + "'," + pid + "," + status + "," + timed + ")";
	            mysql.insertQuery(query);
	            
	            query = "SELECT LAST_INSERT_ID() as eid";
	            
	            rs = mysql.executeQuery(query);
	            rs.next();
	            int ensembleId = rs.getInt("eid");
	            
	            /* CREATING BASEX DATABASE*/
	            ClientSession cSession = new ClientSession(Constants.BASEX_HOST, Constants.BASEX_PORT, Constants.BASEX_USER, Constants.BASEX_PASS);
	            cSession.execute("create db " + ensembleId);
	            cSession.close();
	            
	            query = "INSERT INTO ensembleuser (eid,uid) VALUES (" + ensembleId + "," + uid + ")";
	            mysql.insertQuery(query);
	            
	            jResult.put("servletState", 1);
                jResult.put("servletStateMessage", "Ensemble created successfully");
                jResult.put("ensembleId", ensembleId);
	        } else {
	            jResult.put("servletState", 0);
	            jResult.put("servletStateMessage", "Ensemble already exists.");
	        }
	        mysql.closeConnection();
	        
	        PrintWriter writer = response.getWriter();
            writer.println(jResult);
            writer.flush();
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}
