function sendQuery() {
	var query = $("textarea[query=query]").val(); 
	//document.getElementById("bodyTag").setAttribute("query",query);
	var downSamplingStr = '';
	//Setting START DATE
	if (document.getElementById("startCheck").checked) {
		var startDate = document.getElementById("startDate").value;
		var startTime = document.getElementById("startTime").value;
		var dateArr = startDate.split("-");
		downSamplingStr = "From ={" + dateArr[1] + "/" + dateArr[2] + "/" + dateArr[0] + " " + startTime + "};";
	} else {
		downSamplingStr = "From ={01/01/2012 12:00:00};";
	}

	//Setting END DATE
	if (document.getElementById("endCheck").checked) {
		var endDate = document.getElementById("endDate").value;
		var endTime = document.getElementById("endTime").value;
		dateArr = endDate.split("-");
		downSamplingStr = downSamplingStr + "To={" + dateArr[1] + "/" + dateArr[2] + "/" + dateArr[0] + " " + endTime + "};";
	}else{
		downSamplingStr = downSamplingStr + "To={08/31/2012 12:00:00};";
	}
	//Setting SAMPLING Factor, Unit and Function
	if (document.getElementById("samplingCheck").checked) {
		var sampleFactor = document.getElementById("sampleFactor").value;

		var selectMenu = document.getElementById("sampleUnit");
		var sampleUnit = selectMenu.options[selectMenu.selectedIndex].getAttribute("key");

		downSamplingStr = downSamplingStr + "By={" + sampleFactor + "-" + sampleUnit + "};";

		selectMenu = document.getElementById("sampleFunction");
		var sampleFunction = selectMenu.options[selectMenu.selectedIndex].getAttribute("key");

		downSamplingStr = downSamplingStr + "function={" + sampleFunction + "};";
	}

	var spinner = new Spinner().spin();
	var target = document.getElementById('SpinnerEvent');
	target.appendChild(spinner.el);
	
	$.ajax({
		type : "POST",
		url: "/DMS/QueryMongo",
		data : {jData : JSON.stringify({'query' : query + " " + downSamplingStr}
		)},
		dataType: "json",
		 	success : function(data){
		 		console.log(data);
			var target = document.getElementById('SpinnerEvent');
			target.removeChild(target.childNodes[0]);
			querySendedHandler(data);
			
		},
		error : function(){
			alert('Something Terrible happened 2');
		}
	});
}


