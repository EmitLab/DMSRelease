package defaults;

public class SessionVariableList {
	
	/*
	 * When writing a servlet : always write code in doPost method.
	 * In case, the servlet call go to, by default, doGet() mwthod - 
	 * consider adding a doPost(request, response) in the bogy of doGet() method.s  
	 */
	
	/*MANDATORY SESSION VARIABLES FOR EACH SERVLET*/
	public static String SYSTEM_NAME = "/DMS";
	
	public static String SERVLET_STATE         = "servletSate";
	public static String SERVLET_STATE_MESSAGE = "servletStateMessage";
	// ADMIN LOGIN STATES and MESSAGES
	public static String   ADMIN_LOGIN_STATE    = "adminLoginState";
	public static String[] ADMIN_LOGIN_STATES   = {"NOT_LOGGED", "LOGGEDIN", "LOGGEDOUT"};
	public static String   ADMIN_LOGIN_MESSAGE  = "adminloginMessage"; 
	public static String[] ADMIN_LOGIN_MESSAGES = {"No User Detected", "User Logged In", "User Logged Out"};
	
	// USER LOGIN STATES and MESSAGES
	public static String   USER_LOGIN_STATE    = "userLoginState";
	public static String[] USER_LOGIN_STATES = {"NOT_LOGGED", "LOGGED_IN", "LOGGED_OUT", "IS_ACTIVATED", "IS_BLOCKED", "IS_NOT_ASSOCIATED"};
	public static String   USER_ACCOUNT_TYPE   = "userAccountType";
	public static String[] USER_ACCOUNT_TYPES   = {"STANDARD", "GUEST", "FACEBOOK", "GOOGLE"};
	public static String   USER_LOGIN_MESSAGE  = "userLoginMessage"; 
	public static String[] USER_LOGIN_MESSAGES = {"Invalid login credentials", "User Logged In", "User Logged Out", "User activated", "User blocked","User not associated with this project", "Guest Login failed due to invalid internal credentials", "Error occured while logging into system"};
	
	//USER REGISTER MSG	
	public static String   USER_REGISTER_ERROR_MESSAGE  = "userRegisterErrorMessage"; 
	public static String[] USER_REGISTER_ERROR_MESSAGES = {"Error in User Registration", "User Account already exist"};
	public static String   USER_REGISTER_SUCCESS_MESSAGE  = "userRegisterSuccessMessage"; 
	public static String[] USER_REGISTER_SUCCESS_MESSAGES = {"Account created succesfully"};

	
	// CURRENT PROJECT
	public static String CURRENT_PROJECT_ID   = "project";
	public static String CURRENT_PROJECT_NAME = "projectName";
	
	/* ADMIN SESSION VARIABLES*/	
	public static String ADMIN_ID         = "adminId";
	public static String ADMIN_EMAIL      = "adminEmail";
	public static String ADMIN_PWD        = "adminPwd";
	public static String ADMIN_FIRST_NAME = "adminFirstName";
	public static String ADMIN_LAST_NAME  = "adminLastName";
	
	/* USER SESSION VARIABLES*/	
	public static String USER_ID         = "userId";       // Don't Change
	public static String USER_EMAIL      = "userEmail";
	public static String USER_PWD        = "userPwd";
	public static String USER_FIRST_NAME = "userFirstName";
	public static String USER_LAST_NAME  = "userLastName";
}
