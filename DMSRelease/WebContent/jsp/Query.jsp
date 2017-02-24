<%@page import="defaults.SessionVariableList"%>
<%@ page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%
    if(session.getAttribute(SessionVariableList.USER_LOGIN_STATE) != null && 
        session.getAttribute(SessionVariableList.USER_LOGIN_STATE).equals(SessionVariableList.USER_LOGIN_STATES[1])){
%>
<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<title>Dashboard</title>
<link href="/DMS/css/dev/main.css" rel="stylesheet">
<link href="/DMS/css/sys/bootstrap.min.css" rel="stylesheet">
<link href="/DMS/DashUser/vendor/metisMenu/metisMenu.min.css"
	rel="stylesheet">
<link href="/DMS/DashUser/dist/css/sb-admin-2.css" rel="stylesheet">
<script src="https://use.fontawesome.com/b48a93c02f.js"></script>
<script src="/DMS/js/sys/jquery-3.1.1.min.js"></script>
<script src="/DMS/js/dev/initUserDash.js"></script>
<script src="/DMS/js/dev/query.js"></script>
<script>
        // The below code is used to prevent use of cached file in chome.
        // this ensures that all the JS files are update to date.
        $("script[src]").each(function(){
            $this = $(this);
            var src = $this.attr("src");
            $(this).attr("src", src + "?" + Date.now()%10000);
        });
    </script>

<script>
        function uploadEnsemble(){
            // var uid = '<%= session.getAttribute(SessionVariableList.USER_ID)%>';
            // var pid = '<%= session.getAttribute(SessionVariableList.CURRENT_PROJECT_ID)%>';
            uploadEnsembles();
        }
        window.onload = function() {
            var uid = '<%= session.getAttribute(SessionVariableList.USER_ID)%>';
            var pid = '<%= session.getAttribute(SessionVariableList.CURRENT_PROJECT_ID)%>';
            initUserDashTopMenu(uid, pid);
            initUserDashLeftSideBarMenu();
            initUserDashFooter(pid);
            //getEnsembleList();
            getQueryList();
            $(".loader").fadeOut("slow");
        };
    </script>
</head>

<body id="body">
	<div class="loader"></div>
	<div class="footer">
		Developed by Emitlab
		<div class="pull-right">NSF #</div>
	</div>
	<div id="wrapper">
		<!-- Navigation -->
		<nav class="navbar navbar-default navbar-static-top" role="navigation"
			style="margin-bottom: 0">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="DashUser.jsp">[ERR: PROJECT NAME]</a>
			</div>
			<ul class="nav navbar-top-links navbar-right" id="dashTopMenu">
			</ul>
			<div class="navbar-default sidebar" role="navigation">
				<div class="sidebar-nav navbar-collapse">
					<ul class="nav" id="side-menu">
						<!-- Side Menu Here -->
					</ul>
				</div>
			</div>
		</nav>

		<div id="page-wrapper">
			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">Query Management</h1>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-8">
					<div class="panel panel-default id="queryPanel">
						<div class="panel-heading">
							<h4>
								<i class="fa fa-circle-o-notch pull-right"></i> Query
							</h4>
						</div>
						<ul class="nav nav-tabs" id="myTab">
							<li id="viewQueryLi" class="active"><a href="#viewQuery"
								data-toggle="tab"> <i class="fa fa-reorder"></i> View
							</a></li>
							<li id="addQueryLi"><a href="#addQuery" data-toggle="tab">
									<i class="fa fa-plus"></i> Add
							</a></li>
							<li id="modifyQueryLi"><a href="#modifyQuery"
								data-toggle="tab"> <i class="fa fa-pencil"></i> Modify
							</a></li>
						</ul>

						<div class="tab-content">
							<div class="tab-pane fade in active well" id="viewQuery">
								<div class="list-group" id="viewQueryList"></div>
							</div>
							<div class="tab-pane fade well" id="addQuery">
								<div class="form-group">
									<label>Name : <span id="addQueryNameError"></span></label> <input
										type="text" id="addQueryName" class="form-control"
										placeholder="Enter query name here" />
								</div>
								<div class="form-group">
									<label>Description : <span
										id="addQueryDescriptionError"></span></label>
									<textarea rows="5" id="addQueryDescription"
										class="form-control" placeholder="Describe your query here"></textarea>
								</div>
								<div class="form-group">
									<label>Query : <span id="addQueryQueryError"></span></label>
									<textarea rows="8" id="addQueryQuery" class="form-control"
										placeholder="Enter your FLOWR query here"></textarea>
								</div>
								<div class="form-group">
									* All fields are required.
									<div class="pull-right">
										<button class="btn btn-success" id="addBtn"
											onclick="addQuery('addBtn')">Add Query</button>
									</div>
								</div>
							</div>
							<div class="tab-pane fade well" id="modifyQuery">
								<div class="form-group">
									<label>Select a query : <span id="modifyQueryListError"></span></label>
									<select class="form-control" onchange="getQueryById()"
										id="modifyQueryList"></select>
								</div>
								<div class="form-group">
									<label>Name : <span id="modifyQueryNameError"></span></label> <input
										type="text" id="modifyQueryName" class="form-control"
										placeholder="Enter query name here" />
								</div>
								<div class="form-group">
									<label>Description : <span
										id="modifyQueryDescriptionError"></span></label>
									<textarea rows="5" id="modifyQueryDescription"
										class="form-control" placeholder="Describe your query here"></textarea>
								</div>
								<div class="form-group">
									<label>Query : <span id="modifyQueryQueryError"></span></label>
									<textarea rows="8" id="modifyQueryQuery" class="form-control"
										placeholder="Enter your FLOWR query here"></textarea>
								</div>
								<div class="form-group">
									* All fields are required.
									<div class="pull-right">
										<button class="btn btn-success" id="modifyBtn"
											onclick="modifyQuery()">Modify Query</button>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-4">
					<div class="panel panel-default">
						<div class="panel-heading"
							style="height: 55px; vertical-align: middle;">
							History
							<div class="pull-right">
								<i class="fa fa-history fa-fw"></i>
							</div>
						</div>
						<div class="panel-body">
							<div class="list-group" id="viewQueryLogList"></div>
							<em>* Only 10 latest actions of queries.</em>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- Bootstrap Core JavaScript -->
	<script src="/DMS/js/sys/bootstrap.min.js"></script>

	<!-- Metis Menu Plugin JavaScript -->
	<script src="/DMS/DashUser/vendor/metisMenu/metisMenu.min.js"></script>

	<!-- Custom Theme JavaScript -->
	<script src="/DMS/DashUser/dist/js/sb-admin-2.js"></script>

</body>

</html>

<%
    } else {
        String redirectURL = "/DMS/jsp/LoginUser.jsp";
        response.sendRedirect(redirectURL);
    }
%>