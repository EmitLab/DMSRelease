/**
 * 
 */
function nextDate(start, step) {
	return new Date(start.getTime() + step)
}

function grayscaleHeatmap2(nodeID, content, initMin, initMax, grayScaleFlags) {

	var multiplicationFactor = 10000;
	// Defining lower and upper bounds for min/max normalization
	/*if (grayScaleFlags[2] == 4) {
                var minValue = initMin * multiplicationFactor;
                var maxValue = initMax * multiplicationFactor;
            } else {*/
	var minValue = 0;
	var maxValue = 1 * multiplicationFactor;

	var keyList = Object.keys(content.Data[0].dps);
	var numTimePoints = keyList.length;

	var numDataPoints = content.Data.length;

	var cellw = parseInt(Math.ceil(screen.width * 0.70) / numTimePoints) + 1;
	var cellh = 3;

	var padding = 0;
	var w = Math.ceil(screen.width * 0.70);
	var h = (cellh * numDataPoints) + padding;

	var schakels = new Array();
	var start;
	var end; 
	start = new Date(keyList[0] * 1 + (offset));
	end = new Date(keyList[0] * 1 + (offset));
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
	var dataset = [];

	if (grayScaleFlags[2] == 2) {
		// Simulation Level Normalization
		for (var i = 0; i < numDataPoints; i++) {
			variateID = indices[i];

			var jsonArrObj = content.Data[variateID].dps;
			var arr = Object.keys(jsonArrObj).map(function (key) {
				return jsonArrObj[key];
			});
			if (i == 0) {
				initMin = Math.min.apply(null, arr);
				initMax = Math.max.apply(null, arr);
			} else {
				if (initMin > Math.min.apply(null, arr)) {
					initMin = Math.min.apply(null, arr);
				}
				if (initMax < Math.max.apply(null, arr)) {
					initMax = Math.max.apply(null, arr);
				}
			}
		}
	}

	for (var i = 0; i < numDataPoints; i++) {
		variateID = indices[i];
		var schakel_id = content.Data[variateID].zone;
		if (grayScaleFlags[2] == 3) {
			// Variate Level Normalization
			var jsonArrObj = content.Data[variateID].dps;
			var arr = Object.keys(jsonArrObj).map(function (key) {
				return jsonArrObj[key];
			});
			initMin = Math.min.apply(null, arr);
			initMax = Math.max.apply(null, arr);
		}
		var row = new Array()

		for (var j = 0; j < numTimePoints; j++) {
			var speed = 0.0;
			if (grayScaleFlags[2] == 1) {
				//Hierarhically Normalized Values
				speed = Math.abs((parseFloat(content.Data[variateID].dps[keyList[j]]) - initMin) / (initMax - initMin)) * multiplicationFactor;
			} else {
				/*if (grayScaleFlags[2] == 2 || grayScaleFlags[2] == 3) {*/
				// Simulation/ Variate based Normalization
				speed = Math.abs((parseFloat(content.Data[variateID].dps[keyList[j]]) - initMin) / (initMax - initMin)) * multiplicationFactor;
				/*} else {
                            //Non-Normalized Values
                        	speed = Math.abs(parseFloat(content.Data[variateID].dps[keyList[j]]) * multiplicationFactor);
                    	}*/
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
	var pivotsGray = new Array();
	var bucketsGray = 255;
	var deltaHueGray = (maxValue - minValue) / (bucketsGray - 1);
	for (var i = 0; i < bucketsGray; i++) {
		pivotsGray[i] = minValue + deltaHueGray * i;
	}
	var xscale = d3.time.scale()
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
	.domain(pivotsGray)
	.range(grayColorScheme);

	var svg = d3.select(nodeID)
	.append('svg')
	.attr('style', 'border:1px #A3C2FF solid')//#A3C2FF
	.attr('width', w)
	.attr('height', h)//;
	.attr('class', 'grayScaleHeatmap');//Added  for Yash code

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
	.attr("class", "graytooltip")
	.style("opacity", 1e-6);
	/*Appended for maxMin*/
	var hiddenDIVId = nodeID + "_Index";
	hiddenDIVId = hiddenDIVId.replace('#', '');
	var hiddenDIV = d3.select(nodeID)
	.append("div")
	.attr("id", hiddenDIVId)
	.attr("max", maxValue)
	.attr("min", minValue)
	.attr("value", content.Meta.Index);


	function mousemove(d) {
		div.text("Info about " + d[0] + " : " + d[1] + " : " + d[2] / multiplicationFactor)
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
		// DO NOTHING
	}
}

function getGrayScaleData(grayScaleIndex, grayScaleData, grayScaleFlags) {

	var newGrayScaleData = grayScaleData;
	var dataCount = grayScaleData.length;
	var variateCount = grayScaleData[0].Data.length;
	var keyList = Object.keys(grayScaleData[0].Data[0].dps);
	var timeCount = keyList.length;
	var meanArr = new Array();
	var mean = 0;

	var initMax = -1.7976931348623157E+10308;
	var initMin = 1.7976931348623157E+10308;

	var indices = new Array();

	for (var dataFile = 0; dataFile < dataCount; dataFile++) {
		indices[dataFile] = new Array();
		var variateLabels = new Array();
		for (var i = 0; i < variateCount; i++) {
			variateLabels.push(grayScaleData[dataFile].Data[i].zone);
		}
		var len = variateLabels.length;
		// var indices = new Array(len);
		for (var i = 0; i < len; ++i) {
			indices[dataFile][i] = i;
		}
		indices[dataFile].sort(function (a, b) {
			return variateLabels[a] < variateLabels[b] ? -1 : variateLabels[a] > variateLabels[b] ? 1 : 0;
		});
	}

	for (var variate = 0; variate < variateCount; variate++) {
		meanArr[variate] = new Array();

		for (var time = 0; time < timeCount; time++) {

			for (var dataFile = 0; dataFile < dataCount; dataFile++) {
				mean = mean + Math.abs(parseFloat(grayScaleData[dataFile].Data[indices[dataFile][variate]].dps[keyList[time]]));
			}
			meanArr[variate][time] = mean / dataCount;

			for (var dataFile = 0; dataFile < dataCount; dataFile++) {
				var temp = Math.abs(parseFloat(grayScaleData[dataFile].Data[indices[dataFile][variate]].dps[keyList[time]]) - meanArr[variate][time]);
				if (grayScaleFlags[3] == 1) {
					// Percentile Shift
					if (meanArr[variate][time] != 0)
						temp = temp / meanArr[variate][time];
					else 
						temp = 0;
				}
				if (temp < initMin) {
					initMin = temp;
				}

				if (temp > initMax) {
					initMax = temp;
				}

				newGrayScaleData[dataFile].Data[indices[dataFile][variate]].dps[keyList[time]] = temp.toString();
			}

			mean = 0;
		}
	}
	for (var dataFile = 0; dataFile < dataCount; dataFile++) {
		grayscaleHeatmap2("#" + grayScaleIndex[dataFile], newGrayScaleData[dataFile], initMin, initMax, grayScaleFlags);
	}
}


function removegrayscalewin2(){
	var listsvg = win.document.getElementsByTagName("svg");
	var SVGData = new Array();
	var nodeIDs = new Array();
	var grayScaleIndex = new Array();
	var JSONDataObject = JSON.parse(result);
	//console.log(JSONDataObject);


	for (var i = 1; i < listsvg.length; i++) {

		nodeIDs[i - 1] = listsvg[i].id;
		var localid = nodeIDs[i - 1];
		var id = nodeIDs[i - 1].substring(1);
		if (id != null && id != "") {
			SVGData[i - 1] = JSON.parse(JSON.stringify(Datain2win[(win.document.getElementById(localid + "_Index").getAttribute("value"))]));//("querynum",NumQuery-1);
			grayScaleIndex[i - 1] = id;
		}
	}

	clearGreyScaleDIV(nodeIDs);
}

function greyScaleCompareSVG(chosing) {

	var listsvg = win.document.getElementsByTagName("svg");
	var SVGData = new Array();
	var nodeIDs = new Array();
	var grayScaleIndex = new Array();
	var JSONDataObject = JSON.parse(result);
	//console.log(JSONDataObject);


	for (var i = 1; i < listsvg.length; i++) {

		nodeIDs[i - 1] = listsvg[i].id;
		var localid = nodeIDs[i - 1];
		var id = nodeIDs[i - 1].substring(1);
		console.log(id + "_Index");
		if (id != null && id != "") {
			SVGData[i - 1] = JSON.parse(JSON.stringify(Datain2win[(win.document.getElementById(localid + "_Index").getAttribute("value"))]));//("querynum",NumQuery-1);
			//SVGData[i - 1] = JSONDataObject[document.getElementById(id + "_Index").getAttribute("value")];
			grayScaleIndex[i - 1] = id;
		}
	}

	clearGreyScaleDIV(nodeIDs);
	if(chosing==0){
		// Clear previous greyScaleDIV
		var grayScaleData = SVGData;
		var newGrayScaleData = grayScaleData;

		var dataCount = grayScaleData.length;
		var variateCount = grayScaleData[0].Data.length;
		var keyList = Object.keys(grayScaleData[0].Data[0].dps);
		var timeCount = keyList.length;
		var meanArr = new Array();
		var mean = 0;

		var initMax = -1.7976931348623157E+10308;
		var initMin = 1.7976931348623157E+10308;

		var indices = new Array();

		for (var dataFile = 0; dataFile < dataCount; dataFile++) {
			indices[dataFile] = new Array();
			var variateLabels = new Array();
			for (var i = 0; i < variateCount; i++) {
				if(System =="mongodb"){
					variateLabels.push(grayScaleData[dataFile].Data[i].zone);
				}else if(System =="opentsdb"){
					if(Project == "epidemic"){
						variateLabels.push(grayScaleData[dataFile].Data[i].tags.state);
					}else if(Project =="energy"){
						variateLabels.push(grayScaleData[dataFile].Data[i].tags.zone);
					}
				}
			}
			var len = variateLabels.length;
			// var indices = new Array(len);
			for (var i = 0; i < len; ++i) {
				indices[dataFile][i] = i;
			}
			indices[dataFile].sort(function (a, b) {
				return variateLabels[a] < variateLabels[b] ? -1 : variateLabels[a] > variateLabels[b] ? 1 : 0;
			});
		}

		var grayScaleComputationMenu = win.document.getElementById("grayScaleTypeMenu");//"grayScaleComputationMenu_Win2");
		var CT = grayScaleComputationMenu.options[grayScaleComputationMenu.selectedIndex].getAttribute("key");
		var choiceCT = parseInt(CT);
		var grayScaleDifferenceMenu = win.document.getElementById("grayScaleDifferenceMenu");//_Win2");
		var DT = grayScaleDifferenceMenu.options[grayScaleDifferenceMenu.selectedIndex].getAttribute("key");
		var choiceDT = parseInt(DT);


		for (var variate = 0; variate < variateCount; variate++) {
			meanArr[variate] = new Array();

			for (var time = 0; time < timeCount; time++) {

				for (var dataFile = 0; dataFile < dataCount; dataFile++) {
					if (choiceCT == 1) {
						mean = mean + Math.abs(parseFloat(grayScaleData[dataFile].Data[indices[dataFile][variate]].dps[keyList[time]]));
					} else {
						mean = mean + parseFloat(grayScaleData[dataFile].Data[indices[dataFile][variate]].dps[keyList[time]]);
					}
				}
				meanArr[variate][time] = mean / dataCount;

				for (var dataFile = 0; dataFile < dataCount; dataFile++) {

					if (choiceCT == 1) {
						var temp = Math.abs(parseFloat(grayScaleData[dataFile].Data[indices[dataFile][variate]].dps[keyList[time]]) - meanArr[variate][time]);
					} else {
						var temp = parseFloat(grayScaleData[dataFile].Data[indices[dataFile][variate]].dps[keyList[time]]) - meanArr[variate][time];
					}
					if (choiceDT == 1) {
						// Percentile Shift
						if (meanArr[variate][time] != 0)
							temp = temp / meanArr[variate][time];
						else 
							temp = 0;
					}
					if (temp < initMin) {
						initMin = temp;
					}

					if (temp > initMax) {
						initMax = temp;
					}

					newGrayScaleData[dataFile].Data[indices[dataFile][variate]].dps[keyList[time]] = temp.toString();
				}

				mean = 0;
			}
		}
		for (var dataFile = 0; dataFile < dataCount; dataFile++) {
			printGreyScaleWin2("#" + grayScaleIndex[dataFile], newGrayScaleData[dataFile], initMin, initMax);
		}
	}
	else if(chosing==1){
		featureGrayscalewindow2(grayScaleIndex,SVGData);
		alert("Print feature grayscale windows 2");}
}


function clearGreyScaleDIV(nodeIDs) {
	for (var i = 0; i != nodeIDs.length; i++) {

		if (nodeIDs[i] != "") {
			//console.log(nodeIDs[i] + "_greyscaleid");
			win.document.getElementById(nodeIDs[i] + "_greyscaleid").innerHTML = "";
		}
	}
}


function printGreyScaleWin2(nodeID, content, initMin, initMax) {

	var offset = new Date().getTimezoneOffset() * 60 * 1000;

	//if (SVGData.length <= 1)
	//    alert('At least two simulations are needed for comparison, please add more!');
	//else {

	// Compute greyScale heatmap data
	var grayScaleComputationMenu = win.document.getElementById("grayScaleTypeMenu");//grayScaleComputationMenu_Win2");
	var CT = grayScaleComputationMenu.options[grayScaleComputationMenu.selectedIndex].getAttribute("key");
	var choiceCT = parseInt(CT);
	var grayScaleNomralizationMenu = win.document.getElementById("grayScaleNormalizationMenu");//_Win2");
	var NT = grayScaleNomralizationMenu.options[grayScaleNomralizationMenu.selectedIndex].getAttribute("key");
	var choiceNT = parseInt(NT);
	var grayScaleDifferenceMenu = win.document.getElementById("grayScaleDifferenceMenu");//_Win2");
	var DT = grayScaleDifferenceMenu.options[grayScaleDifferenceMenu.selectedIndex].getAttribute("key");
	var choiceDT = parseInt(DT);

	var multiplicationFactor = 10000;
	// Defining lower and upper bounds for min/max normalization
	if (choiceNT == 4) {
		var minValue = initMin * multiplicationFactor;
		var maxValue = initMax * multiplicationFactor;
	} else {
		var minValue = 0;
		var maxValue = 1 * multiplicationFactor;
	}

	var keyList = Object.keys(content.Data[0].dps);
	var numTimePoints = keyList.length;

	var numDataPoints = content.Data.length;

	var cellh = 20;
	var xpadding = 35;
	var padding = 10 * 10 + xpadding;
	var w = Math.ceil(screen.width * 1) - padding;
	var cellw = parseInt(w / numTimePoints) + 1;
	var h = cellh * numDataPoints + 35;

	var schakels = new Array();

	var start = new Date(keyList[0] * 1 + (offset));
	var end = new Date(keyList[0] * 1 + (offset));
	var variateLabels = new Array();

	for (var i = 0; i < numDataPoints; i++) {
		if(System =="mongodb")
			variateLabels.push(content.Data[i].zone);
		else if(System=="opentsdb"){
			if(Project == "epidemic"){
				variateLabels.push(content.Data[i].tags.state);
			}else if(Project =="energy"){
				variateLabels.push(content.Data[i].tags.zone);
			}
		}
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
	var dataset = [];

	if (choiceNT == 2) {
		// Simulation Level Normalization
		for (var i = 0; i < numDataPoints; i++) {
			variateID = indices[i];

			var jsonArrObj = content.Data[variateID].dps;
			var arr = Object.keys(jsonArrObj).map(function (key) {
				return jsonArrObj[key];
			});
			if (i == 0) {
				initMin = Math.min.apply(null, arr);
				initMax = Math.max.apply(null, arr);
			} else {
				if (initMin > Math.min.apply(null, arr)) {
					initMin = Math.min.apply(null, arr);
				}
				if (initMax < Math.max.apply(null, arr)) {
					initMax = Math.max.apply(null, arr);
				}
			}
		}
	}

	for (var i = 0; i < numDataPoints; i++) {
		variateID = indices[i];
		var schakel_id ;
		if(System =="mongodb"){
			schakel_id = content.Data[variateID].zone;
		}else if(System=="opentsdb"){
			if(Project == "epidemic"){
				schakel_id = content.Data[i].tags.state;
			}else if(Project =="energy"){
				schakel_id = content.Data[i].tags.zone;
			}

		}
		if (choiceNT == 3) {
			// Variate Level Normalization
			var jsonArrObj = content.Data[variateID].dps;
			var arr = Object.keys(jsonArrObj).map(function (key) {
				return jsonArrObj[key];
			});
			initMin = Math.min.apply(null, arr);
			initMax = Math.max.apply(null, arr);
		}
		var row = new Array()

		for (var j = 0; j < numTimePoints; j++) {
			var speed = 0.0;

			if (choiceCT == 1) {
				// Absolute Computation
				if (choiceNT == 1) {
					//Hierarhically Normalized Values
					speed = Math.abs((parseFloat(content.Data[variateID].dps[keyList[j]]) - initMin) / (initMax - initMin)) * multiplicationFactor;
				} else {
					if (choiceNT == 2 || choiceNT == 3) {
						// Simulation/ Variate based Normalization
						speed = Math.abs((parseFloat(content.Data[variateID].dps[keyList[j]]) - initMin) / (initMax - initMin)) * multiplicationFactor;
					} else {
						//Non-Normalized Values
						speed = Math.abs(parseFloat(content.Data[variateID].dps[keyList[j]]) * multiplicationFactor);
					}
				}
			} else {
				//Non - Absolute Computation
				//    if (choiceCT == 1) {
				// Absolute COmputation
				if (choiceNT == 1) {
					//Hierarhically Normalized Values
					speed = parseFloat((parseFloat(content.Data[variateID].dps[keyList[j]]) - initMin) / (initMax - initMin)) * multiplicationFactor;
				} else {
					if (choiceNT == 2 || choiceNT == 3) {
						speed = parseFloat((parseFloat(content.Data[variateID].dps[keyList[j]]) - initMin) / (initMax - initMin)) * multiplicationFactor;
					} else {
						//Non-Normalized Values
						speed = parseFloat(content.Data[variateID].dps[keyList[j]]) * multiplicationFactor;
					}
				}
				//    }
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

	var grayColorScheme = new Array();
	for (var i = 255; i > 0; i--) {
		rgbString = getHexString(i);
		hexString = '#' + rgbString + rgbString + rgbString;
		grayColorScheme.push(hexString);
	}

	var pivotsGray = new Array();
	var bucketsGray = 255;
	var deltaHueGray = (maxValue - minValue) / (bucketsGray - 1);
	for (var i = 0; i < bucketsGray; i++) {
		pivotsGray[i] = 0 + deltaHueGray * i;
	}

	var xscale = d3.time.scale()
	.domain([start, end])
	.range([padding, w]);

	var xAxis = d3.svg.axis()
	.scale(xscale)
	.orient("bottom")
	.ticks(d3.time.days, 25);

	var yscale = d3.scale.ordinal()
	.domain(schakels)
	.rangeBands([0, h - 35]);

	var yAxis = d3.svg.axis()
	.scale(yscale)
	.orient('left');


	var zscale = d3.scale.linear()
	.domain(pivotsGray)
	.range(grayColorScheme);

	var svg_grey = d3.select(win.document.getElementById(nodeID + "_greyscaleid"))
	.append('svg')
	.attr('style', 'border:1px #A3C2FF solid')//#A3C2FF
	.attr('width', w)
	.attr('height', h);

	var row_grey = svg_grey.selectAll('.row')
	.data(dataset)
	.enter()
	.append('svg:g')
	.attr('class', 'row');

	var col_grey = row_grey.selectAll('.cell')
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

	svg_grey.append("g")
	.attr("class", "axis")
	.attr("transform", "translate(0," + (h - padding) + ")")
	.call(xAxis);

	svg_grey.append("g")
	.attr("class", "axis")
	.attr("transform", "translate(" + padding + ",0)")
	.call(yAxis);

	var div_grey = d3.select(win.document.getElementById(nodeID + "_greyscaleid"))
	.append("div")
	.attr("class", "graytooltip")
	.style("opacity", 1e-6);

	/*Appended for maxMin*/
	var hiddenDIVId = nodeID + "_greyscaleid" + "_Index";
	hiddenDIVId = hiddenDIVId.replace('#', '');
	var hiddenDIV = d3.select(nodeID + "_greyscaleid")
	.append("div")
	.attr("id", hiddenDIVId)
	.attr("max", maxValue)
	.attr("min", minValue)
	.attr("value", content.Meta.Index);


	function mousemove(d) {
		div_grey.text("Info about " + d[0] + " : " + d[1] + " : " + d[2] / multiplicationFactor)
		.style("left", (d3.event.pageX ) + "px")
		.style("top", (d3.event.pageY) + "px");
	}

	function mouseover() {
		div_grey.transition()
		.duration(300)
		.style("opacity", 1);
	}

	function mouseout() {
		div_grey.transition()
		.duration(300)
		.style("opacity", 1e-6);
	}

	function mouseclick() {
		//QueryforAllMetadata(paraStruct,nodeID);
		// DO NOTHING
	}
}