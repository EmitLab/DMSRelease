<%@page import="defaults.SessionVariableList"%>
<%@ page import="java.util.*" %>
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
    <link href="/DMS/DashUser/vendor/metisMenu/metisMenu.min.css" rel="stylesheet">
    <link href="/DMS/DashUser/dist/css/sb-admin-2.css" rel="stylesheet">
    <script src="https://use.fontawesome.com/b48a93c02f.js"></script>

    <script src="/DMS/js/sys/jquery-3.1.1.min.js"></script>
    <script src="/DMS/js/dev/initUserDash.js"></script>
    <script src="/DMS/js/dev/ensembles.js"></script>
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
        	var uid = '<%= session.getAttribute(SessionVariableList.USER_ID)%>';
            var pid = '<%= session.getAttribute(SessionVariableList.CURRENT_PROJECT_ID)%>';
            uploadEnsembles(uid, pid);
        }
        window.onload = function() {
            var uid = '<%= session.getAttribute(SessionVariableList.USER_ID)%>';
            var pid = '<%= session.getAttribute(SessionVariableList.CURRENT_PROJECT_ID)%>';
            initUserDashTopMenu(uid, pid);
            initUserDashLeftSideBarMenu();
            initUserDashFooter(pid);
            getEnsembleList();
            getDBStats();
            $(".loader").fadeOut("slow");
        };
	</script>
</head>

<body id="body">
	<div class="loader"></div>
	<div class="footer">
        Developed by Emitlab
        <div class="pull-right">
            NSF #
        </div>
    </div>
    <div id="wrapper">
        <!-- Navigation -->
        <nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="DashUser.jsp">[ERR: PROJECT NAME]</a>
            </div>
            <ul class="nav navbar-top-links navbar-right" id="dashTopMenu">
            </ul>
            <div class="navbar-default sidebar" role="navigation">
                <div class="sidebar-nav navbar-collapse">
                    <ul class="nav" id="side-menu"><!-- Side Menu Here --></ul>
                </div>
            </div>
        </nav>

        <div id="page-wrapper">
            <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">Ensemble Management </h1>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-8">
                    <div class="panel panel-default">
                        <div class="panel-heading" style="height:55px;">
                            <i class="fa fa-upload fa-fw"></i> Upload Ensemble
                            <div class="pull-right">
                                <div class="btn-group">
                                    <button class="btn btn-default"  data-toggle="tooltip" data-placement="top" title="Will display a poster of Ensemble Model in a Modal onClick">
                                        <i class="fa fa-image fa-fw fa-s"></i>
                                    </button>
                                </div>
                            </div>
                        </div>
                        <div class="panel-body">
                            <div class="form-inline" > 
                                <div class="form-group">
                                    <input type="text" class="form-control" placeholder="Create a new Ensemble" 
                                        id="newEnsembleName" maxlength="30">
                                    <label> - OR - </label>
                                    <select class="form-control" style="width:200px;" id="ensembleUploadList"></select>
                                    <input class="form-control" type="file" id="simulationList" multiple webkitdirectory onchange="initProgressBar()"/>
                                    <button class="btn btn-success" id="uploadEnsemble" onclick="uploadEnsemble();">
                                        Upload
                                    </button>
                                </div>
                            </div>
                            <div id="myProgress">
                                <div id="myBar"></div>
                            </div>
                            <span class="btn btn-block" style="cursor:text !important;text-align: left !important;" id="uploadErrorMessage">
                                    Create a new ensemble or select an existing one.
                            </span>
                            <!-- 
                            <div style="padding-top:5px; padding-bottom:5px;border-bottom:1px solid lightgray;">
                                Date File
                                <div class="pull-right">Status</div>
                            </div>
                            <div id="uploadfileStatus" style="max-height: 200px;padding-top:10px;">
                            </div>
                            -->
                        </div>
                    </div>
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <i class="fa fa-table fa-fw"></i> Storage Statistics
                        </div>
                        <div class="panel-body">
                            <div id="dbStat" class="list-group"></div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-4">
                    <div class="panel panel-default">
                        <div class="panel-heading" style="height:55px; vertical-align:middle;">
                            <i class="fa fa-database fa-fw"></i> My Ensembles
                            <div class="pull-right">
                                <div class="btn-group">
                                    <button class="btn btn-default" data-toggle="tooltip" data-placement="top" title="Archive / Unarchive" onclick="archiveEnsembles()">
                                        <i class="fa fa-archive"></i>
                                    </button>
                                    <!--
                                    <button class="btn btn-default" data-toggle="tooltip" data-placement="top" title="Delete">
                                        <i class="fa fa-trash"></i>
                                    </button>
                                    
                                    <button class="btn btn-default" data-toggle="tooltip" data-placement="top" title="Statistices">
                                        <i class="fa fa-table"></i>
                                    </button>
                                    -->
                                    <script>
                                        $(function () {
                                    	  $('[data-toggle="tooltip"]').tooltip({container: 'body'});
                                    	});
                                    </script>
                                </div>
                                <!-- 
                                *******************************
                                ******** DO NOT DELETE ********
                                *******************************
                                <div class="btn-group">
                                    <button type="button" class="btn btn-default btn-xs dropdown-toggle" data-toggle="dropdown">
                                        More
                                        <span class="caret"></span>
                                    </button>
                                    <ul class="dropdown-menu pull-right" role="menu">
                                        <li>
                                            <a href="#">
                                                <i class="fa fa-archive fa-fw"> Archive</i>
                                            </a>
                                        </li>
                                        <li>
                                            <a href="#">
                                                <i class="fa fa-trash-o fa-fw"></i> Delete</a>
                                        </li>
                                        <li class="divider"></li>
                                        <li>
                                            <a href="#">
                                                <i class="fa fa-pie-chart fa-fw"></i> Statistics</a>
                                        </li>
                                    </ul>
                                </div>
                                 -->
                            </div>
                        </div>
                        <div class="panel-body">
                            <span class="btn btn-default btn-sm btn-block" style="cursor:text !important;" id="ensembleListWarning">
                                Select one or more Ensemble to proceed</span>
                                <br/>
                            <div class="list-group" id="ensembleList"></div>
                            <em>* Querying on <span class="label label-warning">Archived</span> ensembles is disabled</em>
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