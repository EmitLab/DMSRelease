<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Compare XML</title>
<script src="/DMS/js/sys/jquery-3.1.1.min.js"></script>
<script type="text/javascript">
function xmlDifferences(){
	//console.log("XML");
	 var query = window.location.search.substring(1).split("=")[1];
	 //var inputQuery = '/MVTSDB/ServletController?ClusterRequest=diff&input='+query;
	 
	 $.ajax({
		 type: "POST",
		 url: "/DMS/MetadataDifference",
		 data : {input : query},
		 dataType : "text",
		 success : function(data){
			 var contentFile = data;
             document.body.innerHTML = contentFile;
		 },
		 error : function(){
			 console.log("Error in Metadata Difference.");
		 }
	 });
	 
	 /*
	 
	 var requestxml= null;
		if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
			requestxml = new XMLHttpRequest();
		}
		else {// code for IE6, IE5
			requestxml = new ActiveXObject("Microsoft.XMLHTTP");
		}
		requestxml.onreadystatechange=function(){//response
			if (requestxml.readyState == 4 && requestxml.status == 200) {
				
				var contentFile = requestxml.responseText;
				document.body.innerHTML = contentFile;
			}
		}
		requestxml.open("GET", inputQuery);
 		requestxml.send(null);
 	*/
 }
</script>
</head>

<body onload="xmlDifferences();">
<div id="append"></div>
</body>
</html>