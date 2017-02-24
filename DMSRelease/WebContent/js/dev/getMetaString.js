/**
 *  Converts the metadata list into human, a.k.a Candan, readable string
 */

function getMetaString(metaJSON){
	console.log(metaJSON);
	var metaDataList = Object.keys(metaJSON);
	var metaDataString = '';
	var simId = '';
	var property = '';
	var model = '';
	for (var mList = 0; mList < metaDataList.length; mList++) {
		
		if (metaDataList[mList] != 'Index'){
			if (metaDataList[mList] == 'SimID'){
				simid = metaJSON[metaDataList[mList]] + '> ';
			}
			if (metaDataList[mList] == 'Model'){
				model = metaJSON[metaDataList[mList]] + ', ';
			}
			if (metaDataList[mList] == 'Metric' ){
				property = metaJSON[metaDataList[mList]] + ', ';
			}
			if (metaDataList[mList] != 'SimID' && metaDataList[mList] != 'Model' && metaDataList[mList] != 'Metric'){
				metaDataString = metaDataString + metaDataList[mList] + ' : ' + metaJSON[metaDataList[mList]] + ', ';
			}
		}
	}
	metaDataString = simid + model + property + metaDataString;
	//var metaDIV = document.createElement("div");
	var metaDivText ="";
	/*
	if(Project == "epidemic"){
		metaDivText = metaDataString.replace("Metric", "Property");
	}else if(Project == "energy"){
		metaDivText = metaDataString.replace("Metric", "Measure");
	}
	return metaDivText;*/
	return metaDataString;
}