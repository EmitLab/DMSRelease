
function toggleQueryMode(queryType){
	if(queryType == 1){
		$("div[queryType=metadata]").css("display","block");
		$("div[queryType=similarity]").css("display","none");
	} else {
		$("div[queryType=metadata]").css("display","none");
		$("div[queryType=similarity]").css("display","block");
		$("#resultDisplay").css("display","none");
		
		$.ajax({
			type: "POST",
			url: "/DMS/GetEnsembleList",
			dataType: "json",
			success:function(data){
				var eCount = data.length;
				if (eCount != 0) {
					//Remove all previous one
					$("#EnsembleSelect").empty();
					$("#EnsembleSelect").append("<option>Select an Ensemble</option>");
					for (var i = 0; i < eCount; i++){
					var x = document.getElementById("EnsembleSelect");
					var option = document.createElement("option");
					option.text = " " + data[i].name;
					option.setAttribute("eid", data[i].id);
					x.add(option);
					}
				} 
			},
			error:function(){
				alert("An Error Occured");
			}
		});
		
	}
}

function GetModelList(){
	var eid = $("#EnsembleSelect").find(":selected").attr("eid");// eid is 6 here
	console.log('EID here - ' + eid);
	$("#ModelSelect").css("border","");
	if(eid) {
		$.ajax({
			type : "POST",
			url : "/DMS/GetModelList", // Can you open this servlet
			data : {jData : JSON.stringify({eid : eid})},
			dataType : "json",
			success : function (data){
				console.log('Model List - ' + JSON.stringify(data));
				$("#ModelSelect").empty();
				$("#ModelSelect").append("<option>Select a Model</option>");
				var mCount = data.length;
				if(mCount > 0) {
					for (var i = 0; i < mCount ; i++ ){
						var x = document.getElementById("ModelSelect");
						var option = document.createElement("option");
						option.text = " " + data[i];
						x.add(option);
					}
					$("#ModelSelect").css("border","1px solid darkgreen");
				} else {
					alert("No Model Found");
				}
			},
			error : function(){
				console.log('Something went wrong. :( ');
			}
		});
	} else {
		console.log("Ensemble undefined");
	}
}

function GetPropertyList(){
	$("#EnsembleSelect").css("border","");
	$("#ModelSelect").css("border","");
	
	var eid   = $("#EnsembleSelect").find(":selected").attr("eid");
	var model = $("#ModelSelect").find(":selected").val();
	
	if(eid && model !== "Select a Model") {
		$.ajax({
			type : "POST",
			url : "/DMS/GetPropertyList",
			data : {jData : JSON.stringify({eid : eid, model : model})},
			dataType : "json",
			success : function (data){
				$("#PropertySelect").empty();
				$("#PropertySelect").append("<option>Select a Property</option>");
				var pCount = data.length;
				if(pCount > 0) {
					for (var i = 0; i < pCount ; i++ ){
						var x = document.getElementById("PropertySelect");
						var option = document.createElement("option");
						option.text = " " + data[i];
						x.add(option);
					}
					$("#PropertySelect").css("border","1px solid darkgreen");
				} else {
					alert("No Model Found");
				}
			},
			error : function(){
				console.log('Something went wrong. :( ');
			}
		});
	} else {
		if(!eid){
			$("#EnsembleSelect").css("border","1px solid firebrick");
		}
		if(model === "Select a Model"){
			$("#ModelSelect").css("border","1px solid firebrick");
		}
	}
}

function GetZoneList(){
	$("#EnsembleSelect").css("border","");
	$("#ModelSelect").css("border","");
	$("#PropertySelect").css("border","");
	
	var eid   = $("#EnsembleSelect").find(":selected").attr("eid");
	var model = $("#ModelSelect").find(":selected").val();
	var property = $("#PropertySelect").find(":selected").val();
	
	if(eid && model !== "Select a Model" && property !== "Select a Property") {
		$.ajax({
			type : "POST",
			url : "/DMS/GetZoneList",
			data : {jData : JSON.stringify({eid : eid, model : model, property : property})},
			dataType : "json",
			success : function (data){
				$("#ZoneSelect").empty();
				$("#ZoneSelect").append("<option>Select Zone(s)</option>");
				$("#sel-option").empty();
				var pCount = data.length;
				if(pCount > 0) {
					for (var i = 0; i < pCount ; i++ ){
						var x = document.getElementById("ZoneSelect");
						var option = document.createElement("option");
						option.text = " " + data[i];
						x.add(option);
						$("#sel-option").append("<li><input type=\"checkbox\">" + data[i] + "</li>");
					}
					$("#ZoneSelect").css("border","1px solid darkgreen");
				} else {
					alert("No Model Found");
				}
			},
			error : function(){
				console.log('Something went wrong. :( ');
			}
		});
	} else {
		if(!eid){
			$("#EnsembleSelect").css("border","1px solid firebrick");
		}
		if(model === "Select a Model"){
			$("#ModelSelect").css("border","1px solid firebrick");
		}
		if(property === "Select a Property"){
			$("#PropertySelect").css("border","1px solid firebrick");
		}
	}
}

