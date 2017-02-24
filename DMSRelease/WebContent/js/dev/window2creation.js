/**
 * Used for displaying time series details from Metadata Queries 
 * */
function enlargeGraph(paraStruct, id, JSONResponse) {
	var parentUrl = window.location.href.split('DMS')[0];
	var m = id.replace('#', 'meta_');
	var widthsetting = 'width=' + screen.width + ", height=700,scrollbars=yes";

	win = window.open("", "Detailed Heatmap", widthsetting);
	
	var style = document.getElementsByTagName("style");
	var arrStyleSheets = document.getElementsByTagName("link");
	var oHead = win.document.getElementsByTagName("head")[0];
	 win.document.getElementsByTagName("body")[0].setAttribute("parentUrl", parentUrl);
	// $(win.document.head).append("<link href='/DMS/css/sys/bootstrap.min.css' rel='stylesheet'>");
	// $(win.document.head).append("<script src='/DMS/js/sys/b'></script>");
	
	oHead.appendChild(style[0].cloneNode(true));
	oHead.appendChild(arrStyleSheets[0].cloneNode(true));	
	
	win.onbeforeunload = function () {
		grapfshower = 0;
		if(Project.toLowerCase()=="epidemic"){
			graph1show = 0;
			graph2show = 0;
			countgraph = 0;
		}
		else if(Project.toLowerCase() == "energy"){
			graph1show = 0;
			graph2show = 0;
			countgraph = 0;
		}
		var listsvg = new Array();
		listsvg = win.document.getElementsByTagName("svg");
		for (var i = 1; i < listsvg.length;) {
			//keep the id of th element in the second windows
			var idlocalnewW = listsvg[1].id; 
			removeElement(idlocalnewW, false);
		}
		win = null;
		colorSchemeNW = null;
	};
	this.onbeforeunload = function () {

		win.close();

	};

	colour = colorScheme;


	if (colorSchemeNW != null) {
		var mn = win.document.getElementById("minlocal").value;
		var Mx = win.document.getElementById("maxlocal").value;
		var buckets = colorSchemeNW.length;
		if(System =="mongodb")
			deltaHue=(Mx * 1000 - mn * 1000) / (buckets - 1);
		else if(System =="opentsdb")
			deltaHue=(Mx  - mn ) / (buckets - 1);

		for (var i = 0; i < buckets; i++) {
			if(System =="mongodb")
				pivotssecWin[i] = BigInteger.toJSValue(BigInteger(mn * 1000).add(deltaHue * i));
			else if(System =="opentsdb")
				pivotssecWin[i] = BigInteger.toJSValue(BigInteger(mn ).add(deltaHue * i));

		}
		colour = colorSchemeNW;
	} else {
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
			// the second parameter, set to 1 here, is the saturation
			// the third parameter is "lightness"    
			rgbString = d3.hsl(theHue, 1, 0.6).toString();
			opacity = opacityStart + deltaOpacity * i;
			p = 0 + deltaPercent * i;
			theData.push({"rgb": rgbString, "opacity": opacity, "percent": p});
			rainbowColor.push(rgbString);
		}

		colorSchemeNW = rainbowColor;
		var mn = document.getElementById("minlocal").value;
		var Mx = document.getElementById("maxlocal").value;
		var buckets = colorSchemeNW.length;
		deltaHue = (Mx * 1000 - mn * 1000) / (buckets - 1);

		for (var i = 0; i < buckets; i++) {
			pivotssecWin[i] = BigInteger.toJSValue(BigInteger(mn * 1000).add(deltaHue * i));
		}
	}

	/**
	 *  Silve Graph Code
	 *  */

	if (grapfshower == 0) {
		/*prepare the colour legend */
		var tablelegend = document.createElement("table");
		// tablelegend.setAttribute( "width","100%");
		tablelegend.setAttribute("align", "center");
		var trlegend = document.createElement("tr");
		var tdleg1 = document.createElement("td");
		tdleg1.setAttribute("id", "tableg1");
		var tdleg2 = document.createElement("td");
		tdleg2.setAttribute("id", "tableg2");
		var tdleg3 = document.createElement("td");
		tdleg3.setAttribute("id", "tableg3");
		trlegend.appendChild(tdleg1);
		trlegend.appendChild(tdleg2);
		trlegend.appendChild(tdleg3);
		tablelegend.appendChild(trlegend);
		/* Add the script to parent for the input to change the colour into the heatmap into the child wndows*/
		//Append down Sampling Menu
		var trSampling = document.createElement("tr");
		var tdSampling = document.createElement("td");
		tdSampling.setAttribute("colspan", "3");

		var samplingMenu = document.getElementById("downsamplingTable").cloneNode(true);

		//samplingMenu.setAttribute("id","secondDowmSmpling");
		tdSampling.appendChild(samplingMenu);
		trSampling.appendChild(tdSampling);
		tablelegend.appendChild(trSampling);
		var downsamplbutton = document.createElement("input");
		var trSamplingbtn = document.createElement("tr");
		var tdSamplingbtn = document.createElement("td");
		tdSamplingbtn.setAttribute("colspan", "3");

		downsamplbutton.setAttribute("type", "button");
		downsamplbutton.setAttribute("align", "center");
		downsamplbutton.setAttribute("class", "Collapsebtn");
		downsamplbutton.setAttribute("value", "Execute Filtering");
		downsamplbutton.setAttribute("id", "DownsamplingAction");
		downsamplbutton.setAttribute("onclick", "window.opener.performDS();");
		tdSamplingbtn.appendChild(downsamplbutton);
		// ADD METADATA COMPARISON BUTTON //
		var tdchekxmldiff = document.createElement("td");
		//button
		var buttoncheckfile = document.createElement("input");
		buttoncheckfile.setAttribute("id", "highlightdifference");
		buttoncheckfile.setAttribute("type", "button");
		buttoncheckfile.setAttribute("class", "Collapsebtn");
		buttoncheckfile.setAttribute("value", "Compare Selected Metadata");
		buttoncheckfile.setAttribute("onclick", "window.opener.xmlDifferences();");//create this jscript function
		tdSamplingbtn.appendChild(buttoncheckfile);
		// End checking xml//
		//start grayscale button
		/*     var comparegreybutton = document.createElement("input");
                //var trgrayscale1 = document.createElement("tr");
                //var tdgrayscale = document.createElement("td");
                comparegreybutton.setAttribute("id", "comparegrayscalewin2button");
                comparegreybutton.setAttribute("type", "button");
                comparegreybutton.setAttribute("class", "Collapsebtn");
                comparegreybutton.setAttribute("value", "Show Vusual Comparison");
                comparegreybutton.setAttribute("onclick", "window.opener.greyScaleCompareSVG(0);");//create this jscript function
                tdSamplingbtn.appendChild(comparegreybutton);

		 */   
		trSamplingbtn.appendChild(tdSamplingbtn);
		tablelegend.appendChild(trSamplingbtn);
		/*  end downsampling */
		var minfieldwin = document.getElementById("minlocal").cloneNode(false);
		minfieldwin.setAttribute("onchange", "window.opener.Changecolorwin()");

		var maxfieldwin = document.getElementById("maxlocal").cloneNode(false);
		maxfieldwin.setAttribute("onchange", "window.opener.Changecolorwin()");

		tdleg1.appendChild(minfieldwin);
		//var leg = document.getElementById("LegendDIV").cloneNode(true);
		var leg = document.createElement("svg");
		var imgNode = document.createElement("img");
		imgNode.setAttribute("src", "https://hive.asu.edu:8443/MongoDBEnergy/Jscript/legend.png");
		tdleg2.appendChild(leg);
		tdleg2.appendChild(imgNode);
		//tdleg2.appendChild(leg);
		tdleg3.appendChild(maxfieldwin);
		win.document.body.appendChild(tablelegend);
		/* colour legend*/

		/*ADD THE DOWNSAMPLING BUTTON*/

		/*    // ADD METADATA COMPARISON BUTTON //

                //button
                var buttoncheckfile = document.createElement("input");
                buttoncheckfile.setAttribute("id", "highlightdifference");
                buttoncheckfile.setAttribute("type", "button");
                buttoncheckfile.setAttribute("class", "Collapsebtn");
                buttoncheckfile.setAttribute("value", "Compare Selected Metadata");
                buttoncheckfile.setAttribute("onclick", "window.opener.xmlDifferences();");//create this jscript function
                //chackbox
                var checkbox = document.createElement('input');
                checkbox.type = "checkbox";
                checkbox.name = "name";
                checkbox.value = "value";
                checkbox.id = "idchackbox";
                //lable for checkbox
                var label = document.createElement('label')
                label.htmlFor = "idlabelchack";
                label.appendChild(document.createTextNode('Click the checkbox to select two elements that you like to compare: '));

                var tablemetacomp = document.createElement("table");
                var trmetacomp = document.createElement("tr");
                var tdmetacomp = document.createElement("td");
                tdmetacomp.appendChild(label);
                tdmetacomp.appendChild(checkbox);
                tdmetacomp.appendChild(buttoncheckfile);
                trmetacomp.appendChild(tdmetacomp);
                tablemetacomp.appendChild(trmetacomp);
                tablemetacomp.setAttribute("align", "center");
                win.document.body.appendChild(tablemetacomp);
                //ADDED THE METADATA COMPARISON//
		 */
		//  win.document.body.appendChild(downsamplbutton);

		//***//


		//Xilun Add options
		/* var tableGrayScale = document.createElement("table");
                tableGrayScale.setAttribute("align", "center");
                var trgrayscale =  document.createElement("tr");
                // Computation Type
                var td1 = document.createElement("td");
                var txt1 = document.createElement("label");
                	txt1.innerHTML="Grayscale Type";	//createTextNode("Grayscale Type");
                var br1 = document.createElement("br");
                td1.appendChild(txt1);
                td1.appendChild(br1);

                var selection1 = document.createElement("select");
                selection1.setAttribute("id", "grayScaleComputationMenu_Win2");
                selection1.setAttribute("onchange","window.opener.ChangemenuWind2();");
                var opt1 = document.createElement('option');
                opt1.setAttribute("key", 1);
                opt1.setAttribute("selected", "selected");
                opt1.innerHTML = "Mean Difference";
                selection1.appendChild(opt1);

                var opt2 = document.createElement('option');
                opt2.setAttribute("key", 2);
                opt2.innerHTML = "Feature Based";
                selection1.appendChild(opt2);
                td1.appendChild(selection1);
                trgrayscale.appendChild(td1);



                // Normalization Type
                var td2 = document.createElement("td");
                td2.setAttribute("id","NormType_win2");
                var txt2 = document.createElement("label");//createTextNode("Normalization Type");
                	txt2.innerHTML="Normalization Type";//.id="Normtypewind2";
                	txt2.setAttribute("id","Normtypewind2");
                var br2 = document.createElement("br");
                td2.appendChild(txt2);
                td2.appendChild(br2);

                var selection2 = document.createElement("select");
                selection2.setAttribute("id", "grayScaleNormalizationMenu_Win2");
                var optN1 = document.createElement('option');
                optN1.setAttribute("key", 2);
                optN1.setAttribute("selected", "selected");
                optN1.innerHTML = "Simulation";
                selection2.appendChild(optN1);

                var optN2 = document.createElement('option');
                optN2.setAttribute("key", 3);
                optN2.innerHTML = "Variate";
                selection2.appendChild(optN2);

                //var optN3 = document.createElement('option');
                //optN3.setAttribute("key", 4);
                //optN3.innerHTML = "Do Nothing";
                //selection2.appendChild(optN3);

                td2.appendChild(selection2);
				trgrayscale.appendChild(td2);


                // Difference Type
                var td3 = document.createElement("td");
                td3.setAttribute("id", "Difftype_tdwin2");
                var txt3 = document.createElement("label");//createTextNode("Difference Type");
                txt3.innerHTML="Difference Type";
                txt3.setAttribute("id","differencetypewind2");
                //txt3.id="differencetypewind2";
                var br3 = document.createElement("br");
                td3.appendChild(txt3);
                td3.appendChild(br3);

                var selection3 = document.createElement("select");
                selection3.setAttribute("id", "grayScaleDifferenceMenu_Win2");
                var optD1 = document.createElement('option');
                optD1.setAttribute("key", 1);
                optD1.setAttribute("selected", "selected");
                optD1.innerHTML = "Absolute";
                selection3.appendChild(optD1);

                var optD2 = document.createElement('option');
                optD2.setAttribute("key", 2);
                optD2.innerHTML = "Percentage";
                selection3.appendChild(optD2);
                td3.appendChild(selection3);
                trgrayscale.appendChild(td3);

                tableGrayScale.appendChild(trgrayscale);

                //Xilun: Add grey Scale compare button
                var trgrayscale1 =  document.createElement("tr");
                // Computation Type
                var td11 = document.createElement("td"); 
                var comparegreybutton = document.createElement("BUTTON");
                var t1 = document.createTextNode("Compare Simulations");
                comparegreybutton.appendChild(t1);
                comparegreybutton.setAttribute("id","comparegrayscalewin2button");
                //comparegreybutton.setAttribute("align", "center");
                comparegreybutton.setAttribute("class", "Collapsebtn");
                comparegreybutton.setAttribute('onclick', 'window.opener.greyScaleCompareSVG(0)');
                td11.appendChild(comparegreybutton);
                trgrayscale1.appendChild(td11);

                tableGrayScale.appendChild(trgrayscale1);

                win.document.body.appendChild(tableGrayScale);
		 */
		var line = document.createElement("hr");
		line.setAttribute("class", "hr");
		line.setAttribute("width", "100%");
		win.document.body.appendChild(line);
		/*Appending of the heatmap*/
		var separator = document.createElement("div");
		separator.setAttribute("id", "separator");
		separator.setAttribute("align", "center");
		win.document.body.appendChild(separator);
		/*PArt to with append the heatmap*/

		/*Graph table preparation*/
		if(Project.toLowerCase()=="epidemic"){
			var tablegraph = document.createElement("table");
			tablegraph.setAttribute("width", "100%");
			tablegraph.setAttribute("height", "50%");
			var tr = document.createElement("tr");
			var td1 = document.createElement("td");
			td1.setAttribute("id", "g1");
			td1.setAttribute("width", "50%");
			var td2 = document.createElement("td");
			td2.setAttribute("id", "g2");
			td2.setAttribute("width", "50%");
			tr.appendChild(td1);
			tr.appendChild(td2);
			tablegraph.appendChild(tr);
			win.document.body.appendChild(tablegraph);
		}
		/**
		 * Copy from above
		 * */
		else if(Project.toLowerCase() == 'energy'){
			var tablegraph = document.createElement("table");
			tablegraph.setAttribute("width", "100%");
			tablegraph.setAttribute("height", "50%");
			var tr = document.createElement("tr");
			var td1 = document.createElement("td");
			td1.setAttribute("id", "g1");
			td1.setAttribute("width", "50%");
			var td2 = document.createElement("td");
			td2.setAttribute("id", "g2");
			td2.setAttribute("width", "50%");
			tr.appendChild(td1);
			tr.appendChild(td2);
			tablegraph.appendChild(tr);
			win.document.body.appendChild(tablegraph);
		}
		/*Graph table preparation*/

		//flag for windows ready
		grapfshower = 1;
	}
	if(Project.toLowerCase()=="epidemic"){
		var graphfile = JSONResponse.split("graph:");
		var nameGraph = graphfile[1].split(".")[0];
		var stateofinterest = "";
		for(var i = 0 ;i<paraStruct['schakels'].length;i++){
			if(i<paraStruct['schakels'].length-1){
				stateofinterest=stateofinterest+paraStruct['schakels'][i]+"_";
			}else{
				stateofinterest=stateofinterest+paraStruct['schakels'][i];
			}
		}
		if (nameGraph == "graph1" && graph1show == 0) {
			countgraph = countgraph + 1;
			var iframegraph = document.createElement("iframe");
			iframegraph.setAttribute("src",parentUrl+"DMS/jsp/Graph1gmap.jsp?intereststate="+stateofinterest);
			iframegraph.setAttribute("scrolling", "no");
			//iframegraph.setAttribute("align","bottom");
			iframegraph.setAttribute("height", "100%");
			iframegraph.setAttribute("width", "100%");
			win.document.getElementById("g" + countgraph).appendChild(iframegraph);
			graph1show = 1;
		}
		if (nameGraph == "graph2" && graph2show == 0) {

			countgraph = countgraph + 1;
			var iframegraph = document.createElement("iframe");
			iframegraph.setAttribute("src", parentUrl+"DMS/jsp/Graph2gmap.jsp?intereststate="+stateofinterest);
			iframegraph.setAttribute("scrolling", "no");
			iframegraph.setAttribute("height", "100%");
			iframegraph.setAttribute("width", "100%");
			win.document.getElementById("g" + countgraph).appendChild(iframegraph);
			graph2show = 1;
		}
	}
	/*Silv graph code*/
	
	else if(Project.toLowerCase() == "energy"){
		var energyModel = id.split("_")[1];
		if(energyModel.indexOf("MultiStory")!=-1 && graph1show == 0){
			countgraph = countgraph + 1;
			var energyGraph = document.createElement("img");
			energyGraph.setAttribute("src", "/MVTSDB/image/EnergyLayout/MultiStory.png");
			// energyGraph.setAttribute("src", "https://hive.asu.edu:8443/MVTSDB/jsp/Graph2gmap.jsp?intereststate="+stateofinterest);//https://hive.asu.edu:8443/Ebola-Project/Graph2Page.html");
			// energyGraph.setAttribute("src", "http://localhost:8080/MVTSDB/jsp/Graph2gmap.jsp?intereststate="+stateofinterest);//https://hive.asu.edu:8443/Ebola-Project/Graph2Page.html");
			energyGraph.setAttribute("scrolling", "no");
			// energyGraph.setAttribute("align","bottom");
			energyGraph.setAttribute("height", "100%");
			energyGraph.setAttribute("width", "100%");
			win.document.getElementById("g" + countgraph).appendChild(energyGraph);
			graph1show = 1;
		}
		else if(energyModel.indexOf("Smalloffice") != -1){
			countgraph = countgraph + 1;
			var energyGraph = document.createElement("img");
			energyGraph.setAttribute("src", "/MVTSDB/image/EnergyLayout/SmallOffice.png");
			// energyGraph.setAttribute("src", "https://hive.asu.edu:8443/MVTSDB/jsp/Graph2gmap.jsp?intereststate="+stateofinterest);//https://hive.asu.edu:8443/Ebola-Project/Graph2Page.html");
			// energyGraph.setAttribute("src", "http://localhost:8080/MVTSDB/jsp/Graph2gmap.jsp?intereststate="+stateofinterest);//https://hive.asu.edu:8443/Ebola-Project/Graph2Page.html");
			energyGraph.setAttribute("scrolling", "no");
			// energyGraph.setAttribute("align","bottom");
			energyGraph.setAttribute("height", "100%");
			energyGraph.setAttribute("width", "100%");
			win.document.getElementById("g" + countgraph).appendChild(energyGraph);
			graph1show = 1;
			
		}
		else {
			countgraph = countgraph + 1;
			var energyGraph = document.createElement("img");
			//energyGraph.setAttribute("src", "http://localhost:8080/MVTSDB/image/EnergyLayout/in.png");
			//console.log("SHould have something1: " + document.getElementById("energyGraph").src);
			
			energyGraph.src = "http://localhost:8080/MVTSDB/image/EnergyLayout/in.png";
			console.log("SHould have something2: " + energyGraph.src);
			// energyGraph.setAttribute("src", "https://hive.asu.edu:8443/MVTSDB/jsp/Graph2gmap.jsp?intereststate="+stateofinterest);//https://hive.asu.edu:8443/Ebola-Project/Graph2Page.html");
			// energyGraph.setAttribute("src", "http://localhost:8080/MVTSDB/jsp/Graph2gmap.jsp?intereststate="+stateofinterest);//https://hive.asu.edu:8443/Ebola-Project/Graph2Page.html");
			energyGraph.setAttribute("scrolling", "no");
			// energyGraph.setAttribute("align","bottom");
			energyGraph.setAttribute("height", "100%");
			energyGraph.setAttribute("width", "100%");
			win.document.getElementById("g" + countgraph).appendChild(energyGraph);
			graph1show = 1;
		}
		
	}
	
	var numTimePoints = paraStruct['numTimePoints'];
	var dataset = paraStruct['dataset'];
	var schakels = paraStruct['schakels'];
	var start = paraStruct['start'];
	var end = paraStruct['end'];
	var numDataPoints = paraStruct['numDataPoints'];

	var cellh = 20;
	var xpadding = 35;
	//////console.log("Num character: enlarge: "+ schakels[0].length);

	//var padding = schakels[0].length*schakels[0].length + xpadding;
	var padding = 10 * 10 + xpadding;
	//   var cellw = 10;//parseInt(w/numTimePoints) + 1;

	var w = Math.ceil(screen.width * 1) - padding;
	var cellw = parseInt(w / numTimePoints) + 1;

	//var w = (cellw * numTimePoints) ;
	var h = cellh * numDataPoints + 35;

	var xscale = d3.time.scale()
	.domain([start, end])
	.range([padding, w]);

	var xAxis = d3.svg.axis()
	.scale(xscale)
	.orient("bottom")
	.ticks(d3.time.days, 3);


	var yscale = d3.scale.ordinal()
	.domain(schakels)
	.rangeBands([0, h - 35]);//.rangeBands([padding, h - padding]);//

	var yAxis = d3.svg.axis()
	.scale(yscale)
	.orient('left');


	var zscale = d3.scale.linear()
	.domain(pivotssecWin)
	.range(colour);

	var title1 = JSONResponse.split("^");
	//console.log(title1);

	//TITLE NEED TO UPDATE TEXT TO BE A VARIABLE
	//NEED A WAY TO GENERATE A TITLE OR ADD TITLES INTO ARRAY

	var tabletitle = document.createElement("Table");
	tabletitle.setAttribute("id", id + "table_Title");
	tabletitle.setAttribute("width", "50%");
	tabletitle.setAttribute("align", "center");
	var tabletitleTR = document.createElement("tr");
	var temptitle = new Array();
	temptitle = title1[0].split(";");
	for (var i = 0; i < temptitle.length; i++) {
		var locTD = document.createElement("td");
		locTD.setAttribute("id", id + "_" + temptitle[i].split(":")[0]);
		var locTX = document.createElement("text");
		locTX.setAttribute("id", id + "_Title_" + temptitle[i].split(":")[0]);
		locTX.setAttribute("style", "font-weight:bold; font-size:14px; font-family:arial");
		locTX.textContent = temptitle[i];//= ;
		locTD.appendChild(locTX);
		tabletitleTR.appendChild(locTD);
	}
	tabletitle.appendChild(tabletitleTR);
	win.document.getElementById("separator").appendChild(tabletitle);

	if(Project.toLowerCase()=="epidemic"){
		var tabletitle1 = document.createElement("Table");
		tabletitle1.setAttribute("id", id + "table_Title1");
		tabletitle1.setAttribute("align", "center");
		tabletitle1.setAttribute("width", "100%");
		var tabletitleTR1 = document.createElement("tr");
		var temptitle1 = new Array();
		temptitle1 = title1[1].split(";");
		for (var i = 0; i < temptitle1.length; i++) {
			var locTD = document.createElement("td");
			locTD.setAttribute("id", id + "_" + temptitle1[i].split(":")[0]);
			var locTX = document.createElement("text");
			locTX.setAttribute("id", id + "_Title_" + temptitle1[i].split(":")[0]);
			locTX.setAttribute("style", "font-weight:bold; font-size:14px; font-family:arial");
			locTX.textContent = temptitle1[i];//= ;
			locTD.appendChild(locTX);
			tabletitleTR1.appendChild(locTD);
		}
		tabletitle1.appendChild(tabletitleTR1);
		win.document.getElementById("separator").appendChild(tabletitle1);
	}
	var svg1 = d3.select(win.document.getElementById("separator"))//win.document.getElementById("extendedHeatmap"))//document.createElement("div"))//
	.append('svg')
	.attr("id", id)
	.attr('width', w)
	.attr('height', h)

	var row1 = svg1.selectAll('.row')
	.data(dataset)
	.enter()
	.append('svg:g')
	.attr('class', 'row')


	var col1 = row1.selectAll('.cell')
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
	.on("mouseout", mouseout);

	svg1.append("g")
	.attr("class", "x axis")
	.attr("transform", "translate(" + 0 + "," + (h - xpadding ) + ")")//.attr("transform", "translate(0," + (h - padding) + ")")
	.call(d3.svg.axis().scale(xscale).ticks(d3.time.months).orient("bottom"))
	.append("text")
	.attr("class", "label")
	.attr("x", 25)
	.attr("y", -5)
	.attr("transform", "rotate(-90)")
	.text("Date");


	svg1.append("g")
	.attr("class", "y axis")
	.attr("transform", "translate(" + padding + ",0)")
	.call(d3.svg.axis().scale(yscale).orient("left"))
	.append("text")
	.attr("class", "label")
	.attr("y", 6)
	.attr("transform", "rotate(-65)");

	d3.select(win.document.getElementById("separator"))
	.append("text")
	.attr("x", (w / 2))
	.attr("y", (h / 5))
	.attr("id", id + "_Title1")
	.attr("text-anchor", "middle")
	.style("font-weight", "bold")
	.style("font-size", "14px")
	.style("display", "none")
	.text(JSONResponse);//.replace(/;/g,'  ~  '));

	var div1 = d3.select(win.document.getElementById("separator"))//.body//win.document.getElementById("extendedHeatmap"))//document.createElement("div"))//win.document.body)
	.append("div")
	.attr("id", id + "_tooltip")
	.attr("class", "tooltip")
	.style("opacity", 1e-6);

	d3.select(win.document.getElementById("separator"))
	.append("br")
	.attr("id", id + "_br");

	d3.select(win.document.getElementById("separator"))
	.append("div")
	.attr("id", id + "_greyscaleid");


	d3.select(win.document.getElementById("separator"))
	.append('label')
	.attr("id", id + "_lablechackbox")
	.text(function (d) {
		return "Check to select this simulation ";
	})
	
	.append("input")
	.attr("type", "checkbox")
	.attr("id", id + "_chackbox")
	.attr("name", "check")
	.attr("onChange", "window.opener.checknumberofSelected(\"" + id + "_chackbox\");");//create this function


	d3.select(win.document.getElementById("separator"))
	.append("input")
	.attr("type", "button")
	.attr("class", "Collapsebtn btn btn-danger btn-xs")
	.attr("value", "Remove")
	.attr("id", id + "_remove")
	.attr("style","border:1px solid firebrick")
	.attr("onclick", "window.opener.removeElement(\"" + id + "\",true);");

	d3.select(win.document.getElementById("separator"))
	.append("input")
	.attr("type", "button")
	.attr("class", "Collapsebtn")
	.attr("value", "Match Range")
	.attr("id", id + "_colorchange")
	.attr("onclick", "window.opener.changecolorinwindows(\"" + id + "\"," + (NumQuery - 1) + ");");


	d3.select(win.document.getElementById("separator"))
	.append("input")
	.attr("type", "button")
	.attr("class", "Collapsebtn")
	.attr("value", "Download Meta")
	.attr("id", id + "_metadownload")
	.attr("onclick", "window.opener.QueryDownload(\"" + id + "\");");

	d3.select(win.document.getElementById("separator"))
	.append("input")
	.attr("type", "button")
	.attr("class", "Collapsebtn")
	.attr("value", "Download Data")
	.attr("id", id + "_datadownload")
	.attr("onclick", "window.opener.QueryDownloadData(\"" + id + "\");");
	//.attr("onclick", "window.opener.QueryDownloadData(\"" + id + "\")");//"window.opener.QueryDownload(\"" + id + "\");");


	var hiddendivCloned = (document.getElementById(id.replace("#", "") + "_Index")).cloneNode(true);

	hiddendivCloned.setAttribute("id", id + "_Index");
	hiddendivCloned.setAttribute("querynum", NumQuery - 1);
	hiddendivCloned.setAttribute("style", "display: none");
	hiddendivCloned.setAttribute("value", eleminewinindex);

	var JsonData = JSON.parse(result);
	Datain2win[eleminewinindex] = JsonData[parseInt(document.getElementById(id.replace("#", "") + "_Index").getAttribute("value"))];

	eleminewinindex = eleminewinindex + 1;


	win.document.getElementById("separator").appendChild(hiddendivCloned);

	d3.select(win.document.getElementById("separator"))
	.append("hr")
	.attr("class", "hr")
	.attr("id", id + "_line")
	.attr("width", "100%");
	function btndivclick() {
		alert("clicks");
	}

	function mousemove(d) {
		div1
		.text("Info about " + d[0] + " : " + d[1] + " : " + d[2] / 1000)
		.style("left", (d3.event.pageX ) + "px")
		.style("top", (d3.event.pageY) + "px");
	}

	function mouseover() {
		div1.transition()
		.duration(300)
		.style("opacity", 1);
	}

	function mouseout() {
		div1.transition()
		.duration(300)
		.style("opacity", 1e-6);
	}

	highlight();
	ChangemenuWind2();
	grayScaleCheckedStatuswin2();

	$('#downsamplingTable').ready(function(){
		// change  the attribute to perform downsampling

		var startcheckwin = win.document.getElementById("startCheck");
		startcheckwin.setAttribute("onclick","window.opener.startTimeCheckedStatuswin2()");

		var endcheckwin=win.document.getElementById("endCheck");
		endcheckwin.setAttribute("onclick","window.opener.endTimeCheckedStatuswin2()");

		var samplingcheckwin=win.document.getElementById("samplingCheck");
		samplingcheckwin.setAttribute("onclick","window.opener.samplingCheckedStatuswin2()");

		var grayScaleMenuwin=win.document.getElementById("grayScaleMenu");
		grayScaleMenuwin.setAttribute("onchange","window.opener.grayScaleCheckedStatuswin2()");
		var chmenuforfeatures= win.document.getElementById("grayScaleTypeMenu");
		chmenuforfeatures.setAttribute("onchange","window.opener.ChangemenuWind2()");


	});
}



