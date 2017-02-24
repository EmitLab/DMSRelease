<%@page import="defaults.SessionVariableList"%>
<%@ page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%
	if (session.getAttribute(SessionVariableList.USER_LOGIN_STATE) != null
			&& session.getAttribute(SessionVariableList.USER_LOGIN_STATE)
					.equals(SessionVariableList.USER_LOGIN_STATES[1])) {
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
<link href="/DMS/css/dev/system.css" rel="stylesheet">
<link href="/DMS/css/sys/bootstrap.min.css" rel="stylesheet">
<link href="/DMS/DashUser/vendor/metisMenu/metisMenu.min.css"
	rel="stylesheet">
<link href="/DMS/DashUser/dist/css/sb-admin-2.css" rel="stylesheet">
<script src="https://use.fontawesome.com/b48a93c02f.js"></script>
<script src="/DMS/js/sys/jquery-3.1.1.min.js"></script>
<script src="/DMS/js/sys/createXmlRequest.js"></script>
<script src="/DMS/js/sys/biginteger.js"></script>
<script src="/DMS/js/sys/d3.v2.js"></script>
<script src="/DMS/js/sys/spin.js"></script>
<script src="/DMS/js/sys/jszip.js"></script>
<script src="/DMS/js/dev/initUserDash.js"></script>
<script src="/DMS/js/dev/MetaToSimilarity.js"></script>
<script src="/DMS/js/dev/System.js"></script>
<script src="/DMS/js/dev/matchrange.js"></script>
<script src="/DMS/js/dev/SecondWindowSimilarity.js"></script>
<script src="/DMS/js/dev/heatmap.js"></script>
<script src="/DMS/js/dev/getGranularity.js"></script>
<script src="/DMS/js/dev/getMetaString.js"></script>
<script src="/DMS/js/dev/legend.js"></script>
<script src="/DMS/js/dev/hierarchy.js"></script>
<script src="/DMS/js/dev/collapsehierarchy.js"></script>
<script src="/DMS/js/dev/ordercluster.js"></script>
<script src="/DMS/js/dev/parameter.js"></script>
<script src="/DMS/js/dev/queryprocess.js"></script>
<script src="/DMS/js/dev/filereader.js"></script>
<script src="/DMS/js/dev/Download.js"></script>
<script src="/DMS/js/dev/showhint.js"></script>
<script src="/DMS/js/dev/searchfunctions.js"></script>
<script src="/DMS/js/dev/metadata2win.js"></script>
<script src="/DMS/js/dev/grayscaleheatmap.js"></script>
<script src="/DMS/js/dev/inputStatusCheck.js"></script>
<script src="/DMS/js/dev/window2creation.js"></script>
<script src="/DMS/js/dev/effects.js"></script>
<script src="/DMS/js/dev/AdaptiveMenu.js"></script>
<script src="/DMS/js/dev/downsampling.js"></script>
<script src="/DMS/js/dev/remove.js"></script>
<script src="/DMS/js/dev/utility.js"></script>
<script>
    var Project = '<%=session.getAttribute(SessionVariableList.CURRENT_PROJECT_NAME)%>';
    var System = "mongodb";
    var cursorX;
    var cursorY;
    var SimilarityDataRetrived= new Array();
    document.onmousemove = function(e){
        cursorX = e.pageX - 250;
        cursorY = e.pageY - 640;
    }
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
            var uid = '<%=session.getAttribute(SessionVariableList.USER_ID)%>';
            var pid = '<%=session.getAttribute(SessionVariableList.CURRENT_PROJECT_ID)%>';
		initUserDashTopMenu(uid, pid);
		initUserDashLeftSideBarMenu();
		initUserDashFooter(pid);
		getQueryList();
		startTimeCheckedStatus();
		endTimeCheckedStatus();
		samplingCheckedStatus();
		grayScaleCheckedStatus();
		$(".loader").fadeOut("slow");
		// console.clear();
	};
