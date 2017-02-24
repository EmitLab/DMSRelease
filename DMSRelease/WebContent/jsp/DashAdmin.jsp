<%@page import="defaults.SessionVariableList"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%
	if(session.getAttribute(SessionVariableList.ADMIN_LOGIN_STATE) != null && 
		session.getAttribute(SessionVariableList.ADMIN_LOGIN_STATE).equals(SessionVariableList.ADMIN_LOGIN_STATES[1])){
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<title>DMS Gateway</title>
<meta name="generator" content="Bootply" />
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1">
<!--<link href="/DMS/DashAdmin/css/bootstrap.min.css" rel="stylesheet">-->
<link href="/DMS/css/sys/bootstrap.min.css" rel="stylesheet">
<script src="https://use.fontawesome.com/b48a93c02f.js"></script>
<link href="/DMS/DashAdmin/css/styles.css" rel="stylesheet">

<!--[if lt IE 9]>
			<script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->

<script>
	window.onload = function(){
		var servletState = '<%=session.getAttribute(SessionVariableList.ADMIN_LOGIN_STATE).equals(SessionVariableList.ADMIN_LOGIN_STATES[1])%>';
		if(servletState === "true"){
			var firstName = '<%=session.getAttribute(SessionVariableList.ADMIN_FIRST_NAME)%>';
			$("#adminName").html(" " + firstName + ",");
		} else {
			window.location.href = "/DMS/jsp/LoginAdmin.jsp";
		}
		
		initializeAdminDash();
		viewGrant();
		viewPublication();
		
		
	};
</script>
<!-- script references -->
<script src="/DMS/js/sys/jquery-3.1.1.min.js"></script>
<script src="/DMS/DashAdmin/js/scripts.js"></script>
<script src="/DMS/js/sys/bootstrap.min.js"></script>
<script src="/DMS/js/dev/ProjectOperations.js"></script>
<script src="/DMS/js/dev/initializeAdminDash.js"></script>
<script src="/DMS/js/dev/QueryOperations.js"></script>
<script src="/DMS/js/dev/UserOperations.js"></script>
<script src="/DMS/js/dev/Grants.js"></script>
<script src="/DMS/js/dev/publications.js"></script>
<script src="/DMS/js/sys/citeproc.js"></script>
<script src="/DMS/js/sys/bibtexParse.js"></script>


</head>
<body>
	<!-- header -->
	<div id="top-nav" class="navbar navbar-inverse navbar-static-top">
		<div class="container-fluid">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-collapse">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#">simDMS Admin</a>
			</div>
			<div class="navbar-collapse collapse">

				<ul class="nav navbar-nav navbar-right">
					<li><a href="#" style="cursor: default;">Welcome <span
							id="adminName"></span></a></li>
					<li class="dropdown"><a class="dropdown-toggle" role="button"
						data-toggle="dropdown" href="#"> <i class="fa fa-gear"></i>
							Settings <span class="caret"></span></a>
						<ul id="g-account-menu" class="dropdown-menu" role="menu">
							<li><a href="#">Update Email</a></li>
							<li><a href="#">Change Password</a></li>
							<li><a href="#">Add New Admin</a></li>
						</ul></li>
					<li><a href="/DMS/AdminLogout"><i class="fa fa-lock"></i>
							Logout</a></li>
				</ul>
			</div>
		</div>
		<!-- /container -->
	</div>
	<!-- /Header -->

	<!-- Main -->
	<div class="container-fluid">
		<div class="row">
			<div class="col-sm-3">
				<!-- Left column -->
				<a href="#"><strong><i class="fa fa-wrench"></i> Tools</strong></a>

				<hr>
				<!-- <iframe src="/DMS/jsp/AdminMenu.jsp"></iframe>-->
				<ul class="nav nav-stacked">
					<li class="nav-header"><a href="#" data-toggle="collapse"
						data-target="#userMenu">Settings <i class="fa fa-angle-down"></i></a>
						<ul class="nav nav-stacked collapse in" id="userMenu">
							<li class="active"><a href="/DMS/jsp/DashAdmin.jsp">
								<div class="pull-right"><i class="fa fa-home"></i></div> 
								Home</a></li>
							<!--<li><a href="#"><i class="fa fa-envelope"></i> Messages
									<span class="badge badge-info">4</span></a></li>-->
							<li><a href="/DMS/jsp/team.jsp">
								<div class="pull-right"><i class="fa fa-users"></i></div> 
								Team</a></li><!-- 
							<li><a href="#">
								<div class="pull-right"><i class="fa fa-newspaper-o"></i></div>
								Publications</a></li>
							<li><a href="#">
								<div class="pull-right"><i class="fa fa-map-signs"></i></div> 
								Documentation</a></li>-->
							<li><a href="/DMS/AdminLogout">
								<div class="pull-right"><i class="fa fa-lock"></i></div>
								Logout</a></li>
						</ul>
					</li><!--
					<li class="nav-header"><a href="#" data-toggle="collapse"
						data-target="#menu2"> Reports <i class="fa fa-angle-down"></i></a>

						<ul class="nav nav-stacked collapse" id="menu2">
							<li>
								<a href="#">Error Logs
									<div class="pull-right">
										<i class="fa fa-exclamation-circle"></i>
									</div>
								</a>
							</li>
							<li>
								<a href="#">Views
									<div class="pull-right">
										<i class="fa fa-eye"></i>
									</div>
								</a>
							</li>
							<li>
								<a href="#">Currect Issues
									<div class="pull-right">
										<i class="fa fa-github"></i>
									</div>
								</a>
							</li>
						</ul>
					</li>-->
				</ul>

				<hr>
				<h4>
					<div class="label label-warning label-md">Access Rights Reserved with Emitlab</div>
				</h4>
			</div>
			<!-- /col-3 -->
			<div class="col-sm-9">
				<a href="#"><strong><i class="fa fa-dashboard"></i> My
						Dashboard</strong></a>
				<hr>

				<div class="row">
					<!-- center left-->
					<div class="col-md-6">
						<div class="btn-group btn-group-justified"
							style="text-align: left;">
							<a href="#" class="btn btn-primary col-sm-3" rel="tooltip"
								data-placement="bottom" title="Databases" id="databaseCountLbl"> <i
								class="fa fa-database"></i> 23
							</a>
							<a href="#" class="btn btn-primary col-sm-3" rel="tooltip"
								data-placement="bottom" title="Queries" id="queryCountLbl"> <i
								class="fa fa-circle-o-notch"></i> 420
							</a>
							<a href="#" class="btn btn-primary col-sm-3" rel="tooltip"
								data-placement="bottom" title="Users" id="userCountLbl"> <i
								class="fa fa-users"></i> 10
							</a>
						</div>

						<hr>

						<div class="panel panel-default">
							<div class="panel-heading">
								<h4>Grant Management</h4>
							</div>
							<ul class="nav nav-tabs" id="myTab">
                                <li id="viewGrantli" class="active"><a
                                    href="#viewGrant" data-toggle="tab"> <i
                                        class="fa fa-reorder"></i> View
                                </a></li>
                                <li id="addGrantli"><a href="#addGrant"
                                    data-toggle="tab"> <i class="fa fa-plus"></i> Add
                                </a></li>
                                <li id="modifyGrantli"><a href="#modifyGrant"
                                    data-toggle="tab"> <i class="fa fa-pencil"></i> Modify
                                </a></li>
                            </ul>
                            <div class="tab-content">
                                <div class="tab-pane fade in active well" id="viewGrant">
                                    <ul class="list-group" id="viewGrant"></ul>
                                </div>
                                <div class="tab-pane fade well" id="addGrant">
                                    <div class="form-group">
                                        <label> Grant name/number</label>
                                        <span class="label label-danger pull-right">Required</span>
                                        <input type="text" id="addGrantName" class="form-control" placeholder="Grant Name or Number"/>
                                    </div>
                                    <div class="form-group">
                                        <label> Grant title</label>
                                        <span class="label label-danger pull-right">Required</span>
                                        <input type="text" id="addGrantTitle" class="form-control" placeholder="Grant Title"/>
                                    </div>
                                    <div class="form-group">
                                        <label> Web link to grant information</label>
                                        <span class="label label-warning pull-right">Recommended</span>
                                        <input type="text" id="addGrantLink" class="form-control" placeholder="Grant URL or Page"/>    
                                    </div>
                                    <div class="form-group">
                                        <label> Select a project</label>
                                        <span class="label label-danger pull-right">Required</span>
                                        <select id="addGrantProject" class="form-control"></select>    
                                    </div>
                                    <button class="btn btn-success" onclick="addGrant()">Add Grant</button>
                                </div>
                                <div class="tab-pane fade well" id="modifyGrant">
                                    <div class="form-group">
                                        <select id="modifyGrantList" class="form-control" onchange="GetGrant()"></select>
                                    </div>
	                                <div class="form-group">
                                        <label> Grant name/number</label>
                                        <span class="label label-danger pull-right">Required</span>
                                        <input type="text" id="modifyGrantName" class="form-control" placeholder="Grant Name or Number"/>
                                    </div>
                                    <div class="form-group">
                                        <label> Grant title</label>
                                        <span class="label label-danger pull-right">Required</span>
                                        <input type="text" id="modifyGrantTitle" class="form-control" placeholder="Grant Title"/>
                                    </div>
                                    <div class="form-group">
                                        <label> Web link to grant information</label>
                                        <span class="label label-warning pull-right">Recommended</span>
                                        <input type="text" id="modifyGrantLink" class="form-control" placeholder="Grant URL or Page"/>    
                                    </div>
                                    <div class="form-group">
                                        <label> Select a project</label>
                                        <span class="label label-danger pull-right">Required</span>
                                        <select id="modifyGrantProject" class="form-control"></select>    
                                    </div>
                                    <button class="btn btn-success" onclick="modifyGrant()">Modify Grant</button>
                                </div>
                            </div>
							<span style="padding-left: 20px;">
							    <i class="fa fa-warning"></i>
							    <span id="projectMessage">No updates to grants at the moment.</span>
                            </span> 
                            <br/>
                            <br/>
						</div>
						<hr/>
						<div class="panel panel-default">
                            <div class="panel-heading">
                                <h4>Publication Management</h4>
                            </div>
                            <ul class="nav nav-tabs" id="myTab">
                                <li id="viewPublicationli" class="active"><a
                                    href="#viewPublication" data-toggle="tab"> <i
                                        class="fa fa-reorder"></i> View
                                </a></li>
                                <li id="addPublicationli"><a href="#addPublication"
                                    data-toggle="tab"> <i class="fa fa-plus"></i> Add
                                </a></li>
                                <li id="modifyPublicationli"><a href="#modifyPublication"
                                    data-toggle="tab"> <i class="fa fa-pencil"></i> Modify
                                </a></li>
                            </ul>
                            <div class="tab-content">
                                <div class="tab-pane fade in active well" id="viewPublication">
                                    <ul class="list-group" id="viewPublication"></ul>
                                </div>
                                <div class="tab-pane fade well" id="addPublication">
                                    <div class="form-group">
                                        <label> Publication Citation</label>
                                        <span class="label label-danger pull-right">Required</span>
                                        <textarea id="addPublicationCitation" class="form-control" 
                                            placeholder="Add Bibtex of publication here" rows="6"></textarea>
                                    </div>
                                    <div class="form-group">
                                        <label> Select Project(s)</label>
                                        <span class="label label-danger pull-right">Required</span>
                                        <div id="addPublicationProject" style="border-top:1px lightgray solid;">
                                        
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label> Tags (comma separated)</label>
                                        <span class="label label-danger pull-right">Required</span>
                                        <input type="text" id="addPublicationTag" class="form-control" placeholder="Tags - comma separated"/>    
                                    </div>
                                    <div class="form-group">
                                        <label> Upload PDF</label>
                                        <span class="label label-warning pull-right">Recommended</span>
                                        <input type="file" id="addPublicationFile" class="form-control" placeholder="Upload File"/>
                                    </div>
                                    <button class="btn btn-success" onclick="addPublication()">Add Publication</button>
                                </div>
                                <div class="tab-pane fade well" id="modifyPublication">
                                    <div class="form-group">
                                        <select id="modifyPublicationList" class="form-control" onchange="GetPublication()"></select>
                                    </div>
                                    <div class="form-group">
                                        <label> Publication Citation</label>
                                        <span class="label label-danger pull-right">Required</span>
                                        <textarea id="modifyPublicationCitation" class="form-control" 
                                            placeholder="Modify Bibtex of publication here" rows="6"></textarea>
                                    </div>
                                    <div class="form-group">
                                        <label> Select Project(s)</label>
                                        <span class="label label-danger pull-right">Required</span>
                                        <div id="modifyPublicationProject" style="border-top:1px lightgray solid;">
                                        
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label> Tags (comma separated)</label>
                                        <span class="label label-danger pull-right">Required</span>
                                        <input type="text" id="modifyPublicationTag" class="form-control" placeholder="Tags - comma separated"/>    
                                    </div>
                                    <button class="btn btn-success" onclick="modifyPublication()">Modify Publication</button>
                                </div>
                            </div>
                            <!--/panel-body-->
                            
                            
                            
                            
                            <span style="padding-left: 20px;">
                                <i class="fa fa-warning"></i>
                                <span id="projectMessage">No updates to publications at the moment.</span>
                            </span> 
                            <br/>
                            <br/>
                        </div>
						<!--/panel-->
					</div>
					<!--/col-->
					<div class="col-md-6">

						<!--tabs-->
						<div class="panel panel-primary" id="projectpanel">
							<div class="panel-heading">
								<h4>
									<i class="fa fa-tv pull-right"></i> Projects
								</h4>
							</div>
							<ul class="nav nav-tabs" id="myTab">
								<li id="viewProjectli" class="active"><a
									href="#viewProject" data-toggle="tab"> <i
										class="fa fa-reorder"></i> View
								</a></li>
								<li id="addProjectli"><a href="#addProject"
									data-toggle="tab"> <i class="fa fa-plus"></i> Add
								</a></li>
								<li id="modifyProjectli"><a href="#modifyProject"
									data-toggle="tab"> <i class="fa fa-pencil"></i> Modify
								</a></li>
							</ul>

							<div class="tab-content">
								<div class="tab-pane fade in active well" id="viewProject">
								</div>
								<div class="tab-pane fade well" id="addProject">
									<div class="control-group">
										<div class="controls">
											<input type="text" class="form-control" name="projectName"
												placeholder="Enter project name">
										</div>
									</div>
									<br>
									<div class="control-group">
										<div class="controls">
											<input type="text" class="form-control" name="projectAbbr"
												placeholder="Enter project abbreviation">
										</div>
									</div>
									<br>
									<div class="control-group">
										<div class="controls">
											<input type="text" class="form-control" name="projectTitle"
												placeholder="Enter project title here">
										</div>
									</div>
									<br>
									<div class="control-group">
										<div class="controls">
											<textarea class="form-control" name="projectDescription"
												placeholder="Descrive your project here"></textarea>
										</div>
									</div>
									<br>
									<div class="control-group">
										<div class="controls">
											<button class="btn btn-primary" onclick="addProject();">
												Add New Project</button>
											<i class="fa fa-asterisk"></i> <span>All field are
												required.</span>
										</div>
									</div>
								</div>
								<div class="tab-pane fade well" id="modifyProject">
									<select class="form-control" id="modifyProjectList"
										onchange="updateProjectMenu()">

									</select>
									<hr />
									<div class="control-group">
										<div class="controls">
											<input type="text" class="form-control" name="projectName"
												placeholder="Enter project name">
										</div>
									</div>
									<br>
									<div class="control-group">
										<div class="controls">
											<input type="text" class="form-control" name="projectAbbr"
												placeholder="Enter project abbreviation">
										</div>
									</div>
									<br>
									<div class="control-group">
										<div class="controls">
											<input type="text" class="form-control" name="projectTitle"
												placeholder="Enter project title here">
										</div>
									</div>
									<br>
									<div class="control-group">
										<div class="controls">
											<textarea class="form-control" name="projectDescription"
												placeholder="Descrive your project here"></textarea>
										</div>
									</div>
									<br>
									<div class="control-group">
										<div class="controls">
											<select class="form-control">
												<option name="enabled">Enabled</option>
												<option name="disabled">Disabled</option>
											</select>
										</div>
									</div>
									<br>
									<div class="control-group">
										<div class="controls">
											<button class="btn btn-primary" onclick="updateProject();">
												Update Project</button>
											<button class="btn btn-danger disabled"
												onclick="deleteProject();">Delete Project</button>
										</div>
									</div>
								</div>
							</div>
							<span style="padding-left: 20px;"> <i
								class="fa fa-warning"></i> <span id="projectMessage">No updates related to
									project.</span>
							</span> <br /> <br />
						</div>
						<!--/tabs-->
						<hr />
						<div class="panel panel-default">
							<div class="panel-heading">
								<!--<div class="panel-title">-->
								<h4>
									<i class="fa fa-th pull-right"></i> Storage
								</h4>
								<!--</div>-->
							</div>
							<div class="panel-body">
							     Not Available at the moment.
							</div>
						</div>
				    </div>
					<!--/col-span-6-->

				</div>
			</div>
			<!--/col-span-9-->
		</div>
	</div>
	<!-- /Main -->
    <footer align="center">
        <div>
            Developed by EmitLab, ASU and Dashboard design by <a href="http://www.bootply.com/85850"><strong>Bootply.com</strong></a>
        </div>
    </footer>
	
	<script>
		$(document).ready(function() {
			$('[rel="tooltip"]').tooltip({
				container : 'body'
			});
		});
	</script>
</body>
</html>
<%
	} else {
		String redirectURL = "/DMS/jsp/LoginAdmin.jsp";
		response.sendRedirect(redirectURL);
	}
%>