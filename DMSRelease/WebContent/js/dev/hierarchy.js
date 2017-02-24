/**
 *  This javascript method creates the hierarchy based on the cluster String
 */

function createHierarchy(nodeID, clusterString, nodeType, count) {

	// Initialize the LI Node to Show the Cluster Entry.
	var liNode = document.createElement('li');
	liNode.setAttribute('nodeType','H');// H : Hierarchy
	//liNode.setAttribute('style','cursor:pointer;');
	if (count < 4) {
		if (count == 3){
			//var liTextNode = document.createTextNode(clusterString.name + ',');
			var liTextNode = document.createTextNode(clusterString.name + ',');
			liNode.setAttribute('Properties',clusterString.name); //metric
		} else {
			if (clusterString.name != null){
				var liTextNode = document.createTextNode(clusterString.name);
			} else {
				var liTextNode = document.createTextNode(' ERR ');
			}
			//	console.log(clusterString.name);//see what is the name of the cluster Project - Model
		}

	} else {
		var liTextNode = document.createTextNode('');
	}
	// liTextNode.setAttribute('style','cursor:pointer;');
	if (count >= 3) {

		if (clusterString['cluster']) {//enter 1 time

			// Handling Singlet Clusters
			if (clusterString.cluster.length < 2) {//no enter

				if (nodeID == ''){
					nodeID = clusterString.name;
					//console.log(clusterString.name);
				}
				else {
					//if(nodeID.endsWith('_'))
					if (nodeID.slice(-1) == '_'){// we do not enter here 
						nodeID = nodeID + clusterString.name;
						//console.log(nodeID + clusterString.name);
					}
					else{
						nodeID = nodeID + '_' + clusterString.name;
						//console.log(nodeID + '_' +  clusterString.name);
					}
				}
				clusterString = clusterString.cluster[0];
			}
		}
	}

	liNode.appendChild(liTextNode);

	// Adding the CSS Class "last" for last node in each level
	if (nodeID == '') {
		liNode.setAttribute("class", "last");
	}

	if (clusterString['Index']) {
		liNode.setAttribute("Index", clusterString.Index);
	}

	var idString = (nodeID == '') ? clusterString.name : (nodeID.slice(-1) == '_') ? nodeID + clusterString.name : nodeID + '_' + clusterString.name;
	liNode.setAttribute('id', idString);
	idString = idString + '_';

	if (clusterString['cluster']) {//here perhaps is appending the leafs

		// var nodeType = 'c';
		var ulNode = document.createElement('ul');
		ulNode.setAttribute('cluster', clusterString.cluster.length);
		var nodeCount = count + 1;

		for (var i = 0; i < clusterString.cluster.length; i++) {
			//recoursive call to create teh hierarchy I think here is the core
			//console.log(clusterString.cluster[i]);//here he got all the representative
			var tempNode = createHierarchy(idString, clusterString.cluster[i], nodeType, nodeCount);

			if (i == clusterString.cluster.length - 1)
				tempNode.setAttribute("class", "last");

			ulNode.appendChild(tempNode);

			if (ulNode.hasAttribute('member')) {
				var num1 = parseInt(ulNode.getAttribute('member'));

				var num2 = parseInt(tempNode.lastChild.getAttribute('member'));
				var numCount = num1 + num2;
				ulNode.setAttribute('member', numCount);
			} else {
				ulNode.setAttribute('member', tempNode.lastChild.getAttribute('member'));
			}
			if (ulNode.hasAttribute('simList')) {
				var simList1 = ulNode.getAttribute('simList');
				var simList2 = tempNode.lastChild.getAttribute('simList');
				var totalSimList = simList1 + "_" + simList2;
				ulNode.setAttribute('simList', totalSimList);
			} else {
				ulNode.setAttribute('simList', tempNode.lastChild.getAttribute('simList'));
			}
		}
		if (nodeCount == 2)
			liNode.appendChild(document.createTextNode(', #Models : ' + ulNode.getAttribute('cluster') + ', #sims : ' + ulNode.getAttribute('member')));
		else {
			if (nodeCount == 3){
				//if(Project=="epidemic"){
					liNode.appendChild(document.createTextNode(', #Properties : ' + ulNode.getAttribute('cluster') + ', #sims : ' + ulNode.getAttribute('member')));
				/*
				}else if(Project =="energy"){
					liNode.appendChild(document.createTextNode(', #Measure : ' + ulNode.getAttribute('cluster') + ', #sims : ' + ulNode.getAttribute('member')));
				}
				*/
			}
			else{
				// liNode.appendChild(document.createTextNode(' #Sub-clusters : ' + ulNode.getAttribute('cluster') + ', #sims : ' + ulNode.getAttribute('member') + ', '));
				liNode.appendChild(document.createTextNode(' #sims : ' + ulNode.getAttribute('member') + ', '));
			}
		}
		liNode.setAttribute("data-position", ulNode.getAttribute('member'));

		if(nodeType == 'm'){
			// Sorting Cluster only when meta Query
			sortTheCluster(ulNode); //here something is happening
		}else{
			// Do not sort in case of Sim Query.
		}
		//console.log(ulNode);
		liNode.appendChild(ulNode);
	}

	if (clusterString['member']) {

		var ulNode = document.createElement('ul');
		// var nodeType = 'm';
		var nodeCount = count + 1;
		ulNode.setAttribute('member', clusterString.member.length);
		var simIdString = '';

		for (var i = 0; i < clusterString.member.length; i++) {

			var tempNode = createHierarchy(idString, clusterString.member[i], nodeType, nodeCount);

			if (i == clusterString.member.length - 1) {
				simIdString = simIdString + clusterString.member[i].name;
			} else {
				simIdString = clusterString.member[i].name + "_" + simIdString;
			}
			if (i == clusterString.member.length - 1)
				tempNode.setAttribute("class", "member last");
			else
				tempNode.setAttribute("class", "member");

			ulNode.appendChild(tempNode);
		}
		ulNode.setAttribute('simList', simIdString);
		liNode.appendChild(document.createTextNode(' #sims : ' + ulNode.getAttribute('member') + ', '));
		liNode.setAttribute("data-position", ulNode.getAttribute('member'));
		if (nodeType == 'm'){
			// Sorting in Meta Query
			sortTheCluster(ulNode);	
		}else{
			// Do not sorting in Similarity Query
		}

		liNode.appendChild(ulNode);
	}
	/*
	if(nodeCount > 3){

		var clusterCount = 0;
		//var liChildNodes = liNode.getElementByTagName('ul')[0].getElementByTagName('li');
		var ulChildNodes = liNode.getElementsByTagName('ul');
		var liChildNodes = ulChildNodes[0].childNodes;
		//var getElementByTagName('ul')[0].childNodes;
		for (var i = 0; i < liChildNodes.length; i++){
			//console.log("TagName " + liChildNodes[i].nodeName);
			//console.log("ORI - " + liChildNodes[i].classList + " -");
			if (liChildNodes[i].classList.contains('member')){
				// Do not counter singleton simulation node as a cluster
				//console.log("IF " + iChildNodes[i].getAttribute('class'));
			} else {
				//console.log("ELSE " + liChildNodes[i].getAttribute('class'));
				clusterCount++;
			}
			//console.log("Counter->" + clusterCount);
		}
		var clusterCountPreffix = '#Clusters-- :: ';
		if (clusterCount > 0){
			clusterCountString = clusterCountPreffix + clusterCount;
			//$(this).prepend(document.createTextNode(clusterCount + clusterCount));
			$(liNode).prepend(document.createTextNode(clusterCountString + ', '));
			//console.log(liNode.text());
		}
	}*/
	return liNode;
}
