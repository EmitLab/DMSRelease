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
import utils.DBUtils;

/**
 * Servlet implementation class socialLogin
 */
@WebServlet("/socialLogin")
public class socialLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public socialLogin() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
		JSONObject obj = new JSONObject();
		try{
			//Fetch data sent with ajax request
            JSONObject jData = new JSONObject(request.getParameter("jData"));
            int acctype = jData.getInt("accountType");  //accountType for facebook = 1 or Google = 2
            System.out.println("accountType ="+acctype);
            // int acctype = 1; //For facebook
            String pid = jData.getString("loginProjectID");
            
            String socialLoginId="", socialLoginName="", socialLoginEmail="", socialFirstName="", socialLastName="";
            if(acctype==1)
            {
            	 socialLoginId = jData.getString("fbId");
                 socialLoginName = jData.getString("fbName");		
                 socialFirstName = jData.getString("fbFirstName");
                 socialLastName = jData.getString("fbLastName");
                 socialLoginEmail = jData.getString("fbEmail");
            }
            else if(acctype==2)
            {
            	socialLoginId = jData.getString("googleId");
                socialLoginName = jData.getString("googleName");
                socialLoginEmail = jData.getString("googleEmail");
                socialFirstName = jData.getString("googleFirstName");
                socialLastName = jData.getString("googleLastName");
            }
            System.out.println("socialFirstName ="+socialFirstName);
            System.out.println("socialLastName ="+socialLastName);
           
   		 	HttpSession session = request.getSession(true);
            String dmsUserQuery = String.format("select * from dms.dmsuser where email = '"+ socialLoginEmail +"' and acctype = "+ acctype +";");
            
            //TODO: Code to store new account info in DMS db
            
            try {
    			MySqlConnector mysql = new MySqlConnector();
    			mysql.getConnection();
    			
    			ResultSet resultset = mysql.executeQuery(dmsUserQuery);
    			//if(resultset!=null)
    			resultset.last();
    			int total = resultset.getRow();
    			if (total == 1){  //Facebook entry found
    				resultset.beforeFirst();
    				resultset.next();  
    				/* next() must be called before result is read, as beforefirst() 
    				 * takes the pointer to header, not to current row. 
    				 */
    				
    				/* START: Check if user is associated with the project */
    				String uid = resultset.getString("userid");
    				String userProjQuery = String.format("select * from dms.userproj where uid = '" + uid + "' and pid ='" + pid + "';");
    				
    				ResultSet resultsetUserProjQuery = mysql.executeQuery(userProjQuery);
    				
    				resultsetUserProjQuery.last();
    				int projectAssociationCount = resultsetUserProjQuery.getRow();
    				if (projectAssociationCount == 0)
    				{
    					System.out.println("User not associated with this proj. adding new entry with association.");
    					// If project not associated with user then associate it 
    					//Query to insert foreign keys into user-proj table 
        				String userproj_query =  String.format("INSERT INTO userproj (uid, pid) VALUES ('" + uid + "', '" + pid + "')");
        				//Inserts record in userproj table
        				mysql.insertQuery(userproj_query);
    				}

    				/* END: Check if user is associated with the project */
    			
    				/* START: Session Variables for logging into system */
    				session.setAttribute(SessionVariableList.USER_LOGIN_STATE,     SessionVariableList.USER_LOGIN_STATES[1]);
					session.setAttribute(SessionVariableList.USER_LOGIN_MESSAGE,   SessionVariableList.USER_LOGIN_MESSAGES[1]);
					session.setAttribute(SessionVariableList.USER_ACCOUNT_TYPE,    SessionVariableList.USER_ACCOUNT_TYPES[2]);
					session.setAttribute(SessionVariableList.USER_ID,        	   resultset.getString("userid"));
					session.setAttribute(SessionVariableList.USER_EMAIL,           resultset.getString("email"));
					session.setAttribute(SessionVariableList.USER_PWD,             resultset.getString("password"));
					session.setAttribute(SessionVariableList.USER_FIRST_NAME,      resultset.getString("fName"));
					session.setAttribute(SessionVariableList.USER_LAST_NAME,       resultset.getString("lName"));
					session.setAttribute(SessionVariableList.CURRENT_PROJECT_ID,   pid);
					/* END: Session Variables for logging into system */
					obj.put("status","success");
    			}
    			else //Facebook login entry not found. new facebook user flow
    			{
    				int isactivated  = 1;
    				int islocked  = 0;
    				String defaultPassword = "default";
    				String dmsuser_insert_query =  String.format("INSERT INTO dmsuser (email, password, fname, lname, isactivated, islocked, acctype) VALUES ('"+ socialLoginEmail +"', '"+ defaultPassword +"', '"+ socialFirstName +"', '"+ socialLastName +"', '"+ isactivated +"', '"+ islocked +"', '"+ acctype +"')");
    				String dmsuser_search_query = String.format("SELECT userid FROM dmsuser where email='"+ socialLoginEmail +"' and acctype = "+ acctype +";");
    				
    				//Inserts record in dmsuser
    				mysql.insertQuery(dmsuser_insert_query);
    				
    				//Searches record in dmsuser to retrieve uid
    				int uid = DBUtils.findUserId(mysql, dmsuser_search_query);

    				//Query to insert foreign keys into user-proj table 
    				String userproj_query =  String.format("INSERT INTO userproj (uid, pid) VALUES ('" + uid + "', '" + pid + "')");
    				//Inserts record in userproj table
    				mysql.insertQuery(userproj_query);
    				
    				/* START: Session Variables for logging into system */
    				session.setAttribute(SessionVariableList.USER_LOGIN_STATE,     SessionVariableList.USER_LOGIN_STATES[1]);
					session.setAttribute(SessionVariableList.USER_LOGIN_MESSAGE,   SessionVariableList.USER_LOGIN_MESSAGES[1]);
					session.setAttribute(SessionVariableList.USER_ACCOUNT_TYPE,    SessionVariableList.USER_ACCOUNT_TYPES[2]);
					session.setAttribute(SessionVariableList.USER_ID,        	   uid);
					session.setAttribute(SessionVariableList.USER_EMAIL,           socialLoginEmail);
					session.setAttribute(SessionVariableList.USER_PWD,             defaultPassword);
					session.setAttribute(SessionVariableList.USER_FIRST_NAME,      socialLoginName);
					session.setAttribute(SessionVariableList.USER_LAST_NAME,       socialLoginName);
					session.setAttribute(SessionVariableList.CURRENT_PROJECT_ID,   pid);
					/* END: Session Variables for logging into system */
					obj.put("status","success");
    			}
            }
            catch(SQLException e)
            {
            	e.printStackTrace();
            	// If login Fails, redirect to login page.
                session.setAttribute(SessionVariableList.USER_LOGIN_STATE,   SessionVariableList.USER_LOGIN_STATES[0]);
                session.setAttribute(SessionVariableList.USER_LOGIN_MESSAGE, SessionVariableList.USER_LOGIN_MESSAGES[7]);
                obj.put("status","fail");
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
