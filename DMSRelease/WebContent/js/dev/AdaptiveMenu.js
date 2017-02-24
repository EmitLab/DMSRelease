/**
 * 
 */

function Changemenu(){


	var selects= document.getElementById("grayScaleTypeMenu");
	var selectedValue = selects.options[selects.selectedIndex].getAttribute("key");
	if(selectedValue == "1"){

		var tdcurvdog = document.getElementById("TDcurvdog");
		// tdcurvdog.removeAttribute("width");
		tdcurvdog.setAttribute("style", "visibility:hidden");
		var tdselectcurvdog= document.getElementById("SelectTDcurvdog");
		tdselectcurvdog.setAttribute("style", "visibility:hidden");
		tdselectcurvdog.removeChild(document.getElementById("selectcurvdog"))
		//	 tdcurvdog.removeChild(document.getElementById("brtdcurv"));

		var select = document.createElement("select");
		select.setAttribute("id", "grayScaleNormalizationMenu");

		var option = document.createElement("option");
		option.setAttribute("key", "1");
		option.setAttribute("selected","selected");
		option.innerHTML="Hierarchy";
		select.appendChild(option);

		option = document.createElement("option");
		option.setAttribute("key", "2");
		option.innerHTML="Simulation";
		select.appendChild(option);

		option = document.createElement("option");
		option.setAttribute("key", "3");
		option.innerHTML="Variate";
		select.appendChild(option);


		document.getElementById("normLabel").innerHTML = "Normalization Type";
		document.getElementById("NormSelection").removeChild(document.getElementById("grayScaleNormalizationMenu"));
		document.getElementById("NormSelection").appendChild(select);

		select = document.createElement("select");
		select.setAttribute("id", "grayScaleDifferenceMenu");

		option = document.createElement("option");
		option.setAttribute("key", "1");
		option.setAttribute("selected","selected");
		option.innerHTML="Absolute";
		select.appendChild(option);

		option = document.createElement("option");
		option.setAttribute("key", "2");
		option.innerHTML="Percentage";
		select.appendChild(option);

		document.getElementById("diffLabel").innerHTML = "Difference Type";
		document.getElementById("DiffSelection").removeChild(document.getElementById("grayScaleDifferenceMenu"));
		document.getElementById("DiffSelection").appendChild(select);

	}else if(selectedValue == "2"){

		var tdcurvdog = document.getElementById("TDcurvdog");
		tdcurvdog.removeAttribute("width");
		tdcurvdog.setAttribute("style", "visibility:visible");
		var select = document.createElement("select");
		select.setAttribute("id", "selectcurvdog");
		var option = document.createElement("option");

		option.setAttribute("key", "1");
		option.setAttribute("selected","selected");
		option.innerHTML="DoG";
		select.appendChild(option);

		option = document.createElement("option");
		option.setAttribute("key", "2");
		option.innerHTML="Curvature";
		select.appendChild(option);

		/* var labeldogcurv = document.createElement("label");
		 labeldogcurv.innerHTML = "Feature Calc";
		 tdcurvdog.appendChild(labeldogcurv);*/
		// var brcurv = document.createElement("br");
		// brcurv.setAttribute("id", "brtdcurv");
		// tdcurvdog.appendChild(brcurv);
		var tdselectcurvdog= document.getElementById("SelectTDcurvdog");
		tdselectcurvdog.setAttribute("style", "visibility:visible");
		tdselectcurvdog.appendChild(select);


		//show the octus and scope selection
		select = document.createElement("select");
		select.setAttribute("id", "grayScaleNormalizationMenu");

		option = document.createElement("option");
		option.setAttribute("key", "1");
		option.setAttribute("selected","selected");
		option.innerHTML="1";
		select.appendChild(option);

		option = document.createElement("option");
		option.setAttribute("key", "2");
		option.innerHTML="2";
		select.appendChild(option);

		/* option = document.createElement("option");
		 option.setAttribute("key", "3");
		 option.innerHTML="3";
		 select.appendChild(option);
		 */

		document.getElementById("normLabel").innerHTML = "Octave";
		document.getElementById("NormSelection").removeChild(document.getElementById("grayScaleNormalizationMenu"));
		document.getElementById("NormSelection").appendChild(select);

		select = document.createElement("select");
		select.setAttribute("id", "grayScaleDifferenceMenu");

		option = document.createElement("option");
		option.setAttribute("key", "1");
		option.setAttribute("selected","selected");
		option.innerHTML="1";
		select.appendChild(option);

		option = document.createElement("option");
		option.setAttribute("key", "2");
		option.innerHTML="2";
		select.appendChild(option);

		option = document.createElement("option");
		option.setAttribute("key", "3");
		option.innerHTML="3";
		select.appendChild(option);
		option = document.createElement("option");
		option.setAttribute("key", "4");
		option.innerHTML="4";
		select.appendChild(option);
		option = document.createElement("option");
		option.setAttribute("key", "5");
		option.innerHTML="5";
		select.appendChild(option);
		option = document.createElement("option");
		option.setAttribute("key", "6");
		option.innerHTML="6";
		select.appendChild(option);

		document.getElementById("diffLabel").innerHTML = "Level";
		document.getElementById("DiffSelection").removeChild(document.getElementById("grayScaleDifferenceMenu"));
		document.getElementById("DiffSelection").appendChild(select);
	}

}




