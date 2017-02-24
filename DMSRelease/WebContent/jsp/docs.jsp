<%@page import="defaults.SessionVariableList"%>
<%@ page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
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
        window.onload = function() {
            var uid = '<%= session.getAttribute(SessionVariableList.USER_ID)%>';
            var pid = '<%= session.getAttribute(SessionVariableList.CURRENT_PROJECT_ID)%>';
            var project = '<%= session.getAttribute(SessionVariableList.CURRENT_PROJECT_NAME)%>';
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
                    <h2 class="page-header">Documentation</h2>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-12">

<!-- ************ start of documentation ************ -->

<!--<h1 id="overview">Overview</h1>-->
<h3 id="background">Background</h3>
<p>This manual serves as an introduction to using the simDMS system, a web application designed to simplify temporal analysis, similarity search, and graph analysis of large-scale multivariate time series data. You will be introduced to the different features of simDMS, receive an overview of the query language syntax, and learn about the storage mechanisms and operational considerations of the system.</p>
<h3 id="data-models">Data Models</h3>
<p>simDMS is a heterogenous system, able to query time series data, metadata, and graph data simultaneously. These are stored in type-appropriate, open-source database systems, including MySQL, MongoDB, and BaseX. Cross-component communication has been standardized on JSON, providing a robust and flexible system to integrate with the web front-end.</p>
<h3 id="query-syntax">Query Syntax</h3>
<p>simDMS queries have two components; the first query loads the parameters from the metadata, and the second query uses those results in a query against the time series information. Both of these query components are required for correct functionality, and each line of a given compound query (except the last line) should be delimited by a caret: <code>^</code></p>
<p>The XML query uses a similar syntax to the classical SELECT-FROM-WHERE SQL language called FLWOR:</p>
<ul>
<li><strong>F</strong>or: Selects the dataset to query</li>
<li><strong>L</strong>et: Defines node objects within the XML</li>
<li><strong>W</strong>here: Defines the conditions by which the results should be filtered</li>
<li><strong>O</strong>rder By: Sets the ordering in which the results appear</li>
<li><strong>R</strong>eturn: List the results which should be displayed to the user</li>
</ul>
<p>The time series query uses a MongoDB-style set-matching method, where one or more properties are listed, and each property can match any of a provided set. All properties should be followed by semicolons: <code>;</code></p>
<p>Here is a sample query:</p>
<pre><code>for $ensemble in fn:collection(&#39;Epidemic&#39;) ^
let $disease := $ensemble/project/scenario/model/disease ^
let $trigger := $ensemble/project/scenario/trigger ^
where $disease/transmissionRate &lt;= 0.6 and
$disease/transmissionRate &gt;= 0.3 and
$disease/recoveryRate = 0.5 and
$trigger/@type = &quot;Vaccination&quot; ^
order by $disease/transmissionRate descending ^
return $disease/transmissionRate, $disease/recoveryRate ^
state = {AZ, CA, NM}; model = {SEIR, SIR}; properties = {Infected, Incidence, Deaths};
</code></pre><h1 id="using-the-system">Using the System</h1>
<h3 id="query-management">Query Management</h3>
<p>Once you have an understanding of the query language, you&#39;re ready to start creating queries, which can be done under the &#39;Query Management&#39; section of the website. As many queries may need to be repeated as the datasets evolve, each query can be stored and associated with your account. Each query can be named and given a description to help categorize and identify your queries, and of course, these queries can always be modified later as needed.</p>
<p><img src="/DMS/images/docs/?/query-mgmt.png"></p>
<h3 id="data-management">Data Management</h3>
<p>However, these queries are only useful when they have data to analyze. To load new data into the system, navigate to the &#39;Data Management&#39; section. Collections of data are called &#39;ensembles&#39;, and each may contain several different collections of data, including time series information and metadata. All data imported into the system should be in CSV format, following the standard STEM format.</p>
<p>Data can be added to existing ensembles, or used as an initial input for a new ensemble. However, one potential limitation to consider is that data cannot be removed or updated within existing ensembles. For example, an ongoing study could append new information to the ensemble as it became available, but a disease&#39;s transmission rate could not be updated within the metadata later on as more accurate figures became available.</p>
<p><img src="/DMS/images/docs/?/data-mgmt.png" width=100%></p>
<h3 id="execution">Execution</h3>
<p>Once all the data has been imported, and queries have been configured, the system is ready to perform your analysis; to do so, please select the &#39;Execution&#39; section on the navigation bar. First, select the type of query that you&#39;d like to conduct.</p>
<p>When conducting a metadata query, choose a saved query from the dropdown. Your query will be inserted into the query field, where final modifications can be made. Below, several additional filtering options are available to help refine the results. Once all settings have been finalized, click &#39;Execute Query&#39; to perform the analysis. An example of this interface can be seen here:</p>
<p><img src="/DMS/images/docs/?/query.png"></p>
<p>When conducting a similarity search, the inputs are slightly different. Instead of choosing an existing query, you will need to supply two source files to use as a basis for comparison. The input formats are identical to the Data Management requirements for ensembles, specifically CSV format in the standard STEM format. Once supplied, the dropdowns can be used to select which data to search. Finally, click &#39;Search Similar Series&#39; to perform the analysis.</p>
<p><img src="/DMS/images/docs/?/similarity1.png" width=100%></p>
<h1 id="understanding-the-results">Understanding the Results</h1>
<h3 id="hierarchy">Hierarchy</h3>
<p>Once your query is complete, the results will appear immediately underneath the query section. At first, it will appear as a simple hierarchical display, as seen here:</p>
<p><img src="/DMS/images/docs/?/hierarchy1.png" width=100%></p>
<p>Click on any entry text in the hierarchy to expand that subtree. The number and depth of the subtrees will be dependent on the composition and complexity of the query. At any time, you may collapse the entire hierarchy by clicking the &#39;Collapse Hierarchy&#39; button at the upper-right.</p>
<h3 id="visualization">Visualization</h3>
<p>After the subtrees have been sufficiently expanded, result heatmaps will appear under their associated subquery headings. These heatmaps represent the time series data, with time on the X-axis and dimensions on the Y-axis:</p>
<p><img src="/DMS/images/docs/?/heatmap1.png" width=100%></p>
<h3 id="detailed-view">Detailed View</h3>
<p>For additional detail, any heatmap can be clicked to pop open a new window containing the Detailed View. This view allows users to refine the data using additional filters, provides access to more functiality such as downloading the time series or metadata, and labels the heatmap Y-axis with the relevant information.</p>
<p>This view should be used when additional information needs to be extracted from a specific region, or when users would like to drill down further into an analysis. An example can be seen here:</p>
<p><img src="/DMS/images/docs/?/details1.png" width=100%></p>
<h3 id="searching-within-results">Searching Within Results</h3>
<p>As query results become more complex, it may be desirable to find particular pieces of information within the results without searching for them manually. The &#39;Search&#39; button at the upper-right of the results hierarchy can be used for this purpose.</p>
<p>Enter in the desired attribute to search for, followed by a <code>;</code>, and then click the &#39;Search&#39; button. This will filter the results to only display entries within the hierarchy that match the given pattern:</p>
<p><img src="/DMS/images/docs/?/subsearch.png" width=100% ></p>

<!-- ************ end of documentation ************ -->

</div>
            </div>
        </div>
    </div>
    <script>
    var project = '<%= session.getAttribute(SessionVariableList.CURRENT_PROJECT_NAME)%>';
    console.log(project.toLowerCase());
    $('img').each(function(){
    	var imgloc = $(this).attr('src');
    	console.log(imgloc);
    	imgloc = imgloc.replace('?',project.toLowerCase());
    	console.log(imgloc);
    	$(this).attr('src',imgloc);
    });
    </script>
    <script src="/DMS/js/sys/bootstrap.min.js"></script>
    <script src="/DMS/DashUser/vendor/metisMenu/metisMenu.min.js"></script>
    <script src="/DMS/DashUser/dist/js/sb-admin-2.js"></script><br/><br/><br/>
</body>
</html>

<%
    } else {
        String redirectURL = "/DMS/jsp/LoginUser.jsp";
        response.sendRedirect(redirectURL);
    }
%>