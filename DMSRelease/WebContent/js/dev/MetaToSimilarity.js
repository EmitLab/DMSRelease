function migratePageContent(nodeID, zoneList){

	$("#queryHeatmap").empty();

	$("div[queryType=metadata]").css("display","none");
	$("#resultDisplay").css("display","none");
	$("input[query=metadata]").prop("checked",false);
	$("input[query=similarity]").prop("checked",true);
	$("div[queryType=similarity]").css("display","block");
	
	listSVG = document.getElementsByClassName("dataHeatmap");
	var node;
	if (listSVG.length == 1){
		node = listSVG[0].parentNode;
	} else {
		node = listSVG[1].parentNode;
	}
	var JSONDataObject = JSON.parse(result);
	var content = JSONDataObject[document.getElementById(node.id + "_Index").getAttribute("value")];
	var flag = false;

	heatmapjson("#queryHeatmap", content, flag, node.id);
	$("#uploadTimeSeriesFile").css('display','none');
	$('#uploadGraphFile').css('display','none');
	$.ajax({
		type: "POST",
		url: "/DMS/GetEnsembleList",
		dataType: "json",
		success:function(data){
			var eCount = data.length;
			if (eCount != 0) {
				//Remove all previous one
				$("#EnsembleSelect").empty();
				var x = document.getElementById("EnsembleSelect");
				var option = document.createElement("option");
				option.text = "Select an Ensemble";
				option.setAttribute("eid", 0);
				x.add(option);
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

	/*
	//here  we are calling a servlet to reprint the page.
	$.ajax({
		type: "POST",
		url: "/DMS/GetSimulationByIdWithZones",
		data : {nodeId: nodeID, zones : zoneList},
		//dataType: "json",
		success:function(){
			//var eCount = data.length;
			alert("Am here");
		},
		error:function(){
			alert("An Error Occured");
		}
	});
	 */
}

function heatmapjson(nodeID, content, flag, realID) {

	var title1 = getMetaString(content.Meta);
	var nodetitle = document.createElement("div");
	var maxValue = 0;
	var minValue = 10000000000;
	var dataset = [];
	var keyList = Object.keys(content.Data[0].dps);
	var cuttedContent= new Array();
	var numTimePoints = keyList.length;
	var numDataPoints = content.Data.length;
	var indices = null;
	var variateLabels =new Array();


	for (var i = 0; i < numDataPoints; i++) 
		variateLabels.push(content.Data[i].zone);

	var len = variateLabels.length;
	indices = new Array(len);
	for (var i = 0; i < len; ++i) {
		indices[i] = i;
	}

	var cellw = parseInt(Math.ceil(screen.width * 0.70) / numTimePoints) + 1;
	var cellh = 6;

	var padding = 0;

	var w = Math.ceil(screen.width * 0.70);
	var h = (cellh * numDataPoints) + padding;
	var schakels = new Array();

	var start = new Date(keyList[0] * 1 + (offset));
	var end = new Date(keyList[0] * 1 + (offset));

	if(flag == true){
		variateLabels.sort();
		indices.sort();
	}else{
		indices.sort(function (a, b) {
			return variateLabels[a] < variateLabels[b] 
			? -1 
					: variateLabels[a] > variateLabels[b] 
					? 1 
							: 0;
		});
	}

	var date;
	var variateID;
	var timeList= new Array(numTimePoints);
	for (var i = 0; i < numDataPoints; i++) {
		variateID = indices[i];
		var schakel_id = content.Data[variateID].zone;
		var row = new Array()

		for (var j = 0; j < numTimePoints; j++) {
			if (i == 0){
				timeList[j] = parseFloat(keyList[j]);
			}
			var speed = content.Data[variateID].dps[keyList[j]] * 1000;

			if (speed < minValue) {
				minValue = speed;
			}
			if (speed > maxValue) {
				maxValue = speed;
			}
			date = new Date((keyList[j] * 1) + (offset));

			if (start > date) {
				start = date;
			}
			if (end < date) {
				end = date;
			}
			row.push([schakel_id, date, speed]);
		}
		dataset.push(row);
		schakels.push(schakel_id);
	}
	timeList = timeList.sort();
	var timeDiff = new Array(numTimePoints-1);
	for (var j = 1; j <numTimePoints - 1; j ++){
		timeDiff[j-1] = timeList[j] - timeList[j-1];
	}

	var colorScheme = showSimilarityLegend(maxValue, minValue);
	var buckets = colorScheme.length;
	var deltaHue = (maxValue  - minValue ) / (buckets - 1);
	var pivots = Array();
	for (var i = 0; i < buckets; i++) {
		pivots[i] = BigInteger.toJSValue(BigInteger(minValue ).add(deltaHue * i));
	}
	/**   Xilun Expanded Heatmap **/
	var paraStruct = new Array();
	paraStruct['numTimePoints'] = numTimePoints;
	paraStruct['numDataPoints'] = numDataPoints;
	paraStruct['dataset'] = dataset;
	paraStruct['schakels'] = schakels;
	paraStruct['start'] = start;
	paraStruct['end'] = end;
	/**  Xilun  end    **/
	var xscale = d3.time.scale() //d3_time_scaleDate()//
	.domain([start, end])
	.range([padding, w - padding]);

	var xAxis = d3.svg.axis()
	.scale(xscale)
	.orient("bottom")
	.ticks(d3.time.days, 25);

	var yscale = d3.scale.ordinal()
	.domain(schakels)
	.rangeBands([padding, h - padding]);

	var yAxis = d3.svg.axis()
	.scale(yscale)
	.orient('left');

	var zscale = d3.scale.linear()
	.domain(pivots)					
	.range(colorScheme);


	minValue = minValue/1000;
	maxValue = maxValue/1000;

	var svg = d3.select(nodeID)
	.insert('svg', ':first-child')
	.attr('width', w)
	.attr('height', h)
	.attr('class', 'dataHeatmap');

	var row = svg.selectAll('.row')
	.data(dataset)
	.enter()
	.append('svg:g')
	.attr('class', 'row');

	var col = row.selectAll('.cell')
	.data(function (d) {
		return d;
	})
	.enter().append('rect')
	.attr('class', 'cell')
	.attr('x', function (d) {

		return xscale(d[1]);
	})
	.attr('y', function (d) {
		return yscale(d[0]);
	})
	.attr('width', cellw)
	.attr('height', cellh)
	.attr('fill', function (d) {
		return zscale(d[2]);
	})
	.on('mousemove', function (d) {
		mousemove(d)
	})
	.on("mouseover", mouseover)
	.on("mouseout", mouseout)
	.on("click", mouseclick);
	svg.append("g")
	.attr("class", "axis")
	.attr("transform", "translate(0," + (h - padding) + ")")
	.call(xAxis);

	svg.append("g")
	.attr("class", "axis")
	.attr("transform", "translate(" + padding + ",0)")
	.call(yAxis);

	var div = d3.select(nodeID)
	.append("div")
	.attr("class", "tooltip")
	.style("opacity", 1.0);

	d3.select(nodeID)
	.insert("span", ":first-child")
	.html(title1+"<br>");

	function mousemove(d) {
		var locD2 = d[2]/1000;
		div.text("Info about " + d[0] + " : " + d[1] + " : " +locD2)//+ d[2] / 1000)
		.style("left", (cursorX - 5) + "px")
		.style("top", (cursorY + 515) + "px");
	}

	function mouseover() {
		div.transition()
		.duration(300)
		.style("opacity", 1);
	}

	function mouseout() {
		div.transition()
		.duration(300)
		.style("opacity", 1e-6);
	}

	function mouseclick() {
		if(flag == true){
			// Do Nothing
			//QueryforAllMetadataSIMILARITY(paraStruct, realID)
		} else{
			//QueryforAllMetadataSIMILARITY(paraStruct, nodeID.id.replace("_heatmap",""));
		}
	};
}


