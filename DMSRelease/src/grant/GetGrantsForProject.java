package grant;

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

@WebServlet("/GetGrantsForProject")
public class GetGrantsForProject extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public GetGrantsForProject() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(true);
            int pid = Integer.parseInt(session.getAttribute(SessionVariableList.CURRENT_PROJECT_ID).toString());
            String query = "SELECT name, link FROM grants WHERE pid = " + pid;

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
