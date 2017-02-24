/**
 * 
 */
function showSimilarityLegend(maxV, minV) {
	var presetMax = 160;
	var svgWidth = 550,
	svgHeight = 100,
	x1 = 50,
	barWidth = 450,
	y1 = 50,
	barHeight = 20,
	numberHues = presetMax;

	if(document.getElementById("legendfirstwin")!= null){
		var idGradient = "legendGradient";
	}


	// Append New Legend Svg
	var svgForLegendStuff = d3.select("#LegendDIV").append("svg")
	.attr("align", "center")
	.attr("width", svgWidth)
	.attr("height", svgHeight);


	//create the empty gradient that we're going to populate later
	svgForLegendStuff.append("g")
	.append("defs")
	.append("linearGradient")
	.attr("id", idGradient)
	.attr("x1", "0%")
	.attr("x2", "100%")
	.attr("y1", "0%")
	.attr("y2", "0%"); // x1=0, x2=100%, y1=y2 results in a horizontal gradient
	// it would have been vertical if x1=x2, y1=0, y2=100%
	// See
	//      http://www.w3.org/TR/SVG/pservers.html#LinearGradients
	// for more details and fancier things you can do
	//create the bar for the legend to go into
	// the "fill" attribute hooks the gradient up to this rect
	svgForLegendStuff.append("rect")
	.attr("align", "center")
	.attr("fill", "url(#" + idGradient + ")")
	.attr("x", x1)
	.attr("y", y1)
	.attr("width", barWidth)
	.attr("height", barHeight)
	.attr("rx", 20)  //rounded corners, of course!
	.attr("ry", 20);


	//add text on either side of the bar

	var textY = y1 + barHeight / 2 + 15;
	var presetMin = 0;
	// document.getElementById("minlocal").value = BigInteger.toJSValue(BigInteger(Minvalue));
	// document.getElementById("maxlocal").value = BigInteger.toJSValue(BigInteger(MaxValue));
	//we go from a somewhat transparent blue/green (hue = 160ยบ, opacity = 0.3) to a fully opaque reddish (hue = 0ยบ, opacity = 1)
	var hueStart = presetMax, hueEnd = presetMin;
	var opacityStart = 0.3, opacityEnd = 1.0;
	var theHue, rgbString, opacity, p;

	var deltaPercent = 1 / (numberHues - 1);
	var deltaHue = (hueEnd - hueStart) / (numberHues - 1);
	var deltaOpacity = (opacityEnd - opacityStart) / (numberHues - 1);

	//kind of out of order, but set up the data here 
	var theData = [];
	var rainbowColor = [];
	for (var i = 0; i < numberHues; i++) {
		theHue = hueStart + deltaHue * i;
		//the second parameter, set to 1 here, is the saturation
		// the third parameter is "lightness"    
		rgbString = d3.hsl(theHue, 1, 0.6).toString();
		opacity = opacityStart + deltaOpacity * i;
		p = 0 + deltaPercent * i;
		theData.push({"rgb": rgbString, "opacity": opacity, "percent": p});
		rainbowColor.push(rgbString);

	}
	//now the d3 magic (imo) ...	
	var stops = d3.select('#' + idGradient).selectAll('stop')
	.data(theData);

	stops.enter().append('stop');
	stops.attr('offset', function (d) {
		return d.percent;
	})
	.attr('stop-color', function (d) {
		return d.rgb;
	})
	.attr('stop-opacity', function (d) {
		return d.opacity;
	});
	return rainbowColor;

}
//function similarityHeatmap("#queryheatmap", "json content", Minvalue, MaxValue) {
function similarityHeatmap(nodeID, content, Minvalue, MaxValue) {
	
	$(nodeID).empty();
	
	var maxValue = Minvalue * 1000;
	var minValue = MaxValue * 1000;

	var dataset = [];
	var keyList = Object.keys(content.Data[0].dps);
	var numTimePoints = keyList.length;

	var numDataPoints = content.Data.length;

	var cellw = parseInt(Math.ceil(screen.width * 0.70) / numTimePoints) + 1;
	var cellh = 6;

	var padding = 0;

	var w = Math.ceil(screen.width * 0.70);
	var h = (cellh * numDataPoints) + padding;

	var schakels = new Array();

	var start = new Date(keyList[0] * 1 + (offset));
	var end = new Date(keyList[0] * 1 + (offset));


	var variateLabels = new Array();
	for (var i = 0; i < numDataPoints; i++) {
			variateLabels.push(content.Data[i].zone);
	}
	var len = variateLabels.length;
	var indices = new Array(len);
	for (var i = 0; i < len; ++i) {
		indices[i] = i;
	}
	indices.sort(function (a, b) {
		return variateLabels[a] < variateLabels[b] ? -1 : variateLabels[a] > variateLabels[b] ? 1 : 0;
	});

	var date;
	var variateID;
	for (var i = 0; i < numDataPoints; i++) {
		variateID = indices[i];
		var schakel_id = content.Data[variateID].zone;

		var row = new Array()

		for (var j = 0; j < numTimePoints; j++) {
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
	.domain(pivots)						//Xilun Color
	.range(colorScheme);
	//.range('#000000','#080808','#101010','#181818','#202020','#282828','#303030');

	var svg = d3.select(nodeID)
	//.append('svg')
	.insert('svg', ':first-child')
	.attr('width', w)
	.attr('height', h)
	.attr('class', 'dataHeatmap');

	/*d3.select(nodeID)
                    .insert("input", ':first-child')
                    .attr("type", "button")
                    .attr("class", "Collapsebtn")
                    .attr("value", "Download Meta")
                    .attr("onclick", "QueryDownload(\"" + nodeID + "\");");
	 */
	if(System =="mongodb"){
		minValue = minValue/1000;
		maxValue = maxValue/1000;
	}

	/*d3.select(nodeID)
                    .insert("input", ":first-child")
                    .attr("type", "button")
                    .attr("class", "Collapsebtn")
                    .attr("onclick", "changeColorLegendMainWin(" + minValue  + "," + maxValue  + ")")//minValue / 1000 + "," + maxValue / 1000 + ")")
                    .attr("value", "Match Range");
	 */
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
	.style("opacity", 1e-6);
	/*Appended for maxMin*/
	var hiddenDIVId = "Query" + "_Index";
	hiddenDIVId = hiddenDIVId.replace('#', '');
	var hiddenDIV = d3.select(nodeID)
	.append("div")
	.attr("id", hiddenDIVId)
	.attr("max", maxValue)// / 1000)
	.attr("min", minValue)// / 1000)
	.attr("value", content.Meta.Index);

	function mousemove(d) {
		var locD2 = d[2];
		if(System=="mongodb"){
			locD2 = d[2]/1000;
		}
		div.text("Info about " + d[0] + " : " + d[1] + " : " +locD2)//+ d[2] / 1000)
		.style("left", (d3.event.pageX ) + "px")
		.style("top", (d3.event.pageY) + "px");
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
		// QueryforAllMetadata(paraStruct, nodeID);
	}
}

function heatmap(nodeID, content) {
	//console.log(Minvalue+"   "+MaxValue);
//	it does not create a new pivoting...
	var maxValue = Minvalue * 1000;
	var minValue = MaxValue * 1000;

	var dataset = [];
	var keyList = Object.keys(content.Data[0].dps);
	var numTimePoints = keyList.length;

	var numDataPoints = content.Data.length;

	var cellw = parseInt(Math.ceil(screen.width * 0.70) / numTimePoints) + 1;
	var cellh = 6;

	var padding = 0;

	var w = Math.ceil(screen.width * 0.70);
	var h = (cellh * numDataPoints) + padding;

	var schakels = new Array();

	var start = new Date(keyList[0] * 1 + (offset));
	var end = new Date(keyList[0] * 1 + (offset));

	var variateLabels = new Array();
	for (var i = 0; i < numDataPoints; i++) {
		variateLabels.push(content.Data[i].zone);
	}
	StateofInterest= variateLabels;

	var len = variateLabels.length;
	var indices = new Array(len);
	for (var i = 0; i < len; ++i) {
		indices[i] = i;
	}
	indices.sort(function (a, b) {
		return variateLabels[a] < variateLabels[b] ? -1 : variateLabels[a] > variateLabels[b] ? 1 : 0;
	});

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
	var granularity = getGranularity(timeDiff);
	//granularity is converted from years to milliseconds, sets the appropriate unit to show
	var convertedGranularity = convertGranularity("years", granularity);

	/**
	 * The code below needs to be reconsidered -- Level: critical
	 * */
	var heatmapTypeAttr = $(nodeID).attr('heatmaptype');
	//this is the problem the heatmap are printed on theyre scale
	if ((Minvalue==-1) &&(typeof heatmapTypeAttr !== typeof undefined && heatmapTypeAttr !== false) ){
		// Run the below line of code to check is the heatmap being plot is for similarity hierarchy.
		if (heatmapTypeAttr == 'SH'){
			colorScheme = showSimilarityLegend(maxValue, minValue);
			buckets = colorScheme.length;
			deltaHue = (maxValue  - minValue ) / (buckets - 1);
			pivots = Array();
			for (var i = 0; i < buckets; i++) {
				pivots[i] = BigInteger.toJSValue(BigInteger(minValue ).add(deltaHue * i));
			}
			Minvalue=minValue;
			MaxValue=maxValue;
			document.getElementById("minlocal").value = BigInteger.toJSValue(BigInteger(Minvalue));
			document.getElementById("maxlocal").value = BigInteger.toJSValue(BigInteger(MaxValue));
		}
	}
	var cntnt = document.getElementById('LegendDIV');
	//console.log('I am Here : ' + cntnt.childNodes.length);
	while (cntnt.childNodes.length > 1) {
	    cntnt.removeChild(cntnt.lastChild);
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
	.domain(pivots)						//Xilun Color
	.range(colorScheme);
	//.range('#000000','#080808','#101010','#181818','#202020','#282828','#303030');

		minValue = minValue/1000;
		maxValue = maxValue/1000;

	var svg = d3.select(nodeID)
	.insert('svg', ':first-child')
	.attr('width', w)
	.attr('height', h)
	.attr('class', 'dataHeatmap');

	d3.select(nodeID)
	.insert("input", ':first-child')
	.attr("type", "button")
	//.attr("class", "Collapsebtn")
	//.attr("class", "hierarchyBtn")//"changeColorLegendMainWin("
	.attr("style","color:orange;border-style:none;background-color: white;cursor:pointer;")
	.attr("onMouseOver","this.style.color='firebrick';")
	.attr("onMouseOut","this.style.color='orange';")
	.attr("value", "Download Data")
	.attr("onclick","QueryDownloadData(\"" + nodeID + "\");");// "QueryDownload(\"" + nodeID + "\");");

	d3.select(nodeID)
	.insert("input", ':first-child')
	.attr("type", "button")
	//.attr("class", "Collapsebtn")
	//.attr("class", "hierarchyBtn")//"changeColorLegendMainWin("
	.attr("style","color:orange;border-style:none;background-color: white;cursor:pointer;")
	.attr("onMouseOver","this.style.color='firebrick';")
	.attr("onMouseOut","this.style.color='orange';")
	.attr("value", "Download Meta")
	.attr("onclick", "QueryDownload(\"" + nodeID + "\");");

	var rangefunc= "changeColorLegendMainWin(";
	//if(typeofsearch!="MetadataQuery"){
	if($("input[query=similarity]").is(':checked')){
		rangefunc= "changeColorLegendMainWinSimilarity(";
	}
	d3.select(nodeID)
	.insert("input", ":first-child")
	.attr("type", "button")
	//.attr("class", "Collapsebtn")
	//.attr("class", "hierarchyBtn")//"changeColorLegendMainWin("
	.attr("style","color:orange;border-style:none;background-color: white;cursor:pointer;")
	.attr("onMouseOver","this.style.color='firebrick';")
	.attr("onMouseOut","this.style.color='orange';")
	.attr("onclick", rangefunc + minValue  + "," + maxValue  + ")")//minValue / 1000 + "," + maxValue / 1000 + ")")
	.attr("value", "Match Range");

	d3.select(nodeID)
	.insert("input", ":first-child")
	.attr("type", "button")
	//.attr("class", "Collapsebtn")
	//.attr("class", "hierarchyBtn")//"changeColorLegendMainWin("
	.attr("style","color:orange;border-style:none;background-color: white;cursor:pointer;")
	.attr("onMouseOver","this.style.color='firebrick';")
	.attr("onMouseOut","this.style.color='orange';")
	//.attr("onclick","refreshpage(\"similarityqueryid\",\""+nodeID+"\",\""+ variateLabels +"\")")
	.attr("onclick","migratePageContent(\"" + nodeID + "\",\"" + variateLabels + "\")")
	.attr("value", "Search Similar");

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
	.style("opacity", 1e-6);

	/*Appending Granularity DIV*/
	var gdiv = d3.select(nodeID)
	.insert("span", ":first-child")
	.html("Resolution : " + convertedGranularity); 

	/*Appended for maxMin*/
	var hiddenDIVId = nodeID + "_Index";
	hiddenDIVId = hiddenDIVId.replace('#', '');
	var hiddenDIV = d3.select(nodeID)
	.append("div")
	.attr("id", hiddenDIVId)
	.attr("max", maxValue)// / 1000)
	.attr("min", minValue)// / 1000)
	.attr("value", content.Meta.Index);
	function mousemove(d) {
		//var locD2 = d[2];
		var locD2 = d[2]/1000;
		/*if(System=="mongodb"){
			locD2 = d[2]/1000;
		}*/
		/*
		div.text("Info about " + d[0] + " : " + d[1] + " : " +locD2)//+ d[2] / 1000)
		.style("left", ((d3.event.pageX/10) + 100) + "px")
		.style("top", ((d3.event.pageY/10) - 20) + "px");
		*/
		div.text("Info about " + d[0] + " : " + d[1] + " : " +locD2)//+ d[2] / 1000)
		.style("left", (cursorX) + "px")
		.style("top", (cursorY) + "px");
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

		var heatmapTypeAttr = $(nodeID).attr('heatmaptype');

		if (typeof heatmapTypeAttr !== typeof undefined && heatmapTypeAttr !== false ){
			// Run the below line of code to check is the heatmap being plot is for similarity hierarchy.
			if (heatmapTypeAttr == 'SH'){
				QueryforAllMetadataSIMILARITY(paraStruct, nodeID);
			}
		} else {
			QueryforAllMetadata(paraStruct, nodeID);
		}
	};
}



function heatmapnoGranularity(nodeID, content) {

	var maxValue = Minvalue * 1000;
	var minValue = MaxValue * 1000;


	var dataset = [];
	var keyList = Object.keys(content.Data[0].dps);
	var numTimePoints = keyList.length;

	var numDataPoints = content.Data.length;

	var cellw = parseInt(Math.ceil(screen.width * 0.70) / numTimePoints) + 1;
	var cellh = 6;

	var padding = 0;

	var w = Math.ceil(screen.width * 0.70);
	var h = (cellh * numDataPoints) + padding;

	var schakels = new Array();

	var start = new Date(keyList[0] * 1 + (offset));
	var end = new Date(keyList[0] * 1 + (offset));

	var variateLabels = new Array();
	for (var i = 0; i < numDataPoints; i++) {
			variateLabels.push(content.Data[i].zone);
	}
	var len = variateLabels.length;
	var indices = new Array(len);
	for (var i = 0; i < len; ++i) {
		indices[i] = i;
	}
	indices.sort(function (a, b) {
		return variateLabels[a] < variateLabels[b] ? -1 : variateLabels[a] > variateLabels[b] ? 1 : 0;
	});

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
	var granularity = getGranularity(timeDiff);
	var convertedGranularity = convertGranularity("", granularity);

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
	.domain(pivots)						//Xilun Color
	.range(colorScheme);
	//.range('#000000','#080808','#101010','#181818','#202020','#282828','#303030');

	var svg = d3.select(nodeID)
	//.append('svg')
	.insert('svg', ':first-child')
	.attr('width', w)
	.attr('height', h)
	.attr('class', 'dataHeatmap');

	d3.select(nodeID)
	.insert("input", ':first-child')
	.attr("type", "button")
	.attr("class", "Collapsebtn")
	.attr("value", "Download Meta")
	.attr("onclick", "QueryDownload(\"" + nodeID + "\");");

	if(System =="mongodb"){
		minValue = minValue/1000;
		maxValue = maxValue/1000;
	}

	d3.select(nodeID)
	.insert("input", ":first-child")
	.attr("type", "button")
	.attr("class", "Collapsebtn")
	.attr("onclick", "changeColorLegendMainWin(" + minValue  + "," + maxValue  + ")")//minValue / 1000 + "," + maxValue / 1000 + ")")
	.attr("value", "Match Range");
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
	.style("opacity", 1e-6);

	/*Appending Granularity DIV
            if(noGranularity!=true){
            	console.log("inside appending granularity");
            var gdiv = d3.select(nodeID)
            			 .insert("span", ":first-child")
            			 .html("[Granularity : " + granularity + " ms]"); 
            }*/
	/*Appended for maxMin*/
	var hiddenDIVId = nodeID + "_Index";
	hiddenDIVId = hiddenDIVId.replace('#', '');
	var hiddenDIV = d3.select(nodeID)
	.append("div")
	.attr("id", hiddenDIVId)
	.attr("max", maxValue)// / 1000)
	.attr("min", minValue)// / 1000)
	.attr("value", content.Meta.Index);
	function mousemove(d) {
		var locD2 = d[2];
		if(System=="mongodb"){
			locD2 = d[2]/1000;
		}
		div.text("Info about " + d[0] + " : " + d[1] + " : " +locD2)//+ d[2] / 1000)
		.style("left", (d3.event.pageX ) + "px")
		.style("top", (d3.event.pageY) + "px");
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
		QueryforAllMetadata(paraStruct, nodeID);
	};
}
