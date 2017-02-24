package servlets.users;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import connections.MySqlConnector;
import defaults.SessionVariableList;

@WebServlet("/UserLogin")
public class UserLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public UserLogin() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		session.removeAttribute(SessionVariableList.USER_REGISTER_ERROR_MESSAGE);
		
		String loginUsername     = request.getParameter("loginUsername");
		String loginPassword     = request.getParameter("loginPassword");	
		//String loginProject      = request.getParameter("btnProjectListLogin");	
		String loginProjectValue = request.getParameter("hiddenTxtProjectValueLogin");
		String dmsUserQuery      = String.format("select * from dmsuser where email ='" + loginUsername + "' and password ='" + loginPassword + "';");
		String pid = loginProjectValue;
		String uid;
		
		try {
			MySqlConnector mysql = new MySqlConnector();
			mysql.getConnection();
			
			ResultSet resultset = mysql.executeQuery(dmsUserQuery);
			
			resultset.last();
			int total = resultset.getRow();
			if (total == 1){
				resultset.beforeFirst();
				resultset.next();  
				/* next() must be called before result is read, as beforefirst() 
				 * takes the pointer to header, not to current row. 
				 */
				
				uid = resultset.getString("userid");
				String userProjQuery = String.format("select * from dms.userproj where uid = '" + uid + "' and pid ='" + pid + "';");
				
				ResultSet resultsetUserProjQuery = mysql.executeQuery(userProjQuery);
				
				resultsetUserProjQuery.last();
				int totalProjects = resultsetUserProjQuery.getRow();
				if (totalProjects == 1){
					resultsetUserProjQuery.beforeFirst();
					resultsetUserProjQuery.next();  
					
					String queryProjName = "SELECT name FROM project WHERE projectid = " + pid;
		            ResultSet projName = mysql.executeQuery(queryProjName);
		            projName.next();
		            
					session.setAttribute(SessionVariableList.USER_LOGIN_STATE,     SessionVariableList.USER_LOGIN_STATES[1]);
					session.setAttribute(SessionVariableList.USER_LOGIN_MESSAGE,   SessionVariableList.USER_LOGIN_MESSAGES[1]);
					session.setAttribute(SessionVariableList.USER_ACCOUNT_TYPE,    SessionVariableList.USER_ACCOUNT_TYPES[0]);
					session.setAttribute(SessionVariableList.USER_ID,        	   resultset.getString("userid"));
					session.setAttribute(SessionVariableList.USER_EMAIL,           resultset.getString("email"));
					session.setAttribute(SessionVariableList.USER_PWD,             resultset.getString("password"));
					session.setAttribute(SessionVariableList.USER_FIRST_NAME,      resultset.getString("fName"));
					session.setAttribute(SessionVariableList.USER_LAST_NAME,       resultset.getString("lName"));
					session.setAttribute(SessionVariableList.CURRENT_PROJECT_ID,   resultsetUserProjQuery.getString("pid"));
					session.setAttribute(SessionVariableList.CURRENT_PROJECT_NAME, projName.getString("name"));
					response.sendRedirect("/DMS/jsp/project.jsp");
				}
				else
				{
					// If login Fails, redirect to login page.
					session.setAttribute(SessionVariableList.USER_LOGIN_STATE,   SessionVariableList.USER_LOGIN_STATES[5]);
					session.setAttribute(SessionVariableList.USER_LOGIN_MESSAGE, SessionVariableList.USER_LOGIN_MESSAGES[5]);
					response.sendRedirect("/DMS/jsp/LoginUser.jsp");
				}
			} else {
				// If login Fails, redirect to login page.
				session.setAttribute(SessionVariableList.USER_LOGIN_STATE,   SessionVariableList.USER_LOGIN_STATES[0]);
				session.setAttribute(SessionVariableList.USER_LOGIN_MESSAGE, SessionVariableList.USER_LOGIN_MESSAGES[0]);
				response.sendRedirect("/DMS/jsp/LoginUser.jsp");
			}
			mysql.closeConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
}