//function querySendedHandler() {
function querySendedHandler(data) {
	//if (myRequest.readyState == 4 && myRequest.status == 200) {

	//var e = document.getElementById('XMLQueryShow');
	//e.style.display = 'block';
	//var variable = myRequest.responseText;
	$("#resultDisplay").css("display","block");
	variable = data.result;
	//console.log(variable);
	if (!variable){
		if(variable == "700"){
			alert("The query does not produce results");
		} else if(variable == "701"){
			alert("The resultset is not empty but the cluster had some problem into the generation");
		} else {}
	} else {


	}

	var index = variable.indexOf("^");
	var JsonreturnedValue = variable.split("^");
	JSONClusterString = JsonreturnedValue[0];
	//console.log(JSONClusterString);
	result = JsonreturnedValue[1];

	NumQuery = NumQuery + 1;

	Minvalue = BigInteger(JsonreturnedValue[3]);
	MaxValue = BigInteger(JsonreturnedValue[2]) + 1;
	var JSONDataObject = JSON.parse(result);//[NumQuery-1]);
	var JSONClusterObject = JSON.parse(JSONClusterString);
	grayColorScheme = getGrayScale();
	//console.log(grayColorScheme);
	// Xilun: Add global legend
	if (colorScheme == null) {
		colorScheme = showLegend(MaxValue, Minvalue);
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

	var nodeID = '';
	var nodeType = 'm'; // Indicator of Meta Query, use 's' for Similarity Query. this flag enables and disables sorting of clusters in the hierarchy.
	var nodeCount = 1;

	// Creating the Hierarchy
	var tempNode = createHierarchy(nodeID, JSONClusterObject, nodeType, nodeCount, "");

	var nodeCount = 1;
	var nodePosition = 'middle';
	removeSingleLeafs(tempNode, nodeCount, nodePosition);

	var rootNode = document.getElementById("hierarchy");
	rootNode.innerHTML = "";
	rootNode.appendChild(tempNode);
	var flag = 0;

	$('li').on('click', function (event) {
		flag = 0;
		event.preventDefault();
		event.stopPropagation();

		var childernArr = $(this).children('ul').children('li');
		var childernIndex2 = [];
		var childernData2 = [];
		var childernCount2 = $(this).children('ul').children('li').length;

		var grayScaleFlags = [];
		if (event.target.nodeName == 'LI'){
			$(".grayScaleHeatmap").remove();
		}

		// Setting the Cluster Numbering 
		if($(this).attr('clustername')){
			$(this).childern('ul').attr('clustername',$(this).attr('clustername'));
		}
		parentClusterName = $(this).children('ul').attr('clustername');
		// Setting the cluster Node counter for each level.
		nodeCounter = 1;

		$(this).children('ul').children('li').each(function () {

			grayScaleFlags = new Array();

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

			grayScaleFlags.push(parseInt(grayScaleValue));
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
				if (typeof attr != typeof undefined && attr !== false) {

					flag = 2;

					var childernIndex = [];
					$.each(childernArr, function () {
						childernIndex.push($(this).attr('index'));
					});

					// Query is meta
					heatmap("#" + $(this).attr("id"), JSONDataObject[parseInt($(this).attr("Index"))]);
					// If query is sim
					//loadHeatmap(sbIDcomposed[i], sbIDX[i]);

					if ($(this).hasClass("member")) {
						var divLINodeID = $(this).attr("id") + "_Meta";
						var metaDivTextString = getMetaString(JSONDataObject[parseInt($(this).attr("Index"))].Meta);

						var metaDIV = document.createElement("div");
						var metaDivText = document.createTextNode(metaDivTextString);
						metaDIV.appendChild(metaDivText);
						metaDIV.setAttribute("id", divLINodeID);
						$(this).prepend(metaDIV);
					} else {
						document.getElementById($(this).attr("id") + "_Index").setAttribute("value", $(this).attr("Index"));
					}
				} else {
					// Do Nothing.
				}
			}
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
						// Do not counter singleton simulation node as a cluster 
					} else {
						clusterCount++;
					}
				});
				// Creating the Cluster Count Prefix
				var clusterCountPreffix = '#subCluster : ';
				if (clusterCount > 0){
					// Checking when not to append the cluster count
					// if (!$(this).text().includes('Models') && !$(this).text().includes('Properties') && !$(this).text().includes('#subCluster')){
					if ($(this).text().indexOf('Models') == -1 && !$(this).text().indexOf('Properties') == -1 && !$(this).text().indexOf('#subCluster') == -1){
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

					//if($(this).text().includes(delimiterClusterName)){
					if($(this).text().indexOf(delimiterClusterName)>-1){
						// If cluster number string already exists, then do not append the cluster number string
					} else {
						// Append cluster number string
						$(this).prepend(clusterNameTextNode);
					}
				}
			}
			// Increment the node counter
			nodeCounter++;
		});
		if (grayScaleFlags[0] == 1) {
			if (flag == 2 && childernCount2 > 1) {
				if (grayScaleFlags[1] == 1){
					getGrayScaleData(childernIndex2, childernData2, grayScaleFlags);

				}else{
					featureGrayscale(childernIndex2, childernData2);	
				}
			}

		}
		var listOfSVGs = $(this).children().first().prop('tagName');
		if (event.target.tagName == "DIV") {
			// DO Nothing
		} else {
			$('ul:first', event.target).toggle("slow");
		}
	});
	//}
}