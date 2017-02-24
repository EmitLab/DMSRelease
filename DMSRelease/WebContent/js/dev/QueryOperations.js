/**
 *  Get Number of Users Registered on the DMS System. 
 */

function getNumberOfQueries(){
	
	$.ajax({
		type:"POST",
		url:"/DMS/GetNumberOfQueries",
		dataType:"json",
		success:function(data){
			if (data.servletState == "1"){
				var userCount = data.queryCount;
				$("#queryCountLbl").html("<i class=\"fa fa-circle-o-notch\"> " + userCount + "</a>");
			} else {
				alert("Error Occured");
			}
		}
	});
}