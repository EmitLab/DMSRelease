<%@page import="defaults.SessionVariableList"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%
	if (session.getAttribute(SessionVariableList.ADMIN_LOGIN_STATE) != null
			&& session.getAttribute(SessionVariableList.ADMIN_LOGIN_STATE)
					.equals(SessionVariableList.ADMIN_LOGIN_STATES[1])) {
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

<script src="/DMS/js/sys/jquery-3.1.1.min.js"></script>

<link href="/DMS/css/sys/bootstrap.min.css" rel="stylesheet">
<script src="/DMS/js/sys/bootstrap-toggle.min.js"></script>
<script src="/DMS/js/sys/bootstrap.min.js"></script>

<link href="/DMS/css/sys/multiselect.css" rel="stylesheet">
<script src="/DMS/js/sys/multiselect.js"></script>

<link href="/DMS/css/dev/MultiStepForm.css" rel="stylesheet">
<script src="/DMS/js/dev/MultiStepForm.js"></script>

<link href="/DMS/css/sys/bootstrap-toggle.min.css" rel="stylesheet">
<link href="/DMS/DashAdmin/css/styles.css" rel="stylesheet">

<script src="https://use.fontawesome.com/b48a93c02f.js"></script>

<!--[if lt IE 9]>
			<script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
<script src="/DMS/DashAdmin/js/scripts.js"></script>

<script src="/DMS/js/dev/ProjectOperations.js"></script>
<script src="/DMS/js/dev/initializeAdminDash.js"></script>
<script src="/DMS/js/dev/QueryOperations.js"></script>
<script src="/DMS/js/dev/UserOperations.js"></script>
<script src="/DMS/js/dev/TeamOperations.js"></script>

<script>
	window.onload = function(){
		var firstName = '<%=session.getAttribute(SessionVariableList.ADMIN_FIRST_NAME)%>';
		$("#adminName").html(" " + firstName + ",");

		//$("#projectlist").multiselect();
		$("#userTypeList").multiselect();
		$("#modifyuserType").multiselect();
		showTeam();
		
		var w_and_h = $("#NewStatus").parent().attr("style");
		$("#modifyStatus").parent().attr("style",w_and_h);

		listProjectonTeamPage();
		

	    //$('#my-multi-select').multiselect({
	      //  includeSelectAllOption: true
	    //});

	  
	};
	
</script>

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
				<a class="navbar-brand" href="#">DMS Admin Dashboard</a>
			</div>
			<div class="navbar-collapse collapse">

				<ul class="nav navbar-nav navbar-right">
					<li>
						<a href="#" style="cursor: default;">Welcome 
							<span id="adminName"></span>
						</a>
					</li>
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

				<ul class="nav nav-stacked">
					<li class="nav-header"><a href="#" data-toggle="collapse"
						data-target="#userMenu">Settings <i class="fa fa-angle-down"></i></a>
						<ul class="nav nav-stacked collapse in" id="userMenu">
							<li class="active"><a href="/DMS/jsp/DashAdmin.jsp">
									<div class="pull-right">
										<i class="fa fa-home"></i>
									</div> Home
							</a></li>
							<!--<li><a href="#"><i class="fa fa-envelope"></i> Messages
									<span class="badge badge-info">4</span></a></li>-->
							<li><a href="/DMS/jsp/team.jsp">
									<div class="pull-right">
										<i class="fa fa-users"></i>
									</div> Team
							</a></li>
							<li><a href="#">
									<div class="pull-right">
										<i class="fa fa-newspaper-o"></i>
									</div> Publications
							</a></li>
							<li><a href="#">
									<div class="pull-right">
										<i class="fa fa-map-signs"></i>
									</div> Documentation
							</a></li>
							<li><a href="/DMS/AdminLogout">
									<div class="pull-right">
										<i class="fa fa-lock"></i>
									</div> Logout
							</a></li>
						</ul></li>
					<li class="nav-header"><a href="#" data-toggle="collapse"
						data-target="#menu2"> Reports <i class="fa fa-angle-down"></i></a>

						<ul class="nav nav-stacked collapse" id="menu2">
							<li><a href="#">Error Logs
									<div class="pull-right">
										<i class="fa fa-exclamation-circle"></i>
									</div>
							</a></li>
							<li><a href="#">Views
									<div class="pull-right">
										<i class="fa fa-eye"></i>
									</div>
							</a></li>
							<li><a href="#">Currect Issues
									<div class="pull-right">
										<i class="fa fa-github"></i>
									</div>
							</a></li>
						</ul></li>
				</ul>

				<hr>
				<h4>
					<div class="label label-warning label-md">EmitLab</div>
				</h4>
			</div>
			<!-- /col-3 -->
			<div class="col-sm-9">

				<a href="#"><strong><i class="fa fa-users"></i> My
						Team</strong></a>
				<hr>
				<div class="row">
					<!-- center left-->
					<div class="col-md-8">
						<div>
							<h4>Principle Investigators</h4><div id="pi"></div>
						</div>
						<div>
							<h4>Current Members</h4><div id="cm"></div>
						</div>
						<div>
							<h4>Past Members</h4><div id="pm"></div>
						</div>
						<hr />
					</div>
					<div class="col-md-4">
						<!--tabs-->
						<div class="panel panel-primary" id="projectpanel" >
							<div class="panel-heading">
								<h4><i class="fa fa-users pull-right"></i> Team</h4>
							</div>
							<ul class="nav nav-tabs" id="myTab">
								<li id="addTeamli" class="active">
									<a href="#addTeam" data-toggle="tab"> 
										<i class="fa fa-plus"></i> Add
									</a>
								</li>
								<li id="modifyTeamli">
									<a href="#modifyTeam" data-toggle="tab"> 
										<i class="fa fa-pencil"></i> Modify
									</a>
								</li>
							</ul>

							<div class="tab-content">
								<div class="tab-pane fade in active well" id="addTeam">
									<div class="stepwizard col-lg-offset-3 col-md-offset-3" align="center">
										<div class="stepwizard-row setup-panel">
											<div class="stepwizard-step">
												<a href="#step-1" type="button" class="btn btn-primary btn-circle">
													<i class="fa fa-user"></i>
												</a>
											</div>
											<div class="stepwizard-step">
												<a href="#step-2" type="button" class="btn btn-default btn-circle">
													<i class="fa fa-image"></i>
												</a>
											</div>
											<div class="stepwizard-step">
												<a href="#step-3" type="button" class="btn btn-default btn-circle">
													<i class="fa fa-share-alt"></i>
												</a>
											</div>
										</div>
									</div>
									<form id="addNewMember" action="/DMS/TeamAdd" method="post"
										style="padding:10px; margin-top: -15px;" novalidate>
										<div class="row setup-content" id="step-1">
											<h4>
												Information
												<div class="pull-right"><i class="fa fa-user"></i></div>
											</h4>
											<div class="form-group">
												<label class="control-label">Name*</label> 
												<input id="NewName" maxlength="50" type="text" required="required"
													class="form-control" placeholder="Enter Name" />
											</div>
											<div class="form-group">
												<label class="control-label">Designation*</label> 
												<input id="NewDesignation" maxlength="100" type="text" 
													class="form-control" placeholder="Enter Designation" />
											</div>
											<div class="form-group">
												<label class="control-label">Affiliation*</label>
												<input id="NewAffiliation" maxlength="100" type="text"
													class="form-control" placeholder="Enter Affiliated Insitute Name" />
											</div>
											<div class="form-group">
												<label class="control-label">Member Type*</label>
											
												<select class="form-control" id="userTypeList">
													<option>Principle Investigator</option>
													<option>Current Member</option>
													<option>Past Member</option>
												</select>
											</div>
											<div class="form-group">
												<label class="control-label">Project List*</label>
			
												<select class="form-control multiselect" id="newUserProjectListMultiSelect" name="options[]" multiple="multiple">
													<!-- Autofill by JS -->
												</select>
											</div>
											
											
											<button class="btn btn-primary nextBtn pull-right"
												type="button">Next</button>
										</div>
										<div class="row setup-content" id="step-2">
											<h4>
												Image
												<div class="pull-right">
													<i class="fa fa-image"></i>
												</div>
											</h4>
											
											<div class="form-group"> 
												<input name="name" maxlength="255" type="url"
													class="form-control" placeholder="Enter Image URL Here" 
													id="imagePreviewUrl" onchange="pullImage('imagePreviewUrl','imagePreview')"/>
													<br/>
												<img id="imagePreview" class="img-fuild rounded" src=""
													onerror="this.src='/DMS/images/team/error.png'" 
													width="150px" height="150px">
											</div>
											
											<button class="btn btn-primary nextBtn pull-right"
												type="button">Next</button>
										</div>
										<div class="row setup-content" id="step-3">
											<h4>
												Social Media
												<div class="pull-right">
													<i class="fa fa-share-alt"></i>
												</div>
											</h4>
											
											<div class="input-group margin-bottom-sm">
												<span class="input-group-addon"> <i
													class="fa fa-anchor fa-fw" aria-hidden="true"></i>
												</span> 
												<input id="NewHomepage" class="form-control" type="text"
													placeholder="Homepage" maxlength="255">
											</div>
											<br />
											<div class="input-group margin-bottom-sm">
												<span class="input-group-addon"> <i
													class="fa fa-linkedin fa-fw" aria-hidden="true"></i>
												</span> 
												<input id="NewLinkedin" class="form-control" type="text"
													placeholder="Linkedin Profile" maxlength="255">
											</div>
											<br />
											<div class="input-group margin-bottom-sm">
												<span class="input-group-addon"> <i
												class="fa fa-google fa-fw" aria-hidden="true"></i>
												</span> 
												<input id="NewGoogle" class="form-control" type="text"
													placeholder="Google Scholar Profile" maxlength="255">
											</div>
											<br />
											<div class="input-group margin-bottom-sm">
												<span class="input-group-addon"> <i
													class="fa fa-twitter fa-fw" aria-hidden="true"></i>
												</span> 
												<input id="NewTwitter" class="form-control" type="text"
													placeholder="Twitter Account" maxlength="255">
											</div><br/>
											<input id="NewStatus" type="checkbox" class="pull-left" data-toggle="toggle" 
												data-on="Enabled" data-off="Disabled">
											<button class="btn btn-success pull-right"
												type="submit">Submit</button>
										</div>
									</form>
								</div>
								<div class="tab-pane fade well" id="modifyTeam">
									<div class="form-group" style="display:none;">
										<label class="control-label">Select a member</label>
										<select class="form-control" id="modifyTeamList"></select>
									</div>

									<div class="stepwizard col-lg-offset-3 col-md-offset-3" align="center">
										<div class="stepwizard-row setup-panel">
											<div class="stepwizard-step">
												<a href="#step-1_modify" type="button"
													class="btn btn-primary btn-circle">
													<i class="fa fa-user"></i>
												</a>
											</div>
											<div class="stepwizard-step">
												<a href="#step-2_modify" type="button"
													class="btn btn-default btn-circle">
													<i class="fa fa-image"></i>
												</a>
											</div>
											<div class="stepwizard-step">
												<a href="#step-3_modify" type="button"
													class="btn btn-default btn-circle"><!--  disabled="disabled" -->
													<i  class="fa fa-share-alt"></i>
												</a>
											</div>
										</div>
									</div>
									
									<form id="modifyTeamMember" action="/DMS/TeamUpdate" method="post"
										style="padding:10px; margin-top: -15px;" novalidate>
										<div class="row setup-content" id="step-1_modify">
											<h4>
												Information
												<div class="pull-right">
													<i class="fa fa-user"></i>
												</div>
											</h4>
											<div class="form-group">
												<label class="control-label">Name*</label> 
												<input id="modifyName" maxlength="50" type="text" required="required"
													class="form-control" placeholder="Enter Name" />
											</div>
											<div class="form-group">
												<label class="control-label">Designation*</label> 
												<input id="modifyDesignation" maxlength="100" type="text" 
													class="form-control" placeholder="Enter Designation" />
											</div>
											<div class="form-group">
												<label class="control-label">Affiliation*</label>
												<input id="modifyAffiliation" maxlength="100" type="text"
													class="form-control" placeholder="Enter Affiliated Insitute Name" />
											</div>
											<div class="form-group">
												<label class="control-label">Member Type*</label>
											
												<select class="form-control" id="modifyuserType">
													<option>Principle Investigator</option>
													<option>Current Member</option>
													<option>Past Member</option>
												</select>
											</div>
											<div class="form-group">
												<label class="control-label">Project List*</label>
											
												<select class="form-control multiselect" id="modifyUserProjectListMultiSelect" name="options[]" multiple="multiple">
													<!-- Autofill by JS -->
												</select>
												
											</div>
											<button class="btn btn-primary nextBtn pull-right"
												type="button">Next</button>
										</div>
										<div class="row setup-content" id="step-2_modify">
											<h4>
												Image
												<div class="pull-right">
													<i class="fa fa-image"></i>
												</div>
											</h4>
											
											<div class="form-group"> 
												<input name="name" maxlength="255" type="url"
													class="form-control" placeholder="Enter Image URL Here" 
													id="modifyImageUrl" onchange="pullImage('modifyImageUrl','modifyImagePreview')"/>
													<br/>
												<img id="modifyImagePreview" class="img-fuild rounded" src=""
													onerror="this.src='/DMS/images/team/error.png'" 
													width="150px" height="150px">
											</div>
											
											<button class="btn btn-primary nextBtn pull-right"
												type="button">Next</button>
										</div>
										<div class="row setup-content" id="step-3_modify">
											<h4>
												Social Media
												<div class="pull-right">
													<i class="fa fa-share-alt"></i>
												</div>
											</h4>
											
											<div class="input-group margin-bottom-sm">
												<span class="input-group-addon"> <i
													class="fa fa-anchor fa-fw" aria-hidden="true"></i>
												</span> 
												<input id="modifyHomepage" class="form-control" type="text"
													placeholder="Homepage" maxlength="255">
											</div>
											<br />
											<div class="input-group margin-bottom-sm">
												<span class="input-group-addon"> <i
													class="fa fa-linkedin fa-fw" aria-hidden="true"></i>
												</span> 
												<input id="modifyLinkedin" class="form-control" type="text"
													placeholder="Linkedin Profile" maxlength="255">
											</div>
											<br />
											<div class="input-group margin-bottom-sm">
												<span class="input-group-addon"> <i
												class="fa fa-google fa-fw" aria-hidden="true"></i>
												</span> 
												<input id="modifyGoogle" class="form-control" type="text"
													placeholder="Google Scholar Profile" maxlength="255">
											</div>
											<br />
											<div class="input-group margin-bottom-sm">
												<span class="input-group-addon"> <i
													class="fa fa-twitter fa-fw" aria-hidden="true"></i>
												</span> 
												<input id="modifyTwitter" class="form-control" type="text"
													placeholder="Twitter Account" maxlength="255">
											</div><br/>
											<input id="modifyStatus" type="checkbox" class="pull-left" data-toggle="toggle" 
												data-on="Enabled" data-off="Disabled">
											<button class="btn btn-success pull-right"
												type="submit">Submit</button>
										</div>
									</form>	
								</div>
							</div>
							<span style="padding-left: 20px;"> <i
								class="fa fa-warning"></i> <span id="projectMessage">No
									updates related to a team or a member.</span>
							</span> <br /> <br />
						</div>
						<!--/tabs-->
					</div>
					<!--/col-span-6-->
				</div>
				<!--/col-span-9-->
			</div>
		</div>
		<!-- /Main -->

		<footer class="text-center">
			This Bootstrap 3 dashboard layout is compliments of <a
				href="http://www.bootply.com/85850"><strong>Bootply.com</strong></a>
		</footer>

		<div class="modal" id="addWidgetModal">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title">Add Widget</h4>
					</div>
					<div class="modal-body">
						<p>Add a widget stuff here..</p>
					</div>
					<div class="modal-footer">
						<a href="#" data-dismiss="modal" class="btn">Close</a> <a href="#"
							class="btn btn-primary">Save changes</a>
					</div>
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal-dalog -->
		</div>
		<!-- /.modal -->
		<script>
		$(document).ready(function() {
			$('[rel="tooltip"]').tooltip({
				container : 'body'
			});
		});
	</script>
	<script>
	$('#addNewMember').submit(function(event) {
		// Stop form from normal submission
		event.preventDefault();
		addTeamMember(event);
	});
	
	$('#modifyTeamMember').submit(function(event) {
		// Stop form from normal submission
		event.preventDefault();
		updateTeamMember(event);
	});
	
	</script>
	<script>

	
	</script>
</body>
</html>
<%
	} else {
		String redirectURL = "/DMS/jsp/LoginAdmin.jsp";
		response.sendRedirect(redirectURL);
	}
%>