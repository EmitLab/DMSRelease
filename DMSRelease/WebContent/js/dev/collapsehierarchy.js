function collapseAll(rootName) {
	var root = document.getElementById(rootName);
	var rootChildren = root.childNodes;
	$('#' + rootName).find('svg').remove();
	$('#' + rootName).find('div').remove();
	$('#' + rootName).find('input').remove();
	$('#' + rootName).find('span').remove();

	////////console.log(rootChildren);
	for (var i = 0; i < rootChildren.length; i++) {
		if (rootChildren[i].tagName == 'UL')
			rootChildren[i].style.display = 'none';
		if (rootChildren[i].tagName == 'UL' || rootChildren[i].tagName == 'LI')
			iterativeCollapse(rootChildren[i]);
	}


}

function iterativeCollapse(node) {

	var children = node.childNodes;
	////////console.log(children);
	for (var i = 0; i < children.length; i++) {
		if (children[i].tagName == 'UL')
			children[i].style.display = 'none';
		if (children[i].tagName == 'UL' || children[i].tagName == 'LI')
			iterativeCollapse(children[i]);
	}

}

