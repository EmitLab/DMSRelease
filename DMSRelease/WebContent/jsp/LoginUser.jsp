<%@page import="defaults.SessionVariableList"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<% 
	//if(session.getAttribute("AdminLoginState") == null || session.getAttribute("AdminLoginState").equals("0")){
	if(session.getAttribute(SessionVariableList.USER_LOGIN_STATE) == null || 
			!session.getAttribute(SessionVariableList.USER_LOGIN_STATE).equals(SessionVariableList.USER_LOGIN_STATES[1])){
%>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Data Management System</title>
		<meta name="google-signin-client_id" content="388189875333-llafleon4sb6sqecp3if5hoj5vtqvum4.apps.googleusercontent.com">
		<title>Data Management System</title>
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
		
		<script src="https://apis.google.com/js/platform.js" async defer></script> <!-- For Google Signin -->
		<link rel="stylesheet" type="text/css" href="http://fonts.googleapis.com/css?family=Roboto:100,100i,400">
	
		<!-- Facebook Signin init -->
		<script>
			  window.fbAsyncInit = function() {
			    FB.init({
			      appId      : '1552899854727075',
			      xfbml      : true,
			      version    : 'v2.8'
			    });
			    
			 
			  }; //END: window.fbAsyncInit
			 

			  (function(d, s, id){
			     var js, fjs = d.getElementsByTagName(s)[0];
			     if (d.getElementById(id)) {return;}
			     js = d.createElement(s); js.id = id;
			     js.src = "//connect.facebook.net/en_US/sdk.js";
			     fjs.parentNode.insertBefore(js, fjs);
			   }(document, 'script', 'facebook-jssdk'));
		</script>
	
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
		<link rel='stylesheet prefetch' href='http://fonts.googleapis.com/css?family=Roboto:400,100,300,500,700,900|RobotoDraft:400,100,300,500,700,900'>
		<link rel='stylesheet prefetch' href='http://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css'>
		
		<link rel="stylesheet" href="/DMS/LoginUser/css/reset.css">
		<link rel="stylesheet" href="/DMS/LoginUser/css/style.css">
		
		<link href="/DMS/LoginUser/css/vLmRVp.css" rel="stylesheet">
		<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
		
		<script src="/DMS/js/sys/jquery-3.1.1.min.js"></script>
		<script src="/DMS/js/dev/ProjectOperations.js"></script>
		
		<link href="/DMS/css/sys/multiselect.css" rel="stylesheet">
		<script src="/DMS/js/sys/multiselect.js"></script>
		
		<script src="/DMS/js/dev/loginOptionsHandler.js"></script>
		
		<script>
		
		$(function(){

			//Function to load project list 
			listProjectonLoginWindow();
			
			<%
			//System.out.println("------errorMessageIndex----- ="+errorMessageIndex);
			if(session.getAttribute(SessionVariableList.USER_LOGIN_STATE) != null)
			{
			%>
			
				var errorMsg = '<%=session.getAttribute(SessionVariableList.USER_LOGIN_MESSAGE)%>';
				console.log('e'+errorMsg);
				if(errorMsg!='null'){
					$("#lblLoginFormErrorMsg").addClass('alert alert-danger');
					$("#lblLoginFormErrorMsg").text(errorMsg);
					console.log("Here 1");
					if (errorMsg === "User not associated with this project"){
						console.log("Here 2");
						$("#btnProjectListLogin").attr("style","border:2px solid firebrick !important");
					}
				}
				else
				{
					$("#lblLoginFormErrorMsg").removeClass('alert alert-danger');
				}
				
			<%
			}
			%> 
			
			<%
			if(session.getAttribute(SessionVariableList.USER_LOGIN_STATE) != null)
			{
			%>
				var registerFormErrorMsg = '<%=session.getAttribute(SessionVariableList.USER_REGISTER_ERROR_MESSAGE)%>';
				console.log('r'+registerFormErrorMsg);
				if(registerFormErrorMsg!='null'){
					$("#lblRegisterFormErrorMsg").addClass('alert alert-danger');
					$("#lblRegisterFormErrorMsg").text(registerFormErrorMsg);
				}
				else
				{
					$("#lblRegisterFormErrorMsg").removeClass('alert alert-danger');
				}
				
			<%
			}
			%> 
				

		

			//Function to select project in dropdown on Login form
			$("#projectListLogin").on('click', 'li a', function(){
		
			      $("#btnProjectListLogin").text($(this).text());
			      $("#btnProjectListLogin").val($(this).text());
			      $("#hiddenTxtProjectValueLogin").val($(this).parent()[0].id); //To assign the id which contains the id of userProj table
			      //console.log($(this).parent()[0].id);
	
		   	});
			
			//Function to select project in dropdown on Register form
			$("#projectListRegister").on('click', 'li a', function(){

			      $("#btnProjectListRegister").text($(this).text());
			      $("#btnProjectListRegister").val($(this).text());
			      $("#hiddenTxtProjectValueRegister").val($(this).parent()[0].id); //To assign the id which contains the id of userProj table
				    
			   });
			
			//Function to select project in dropdown on Register form
			$("#btnGuestAccess").on('click', function(){
				guestLoginAjax();
				    
			   });
			

		});
		
		</script>
		
	
		<style>
			
			.isa_info {
			    color: #00529B;
			    background-color: #BDE5F8;
			}
			.isa_success {
			    color: #4F8A10;
			    background-color: #DFF2BF;
			}
			.isa_warning {
			    color: #9F6000;
			    background-color: #FEEFB3;
			}
			.isa_error {
			    color: #D8000C;
			    background-color: #FFBABA;
			}
			.isa_error_register {
			    color: #D8000C;
			    background-color: #FFBABA;
			    align: center;
			}
	
		</style>
		
	</head>

<body>

	<!-- Form Mixin-->
	<!-- Input Mixin-->
	<!-- Button Mixin-->
	<!-- Pen Title-->
	<div class="pen-title">
		<h1>Data Management System for Multi-Variate Time Series</h1>
		<span>
			Developed <i class='fa fa-code'></i> by <a href='http://aria.asu.edu/candan/'>EMITLab</a>
		</span>
	</div>
	<!-- Form Module-->
	<div class="module form-module">
		<div class="toggle" style="padding-top:8px;">
			<i class="fa fa-times fa-pencil" data-toggle="tooltip" data-placement="top" title="Register"></i>
			
			<!--  <div class="tooltip">Register</div> -->
		
		</div>
		<div class="form">
			<h2>Login to your account</h2>
			<form name="loginForm" method="POST" action="/DMS/UserLogin">
				<div class="dropup">
  					<button class="btn btn-default dropdown-toggle" type="button" data-toggle="dropdown" id="btnProjectListLogin" name="btnProjectListLogin">Select a system
  					<span class="caret"></span></button>
  					<ul class="dropdown-menu" id="projectListLogin">
    					<!-- 
    					<li class="divider"><a href="#"></a></li>
    					<li><a href="#">Learn More</a></li>
    					-->
  					</ul>
				</div>
				
				<br/><br/>
				<input type="hidden" id="hiddenTxtProjectValueLogin" name="hiddenTxtProjectValueLogin" />
				<input type="text" placeholder="Username" id="loginUsername" name="loginUsername" /> 
				<input type="password" placeholder="Password" id="loginPassword" name="loginPassword" />
				<button>
					Login 
					<span class="pullright">
						<i class="fa fa-arrow-right"></i>
					</span>
				</button>
			</form>
			<div align="center">
			<a href="/DMS/ForgotPassword/" class="btn btn-link">Forgot your password?</a><br>
			<div id="lblLoginFormErrorMsg"></div>
			</div>
		</div>
		<div class="form">
			<h2>Create an account</h2>
			<form name="loginForm" method="POST" action="/DMS/UserRegistration">
				<div class="dropup">
  					<button class="btn btn-default dropdown-toggle" type="button" data-toggle="dropdown" id="btnProjectListRegister">Select a system
  					<span class="caret"></span></button>
  					<ul class="dropdown-menu" id="projectListRegister">
  						<!--
    					<li class="divider"><a href="#"></a></li>
    					<li><a href="#">Learn More</a></li>
    					-->
  					</ul>
				</div>
				<br/><br/>
				<input type="text" style="display:none;"/>
				<input type="hidden" id="hiddenTxtProjectValueRegister" name="hiddenTxtProjectValueRegister" />
				<input type="text" placeholder="First Name" id="txtRegUserFirstName" name="txtRegUserFirstName" /> 
				<input type="text" placeholder="Last Name" id="txtRegUserLastName" name="txtRegUserLastName" /> 
				<input type="email" placeholder="Email Address" id="txtRegUserEmail" name="txtRegUserEmail" /> 
				<input type="password" placeholder="Password" id="txtRegUserPassword" name="txtRegUserPassword" />
				
				<button>Register</button><br>
			</form>
			<br>
			<div id="lblRegisterFormErrorMsg"></div>
		</div>
		
			
		<div class="cta">
			<a class="btn btn-default" id="btnGuestAccess">Guest Access</a>
		</div>
		<div class="cta">
			Login with<br/><br/>
			  <!-- <a class="btn btn-default" style="width:45%" onclick="loginfb()">Facebook</a> --> 
			<div class="fb-login-button" data-max-rows="1" data-size="large" data-show-faces="false" data-auto-logout-link="false" data-scope="email" onlogin="facebookSignIn"></div>
			<br><br><div class="g-signin2" data-onsuccess="onSignIn" align="center"></div>
		</div>
	</div>
	
	<!--<script src='http://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js'></script>-->
	<script src='/DMS/LoginUser/js/vLmRVp.js'></script>
	<script src="/DMS/LoginUser/js/index.js"></script>
	<script>
$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip(); 
});
</script>
</body>
</html>
<% } else{
	String redirectURL = "/DMS/jsp/project.jsp";
    response.sendRedirect(redirectURL);
} %>