/**
 * @author Sicong and Reece
 * 
 * This function is called onchange of select csv button 
 * to extract the file's information.
 * */

function getFileInfo() {
	console.log("Inside filereader!!!!!!!");
	var fileInput = document.getElementById('fileInput');
	//var filemeta = document.getElementById('fileMeta');
	var filegraph = document.getElementById('fileGraph');
	//var filefeature = document.getElementById('fileFeature');
	var fileDisplayArea = document.getElementById('fileDisplayArea');
	var file ="";
	file= fileInput.files[0];
	var name = file.name.split(".")[1];

	if (name == "csv"){
		var reader = new FileReader();
		reader.onload = function(e) {
			load = reader.result;	// load contains csv raw data
			$.ajax({
				type: "POST",
				url:"http://localhost:8080/MVTSDB/FromCSVtoJson",
				data: {jsonData : JSON.stringify({"csvfile":load})},
				dataType: "json",
				success:function(content){
					var heatmapjson = content.data;
					var dataMax     = content.max;
					var dataMin     = content.min;
					console.log(JSON.parse(heatmapjson).Data[0].zone);
					var obj = {Data : []};
					var queryVariate1 = InterestedVariate;
					queryVariate1 = queryVariate1.replace(/,/g, ";");
					var queryVariate = queryVariate1 + ';';
					queryVariate = queryVariate.replace(";;", "");
					queryVariate = queryVariate.split(";");
					if(modifier == 0)
					{
						console.log("INSIDE IF STATEMENT");
						queryVariate = (JSON.parse(heatmapjson));
						//queryVariate = [];
						//queryVariate[0] = "AL";
						var counter = 0;
						for(var i = 0; i < Object.keys(JSON.parse(heatmapjson).Data).length; i++)
						{
							for(var j = 0; j < queryVariate.length; j++)
							{
								if(JSON.parse(heatmapjson).Data[i].zone == queryVariate[j])
								{
									console.log("JSON Object newwww" + JSON.parse(heatmapjson).Data[i].zone);
									obj.Data[counter] = JSON.parse(heatmapjson).Data[i];
									counter++;
								};
							};
						}
						//console.log("Object!!!!!!!!!!!!" + obj.Data[0].zone);
						console.log("JSON Object" + Object.keys(JSON.parse(heatmapjson).Data).length);
						similarityHeatmap("#queryheatmap",JSON.parse(heatmapjson) , dataMin, dataMax);					
					}
					else
					{
						console.log("INSIDE ELSE STATEMENT");
						//queryVariate = (JSON.parse(heatmapjson))
						//queryVariate = [];
						//queryVariate[0] = "AL";
						var counter = 0;
						for(var i = 0; i < Object.keys(JSON.parse(heatmapjson).Data).length; i++)
						{
							for(var j = 0; j < queryVariate.length; j++)
							{
								if(JSON.parse(heatmapjson).Data[i].zone == queryVariate[j])
								{
									console.log("JSON Object newwww" + JSON.parse(heatmapjson).Data[i].zone);
									obj.Data[counter] = JSON.parse(heatmapjson).Data[i];
									counter++;
								};
							};
						}
						//console.log("Object!!!!!!!!!!!!" + obj.Data[0].zone);
						console.log("JSON Object" + Object.keys(JSON.parse(heatmapjson).Data).length);
						similarityHeatmap("#queryheatmap",obj , dataMin, dataMax);
					}
					
				}
				
			});
		};
		reader.readAsText(file);
	} else {
		fileDisplayArea.innerText = "File not supported!";
		load = "Error Load !";
	}
}


/*	filemeta.addEventListener('change', function(e) {
				if(this.id == "fileMeta"){
					file= filemeta.files[0];
					var textType = /xml.;
					if (file.type.match(textType)) {
						var reader = new FileReader();
						reader.onload = function(e) {
							console.log("meta");
							metadataFile = reader.result;
							console.log(metadataFile);
						}
						reader.readAsText(file);	
					} else {
						fileDisplayArea.innerText = "File not supported!";
						load = "Error Load !";
					}
				}
			});	
 */

function getGraphFileInfo() {
	console.log("readerfile on change");
	var file ="";
	var filegraph = document.getElementById('fileGraph');
	file= filegraph.files[0];
	var name = file.name.split(".")[1];
	if (name == "csv"){					
		var reader = new FileReader();
		reader.onload = function(e) {
			graphFile=reader.result;	
			$.ajax({
				type: "POST",
				url:"http://localhost:8080/MVTSDB/LocationMatrixUpload",
				data: {jsonData : JSON.stringify({"locationMatrix":graphFile})},
				dataType: "json",
				success:function(content){
					console.log("Sent location csv successfully");
				}
			});
			
			
		};
		reader.readAsText(file);
		changePage(1);
	}else {
		fileDisplayArea.innerText = "File not supported!";
		load = "Error Load !";
	}

}

/*filefeature.addEventListener('change', function(e) {
				if(this.id == "fileFeature"){
					file= filefeature.files[0];
					Papa.parse(file, {
						header: false,
					    complete: function(results) {
					    	ParsedCSV = results;
					    	console.log(ParsedCSV);
					    	PrintGrayscale(null);
					    	}
					    });

					//console.log(ParsedCSV);
				}
			});		
 */