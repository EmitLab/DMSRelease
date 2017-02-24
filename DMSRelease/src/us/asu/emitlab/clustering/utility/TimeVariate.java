package us.asu.emitlab.clustering.utility;

import java.util.Comparator;



public class TimeVariate implements Comparator<TimeVariate>{

public long timepoint;
public String variates;

public TimeVariate(long parseLong, String concvariate) {
 this.timepoint= parseLong;
 this.variates=concvariate;
}
public TimeVariate() {
	// TODO Auto-generated constructor stub
	super();
}

@Override
	public String toString() {
		// TODO Auto-generated method stub
		return ""+this.timepoint+this.variates;
	}
@Override
public int compare(TimeVariate arg0, TimeVariate arg1) {
	// TODO Auto-generated method stub
	int comparison;
	if(arg0.timepoint> arg1.timepoint){
		comparison=1;
	}
	else if(arg0.timepoint == arg1.timepoint){
		comparison=0;
	}
	else{
		comparison=-1;
	}
	return comparison;
	
}
}