function SearchSimilar(){
	
	listSVG = document.getElementsByClassName("dataHeatmap");
	var i = 1;
	var node = listSVG[i].parentNode;
	var JSONDataObject = JSON.parse(result);
	var content = JSONDataObject[document.getElementById(node.id + "_Index").getAttribute("value")];
	var title1 = getMetaString(content.Meta);
	var querySimID = title1.split(">")[0];
	console.log("query SimID: " + querySimID);
	
	var eid   = $("#EnsembleSelect").find(":selected").attr("eid");
	var model = $("#ModelSelect").find(":selected").val();
	var property = $("#PropertySelect").find(":selected").val();
	// var zones = $("#ZoneSelect").find(":selected").val();
	console.log($('#ZoneSelect'));
	var zones = $("#ZoneSelect").find(":checked").val();
	console.log("we select zone(s): " + zones);
	// if(eid && model !== "Select a Model" && property !== "Select a Property" && zones !== "Select Zone(s)") {
	if(eid && model !== "Select a Model" && property !== "Select a Property") {
		$.ajax({
			type : "POST",
			url : "/DMS/FeatureExtraction",
			data : {jData : JSON.stringify({searchType: 'jsonfile', eid : eid, model : model, metric : property, zones : zones, querySimID: querySimID})},
			dataType : "text",
			success : function (data){
				SearchSimilarFromMetaQuery(data);
				console.log("done with similarity search")
				// call function to plot heat map
			},
			error : function(){
				console.log('Something went wrong. :( ');
			}
		});
	}
}

