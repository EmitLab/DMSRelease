function typeOf(obj){
	return {}.toString.call(obj).split(' ')[1].slice(0, -1).toLowerCase();
}

function listProjectonLoginWindow(){
	$.ajax({
		type:"POST",
		url:"/DMS/ProjectListFilter",
		dataType:"json",
		success:function(data){
			for(var i = 0; i < data.length; i++){
				$("#projectListLogin")
					.append("<li id=" + data[i].id + "><a href=\"#\">" + data[i].name + "</a></li>");
				$("#projectListRegister")
					.append("<li id=" + data[i].id + "><a href=\"#\">" + data[i].name + "</a></li>");
			}
		}
	});
}
function updateProject(){
	var id          = $("#modifyProject div div input[name='projectName']").attr("projectid");
	var name        = $("#modifyProject div div input[name='projectName']").val();
	var abbr        = $("#modifyProject div div input[name='projectAbbr']").val();
	var title       = $("#modifyProject div div input[name='projectTitle']").val();
	var description = $("#modifyProject div div textarea[name='projectDescription']").val();
	var statusName  = $("#modifyProject div div select option").filter(":selected").text();
	var status = null;
	
	if (statusName === 'Enabled') {
		status = 1;
	} else {
		status = 0;
	}
	$.ajax({
		type:"POSt",
		url:"/DMS/ProjectUpdate",
		data:{jData:JSON.stringify({"id":id,"name":name,"abbr":abbr,"title":title,"description":description,"status":status})},
		dataType:"json",
		success:function(data){
			$("#modifyProject").removeClass("active");
			$("#modifyProject").removeClass("in");
			$("#modifyProjectli").removeClass("active");
			$("#viewProject").addClass("active");
			$("#viewProject").addClass("in");
			$("#viewProjectli").addClass("active");
			$("#projectMessage").text("Project " + name + " updated.");
			displayProject(data);
		}
	});
}

function deleteProject(){
	
}

function addProject(){
	var name        = $("#addProject div div input[name='projectName']").val();
	var abbr        = $("#addProject div div input[name='projectAbbr']").val();
	var title       = $("#addProject div div input[name='projectTitle']").val();
	var description = $("#addProject div div textarea[name='projectDescription']").val();

	if(name && abbr && title && description){

		$("#addProject div div input[name='projectName']").css("border","");
		$("#addProject div div input[name='projectTitle']").css("border","");
		$("#addProject div div input[name='projectAbbr']").css("border","");
		$("#addProject div div textarea[name='projectDescription']").css("border","");
		$("#addProject div div span").css("color","");
		$.ajax({
			type:"POST",
			url:"/DMS/ProjectAdd",
			data:{jData :JSON.stringify({"projectName":name, "projectAbbr":abbr, "projectTitle":title, "projectDescription":description})},
			dataType:"json",
			success:function(data){
				$("#addProject").removeClass("active");
				$("#addProject").removeClass("in");
				$("#addProjectli").removeClass("active");
				$("#viewProject").addClass("active");
				$("#viewProject").addClass("in");
				$("#viewProjectli").addClass("active");
				$("#projectMessage").text("Project " + name + " added.");
				displayProject(data);
			}
		});

	} else {
		if(name){
			$("#addProject div div input[name='projectName']").css("border","");
		} else {
			$("#addProject div div input[name='projectName']").attr("style","border:1px solid red");
		}
		if(abbr){
			$("#addProject div div input[name='projectAbbr']").css("border","");
		} else {
			$("#addProject div div input[name='projectAbbr']").attr("style","border:1px solid red");
		}
		if(title){
			$("#addProject div div input[name='projectTitle']").css("border","");
		} else {
			$("#addProject div div input[name='projectTitle']").attr("style","border:1px solid red");
		}
		if(description){
			$("#addProject div div textarea[name='projectDescription']").css("border","");
		} else {
			$("#addProject div div textarea[name='projectDescription']").attr("style","border:1px solid red");
		}
		$("#addProject div div span").attr("style","color:red");
	}
}