</script>
<style>
.sel-box {
	position: relative;
}

.sel-box ul {
	max-height: 350px;
	overflow: scroll;
	overflow-x: hidden;
	border: 1px lightgray solid;
	position: relative;
	left: -10px;
	top: 10px;
	width: 100%;
}

a {
	text-decoration: none;
}

#select {
	display: block;
	width: auto;
	height: 20px;
	padding-top: -5px;
}

.toc-odd {
	position: absolute;
	width: auto;
	display: none;
	border: 1px lightgray solid;
	cursor: pointer;
	padding-left: -20px !important;
}

.toc-odd li {
	text-align: left;
	list-style-type: none;
	color: black;
}
</style>
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
			<br />
			<div class="row">
				<div class="col-lg-12">
					Query Type : <label class="radio-inline"><input
						type="radio" name="optradio" query="metadata"
						onclick="toggleQueryMode(1)" checked>Metadata Query </label> <label
						class="radio-inline"><input type="radio" name="optradio"
						query="similarity" onclick="toggleQueryMode(2)">Similarity
						Search</label>
				</div>
			</div>
			<hr />
			<div class="row" queryType="metadata">
				<div class="col-lg-8">
					<select id="queryList" style="height: 30px;" onchange="getQuery()"></select>
					<div class="pull-right">
						<div class="btn-group">
							<button class="btn btn-default" query="add" data-toggle="tooltip"
								data-placement="top" title="Save as New" onclick="addQuery()">
								<i class="fa fa-plus"></i>
							</button>
							<button class="btn btn-default" query="modify"
								data-toggle="tooltip" data-placement="top"
								title="Save as Modified" disabled onclick="modifyQuery()">
								<i class="fa fa-pencil"></i>
							</button>
							<button class="btn btn-default" query="delete"
								data-toggle="tooltip" data-placement="top" title="Delete"
								disabled onclick="deleteQuery()">
								<i class="fa fa-trash"></i>
							</button>
							<script>
								$(function() {
									$('[data-toggle="tooltip"]').tooltip({
										container : 'body'
									});
								});
							</script>
						</div>
					</div>
				</div>
				<div class="col-lg-4">
					<div class="form-group">
						<label>Name : <span query="nameError"></span></label> <input
							type="text" query="name" class="form-control"
							placeholder="Query Name" />
					</div>
				</div>
				<div class="col-lg-8">
					<div class="form-group">
						<label>Query : <span query="queryError"></span></label>
						<textarea rows="8" query="query" class="form-control"
							placeholder="Executable Query"></textarea>
					</div>
				</div>
				<div class="col-lg-4">
					<div class="form-group">
						<label>Description : <span query="descriptionError"></span></label>
						<textarea rows="8" query="description" class="form-control"
							placeholder="Query Description"></textarea>
					</div>
				</div>
				<div class="col-lg-8">
					<table id="downsamplingTable">
						<tr>
							<td><input type="checkbox" id="startCheck"
								onclick="startTimeCheckedStatus()" />Start Time</td>
							<td><input type="date" id="startDate" value="2012-01-01" />
								<input type="time" id="startTime" value="12:00:00" /></td>
							<td rowspan="4">
								<table id="grayScaleTable" width="100%">
									<tr>
										<td>Visual Comparison</td>
										<td><select id="grayScaleMenu"
											style="height: 22px; width: 120px;"
											onchange="grayScaleCheckedStatus()">
												<option key="1">Yes</option>
												<option key="2" selected="selected">No</option>
										</select></td>
									<tr>
										<td>Comparison Type</td>
										<td><select id="grayScaleTypeMenu"
											onchange="Changemenu();" style="height: 22px; width: 120px;">
												<option key="1" selected="selected">Mean Difference</option>
												<option key="2">Feature Based</option>
										</select></td>
									</tr>
									<tr>
										<td id="Difftype"><label id=diffLabel>Difference
												Type</label></td>
										<td id="DiffSelection"><select
											id="grayScaleDifferenceMenu"
											style="height: 22px; width: 120px;">
												<option key="1" selected="selected">Absolute</option>
												<option key="2">Percentage</option>
										</select></td>
									</tr>
									<tr>
										<td>
											<div id="TDcurvdog" style="visibility: hidden" width=0>
												Feature Selection</div>
										</td>
										<td>
											<div id="SelectTDcurvdog" style="visibility: hidden" width=0>
											</div>
										</td>
									</tr>
									<tr>
										<td>
											<div id="NormType" style="text-align: left;">
												<label id=normLabel>Normalization</label>
											</div>
										</td>
										<td>
											<!-- <td>Normalization Type<br/> -->
											<div id="NormSelection">
												<select id="grayScaleNormalizationMenu"
													style="height: 22px; width: 120px;">
													<option key="1" selected="selected">Hierarchy</option>
													<option key="2">Simulation</option>
													<option key="3">Variate</option>
												</select>
											</div>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td><input type="checkbox" id="endCheck"
								onclick="endTimeCheckedStatus()" />End Time</td>
							<td><input type="date" id="endDate" value="2012-08-31" /> <input
								type="time" id="endTime" value="12:00:00" /></td>
						</tr>
						<tr style="vertical-align: top;">
							<td><input type="checkbox" id="samplingCheck"
								onclick="samplingCheckedStatus()" />Down Sampling</td>
							<td><input type="text" id="sampleFactor" value="1"
								style="width: 55px; text-align: right; padding-right: 5px;" />
								<select id="sampleUnit">
									<option key="Y">Year</option>
									<option key="M">Month</option>
									<!--    <option key="W">Weeks</option> -->
									<option key="D" selected="selected">Day</option>
									<option key="h">Hour</option>
									<option key="m">Minute</option>
									<option key="s">Second</option>
							</select> <select id="sampleFunction">
									<option selected="selected" key="avg">Average</option>
									<option key="min">Minimum</option>
									<option key="max">Maximum</option>
							</select></td>
						</tr>
						<tr>
							<td colspan="2"></td>
						</tr>
					</table>
				</div>
				<div class="col-lg-4">&nbsp;</div>
				<br />
				<div class="col-lg-12">
					<br />
					<button class="btn btn-warning" onclick="sendQuery()">
						Execute Query</button>
					<hr />
				</div>
			</div>
			<div class="row" queryType="similarity" style="display: none">
				<div class="col-lg-6" id="uploadTimeSeriesFile">
					<div class="form-group">
						<label>Select Time Series : </label> <input type="file"
							class="form-control" />
					</div>
				</div>
				<div class="col-lg-6" id="uploadGraphFile">
					<div class="form-group">
						<label>Select Graph File : </label> <input type="file"
							class="form-control" />
					</div>
				</div>

				<div class="col-lg-12" id="queryHeatmap"></div>

				<div class="col-lg-12">
					<div class="form-inline">
						<label> Ensemble : </label> <select class="form-control"
							id="EnsembleSelect" onchange="GetModelList()">
							<option>Select an Ensemble</option>
						</select> <label> Model : </label> <select class="form-control"
							id="ModelSelect" onchange="GetPropertyList()">
							<option>Select a Model</option>
						</select> <label> Property : </label> <select class="form-control"
							id="PropertySelect" onchange="GetZoneList()">
							<option>Select a Property</option>
						</select> <label> Zone : </label> <select class="form-control"
							id="ZoneSelect" style="display: none;">
							<option>Select a Zone</option>
						</select>
						<div class="form-control">
							<div class="sel-box">
								<span id='select'> <span id="selectText">Select
										Zone(s)</span>
									<div class='pull-right'>
										<i class='fa fa-caret-down'></i>
									</div>
								</span>

								<ul class='toc-odd level-1' id="sel-option"
									style="padding-top: -25px !important;">
									<!--
                                <li><input type="checkbox"> It's finally here</li>
                                <li><input type="checkbox"> Improvements</li>
                                <li><input type="checkbox"> Handling</li>
                                -->
								</ul>
							</div>
						</div>
						<button class="btn btn-warning" onclick="SearchSimilar()">Search
							Similar Series</button>
					</div>
				</div>
			</div>
			<div class="row" id="resultDisplay" style="display: none;">
				<div class="col-lg-12" align="center"
					style="vertical-align: middle;">
					<input type="text" id="minlocal" onchange="changeColorLegend()" />
					<span id="LegendDIV"></span> <input type="text" id="maxlocal"
						onchange="changeColorLegend()" />
				</div>
				<div class="col-lg-12" class="pull-right">
					<br />
					<div class="pull-right" align="right" style="text-algin:">
						<button class="btn btn-primary btn-xs"
							onclick="collapseAll('hierarchy')">Collapse Hierarchy</button>
						<input type='text' id='search-string'
							placeholder="Model;Property;SimulationID" name='search-string'
							class="btn btn-default btn-xs"
							style="cursor: text; text-align: right">
						<button class="btn btn-success btn-xs" name="answer"
							onclick="searchText()">Search</button>
						<a href="#" class="btn btn-default btn-xs"
							onMouseover="showhint('Fill the bar using the syntax. E.g. SIR;Deaths;224', this, event, '180px')">
							Help<i class="fa fa-question fa-fw"></i>
						</a>
					</div>
					<div id="SpinnerEvent"></div>
					<ul id="hierarchy" class="tree" clustername=""></ul>
				</div>
				<div class="col-lg-12">

					<br /> <br /> <br />
				</div>
			</div>
	</div>
		</div>
		
	<script src="/DMS/js/sys/bootstrap.min.js"></script>
	<script src="/DMS/DashUser/vendor/metisMenu/metisMenu.min.js"></script>
	<script src="/DMS/DashUser/dist/js/sb-admin-2.js"></script>
	<script>
		$(function() {
			/*
			$('.sel-box ul li').click(function(e){
			    e.stopPropagation();
			});*/
			$('.sel-box')
					.click(
							function(e) {

								if (!$('#sel-option').is(':visible')) {
									// console.log("I am here 1");
									$('#sel-option').toggle();
								} else {
									// console.log("I am here 2");
									// console.log('Is li chk - ' + $(e.target).is('li'));
									// console.log('Is input chk - ' + $(e.target).is('input'));
									if ($(e.target).is('li')
											|| $(e.target).is('input')) {
										//$('#sel-option').toggle();
										// console.log("I am here 3");
										// console.log("Counting");
										var count = 0;
										if ($(e.target).is('input')) {
											if ($(this).prop("checked")) {
												$(this).prop("checked", false);
											} else {
												$(this).prop("checked", true);
											}
										}
										$('#sel-option input:checked').each(
												function() {
													count += 1;
												});
										$("#selectText")
												.text(
														count
																+ ((count == 1) ? " zone "
																		: " zones ")
																+ "selected");
									} else {
										$('#sel-option').toggle();
										// console.log("I am here 4");
									}
								}

							});
			$('#sel-option li').click(
					function() {
						// console.log("Counting");
						var count = 0;
						if ($(this).find("input").prop("checked")) {
							$(this).find("input").prop("checked", false);
						} else {
							$(this).find("input").prop("checked", true);
						}
						$('#sel-option input:checked').each(function() {
							count += 1;
						});
						$("#selectText").html(
								count + ((count == 1) ? " zone " : " zones ")
										+ "selected");
					});
		})
	</script>
</body>
</html>

<%
	} else {
		String redirectURL = "/DMS/jsp/LoginUser.jsp";
		response.sendRedirect(redirectURL);
	}
%>