function listGroupList(data, prefix){
	if (prefix === undefined){
		prefix = "";
	}
	var aNode = document.createElement("a");
	aNode.setAttribute("class", "list-group-item");
	aNode.setAttribute("identify", data.id);
		var lblNode = document.createElement("label");
		lblNode.style.width = "100%";
		lblNode.style.fontWeight = "600";
			var inputText = document.createTextNode(" " + data.name);
		lblNode.appendChild(inputText);
		if(data["timed"]){
			var divNode = document.createElement("span");
			divNode.style.fontStyle = "oblique";
			divNode.style.fontWeight = "normal";
			divNode.setAttribute("class", "pull-right text-muted small");
				var date = agoTime(data.timed);
				var divText = document.createTextNode(prefix + date + " ago");
			divNode.appendChild(divText);
			lblNode.appendChild(divNode);
		}
	aNode.appendChild(lblNode);
	return aNode;
}
function setProjectDetails(){
	$.ajax({
		type: "POST",
		// url : "/DMS/GetProjectByID",
		url : "/DMS/InitProjectPage",
		dataType : "json",
		success:function(data){
			var project     = data.project;
			var ensemble    = data.ensemble;
			var query       = data.query;
			var team        = data.team;
			var publication = data.publication;
			console.log(publication);
			if (publication.length > 0){
				for(var p = 0; p < publication.length; p++){
					var cite = new Cite(publication[p].title,{format: 'string',type: 'string',style: 'citation',lang: 'en-US'});
					var pubStr = cite.get();
					console.log(pubStr);
					$("#publication").append(pubStr + "<hr/>");
				}
			} else {
				$("#publication").text("No publication(s) at the moment.");
			}
			if (project.length == 1) {
				$("h1.page-header").text(project[0].abbr + " : " + project[0].title);
				$("div.description").text(project[0].description);
			} else {
				window.location.href = "/DMS/jsp/LogniUser.jsp";
			}

			if (ensemble.length > 0) {
				var eCount = ensemble.length;
				for ( var i = 0; i < eCount; i++){
					var aNode = listGroupList(ensemble[i], "Last Accessed ");
					document.getElementById("ensembles").appendChild(aNode);
				}
			} else {
				$("#ensembles").text("No ensemble(s) at the moment.");
			}
			
			if (query.length > 0) {
				var qCount = query.length;
				for ( var i = 0; i < qCount; i++){
					var aNode = listGroupList(query[i]);
					document.getElementById("query").appendChild(aNode);
				}
			} else {
				$("#query").text("No query(ies) at the moment.");
			}
			
			if (team.length > 1){
				var prop = 'name';
				var asc = true;
				team = team.sort(function(a, b) {
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
				for(var i = 0; i < team.length; i++){

					appendKey = (team[i].type == "Principle Investigator") ? appendKey = "pi" : ((team[i].type == "Current Member") ? appendKey = "cm" : appendKey = "pm");

					var divID = team[i].id + "List";

					var mainDiv = document.createElement("div");
					mainDiv.setAttribute("class","media block-update-card");
					mainDiv.setAttribute("id",divID);
					mainDiv.style.borderTop = "1px lightgray solid";

					var imgDiv = document.createElement("div");
					imgDiv.setAttribute("class","pull-left");
					var imgTag   = document.createElement("img");
					imgTag.setAttribute("class","img-fuild rounded");
					imgTag.setAttribute("src",team[i].imageurl);
					imgTag.setAttribute("onerror","this.src='/DMS/images/team/error.png'");
					imgTag.setAttribute("width","50px");
					imgTag.setAttribute("height","50px");

					imgDiv.appendChild(imgTag);
					mainDiv.appendChild(imgDiv);

					var socialDiv = document.createElement("div");
					socialDiv.setAttribute("class","card-action-pellet btn-toolbar pull-right");
					socialDiv.setAttribute("role","toolbar");

					if(team[i].linkedin){
						var lDiv = document.createElement("div");
						var aNode = document.createElement("a");
						aNode.setAttribute("href",team[i].linkedin);
						var iNode = document.createElement("i");
						aNode.setAttribute("class","fa fa-linkedin");
						aNode.appendChild(iNode);
						lDiv.appendChild(aNode);
						lDiv.setAttribute("class","btn-group");
						socialDiv.appendChild(lDiv);
					}
					if(team[i].scholar){
						var sDiv = document.createElement("div");
						var aNode = document.createElement("a");
						aNode.setAttribute("href",team[i].scholar);
						var iNode = document.createElement("i");
						aNode.setAttribute("class","fa fa-google");
						aNode.appendChild(iNode);
						sDiv.appendChild(aNode);
						sDiv.setAttribute("class","btn-group");
						socialDiv.appendChild(sDiv);
					}
					if(team[i].twitter){
						var tDiv = document.createElement("div");
						var aNode = document.createElement("a");
						aNode.setAttribute("href",team[i].twitter);
						var iNode = document.createElement("i");
						aNode.setAttribute("class","fa fa-twitter");
						aNode.appendChild(iNode);
						tDiv.appendChild(aNode);
						tDiv.setAttribute("class","btn-group");
						socialDiv.appendChild(tDiv);
					}
					if(team[i].homepage){
						var hDiv = document.createElement("div");
						var aNode = document.createElement("a");
						aNode.setAttribute("href",team[i].homepage);
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
					var nameSpanText = document.createTextNode(team[i].name);
					nameSpan.appendChild(nameSpanText);

					emptyDiv.appendChild(nameSpan);
					emptyDiv.appendChild(document.createTextNode(", "));

					var infoSpan      = document.createElement("span");

					var degreeText    = document.createTextNode(team[i].designation + ", ");
					var brNode        = document.createElement("br"); 
					var instituteText = document.createTextNode(team[i].institute);

					infoSpan.appendChild(degreeText);
					infoSpan.appendChild(brNode);
					infoSpan.appendChild(instituteText);

					emptyDiv.appendChild(infoSpan);
					contentDiv.appendChild(emptyDiv);

					contentDiv.appendChild(document.createElement("br"));

					mainDiv.appendChild(contentDiv);

					document.getElementById(appendKey).appendChild(mainDiv);
				}
			} else {
				$("#team").text("No team at the moment.");
			}
		},
		error:function(){
			console.log('Error');
		}
	});
}