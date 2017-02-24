/**
 * By Yash Garg
 */

function getGranularity(timeDiff){

	var elements = [], frequency = [], days = [], prev; 
	timeDiff.sort();

	for (var i = 0; i < timeDiff.length; i++){
		if(timeDiff[i] !== prev){
			days.push((timeDiff[i] / (1000*60*60*24)));
			elements.push(timeDiff[i]);
			frequency.push(1);
		}else{
			frequency[frequency.length - 1]++;
		}
		prev = timeDiff[i];
	}

	var granularityIndex = frequency.indexOf(Math.max.apply(Math,frequency));
	var granularity = elements[granularityIndex];

	return granularity;
}

/**
 * By Anisha Gupta
 */
//returns the string granularity
function convertGranularity(unit,granularity){
	var count = 0; 
	var convertedGranularity, newGranularity;

	if(unit == "seconds"){
		count = 6; 
		newGranularity = granularity/(1000);
		convertedGranularity = newGranularity + " second"
	}
	else if(unit == "minutes"){
		count = 5; 
		newGranularity = granularity/(1000*60);
		convertedGranularity = newGranularity + " minute"
	}
	else if(unit == "hours"){
		count = 4; 
		newGranularity = granularity/(1000*60*60);
		convertedGranularity = newGranularity + " hour"
	}
	else if(unit == "days"){
		count = 3; 
		newGranularity = granularity/(1000*60*60*24);	
		convertedGranularity = newGranularity + " day"
	}
	else if(unit == "months"){
		count = 2; 
		newGranularity = granularity/(1000*60*60*24*12);
		convertedGranularity = newGranularity.toFixed(2) + " month"
	}
	else if(unit =="years") {
		count = 1;
		newGranularity = granularity/(1000*60*60*24*365);
		convertedGranularity = newGranularity.toFixed(2) + " year"
	}
	else { // default unit is milliseconds
		convertedGranularity = granularity + " millisecond"
	}
	if(newGranularity >= 1){
		if(newGranularity != 1){
			convertedGranularity = convertedGranularity + "s";
		}
	}
	else {
		//console.log("Recursive call: " + count + " " + convertedGranularity);
		switch(count){
		case 1: 
			return convertGranularity("months", granularity);
			break;
		case 2: 
			return convertGranularity("days", granularity);
			break;
		case 3: 
			return convertGranularity("hours", granularity);
			break;
		case 4: 
			return convertGranularity("minutes", granularity);
			break;
		case 5: 
			return convertGranularity("seconds", granularity);
			break; 
		case 6: 
			return convertGranularity("", granularity);
			break; 
		}

	}
	return convertedGranularity;
}