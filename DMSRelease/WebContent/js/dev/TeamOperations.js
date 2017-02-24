/**
 *  CORS : https://www.html5rocks.com/en/tutorials/cors/
 */

function listProjectonTeamPage(){
	console.log("listProjectonTeamPage");
	$.ajax({
		type:"POST",
		url:"/DMS/ProjectListFilter",
		dataType:"json",
		success:function(data){
			console.log("listProjectonTeamPage data");
			for(var i = 0; i < data.length; i++){
				$("#newUserProjectListMultiSelect")
					.append("<option id=" + data[i].id + ">" + data[i].name + "</option>");
				
				$("#modifyUserProjectListMultiSelect")
				.append("<option id=" + data[i].id + ">" + data[i].name + "</option>");
			}
			
			  $("#newUserProjectListMultiSelect").multiselect('selectAll', false);
			  $("#newUserProjectListMultiSelect").multiselect('updateButtonText');
			  
			  $("#modifyUserProjectListMultiSelect").multiselect('selectAll', false);
			  $("#newUserProjectListMultiSelect").multiselect('updateButtonText');
		}
	});
}

function showTeam(){
	$.ajax({
		type     : "POST",
		url      : "/DMS/TeamList",
		dataType : 'json',
		success  : function(data){

			// LINK - http://stackoverflow.com/questions/881510/sorting-json-by-values
			var prop = 'name';
			var asc = true;
			data = data.sort(function(a, b) {
		        if (asc) {
		            return (a[prop] > b[prop]) ? 1 : ((a[prop] < b[prop]) ? -1 : 0);
		        } else {
		            return (b[prop] > a[prop]) ? 1 : ((b[prop] < a[prop]) ? -1 : 0);
		        }
		    });
			
			
			$("#pi").html("");
			$("#cm").html("");
			$("#pm").html("");
			var appendKey = "";
			document.getElementById("modifyTeamList").innerHTML = null;

			var pi = {label:"Principle Investigator",children:[]};
			var cm = {label:"Current Member", children:[]};
			var pm = {label:"Past Member", children:[]};
			var teamJSON = [];
			for(var i = 0; i < data.length; i++){

				if(data[i].type == "Principle Investigator") {
					appendKey = "pi";
					pi.children.push({label:data[i].name,"value":data[i].id});
				} else if(data[i].type == "Current Member") {
					appendKey = "cm";
					cm.children.push({label:data[i].name,"value":data[i].id});
				} else {
					appendKey = "pm";
					pm.children.push({label:data[i].name,"value":data[i].id});
				}

				var divID = data[i].id + "List";

				var mainDiv = document.createElement("div");
				mainDiv.setAttribute("class","media block-update-card");
				mainDiv.setAttribute("id",divID);
				//mainDiv.setAttribute("style","border-top:1px solid black");
				mainDiv.style.borderTop = "1px lightgray solid";
				mainDiv.setAttribute("onMouseEnter","toggleModifySpan('" + divID + "');");
				mainDiv.setAttribute("onMouseLeave", "toggleModifySpan('" + divID + "');");

				var imgDiv = document.createElement("div");
				imgDiv.setAttribute("class","pull-left");

				var imgTag   = document.createElement("img");
				imgTag.setAttribute("class","img-fuild rounded");
				imgTag.setAttribute("src",data[i].image);
				imgTag.setAttribute("onerror","this.src='/DMS/images/team/error.png'");
				imgTag.setAttribute("width","100px");
				imgTag.setAttribute("height","100px");

				imgDiv.appendChild(imgTag);
				mainDiv.appendChild(imgDiv);

				var socialDiv = document.createElement("div");
				socialDiv.setAttribute("class","card-action-pellet btn-toolbar pull-right");
				socialDiv.setAttribute("role","toolbar");

				if(data[i].linkedin){
					var lDiv = document.createElement("div");
					var aNode = document.createElement("a");
					aNode.setAttribute("href",data[i].linkedin);
					var iNode = document.createElement("i");
					aNode.setAttribute("class","fa fa-linkedin");
					aNode.appendChild(iNode);
					lDiv.appendChild(aNode);
					lDiv.setAttribute("class","btn-group");
					socialDiv.appendChild(lDiv);
				}
				if(data[i].scholar){
					var sDiv = document.createElement("div");
					var aNode = document.createElement("a");
					aNode.setAttribute("href",data[i].scholar);
					var iNode = document.createElement("i");
					aNode.setAttribute("class","fa fa-google");
					aNode.appendChild(iNode);
					sDiv.appendChild(aNode);
					sDiv.setAttribute("class","btn-group");
					socialDiv.appendChild(sDiv);
				}
				if(data[i].twitter){
					var tDiv = document.createElement("div");
					var aNode = document.createElement("a");
					aNode.setAttribute("href",data[i].twitter);
					var iNode = document.createElement("i");
					aNode.setAttribute("class","fa fa-twitter");
					aNode.appendChild(iNode);
					tDiv.appendChild(aNode);
					tDiv.setAttribute("class","btn-group");
					socialDiv.appendChild(tDiv);
				}
				if(data[i].homepage){
					var hDiv = document.createElement("div");
					var aNode = document.createElement("a");
					aNode.setAttribute("href",data[i].homepage);
					var iNode = document.createElement("i");
					aNode.setAttribute("class","fa fa-link");
					aNode.appendChild(iNode);
					hDiv.appendChild(aNode);
					hDiv.setAttribute("class","btn-group");
					socialDiv.appendChild(hDiv);
				}

				mainDiv.appendChild(socialDiv);

				var contentDiv = document.createElement("div");
				contentDiv.setAttribute("class","media-body update-card-body");

				var emptyDiv = document.createElement("div");

				var nameSpan = document.createElement("span");
				nameSpan.setAttribute("style","font-size: large;");
				var nameSpanText = document.createTextNode(data[i].name);
				nameSpan.appendChild(nameSpanText);

				emptyDiv.appendChild(nameSpan);
				emptyDiv.appendChild(document.createTextNode(", "));

				var infoSpan      = document.createElement("span");

				var degreeText    = document.createTextNode(data[i].designation + ", ");
				var brNode        = document.createElement("br"); 
				var instituteText = document.createTextNode(data[i].institute);

				infoSpan.appendChild(degreeText);
				infoSpan.appendChild(brNode);
				infoSpan.appendChild(instituteText);

				emptyDiv.appendChild(infoSpan);
				contentDiv.appendChild(emptyDiv);

				var enableSpan = document.createElement("span");
				if(data[i].status == 1){
					enableSpan.setAttribute("class","label label-success");
					var enableText = document.createTextNode("Enabled");
					enableSpan.appendChild(enableText);
				} else {
					enableSpan.setAttribute("class","label label-warning");
					var enableText = document.createTextNode("Disabled");
					enableSpan.appendChild(enableText);
				}

				contentDiv.appendChild(enableSpan);
				contentDiv.appendChild(document.createElement("br"));

				var modifyBtn = document.createElement("span");
				modifyBtn.setAttribute("class","label label-default");
				modifyBtn.setAttribute("onclick","getMemberById(" + data[i].id + ")");
				modifyBtn.setAttribute("style","cursor:pointer;display:none;");
				modifyBtn.appendChild(document.createTextNode("Modify"));
				contentDiv.appendChild(modifyBtn);

				mainDiv.appendChild(contentDiv);

				document.getElementById(appendKey).appendChild(mainDiv);
				/*
					<div class="media block-update-card">
						<div class="pull-left">
							<img class="img-fuild rounded"
								src="http://www.public.asu.edu/~ygarg/images/avatar.jpg"
								onerror="this.src='/DMS/images/team/error.png'" width="100px"
								height="100px">
						</div>
						<div class="card-action-pellet btn-toolbar pull-right"
							role="toolbar">
							<div class="btn-group fa fa-linkedin"></div>
							<div class="btn-group fa fa-twitter"></div>
							<div class="btn-group fa fa-google"></div>
							<div class="btn-group fa fa-link"></div>
						</div>
						<div class="media-body update-card-body">
							<div>
								<span style="font-size: large;">Yash Garg</span>, <span>Doctoral
									Student, <br />Arizona State University.
								</span>
							</div>
							<div>2013/12 - Present</div>
							<span class="label label-success">Enabled</span>
						</div>
					</div>
				 */
			}
			teamJSON.push(pi);
			teamJSON.push(cm);
			teamJSON.push(pm);
			$('#modifyTeamList').multiselect('dataprovider', teamJSON);
		},
		error: function(){

		} 
	});
}

