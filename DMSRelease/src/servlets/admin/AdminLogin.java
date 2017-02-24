package servlets.admin;

import java.io.IOException;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import connections.MySqlConnector;
import defaults.SessionVariableList;

//@WebServlet("/AdminLogin")
public class AdminLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public AdminLogin() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(true);

		String adminEmail = request.getParameter("adminEmail");
		String adminPwd   = request.getParameter("adminPwd");			
		String query = String.format("select * from adminuser where email ='" + 
						adminEmail + "' and password ='" + adminPwd + "';");
		try {
			MySqlConnector mysql = new MySqlConnector();
			mysql.getConnection();

			ResultSet resultset = mysql.executeQuery(query);
			
			resultset.last();
			int total = resultset.getRow();
			if (total == 1){
				resultset.beforeFirst();
				resultset.next();  
				/* next() must be called before result is read, as beforefirst() 
				 * takes the pointer to header, not to current row. 
				 */
				do{
					// session.setAttribute(SessionVariableList.ADMIN_LOGIN_STATE,   "1");
					session.setAttribute(SessionVariableList.ADMIN_LOGIN_STATE,   SessionVariableList.ADMIN_LOGIN_STATES[1]);
					session.setAttribute(SessionVariableList.ADMIN_LOGIN_MESSAGE, SessionVariableList.ADMIN_LOGIN_MESSAGES[1]);
					session.setAttribute(SessionVariableList.ADMIN_ID,        	  resultset.getInt("userid"));
					session.setAttribute(SessionVariableList.ADMIN_EMAIL,         resultset.getString("email"));
					session.setAttribute(SessionVariableList.ADMIN_PWD,           resultset.getString("password"));
					session.setAttribute(SessionVariableList.ADMIN_FIRST_NAME,    resultset.getString("fName"));
					session.setAttribute(SessionVariableList.ADMIN_LAST_NAME,     resultset.getString("lName"));
					goToPage("/jsp/DashAdmin.jsp", request, response);
				} while (resultset.next());
			} else {
				// If login Fails, redirect to login page.
				session.setAttribute(SessionVariableList.ADMIN_LOGIN_STATE,   SessionVariableList.ADMIN_LOGIN_STATES[0]);
				session.setAttribute(SessionVariableList.ADMIN_LOGIN_MESSAGE, SessionVariableList.ADMIN_LOGIN_MESSAGES[0]);
				goToPage("/jsp/LoginAdmin.jsp", request, response);
			}
			mysql.closeConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	private void goToPage(String address, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(address);
		dispatcher.forward(request, response);
	}
}
