<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>DMS - Oops! 404 Error</title>
<link href="/DMS/css/sys/bootstrap.min.css" rel="stylesheet">
<link href="/DMS/fonts/css/font-awesome.min.css" rel="stylesheet">
<script src="/DMS/js/sys/jquery-3.1.1.min.js"></script>
<script>
	window.onload = function(){
		document.getElementById("404link").innerHTML = "";
		document.getElementById("404link").innerHTML = window.location.href;
	}
	$.ajax({
		type:"POST",
		url:"/DMS/Log404Errors",
		data:{jData : JSON.stringify({"url":window.location.href})},
		success:function(){
			console.log("404 Error Detection");
		}
	});
</script>
</head>
<body style="text-align:center">
<div class="container">
    <div class="row">
        <div class="col-md-12">
            <div class="error-template">
                <h1>
                    Oops!</h1>
                <h2>
                    404 Not Found</h2>
                <div class="error-details">
                    Sorry, an error has occurred, while requesting the below page!
                </div>
                <hr />
                <a class="btn btn-link" href="#" id="404link"></a>
                <hr />
                <div class="error-actions">
                    <a href="/DMS/" class="btn btn-primary btn-md">
                    	<span class="fa fa-home"></span>
                        Go to DMS Home
                    </a>
                    <a href="mailto:emitlab@gmail.com" class="btn btn-default btn-md">
                    	<span class="fa fa-envelope"></span>
                    	 Contact Support
                   	</a>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>