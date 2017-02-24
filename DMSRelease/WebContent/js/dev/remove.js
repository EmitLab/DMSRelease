/**
 * 
 */
function removeElement(id, check) {
	//document.getElementById(elementId).r.removeChild(oldChild)
	win.document.getElementById("separator").removeChild(win.document.getElementById(id));
	win.document.getElementById("separator").removeChild(win.document.getElementById(id + "_line"));
	win.document.getElementById("separator").removeChild(win.document.getElementById(id + "_remove"));
	win.document.getElementById("separator").removeChild(win.document.getElementById(id + "_tooltip"));
	win.document.getElementById("separator").removeChild(win.document.getElementById(id + "_colorchange"));
	win.document.getElementById("separator").removeChild(win.document.getElementById(id + "_metadownload"));
	win.document.getElementById("separator").removeChild(win.document.getElementById(id + "_datadownload"));
	win.document.getElementById("separator").removeChild(win.document.getElementById(id + "_Title1"));
	win.document.getElementById("separator").removeChild(win.document.getElementById(id + "_Index"));

	win.document.getElementById("separator").removeChild(win.document.getElementById(id + "_greyscaleid"));

	win.document.getElementById("separator").removeChild(win.document.getElementById(id + "_lablechackbox"));
	if(Project=="epidemic"){
		win.document.getElementById("separator").removeChild(win.document.getElementById(id + "table_Title1"));
	}
	win.document.getElementById("separator").removeChild(win.document.getElementById(id + "table_Title"));

	win.document.getElementById("separator").removeChild(win.document.getElementById(id + "_br"));

	$(id).removeClass("selected");
	$(id).parents().removeClass("selected");


	var listsvg = new Array();
	listsvg = win.document.getElementsByTagName("svg");

	if (listsvg.length == 1 && check == true) {
		win.close();
		colorSchemeNW = null;
	}
	if (check == true) {
		HighlightDifferences();
		//     greyScaleCompareSVG();
	}

	highlight();

}
