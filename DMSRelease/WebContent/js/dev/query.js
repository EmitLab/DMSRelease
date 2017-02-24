function getQueryByThisId(qid){
	$.ajax({
		type: "POST",
		url: "/DMS/GetQueryById",
		dataType: "json",
		data: {jData : JSON.stringify({"qid":qid})},
		success: function (data){
			console.log(data);
			var $parent = $("#viewQueryLi").parent();
			$parent.find("li").each(function(){
				$this = $(this);
				if($this.hasClass("active")){
					$this.removeClass("active");
				}
			});
			
			$parent = $("#viewQuery").parent();
			$parent.find("div").each(function(){
				$this = $(this);
				if($this.hasClass("active") && $this.hasClass("in")){
					$this.removeClass("active");
					$this.removeClass("in");
				}
			});
			
			$("#modifyQueryLi").addClass("active");
			$("#modifyQuery").addClass("active in");
			//$("#modifyQuery").addClass("in");
			
			$("#modifyQueryList option[qid=" + qid + "]").prop("selected",true);
			console.log(data);
			$("#modifyQueryName").val(data[0].name);
			$("#modifyQueryDescription").val(data[0].description);
			$("#modifyQueryQuery").val(data[0].query);
			
		},
		error: function(){
			console.log("Error");
		}
	});
}
function getQueryById(qid){
	$("#modifyQueryList").css("border","");
	$("#modifyQueryListError").hide();
	$("#modifyQueryName").val("");
	$("#modifyQueryDescription").val("");
	$("#modifyQueryQuery").val("");
	if (qid !== undefined && qid !== null && qid){
		getQueryByThisId(qid);
	} else {
		$selected = $("#modifyQueryList option:selected");
		if ($selected.text() === "Select a Query"){
			$("#modifyQueryList").css("border","1px solid firebrick");
			$("#modifyQueryListError").text("Query must be selected");
			$("#modifyQueryListError").addClass("error");
			$("#modifyQueryListError").show();
		} else {
			var qid = $("#modifyQueryList").find(":selected").attr("qid");
			getQueryByThisId(qid);
		}
	}
}

function modifyQuery(){
	
	$("#modifyQueryList").css("border","");
	$("#modifyQueryName").css("border","");
	$("#modifyQueryDescription").css("border","");
	$("#modifyQueryQuery").css("border","");
	$("#modifyQueryListError").hide();
	$("#modifyQueryNameError").hide();
	$("#modifyQueryDescriptionError").hide();
	$("#modifyQueryQueryError").hide();
	
	var qid         = $("#modifyQueryList").find(":selected").attr("qid");
	var name        = $("#modifyQueryName").val();
	var description = $("#modifyQueryDescription").val();
	var query       = $("#modifyQueryQuery").val();
	//var timed       = Date.now();
	if (qid && name && description && query){
		$.ajax({
			type: 'POST',
			url: '/DMS/ModifyQuery',
			data: {jData:JSON.stringify({"qid"         : qid,
										 "name"        : name,
										 "description" : description,
										 "query"       :query}//,"timed":timed}
			)},
			success:function(){
				getQueryList();
				
				$("#modifyQueryListError").text("Query Updated");
				$("#modifyQueryListError").removeClass("error");
				$("#modifyQueryListError").show();
				
				$("#modifyQueryName").val("");
				$("#modifyQueryDescription").val("");
				$("#modifyQueryQuery").val("");
			},
			error: function(){
				$("#modifyQueryListError").text("Query Updated");
				if (!$("#modifyQueryListError").hasClass("error")){
					$("#modifyQueryListError").addClass("error");
				}
				$("#modifyQueryListError").show();
			}
		});
	} else {
		if (!qid){
			$("#modifyQueryList").css("border","1px solid firebrick");
			$("#modifyQueryListError").text("Query must be selected");
			$("#modifyQueryListError").addClass("error");
			$("#modifyQueryListError").show();
		}
		if (!name){
			$("#modifyQueryName").css("border","1px solid firebrick");
			$("#modifyQueryNameError").text("Name cannot be empty.");
			$("#modifyQueryNameError").addClass("error");
			$("#modifyQueryNameError").show();
		}
		if (!description){
			$("#modifyQueryDescription").css("border","1px solid firebrick");
			$("#modifyQueryDescriptionError").text("Description cannot be empty.");
			$("#modifyQueryDescriptionError").addClass("error");
			$("#modifyQueryDescriptionError").show();
		}
		if (!query){
			$("#modifyQueryQuery").css("border","1px solid firebrick");
			$("#modifyQueryQueryError").text("Query cannot be empty.");
			$("#modifyQueryQueryError").addClass("error");
			$("#modifyQueryQueryError").show();
		}
	}
}

