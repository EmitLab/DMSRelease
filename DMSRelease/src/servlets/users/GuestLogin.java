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

import org.json.JSONObject;

import connections.MySqlConnector;
import defaults.SessionVariableList;

@WebServlet("/GuestLogin")
public class GuestLogin extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public GuestLogin() {
        super();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            JSONObject jData = new JSONObject(request.getParameter("jData"));
            String email = jData.getString("email");
            String pid = jData.getString("loginProjectID");
            JSONObject obj = new JSONObject();

            HttpSession session = request.getSession(true);

            String dmsUserQuery = String.format("select * from dmsuser where email = '" + email + "';");

            try {
                MySqlConnector mysql = new MySqlConnector();
                mysql.getConnection();
                ResultSet resultset = mysql.executeQuery(dmsUserQuery);

                resultset.last();
                int total = resultset.getRow();
                if (total == 1){
                    System.out.println("*********User found....");
                    resultset.beforeFirst();
                    resultset.next();  
                    /* next() must be called before result is read, as beforefirst() 
                     * takes the pointer to header, not to current row. 
                     */
                    /* START: Session Variables for logging into system */
                    session.setAttribute(SessionVariableList.USER_LOGIN_STATE,     SessionVariableList.USER_LOGIN_STATES[1]);
                    session.setAttribute(SessionVariableList.USER_LOGIN_MESSAGE,   SessionVariableList.USER_LOGIN_MESSAGES[1]);
                    session.setAttribute(SessionVariableList.USER_ACCOUNT_TYPE,    SessionVariableList.USER_ACCOUNT_TYPES[1]);
                    session.setAttribute(SessionVariableList.USER_ID,        	   resultset.getString("userid"));
                    session.setAttribute(SessionVariableList.USER_EMAIL,           resultset.getString("email"));
                    session.setAttribute(SessionVariableList.USER_PWD,             resultset.getString("password"));
                    session.setAttribute(SessionVariableList.USER_FIRST_NAME,      resultset.getString("fName"));
                    session.setAttribute(SessionVariableList.USER_LAST_NAME,       resultset.getString("lName"));
                    session.setAttribute(SessionVariableList.CURRENT_PROJECT_ID,   pid);
                    obj.put("status","success");
                } else {
                    // If login Fails, redirect to login page.
                    session.setAttribute(SessionVariableList.USER_LOGIN_STATE,   SessionVariableList.USER_LOGIN_STATES[0]);
                    session.setAttribute(SessionVariableList.USER_LOGIN_MESSAGE, SessionVariableList.USER_LOGIN_MESSAGES[6]);
                    obj.put("status","fail");
                }
                mysql.closeConnection();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(obj.toString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