function getMemberById(id){
	$.ajax({
		type : "POST",
		url  : "/DMS/GetMemberById",
		data : {jData : JSON.stringify({"id": id})},
		dataType : 'json',
		success : function(data){
			console.log(data);
			$('#modifyName').val(data.name);
			$('#modifyName').attr("memberId",data.id);
			$('#modifyDesignation').val(data.designation);
			$('#modifyAffiliation').val(data.institute);
			$('#modifyImageUrl').val(data.image);
			$('#modifyHomepage').val(data.homepage);
			$('#modifyLinkedin').val(data.linkedin);
			$('#modifyGoogle').val(data.scholar);
			$('#modifyTwitter').val(data.twitter);
			$('#modifyImagePreview').attr("src",data.image);
			var memType = data.type;
			var memStatus = data.status;
			
			$("#modifyuserType").parent().find("div ul li").attr("class","");
			$("#modifyuserType").parent().find("div ul li input:radio").prop('checked', false)
			$("#modifyuserType").parent().find("div ul li input[value=\"" + memType +"\"]").parent().parent().parent().attr("class","active");
			$("#modifyuserType").parent().find("div ul li input[value=\"" + memType +"\"]").prop('checked', true)
			$("#modifyuserType").parent().find("div button").attr("title",memType);
			$("#modifyuserType").parent().find("div button span").html(memType);
			
			if(memStatus == 0){
				// Disabled Member
				$("#modifyStatus").parent().attr("class","toggle btn btn-default off");
			} else {
				// Enabled Member
				$("#modifyStatus").parent().attr("class","toggle btn btn-primary");
			}
		},
		error : function(){
			console.log("Something went wrong");
		}
	});
}

