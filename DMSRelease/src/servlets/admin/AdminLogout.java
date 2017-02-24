package servlets.admin;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import defaults.SessionVariableList;

public class AdminLogout extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public AdminLogout() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(true);
		session.invalidate();
		session = request.getSession();
		session.setAttribute(SessionVariableList.ADMIN_LOGIN_STATE, SessionVariableList.ADMIN_LOGIN_STATES[2]);
		session.setAttribute(SessionVariableList.ADMIN_LOGIN_MESSAGE, SessionVariableList.ADMIN_LOGIN_MESSAGES[2]);
		goToPage("/jsp/LoginAdmin.jsp", request, response);
	}
	private void goToPage(String address, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		//getNamedDispatcher
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(address);
		//System.out.println(dispatcher.toString());
		dispatcher.forward(request, response);
	}
}
