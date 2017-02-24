/**
 * 
 */
function viewPublication(){
	$.ajax({
		type : "POST",
		url : "/DMS/GetPublicationList",
		dataType : "json",
		success : function (data){
			displayPublication(data);
		},
		error : function(){
			$("#viewPublication").append("No publications to display at the moment");
		}
		 
	});
}
function displayPublication(data){
	console.log(data);
	var gCount = data.length;
	$vPublication = $("#viewPublication").empty();
	$mPublication = $("#modifyPublicationList").empty();
	$mPublication = $("#modifyPublicationList").append("<option>Select a Publication</option>");
	if(gCount > 0){
		for (var g = 0; g < gCount; g++){
			if($("li[pubid=" + data[g].pubid +"]").length){
				$("li[pubid=" + data[g].pubid +"] div[category=projects]").append("<div class=\" label label-primary\">" + data[g].projname + "</div>&nbsp;");
			} else {
				var cite = new Cite(data[g].pubname,{format: 'string',type: 'string',style: 'citation',lang: 'en-US'});
				var pubStr = cite.get();
				$vPublication.append(
					"<li class=\"list-group-item\" pubid=\"" + data[g].pubid + "\">" + pubStr + 
					"<br/><div category='projects'><em>Projects : </em></div><div category='tags'><em>Tags : </em></div></li>"
				);
				$("li[pubid=" + data[g].pubid +"] div[category=projects]").append("<div class=\" label label-primary\">" + data[g].projname + "</div>&nbsp;");
				
				
				var tagStr = data[g].pubtags;
				console.log(tagStr);
				if (tagStr.includes(',')){
					var tagArr = (data[g].pubtags).split(',');	
				} else {
					var tagArr = [data[g].pubtags];
				}
				for (var t = 0 ; t < tagArr.length; t++){
					$("li[pubid=" + data[g].pubid +"] div[category=tags]").append("<div class=\" label label-default\">" + tagArr[t].trim() + "</div>&nbsp;");	
				}
				$mPublication.append(
					"<option pubid=\"" + data[g].pubid + "\">" + data[g].pubname + "</option>"
				);
			}
		}
	} else {
		$("#viewPublication").append("No publications to display at the moment");
	}
}
function GetPublication(){
	$("#modifyPublicationList").css("border","");
	var $Publication = $("#modifyPublicationList").find(":selected");
	$Publication.val();
	if ($Publication.val() === "Select a Publication" || $Publication.val() == "Select a Publication"){
		$("#modifyPublicationList").css("border","1px solid firebrick");
		$("#modifyPublicationCitation").val("");
		$("#modifyPublicationProject input:checked").each(function(){
			$(this).prop("checked",false);
		});
	} else {
		var id   = $Publication.attr("pubid");
		$.ajax({
			type : "POST",
			url  : "/DMS/GetPublicationById",
			data : {jData : JSON.stringify({id : id})},
			dataType : "json",
			success : function(data){
				var pubInfo  = data[0];
				var projInfo = data[1];
				if(data){
					if(pubInfo.length == 1 ){
						$("#modifyPublicationCitation").val(pubInfo[0].title);
						$("#modifyPublicationTag").val(pubInfo[0].tags);
						$("#modifyPublicationProject input").each(function(){
							$(this).prop("checked",false);
						});
						for (var i = 0; i < projInfo.length; i++){
							$("#modifyPublicationProject input[pid=" + projInfo[i].projid + "]").prop("checked",true);
						}
					} else {
						alert("Try Again. Something went wrong.");
					}	
				}
			},
			error : function(){
				alert("Try Again. Something went wrong.");
			}
		});
	}
}
function modifyPublication(){
	
	$("#modifyPublicationList").css("border","");
	$("#modifyPublicationCitation").css("border","");
	$("#modifyPublicationProject").css("border","");
	$("#modifyPublicationTag").css("border","");
	
	var $Publication = $("#modifyPublicationList").find(":selected");
	if ($Publication.val() === "Select a Publication" || $Publication.val() == "Select a Publication"){
		$("#modifyPublicationList").css("border","1px solid firebrick");
		$("#modifyPublicationCitation").val("");
		$("#modifyPublicationProject input:checked").each(function(){
			$(this).prop("checked",false);
		});
	} else {
		$inputs      = $("#modifyPublicationProject input:checked");
		var citation = $Publication.val();
		var pubid    = $Publication.attr("pubid");
		var tags     = $("#modifyPublicationTag").val();
		
		tags=tags.trim();
		if(tags.endsWith(",")){
			tags = tags.substring(0,tags.length-1);
		}
		
		if(citation && $inputs.length > 0){
			var pids = "";
			var count = 0
			$inputs.each(function(){
				if (count == $inputs.length - 1){
					pids = pids + $(this).attr("pid");
				} else {
					pids = pids + $(this).attr("pid") + ",";
				}
				count++;
			});
			var data2Servlet = {citation : citation, 
								pids  : pids, 
								pubid : pubid,
								tags  : tags};
			$.ajax({
				type : "POST",
				url : "/DMS/ModifyPublication",
				data : {jData : JSON.stringify(data2Servlet)},
				success : function(){
					viewPublication();
					alert("Successfully added a new Publication.");
				},
				error : function(){
					alert("Please try again.");
				}
			});
		} else {
			if(!citation){
				$("#modifyPublicationCitation").css("border","1px solid firebrick");
			}
			if(!($inputs.length > 0)){
				$("#modifyPublicationProject").css("border","1px solid firebrick");
			}
		}
	}	
}

function addPublication(){
	
	$("#addPublicationCitation").css("border","");
	$("#addPublicationProject").css("border","");
	$("#addPublicationTag").css("border","");
	
	$inputs   = $("#addPublicationProject input:checked");
	var title = $("#addPublicationCitation").val();
	var tags  = $("#addPublicationTag").val();
	
	tags=tags.trim();
	if(tags.endsWith(",")){
		tags = tags.substring(0,tags.length-1);
	}
	
	if(title && $inputs.length > 0 && tags){
		var pids = "";
		var count = 0
		$inputs.each(function(){
			if (count == $inputs.length - 1){
				pids = pids + $(this).attr("pid");
			} else {
				pids = pids + $(this).attr("pid") + ",";
			}
			count++;
		});
		$.ajax({
			type : "POST",
			url : "/DMS/AddPublication",
			data : {jData : JSON.stringify({
					title : title,
					pids  : pids,
					tags  : tags
				})
			},
			
			success : function(){
				viewPublication();
				alert("Successfully added a new Publication.");
			},
			error : function(){
				alert("Please try again.");
			}
		});
	} else {
		if(!title){
			$("#addPublicationCitation").css("border","1px solid firebrick");
		}
		if(!($inputs.length > 0)){
			$("#addPublicationProject").css("border","1px solid firebrick");
		}
		if(!tags){
			$("#addPublicationTag").css("border","1px solid firebrick");
		}
	}
}