function highlight() {
	var listsvg = new Array();
	listsvg = win.document.getElementsByTagName("svg");
	for (var i = 1; i < listsvg.length; i++) {
		var id1 = listsvg[i].id;
		$(id1).removeClass("searched");
		$(id1).parents().removeClass("searched");
		$(id1).addClass("selected");
		$(id1).parents().addClass("selected");
	}
}


function HighlightDifferences() {
	var listsvg = new Array();
	listsvg = win.document.getElementsByTagName("svg");
	for (var i = 1; i < listsvg.length; i++) {
		var id1 = listsvg[i].id;
		console.log('ID -' + id1);
		console.log(Project);
		if(Project.toLowerCase() === "epidemic"){
			console.log('In epidemic');
			
			if (id1) {
				var styleStr = "font-weight:bold; font-size:14px; font-family:arial; background-color:white; color:black;";
				$("#\\" + id1 + "_Title_Simid", win.document).attr("style", styleStr);
				$("#\\" + id1 + "_Title_graph", win.document).attr("style", styleStr);
				$("#\\" + id1 + "_Title_Property", win.document).attr("style", styleStr);
				$("#\\" + id1 + "_Title_Model", win.document).attr("style", styleStr);
				$("#\\" + id1 + "_Title_immunityLossRate", win.document).attr("style", styleStr);
				$("#\\" + id1 + "_Title_infectionMortalityRate", win.document).attr("style", styleStr);
				$("#\\" + id1 + "_Title_infector", win.document).attr("style", styleStr);
				$("#\\" + id1 + "_Title_incubationRate", win.document).attr("style", styleStr);
				$("#\\" + id1 + "_Title_deathRate", win.document).attr("style", styleStr);
				$("#\\" + id1 + "_Title_recoveryRate", win.document).attr("style", styleStr);
				$("#\\" + id1 + "_Title_transmissionRate", win.document).attr("style", styleStr);
				$("#\\" + id1 + "_Title_birthRate", win.document).attr("style", styleStr);
			}
			for (var i = 1; i < listsvg.length; i++) {
				for (var j = 2; j < listsvg.length; j++) {
					//////console.log(i+"=i   j="+ j+"   size: "+ listsvg.length );
					var id1 = listsvg[i].id;
					var id2 = listsvg[j].id;
					
					var warningStr = "font-weight:bold; font-size:14px; font-family:arial; background-color:lightgrey; color:red;";
					
					if (id1 != "" && id2 != "") {

						if ($("#\\" + id1 + "_Title_Simid", win.document).text() !== $("#\\" + id2 + "_Title_Simid", win.document).text()) {
							//	//console.log("change the colour of the text");
							$("#\\" + id1 + "_Title_Simid", win.document).attr("style", warningStr);
							$("#\\" + id2 + "_Title_Simid", win.document).attr("style", warningStr);
						}
						if ($("#\\" + id1 + "_Title_graph", win.document).text() !== $("#\\" + id2 + "_Title_graph", win.document).text()) {
							//	//console.log("change the colour of the text");
							$("#\\" + id1 + "_Title_graph", win.document).attr("style", warningStr);
							$("#\\" + id2 + "_Title_graph", win.document).attr("style", warningStr);
						}
						if ($("#\\" + id1 + "_Title_Property", win.document).text() !== $("#\\" + id2 + "_Title_Property", win.document).text()) {
							//console.log("change the colour of the text");
							$("#\\" + id1 + "_Title_Property", win.document).attr("style", warningStr);
							$("#\\" + id2 + "_Title_Property", win.document).attr("style", warningStr);
						}
						if ($("#\\" + id1 + "_Title_Model", win.document).text() !== $("#\\" + id2 + "_Title_Model", win.document).text()) {
							//	//console.log("change the colour of the text");
							$("#\\" + id1 + "_Title_Model", win.document).attr("style", warningStr);
							$("#\\" + id2 + "_Title_Model", win.document).attr("style", warningStr);
						}
						//secondTable
						if ($("#\\" + id1 + "_Title_immunityLossRate",  win.document).text() !== $("#\\" + id2 + "_Title_immunityLossRate",  win.document).text()) {
							//	//console.log("change the colour of the text");
							$("#\\" + id1 + "_Title_immunityLossRate",  win.document).attr("style", warningStr);
							$("#\\" + id2 + "_Title_immunityLossRate",  win.document).attr("style", warningStr);
						}
						if ($("#\\" + id1 + "_Title_infectionMortalityRate",  win.document).text() !== $("#\\" + id2 + "_Title_infectionMortalityRate",  win.document).text()) {
							//	//console.log("change the colour of the text");
							$("#\\" + id1 + "_Title_infectionMortalityRate",  win.document).attr("style", warningStr);
							$("#\\" + id2 + "_Title_infectionMortalityRate",  win.document).attr("style", warningStr);
						}
						if ($("#\\" + id1 + "_Title_infector",  win.document).text() !== $("#\\" + id2 + "_Title_infector",  win.document).text()) {
							//	//console.log("change the colour of the text");
							$("#\\" + id1 + "_Title_infector",  win.document).attr("style", warningStr);
							$("#\\" + id2 + "_Title_infector",  win.document).attr("style", warningStr);
						}
						if ($("#\\" + id1 + "_Title_incubationRate",  win.document).text() !== $("#\\" + id2 + "_Title_incubationRate",  win.document).text()) {
							//	//console.log("change the colour of the text");
							$("#\\" + id1 + "_Title_incubationRate",  win.document).attr("style", warningStr);
							$("#\\" + id2 + "_Title_incubationRate",  win.document).attr("style", warningStr);
						}
						if ($("#\\" + id1 + "_Title_deathRate",  win.document).text() !== $("#\\" + id2 + "_Title_deathRate",  win.document).text()) {
							//	//console.log("change the colour of the text");
							$("#\\" + id1 + "_Title_deathRate",  win.document).attr("style", warningStr);
							$("#\\" + id2 + "_Title_deathRate",  win.document).attr("style", warningStr);
						}
						if ($("#\\" + id1 + "_Title_recoveryRate",  win.document).text() !== $("#\\" + id2 + "_Title_recoveryRate",  win.document).text()) {
							//	//console.log("change the colour of the text");
							$("#\\" + id1 + "_Title_recoveryRate",  win.document).attr("style", warningStr);
							$("#\\" + id2 + "_Title_recoveryRate",  win.document).attr("style", warningStr);
						}
						if ($("#\\" + id1 + "_Title_transmissionRate",  win.document).text() !== $("#\\" + id2 + "_Title_transmissionRate",  win.document).text()) {
							//	//console.log("change the colour of the text");
							$("#\\" + id1 + "_Title_transmissionRate",  win.document).attr("style", warningStr);
							$("#\\" + id2 + "_Title_transmissionRate",  win.document).attr("style", warningStr);
						}
						if ($("#\\" + id1 + "_Title_birthRate",  win.document).text() !== $("#\\" + id2 + "_Title_birthRate",  win.document).text()) {
							//	//console.log("change the colour of the text");
							$("#\\" + id1 + "_Title_birthRate",  win.document).attr("style", warningStr);
							$("#\\" + id2 + "_Title_birthRate",  win.document).attr("style", warningStr);
						}
						/*
                             if(win.document.getElementById(id1+"_Title_Simid").textContent != win.document.getElementById(id2+"_Title_Simid").textContent){

                             win.document.getElementById(id1+"_Title_Simid").setAttribute("style", warningStr);
                             win.document.getElementById(id2+"_Title_Simid").setAttribute("style", warningStr);
                             }
                             if(win.document.getElementById(id1+"_Title_ Metric").textContent != win.document.getElementById(id2+"_Title_ Metric").textContent){
                             ////console.log("change the colour of the text");
                             win.document.getElementById(id1+"_Title_ Metric").setAttribute("style", warningStr);
                             win.document.getElementById(id2+"_Title_ Metric").setAttribute("style", warningStr);
                             }
                             if(win.document.getElementById(id1+"_Title_ Model").textContent != win.document.getElementById(id2+"_Title_ Model").textContent){

                             win.document.getElementById(id1+"_Title_ Model").setAttribute("style", warningStr);
                             win.document.getElementById(id2+"_Title_ Model").setAttribute("style", warningStr);
                             }

						 */
					}
				}

			}

		}else if(Project=="energy"){
			/* Energy*/

			win.document.getElementById(id1+"_Title_Simid").setAttribute("style", "font-weight:bold; font-size:14px; font-family:arial; background-color:white; color:black;");
			win.document.getElementById(id1+"_Title_Measure").setAttribute("style", "font-weight:bold; font-size:14px; font-family:arial; background-color:white; color:black;");
			win.document.getElementById(id1+"_Title_Model").setAttribute("style", "font-weight:bold; font-size:14px; font-family:arial; background-color:white; color:black;");

			for (var i = 1; i < listsvg.length; i++) {
				for (var j = 2; j < listsvg.length; j++) {
					//////console.log(i+"=i   j="+ j+"   size: "+ listsvg.length );
					var id1 = listsvg[i].id;
					var id2 = listsvg[j].id;

					if (win.document.getElementById(id1 + "_Title_Simid").textContent != win.document.getElementById(id2 + "_Title_Simid").textContent) {
						win.document.getElementById(id1 + "_Title_Simid").setAttribute("style", "font-weight:bold; font-size:14px; font-family:arial; background-color:lightgrey; color:red;");
						win.document.getElementById(id2 + "_Title_Simid").setAttribute("style", "font-weight:bold; font-size:14px; font-family:arial; background-color:lightgrey; color:red;");
					}
					if (win.document.getElementById(id1 + "_Title_Measure").textContent != win.document.getElementById(id2 + "_Title_Metric").textContent) {
						////console.log("change the colour of the text");
						win.document.getElementById(id1 + "_Title_Measure").setAttribute("style", "font-weight:bold; font-size:14px; font-family:arial; background-color:lightgrey; color:red;");
						win.document.getElementById(id2 + "_Title_Measure").setAttribute("style", "font-weight:bold; font-size:14px; font-family:arial; background-color:lightgrey; color:red;");
					}
					if (win.document.getElementById(id1 + "_Title_Model").textContent != win.document.getElementById(id2 + "_Title_Model").textContent) {
						win.document.getElementById(id1 + "_Title_Model").setAttribute("style", "font-weight:bold; font-size:14px; font-family:arial; background-color:lightgrey; color:red;");
						win.document.getElementById(id2 + "_Title_Model").setAttribute("style", "font-weight:bold; font-size:14px; font-family:arial; background-color:lightgrey; color:red;");
					}
				}
			}
		}
	}
}
