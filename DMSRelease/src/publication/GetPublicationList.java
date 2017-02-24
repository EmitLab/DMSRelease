package publication;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import connections.MySqlConnector;
import defaults.ParseRStoJSON;

@WebServlet("/GetPublicationList")
public class GetPublicationList extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public GetPublicationList() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		    String query = "SELECT pubid, pubname, pubtags, projectid as projid, name as projname FROM (SELECT pub.id as pubid, pub.title as pubname, pub.tags as pubtags, pp.projid as projid FROM publications as pub RIGHT JOIN pubproj as pp ON pub.id = pp.pubid) as ppp LEFT JOIN project as proj ON ppp.projid = proj.projectid";
		    
		    MySqlConnector mysql = new MySqlConnector();
		    mysql.getConnection();
		    ResultSet rs = mysql.executeQuery(query);
		    JSONArray jArray = ParseRStoJSON.parseRS(rs);
		    mysql.closeConnection();
		    
		    PrintWriter writer  = response.getWriter();
            writer.println(jArray);
            writer.flush();
		} catch (Exception e){
		    e.printStackTrace();
		}
	}

}
