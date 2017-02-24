function sortTheCluster(ulNode) {
	//var ulNodes = liNode.getElementsByTagName("ul");
	if (ulNode.hasAttribute("member")) {
		var liNodes = ulNode.children;
		$(liNodes).sort(sort_li).appendTo(ulNode);

		var liNodes = ulNode.children;
		var counter = 1;
		for (i = 0; i < liNodes.length; i++) {
			/*
			var textNodeValue = $(liNodes[i]).text(); 
			if (textNodeValue.indexOf(">") === -1){
				//do Nothing
				console.log("I am Here");
			}else{
				var textNodeArr = textNodeValue.split(">");
				console.log("Original     : " + textNodeArr[0]);
				//console.log("Original - 1 : " + textNodeArr[0].substring(0, textNodeArr[0].length - 1));
				//console.log("Original-1+i : " + textNodeArr[0].substring(0, textNodeArr[0].length - 1) + counter);
				$(liNodes[i]).text(textNodeArr[0].substring(0, textNodeArr[0].length - 1) + counter + "> " + textNodeArr[1]);
				counter++;
			}*/
			if (i < liNodes.length - 1) {
				if ($(liNodes[i]).hasClass("last")) {
					$(liNodes[i]).removeClass("last");
				} else {
					// Do Nothing
				}

			} else {
				$(liNodes[i]).addClass("last");
			}
		}

	}
}
function sort_li(a, b) {
	return ($(b).data('position')) > ($(a).data('position')) ? 1 : -1;
}
function removeSingleLeafs(liNode, count, position) {
	if ($(liNode).children('ul').length > 0) {
		// alert("Has UL Node");
		var ulNodes = liNode.getElementsByTagName("ul");
		// alert(liNode.getAttribute("id"));
		if (ulNodes[0].hasAttribute("member")) {
			var liNodes = ulNodes[0].children;
			if (ulNodes[0].getAttribute("member") == 1) {
				if (count > 3) {
					// Code to convery single cluster to member
					liNode.setAttribute("index", liNodes[0].getAttribute("index"));
					liNode.setAttribute("id", liNodes[0].getAttribute("id"));
					if (position == 'last')
						liNode.setAttribute("class", liNodes[0].getAttribute("class"));
					else
						liNode.setAttribute("class", 'member');
					liNode.innerHTML = '';

					// Code to remove single Cluster
					// liNode.remove();
				}
			} else {
				var varCount = count + 1;
				for (var i = 0; i < liNodes.length; i++) {
					// alert(liNodes[i].getAttribute("id"));
					if (i == liNodes.length - 1)
						removeSingleLeafs(liNodes[i], varCount, 'last');
					else
						removeSingleLeafs(liNodes[i], varCount, 'middle')
				}
			}
		}
	} else {
		// Do Nothing
		// alert("Does not has UL Node");
	}
}
