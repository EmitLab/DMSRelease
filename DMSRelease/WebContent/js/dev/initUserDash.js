function fromBytes(bytes) {
	var unit = '';
	var interval = Math.floor(bytes/1073741824);
	if (interval >= 1) {
    	return interval + " GB";
    }
	interval = Math.floor(bytes/1000000000);
	if (interval >= 1) {
    	return interval + " GiB";
    }
	interval = Math.floor(bytes/1048576);
	if (interval >= 1) {
    	return interval + " MB";
    }
	interval = Math.floor(bytes/1000000);
	if (interval >= 1) {
    	return interval + " MB";
    }
	interval = Math.floor(bytes/1024);
	if (interval >= 1) {
    	return interval + " KB";
    }
	interval = Math.floor(bytes/1000);
	if (interval >= 1) {
    	return interval + " KiB";
    }
	return Math.floor(bytes) + " Bytes";
}
function agoTime(date) {

    var seconds = Math.floor((Date.now() - date) / 1000);
    var unit = '';
    
    var interval = Math.floor(seconds / 31536000);
    if (interval >= 1) {
    	unit = (interval == 1) ? " year" : " years";;
    	return interval + unit;
    }
    
    interval = Math.floor(seconds / 2592000);
    if (interval >= 1) {
    	unit = (interval == 1) ? " month" : " months";;
    	return interval + unit;
    }
    
    interval = Math.floor(seconds / 86400);
    if (interval >= 1) {
    	unit = (interval == 1) ? " day" : " days";;
    	return interval + unit;
    }
    
    interval = Math.floor(seconds / 3600);
    if (interval >= 1) {
    	unit = (interval == 1) ? " hour" : " hours";;
    	return interval + unit;
    }
    
    interval = Math.floor(seconds / 60);
    if (interval >= 1) {
    	unit = (interval == 1) ? " minute" : " minutes";;
    	return interval + unit;
    }
    return Math.floor(seconds) + " seconds";
}

/**
 * Initializes the footer menu on user dash-board
 * @author Yash garg <ygarg@asu.edu>
 */

function initUserDashFooter(pid){
	var $footer = $("body div.footer");
	for (var i = 0; i< 2; i++){
		$("body").append("<br/>");
	}
	$footer.text("Developed by EMITLab");
	$.ajax({
		type: "POST",
		url : "/DMS/GetGrantsForProject",
		dataType: "json",
		success : function(data){
			
			var gCount = data.length;
			if (gCount == 0){
				$footer.append("<div class=\"pull-right\">No Grants at the moments</div>");
			} else {
				var fNode = document.getElementsByClassName("footer")[0];
				console.log(fNode);
				var divNode = document.createElement("div");
				divNode.setAttribute("class","pull-right");
				var divNodeText = document.createTextNode("Associated Grant" + ((gCount > 1) ? "s" : "") + " : ");
				divNode.appendChild(divNodeText);
				for (var g = 0; g < gCount; g++) {
					var aNode = document.createElement("a");
					var aNodeText = document.createTextNode(data[g].name);
					aNode.setAttribute("href", data[g].link);
					aNode.setAttribute("target", "_blank");
					aNode.appendChild(aNodeText);
					divNode.appendChild(aNode);
					
					var span = document.createElement("span");
					var spanStr = (g == (gCount - 1)) ? "." : ((g == (gCount - 2)) ? ", and " : ", ");	
					var spanText = document.createTextNode(spanStr);
					span.appendChild(spanText);
					divNode.appendChild(span);
				}
				fNode.appendChild(divNode);
			}
		},
		error : function(){
			$footer.append("<div class=\"pull-right\">No Grants at the moments</div>");
		}
	});
}

