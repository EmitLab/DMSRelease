/**
 * 
 */
function showLegend(maxV, minV) {
	var svgWidth = 550,
	svgHeight = 20,
	x1 = 50,
	barWidth = 450,
	y1 = 1,
	barHeight = 20,
	numberHues = presetMax;

	var idGradient = "legendGradient";
	if ($('#LegendDIV').find('#legendfirstwinid').length){
		// Don't Append New Legend SVG
		//console.log('1. ' + $('#LegendDIV').find('#legendfirstwinid').length);
	}else{
		//console.log('2. ' + $('#LegendDIV').find('#legendfirstwinid').length);
		var nodelegend= document.getElementById("legendfirstwinid");
		if(nodelegend!=null){
			var parlegend= nodelegend.parentNode;
			parlegend.removeChild(nodelegend);

		}
		var svgForLegendStuff = d3.select("#LegendDIV").append("svg")
		.attr("align", "center")
		.attr("width", svgWidth)
		.attr("height", svgHeight)
		.attr("id","legendfirstwinid");


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
	}
	//add text on either side of the bar

	var textY = y1 + barHeight / 2 + 15;

	document.getElementById("minlocal").value = BigInteger.toJSValue(BigInteger(Minvalue));
	document.getElementById("maxlocal").value = BigInteger.toJSValue(BigInteger(MaxValue));
	//we go from a somewhat transparent blue/green (hue = 160º, opacity = 0.3) to a fully opaque reddish (hue = 0º, opacity = 1)
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
		//console.log(theHue);
		//the second parameter, set to 1 here, is the saturation
		// the third parameter is "lightness"    
		rgbString = d3.hsl(theHue, 1, 0.6).toString();
		//console.log(rgbString);
		opacity = opacityStart + deltaOpacity * i;
		p = 0 + deltaPercent * i;
		//onsole.log("i, values: " + i + ", " + rgbString + ", " + opacity + ", " + p);
		theData.push({"rgb": rgbString, "opacity": opacity, "percent": p});
		rainbowColor.push(rgbString);

	}
	//console.log(rainbowColor);
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

function getGrayScale() {
	var grayScale = [];
	for (var i = 255; i >= 0; i--) {
		rgbString = getHexString(i);

		hexString = '#' + rgbString + rgbString + rgbString;
		grayScale.push(hexString);
	}
	return grayScale;
}


function getHexString(color) {
	var hex = color.toString(16);
	return hex.length == 1 ? "0" + hex : hex;
}
