package servlets;

import java.io.IOException;

import connections.*;
import defaults.SessionVariableList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/*
import us.asu.emitlab.connection.xml.BaseXConnector;
import us.asu.emitlab.connection.xml.DatabaseConnection;
import us.asu.emitlab.connection.xml.SednaConnector;*/
import us.asu.emitlab.datastructure.JsonList;
import us.asu.emitlab.query.FlowrQuery;


@WebServlet("/Metadata")
public class Metadata extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Metadata() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(true);
	    String project = session.getAttribute(SessionVariableList.CURRENT_PROJECT_NAME).toString().toLowerCase();
	    
		String[] query = request.getParameterValues("where");
		System.out.println(query[0]);
		String querywhere= query[0].split(";")[0];
		String metricinput = query[0].split(";")[1];
		String firstPart="";
		String returned="";
		String dbName = "Epidemic";
		
		if(project.equals("epidemic")){
		 firstPart = "for $p in db:open('Epidemic')# "
							+ "let $a := $p/project/scenario/model/disease# "
							+ "let $t := $p/project/scenario/trigger# "
							+"let $s := $p/project/scenario# "
		 					+"let $b := $p/project/scenario/model/populationModel#";
		 returned=  "return $a/transmissionRate, $a/recoveryRate, $a/infectionMortalityRate, $a/incubationRate, $a/immunityLossRate, $s/infector/@targetISOKey, $s/graph, $b/birthRate, $b/deathRate";//$t/@type, 
		}else if(project.equals("energy")){
			firstPart =   "for $p in db:open('Energy')# "//('Epidemic')# "
					+"let $simid :=$p/EnergyPlus_XML # ";
			returned =  "return $p";
		}
		String where = querywhere+"# ";
		
		//query[0]=query[0].replace("^", " ");
		String col= firstPart.split("#")[0].split("'")[1];
		
		
		
		FlowrQuery fq = new FlowrQuery(firstPart+where+returned, true);
		//log.debug(fq.getNormalQuery());
		 System.out.println("Printing out the good stuff " + querywhere.split(" ")[3]+";Measure: "+metricinput+";Model: "+querywhere.split(" ")[7].replace("\"", "")+";"+"Collection:"+col+";");

		
		 if(project.equals("energy"))
			 response.getOutputStream().print("Simid: "+querywhere.split(" ")[3]+";Measure: "+metricinput+";Model: "+querywhere.split(" ")[7].replace("\"", "")+";"+"Collection:"+col+";");
		 else{
			 JsonList rs = null;
			 //DatabaseConnection conn = new SednaConnector("SYSTEM", "MANAGER", "localhost", "testdb",5050);
			 //DatabaseConnection conn = new BaseXConnector("admin", "admin", "localhost", "testdb",1984);
			 DatabaseConnection conn = new BaseXConnector("admin", "admin", "localhost", dbName, 3308);
			 //execute query on xml database
			 rs=conn.executeQuery(fq.getNormalQuery(), conn.getConnection());
			 JSONObject obj = null;
			 try {
				 obj = new JSONArray(rs.toString()).getJSONObject(0);			
			 } catch (JSONException e1) {
			 }
			 String simid="";
			 String model="";
			 String graph="";
			 try {
				 simid= ""+ (int)obj.getDouble("simid");
				 model = obj.getString("model");
				 graph = obj.getString("graph");
			 } catch (JSONException e) {
				 e.printStackTrace();
			 }		 
			 String pippo = rs.toString().replace("[", "");
			 pippo= pippo.replace("]", "");
			 pippo= pippo.replace("{", "");
			 pippo= pippo.replace("}", "");
		 
			 String[]names = pippo.split(",");
			 String r="";
			 for(int i =0;i<names.length;i++){	 
				String val[]=names[i].split(":");
				if(val[0].equals("\"simid\"") || val[0].equals("\"model\"") || val[0].equals("\"graph\"")){

				}else {
					if(i<names.length-1)
					 r=r+val[0]+": "+val[1]+"; ";
					else{
						r=r+val[0]+": "+val[1];
					}
				}
			 }
			 r= "Simid:"+simid+";Property:"+metricinput+";Model:"+model+";graph:"+graph+";Collection:"+col+"^"+r.replace("\"", "").replace(" ", "");
			 response.getOutputStream().print(r);//rs.toString().replace(",", "<br/>"));
		 }
		 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