function createNode (url, faicon, name){
	var liNode = document.createElement("li");
    	var aNode = document.createElement("a");
    	aNode.setAttribute("href", url);
        	var iNode = document.createElement("i");
        	iNode.setAttribute("class", "fa fa-" + faicon + " fa-fw");
        aNode.appendChild(iNode);
        	aNodeText = document.createTextNode(" " + name);
        aNode.appendChild(aNodeText);
    liNode.appendChild(aNode);
    return liNode;
}

/**
 * Initializes the left0sidebar of the pages in user dash-board.
 * @author Yash Garg <ygarg@asu.edu>
 */

function initUserDashLeftSideBarMenu(){
	var sideMenu = document.getElementById("side-menu");
		liNode = createNode("/DMS/jsp/project.jsp", "home", "Home");
	sideMenu.appendChild(liNode);
	//	liNode = createNode("/DMS/jsp/DashUser.jsp", "dashboard", "Dashboard");
	//sideMenu.appendChild(liNode);
		liNode = createNode("/DMS/jsp/Data.jsp", "database", "Ensemble");
	sideMenu.appendChild(liNode);
		//liNode = createNode("/DMS/jsp/Query.jsp", "circle-o-notch", "Query");
	//sideMenu.appendChild(liNode);
		liNode = createNode("/DMS/jsp/System.jsp", "sitemap", "System");
	sideMenu.appendChild(liNode);
		liNode = createNode("/DMS/jsp/docs.jsp", "book", "Documentation");
	sideMenu.appendChild(liNode);
		liNode = createNode("/DMS/jsp/Contact.jsp", "phone", "Contact");
	sideMenu.appendChild(liNode);
}

/**
 * Initializes the top menu on pages in user dash-board.
 * @param {int} userId - UserID of currently logged in user.
 * @param {int} project - ProjectId of currently active project.
 * @author Yash Garg <ygarg@asu.edu>
 */

function initUserDashTopMenu(userId, projectId){
	var topMenu = document.getElementById("dashTopMenu");
		var projectHomeNode = document.createElement("li");
		projectHomeNode.setAttribute("class","dropdown");
		projectHomeNode.setAttribute("title","Home");
		projectHomeNode.setAttribute("id","projectHomeBtn");
			var aNode = document.createElement("a");
			aNode.setAttribute("href", "project.jsp");
				var iNode = document.createElement("i");
				iNode.setAttribute("class", "fa fa-home fa-fw fa-lg");
			aNode.appendChild(iNode);
		projectHomeNode.appendChild(aNode);
	topMenu.appendChild(projectHomeNode);	
		var projectLiNode = document.createElement("li");
		projectLiNode.setAttribute("class","dropdown");
		projectLiNode.setAttribute("title","Project");
		projectLiNode.setAttribute("id","projectSettingMenu");
	topMenu.appendChild(projectLiNode);
		var userLiNode = document.createElement("li");
		userLiNode.setAttribute("class","dropdown");
		userLiNode.setAttribute("title","User Settings");
		userLiNode.setAttribute("id","userSettingMenu");
	topMenu.appendChild(userLiNode);
	initProjectMenu(userId, projectId);
	initUserSettingMenu();
	$("#userEmailUpdate").submit(function(event){
		event.preventDefault();
		updateUserEmail(userId);
	});
	$("#userPassUpdate").submit(function(event){
		event.preventDefault();
		updateUserPass(userId);
	});
}

