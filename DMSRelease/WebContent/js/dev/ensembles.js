function getDBStats(){
	// var scale = {factor:1048576, unit:"MB"};
	$.ajax({
		type: "POST",
		url: "/DMS/GetDBStates",
		dataType: "json",
		success:function(data){
			var eCount = data.length;
			var statNode = document.getElementById("dbStat");
			statNode.innerHTML = "";
			if (eCount > 0){
				var keys = ["Size of uncompressed data", "dataSize", "Size of file in databases", "fileSize", "Size of index", "indexSize", "Average Simulation Size", "avgObjSize"];
				for(var i = 0; i < keys.length; i += 2){
					var flag = 1;
					var divNode = document.createElement("a");
					divNode.setAttribute("class","list-group-item");
						var divText = document.createTextNode(keys[i]);
					divNode.appendChild(divText);
					var divRight = document.createElement("div");
					divRight.setAttribute("class","pull-right");
					var byteValue = 0;
					for (j = 0; j < eCount && flag == 1; j++){
						if (!data[j][keys[i+1]]){
							flag = 0;
						}
						byteValue += data[j][keys[i+1]];
					}
					if (flag){
						var divRightText = document.createTextNode(fromBytes(byteValue));
						divRight.appendChild(divRightText);
						divNode.appendChild(divRight);
						statNode.appendChild(divNode);
					}
				}
			} else {
				var statText = document.createTextNode("No Statistics to display at the moment.");
				statNode.appendChild(statText);
			}
		},
		error:function(){
			console.log("An error occured.");
		}
	});
}

function uploadEnsembles(){
	$("#ensembleUploadList").css("border", "");
	$("#newEnsembleName").css("border", "");
	$("#simulationList").css("border", "");
	
	var ensembleName = $("#newEnsembleName").val();
	var selectEnsembleName = $("#ensembleUploadList option:selected").text();
	var files = document.getElementById("simulationList").files; 
	
	var new_Flag    = (ensembleName) ? 1 : 0;
	var select_Flag = (selectEnsembleName !== "Select an Ensemble") ? 1 : 0;
	var file_Flag   = (files.length != 0 && files.length > 0) ? 1 : 0;

	var $errMsg = $("#uploadErrorMessage");
	if($errMsg.hasClass("error")){
		$errMsg.removeClass("error");
	}
	$errMsg.html("Processing ....");
	
	if (new_Flag && !select_Flag && file_Flag){
		// Before insertion check if Ensemble name already exists
		// Status : 0 - Live, 1 - Archive, 2 - Delete
		$.ajax({
			type: "POST",
			url: "/DMS/CreateEnsembles",
			data: {jData:JSON.stringify({name : ensembleName, status : 0})},
			dataType: "json",
			success:function(data){
				if (data.servletState === 0) {
					$errMsg.html("Ensemble already exists");
					$errMsg.addClass("error");
				} else {
					getEnsembleList();
					console.log("New Ensemble Id - " + data.ensembleId);
					fileReader(data.ensembleId, files, 0);
				}
			},
			error:function(){
				$errMsg.html("Oops! something went wrong");
				$errMsg.addClass("error");
			}
		});
	} else if(!new_Flag && select_Flag && file_Flag) {
		console.log("Existing Ensemble Id - " + $("#ensembleUploadList option:selected").attr("eid"));
		fileReader($("#ensembleUploadList option:selected").attr("eid"), files, 0);
	} else {
		$errMsg.html("");
		if(!$errMsg.hasClass("error")){
			$errMsg.addClass("error");
		}
		if(new_Flag && select_Flag){
			$errMsg.html("Either create a new ensemble or select an existing ensemble; not both");
			$("#newEnsembleName").css("border","1px solid firebrick");
			$("#ensembleUploadList").css("border","1px solid firebrick");
		}
		if(!new_Flag && !select_Flag){
			$errMsg.html("Select either of the two ensemble option");
			$("#newEnsembleName").css("border","1px solid firebrick");
			$("#ensembleUploadList").css("border","1px solid firebrick");
		}
		//alert(!file_Flag);
		if (!file_Flag){
			if($errMsg.html()){
				$errMsg.html($errMsg.html() + ", and no File selected to upload.");
			} else {
				$errMsg.html("No File selected to upload.");
			}
			$("#simulationList").css("border","1px solid firebrick");
		}
	}
}

/**
 * @param eid {int} - Ensemble Id
 * @param files {object} - File list
 * @param count {int} - current file index
 * @param e {object} - Object return when file is read as binary string.
 * @author Yash Garg
 */

