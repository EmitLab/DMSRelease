package us.asu.emitlab.datastructure;

public class ModelPosition {
	String Model ="";
	int spos=0;
public ModelPosition(String model,int spos) {
	this.Model=model;
	this.spos=spos;
	// TODO Auto-generated constructor stub
}
private void setposition(int pos) {
	// TODO Auto-generated method stub
this.spos=spos;
}
public int getSpos() {
	return spos;
}
public String getModel() {
	return Model;
}
}