function updateUserEmail(uid){
	var email = $("#newEmail").val();
	var reg = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/igm;
	if(email && uid && reg.test(email)){
		$("#newEmail").css("border", "");
		$.ajax({
			type: "POST",
			url: "/DMS/SQLSelectQuery",
			data: {jData : JSON.stringify({query: "SELECT userid FROM dmsuser WHERE userid != " + uid + ";"})},
			dataType : "json",
			success: function(data){
				if (data.length == 0){
					$.ajax({
						type:"POST",
						url: "/DMS/SQLUpdateQuery",
						data: {jData : JSON.stringify({query:"UPDATE dmsuser SET email = '" + email + "' WHERE userid = " + uid})},
						success : function(){
							console.log("Email successfully updated.");
							$("#newEmail").css("border", "1px solid green");
						},
						error : function(){
							console.error("Oops! Something went wrong.");
							$("#newEmail").css("border", "1px solid firebrick");
						}
					});		
				} else {
					console.log("Email is already in user for other account.");
					$("#newEmail").css("border", "1px solid firebrick");
				}
			},
			error: function(){
				console.error("Oops! Something went wrong.");
				$("#newEmail").css("border", "1px solid firebrick");
			}
		});
	} else {
		if(!uid){
			window.location = "/DMS/jsp/LoginUser.jsp";
		}else if(!email){
			$("#newEmail").css("border", "1px solid firebrick");
		}else if(!reg.test(email)){
			$("#newEmail").css("border", "1px solid firebrick");
		}
	}
}

function updateUserPass(uid){
	
	$("#oldPass").css("border", "");
	$("#newPass").css("border", "");
	$("#reNewPass").css("border", "");
	
	//var uid       = $("#body").attr("userid");
	var oldPass   = $("#oldPass").val();
	var newPass   = $("#newPass").val();
	var reNewPass = $("#reNewPass").val();
	if (uid && oldPass && newPass && reNewPass && (newPass == reNewPass)){
			$.ajax({
				type:"POST",
				url:"/DMS/SQLSelectQuery",
				data:{jData : JSON.stringify({query:"SELECT userid FROM dmsuser WHERE userid = " + uid + " AND password = '" + oldPass + "';"})},
				dataType: "JSON",
				success: function(data){
					if(data.length != 1){
						$("#oldPass").css("border", "1px firebrick solid");					
						console.error("Current password Didn't Match");
					} else {
						$.ajax({
							type: "POST",
							url: "/DMS/SQLUpdateQuery",
							data: {jData : JSON.stringify({query : "UPDATE dmsuser SET password = '" + newPass + "' WHERE userid = " + uid + ";"})},
							success : function() {
								$("#oldPass").css("border", "1px solid green");
								$("#newPass").css("border", "1px solid green");
								$("#reNewPass").css("border", "1px solid green");
								console.log("Successfully Updated password");
							},
							error : function() {
								$("#oldPass").css("border", "1px solid firebrick");
								$("#newPass").css("border", "1px solid firebrick");
								$("#reNewPass").css("border", "1px solid firebrick");
								console.error("something Went wrongs");
							}
						});
					}
				}
			});
	} else {
		if(!uid) {
			window.location = "/DMS/jsp/LoginUser.jsp";
		}
		if(!oldPass) {
			$("#oldPass").css("border", "1px solid firebrick");
		} 
		if(!newPass) {
			$("#newPass").css("border", "1px solid firebrick");
		}
		if(!reNewPass) {
			$("#reNewPass").css("border", "1px solid firebrick");
		}
		if(newPass !=  reNewPass) {
			$("#newPass").css("border", "1px solid firebrick");
			$("#reNewPass").css("border", "1px solid firebrick");
		}
	}
	
}