function fileLoaded(eid, files, count, e){
	var nFiles = files.length;
	$("#uploadErrorMessage").html("Uploaded " + (count+ 1) + " file" + ((nFiles < 2) ? "" : "s") + " of " + nFiles + " files.");
	$("#myBar").css("width",((count+1)*100)/nFiles + '%');
	var relativePath = files[count].webkitRelativePath.split("/");
	var fileName = files[count].name;
	
	var dbName  = eid;
	var model   = relativePath[1];
	var simName = relativePath[2]; 
	var content = e.target.result;
	var metric  = fileName.split(".")[0];
	
	console.log("Model - " + model + " & simName - " + simName + " File name - " + fileName);
	
	metric = metric.replace("_", "");	// Removing any underscores
	metric = metric.replace(/\s/g, "");  // Removing spaces and tabs in names
	
	simName = simName.replace("_", "");	 // Removing any underscores
	simName = simName.replace(/\s/g, ""); // Removing spaces and tabs in names
	
	var fileTypeFlag = fileName.endsWith(".xml") || fileName.endsWith(".html") || fileName.endsWith(".htm");
	//var logFlag      = fileName === "ScenarioLog.html";
	var runFlag      = fileName === "runparameters.csv";
	
	var obj = new Object();
	obj.dbName = (typeof dbName === "string") ? dbName : dbName.toString();
	obj.simName = simName;
	obj.model = model;
	obj.metric = metric;
	obj.content = content;
	var uploadContent = JSON.stringify(obj);
	
	if(fileTypeFlag){
		console.log("BaseX " + fileName);
		$.ajax({
			type: "POST",
			url: "/DMS/UploadXml",
			data: {jData : uploadContent},
			success:function(){
				console.log("Hurray XML");
			},error:function(){
				console.log("Oops XML");
			}
		});		
	} if(runFlag){
		// Do Nothing
	} else {
		console.log("Mongo and " + fileName);
		$.ajax({
			type: "POST",
			url: "/DMS/UploadEnsembles",
			data: {jData : uploadContent},
			success:function(){
				console.log("Hurray Mongo");
			},error:function(){
				console.log("Oops Mongo");
			}
		});
		
	}
	if (count < nFiles - 1) { // Load the next file
		fileReader(eid, files, count+1);
	} else {
		$("#uploadErrorMessage").html("Successfully uploaded " + nFiles + " file" + ((nFiles < 2) ? "." : "s."));
	}
}

function fileReader(eid, files, count){
	var reader = new FileReader();
	reader.onload = function(e){
		fileLoaded(eid, files, count, e);
	}
	reader.readAsBinaryString(files[count]);
}

function initProgressBar(){
	$("#myBar").css("width","0%");
}

function archiveEnsembles(){
	var $eWarning = $("#ensembleListWarning");
	$eWarning.css("border", "");
	if ($eWarning.hasClass("error")){
		$eWarning.removeClass("error");
	}
	$eWarning.html("Processing . . .");
	if($("#ensembleList").find("input:checkbox").length < 1){
		$eWarning.html("There is no ensemble to archive");
		$eWarning.addClass("error");
	} else {
		var ensembles = $("#ensembleList").find(":checked");
		if (ensembles.length < 1){
			$eWarning.html("No Ensemble selected. Select at-least one.");
			$eWarning.addClass("error");
		} else {
			var eidStr = "";
			ensembles.each(function(){
				eidStr += $(this).parent().parent().attr("eid") + ",";
			});
			eidStr = eidStr.replace(/\,$/, ''); // To remove a comma ',' at the end of string if there.
			var updateQuery = "UPDATE ensembles SET status = case when status = 0 then 1 else status = 0 end WHERE id in (" + eidStr + ")";
			console.log(eidStr);
			$.ajax({
				type: "POST",
				url: "/DMS/SQLUpdateQuery",
				data: {jData:JSON.stringify({query:updateQuery})},
				success:function(){
					ensembles.each(function(){
						var $node = $(this).parent().find("span"); 
						if($node.hasClass("label-success")){
							$node.removeClass("label-success");
							$node.addClass("label-warning");
							$node.text("Archived");
						} else {
							$node.removeClass("label-warning");
							$node.addClass("label-success");
							$node.text("Active");
						}
					});
					$eWarning.html("Updated " + ensembles.length + " ensemble" + ((ensembles.length < 2) ? "." : "s."));
				},
				error:function(){
					console.log("Need to work more");
				}
			});
		}
	}
}

function getEnsembleList(){
	$.ajax({
		type: "POST",
		url: "/DMS/GetEnsembleList",
		dataType: "json",
		success:function(data){
			var uploadListNode = document.getElementById("ensembleUploadList");
			uploadListNode.innerHTML = "";
				var optionNode = document.createElement("option");
					var optionText = document.createTextNode("Select an Ensemble");
				optionNode.appendChild(optionText);
			uploadListNode.appendChild(optionNode);
			
			var listNode = document.getElementById("ensembleList");
			listNode.innerHTML = "";
			var eCount = data.length;
			if (eCount == 0){
				var spanNode = document.createElement("span");
				var spanText = document.createTextNode("No Ensemble at the moment");
				spanNode.appendChild(spanText);
				listNode.appendChild(spanNode);
			} else {
				for (var i = 0; i < eCount; i++){
					var aNode = document.createElement("a");
					aNode.setAttribute("class", "list-group-item");
					aNode.setAttribute("eid", data[i].id);
						var lblNode = document.createElement("label");
						lblNode.setAttribute("style", "width:100%;cursor:pointer;");
							var inputNode = document.createElement("input");
							inputNode.setAttribute("type", "checkbox");
							var inputText = document.createTextNode(" " + data[i].name);
						lblNode.appendChild(inputNode);
						lblNode.appendChild(inputText);
							var divNode = document.createElement("span");
							if(data[i].status == 0){
								divNode.setAttribute("class", "pull-right label label-success text-muted small");
								var divText = document.createTextNode("Active");
							} else {
								divNode.setAttribute("class", "pull-right label label-warning text-muted small");
								var divText = document.createTextNode("Archived");
							}
							divNode.appendChild(divText);
						lblNode.appendChild(divNode);
					aNode.appendChild(lblNode);
				
					listNode.appendChild(aNode);
				
					optionNode = document.createElement("option");
					optionNode.setAttribute("eid", data[i].id);
						optionText = document.createTextNode(data[i].name);
					optionNode.appendChild(optionText);
					uploadListNode.appendChild(optionNode);
				}
			}
		},
		error:function(){
			alert("An Error Occured");
		}
	});
}