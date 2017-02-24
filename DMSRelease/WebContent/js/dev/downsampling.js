function performDS() {
	var listsvg = new Array();

	var ts, te, ds = '';

	//Setting START DATE
	if (win.document.getElementById("startCheck").checked) {
		var startDate = win.document.getElementById("startDate").value;
		var startTime = win.document.getElementById("startTime").value;

		var dateArr = startDate.split("-");
			ts = "Ts=" + dateArr[1] + "/" + dateArr[2] + "/" + dateArr[0] + " " + startTime + ",";
	} else {
		ts = "Ts=,";
	}

	//Setting END DATE
	if (win.document.getElementById("endCheck").checked) {
		var endDate = win.document.getElementById("endDate").value;
		var endTime = win.document.getElementById("endTime").value;

		dateArr = endDate.split("-");
		te = "Te=" + dateArr[1] + "/" + dateArr[2] + "/" + dateArr[0] + " " + endTime + ",";
	} else {
		te = "Te=,";
	}

	//Setting SAMPLING Factor, Unit and Function
	if (win.document.getElementById("samplingCheck").checked) {
		var sampleFactor = win.document.getElementById("sampleFactor").value;

		var selectMenu = win.document.getElementById("sampleUnit");
		var sampleUnit = selectMenu.options[selectMenu.selectedIndex].getAttribute("key");

		ds = "DS=" + sampleFactor + "-" + sampleUnit;

		selectMenu = win.document.getElementById("sampleFunction");
		var sampleFunction = selectMenu.options[selectMenu.selectedIndex].getAttribute("key");

		if(System =="mongodb")
			ds = ds + "-" + sampleFunction + ",";
		else if (System =="opentsdb")
			ds = ds.replace("-","") + "-" + sampleFunction + ",";
	} else {
		ds = "DS=,";
	}

	console.log("DS: "+ ds);
	var Metric, Model, SimID, collection, zone;
	listsvg = win.document.getElementsByTagName("svg"); 
	for (var i = 1; i < listsvg.length; i++) {
		//   console.log(listsvg[i].id);
		if (listsvg[i].id == "") {
			var node = listsvg[i].parentNode;
			node.removeChild(listsvg[i]);
			i = i - 1;
		}
	}
	console.log(listsvg.length+"  in the function performDS()");
	for (var i = 1; i < listsvg.length; i++) {
		////////console.log("change content of the widow: "+ listsvg[i].id);
		var idlocalnewW = listsvg[i].id;
		var idlocheatmap = idlocalnewW.replace("#", "");
		var index = win.document.getElementById(idlocalnewW + "_Index").getAttribute("value");
		//     console.log(idlocalnewW + "_Index    "+index);
		var data = Datain2win[(win.document.getElementById(idlocalnewW + "_Index").getAttribute("value"))].Data;
		//    console.log(data);
		var numDataPoints = data.length;
		var variateLabels = '';
		for (var j = 0; j < numDataPoints; j++) {
			if (j < numDataPoints - 1){
				if (System=="mongodb"){
					variateLabels = variateLabels + data[j].zone + "|";
				}else if (System == "opentsdb"){
					if(Project=="epidemic"){
						variateLabels = variateLabels + data[j].tags.state + "|";
					}else if(Project=="energy"){
						variateLabels = variateLabels + data[j].tags.zone+ "|";
					}
				}
			}else{
				if (System=="mongodb"){
					variateLabels = variateLabels + data[j].zone;
				}else if (System == "opentsdb"){
					if(Project=="epidemic"){
						variateLabels = variateLabels + data[j].tags.state;
					}else if(Project=="energy"){
						variateLabels = variateLabels + data[j].tags.zone;
					}
				}
			}
			//alert(data[i].zone);
		}

		//////console.log("idlocal: "+idlocalnewW);
		inputs = idlocheatmap.split("_");
		collection = "CN=" + inputs[0];
		SimID = "SimID=" + inputs[inputs.length - 1] + ",";
		Model = "Model=" + inputs[1] + ",";
		Metric = "Metric=" + inputs[2] + ",";
		zone = "Zones=" + variateLabels + ",";
		//to grep zones use index and go into the arraylist to read the zone attribute.
		var query = ts + te + Model + SimID + Metric + zone + ds + collection; //miss zones
		var reqMongo = null;
		//query the simlation
		var MongoQuery = "/MVTSDB/ServletController?ClusterRequest=downsampling&project="+Project+"&System="+System+"&query=" + query;//"https://hive.asu.edu:8443
		//  var MongoQuery = 'https://hive.asu.edu:8443/Ebola-Project/Filtering?query=' + query;	//https://hive.asu.edu:8443 

		
		$.ajax({
			type : "POST",
			url : "/DMS/RetrieveDataFromMongo",
			data : {project : Project, System : System, query : query},
			dataType : "text",
			success : function(data){
				console.log(data);
				var mongoData = data.split("@@@")[0];
				var Datarecived = JSON.parse(mongoData);
				Datain2win[(win.document.getElementById(idlocalnewW + "_Index").getAttribute("value"))].Data = Datarecived.Data;
			},
			error : function(){
				console.log("Error in filtering");
			}
		});

		console.log("with ds: " + MongoQuery);
		/*
		var xmlhttp;
		if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
			reqMongo = new XMLHttpRequest();
		}
		else {// code for IE6, IE5
			reqMongo = new ActiveXObject("Microsoft.XMLHTTP");
		}

		reqMongo.onreadystatechange = function () {
			if (reqMongo.readyState == 4 && reqMongo.status == 200) {
				var Datarecived = JSON.parse(reqMongo.responseText);
				//  console.log("datarec: "+ Datarecived);
				Datain2win[(win.document.getElementById(idlocalnewW + "_Index").getAttribute("value"))].Data = Datarecived.Data;
			}
		}
		reqMongo.open("GET", MongoQuery, false);
		reqMongo.send(null);
		*/
	}
	//call the new function to reprint the elements
	/*
	 * Update the min and the max of the simulations
	 * */
	Changecolorwin();
}
