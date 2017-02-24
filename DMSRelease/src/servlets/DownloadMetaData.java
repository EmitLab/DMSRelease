package servlets;


import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQResultSequence;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import connections.BaseXConnector;
import connections.DatabaseConnection;
import us.asu.emitlab.query.FlowrQuery;

@WebServlet("/DownloadMetaData")
public class DownloadMetaData extends HttpServlet {
	private static final long serialVersionUID = 5764960660574831472L;
	public DownloadMetaData() {
		super();
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String simid = request.getParameter("SimId");
		String model = request.getParameter("ModelName");
		String metric = request.getParameter("Metric");
		String project = request.getParameter("project");
		
		System.out.println(simid + " " + model + " " + metric + " " + project);
		String simIDs[] =  simid.split("_");
		StringWriter writer=new StringWriter();
		DatabaseConnection conn = null;
		if(project.toLowerCase().equals("epidemic")){
			conn  = new BaseXConnector("admin", "admin", "localhost", "Epidemic",3308);
		}else if(project.toLowerCase().equals("energy")){
			conn = new BaseXConnector("admin", "admin", "localhost", "testdb",3308);
		}

		for(int i=0;i<simIDs.length;i++){
			String firstPart =   "";
			String where = "";
			String returned=  "";
			if(project.toLowerCase().equals("epidemic")){
				firstPart =   "for $p in db:open('Epidemic')# "
						+ "let $a := $p/project/scenario/model/disease# ";
				where = "where $p/project/@name="+("\""+simIDs[i].replace("\"", "")+"\"")+" and $a/@model="+model+" # "; //replace just for multistory .replace("MultiS", "Multis")
				returned=  "return $p";//"return $a/transmissionRate,$a/recoveryRate,$a/infectionMortalityRate,$a/incubationRate,$a/immunityLossRate, $s/infector/@targetISOKey,$s/graph,$b/birthRate,$b/deathRate";//$t/@type,
			}else if(project.toLowerCase().equals("energy")){
				firstPart =   "for $p in db:open('Energy')# "
						+ "let $simid :=$p/EnergyPlus_XML # ";
				where = "where $simid/@SimulationID="+("\""+simid.replace("\"", "")+"|"+model.replace("\"", "")+"\"")+" and $simid/modelname="+model+" # ";
				returned=  "return $p";
			} 
			FlowrQuery fq = new FlowrQuery(firstPart+where+returned, true);
			//execute query on xml database
			XQExpression xqe =null;
			XQResultSequence rs = null;
			try {
				xqe = conn.getConnection().createExpression();
			} catch (XQException e) {
				System.out.println(e.getMessage());//	log.error(e.getMessage());
			} 

			try {
				rs = xqe.executeQuery((firstPart+where+returned).replace("#", "  "));
			} catch (XQException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());//	log.error(e.getMessage());//log.debug(e.getLocalizedMessage());
			}
			String result ="";

			try {
				while (rs.next()) {
					javax.xml.transform.Result r=new StreamResult(writer);
					rs.writeItemToResult(r);
					writer.flush();
				}
			} catch (XQException e) {
				e.printStackTrace();
			}
			writer.append("End_File");
		}
		String documentstoreturn=writer.toString();
		HashMap<String, String> graphsname= new HashMap<String, String>();
		if(project.equals("epiemic")){
			String header= "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
			String idgraphs[]=documentstoreturn.split("End_File");
			for(int i=0;i<idgraphs.length;i++){
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
				DocumentBuilder builder;  
				try  
				{  
					builder = factory.newDocumentBuilder();  
					Document document = builder.parse( new InputSource( new StringReader(header+ idgraphs[i] ) ) );
					String graphname= document.getElementsByTagName("graph").item(0).getNodeValue();
					if(!graphsname.containsKey(graphname)){
						graphsname.put(graphname, graphname);
					}
				} catch (Exception e) {  
					e.printStackTrace();  
				}
			}
		}
		/*end added code*/

		response.getOutputStream().print(writer.toString());
		response.flushBuffer();
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
