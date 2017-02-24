package us.asu.emitlab.query;

import java.util.ArrayList;
import java.util.List;

public class OutputQuery {
    public List<String>queryasList = new ArrayList<String>();
    public OutputQuery(String query) {
        queryasList = getQueryPart(query);

    }

    private List<String> getQueryPart(String query){
        List <String> list =  new ArrayList<String>();
        String par[]= query.split("#");
        boolean end= false;
        for(int i=0;i<par.length && !end;i++){
            //if(par[i].contains("@Output"))
            if(!(par[i].contains("for") || par[i].contains("let") || 
                    par[i].contains("where") || par[i].contains("order") || 
                    par[i].contains("return")))
            {
                par[i] = par[i].replace("US-", "");
                list.add(par[i]);
            }
        }
        return list;
    }
    public String getQueryList(){
        return this.queryasList.get(0);    
    }
    @Override
    public String toString() {
        return this.queryasList.get(0);
    }

    public String prepareToQuery(){
        return null;
    }

    public String toParametric(){
        return toString();  
    }

    public String getAdditionalMetadata(String xqueryString) {
        // TODO Auto-generated method stub
        String data= this.queryasList.get(0);
        String ret= data.replace("@Output{", "");
        String[] tempFilterArray =ret.split(";");
        //  System.out.println("return after metadata: "+xqueryString);

        for(int i=0; i<tempFilterArray.length; i++){
            String[] strTemp = tempFilterArray[i].split("=");
            if(strTemp[0].contains("metadata") && !strTemp[1].trim().equals("[]}")){
                //String strXQueryWhere = xqueryString.substring(0, xqueryString.length()-1);
                //String strMetatdata = tempFilterArray[i].substring(tempFilterArray[i].indexOf("[")+1, tempFilterArray[i].indexOf("]"));
                String d= strTemp[1];
                d= d.replace("[", "");
                d= d.replace("]", "");
                d= d.replace("}", "");

                xqueryString= xqueryString+","+d; 
            }
        }


        //System.out.println("return prepared metadata: "+xqueryString);
        return xqueryString;
    }

    public String getModels(String topid){
        // @Output{tag=[model:SI];metadata=[$a/infectionMortalityRate,$a/incubationRate]}
        String returned = "";
        String arr[]=null;

        String data= this.queryasList.get(0);
        String[] tempFilterArray =data.split(";");
        for(int i=0; i<tempFilterArray.length; i++){
            String[] strTemp = tempFilterArray[i].split("=");
            if(strTemp[0].contains("model")){
                if(strTemp[1].equals("{*}")){
                    returned = "matches("+topid+"/project/scenario/model/disease/@model,'.*')";
                }else{
                    strTemp[1] = strTemp[1].replace("{", "");
                    strTemp[1] = strTemp[1].replace("}", "");
                    arr = strTemp[1].split(",");
                    for(int j=0; j<arr.length; j++){
                        if(j<arr.length-1){
                            returned= returned+" "+topid+"/project/scenario/model/disease/@model=\""+ arr[j].trim().toUpperCase()+"\" or";
                        }
                        else{
                            returned= returned+" "+topid+"/project/scenario/model/disease/@model=\""+ arr[j].trim().toUpperCase()+"\"";
                        }
                    }
                }
            }
        }
        return returned;
    }
}
