<%@page import="defaults.SessionVariableList"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<% 
	//if(session.getAttribute("AdminLoginState") == null || session.getAttribute("AdminLoginState").equals("0")){
	if(session.getAttribute(SessionVariableList.ADMIN_LOGIN_STATE) == null || 
			!session.getAttribute(SessionVariableList.ADMIN_LOGIN_STATE).equals(SessionVariableList.ADMIN_LOGIN_STATES[1])){
%>
<html>
<head>
<meta charset="UTF-8">
<title>DMS : Gateway</title>

	<link rel="stylesheet prefetch" href="/DMS/css/sys/bootstrap.min.css">
	<link rel='stylesheet prefetch' 
		href='http://fonts.googleapis.com/css?family=Roboto:400,100,300,500,700,900|RobotoDraft:400,100,300,500,700,900'>
	<link rel='stylesheet prefetch' 
		href='http://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css'>
	<link rel="stylesheet" href="/DMS/LoginAdmin/css/style.css">
	<script src="/DMS/js/sys/jquery-3.1.1.min.js"></script>
</head>

<body>

<div class="pen-title">
		<h1>Data Management System for Multi-Variate Time Series</h1>
		<span>
			Developed <i class='fa fa-code'></i> by <a href='http://aria.asu.edu/candan/'>EMITLab</a>
		</span>
	</div>
	
	<div class="wrapper">
		<form class="form-signin" name="loginForm" method="POST" action="/DMS/AdminLogin" >
			<h4 class="form-signin-heading btn-link" style="cursor: default; text-decoration: none;">
				Login to your account
			</h4>
			<div class="form-group"> 
				<input type="email" class="form-control" id="adminEmail" name="adminEmail" 
					placeholder="Enter email" required=""> 
			</div>
			<div class="form-group">
				<input type="password" class="form-control" name="adminPwd" 
					placeholder="Enter Password" required="" /> 
			</div>
			<button class="btn btn-lg btn-primary btn-block" type="submit">Login</button>
			<br/>
			<div class="alert alert-danger" name="message" style="display: none;" id="errorMessage">
				You need to login to proceed.
			</div>
			<script>
				var servletState = '<%= session.getAttribute(SessionVariableList.ADMIN_LOGIN_STATE)%>';
				var message      = '<%= session.getAttribute(SessionVariableList.ADMIN_LOGIN_MESSAGE)%>';
				if (servletState == 0){
					document.getElementById("errorMessage").innerHTML = message;
					document.getElementById("errorMessage").style.display = "block";
				} else {
					if (servletState == 1){
						document.getElementById("errorMessage").innerHTML = message;
						document.getElementById("errorMessage").setAttribute("class","alert alert-success");
						document.getElementById("errorMessage").style.display = "block";
					}
				}
				document.getElementById("errorMessage").innerHTML = message;
                document.getElementById("errorMessage").style.display = "block";
			</script>
		</form>
	</div>
</body>
</html>
<% } else{
	String redirectURL = "/DMS/jsp/DashAdmin.jsp";
    response.sendRedirect(redirectURL);
} %>
