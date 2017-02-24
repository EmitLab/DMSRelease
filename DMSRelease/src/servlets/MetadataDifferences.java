package servlets;
import connections.*;
import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQResultSequence;

@WebServlet("/MetadataDifference")
public class MetadataDifferences extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public MetadataDifferences() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String element[] = request.getParameterValues("input");
	//	System.out.println("recived " + element[0]);
		String elements[] = element[0].split(";");
		StringWriter writer=new StringWriter();
	
		//DatabaseConnection conn = new SednaConnector("SYSTEM", "MANAGER", "localhost", "testdb",5050);
		//DatabaseConnection conn = new BaseXConnector("admin", "admin", "localhost", "testdb",1984);
		DatabaseConnection conn = new BaseXConnector("admin", "admin", "localhost", "testdb",3308);
		
		XQExpression xqe =null;
		XQResultSequence rs = null;
		try {
			xqe = conn.getConnection().createExpression();
		} catch (XQException e) {
	//	log.error(e.getMessage());//	log.error(e.getMessage());
		} 
		
		for(int i=0;i<elements.length;i++){
			String [] value = elements[i].split("_");
		//	System.out.println("lenght: "+ value.length);
			String project =value[0];
			String simid=value[value.length-1];
			String model=value[1];
			String metric=value[2];
			String firstPart; 
			String where;
			String returned;
			if(project.equals("Epidemic")){
				//Reece made change from fn:collection to db:open
			firstPart=   "for $p in db:open('Epidemic')# "
					+"let $simid :=$p/project # ";
			 where = "where $simid/@name="+("\""+simid+"\"")+" and $simid/scenario/model/disease/@model=\""+model+"\" # ";
			 returned=  "return $p";
			}else{
				 firstPart =   "for $p in db:open('Energy')# "
						+"let $simid :=$p/EnergyPlus_XML # ";
				 where = "where $simid/@SimulationID="+("\""+simid.replace("\"", "")+"|"+model.replace("\"", "")+"\"")+" and $simid/modelname=\""+model+"\" # ";
				 returned=  "return $p";
			}
			//FlowrQuery fq = new FlowrQuery(firstPart+where+returned, true);
		//	System.out.println(firstPart+where+returned);
			try {
				
				rs = xqe.executeQuery((firstPart+where+returned).replace("#", "  "));
			} catch (XQException e) {
				e.printStackTrace();
				//	log.error(e.getMessage());//log.debug(e.getLocalizedMessage());
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
		String twofiles[] = writer.toString().replace(">",">\n").split("End_File");
		/*INCORPORATE THE CODE OF SICONG */
		String comparison = compXMLOnlyDiff(twofiles[0], twofiles[1]);
		comparison = comparison+"<br/> <hr class=\"hr\" width=\"100%\">"+compXML(twofiles[0], twofiles[1]);
		//StringBuffer[] resultfiles = compXML(twofiles[0], twofiles[1]);
		//StringBuffer[] result2 = compXMLOnlyDiff(twofiles[0], twofiles[1]);
		response.getOutputStream().print(comparison);
		//response.getOutputStream().print(resultfiles[0].toString()+"End_File"+result2[0].toString()+"End_File"+resultfiles[1].toString()+"End_File"+result2[1].toString());
    	response.flushBuffer();
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	
	public String compXML(String XMLStream1, String XMLStream2) throws IOException
	{
		
		String returned ="";
		StringBuffer output = new StringBuffer();
		String[] strLines1 = XMLStream1.split("\n");
		String[] strLines2 = XMLStream2.split("\n");
		StringBuffer strHtml1 = new StringBuffer();
		StringBuffer strHtml2 = new StringBuffer();
		
		for(int i=0; i<strLines1.length; i++){
			if(strLines1[i].equals(strLines2[i])){
				strLines1[i] = strLines1[i].replace("<", "&lt;");
				strLines1[i] = strLines1[i].replace(">", "&gt;");
				strLines2[i] = strLines2[i].replace("<", "&lt;");
				strLines2[i] = strLines2[i].replace(">", "&gt;");
				strLines1[i] = strLines1[i].replace("\t", "&emsp;");
				strLines2[i] = strLines2[i].replace("\t", "&emsp;");
				strHtml1.append("<font color=\"black\">" + strLines1[i] + "</font></br>\n");
				strHtml2.append("<font color=\"black\">" + strLines2[i] + "</font></br>\n");
			}else{
				strLines1[i] = strLines1[i].replace("<", "&lt;");
				strLines1[i] = strLines1[i].replace(">", "&gt;");
				strLines2[i] = strLines2[i].replace("<", "&lt;");
				strLines2[i] = strLines2[i].replace(">", "&gt;");
				strLines1[i] = strLines1[i].replace("\t", "&emsp;");
				strLines2[i] = strLines2[i].replace("\t", "&emsp;");
				strHtml1.append("<font color=\"red\">" + strLines1[i] + "</font></br>\n");
				strHtml2.append("<font color=\"red\">" + strLines2[i] + "</font></br>\n");
			}
		}
		
		output.append("<div style=\"width:100%;\">\n");
		output.append("<div style=\"float:left; width:50%;\">\n");
		output.append(strHtml1 + "</div>\n");
		output.append("<div style=\"float:right; width:50%; \">\n");
		output.append(strHtml2 + "</div>");
		
		returned= output.toString();
		return returned;
	}
	
	public String compXMLOnlyDiff(String XMLStream1, String XMLStream2) throws IOException
	{
		String returned ="";
		StringBuffer output = new StringBuffer();
		String[] strLines1 = XMLStream1.split("\n");
		String[] strLines2 = XMLStream2.split("\n");
		StringBuffer strHtml1 = new StringBuffer();
		StringBuffer strHtml2 = new StringBuffer();
		
		for(int i=0; i<strLines1.length; i++){
			if(!strLines1[i].equals(strLines2[i])){
				strLines1[i] = strLines1[i].replace("<", "&lt;");
				strLines1[i] = strLines1[i].replace(">", "&gt;");
				strLines2[i] = strLines2[i].replace("<", "&lt;");
				strLines2[i] = strLines2[i].replace(">", "&gt;");
				strLines1[i] = strLines1[i].replace("\t", "&emsp;");
				strLines2[i] = strLines2[i].replace("\t", "&emsp;");
				strHtml1.append("<font color=\"red\">" + strLines1[i] + "</font></br>\n");
				strHtml2.append("<font color=\"red\">" + strLines2[i] + "</font></br>\n");
			}
		}
		
		output.append("<div style=\"width:100%;\">\n");
		output.append("<div style=\"float:left; width:50%;\">\n");
		output.append(strHtml1 + "</div>\n");
		output.append("<div style=\"float:right; width:50%; \">\n");
		output.append(strHtml2 + "</div>");
		
		returned= output.toString();
		
		return returned;
	}
}
