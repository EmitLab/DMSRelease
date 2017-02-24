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
import org.json.JSONObject;

import connections.MySqlConnector;
import defaults.ParseRStoJSON;

@WebServlet("/GetPublicationById")
public class GetPublicationById extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public GetPublicationById() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            JSONObject jData = new JSONObject(request.getParameter("jData"));

            int id = jData.getInt("id");

            MySqlConnector mysql = new MySqlConnector();
            mysql.getConnection();

            String query = "SELECT title, tags FROM publications WHERE id = " + id;
            ResultSet rs = mysql.executeQuery(query);
            JSONArray jArr1 = ParseRStoJSON.parseRS(rs);
            
            query = "SELECT projid FROM pubproj WHERE pubid = " + id;
            rs = mysql.executeQuery(query);
            JSONArray jArr2 = ParseRStoJSON.parseRS(rs);
            
            JSONArray jArray = new JSONArray();
            jArray.put(jArr1);
            jArray.put(jArr2);
            
            mysql.closeConnection();

            PrintWriter writer  = response.getWriter();
            writer.println(jArray);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
