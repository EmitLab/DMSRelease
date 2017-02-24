package servlets.admin;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import connections.MySqlConnector;

public class Log404Errors extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public Log404Errors() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try{
			JSONObject jObject = new JSONObject(request.getParameter("jData"));
			String url404 = jObject.getString("url");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date(); 
			String strDate = dateFormat.format(date);
			
			String query = "INSERT INTO log404errors(link, ontime, resolve) VALUES ('" + url404 + "','" + strDate + "'," + 0 + ");";
			
			MySqlConnector mysql = new MySqlConnector();
			mysql.getConnection();
			mysql.insertQuery(query);
			mysql.closeConnection();
			
		} catch(Exception e){
			e.printStackTrace();
		}
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}
}