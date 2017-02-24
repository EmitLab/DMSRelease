function changeColorLegend() {
	Minvalue = document.getElementById("minlocal").value;
	MaxValue = document.getElementById("maxlocal").value;
	//document.getElementById("LegendDIV").innerHTML="";
	reprintall();
	//showLegend(MaxValue, Minvalue);	
}
//changecolorbuttonfunction
function changeColorLegendMainWin(minval, maxval) {

	document.getElementById("minlocal").value = BigInteger.toJSValue(BigInteger(minval));
	document.getElementById("maxlocal").value = BigInteger.toJSValue(BigInteger(maxval) + 1);
	Minvalue = document.getElementById("minlocal").value;
	MaxValue = document.getElementById("maxlocal").value;
	//document.getElementById("LegendDIV").innerHTML="";
	reprintall();
	//showLegend(MaxValue, Minvalue);	
}


function changecolorinwindows(id, resultindex) {

	var min = BigInteger(win.document.getElementById(id + "_Index").getAttribute("min"));
	var max = BigInteger(win.document.getElementById(id + "_Index").getAttribute("max")) + 1;
	win.document.getElementById("minlocal").value = BigInteger.toJSValue(min);
	win.document.getElementById("maxlocal").value = BigInteger.toJSValue(BigInteger(max));
	Changecolorwin();
}


