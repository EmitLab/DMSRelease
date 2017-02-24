package us.asu.emitlab.clustering.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletContext;

public class Utility {
	
	private static Utility u = new Utility();
	
	public static Utility getInstance(){
		return u;
	
	}
	
	public double maxDistance(double[]a,double[]b){
		double d=0;
		if(a !=null){
			for(int i=0;i<a.length;i++){
				d+=Math.max(d, Math.abs(a[i] - b[i]));
			}
		}else{
			d=-1;
		}
				
		return d;
	}
	
	
	public double[] addVectors (double[]vec1,double[]vec2) {
		
		double[] vec = new double[vec1.length] ;
		for (int i = 0 ; i < vec1.length ; i++)
			vec[i] = vec1[i] + vec2[i] ;
		return vec ;
	}	
	
	
	public double[] multiplyVectorByValue (double value , double[] vec) {
		double[] v = new double[vec.length] ;
		for (int i = 0 ; i < vec.length ; i++)
			v[i] = value * vec[i] ;
		return v ;
	}	
	
	public double vectorDotProduct (double[] vec1,double[] vec2) {
		
		double s = 0 ;
		for (int i = 0 ; i < vec1.length ; i++)
			s += vec1[i] * vec2[i] ;
		return s ;
	}
	
	
public String[] readFile1(String path,String nameFile, ServletContext context){
	String readed= "";
	BufferedReader br= null;
	//System.out.println("path: "+context.getContextPath()+"/"+path+"/"+nameFile);
	InputStream input = context.getResourceAsStream(path+"/"+nameFile);
	br= new BufferedReader(new InputStreamReader(input));
	String currentline="";
	try {
		while((currentline = br.readLine())!=null){
		    readed += currentline+"/n";
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return readed.split("/n");
}	

public String readFile(String path,String nameFile, ServletContext context){
	String readed= "";
	BufferedReader br= null;
//	System.out.println("path: "+context.getContextPath()+"/"+path+"/"+nameFile);
	InputStream input = context.getResourceAsStream(path+"/"+nameFile);
	br= new BufferedReader(new InputStreamReader(input));
	String currentline="";
	try {
		while((currentline = br.readLine())!=null){
		    readed += currentline;
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return readed;
}	
public String readFile(String path,String nameFile){
	String readed= "";
	
	BufferedReader br= null;
	
	try {
		br = new BufferedReader(new FileReader(path+File.separator+nameFile));

		String currentline="";
		while((currentline = br.readLine())!=null){
		    readed += currentline;
		}
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	//System.out.println(readed);
	return readed;
	
}
	
}
