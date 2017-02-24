function viewGrant(){
	$.ajax({
		type : "POST",
		url : "/DMS/GetGrantList",
		dataType : "json",
		success : function (data){
			displayGrant(data);
		},
		error : function(){
			alert("Cannot view the grant at the moment.");
		}
	});
}
function displayGrant(data){
	var gCount = data.length;
	$vGrant = $("#viewGrant").empty();
	$mGrant = $("#modifyGrantList").empty();
	$mGrant = $("#modifyGrantList").append("<option>Select a grant</option>");
	if(gCount > 0){
		for (var g = 0; g < gCount; g++){
			$vGrant.append(
					"<li class=\"list-group-item\" gid=\"" + data[g].gid + "\">" + data[g].gname + 
					"<div class=\"pull-right label label-primary\" pid = \"" + data[g].pid + "\">" + data[g].pname + "</div></li>"
			);
			$mGrant.append(
					"<option gid=\"" + data[g].gid + "\">" + data[g].gname + "</option>"
			);
		}
	} else {
		$("#viewGrant").append("No Grants at the Moment");
	}
}
function GetGrant(){
	var $grant = $("#modifyGrantList").find(":selected");
	$grant.css("border","");
	console.log($grant.val());
	if ($grant.val() === "Select a grant"){
		$grant.css("border","1px solid firebrick");
	} else {
		var gid   = $grant.attr("gid");
		console.log("Grant id - " + gid);
		$.ajax({
			type : "POST",
			url  : "/DMS/GetGrantById",
			data : {jData : JSON.stringify({gid : gid})},
			dataType : "json",
			success : function(data){
				if(data.length == 1 ){
					$("#modifyGrantName").val(data[0].name);
					$("#modifyGrantTitle").val(data[0].title);
					$("#modifyGrantLink").val(data[0].link);
					$("#modifyGrantProject").find("option[pid=" + data[0].pid + "]").prop("selected", true);	
				} else {
					alert("Try Again. Something went wrong.");
				}
			},
			error : function(){
				alert("Try Again. Something went wrong.");
			}
		});
	}
}
function modifyGrant(){
	$("#modifyGrantList").css("border","");
	$("#modifyGrantName").css("border","");
	$("#modifyGrantTitle").css("border","");
	$("#modifyGrantProject").css("border","");
	
	var $grant = $("#modifyGrantList").find(":selected");
	$grant.css("border","");
	console.log($grant.val());
	if ($grant.val() === "Select a grant"){
		$grant.css("border","1px solid firebrick");
	} else {
		var gid   = $grant.attr("gid");
		var name  = $("#modifyGrantName").val();
		var title = $("#modifyGrantTitle").val();
		var link  = $("#modifyGrantLink").val();
		var pid   = $("#modifyGrantProject").find(":selected").attr("pid");
		if(name && title && pid && gid){
			$.ajax({
				type : "POST",
				url : "/DMS/ModifyGrant",
				data : {jData : JSON.stringify({
						id    : gid, 
						name  : name,
						title : title,
						link  : link,
						pid   : pid
					})
				},
				dataType : "json",
				success : function(data) {
					console.log(data);
					displayGrant(data);
					alert("Successfully modified an existing grant.");
				},
				error : function(){
					alert("Please try again.");
				}
			});
		} else {
			if(!name){
				$("#modifyGrantName").css("border","1px solid firebrick");
			}
			if(!title){
				$("#modifyGrantTitle").css("border","1px solid firebrick");
			}
			if(!pid){
				$("#modifyGrantProject").css("border","1px solid firebrick");
			}
			if(!gid){
				$("#modifyGrantList").css("border","1px solid firebrick");
			}
		}
	}
	
	
}
function addGrant(){
	
	$("#addGrantName").css("border","");
	$("#addGrantTitle").css("border","");
	$("#addGrantProject").css("border","");
	
	var name  = $("#addGrantName").val();
	var title = $("#addGrantTitle").val();
	var link  = $("#addGrantLink").val();
	var pid   = $("#addGrantProject").find(":selected").attr("pid");
	
	if(name && title && pid){
		$.ajax({
			type : "POST",
			url : "/DMS/AddGrant",
			data : {jData : JSON.stringify({
					name : name,
					title : title,
					link : link,
					pid : pid
				})
			},
			dataType : "json",
			success : function(data) {
				displayGrant(data);
				alert("Successfully added a new grant.");
			},
			error : function(){
				alert("Please try again.");
			}
		});
	} else {
		if(!name){
			$("#addGrantName").css("border","1px solid firebrick");
		}
		if(!title){
			$("#addGrantTitle").css("border","1px solid firebrick");
		}
		if(!pid){
			$("#addGrantProject").css("border","1px solid firebrick");
		}
	}
}