function initUserSettingMenu(){
	var userLiNode = document.getElementById("userSettingMenu");
	
	var aNode = document.createElement("a");
	aNode.setAttribute("class", "dropdown-toggle");
	aNode.setAttribute("data-toggle", "dropdown");
	aNode.setAttribute("href", "#");

	var iNode = document.createElement("i");
	iNode.setAttribute("class", "fa fa-user fa-fw");
	aNode.appendChild(iNode);

	iNode = document.createElement("i");
	iNode.setAttribute("class", "fa fa-caret-down");
	aNode.appendChild(iNode);

	userLiNode.appendChild(aNode);

	var ulNode = document.createElement("ul");
	ulNode.setAttribute("class", "dropdown-menu dropdown-user");
	ulNode.setAttribute("style", "width:250px !important");
	
		var liNode = document.createElement("li");
	
			aNode = document.createElement("a");
			aNode.setAttribute("href", "#");
				
				var fNode = document.createElement("form");
				fNode.setAttribute("method","POST");
				fNode.setAttribute("id","userPassUpdate");
					
					var lblNode = document.createElement("label");
					    var faNode = document.createElement("i");
					    faNode.setAttribute("class","fa fa-unlock-alt");
						var lblText = document.createTextNode(" Update Password");
					lblNode.appendChild(faNode);
					lblNode.appendChild(lblText);
				fNode.appendChild(lblNode);
					var divNode = document.createElement("div");
					divNode.setAttribute("class","form-group");
						var inputNode = document.createElement("input");
						inputNode.setAttribute("class","form-control");
						inputNode.setAttribute("type","password");
						inputNode.setAttribute("placeholder", "Current Password");
						inputNode.setAttribute("id","oldPass");
					divNode.appendChild(inputNode);
				fNode.appendChild(divNode);
					divNode = document.createElement("div");
					divNode.setAttribute("class","form-group");
						inputNode = document.createElement("input");
						inputNode.setAttribute("class","form-control");
						inputNode.setAttribute("type","password");
						inputNode.setAttribute("placeholder", "Enter New Password");
						inputNode.setAttribute("id","newPass");
					divNode.appendChild(inputNode);
				fNode.appendChild(divNode);
					divNode = document.createElement("div");
					divNode.setAttribute("class","form-group");
						inputNode = document.createElement("input");
						inputNode.setAttribute("class","form-control");
						inputNode.setAttribute("type","password");
						inputNode.setAttribute("placeholder", "Re-Enter New Password");
						inputNode.setAttribute("id","reNewPass");
					divNode.appendChild(inputNode);
				fNode.appendChild(divNode);
					var btnNode = document.createElement("button");
					btnNode.setAttribute("class","btn btn-success btn-block");
					btnNode.setAttribute("type","submit");
						var btnText = document.createTextNode("Update Password ");// Keep the trailing space
						iNode = document.createElement("i");
						iNode.setAttribute("class","fa fa-arrow-right");
					btnNode.appendChild(btnText);
					btnNode.appendChild(iNode);
				fNode.appendChild(btnNode);
			aNode.appendChild(fNode);
		liNode.appendChild(aNode);
	ulNode.appendChild(liNode);
		liNode = document.createElement("li");
		liNode.setAttribute("class","divider");
	ulNode.appendChild(liNode);
		liNode = document.createElement("li");
			aNode = document.createElement("a");
			aNode.setAttribute("href", "#");
				var fNode = document.createElement("form");
				fNode.setAttribute("method","POST");
				fNode.setAttribute("id","userEmailUpdate");
					var lblNode = document.createElement("label");
					    var faNode = document.createElement("i");
				        faNode.setAttribute("class","fa fa-envelope-o");
						var lblText = document.createTextNode(" Update Email");
					lblNode.appendChild(faNode);
					lblNode.appendChild(lblText);
				fNode.appendChild(lblNode);				
					var divNode = document.createElement("div");
					divNode.setAttribute("class","form-group");
						var inputNode = document.createElement("input");
						inputNode.setAttribute("class","form-control");
						inputNode.setAttribute("type","text");
						inputNode.setAttribute("placeholder", "New Email");
						inputNode.setAttribute("id","newEmail");
						divNode.appendChild(inputNode);
				fNode.appendChild(divNode);
					var btnNode = document.createElement("button");
					btnNode.setAttribute("class","btn btn-success btn-block");
					btnNode.setAttribute("type","submit");
						var btnText = document.createTextNode("Update Email ");// Keep the trailing space
							iNode = document.createElement("i");
							iNode.setAttribute("class","fa fa-arrow-right");
					btnNode.appendChild(btnText);
					btnNode.appendChild(iNode);
				fNode.appendChild(btnNode);
			aNode.appendChild(fNode);
		liNode.appendChild(aNode);
	ulNode.appendChild(liNode);
		liNode = document.createElement("li");
		liNode.setAttribute("class","divider");
	ulNode.appendChild(liNode);
		liNode = document.createElement("li");
			aNode = document.createElement("a");
			aNode.setAttribute("href","/DMS/UserLogout");
				iNode = document.createElement("i");
				iNode.setAttribute("class","fa fa-sign-out");
			aNode.appendChild(iNode);
				var aText = document.createTextNode(" Logout");
			aNode.appendChild(aText);
		liNode.appendChild(aNode);
	ulNode.appendChild(liNode);
	userLiNode.appendChild(ulNode);
}

