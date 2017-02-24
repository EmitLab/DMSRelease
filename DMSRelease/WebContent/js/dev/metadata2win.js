//you are calling this and all metadata are successfully retrived
function QueryforAllMetadata(parastruct, id) {
	console.log("BASIC -- " + id);
	//Please you need to check the parastruct object it should contain the timeseries data XILUN created this structure
	var m = id.replace("#", "");
	var inputs = new Array();
	inputs = m.split("_");
	var BaseXInputs="";
	if(Project.toLowerCase() =="epidemic"){
		BaseXInputs = "where $p/project/@name = " + inputs[inputs.length - 1] + " and $a/@model = \"" + inputs[1] + "\";" + inputs[2];
	}else if(Project.toLowerCase() =="energy"){
		BaseXInputs = "where $p/project/@name = " + inputs[inputs.length - 1] + " and $a/@model = \"" + inputs[1] + "\";" + inputs[2];
	}
	//var SednaQuery = 'https://hive.asu.edu:8443/MVTSDB/ServletController?ClusterRequest=metadata&project='+Project+'&where=' + SednaInputs;	//https://hive.asu.edu:8443
	
	//console.log('Sedna Inputs - ' + SednaInputs);
	$.ajax({
		type: "POST",
		url : "/DMS/Metadata" + '?where=' + BaseXInputs,
		dataType : "text",
		success : function(data){
			if(!checkID(id)){
				enlargeGraph(parastruct, id, data); //after the metadata you call the second window generation 
				HighlightDifferences();//highlight difference in the metadata in second window
				highlight();// this is refered to the hierarchical highlith fo the element that are present in the second window
			} else {
				alert("Simulation already added.");
			}
		},
		error : function(){
			console.log("OOPS");
		}
	});
}

function QueryDownload(id) {
	var m = id.replace("#", "");
	var listSim;
	var simids = new Array();
	//var listSim= (document.getElementById(m).ch);//.simlist;

	listSim = $(id).children('ul');
	//alert(listSim.length);
	if (listSim.length == 0) {
		var p = new Array();
		p = id.split("_");
		simids[0] = p[p.length - 1];
	}
	else {
		//	alert(listSim.tagName);
		simids = listSim.attr("simlist").split("_");
	}
	//var listSim = ulNode.getAttribute()
	var uniquesimids = simids.filter(function (item, i, ar) {
		return ar.indexOf(item) === i;
	});
	var inputsimids = "";

	for (var i = 0; i < uniquesimids.length; i++) {
		if (i < (uniquesimids.length - 1)) {
			inputsimids = inputsimids + uniquesimids[i] + "_";
		} else {
			inputsimids = inputsimids + uniquesimids[i];
		}
	}
	var inputs = new Array();
	inputs = m.split("_");
	
	var XMLInputs = "project="+Project+"&"+"SimId=\"" + inputsimids + "\"&ModelName=\"" + inputs[1] + "\"&Metric=\"" + inputs[2] + "\"";
	var XMLQuery = 'http://localhost:8080/DMS/DownloadMetaData?' + XMLInputs;
	// var XMLQuery = 'https://hive.asu.edu:8443/DMS/DownloadMetaData?' + XMLInputs;
	var xmlhttp;
	if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
		reqBaseX = new XMLHttpRequest();
	}
	else {// code for IE6, IE5
		reqBaseX = new ActiveXObject("Microsoft.XMLHTTP");
	}
	reqBaseX.onreadystatechange = function () {
		if (reqBaseX.readyState == 4 && reqBaseX.status == 200) {
			var contentFile = reqBaseX.responseText;
			var files = contentFile.split("End_File");
			var zip = new JSZip();
			for (var i = 0; i < files.length - 1; i++) {
				zip.file(inputs[1] + "_" + inputs[2] + "_" + uniquesimids[i] + ".xml", files[i] + "/n");
			}

			var pom = document.createElement('a');

			pom.setAttribute('href', "data:application/zip;base64," + zip.generate());
			var downloadFile = 'metaDownLoad' + '_' +inputs[1]+'_'+inputs[2]+'_centroid_'+inputs[inputs.length -1] + '.zip';
			pom.setAttribute('download', downloadFile);
			document.body.appendChild(pom);
			pom.click();
			document.body.removeChild(pom);
		}

	}
	reqBaseX.open("GET", XMLQuery);
	reqBaseX.send(null);
}



function xmlDifferences() {
	var listcheck = new Array();
	listcheck = win.document.getElementsByName('check');
	var checknumber = 0;
	var nameofChaked = new Array();
	for (var i = 0; i < listcheck.length; i++) {
		if (listcheck[i].checked) {
			checknumber++;
			nameofChaked.push(listcheck[i].id);
		}
	}
	if (checknumber != 2) {
		alert("You need to select two simulations ");
	} else {
		var sim1 = nameofChaked[0];//listcheck[0].id; 
		sim1 = sim1.replace("#", "");
		sim1 = sim1.replace("_chackbox", "");
		var sim2 = nameofChaked[1];//listcheck[1].id;
		sim2 = sim2.replace("#", "");
		sim2 = sim2.replace("_chackbox", "");


		if (comparewindow != null)
			comparewindow.close();

		var comparewindow = null;
		// comparewindow = window.open("https://hive.asu.edu:8443/DMS/jsp/MetadataComparison.jsp?input=" + sim1 + ";" + sim2, "Compare XML");
		comparewindow = window.open("http://localhost:8080/DMS/jsp/MetadataComparison.jsp?input=" + sim1 + ";" + sim2, "Compare XML");
	}
}