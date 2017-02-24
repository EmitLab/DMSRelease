package servlets.admin;

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
import org.json.JSONObject;

import connections.MySqlConnector;
import defaults.ParseRStoJSON;
import defaults.SessionVariableList;

@WebServlet("/InitProjectPage")
public class InitProjectPage extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public InitProjectPage() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            JSONObject jObject = new JSONObject();
            JSONArray  jArray  = new JSONArray();
            ResultSet rs = null ;
            MySqlConnector mysql = new MySqlConnector();
            mysql.getConnection();
            
            HttpSession session = request.getSession(true);
            int pid = Integer.parseInt(session.getAttribute(SessionVariableList.CURRENT_PROJECT_ID).toString());        
            int uid = Integer.parseInt(session.getAttribute(SessionVariableList.USER_ID).toString());  
            
            String query = "SELECT * FROM project WHERE projectid = " + pid + ";";
            
            rs = mysql.executeQuery(query);
            jArray = ParseRStoJSON.parseRS(rs);
            jObject.put("project", jArray);
            
            query = "SELECT * FROM team where id in (SELECT teamid from teamproj where projid = "+ pid +")";
            rs = mysql.executeQuery(query);
            jArray = ParseRStoJSON.parseRS(rs);
            jObject.put("team", jArray);
            
            query = "SELECT id, name FROM ensembles WHERE pid = " + pid + " AND id IN (SELECT eid FROM ensembleuser WHERE uid = " + uid + ");";
            rs = mysql.executeQuery(query);
            jArray = ParseRStoJSON.parseRS(rs);
            jObject.put("ensemble", jArray);
            
            query = "SELECT id, name, created as timed FROM query WHERE uid = " + uid + " AND pid = " + pid + " AND visibility = 1 ;";
            rs = mysql.executeQuery(query);
            jArray = ParseRStoJSON.parseRS(rs);
            jObject.put("query", jArray);
            
            query = "SELECT title FROM publications WHERE id in ( SELECT pubid FROM pubproj WHERE projid = " + pid + ");";
            rs = mysql.executeQuery(query);
            jArray = ParseRStoJSON.parseRS(rs);
            jObject.put("publication", jArray);
            
            mysql.closeConnection();
            PrintWriter writer = response.getWriter();
            writer.println(jObject);
            writer.flush();
        } catch (Exception e){
            e.printStackTrace();
            response.sendRedirect("/DMS/jsp/LoginUser.jsp");
        } 
	}  
}
