package us.asu.emitlab.clustering.utility;

public class Contatore {

	public static Contatore cont= new Contatore();
	int conta=0;
	 
	public static int getConta() {
		return cont.conta++;
	}
	
	public void setConta(int conta) {
		cont.conta = conta;
	}
	

}