function Changecolorwin() {

	console.log("in changecolorwin2");
	/*colorlegend*/
	numberHues = presetMax;
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
		//console.log(rainbowColor);
	}

	colorSchemeNW = rainbowColor;

	var listsvg = new Array();
	listsvg = win.document.getElementsByTagName("svg");

	//console.log("svgsize: " + listsvg.length);
	for (var i = 1; i < listsvg.length; i++) {
		//  console.log("Remove: " + listsvg[i].id);
		if (listsvg[i].id == "") {
			//    console.log("found the grayscale");
			var node = listsvg[i].parentNode;
			node.removeChild(listsvg[i]);
			i = i - 1;
		}
	}

	// console.log("svgsize: " + listsvg.length);
	for (var i = 1; i < listsvg.length; i++) {

		var idlocalnewW = listsvg[1].id;
		var idlocheatmap = idlocalnewW.replace("#", "");


		var hideddiv = win.document.getElementById(idlocalnewW + "_Index").cloneNode(true);
		//call the function  as enlargeGraph with one ore attribute to avoid the clone from the 

		//change here
		var content = Datain2win[(win.document.getElementById(idlocalnewW + "_Index").getAttribute("value"))];//("querynum",NumQuery-1);

		var metadatalocal = win.document.getElementById(idlocalnewW + "_Title1").textContent;

		/* Prepare Data for heatmap Printing */
		var dataset = [];
		var keyList ;
		//if( content.Data == "undefined"){//content == null || content.lenth==0){
		//	keyList = Object.keys(SimilarityDataRetrived.Data[0].dps);
		//}else{
		keyList = Object.keys(content.Data[0].dps);
		// }
		//var keyList = Object.keys(content.Data[0].dps);
		var numTimePoints = keyList.length;

		var numDataPoints = content.Data.length;
		var cellw = 3;
		var cellh = 6;

		var padding = 0;
		var w = cellw * numTimePoints;
		var h = (cellh * numDataPoints) + padding;

		var schakels = new Array();

		var start;
		var end ;
		if(System =="mongodb"){
			start = new Date(keyList[0] * 1 + offset);//*1000);
			end = new Date(keyList[0] * 1 + offset);//*1000);
		}else if(System =="opentsdb"){
			start = new Date(keyList[0] * 1000);// + offset);//*1000);
			end = new Date(keyList[0] * 1000);
		}
		var variateLabels = new Array();

		for (var j = 0; j < numDataPoints; j++) {
			if(System =="mongodb"){
				variateLabels.push(content.Data[j].zone);
			}else if(System =="opentsdb"){
				if(Project =="epidemic"){
					variateLabels.push(content.Data[i].tags.state);
				}else if(Project =="energy"){
					variateLabels.push(content.Data[i].tags.zone);
				}
			}
		}

		var len = variateLabels.length;
		var indices = new Array(len);
		for (var j = 0; j < len; ++j) {
			indices[j] = j;
		}
		indices.sort(function (a, b) {
			return variateLabels[a] < variateLabels[b] ? -1 : variateLabels[a] > variateLabels[b] ? 1 : 0;
		});

		var variateID;
		for (var j = 0; j < numDataPoints; j++) {
			variateID = indices[j];
			var schakel_id;
			if(System =="mongodb"){
				schakel_id = content.Data[variateID].zone;
			}else if(System =="opentsdb"){
				if(Project =="epidemic"){
					schakel_id = content.Data[variateID].tags.state;
				}else if(Project =="energy"){
					schakel_id = content.Data[variateID].tags.zone;
				}

			}
			var row = new Array()

			for (var k = 0; k < numTimePoints; k++) {
				var speed;
				var date;
				if(System =="mongodb"){
					speed = content.Data[variateID].dps[keyList[k]] * 1000;
					date = new Date(keyList[k] * 1 + offset);
				}else if(System =="opentsdb"){
					speed = content.Data[variateID].dps[keyList[k]];
					date = new Date(keyList[k] * 1000 );
				}
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

		var paraStruct = new Array();
		paraStruct['numTimePoints'] = numTimePoints;
		paraStruct['numDataPoints'] = numDataPoints;
		paraStruct['dataset'] = dataset;
		paraStruct['schakels'] = schakels;
		paraStruct['start'] = start;
		paraStruct['end'] = end;

		//rimozione della heatmap dalla finestra secondaria.
		removeElement(idlocalnewW, false);

		//chiamata di extendedheatmap modificata
		enlargeGraph1(paraStruct, idlocalnewW, metadatalocal, hideddiv);
	}
	HighlightDifferences();
	highlight();
}


function reprintall() {

	var JSONDataObject = JSON.parse(result);
	if (colorScheme == null) {
		colorScheme = showLegend(MaxValue, Minvalue);
	} else {
		document.getElementById("minlocal").value = BigInteger.toJSValue(BigInteger(Minvalue));
		document.getElementById("maxlocal").value = BigInteger.toJSValue(BigInteger(MaxValue));
	}
	var buckets = colorScheme.length;
	var deltaHue =  (MaxValue * 1000 - Minvalue * 1000) / (buckets - 1);
	for (var i = 0; i < buckets; i++) {
		pivots[i] = BigInteger.toJSValue(BigInteger(Minvalue * 1000).add(deltaHue * i));
	}
	var listSVG = new Array();

	listSVG = document.getElementsByClassName("dataHeatmap");
	var l = listSVG.length;

	for (var i = 0; i < l; i++) {

		var node = listSVG[i].parentNode;
		node.removeChild(listSVG[i]);
		var button = node.getElementsByTagName("input");
		for (var j = 0; j < 4; j++) {  //SICONG here i correct from 3 to 4
			node.removeChild(button[0]);
		}

		//$('#' + node.id).remove('input');
		var granularity = node.getElementsByTagName("span");
		node.removeChild(granularity[0]);
		//node.removeChild(granularity[1]);
		//$('#' + node.id).remove('span');

		var heatmapTypeAttr = $('#' + node.id).attr('heatmaptype');
		if (typeof heatmapTypeAttr !== typeof undefined && heatmapTypeAttr !== false){
			if (heatmapTypeAttr == 'SH'){
				heatmap("#" + node.id, JSON.parse($('#' + node.id).attr('heatmapdata')));
			}
		} else {
			heatmap("#" + node.id, JSONDataObject[document.getElementById(node.id + "_Index").getAttribute("value")]);
		}
		$("#" + node.id).prepend($("#" + node.id + "_Meta"));
		//heatmap("#" + node.id, JSONDataObject[document.getElementById(node.id + "_Index").getAttribute("value")]);// listSVG[i]);//innerHTML = heatmap(nodeID, content);
	}
}
