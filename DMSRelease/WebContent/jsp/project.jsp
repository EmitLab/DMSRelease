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
    <link href="/DMS/DashUser/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="/DMS/DashUser/vendor/metisMenu/metisMenu.min.css" rel="stylesheet">
    <link href="/DMS/DashUser/dist/css/sb-admin-2.css" rel="stylesheet">
    <script src="https://use.fontawesome.com/b48a93c02f.js"></script>
    <style>
        .loader {
            position: fixed;
            left: 0px;
            top: 0px;
            width: 100%;
            height: 100%;
            z-index: 9999;
            background: url('/DMS/images/loader.gif') 50% 50% no-repeat rgb(249,249,249);
        }
        .footer {
            position: fixed;
            bottom: 0;
            width:100%;
            z-index:9999;
            padding: 1rem;
            background-color: #efefef;
            //text-align: center;
        }
        .error{
            color:firebrick !important;
            border: 1px firebrick solid !important;
        }
        .sel-box{
            position:relative;
        }
        a{
            text-decoration:none;
        }
        #select{
            display:block;
            width:240px;
            height:20px;
            border:1px solid #999;
            padding:5px;
        }
        .toc-odd{
             position:absolute;
            top:32px;
            /*background:#f1f1f1;*/
            width:250px;
            display:none;
            border : 1px lightgray solid;
        }
        .toc-odd li{
            padding:5px 10px;
            /*border-bottom:1px solid #999;*/
        }
    </style>
    <script src="/DMS/js/sys/jquery-3.1.1.min.js"></script>
    <script src="/DMS/js/dev/initUserDash.js"></script>
    <script src="/DMS/js/dev/initProjectPage.js"></script>
    <script src="/DMS/js/sys/citeproc.js"></script>
    <script src="/DMS/js/sys/bibtexParse.js"></script>
    <script>
        window.onload = function() {
            var uid = '<%= session.getAttribute(SessionVariableList.USER_ID)%>';
            var pid = '<%= session.getAttribute(SessionVariableList.CURRENT_PROJECT_ID)%>';
            initUserDashTopMenu(uid, pid);
            initUserDashLeftSideBarMenu();
            initUserDashFooter(pid);
            setProjectDetails();
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
                    <h1 class="page-header">[ERR: PROJECT TITLE HERE] </h1>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-8">
                    <div class="panel panel-default" type="description">
                        <div class="panel-heading" style="height:55px; vertical-align:middle;">
                            Project
                            <div class="pull-right"> <i class="fa fa-tv"></i></div>
                        </div>
                        <div class="panel-body description">
                        </div>
                    </div>
                    <div class="panel panel-default" type="publication">
                        <div class="panel-heading" style="height:55px; vertical-align:middle;">
                            Publications
                            <div class="pull-right"> <i class="fa fa-file"></i></div>
                        </div>
                        <div class="panel-body" id="publication"></div>
                    </div>
                    <div class="panel panel-default" type="team">
                        <div class="panel-heading" style="height:55px; vertical-align:middle;">
                            Team
                            <div class="pull-right"> <i class="fa fa-user"></i></div>
                        </div>
                        <div class="panel-body" id="team">
                            <h4>Principal Investigator</h4>
                            <span id="pi"></span>
                            <h4>Current Members</h4>
                            <span id="cm"></span>
                            <h4>Past Members</h4>
                            <span id="pm"></span>
                        </div>
                    </div>
                </div>
                <div class="col-lg-4">
                    <div class="panel panel-default" type="ensembles">
                        <div class="panel-heading" style="height:55px; vertical-align:middle;">
                            My Ensembles
                            <div class="pull-right"> <i class="fa fa-database"></i></div>
                        </div>
                        <div class="panel-body">
                            <div class="list-group" id="ensembles">
                            </div> 
                        </div>
                    </div>
                    <div class="panel panel-default" type="query">
                        <div class="panel-heading" style="height:55px; vertical-align:middle;">
                            My Queries
                            <div class="pull-right"> <i class="fa fa-circle-o-notch"></i></div>
                        </div>
                        <div class="panel-body">
                            <div class="list-group" id="query"></div> 
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