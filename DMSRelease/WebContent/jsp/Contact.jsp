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
                    <h1 class="page-header">Reach us @ </h1>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-8">
                    <div class="panel panel-default id="queryPanel">
                        <div class="panel-heading">
                            <h4>
                                <i class="fa fa-map-o pull-right"></i> Navigate
                            </h4>
                        </div>
                        <div class="panel-body">
                            <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3329.986159896658!2d-111.94174218433285!3d33.42360518078157!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x872b08d8329334d3%3A0x4fe12bf6b8cf1757!2sBrickyard+Engineering%2C+699+S+Mill+Ave%2C+Tempe%2C+AZ+85281!5e0!3m2!1sen!2sus!4v1482866659080" width="100%" height="287" frameborder="0" style="border:0" allowfullscreen></iframe>
                        </div>
                    </div>
                </div>
                <div class="col-lg-4">
                    <div class="panel panel-default">
                        <div class="panel-heading"
                            style="height: 55px; vertical-align: middle;">
                            Address
                            <div class="pull-right">
                                <i class="fa fa-map-marker fa-fw fa-2x"></i>
                            </div>
                        </div>
                        <div class="panel-body">
                            <h4>Address:</h4>
                            <em>K. Selcuk Candan</em><br/>
                            School of Computing, Informatics and Decision Science Engineering<br/>
                            Ira A. Fulton School of Engineering<br/>
                            Arizona State University<br/>
                            699 S Mill Ave., Suite #553<br/>
                            Tempe, AZ<br/>
                            <h4>Phone:</h4>
                            +1-(480) 965-2770
                            <h4>Fax:</h4>
                            +1-(480) 965-2751
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