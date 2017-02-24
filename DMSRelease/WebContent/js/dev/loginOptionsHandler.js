/**
 * This JS handles ajax flows for Guest login, Google login and facebook login
 * 
 */

/*Handles guest Login Access*/
function guestLoginAjax(){
		console.log("In guest login Ajax");
		var projectID=$("#hiddenTxtProjectValueLogin").val();
		if(projectID=="")
		{
			var errorMsg = "Please select a system/project from dropdown list";
			console.log(errorMsg);
			$("#lblLoginFormErrorMsg").addClass('alert alert-danger');
			$("#lblLoginFormErrorMsg").text(errorMsg);
		}
		else
		{
		
			$("#lblLoginFormErrorMsg").removeClass('alert alert-danger');
			$.ajax({
				type:"POST",
				url: "/DMS/GuestLogin",
				data: {jData : JSON.stringify({email:"guest@emitlab.edu", loginProjectID:  projectID})},
				success : function(data){
					console.log(data);
					if(data["status"] == "success")
					{
						console.log("Accessing Dashboard"); //Using session variables set loginUser.jsp will call DashUser.jsp
						window.location = "/DMS/jsp/LoginUser.jsp";
					}
					else
					{
						console.log("");
						window.location = "/DMS/jsp/LoginUser.jsp";
					}
					//$("#newEmail").css("border", "1px solid green");
					
				},
				error : function(){
					console.log(error);
					//console.error("Oops! Something went wrong.");
					$("#newEmail").css("border", "1px solid firebrick");
				}
			}); //END:$.ajax
	} //END: else part of if(projectID=="")
}

// For Google SignIn
function onSignIn(googleUser) {
		
	  //Fetch Profile Info
	  var profile = googleUser.getBasicProfile();
	  console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
	  console.log('Name: ' + profile.getGivenName());
	  console.log('Family Name: ' + profile.getFamilyName());
	  console.log('Image URL: ' + profile.getImageUrl());
	  console.log('Email: ' + profile.getEmail());
	  
	  //Signout from google login
	  var auth2 = gapi.auth2.getAuthInstance();
	  auth2.signOut().then(function () {
	      console.log('User signed out.');
		});
	  
	   console.log("In google login Ajax");
		var projectID=$("#hiddenTxtProjectValueLogin").val();
		if(projectID=="")
		{
			var errorMsg = "Please select a system/project from dropdown list";
			console.log(errorMsg);
			$("#lblLoginFormErrorMsg").addClass('alert alert-danger');
			$("#lblLoginFormErrorMsg").text(errorMsg);
		}
		else
		{
		
			$("#lblLoginFormErrorMsg").removeClass('alert alert-danger');
		
		  //Establish session with Google profile info //For google login accountType = 2 is required and servlet end to make decision 
			$.ajax({
				type:"POST",
				url: "/DMS/socialLogin",
				data: {jData : JSON.stringify({accountType: 2, loginProjectID: projectID, googleId: profile.getId(), googleFirstName: profile.getGivenName(), googleLastName: profile.getFamilyName(), googleName: profile.getName(), googleEmail: profile.getEmail()})},
				success : function(data){
					console.log(data);
					if(data["status"] == "success")
					{
						console.log("Accessing Dashboard"); //Using session variables set loginUser.jsp will call DashUser.jsp
						window.location = "/DMS/jsp/LoginUser.jsp";
					}
					else
					{
						console.log("");
						window.location = "/DMS/jsp/LoginUser.jsp";
					}
					//$("#newEmail").css("border", "1px solid green");
					
				},
				error : function(){
					console.log(error);
					//console.error("Oops! Something went wrong.");
					$("#newEmail").css("border", "1px solid firebrick");
				}
			}); //END:$.ajax
		} //else: 
}

 //Facebook login 
  function facebookSignIn(response)
  {
	  
	  console.log("In fbSignIn...");
	  //FB.login(function(response) {
		    console.log(response);
			if (response.status === 'connected') {
		    // Logged into your app and Facebook.
		    console.log("FB Logged in....");
	
		    //To fetch basic info for logged in user
		    FB.api('/me', {fields: 'id,name, first_name, last_name, email'}, function(fbApiData) {
		    	  
		    	console.log('Facebook profile details');
		    	console.log(fbApiData);
		    	  
		    	//Servlet Call for storing data and establishing session with user details
	    	 	console.log("In facebook login Ajax");
				var projectID=$("#hiddenTxtProjectValueLogin").val();
				if(projectID=="")
				{
					var errorMsg = "Please select a system/project from dropdown list";
					console.log(errorMsg);
					$("#lblLoginFormErrorMsg").addClass('alert alert-danger');
					$("#lblLoginFormErrorMsg").text(errorMsg);
				}
				else
				{
					
					 //Logout from facebook as user info is fetched
				    facebookLogout();
				
					$("#lblLoginFormErrorMsg").removeClass('alert alert-danger');
				
				    //Establish session with Facebook profile info. //accountType = 1 is for facebook login. This is used to take decision at servlet method
					$.ajax({
						type:"POST",
						url: "/DMS/socialLogin",
						data: {jData : JSON.stringify({accountType: 1 , loginProjectID: projectID, fbId: fbApiData.id, fbName: fbApiData.name, fbEmail: fbApiData.email, fbFirstName: fbApiData.first_name, fbLastName: fbApiData.last_name})},
						success : function(data){
							console.log(data);
							if(data["status"] == "success")
							{
								console.log("Accessing Dashboard"); //Using session variables set loginUser.jsp will call DashUser.jsp
								window.location = "/DMS/jsp/LoginUser.jsp";
							}
							else
							{
								console.log("");
								window.location = "/DMS/jsp/LoginUser.jsp";
							}
							//$("#newEmail").css("border", "1px solid green");
							
						},
						error : function(){
							console.log(error);
							//console.error("Oops! Something went wrong.");
							$("#newEmail").css("border", "1px solid firebrick");
						}
					}); //END:$.ajax
				} //else: 
	    	  //END: Servlet Call 

			    });
		    
		   
		    
		  } else if (response.status === 'not_authorized') {
		    // The person is logged into Facebook, but not your app.
			  console.log("FB Logged in  but not authorized....");
		  } else {
		    // The person is not logged into Facebook, so we're not sure if
		    // they are logged into this app or not.
			  console.log("FB login failed....");
		  }
		//});
  }

 function facebookLogout()
 {
	 FB.logout(function(response) {
		   // Person is now logged out
		 	console.log("FB Logged out....");
		});
 }
 
	