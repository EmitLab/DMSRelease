/**
 *   Checked whether to enable or disable the query input option based on whether the checkbox is checked or not 
 */

/************************************
****** Start Time Status Check ******
************************************/

function startTimeCheckedStatus(){
	// Checking checkbox Status 
	if(document.getElementById("startCheck").checked){
		// Status : Checked
		document.getElementById('startDate').disabled = false;
		document.getElementById('startTime').disabled = false;
	}else{
		// Status : Un-Checked
		document.getElementById('startDate').disabled = true;
		document.getElementById('startTime').disabled = true;
	}
}

/************************************
******* End Time Status Check *******
************************************/

function endTimeCheckedStatus(){
	// Checking checkbox Status
	if(document.getElementById("endCheck").checked){
		// Status : Checked
		document.getElementById('endDate').disabled = false;
		document.getElementById('endTime').disabled = false;
	}else{
		// Status : Un-Checked
		document.getElementById('endDate').disabled = true;
		document.getElementById('endTime').disabled = true;
	}
}

/************************************
***** Downsampling Status Check *****
************************************/

function samplingCheckedStatus(){
	// Checking checkbox Status
	if(document.getElementById("samplingCheck").checked){
		// Status : Checked
		document.getElementById('sampleFactor').disabled = false;
		document.getElementById('sampleUnit').disabled = false;
		document.getElementById('sampleFunction').disabled = false;
	}else{
		// Status : Un-Checked
		document.getElementById('sampleFactor').disabled = true;
		document.getElementById('sampleUnit').disabled = true;
		document.getElementById('sampleFunction').disabled = true;
	}
}

/************************************
****** Gray Scale Status Check ******
************************************/

function grayScaleCheckedStatus(){
	var grayScaleMenu = document.getElementById("grayScaleMenu");
	var grayScaleCount = grayScaleMenu.selectedIndex;
	if (grayScaleCount == 0){
		document.getElementById('grayScaleTypeMenu').disabled = false;
		document.getElementById('grayScaleDifferenceMenu').disabled = false;
		document.getElementById('grayScaleNormalizationMenu').disabled = false;
		document.getElementById('NormType').disabled = false;
		document.getElementById('TDcurvdog').disabled = false;
	}else{
		document.getElementById('grayScaleTypeMenu').disabled = true;
		document.getElementById('grayScaleDifferenceMenu').disabled = true;
		document.getElementById('grayScaleNormalizationMenu').disabled = true;
		document.getElementById('NormType').disabled = true;
		document.getElementById('TDcurvdog').disabled = true;
	}
}






/**
 *   Checked whether to enable or disable the query input option based on whether the checkbox is checked or not 
 *   windows2
 */

/************************************
****** Start Time Status Check ******
************************************/

function startTimeCheckedStatuswin2(){
	// Checking checkbox Status 
	if(win.document.getElementById("startCheck").checked){
		// Status : Checked
		win.document.getElementById('startDate').disabled = false;
		win.document.getElementById('startTime').disabled = false;
	}else{
		// Status : Un-Checked
		win.document.getElementById('startDate').disabled = true;
		win.document.getElementById('startTime').disabled = true;
	}
}

/************************************
******* End Time Status Check *******
************************************/

function endTimeCheckedStatuswin2(){
	// Checking checkbox Status
	if(win.document.getElementById("endCheck").checked){
		// Status : Checked
		win.document.getElementById('endDate').disabled = false;
		win.document.getElementById('endTime').disabled = false;
	}else{
		// Status : Un-Checked
		win.document.getElementById('endDate').disabled = true;
		win.document.getElementById('endTime').disabled = true;
	}
}

/************************************
***** Downsampling Status Check *****
************************************/

function samplingCheckedStatuswin2(){
	// Checking checkbox Status
	if(win.document.getElementById("samplingCheck").checked){
		// Status : Checked
		win.document.getElementById('sampleFactor').disabled = false;
		win.document.getElementById('sampleUnit').disabled = false;
		win.document.getElementById('sampleFunction').disabled = false;
	}else{
		// Status : Un-Checked
		win.document.getElementById('sampleFactor').disabled = true;
		win.document.getElementById('sampleUnit').disabled = true;
		win.document.getElementById('sampleFunction').disabled = true;
	}
}

/************************************
****** Gray Scale Status Check ******
************************************/

function grayScaleCheckedStatuswin2(){
	var grayScaleMenu = win.document.getElementById("grayScaleMenu");
	var grayScaleCount = grayScaleMenu.selectedIndex;
	if (grayScaleCount == 0){
		win.document.getElementById('grayScaleTypeMenu').disabled = false;
		win.document.getElementById('grayScaleDifferenceMenu').disabled = false;
		win.document.getElementById('grayScaleNormalizationMenu').disabled = false;
		win.document.getElementById('NormType').disabled = false;
		win.document.getElementById('TDcurvdog').disabled = false;
		console.log("grayscale on");
		greyScaleCompareSVG(0);
		
	}else{
		win.document.getElementById('grayScaleTypeMenu').disabled = true;
		win.document.getElementById('grayScaleDifferenceMenu').disabled = true;
		win.document.getElementById('grayScaleNormalizationMenu').disabled = true;
		win.document.getElementById('NormType').disabled = true;
		win.document.getElementById('TDcurvdog').disabled = true;
		console.log("grayscale off");
		removegrayscalewin2();
	}
}

function clearRadioButton(){
	 document.getElementById("metaRadio").checked = true;
	
	
}
