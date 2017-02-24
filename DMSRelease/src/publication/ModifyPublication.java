package publication;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
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

@WebServlet("/ModifyPublication")
public class ModifyPublication extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public ModifyPublication() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            JSONObject jData = new JSONObject(request.getParameter("jData"));
            int pubid    = jData.getInt("pubid");
            String title = jData.getString("citation");
            String pids  = jData.getString("pids");
            String tags  = jData.getString("tags");
            
            MySqlConnector mysql = new MySqlConnector();
            mysql.getConnection();
            // Updating Query
            String query = "UPDATE publications SET title=?,tags=? WHERE id = " + pubid;
            PreparedStatement statement = mysql.db.prepareStatement(query);
            statement.setString(1, title);
            statement.setString(2, tags);
            statement.executeUpdate();
            
            query = "DELETE FROM pubproj WHERE pubid = " + pubid;
            mysql.updateQuery(query);
            
            String[] pidArr = pids.split(",");
            for (int i = 0; i < pidArr.length; i++){
                query = "INSERT INTO pubproj (pubid, projid) VALUES (" + pubid + "," + Integer.parseInt(pidArr[i]) + ")";
                statement = mysql.db.prepareStatement(query);
                statement.executeUpdate();
            }

            mysql.closeConnection();
            
            PrintWriter writer  = response.getWriter();
            writer.flush();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