/**
 * Used for displaying time series details of Search Similar query object 
 * */
function enlargeGraph1(paraStruct, id, JSONResponse, hidediv) {

	var m = id.replace('#', 'meta_');
	var widthsetting = 'width=' + screen.width + ", height=700";

	win = window.open("", "Detailed Heatmap", widthsetting);
	win.onbeforeunload = function () {
		grapfshower = 0;
		if(Project=="epidemic"){
			graph1show = 0;
			graph2show = 0;
			countgraph = 0;
		}
		var listsvg = new Array();
		listsvg = win.document.getElementsByTagName("svg");
		for (var i = 1; i < listsvg.length;) {
			var idlocalnewW = listsvg[1].id; //keep the id of th element in the second windows
			removeElement(idlocalnewW, false);
		}
		win = null;
		colorSchemeNW = null;
	};
	this.onbeforeunload = function () {


		win.close();

	};

	//var colour = colorScheme;


	if (colorSchemeNW != null) {
		var mn = win.document.getElementById("minlocal").value;
		var Mx = win.document.getElementById("maxlocal").value;
		var buckets = colorSchemeNW.length;
		var deltaHue;
		if(System == "mongodb"){
			deltaHue = (Mx * 1000 - mn * 1000) / (buckets - 1);
		}else if(System == "opentsdb"){
			deltaHue = (Mx  - mn ) / (buckets - 1);
		}
		for (var i = 0; i < buckets; i++) {
			if(System == "mongodb"){
				pivotssecWin[i] = BigInteger.toJSValue(BigInteger(mn * 1000).add(deltaHue * i));
			}else if(System == "opentsdb"){
				pivotssecWin[i] = BigInteger.toJSValue(BigInteger(mn ).add(deltaHue * i));
			}
		}
		colour = colorSchemeNW;
	}

	/* Silve Graph Code*/

	if (grapfshower == 0) {
		/*prepare the colour legend */
		var tablelegend = document.createElement("table");
		// tablelegend.setAttribute( "width","100%");
		var trlegend = document.createElement("tr");
		var tdleg1 = document.createElement("td");
		tdleg1.setAttribute("id", "tableg1");
		var tdleg2 = document.createElement("td");
		tdleg2.setAttribute("id", "tableg2");
		var tdleg3 = document.createElement("td");
		tdleg3.setAttribute("id", "tableg3");
		trlegend.appendChild(tdleg1);
		trlegend.appendChild(tdleg2);
		trlegend.appendChild(tdleg3);
		tablelegend.appendChild(trlegend);
		/* Add the script to parent for the input to change the colour into the heatmap into the child wndows*/
		//Append down Sampling Menu
		var trSampling = document.createElement("tr");
		var tdSampling = document.createElement("td");
		tdSampling.setAttribute("colspan", "3");

		var samplingMenu = document.getElementById("downsamplingTable").cloneNode(true);
		//samplingMenu.setAttribute("id","secondDowmSmpling");
		tdSampling.appendChild(samplingMenu);
		trSampling.appendChild(tdSampling);
		tablelegend.appendChild(trSampling);
		var downsamplbutton = document.createElement("input");
		var trSamplingbtn = document.createElement("tr");
		var tdSamplingbtn = document.createElement("td");
		tdSamplingbtn.setAttribute("colspan", "3");

		downsamplbutton.setAttribute("type", "button");
		downsamplbutton.setAttribute("align", "center");
		downsamplbutton.setAttribute("class", "Collapsebtn");
		downsamplbutton.setAttribute("value", "Execute Filtering");
		downsamplbutton.setAttribute("id", "DownsamplingAction");
		downsamplbutton.setAttribute("onclick", "window.opener.performDS();");
		tdSamplingbtn.appendChild(downsamplbutton);
		// ADD METADATA COMPARISON BUTTON //
		var tdchekxmldiff = document.createElement("td");
		//button
		var buttoncheckfile = document.createElement("input");
		buttoncheckfile.setAttribute("id", "highlightdifference");
		buttoncheckfile.setAttribute("type", "button");
		buttoncheckfile.setAttribute("class", "Collapsebtn");
		buttoncheckfile.setAttribute("value", "Compare Selected Metadata");
		buttoncheckfile.setAttribute("onclick", "window.opener.xmlDifferences();");//create this jscript function
		tdSamplingbtn.appendChild(buttoncheckfile);
		// End checking xml//
		trSamplingbtn.appendChild(tdSamplingbtn);
		tablelegend.appendChild(trSamplingbtn);

		//ADD A BUTTON TO FILTER THE ELEMENT IN HERE AND PERFORM AN ACTION
		/* END dOWNSAMPLING CODE*/
		var minfieldwin = document.getElementById("minlocal").cloneNode(false);
		minfieldwin.setAttribute("onchange", "window.opener.Changecolorwin()");

		var maxfieldwin = document.getElementById("maxlocal").cloneNode(false);
		maxfieldwin.setAttribute("onchange", "window.opener.Changecolorwin()");

		tdleg1.appendChild(minfieldwin);
		// var leg = document.getElementById("LegendDIV").cloneNode(true);
		var leg = document.createElement("svg");
		var imgNode = document.createElement("img");
		imgNode.setAttribute("src", "https://hive.asu.edu:8443/MongoDBEnergy/Jscript/legend.png");
		tdleg2.appendChild(leg);
		tdleg2.appendChild(imgNode);

		tdleg3.appendChild(maxfieldwin);
		win.document.body.appendChild(tablelegend);
		/* colour legend*/
		/* ADD THE DOWNSAMPLING BUTTON */
		/*      // ADD METADATA COMPARISON BUTTON //

                //button
                var buttoncheckfile = document.createElement("input");
                buttoncheckfile.setAttribute("id", "highlightdifference");
                buttoncheckfile.setAttribute("type", "button");
                buttoncheckfile.setAttribute("class", "Collapsebtn");
                buttoncheckfile.setAttribute("value", "Compare Selected Metadata");
                buttoncheckfile.setAttribute("onclick", "window.opener.xmlDifferences();");//create this jscript function
                //chackbox
                var checkbox = document.createElement('input');
                checkbox.type = "checkbox";
                checkbox.name = "name";
                checkbox.value = "value";
                checkbox.id = "idchackbox";
                //lable for checkbox
                var label = document.createElement('label')
                label.htmlFor = "idlabelchack";
                label.appendChild(document.createTextNode('Click the checkbox to select two elements that you like to compare: '));

                var tablemetacomp = document.createElement("table");
                var trmetacomp = document.createElement("tr");
                var tdmetacomp = document.createElement("td");
                tdmetacomp.appendChild(label);
                tdmetacomp.appendChild(checkbox);
                tdmetacomp.appendChild(buttoncheckfile);
                trmetacomp.appendChild(tdmetacomp);
                tablemetacomp.appendChild(trmetacomp);
                tablemetacomp.setAttribute("align", "center");
                win.document.body.appendChild(tablemetacomp);
                //ADDED THE METADATA COMPARISON//
		 */

		//  win.document.body.appendChild(downsamplbutton);
		//***//
		var line = document.createElement("hr");
		line.setAttribute("class", "hr");
		line.setAttribute("width", "100%");
		win.document.body.appendChild(line);

		//Xilun: Add grey Scale compare button
		var comparegreybutton = document.createElement("BUTTON");
		var t1 = document.createTextNode("Compare Simulations");
		comparegreybutton.appendChild(t1);
		//comparegreybutton.setAttribute("align", "center");
		comparegreybutton.setAttribute("class", "Collapsebtn");
		comparegreybutton.setAttribute('onclick', 'window.opener.greyScaleCompareSVG()');
		win.document.body.appendChild(comparegreybutton);


		/*Appending of the heatmap*/
		var separator = document.createElement("div");
		separator.setAttribute("id", "separator");
		separator.setAttribute("align", "center");
		win.document.body.appendChild(separator);
		/*PArt to with append the heatmap*/

		/*Graph table preparation*/
		if(Project=="epidemic"){
			var tablegraph = document.createElement("table");
			tablegraph.setAttribute("width", "100%");
			tablegraph.setAttribute("height", "50%");
			var tr = document.createElement("tr");
			var td1 = document.createElement("td");
			td1.setAttribute("id", "g1");
			td1.setAttribute("width", "50%");
			var td2 = document.createElement("td");
			td2.setAttribute("id", "g2");
			td2.setAttribute("width", "50%");
			tr.appendChild(td1);
			tr.appendChild(td2);
			tablegraph.appendChild(tr);
			win.document.body.appendChild(tablegraph);
		}
		/*Graph table preparation*/

		//flag for windows ready
		grapfshower = 1;
	}
	if(Project=="epidemic"){
		var graphfile = JSONResponse.split("graph:");
		////console.log(JSONResponse);
		var nameGraph = graphfile[1].split(".")[0];
		////console.log("namegrph: " +nameGraph);
		var stateofinterest;
		for(var i = 0 ;i<paraStruct['schakels'].length;i++){
			if(i<paraStruct['schakels'].length-1){
				stateofinterest=stateofinterest+paraStruct['schakels'][i]+"_";
			}else{
				stateofinterest=stateofinterest+paraStruct['schakels'][i];
			}
		}

		if (nameGraph == "graph1" && graph1show == 0) {
			countgraph = countgraph + 1;
			var iframegraph = document.createElement("iframe");
			iframegraph.setAttribute("src", "https://hive.asu.edu:8443/MVTSDB/jsp/Graph1gmap.jsp?intereststate="+stateofinterest);//AZ_CA_NM");//  iframegraph.setAttribute("src", "https://hive.asu.edu:8443/MongoDBEnergy/Graph1Page.html");//http://localhost:8080
			//iframegraph.setAttribute("src", "localhost:8080/MVTSDB/jsp/Graph1gmap.jsp?intereststate="+stateofinterest);//AZ_CA_NM");//  iframegraph.setAttribute("src", "https://hive.asu.edu:8443/MongoDBEnergy/Graph1Page.html");//http://localhost:8080
			iframegraph.setAttribute("scrolling", "no");
			iframegraph.setAttribute("height", "100%");
			iframegraph.setAttribute("width", "100%");
			win.document.getElementById("g" + countgraph).appendChild(iframegraph);
			graph1show = 1;
		}
		if (nameGraph == "graph2" && graph2show == 0) {
			countgraph = countgraph + 1;
			var iframegraph = document.createElement("iframe");
			iframegraph.setAttribute("src", "https://hive.asu.edu:8443/MVTSDB/jsp/Graph2gmap.jsp?intereststate="+stateofinterest);//              iframegraph.setAttribute("src", "https://hive.asu.edu:8443/MongoDBEnergy/Graph2Page.html");
			//iframegraph.setAttribute("src", "localhost:8080/MVTSDB/jsp/Graph2gmap.jsp?intereststate="+stateofinterest);//              iframegraph.setAttribute("src", "https://hive.asu.edu:8443/MongoDBEnergy/Graph2Page.html");
			iframegraph.setAttribute("scrolling", "no");
			iframegraph.setAttribute("height", "100%");
			iframegraph.setAttribute("width", "100%");
			win.document.getElementById("g" + countgraph).appendChild(iframegraph);
			graph2show = 1;
		}
	}
	/*Silv graph code*/
	
	else if(Project == "energy"){
		var energyModel = id.split("_")[1];
		console.log("SIcong and REece are here: " + energyModel);
		
	}
	else{
		console.log("THe other guy: " + Project);
	}
		
		
	var numTimePoints = paraStruct['numTimePoints'];
	var dataset = paraStruct['dataset'];
	var schakels = paraStruct['schakels'];
	var start = paraStruct['start'];
	var end = paraStruct['end'];
	var numDataPoints = paraStruct['numDataPoints'];

	var cellh = 20;

	var xpadding = 35;
	var padding = 10 * 10 + xpadding;


	var w = Math.ceil(screen.width * 1) - padding;
	var cellw = parseInt(w / numTimePoints) + 1;

	//var w = (cellw * numTimePoints) ;
	var h = cellh * numDataPoints + 35;


	var xscale = d3.time.scale()
	.domain([start, end])
	.range([padding, w]);//- padding]);

	var xAxis = d3.svg.axis()
	.scale(xscale)
	.orient("bottom")
	.ticks(d3.time.days, 3);


	var yscale = d3.scale.ordinal()
	.domain(schakels)
	.rangeBands([0, h - 35]);//.rangeBands([padding, h - padding]);

	var yAxis = d3.svg.axis()
	.scale(yscale)
	.orient('left');


	var zscale = d3.scale.linear()
	.domain(pivotssecWin)
	.range(colour);

	var title1 = JSONResponse.split("^");
	var tabletitle = document.createElement("Table");
	tabletitle.setAttribute("id", id + "table_Title");
	tabletitle.setAttribute("width", "50%");
	tabletitle.setAttribute("align", "center");
	var tabletitleTR = document.createElement("tr");
	var temptitle = new Array();
	temptitle = title1[0].split(";");
	for (var i = 0; i < temptitle.length; i++) {
		var locTD = document.createElement("td");
		locTD.setAttribute("id", id + "_" + temptitle[i].split(":")[0]);
		var locTX = document.createElement("text");
		locTX.setAttribute("id", id + "_Title_" + temptitle[i].split(":")[0]);
		locTX.setAttribute("style", "font-weight:bold; font-size:14px; font-family:arial");
		locTX.textContent = temptitle[i];//= ;
		locTD.appendChild(locTX);
		tabletitleTR.appendChild(locTD);
	}
	tabletitle.appendChild(tabletitleTR);
	win.document.getElementById("separator").appendChild(tabletitle);
	if(Project=="epidemic"){
		var tabletitle1 = document.createElement("Table");
		tabletitle1.setAttribute("id", id + "table_Title1");
		tabletitle1.setAttribute("align", "center");
		tabletitle1.setAttribute("width", "100%");
		var tabletitleTR1 = document.createElement("tr");
		var temptitle1 = new Array();
		temptitle1 = title1[1].split(";");
		for (var i = 0; i < temptitle1.length; i++) {
			var locTD = document.createElement("td");
			locTD.setAttribute("id", id + "_" + temptitle1[i].split(":")[0]);
			var locTX = document.createElement("text");
			locTX.setAttribute("id", id + "_Title_" + temptitle1[i].split(":")[0]);
			locTX.setAttribute("style", "font-weight:bold; font-size:14px; font-family:arial");
			locTX.textContent = temptitle1[i];//= ;
			locTD.appendChild(locTX);
			tabletitleTR1.appendChild(locTD);
		}	
		tabletitle1.appendChild(tabletitleTR1);
		win.document.getElementById("separator").appendChild(tabletitle1);
	}

	/*

             d3.select(win.document.getElementById("separator"))
             .attr("x", (w/2))
             .attr("y", (h/5))
             .attr("id",id+"_Title")
             .attr("text-anchor", "middle")
             .style("font-weight", "bold")
             .style("font-size", "14px")
             .text(title1[0]);
	 */
	var svg1 = d3.select(win.document.getElementById("separator"))//win.document.getElementById("extendedHeatmap"))//document.createElement("div"))//
	.append('svg')
	.attr("id", id)
	.attr('width', w)
	.attr('height', h)

	var row1 = svg1.selectAll('.row')
	.data(dataset)
	.enter()
	.append('svg:g')
	.attr('class', 'row')


	var col1 = row1.selectAll('.cell')
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
	.on("mouseout", mouseout);

	svg1.append("g")
	.attr("class", "x axis")
	.attr("transform", "translate(" + 0 + "," + (h - xpadding ) + ")")//.attr("transform", "translate("+3+"," + (h - padding+3) + ")")//.attr("transform", "translate(0," + (h - padding) + ")")
	.call(d3.svg.axis().scale(xscale).ticks(d3.time.months).orient("bottom"))
	.append("text")
	.attr("class", "label")
	.attr("x", 25)
	.attr("y", -5)
	.attr("transform", "rotate(-90)")
	.text("Date");


	svg1.append("g")
	.attr("class", "y axis")
	.attr("transform", "translate(" + padding + ",0)")
	.call(d3.svg.axis().scale(yscale).orient("left"))
	.append("text")
	.attr("class", "label")
	.attr("y", 6)
	.attr("transform", "rotate(-65)")


	//TITLE NEED TO UPDATE TEXT TO BE A VARIABLE
	//NEED A WAY TO GENERATE A TITLE OR ADD TITLES INTO ARRAY

	/*	svg1.append("text")
             .attr("x", (w/2))
             .attr("y", (h/(numDataPoints+2)))
             .attr("id",id+"_Title")
             .attr("text-anchor", "middle")
             .style("font-weight", "bold")
             .style("font-size", "14px")
             .text(title1[0].replace(/;/g,'  ~  '));
	 */
	d3.select(win.document.getElementById("separator"))
	.append("text")
	.attr("x", (w / 2))
	.attr("y", (h / 5))
	.attr("id", id + "_Title1")
	.attr("text-anchor", "middle")
	.style("font-weight", "bold")
	.style("font-size", "14px")
	.style("display", "none")
	.text(JSONResponse);//title1[1].replace(/;/g,'  ~  '));

	var div1 = d3.select(win.document.getElementById("separator"))//.body//win.document.getElementById("extendedHeatmap"))//document.createElement("div"))//win.document.body)
	.append("div")
	.attr("id", id + "_tooltip")
	.attr("class", "tooltip")
	.style("opacity", 1e-6);

	d3.select(win.document.getElementById("separator"))
	.append("br")
	.attr("id", id + "_br");

	d3.select(win.document.getElementById("separator"))
	.append("div")
	.attr("id", id + "_greyscaleid");

	d3.select(win.document.getElementById("separator"))
	.append('label')
	.attr("id", id + "_lablechackbox")
	.text(function (d) {
		return "Check to select this simulation ";
	})
	.append("input")
	.attr("type", "checkbox")
	.attr("id", id + "_chackbox")
	.attr("name", "check")
	.attr("onChange", "window.opener.checknumberofSelected(\"" + id + "_chackbox\");");//create this function


	d3.select(win.document.getElementById("separator"))
	.append("input")
	.attr("type", "button")
	.attr("class", "Collapsebtn")
	.attr("value", "Remove")
	.attr("id", id + "_remove")
	.attr("onclick", "window.opener.removeElement(\"" + id + "\",true);");

	d3.select(win.document.getElementById("separator"))
	.append("input")
	.attr("type", "button")
	.attr("class", "Collapsebtn")
	.attr("value", "Match Range")
	.attr("id", id + "_colorchange")
	.attr("onclick", "window.opener.changecolorinwindows(\"" + id + "\"," + (NumQuery - 1) + ");");
	d3.select(win.document.getElementById("separator"))
	.append("input")
	.attr("type", "button")
	.attr("class", "Collapsebtn")
	.attr("value", "Download Meta")
	.attr("id", id + "_metadownload")
	.attr("onclick", "window.opener.QueryDownload(\"" + id + "\");");

	d3.select(win.document.getElementById("separator"))
	.append("input")
	.attr("type", "button")
	.attr("class", "Collapsebtn")
	.attr("value", "Download Data")
	.attr("id", id + "_datadownload")
	.attr("onclick","window.opener.QueryDownloadData(\"" + id + "\");");// "window.opener.QueryDownload(\"" + id + "\");");

	//			.attr("onclick","window.opener.changecolorinwindows(\""+id+"\","+(NumQuery-1)+");");

	/*  var hiddendivCloned = (document.getElementById(id.replace("#","")+"_Index")).cloneNode(true);
             ////console.log(hiddendivCloned);
             d3.select(win.document.getElementById("separator"))
             .append(hiddendivCloned);*/
	//.attr("querynum",NumQuery-1);
	//.attr("style","visibility: hidden");
	//hiddendivCloned.setAttribute("id",id+"_Index");
	//hiddendivCloned.setAttribute("querynum",NumQuery-1);
	//hiddendivCloned.setAttribute("style","visibility: hidden");*/
	win.document.getElementById("separator").appendChild(hidediv);

	d3.select(win.document.getElementById("separator"))
	.append("hr")
	.attr("class", "hr")
	.attr("id", id + "_line")
	.attr("width", "100%");

	function btndivclick() {
		alert("clicks");
	}

	function mousemove(d) {
		div1
		.text("Info about " + d[0] + " : " + d[1] + " : " + d[2] / 1000)
		.style("left", (d3.event.pageX ) + "px")
		.style("top", (d3.event.pageY) + "px");
	}

	function mouseover() {
		div1.transition()
		.duration(300)
		.style("opacity", 1);
	}

	function mouseout() {
		div1.transition()
		.duration(300)
		.style("opacity", 1e-6);
	}

}