function addQuery(btn){
	$btn = $('#' + btn);
	$parent = $btn.parent().parent().parent();
	var name = $parent.find('input[type=text]').val();
	var $text = $parent.find('textarea');
	var count = 0;
	var description = "";
	var query = "";
	$text.each(function(){
		if (count == 0) {
			description = $(this).val();
		} else {
			query = $(this).val();
		}
		count++;
	});
	//var timed = Date.now();
	if (name && description && query){
		$.ajax({
			type: 'POST',
			url: '/DMS/AddQuery',
			data: {jData : JSON.stringify({
						'name' : name, 
						'description' : description, 
						'query' : query} 
						//'timed' : timed}
			)},
			success:function(){
				getQueryList();
			},
			error:function(){
				console.log('Something went Wrong');
			}
		});
	} else {
		if (!name){
			$("#addQueryName").css("border","1px solid firebrick");
			$("#addQueryNameError").text("Name cannot be empty.");
			$("#addQueryNameError").addClass("error");
			$("#addQueryNameError").show();
		}
		if (!description){
			$("#addQueryDescription").css("border","1px solid firebrick");
			$("#addQueryDescriptionError").text("Description cannot be empty.");
			$("#addQueryDescriptionError").addClass("error");
			$("#addQueryDescriptionError").show();
		}
		if (!query){
			$("#addQueryQuery").css("border","1px solid firebrick");
			$("#addQueryQueryError").text("Query cannot be empty.");
			$("#addQueryQueryError").addClass("error");
			$("#addQueryQueryError").show();
		}
	}
}

function getQueryLog(){
	$.ajax({
		type: "POST",
		url: "/DMS/GetQueryLog",
		dataType: "json",
		success:function(data){
			var logList = document.getElementById("viewQueryLogList");
			logList.innerHTML = "";
			
			var qCount = data.length;
				
			if (qCount < 1) {
				var spanNode = document.createElement("span");
					var spanText = document.createTextNode("No logs at the moments");
				spanNode.appendChild(spanText);
				logList.appendChild(spanNode);
			} else {
				for (var q = qCount -1 ; q >= 0; q--){
					var aNode = document.createElement("a");
					aNode.setAttribute("class", "list-group-item");
					aNode.setAttribute("qid", data[q].id);
					aNode.style.height = "30px";
					aNode.style.padding = "5px 10px";
						var lblNode = document.createElement("label");
						lblNode.style.width = "100%";
						lblNode.style.fontWeight = "600";
							var inputText = document.createTextNode(" " + data[q].name);
						lblNode.appendChild(inputText);
							var divNode = document.createElement("span");
							divNode.style.fontStyle = "oblique";
							divNode.style.fontWeight = "normal";
							divNode.setAttribute("class", "pull-right text-muted small");
								var date = agoTime(data[q].timed);
								var divText = document.createTextNode(data[q].action + " " + date + " ago");
							divNode.appendChild(divText);
						lblNode.appendChild(divNode);
					aNode.appendChild(lblNode);
					logList.appendChild(aNode);
				}
				$("#viewQueryLogList").find("*").each(function(){
					$(this).css("cursor","text");
				});
			}
		},
		error:function(){
			console.log("Oops! Some bugs need a fix");
		}
	});
}

function getQueryList(){
	getQueryLog();
	$.ajax({
		type: "POST",
		url: "/DMS/GetQueryList",
		dataType: "json",
		success:function(data){
			
			var uploadListNode = document.getElementById("modifyQueryList");
			uploadListNode.innerHTML = "";
				var optionNode = document.createElement("option");
					var optionText = document.createTextNode("Select a Query");
				optionNode.appendChild(optionText);
			uploadListNode.appendChild(optionNode);
			
			var listNode = document.getElementById("viewQueryList");
			listNode.innerHTML = "";
			var eCount = data.length;
			if (eCount == 0){
				var spanNode = document.createElement("span");
				var spanText = document.createTextNode("No query at the moment");
				spanNode.appendChild(spanText);
				listNode.appendChild(spanNode);
			} else {
				for (var i = 0; i < eCount; i++){
					var aNode = document.createElement("a");
					aNode.setAttribute("class", "list-group-item");
					aNode.setAttribute("qid", data[i].id);
					aNode.setAttribute("onclick", "getQueryById(" + data[i].id + ")");
						var lblNode = document.createElement("label");
						lblNode.setAttribute("style", "width:100%;cursor:pointer;");
							//var inputNode = document.createElement("input");
							//inputNode.setAttribute("type", "checkbox");
							var inputText = document.createTextNode(" " + data[i].name);
						//lblNode.appendChild(inputNode);
						lblNode.appendChild(inputText);
							var divNode = document.createElement("span");
							divNode.style.fontStyle = "oblique";
							divNode.style.fontWeight = "normal";
							divNode.setAttribute("class", "pull-right text-muted small");
								var date = (data[i].created == data[i].accessed) ? agoTime(data[i].created) : agoTime(data[i].accessed);
								var divText = document.createTextNode(data[i].action + " " + date + " ago");
							divNode.appendChild(divText);
						lblNode.appendChild(divNode);
					aNode.appendChild(lblNode);
				
					listNode.appendChild(aNode);
					
					optionNode = document.createElement("option");
					optionNode.setAttribute("qid", data[i].id);
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