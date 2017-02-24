function searchText() {
	var searchString = document.getElementById('search-string').value;
	var keywords = new Array();
	keywords = searchString.split(";");

	// If a DIV contains all keywords and SimID at the last, then it is a match
	var resultDIV = findElements(keywords);
	resultDIV = resultDIV[resultDIV.length - 1];

	if (resultDIV == null)
		alert('No matching simulation found!');
	else {
		collapseAll('hierarchy');
		expandParent(resultDIV);
	}
}

function findElements(keywords) {
	var results = Array();
	var tmp = document.getElementsByTagName('*');


	for (var i = 0; i < tmp.length; i++) {
		var countMatch = 0;
		for (var j = 0; j < keywords.length - 1; j++) {
			var DIVID = tmp[i].id;
			if (DIVID.indexOf(keywords[j]) > -1) {
				countMatch = countMatch + 1;
			}
		}

		if ((countMatch == keywords.length - 1) && (DIVID.lastIndexOf(keywords[keywords.length - 1]) == DIVID.length - keywords[keywords.length - 1].length))
			results.push(tmp[i]);
	}

	return results;
}

function expandParent(childDIV) {
	//$(childDIV.getAttribute('id')).addClass("searched");
	//$(childDIV.getAttribute('id')).parents().addClass("searched");
	var JSONDataObject = JSON.parse(result);
	var nodeID = document.getElementById(childDIV.getAttribute('id'));
	//childDIV.getAttribute('id') !=null && document.getElementById(childDIV.getAttribute('id')).hasAttribute('Index') &&
	if (nodeID.innerHTML == "") {
		//console.log("expandCreaete: "+ childDIV.getAttribute('id'));
		heatmap("#" + childDIV.getAttribute('id'), JSONDataObject[parseInt(document.getElementById(childDIV.getAttribute('id')).getAttribute('Index'))]);

		//alert(nodeID.getAttribute('id'));
		//if (nodeID.hasAttribute("member")|| nodeID.hasAttribute("member last")){
		//	alert("1");
		var divLINode = document.createElement('div');
		var divLINodeID = $(nodeID).attr("id") + "_Meta";
		divLINode.setAttribute("id", divLINodeID);

		var metaDataList = Object.keys(JSONDataObject[parseInt($(nodeID).attr("index"))].Meta);
		var metaDataString = '';
		for (var mList = 0; mList < metaDataList.length; mList++) {
			if (metaDataList[mList] != 'Index')
				metaDataString = metaDataString + metaDataList[mList] + ' : ' + JSONDataObject[parseInt($(nodeID).attr("Index"))].Meta[metaDataList[mList]] + ' ~ ';
		}
		var metaDIV = document.createElement("div");
		var metaDivText = document.createTextNode(metaDataString);
		metaDIV.appendChild(metaDivText);
		metaDIV.setAttribute("id", divLINodeID);
		document.getElementById($(nodeID).attr("id")).appendChild(metaDIV);
		$(nodeID).addClass("searched");
		$(nodeID).parents().addClass("searched");
		//	}else{
		//		document.getElementById($(nodeID).attr("id") + "_Index").setAttribute("value",$(nodeID).attr("Index"));
		//	}
	} else {
	}


	while (childDIV.parentNode) {
		/*	
             if (childDIV.getAttribute('id') !=null && document.getElementById(childDIV.getAttribute('id')).hasAttribute('Index')){
             ////console.log("expandCreaete: "+ childDIV.getAttribute('id'));
             heatmap("#"+childDIV.getAttribute('id'), JSONDataObject[parseInt(document.getElementById(childDIV.getAttribute('id')).getAttribute('Index'))]);
             }else{
             ////console.log("wrost: "+ childDIV.getAttribute('id'));
             }
		 */
		childDIV = childDIV.parentNode;
		if (childDIV.tagName == 'UL') {
			//childDIV.show();
			childDIV.style.display = 'block';
		}

	}
}

