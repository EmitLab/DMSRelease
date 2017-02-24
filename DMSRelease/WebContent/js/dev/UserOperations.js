/**
 *  Get Number of Users Registered on the DMS System. 
 */

function getNumberOfUsers(){
	
	$.ajax({
		type:"POST",
		url:"/DMS/GetNumberOfUsers",
		dataType:"json",
		success:function(data){
			if (data.servletState == "1"){
				var userCount = data.userCount;
				$("#userCountLbl").html("<i class=\"fa fa-users\"> " + userCount + "</a>");
			} else {
				alert("Error Occured");
			}
		}
	});
}