function viewProject(){
	$.ajax({
		type:"POST",
		url:"/DMS/ProjectList",
		dataType:"json",
		success:function(data){
			displayProject(data);
		}
	});
}
function updateProjectMenu(){

	var id          = $("#modifyProjectList").find(":selected").attr("id");
	var name        = $("#modifyProjectList").find(":selected").attr("name");
	var abbr        = $("#modifyProjectList").find(":selected").attr("abbr");
	var title       = $("#modifyProjectList").find(":selected").attr("title");
	var status      = $("#modifyProjectList").find(":selected").attr("status");
	var description = $("#modifyProjectList").find(":selected").attr("description");

	$("#modifyProject div div input[name='projectName']").val(name);
	$("#modifyProject div div input[name='projectName']").attr("projectid",id);
	$("#modifyProject div div input[name='projectAbbr']").val(abbr);
	$("#modifyProject div div input[name='projectTitle']").val(title);
	$("#modifyProject div div textarea[name='projectDescription']").val(description);
	if (status == "1"){
		$("#modifyProject div div select option[name='enabled']").prop('selected',true);
		$("#modifyProject div div select option[name='disabled']").prop('selected',false);
	} else {
		$("#modifyProject div div select option[name='enabled']").prop('selected',false);
		$("#modifyProject div div select option[name='disabled']").prop('selected',true);
	}
}

function displayProject(data){
	if (data.length != 0 ){
		var table = document.createElement("table");
		table.setAttribute("class","table table-striped");
		table.setAttribute("id","viewProjectTbl");
		var thead = document.createElement("thead");
		var tr = document.createElement("tr");
		var th = document.createElement("th");
		var thText = document.createTextNode("Name");
		th.appendChild(thText);
		tr.appendChild(th);

		th = document.createElement("th");
		thText = document.createTextNode("Abbr");
		th.appendChild(thText);
		tr.appendChild(th);

		th = document.createElement("th");
		thText = document.createTextNode("Status");
		th.appendChild(thText);
		tr.appendChild(th);

		thead.appendChild(tr);
		var tbody = document.createElement("tbody");
		$("#addPublicationProject").empty();
		$("#modifyPublicationProject").empty();
		
		$("#modifyProjectList").empty();
		$("#modifyProjectList").append("<option>Select a project</option>");
		
		$("#addGrantProject").empty();
		$("#addGrantProject").append("<option>Select a project</option>");
		$("#modifyGrantProject").empty();
		$("#modifyGrantProject").append("<option>Select a project</option>");
		for(var i = 0; i < data.length; i++){
			$("#addGrantProject").append("<option pid=" + data[i].id + ">" + data[i].name + "</option>");
			$("#modifyGrantProject").append("<option pid=\"" + data[i].id + "\">" + data[i].name + "</option>");
			
			$("#addPublicationProject").append("<label class=\"checkbox-inline\"><input type=\"checkbox\" pid=\"" + data[i].id + "\">" + data[i].name + "</label>");
			$("#modifyPublicationProject").append("<label class=\"checkbox-inline\"><input type=\"checkbox\" pid=\"" + data[i].id + "\">" + data[i].name + "</label>");
			
			$("#modifyProjectList")
			.append("<option id=\""     + data[i].id +
					"\" name=\""        + data[i].name + 
					"\" abbr=\""        + data[i].abbr +
					"\" title=\""       + data[i].title + 
					"\" description=\"" + data[i].description +
					"\" status=\""      + data[i].status + "\">" +
					data[i].abbr        + " : " + 
					data[i].name        + "</option>");

			var tr = document.createElement("tr");
			var td = document.createElement("td");
			tdText = document.createTextNode(data[i].name);
			td.appendChild(tdText);
			tr.appendChild(td);

			td = document.createElement("td");
			tdText = document.createTextNode(data[i].abbr);
			td.appendChild(tdText);
			tr.appendChild(td);

			td = document.createElement("td");
			span = document.createElement("span");
			if(data[i].status == 1){
				span.setAttribute("class","label label-success");
				text = document.createTextNode("Enabled");
			} else {
				span.setAttribute("class","label label-warning");
				text = document.createTextNode("Disabled");
			}
			span.appendChild(text);
			td.appendChild(span);
			tr.appendChild(td);

			tbody.appendChild(tr);
		}
		table.appendChild(thead);
		table.appendChild(tbody);
		document.getElementById("viewProject").innerHTML = "";
		document.getElementById("viewProject").appendChild(table);
	} else {
		document.getElementById("viewProject").innerHTML = "Sorry! No project at the moment.";
	}
}