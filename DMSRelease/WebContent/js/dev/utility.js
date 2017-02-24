
function checkID(idm) {
	if (win != null) {
		var listsvg = new Array();
		listsvg = win.document.getElementsByTagName("svg");
		for (var i = 0; i < listsvg.length; i++) {
			if (listsvg[i].id == idm) {
				return true;
			}
		}
	}
	return false;
}


function checknumberofSelected(checked) {
	var listcheck = new Array();
	console.log("check function");
	listcheck = win.document.getElementsByName('check');
	var checknumber = 0;
	for (var i = 0; i < listcheck.length; i++) {
		if (listcheck[i].checked)
			checknumber++;
	}
	console.log(checked + "    num: " + listcheck.length);
	if (checknumber > 2) {

		var c = win.document.getElementById(checked);
		c.checked = false;
		alert("You cannot select more than two simulations");
	}

}
