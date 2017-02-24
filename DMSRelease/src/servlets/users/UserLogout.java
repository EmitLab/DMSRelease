package servlets.users;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import defaults.SessionVariableList;

@WebServlet("/UserLogout")
public class UserLogout extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public UserLogout() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		session.invalidate();
		session = request.getSession();
		session.setAttribute(SessionVariableList.USER_LOGIN_STATE, SessionVariableList.USER_LOGIN_STATES[2]);
		session.setAttribute(SessionVariableList.USER_LOGIN_MESSAGE, SessionVariableList.USER_LOGIN_MESSAGES[2]);
		response.sendRedirect(SessionVariableList.SYSTEM_NAME + "/jsp/LoginUser.jsp");
	}
}