/*
 *  Below function is to toggle the display of modify team member button.
 */

function toggleModifySpan(nodeId){
	var nodeID  = "#" + nodeId;
	var lookFor = "span.label-default";
	var cssProperty = "display";
	if($(nodeID).find(lookFor).css(cssProperty)==="inline"){
		$(nodeID).find(lookFor).css(cssProperty,"none");
	} else {
		$(nodeID).find(lookFor).css(cssProperty,"inline");
	}
}

function updateTeamMember(event){
	// Getting data
	var type        = $(this).attr('method');
	var url         = $(this).attr('action');
	
	// Reading the Information
	var id          = $('#modifyName').attr("memberId");
	var name        = $('#modifyName').val();
	var designation = $('#modifyDesignation').val();
	var institute   = $('#modifyAffiliation').val();
	var image       = $('#modifyImageUrl').val();
	var homepage    = $('#modifyHomepage').val();
	var linkedin    = $('#modifyLinkedin').val();
	var scholar     = $('#modifyGoogle').val();
	var twitter     = $('#modifyTwitter').val();
	var memberType  = $("#modifyuserType").parent().find("button").attr("title");
	var enableCheck = $("#modifyStatus").parent().hasClass("off");

	var status = 1;
	if (enableCheck) {
		status = 0;
	}
	
	var multiVal = $("#modifyUserProjectListMultiSelect" ).parent().find("button.multiselect").attr("title");
	console.log(multiVal);
	
	
	// Resetting the form inputs
	$('#modifyName').val('');
	$('#modifyName').attr('memberId','');
	$('#modifyDesignation').val('');
	$('#modifyAffiliation').val('');
	$('#modifyImageUrl').val('');
	$('#modifyHomepage').val('');
	$('#modifyLinkedin').val('');
	$('#modifyGoogle').val('');
	$('#modifyTwitter').val('');
	
	jsonData = JSON.stringify({
		"id"		 : id,
		"name"       : name,
		"designation": designation,
		"institute"  : institute,
		"image"      : image,
		"homepage"   : homepage,
		"linkedin"   : linkedin,
		"scholar"    : scholar,
		"twitter"    : twitter,
		"status"     : status,
		"type"       : memberType,
		"projectAssociationList" : multiVal
	});

	$.ajax({
		type : "POST",
		url  : "/DMS/TeamUpdate",
		data : {jData: jsonData},
		success : function() {
			console.log("Team Member Updated");
			listProjectonTeamPage();
		},
		error : function() {
			console.log("Error in Team Member Updation");
		}
	});
}