function ChangemenuWind2(){


	var selects= win.document.getElementById("grayScaleTypeMenu");
//	console.log(selects.selectedIndex);
	var selectedValue = selects.options[selects.selectedIndex].getAttribute("key");
//	console.log(""+selectedValue);
	if(selectedValue == "1"){

		var tdcurvdog = win.document.getElementById("TDcurvdog");
		// tdcurvdog.removeAttribute("width");
		tdcurvdog.setAttribute("style", "visibility:hidden");
		var tdselectcurvdog= win.document.getElementById("SelectTDcurvdog");
		tdselectcurvdog.setAttribute("style", "visibility:hidden");
		var elem=win.document.getElementById("selectcurvdog");
		if(elem!= null)
			tdselectcurvdog.removeChild(elem);
		//tdcurvdog.removeChild(win.document.getElementById("brtdcurv"));

		var select = win.document.createElement("select");
		select.setAttribute("id", "grayScaleNormalizationMenu");

		/*	 var option = win.document.createElement("option");
		 option.setAttribute("key", "1");
		 option.setAttribute("selected","selected");
		 option.innerHTML="Hierarchy";
		 select.appendChild(option);
		 */ 
		option = win.document.createElement("option");
		option.setAttribute("key", "2");
		option.setAttribute("selected","selected");
		option.innerHTML="Simulation";
		select.appendChild(option);

		option = win.document.createElement("option");
		option.setAttribute("key", "3");
		option.innerHTML="Variate";
		select.appendChild(option);


		win.document.getElementById("normLabel").innerHTML = "Normalization Type";
		var elem=win.document.getElementById("grayScaleNormalizationMenu");
		if(elem!=null)
			win.document.getElementById("NormSelection").removeChild(elem);
		win.document.getElementById("NormSelection").appendChild(select);

		select = win.document.createElement("select");
		select.setAttribute("id", "grayScaleDifferenceMenu");

		option = win.document.createElement("option");
		option.setAttribute("key", "1");
		option.setAttribute("selected","selected");
		option.innerHTML="Absolute";
		select.appendChild(option);

		option = win.document.createElement("option");
		option.setAttribute("key", "2");
		option.innerHTML="Percentage";
		select.appendChild(option);

		win.document.getElementById("diffLabel").innerHTML = "Difference Type";
		win.document.getElementById("DiffSelection").removeChild(win.document.getElementById("grayScaleDifferenceMenu"));
		win.document.getElementById("DiffSelection").appendChild(select);

	}else if(selectedValue == "2"){

		var tdcurvdog = win.document.getElementById("TDcurvdog");
		tdcurvdog.removeAttribute("width");
		tdcurvdog.setAttribute("style", "visibility:visible");
		var select = win.document.createElement("select");
		select.setAttribute("id", "selectcurvdog");
		var option = win.document.createElement("option");

		option.setAttribute("key", "1");
		option.setAttribute("selected","selected");
		option.innerHTML="DoG";
		select.appendChild(option);

		option = win.document.createElement("option");
		option.setAttribute("key", "2");
		option.innerHTML="Curvature";
		select.appendChild(option);

		/* var labeldogcurv = document.createElement("label");
		 labeldogcurv.innerHTML = "Feature Calc";
		 tdcurvdog.appendChild(labeldogcurv);*/
		// var brcurv = win.document.createElement("br");
		// brcurv.setAttribute("id", "brtdcurv");
		// tdcurvdog.appendChild(brcurv);
		var tdselectcurvdog= win.document.getElementById("SelectTDcurvdog");
		tdselectcurvdog.setAttribute("style", "visibility:visible");
		tdselectcurvdog.appendChild(select);


		//show the octus and scope selection
		select = win.document.createElement("select");
		select.setAttribute("id", "grayScaleNormalizationMenu");

		option = win.document.createElement("option");
		option.setAttribute("key", "1");
		option.setAttribute("selected","selected");
		option.innerHTML="1";
		select.appendChild(option);

		option = win.document.createElement("option");
		option.setAttribute("key", "2");
		option.innerHTML="2";
		select.appendChild(option);

		/* option = document.createElement("option");
		 option.setAttribute("key", "3");
		 option.innerHTML="3";
		 select.appendChild(option);
		 */

		win.document.getElementById("normLabel").innerHTML = "Octave";
		win.document.getElementById("NormSelection").removeChild(win.document.getElementById("grayScaleNormalizationMenu"));
		win.document.getElementById("NormSelection").appendChild(select);

		select = win.document.createElement("select");
		select.setAttribute("id", "grayScaleDifferenceMenu");

		option = win.document.createElement("option");
		option.setAttribute("key", "1");
		option.setAttribute("selected","selected");
		option.innerHTML="1";
		select.appendChild(option);

		option = win.document.createElement("option");
		option.setAttribute("key", "2");
		option.innerHTML="2";
		select.appendChild(option);

		option = win.document.createElement("option");
		option.setAttribute("key", "3");
		option.innerHTML="3";
		select.appendChild(option);
		option = win.document.createElement("option");
		option.setAttribute("key", "4");
		option.innerHTML="4";
		select.appendChild(option);
		option = win.document.createElement("option");
		option.setAttribute("key", "5");
		option.innerHTML="5";
		select.appendChild(option);
		option = win.document.createElement("option");
		option.setAttribute("key", "6");
		option.innerHTML="6";
		select.appendChild(option);

		win.document.getElementById("diffLabel").innerHTML = "Level";
		win.document.getElementById("DiffSelection").removeChild(win.document.getElementById("grayScaleDifferenceMenu"));
		win.document.getElementById("DiffSelection").appendChild(select);
	}

}


