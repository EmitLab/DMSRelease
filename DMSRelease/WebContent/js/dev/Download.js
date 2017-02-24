/**
 Download query data, entire variate set
 */
function QueryDownloadData(id) {

	console.log(id);
	
	var m = id.replace("#", "");
	var inputs = new Array();
	inputs = m.split("_");
	var model=inputs[1];
	var metric=inputs[2];
	var simidname=inputs[inputs.length -1];
	var modelname="Model=" +inputs[1]+",";
	var metricname="Metric=" +inputs[2]+",";
	var simid="SimID=" +inputs[inputs.length -1]+",";




	var ts = "Ts=,";
	var te = "Te=,";
	var ds = "DS=,";
	var zone = "Zones=,";
	var collection = "CN=" + inputs[0];
	var query = ts + te + modelname + simid + metricname + zone + ds + collection;

	$.ajax({
		type: "POST",
		url : "/DMS/RetrieveDataFromMongo",
		data : {"project": Project, "query" : query, "type":"complete"},
		dataType : "text",
		success : function(data){
			console.log('Success');
			// var Datarecived = reqMongo.responseText;
			var Datarecived = data.split("@@@")[0];
			var jsonData = JSON.parse(Datarecived);
			var csvData = JSON2CSV(jsonData);
			var zip = new JSZip();
			//zip.file(model + "_" + metric + "_" + simidname + ".csv", Datarecived);
			zip.file(model + "_" + metric + "_" + simidname + ".csv", csvData);
			var pom = document.createElement('a');
			pom.setAttribute('href', "data:application/zip;base64," + zip.generate());
			var downloadFile = 'dataDownLoad' + '_' +inputs[1]+'_'+inputs[2]+'_'+inputs[inputs.length -1] + '.zip';
			pom.setAttribute('download', downloadFile);
			document.body.appendChild(pom);
			pom.click();
			document.body.removeChild(pom);
		},
		error : function(){
			console.log("Error");
		}
	});
}

function JSON2CSV(jsonDataAll){
	var jsonData = jsonDataAll.Data;
	var variateSize = jsonData.length;
	var jsonDPS = jsonData[0].dps;
	var dpsLength = Object.keys(jsonDPS).length;
	var csvData='';
	// 1. gt the time ina  key array
	// 2. get the variate in the variate array
	// var csvfile() 
	// columnname= timearray from 1 to timearray.lenght+1
	//for i= 0 to variatearray.lenght
	// read the variate with the specific state
	// element[i+1][0]  
	// for j =0 to timearray.lenght

	//what are you doing?, to explain what I did? Or try to speed up?  

	// Trying to write you a pesudocode that explay what to do adn to speedup the code
	//ToCSV

	var variateLabels=new Array();
	for(k=0;k<variateSize;k++){
		var zone = jsonData[k].zone;
		variateLabels.push(zone);
	}
	var len = variateLabels.length;
	var indices = new Array(len);
	for (var i = 0; i < len; ++i) {
		indices[i] = i;
	}
	indices.sort(function (a, b) {
		return variateLabels[a] < variateLabels[b] ? -1 : variateLabels[a] > variateLabels[b] ? 1 : 0;
	});



	for(i=-1;i<variateSize;i++){
		var line = '';
		if(i == -1){
			line = line + 'Data,';
			for(var key in jsonData[0].dps){
				var date = timeConverter(key);
				line = line + date + ',';
			}
		}
		else{
			variateID = indices[i];
			var zone = jsonData[variateID].zone;
			line = line + zone + ',';
			for(var key in jsonData[variateID].dps){
				line = line + jsonData[variateID].dps[key] + ',';
			}
		}
		line = line + '\r\n';
		csvData = csvData + line;
	}
	/*console.log("variate size: " + variateSize);
	console.log("zone: " + jsonData[0]);
	console.log("zone: " + jsonData[0].zone);
	console.log(jsonDPS);
	console.log("dpsLength: " + dpsLength);*/
	return csvData;
}

function timeConverter(UNIX_timestamp){
	var a = new Date(UNIX_timestamp*1);
	var months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
	var year = a.getFullYear();
	var month = months[a.getMonth()];
	var date = a.getDate();
	var hour = a.getHours();
	var min = a.getMinutes();
	var sec = a.getSeconds();
	var time = date + ' ' + month + ' ' + year + ' ' + hour + ':' + min + ':' + sec ;
	return time;
}


