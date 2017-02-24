package servlets.users;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import defaults.SessionVariableList; 

public class ChangeUserProject extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public ChangeUserProject() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	      doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    try {
	        String targetProject = request.getParameter("pid");
	        String targetURL = "/DMS" + request.getParameter("currentURL");
	        HttpSession session = request.getSession();
	        session.setAttribute(SessionVariableList.CURRENT_PROJECT_ID, targetProject);
	        response.sendRedirect(targetURL);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
