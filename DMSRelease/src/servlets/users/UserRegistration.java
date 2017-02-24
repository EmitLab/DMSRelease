package servlets.users;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import connections.MySqlConnector;
import defaults.SessionVariableList;
import utils.DBUtils;

@WebServlet("/UserRegistration")
public class UserRegistration extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public UserRegistration() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);	
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try
		{
			HttpSession session = request.getSession(true);
			session.removeAttribute(SessionVariableList.USER_LOGIN_MESSAGE);

			String regUserFirstName = request.getParameter("txtRegUserFirstName");
			String regUserLastName = request.getParameter("txtRegUserLastName");
			String regUserEmail = request.getParameter("txtRegUserEmail");
			String regUserPassword = request.getParameter("txtRegUserPassword");
			String regUserProjectList = request.getParameter("projectListRegister"); //To get the project id from the list
			System.out.println("regUserProjectList : "+regUserProjectList);
			String registrationProjectValue   = request.getParameter("hiddenTxtProjectValueRegister");
			int pid = Integer.parseInt(registrationProjectValue);
			int uid = 0;
			int isactivated  = 1;
			int islocked  = 0;
			int acctype  = 0;
		
			//Query to insert into DMS user table
			String dmsuser_query = "INSERT INTO dmsuser (email, password, fname, lname, isactivated, islocked, acctype) VALUES ('"+ regUserEmail +"', '"+ regUserPassword +"', '"+ regUserFirstName +"', '"+ regUserLastName +"', '"+ isactivated +"', '"+ islocked +"', '"+ acctype +"')";
			//Fetch foreign keys from dms user table
			String dmsuser_search_query ="SELECT userid FROM dmsuser where email='"+ regUserEmail +"'";
			MySqlConnector mysql = new MySqlConnector();
			mysql.getConnection();
			//Searches if record in dmsuser exist o0r not. eg. uid=0 means not exist
			uid = DBUtils.findUserId(mysql, dmsuser_search_query);
			if(uid == 0) 
			{
				//Inserts record in dmsuser
				mysql.insertQuery(dmsuser_query);
				
				//Searches record in dmsuser to retrieve uid
				uid = DBUtils.findUserId(mysql, dmsuser_search_query);

				//Query to insert foreign keys into user-proj table 
				String userproj_query = "INSERT INTO userproj (uid, pid) VALUES ('" + uid + "', '" + pid + "')";
				//Inserts record in userproj table
				mysql.insertQuery(userproj_query);
				session.setAttribute(SessionVariableList.USER_REGISTER_SUCCESS_MESSAGE, SessionVariableList.USER_REGISTER_SUCCESS_MESSAGES[0]);
			} else {
				session.setAttribute(SessionVariableList.USER_REGISTER_ERROR_MESSAGE, SessionVariableList.USER_REGISTER_ERROR_MESSAGES[1]);
			}
			mysql.closeConnection();
			response.sendRedirect("/DMS/jsp/LoginUser.jsp");
		} catch(Exception e) {
			HttpSession session = request.getSession(true);
			session.setAttribute(SessionVariableList.USER_REGISTER_ERROR_MESSAGE, SessionVariableList.USER_REGISTER_ERROR_MESSAGES[0]);
			response.sendRedirect("/DMS/jsp/LoginUser.jsp");
			e.printStackTrace();
		}
	}
}