function initProjectMenu(uid, pid){
	$.ajax({
		type:"POST",
		url:"/DMS/ProjectListFilter",
		dataType:"json",
		success:function(data){
			$.ajax({
				type: "POST",
				url: "/DMS/SQLSelectQuery",
				dataType: "json",
				data: {jData:JSON.stringify({query:"SELECT pid FROM userproj WHERE uid = " + uid})},
				success: function(pidData){
					var projectLiNode = document.getElementById("projectSettingMenu");
					var aNode = document.createElement("a");
					aNode.setAttribute("class", "dropdown-toggle");
					aNode.setAttribute("data-toggle", "dropdown");
					aNode.setAttribute("href", "#");

					var iNode = document.createElement("i");
					iNode.setAttribute("class", "fa fa-tv fa-fw");
					aNode.appendChild(iNode);

					iNode = document.createElement("i");
					iNode.setAttribute("class", "fa fa-caret-down");
					aNode.appendChild(iNode);

					projectLiNode.appendChild(aNode);

					var ulNode = document.createElement("ul");
					ulNode.setAttribute("class", "dropdown-menu");

					var pCount = data.length;
					for(var i = 0; i < pCount; i++){
						var liNode = document.createElement("li");
						if (pid == data[i].id){
							liNode.setAttribute("class", "btn-primary");
							$("#wrapper a.navbar-brand").html(data[i].name);
							$("#wrapper a.navbar-brand").attr("href","project.jsp");
						}
						aNode = document.createElement("a");
						aNode.setAttribute("href","#");

						var divNode = document.createElement("div");
						var divNodeText = document.createTextNode(data[i].name);
						divNode.appendChild(divNodeText);
						
						var innerDivNode = document.createElement("div");
						innerDivNode.setAttribute("class","pull-right");
						
						iNode = document.createElement("i");
						
						var upCount = pidData.length;
						for (var j = 0; j < upCount; j++){
							if (data[i].id == pidData[j].pid){
								iNode.setAttribute("class", "fa fa-check-square-o");
								liNode.setAttribute("onclick","changeUserProject(" + data[i].id +");");
								break;
							} else {
								iNode.setAttribute("class", "fa fa-square-o");
								liNode.setAttribute("onclick","addUserToProj('" + uid + "','" + data[i].id + "');");
							}
						}
						innerDivNode.appendChild(iNode);
						divNode.appendChild(innerDivNode);
						divNode.setAttribute("projectId", data[i].id);

						aNode.appendChild(divNode);
						liNode.appendChild(aNode);
						ulNode.appendChild(liNode);
					}
					projectLiNode.appendChild(ulNode);	
				}
			});
		}
	});
}

function changeUserProject(pid){
	var url = (window.location.href).split("/DMS")[1];
	window.location = "/DMS/ChangeUserProject?pid=" + pid + "&currentURL=" + url;
}

function addUserToProj(uid, pid){
	var insertQuery = "INSERT INTO userproj (uid,pid) VALUES (" + uid + "," + pid + ");";
	
	$.ajax({
		type: "POST",
		url: "/DMS/SQLInsertQuery",
		data: {jData : JSON.stringify({query:insertQuery})},
		success: function(){
			console.log("you are now connected.");
		},
		error:function(){
			console.error('Oops! Something went wrong.');
		}
	});
	changeUserProject(pid);
}
