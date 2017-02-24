package ensembles;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.basex.api.client.ClientSession;
import org.basex.core.Context;
import org.json.JSONObject;

import defaults.Constants;

@WebServlet("/UploadXml")
public class UploadXml extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public UploadXml() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    try {
	        JSONObject jData = new JSONObject(request.getParameter("jData"));
            
            String dbName  = jData.getString("dbName");
            // String metric  = jData.getString("metric");
            String simId   = jData.getString("simName");
            String content = jData.getString("content");
            // String model   = jData.getString("model");
            
            insertXML(dbName, simId, content);
            
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public static void insertXML(String dbName, String id, String doc) throws IOException{       

        try {
            Context context = new Context();
            ClientSession session = new ClientSession(Constants.BASEX_HOST, Constants.BASEX_PORT, Constants.BASEX_USER, Constants.BASEX_PASS);
            session.execute("OPEN " + dbName);
            InputStream input = new ByteArrayInputStream(doc.getBytes());
            String fileName = id + ".xml";
            session.add(fileName, input);
            session.execute("CLOSE");
            session.close();
            context.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
	public static void deleteXML(String dbName, String id, String doc) throws IOException{ 
        try {
            Context context = new Context();
            ClientSession session = new ClientSession(Constants.BASEX_HOST, Constants.BASEX_PORT, Constants.BASEX_USER, Constants.BASEX_PASS);
            session.execute("OPEN " + dbName);
            String fileName = id + ".xml";
            session.execute("DELETE " + fileName);
            session.execute("CLOSE");
            session.close();
            context.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