function SearchSimilarFromMetaQuery(samosas){
	$('#minlocal').val('');
	$('#maxlocal').val('');
	// 	$('#LegendDIV').html('');
	$('#hierarchy').html('');
	
	$("#resultDisplay").css("display","block");
	Minvalue =-1;//0;// BigInteger(0);
	MaxValue = BigInteger(10000000000);
	//create colourscheme
	grayColorScheme = getGrayScale();
	if (colorScheme == null) {
		//console.log("scale already present");
		colorScheme = showLegend(MaxValue, Minvalue);
		var cntnt = document.getElementById('LegendDIV');
		//console.log('I am Here : ' + cntnt.childNodes.length);
		while (cntnt.childNodes.length > 1) {
			cntnt.removeChild(cntnt.lastChild);
		}
	} else {
		document.getElementById("minlocal").value = BigInteger.toJSValue(BigInteger(Minvalue));
		document.getElementById("maxlocal").value = BigInteger.toJSValue(BigInteger(MaxValue));
	}

	var buckets = colorScheme.length;
	var deltaHue;
	deltaHue=(MaxValue * 1000 - Minvalue * 1000) / (buckets - 1);

	for (var i = 0; i < buckets; i++) {
		pivots[i] = BigInteger.toJSValue(BigInteger(Minvalue * 1000).add(deltaHue * i));
	}
	
	var JSONClusterObject = JSON.parse(samosas);
	// Creating the Hierarchy
	// document.getElementById("mutliSelect").setAttribute("class", "mutliSelect");

	var nodeID = '';
	var nodeType = 'r';
	var nodeCount = 1;
	//console.log('Cluster String :: -- ' + JSON.stringify(JSONClusterObject));
	//it seems here it is still apppending  under the same node and not removing the old ones
	document.getElementById("hierarchy").innerHTML="";
	var tempNode = createHierarchy(nodeID, JSONClusterObject, 's', nodeCount, "");
//	seems correct since here
	var nodeCount = 1;
	var nodePosition = 'middle';
	removeSingleLeafs(tempNode, nodeCount, nodePosition);

	var rootNode = document.getElementById("hierarchy");
	rootNode.innerHTML = "";
	rootNode.appendChild(tempNode);
	var flag = 0;
	
	$("li[nodeType='H']").on('click', function (event) {
		event.preventDefault();
		event.stopPropagation();

		var childernArr = $(this).children('ul').children('li');
		var childernIndex2 = [];
		var childernData2 = [];
		var childernCount2 = $(this).children('ul').children('li').length;

		var grayScaleFlags = [];
		/*if (event.target.nodeName == 'LI'){
			$(".grayScaleHeatmap").remove();
		}*/
		/* remove to try SILV*/
		// Setting the Cluster Numbering see the tag below if you comment the code from here to theend  the selection continue to work be 
		if($(this).attr('clustername')){
			$(this).childern('ul').attr('clustername',$(this).attr('clustername'));
		}
		parentClusterName = $(this).children('ul').attr('clustername');
		// Setting the cluster Node counter for each level.
		nodeCounter = 1;
		var nodeC = 0;
		$(this).children('ul').children('li').each(function () {

			// Appending the parent cluster number to child. 
			if(parentClusterName == '' || typeof parentClusterName == typeof undefined){
				childClusterName = nodeCounter;
			} else {
				childClusterName = parentClusterName + '.' + nodeCounter;
			}
			var delimiterClusterName = '>';

			if($(this).has('ul')){
				var clusterCount = 0;
				$(this).children('ul').children('li').each( function() {
					if ($(this).hasClass('member')){
						// Do not count singleton simulation node as a cluster 
					} else {
						clusterCount++;
					}
				});
				// Creating the Cluster Count Prefix
				var clusterCountPreffix = '#subCluster : ';
				if (clusterCount > 0){
					// Checking when not to append the cluster count
					if (!$(this).text().includes('Models') && !$(this).text().includes('Properties') && !$(this).text().includes('#subCluster')){
						// Replacing the Property name
						var pAttr = $(this).attr('properties');
						// checking if Property Attribute Exists
						if( typeof pAttr !== typeof undefined && pAttr !== false){
							// if it exists
							var propertyStr = pAttr + ', ';
						} else {
							// if it doesn't exist
							var propertyStr = '';
						}
						// Adding the Cluster Count to LI Node 
						$(this).prepend(document.createTextNode(propertyStr + clusterCountPreffix + clusterCount + ', '));
					}
				}
				// Saving current Node cluster number as Attribute in it.
				$(this).children('ul').attr('clustername',childClusterName);
				// Creating Text node to show cluster number.
				clusterNameTextNode = document.createTextNode(childClusterName + delimiterClusterName + ' ');
				if ($(this).hasClass("member")) {
					// If the node is a simulation, then do not append the cluster number string.
				}else{

					if($(this).text().includes(delimiterClusterName)){
						// If cluster number string already exists, then do not append the cluster number string
					} else {
						// Append cluster number string
						$(this).prepend(clusterNameTextNode);
					}
				}
			}
			// Increment the node counter
			nodeCounter++;
			/*	grayScaleFlags = new Array();

			var grayScaleMenu = document.getElementById("grayScaleMenu");
			var grayScaleValue = grayScaleMenu.options[grayScaleMenu.selectedIndex].getAttribute("key");

			grayScaleFlags.push(parseInt(grayScaleValue));

			var grayScaleTyleMenu = document.getElementById("grayScaleTypeMenu");
			var grayScaleValue = grayScaleTypeMenu.options[grayScaleTypeMenu.selectedIndex].getAttribute("key");

			grayScaleFlags.push(parseInt(grayScaleValue));

			var grayScaleNomralizationMenu = document.getElementById("grayScaleNormalizationMenu");
			var grayScaleValue = grayScaleNomralizationMenu.options[grayScaleNomralizationMenu.selectedIndex].getAttribute("key");

			grayScaleFlags.push(parseInt(grayScaleValue));

			var grayScaleDifferenceMenu = document.getElementById("grayScaleDifferenceMenu");
			var grayScaleValue = grayScaleDifferenceMenu.options[grayScaleDifferenceMenu.selectedIndex].getAttribute("key");

			grayScaleFlags.push(parseInt(grayScaleValue));*/

			// JSONDataObject does not exist because we did not retrieve the time series data. 
			// This data will be retrieved on-the-fly based on the hierarchy, each node of the 
			// hierarchy have name=simid metric = metric is there in the  node of the hierarchy, 
			// model is the model selected or extracted from the  second node and project is the project.
			// It is exactly the same format of the hierarchical Json I retrived in the  metadata query the clusterstring.
			// Hey SIlvestro , This is Yash, what is the current format of the data???
			// It will be the same of the previus
			// - Yash - like the cluster:{[.....]}  this one ??
			// yes it is the same The i
			//n//dian restourant is expensive hehehehehe
			// Can you put the cluster string in console.log(....); in the javascript
			//ok

			/*
			 * Below code does create the array of children in the level of interest to compute gray scale
			var temp;
			if (typeof(JSONDataObject[$(this).attr("Index")]) === 'undefined') {
				temp = JSONDataObject[$(this).attr("Index")];
			} else {
				temp = JSON.parse(JSON.stringify(JSONDataObject[$(this).attr("Index")]));
			}
			if (childernCount2 > 1) {
				childernIndex2.push($(this).attr("id"));
				childernData2.push(temp);//JSON.parse(JSON.stringify(temp)));
			}
			 */
			/* remove to try SILV*/
			if ($(this).is(':visible')) {

				flag = 1;
				if (event.target.tagName != "INPUT" && event.target.tagName != "rect") {
					collapseAll($(this).attr("id"));
					if (event.target.tagName == 'li') {
						$(this).children("svg").remove();
						$(this).children("div").remove();
						$(this).children("input").remove();
						$(this).children("span").remove();
					}
				}
			} else {

				if (event.target.tagName == "rect") {
					// DO NOTHING
				} else {
					$(this).find('svg').each(function () {
						$(this).remove();
					});
					$(this).find('div').each(function () {
						$(this).remove();
					});
					$(this).find('input').each(function () {
						$(this).remove();
					});
					$(this).find('span').each(function () {
						$(this).remove();
					});
				}                  

				var attr = $(this).attr("Index");
				var tempAttr = $(this).attr("properties");

				if (typeof attr != typeof undefined && attr !== false) {

					flag = 2;

					var childernIndex = [];
					$.each(childernArr, function () {
						childernIndex.push($(this).attr('index'));
					});

					var propertyAttr = $(this).attr('properties');
					if( typeof propertyAttr !== typeof undefined && propertyAttr !== false){
						// Do Nothing
					}else{
						var heatmapNodeID = $(this).attr('id');

						// loadHeatmap(heatmapNodeID,nodeC);
						// loadHeatmap(id,index)

						// curr refers to current;

						var heatmapDataAttrCheck = $("#" + heatmapNodeID).attr("heatmapdata");

						if (typeof heatmapDataAttrCheck !== typeof undefined && heatmapDataAttrCheck !== false){

							var Datarecived = JSON.parse(heatmapDataAttrCheck);
							SimilarityDataRetrived[nodeC]=Datarecived;
							Datain2win[nodeC]=Datarecived; //the problem is here 

							heatmap("#" + heatmapNodeID,Datarecived);//, zoneList, flag);
							if ($("#" + heatmapNodeID).hasClass("member")) {
								/*
								var divLINode = document.createElement('span');
								var divLINodeID = $(this).attr("id") + "_Meta";
								divLINode.setAttribute("id", divLINodeID);
								 */
								//console.log("MEM ID - " + heatmapNodeID);
								//var divLINode = document.createElement('span');
								//var divLINodeID = $("#" + heatmapNodeID).attr("id") + "_Meta";
								/* remove to try SILV*/
								var divLINodeID = heatmapNodeID + "_Meta";
								//divLINode.setAttribute("id", divLINodeID);

								var metaDivTextString = getMetaString(Datarecived.Meta);

								//var metaDIV = document.createElement("span");
								var metaDIV = document.createElement("span");
								var metaDivText = document.createTextNode(metaDivTextString);
								metaDIV.appendChild(metaDivText);
								metaDIV.setAttribute("id", divLINodeID);

								//document.getElementById($("#" + heatmapNodeID)).insertBefore(metaDIV, document.getElementById($("#" + heatmapNodeID)).firstChild);
								//$("#" + heatmapNodeID).prepend(metaDIV);

								//document.getElementById($(this).attr("id")).insertBefore(metaDIV, document.getElementById($(this).attr("id")).firstChild)
								$("#" + heatmapNodeID).prepend(metaDIV);


							} else {
								//document.getElementById($(this).attr("id") + "_Index").setAttribute("value", $(this).attr("Index"));
							}

						} else {

							var Project= document.getElementById('hierarchy').childNodes[0].getAttribute('id');
							Project = Project.toLowerCase();
							//var Project= "energy";
							//alert(Project);
							var System="mongodb";
							var nodeIDArr = heatmapNodeID.split("_");
							var currModel = "Model=" + nodeIDArr[1] + ",";//SIR,";
							var currMetric = "Metric=" + nodeIDArr[2] + ",";//Incidence,";
							//var currZones = document.getElementById("Zones").value;

							var currZones = "";
							/*var inputSelected = document.querySelectorAll('input[type="checkbox"]:checked');
							var selectedItemSize = inputSelected.length;
							for(var ii=0; ii < selectedItemSize; ii++){
								var tqueryVariate = inputSelected[ii].id + ",";
								currZones = currZones + tqueryVariate;
							}*/
							/* remove to try SILV		*/						
							if (typeof InterestedVariatetobeused == undefined){
								currZones=InterestedVariatetobeused;	
							} else {
								if(Project =="energy")
									currZones = 'PLENUM-1,SPACE1-1,SPACE2-1,SPACE3-1,SPACE4-1,SPACE5-1';
								else {
									currZones = 'AR,CA,AZ';
								}
							}
							
							console.log(currZones);
							if (currZones.endsWith(",")){
								currZones = currZones.substring(0,currZones.length - 1);
							} else {
								// Do Nothing
							}

							currZones = currZones.replace(/,/g,"|")+",";
							var zone = "Zones="+currZones.replace(/,/g,"|")+",";

							// The last element element with always be the simulation id. 
							var SimID = "SimID=" + nodeIDArr[nodeIDArr.length-1]+",";

							var startTime = "Ts=,";
							var endTime = "Te=,";
							//console.log("Current Project in Hierarchy: " + Project);
							if(Project =="energy")
								var downsampling = "DS=1-D-avg,";
							else
								var downsampling = "DS=,";
							//var downsampling = "DS=,";

							var collection = "CN="+nodeIDArr[0];
							//var collection = "CN=Energy";

							var reqMongo = null;
							var query = startTime + endTime + currModel + SimID + currMetric + zone + downsampling + collection; //miss zones
							$.ajax({
								type: "POST",
								url : "/DMS/GetSimilarityChildData",
								data : {
									type : 'complete',
									query : query
								},
								dataType : "text",
								success : function(data){
									$('#'+heatmapNodeID).attr('heatmapType','SH');
									$('#'+heatmapNodeID).attr('heatmapdata',data.split('@@@')[0]);

									$('#'+heatmapNodeID).attr('Indexofthedata',nodeC);
									/**
									 * Sicong and Reece change here
									 * Delimiter to separate time series data and metadata info
									 * */
									var tempData = data;
									//console.log(tempData);
									tempData = tempData.split('@@@')[0];
									var Datarecived = JSON.parse(tempData);
									/**
									 * Sicong and Reeece change end
									 * */
									//var Datarecived = JSON.parse(reqMongo.responseText);
									SimilarityDataRetrived[nodeC]=Datarecived;
									Datain2win[nodeC]=Datarecived;
									heatmap("#" + heatmapNodeID,Datarecived);//, zoneList, flag);
									if ($("#" + heatmapNodeID).hasClass("member")) {

										/*
										var divLINode = document.createElement('span');
										var divLINodeID = $(this).attr("id") + "_Meta";
										divLINode.setAttribute("id", divLINodeID);
										 */
										//console.log("MEM ID - " + heatmapNodeID);
										//var divLINode = document.createElement('span');
										//var divLINodeID = $("#" + heatmapNodeID).attr("id") + "_Meta";
										/* remove to try SILV*/
										var divLINodeID = heatmapNodeID + "_Meta";
										//divLINode.setAttribute("id", divLINodeID);

										var metaDivTextString = getMetaString(Datarecived.Meta);

										var metaDIV = document.createElement("div");
										var metaDivText = document.createTextNode(metaDivTextString);
										metaDIV.appendChild(metaDivText);
										metaDIV.setAttribute("id", divLINodeID);

										//document.getElementById($("#" + heatmapNodeID)).insertBefore(metaDIV, document.getElementById($("#" + heatmapNodeID)).firstChild);
										$("#" + heatmapNodeID).prepend(metaDIV);
									} else {
										//document.getElementById($(this).attr("id") + "_Index").setAttribute("value", $(this).attr("Index"));
									}
								},
								error : function(){
									console.log('Cannot get data for child nodes');
								}
							});
							/*
							var MongoQuery = "/MVTSDB/ServletController?ClusterRequest=downsampling&type=complete&project="+Project+"&System="+System+"&query=" + query;//https://hive.asu.edu:8443
							var xmlhttp;
							if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
								reqMongo = new XMLHttpRequest();
							}
							else {// code for IE6, IE5
								reqMongo = new ActiveXObject("Microsoft.XMLHTTP");
							}
							reqMongo.onreadystatechange = function () {
								if (reqMongo.readyState == 4 && reqMongo.status == 200) {

									// This attribute will be used to check if the heatmap function 
									// for a particular type of heatmap plotting.

									$('#'+heatmapNodeID).attr('heatmapType','SH');
									$('#'+heatmapNodeID).attr('heatmapdata',reqMongo.responseText);

									$('#'+heatmapNodeID).attr('Indexofthedata',nodeC);
\
									var tempData = reqMongo.responseText;
									//console.log(tempData);
									tempData = tempData.split('@@@')[0];
									var Datarecived = JSON.parse(tempData);

									//var Datarecived = JSON.parse(reqMongo.responseText);
									SimilarityDataRetrived[nodeC]=Datarecived;
									Datain2win[nodeC]=Datarecived;
									heatmap("#" + heatmapNodeID,Datarecived);//, zoneList, flag);
									if ($("#" + heatmapNodeID).hasClass("member")) {

										
										// var divLINode = document.createElement('span');
										// var divLINodeID = $(this).attr("id") + "_Meta";
										// divLINode.setAttribute("id", divLINodeID);
										//console.log("MEM ID - " + heatmapNodeID);
										//var divLINode = document.createElement('span');
										//var divLINodeID = $("#" + heatmapNodeID).attr("id") + "_Meta";
										var divLINodeID = heatmapNodeID + "_Meta";
										//divLINode.setAttribute("id", divLINodeID);

										var metaDivTextString = getMetaString(Datarecived.Meta);

										var metaDIV = document.createElement("div");
										var metaDivText = document.createTextNode(metaDivTextString);
										metaDIV.appendChild(metaDivText);
										metaDIV.setAttribute("id", divLINodeID);

										//document.getElementById($("#" + heatmapNodeID)).insertBefore(metaDIV, document.getElementById($("#" + heatmapNodeID)).firstChild);
										$("#" + heatmapNodeID).prepend(metaDIV);
									} else {
										//document.getElementById($(this).attr("id") + "_Index").setAttribute("value", $(this).attr("Index"));
									}
								}
							}
							reqMongo.open("GET", MongoQuery, true);
							reqMongo.send(null);
							*/
						}
					}	
				} else {
					//alert('Has no Index') to append Heatmap
				}
			}

		});
		nodeC = nodeC + 1;
		/*
		if (grayScaleFlags[0] == 1) {

			if (flag == 2 && childernCount2 > 1) {
				if (grayScaleFlags[1] == 1){
					getGrayScaleData(childernIndex2, childernData2, grayScaleFlags);
				}else{
					//console.log(childernIndex2);
					featureGrayscale(childernIndex2, childernData2);
					//		alert('RMT feature based module will be printed here');	
				}
			}

		}
		 */
		/* remove to try SILV*/
		var listOfSVGs = $(this).children().first().prop('tagName');
		if (event.target.tagName == "DIV") {
			// DO Nothing
		} else {
			$('ul:first', event.target).toggle("slow");
		}
	});
	
}