function addTeamMember(event){

	// Getting data
	var type        = $(this).attr('method');
	var url         = $(this).attr('action');
	
	// Reading the Information
	var name        = $('#NewName').val();
	var designation = $('#NewDesignation').val();
	var institute   = $('#NewAffiliation').val();
	var image       = $('#imagePreviewUrl').val();
	var homepage    = $('#NewHomepage').val();
	var linkedin    = $('#NewLinkedin').val();
	var scholar     = $('#NewGoogle').val();
	var twitter     = $('#NewTwitter').val();
	var memberType  = $("#userTypeList").parent().find("button").attr("title");
	var enableCheck = $("#NewStatus").parent().hasClass("off");

	var status = 1;
	if (enableCheck) {
		status = 0;
	}
	
	var multiVal = $("#newUserProjectListMultiSelect").parent().find("button.multiselect").attr("title");
	console.log(multiVal);
	
	
	// Resetting the form inputs
	$('#NewName').val('');
	$('#NewDesignation').val('');
	$('#NewAffiliation').val('');
	$('#imagePreviewUrl').val('');
	$('#NewHomepage').val('');
	$('#NewLinkedin').val('');
	$('#NewGoogle').val('');
	$('#NewTwitter').val('');
	
	jsonData = JSON.stringify({
		"name"       : name,
		"designation": designation,
		"institute"  : institute,
		"image"      : image,
		"homepage"   : homepage,
		"linkedin"   : linkedin,
		"scholar"    : scholar,
		"twitter"    : twitter,
		"status"     : status,
		"type"       : memberType,
		"projectAssociationList" : multiVal
	});

	$.ajax({
		type : "POST",
		url  : "/DMS/TeamAdd",
		data : {jData: jsonData},
		success : function() {
			console.log("Team Member Added");
			listProjectonTeamPage();
		},
		error : function() {
			console.log("Error in Team Member Addition");
		}
	});
}
function createCORSRequest(method, url) {
	var xhr = new XMLHttpRequest();
	if ("withCredentials" in xhr) {
		// Check if the XMLHttpRequest object has a "withCredentials" property.
		// "withCredentials" only exists on XMLHTTPRequest2 objects.
		xhr.open(method, url, true);
	} else if (typeof XDomainRequest != "undefined") {
		// Otherwise, check if XDomainRequest.
		// XDomainRequest only exists in IE, and is IE's way of making CORS requests.
		xhr = new XDomainRequest();
		xhr.open(method, url);
	} else {
		// Otherwise, CORS is not supported by the browser.
		xhr = null;
	}
	return xhr;
}

function pullImage(urlLink, imageShow){
	var imageURL = $("#" + urlLink).val();
	var request = createCORSRequest("GET", imageURL);
	if (!request) {
		throw new Error('CORS not supported');
	} else {
		if(request.state === 404){
			$("#" + urlLink).attr("style","border:1px firebrick solid;");
		} else {
			$("#" + urlLink).attr("style","border:'';");
			$("#" + imageShow).attr("src",$("#" + urlLink).val());
		}
	}
}
