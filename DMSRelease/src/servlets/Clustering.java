package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import defaults.SessionVariableList;
import us.asu.emitlab.clustering.ClusteringInterface;
import us.asu.emitlab.clustering.datacluster.ModelClusterMongo;
import us.asu.emitlab.clustering.datacluster.ModelClusterMongoEnergy;
import us.asu.emitlab.clustering.utility.Utility;
import us.asu.emitlab.datastructure.JsonList;

@WebServlet("/Clustering")
public class Clustering extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ClusteringInterface c = null;
    private JsonList clusterArray = null;
    public static double MinValue = 0.0;//Double.MAX_VALUE;
    public static double MaxValue = 1000000000.0;//Double.MIN_VALUE;
    public static String system = "";

    public Clustering() {
        super();
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            MinValue = Double.MAX_VALUE;
            MaxValue = Double.MIN_VALUE;
            String kindofCluster = "";
            String path          = "";
            String requestKind   = "";
            String queryID       = "";
            String on            = "";
            String parameters    = "";
            String dbName        = session.getAttribute("CollName").toString();        
            System.out.println("Current Db Name - " + dbName);
            system = "mongodb";

            if(request.getAttribute("Path")!=null){
                path = (String) request.getAttribute("Path");
                kindofCluster =  (String) request.getAttribute("KindofCluster");

                requestKind = request.getParameter("ClusterRequest");
                queryID = (String) request.getAttribute("QueryId");
                on = (String) request.getAttribute("On");
                parameters = (String) request.getAttribute("Parameters");
            }else{
                //	log.debug("messaggio ricevuto da QueryServlet");
                parameters = (String) request.getAttribute("Parameters");
                on = (String) request.getAttribute("On");
            }

            if(kindofCluster.equals("")){
                kindofCluster="ClusterDemo";
            }

            String json_string = "";
            String features = queryID + ":" + on + ":" + parameters;
            JsonList j1 = null;
            switch(kindofCluster){

            case "ClusterDemo":

                if(!path.equals("")){	
                    json_string = Utility.getInstance().readFile(path, queryID);
                }else{
                    j1 = (JsonList) session.getAttribute("queryResult") ;
                }
                if(queryID.equals("")){
                    queryID = "N0";
                }
                String project = (session.getAttribute(SessionVariableList.CURRENT_PROJECT_NAME).toString()).toLowerCase();
                if(project.equals("energy")){
                    c = new ModelClusterMongoEnergy(dbName);
                } else {
                    c = new ModelClusterMongo(dbName);
                }

                break;
            default:
                System.out.println("No match the cluster");
            }	

            clusterArray = c.getObjectClustered(4, j1, null, features);
            /*
        if(j1.isEmpty()){
            response.getOutputStream().print("700"); //Errore la query non ha prodotto risultati
        }
        else if (clusterArray.size() == 0 ){
            response.getOutputStream().print("701");// Errore il cluster � vuoto
        }else{
            System.out.println("Min: "+ MinValue+"  Max: "+ MaxValue);
            response.getOutputStream().print(clusterArray.get(0).toString()+"^"+j1.toString()+"^"+MaxValue+"^"+MinValue);//+1000000+"^"+0);
        }
             */
            JSONObject jObject = new JSONObject();

            jObject.put("result", clusterArray.get(0).toString()+"^"+j1.toString()+"^"+MaxValue+"^"+MinValue);//+1000000+"^"+0)

            PrintWriter writer = response.getWriter();
            writer.println(jObject);
            writer.flush();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