function getQueryById(qid){
	$.ajax({
		type: "POST",
		url: "/DMS/GetQueryById",
		dataType: "json",
		data: {jData : JSON.stringify({"qid" : qid})},
		success: function (data){
			var qCount = data.length;
			if(qCount != 1){
				$("queryList").css("border", "1px solid firebrick");
			} else {
				$("input[query=name]").val(data[0].name);
				$("textarea[query=query]").val(data[0].query);
				$("textarea[query=description]").val(data[0].description);
			}
		},
		error: function(){
			$("#queryList").css("border", "1px solid green");
		}
	});
}

function getQuery(){
	$("#queryList").css("border","");
	$selected = $("#queryList option:selected");
	$("input[query=name]").css("border","");
	$("textarea[query=query]").css("border","");
	$("textarea[query=description]").css("border","");
	if ($selected.text() === "Select a Query"){
		$("#queryList").css("border","1px solid firebrick");
		$("input[query=name]").val("");
		$("textarea[query=query]").val("");
		$("textarea[query=description]").val("");
		$("button[query=modify]").prop("disabled",true);
		$("button[query=delete]").prop("disabled",true);
	} else {
		$("button[query=modify]").prop("disabled",false);
		$("button[query=delete]").prop("disabled",false);
		var qid = $selected.attr("qid");
		getQueryById(qid);
	}
}
function deleteQuery(){
	$selected = $("#queryList option:selected");
	var qid = $selected.attr("qid");
	$.ajax({
		type : "POST",
		url : "/DMS/DeleteQuery",
		data : {jData : JSON.stringify({'qid' : qid})},
		success : function () {
			alert("Query Deleted");
			getQueryList();
		},
		error : function (){
			alert("Try again later");
		}
	});
}