/*{
	 var selects= win.document.getElementById("grayScaleComputationMenu_Win2");
	 var selectedValue = selects.options[selects.selectedIndex].getAttribute("key");
	 if(selectedValue == "1"){ // standard grayscale
		 	var tdcurvdog = win.document.getElementById("tdfeatcalcwin2");
			// tdcurvdog.removeAttribute("width");
			 tdcurvdog.setAttribute("style", "visibility:hidden");
			 tdcurvdog.removeChild(win.document.getElementById("selectcurvdog_win2"));
			 tdcurvdog.removeChild(win.document.getElementById("brtdcurv"));

			 var select = document.createElement("select");
			 select.setAttribute("id", "grayScaleNormalizationMenu_Win2");

			 var option = document.createElement("option");

			 option = document.createElement("option");
			 option.setAttribute("key", "1");
			 option.innerHTML="Simulation";
			 select.appendChild(option);

			 option = document.createElement("option");
			 option.setAttribute("key", "2");
			 option.innerHTML="Variate";
			 select.appendChild(option);


			 win.document.getElementById("Normtypewind2").innerHTML = "Normalization Type";
			 win.document.getElementById("NormType_win2").removeChild(win.document.getElementById("grayScaleNormalizationMenu_Win2"));
			 win.document.getElementById("NormType_win2").appendChild(select);

			 select = document.createElement("select");
			 select.setAttribute("id", "grayScaleDifferenceMenu_Win2");

			 option = document.createElement("option");
			 option.setAttribute("key", "1");
			 option.setAttribute("selected","selected");
			 option.innerHTML="Absolute";
			 select.appendChild(option);

			 option = document.createElement("option");
			 option.setAttribute("key", "2");
			 option.innerHTML="Percentage";
			 select.appendChild(option);

			 win.document.getElementById("differencetypewind2").innerHTML = "Difference Type";
			 win.document.getElementById("Difftype_tdwin2").removeChild(win.document.getElementById("grayScaleDifferenceMenu_Win2"));
			 win.document.getElementById("Difftype_tdwin2").appendChild(select);

			 win.document.getElementById("comparegrayscalewin2button").setAttribute("onclick",'window.opener.greyScaleCompareSVG(0);');

	 }else if(selectedValue == "2"){//Dog-Curvature grayscale
		 var tdcurvdog = win.document.getElementById("tdfeatcalcwin2");
		// tdcurvdog.removeAttribute("width");
		 tdcurvdog.setAttribute("style", "visibility:visible");
		 var select = document.createElement("select");
		 select.setAttribute("id", "selectcurvdog_win2");
		 var option = document.createElement("option");
		 option.setAttribute("key", "1");
		 option.setAttribute("selected","selected");
		 option.innerHTML="DoG";
		 select.appendChild(option);

		 option = document.createElement("option");
		 option.setAttribute("key", "2");
		 option.innerHTML="Curvature";
		 select.appendChild(option);

		 var brcurv = document.createElement("br");
		 brcurv.setAttribute("id", "brtdcurv");
		 tdcurvdog.appendChild(brcurv);
		 tdcurvdog.appendChild(select);


		 //show the octus and scope selection
		 select = document.createElement("select");
		 select.setAttribute("id", "grayScaleNormalizationMenu_Win2");

		  option = document.createElement("option");
		 option.setAttribute("key", "1");
		 option.setAttribute("selected","selected");
		 option.innerHTML="1";
		 select.appendChild(option);

		 option = document.createElement("option");
		 option.setAttribute("key", "2");
		 option.innerHTML="2";
		 select.appendChild(option);


		 win.document.getElementById("Normtypewind2").innerHTML = "Octave";
		 win.document.getElementById("NormType_win2").removeChild(win.document.getElementById("grayScaleNormalizationMenu_Win2"));
		 win.document.getElementById("NormType_win2").appendChild(select);

		 select = document.createElement("select");
		 select.setAttribute("id", "grayScaleDifferenceMenu_Win2");

		 option = document.createElement("option");
		 option.setAttribute("key", "1");
		 option.setAttribute("selected","selected");
		 option.innerHTML="1";
		 select.appendChild(option);

		 option = document.createElement("option");
		 option.setAttribute("key", "2");
		 option.innerHTML="2";
		 select.appendChild(option);

		 option = document.createElement("option");
		 option.setAttribute("key", "3");
		 option.innerHTML="3";
		 select.appendChild(option);
		 option = document.createElement("option");
		 option.setAttribute("key", "4");
		 option.innerHTML="4";
		 select.appendChild(option);
		 option = document.createElement("option");
		 option.setAttribute("key", "5");
		 option.innerHTML="5";
		 select.appendChild(option);
		 option = document.createElement("option");
		 option.setAttribute("key", "6");
		 option.innerHTML="6";
		 select.appendChild(option);

		 win.document.getElementById("differencetypewind2").innerHTML = "Level";
		 win.document.getElementById("Difftype_tdwin2").removeChild(win.document.getElementById("grayScaleDifferenceMenu_Win2"));
		 win.document.getElementById("Difftype_tdwin2").appendChild(select);

		 //button setting function
		 win.document.getElementById("comparegrayscalewin2button").setAttribute("onclick",'window.opener.greyScaleCompareSVG(1);');

	 }

}*/