function modifyQuery(){
	$("input[query=name]").css("border","");
	$("textarea[query=query]").css("border","");
	$("textarea[query=description]").css("border","");
	$selected = $("#queryList option:selected");
	var qid = $selected.attr("qid");
	var name = $("input[query=name]").val();
	var query = $("textarea[query=query]").val();
	var description = $("textarea[query=description]").val();
	if (qid && name && query && description){
		$.ajax({
			type : "POST",
			url : "/DMS/ModifyQuery",
			data : {jData : JSON.stringify({
						'qid'         : qid,
						'name'        : name,
						'query'       : query,
						'description' : description
					}
			)},
			success : function(){
				alert("Query Modified Successfully");
				getQueryList();
			},
			error : function(){
				alert("Error : Try again later");
			}
		});
	} else {
		if (!name){
			$("input[query=name]").css("border","1px firebrick solid");
		}
		if (!query){
			$("textarea[query=query]").css("border","1px firebrick solid");
		}
		if (!description){
			$("textarea[query=description]").css("border","1px firebrick solid");
		}
	}
}

function addQuery(){
	$("input[query=name]").css("border","");
	$("textarea[query=query]").css("border","");
	$("textarea[query=description]").css("border","");
	
	var name = $("input[query=name]").val();
	var query = $("textarea[query=query]").val();
	var description = $("textarea[query=description]").val();
	if (name && query && description){
		$.ajax({
			type : "POST",
			url : "/DMS/AddQuery",
			data : {jData : JSON.stringify({
						'name' : name,
						'query' : query,
						'description' : description
					}
			)},
			success : function(){
				alert("Query Added Successfully");
				getQueryList();
			},
			error : function(){
				alert("Error : Try again later");
			}
		});
	} else {
		if (!name){
			$("input[query=name]").css("border","1px firebrick solid");
		}
		if (!query){
			$("textarea[query=query]").css("border","1px firebrick solid");
		}
		if (!description){
			$("textarea[query=description]").css("border","1px firebrick solid");
		}
	}
}

function getQueryList(){
	$("button[query=modify]").prop("disabled",true);
	$("button[query=delete]").prop("disabled",true);
	$.ajax({
		type : "POST",
		url : "/DMS/GetQueryList",
		dataType : "json",
		success : function(data){
			var qCount = data.length;
			var sNode = document.getElementById("queryList");
			sNode.innerHTML = "";
			$("input[query=name]").val("");
			$("textarea[query=query]").val("");
			$("textarea[query=description]").val("");
			
			var oNode = document.createElement("option");
			var oText = document.createTextNode("Select a Query");
			oNode.appendChild(oText);
			sNode.appendChild(oNode);
			
			if (qCount > 0){
				for ( var q = 0; q < qCount; q++){
					var oNode = document.createElement("option");
					var oText = document.createTextNode(data[q].name);
					oNode.appendChild(oText);
					oNode.setAttribute("qid",data[q].id);
					sNode.appendChild(oNode);
				}
			}
		},
		error : function(){
			console.log("Error");
		}
	});
}

