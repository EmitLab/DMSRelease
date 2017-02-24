package us.asu.emitlab.query;

import java.util.ArrayList;
import java.util.List;


public class FlowrQuery {
    public List<String>queryasList = new ArrayList<String>();
    OutputQuery output = null;
    String topId = "";
    public FlowrQuery(String query) {
        queryasList = getQueryPart(query);
    }

    public FlowrQuery(String query, boolean b) {
        String par[] = query.split("#");
        for(int i = 0; i < par.length; i++){
            if(i == 0 && par[i].contains("for")){
                topId = getTopSymbol(par[i]);
            }
            queryasList.add(par[i]);
        }
    }

    public String getNormalQuery(){
        String val = "";
        for (int i = 0; i < queryasList.size(); i++) {
            if(queryasList.get(i).contains("return")){
                val = val + " " + setReturnValue(queryasList.get(i));
            } else {
                val = val + " " + queryasList.get(i);
            }
        }
        return val;
    }

    private List<String> getQueryPart(String query){
        List <String> list =  new ArrayList<String>();
        String par[] = query.split("#");
        boolean end = false;
        for(int i =0;i<par.length && !end;i++){
            //if(i==0 && !par[i].contains("@Input")){
            if(i==0 && par[i].contains("for")){
                topId =  getTopSymbol(par[i]);
            }
            //if(!par[i].contains("@Input"))
            if(par[i].contains("for") || par[i].contains("let") || 
                    par[i].contains("where") || par[i].contains("order") || 
                    par[i].contains("return"))
            {
                list.add(par[i]);
            }
            else{
                end=true;
            }
        }
        return list;
    }

    private String getTopSymbol(String string) {
        // TODO Auto-generated method stub
        String returned ="";
        String[] par = string.split(" ");
        boolean terminated= false;
        for (int i = 0; (i < par.length)&& !terminated; i++) {
            if(par[i].contains("$")){
                returned= par[i];
                terminated=true;
            }
        }
        return returned;
    }

    public String toString1(){
        String val="";
        for (int i = 0; i < queryasList.size(); i++) {
            if(queryasList.get(i).contains("where")){
                val= val+" "+setModelinWhere(queryasList.get(i)); 
            }
            else
                val=val+queryasList.get(i);

        }
        return val;
    }

    @Override
    public String toString() {
        String val="";
        for (int i = 0; i < queryasList.size(); i++) {
            if(queryasList.get(i).contains("where") && !queryasList.get(i).contains("return")){
                val= val+" "+setModelinWhere(queryasList.get(i)); 
            }
            if(queryasList.get(i).contains("return") && !queryasList.get(i).contains("where")){ 
                //      System.out.println("return value fetched: "+queryasList.get(i));
                val=val+" "+setReturnValue(queryasList.get(i));
            }
            if(!queryasList.get(i).contains("return") && !queryasList.get(i).contains("where")){
                val=val+queryasList.get(i);
            }
        }
        return val;
    }

    private String setModelinWhere(String string) {
        // TODO Auto-generated method stub


        String pluto=this.output.getModels(topId);
        if(pluto!=null && !string.contains("project/scenario/model/disease@model")){
            string = string.replace("where", "");
            string = "where ("+string.trim()+") "+"and ("+pluto+")";
        }
        return string+" ";
    }

    private String setReturnValue(String string) {

        if(this.output!=null){
            string= output.getAdditionalMetadata(string);
        }
        String returned="";
        if(this.queryasList.get(0).contains("Epidemic")||this.queryasList.get(0).contains("TestSimulations")){
            //returned="return concat(\"simid:\", data("+topId+"/project/@name),\",\","+"\"model:\", data("+topId+"/project/scenario/model/disease/@model),\",\",";
            //returned="return (\"simid:\"|| \" \" || data("+topId+"/project/@name) || \" \" || \"model:\" || \" \" || data("+topId+"/project/scenario/model/disease/@model) || \" \" ||";
            returned="return fn:concat(\"simid:\", data("+topId+"/project/@name),\",\","+"\"model:\", data("+topId+"/project/scenario/model/disease/@model),\",\",";
        }else if(this.queryasList.get(0).contains("Energetic")){
            /*change with absolute path from xml Energy*/
            // returned="return concat(\"simid:\", data("+topId+"/project/@name),\",\","+"\"model:\", data("+topId+"/project/scenario/model/@name),\",\",";
            //returned="return (\"simid:\"|| \" \" || data("+topId+"/project/@name) || \" \" || \"model:\" || \" \" || data("+topId+"/project/scenario/model/@name)|| \" \" ||";
            returned="return fn:concat(\"simid:\", data("+topId+"/project/@name),\",\","+"\"model:\", data("+topId+"/project/scenario/model/@name),\",\",";
        }

        String[] dat1 = string.split("return");
        String []dat2 = dat1[1].split(",");
        for(int i= 0;i<dat2.length;i++){
            String dat3[] = dat2[i].split("/");
            String val = dat3[1];
            dat3[0]=val+dat3[0];

            if(i<dat2.length-1){
                returned=returned+"\""+ dat3[1]+":\",data("+dat2[i]+"),\",\",";
            }
            else{
                returned=returned+"\""+dat3[1]+":\",data("+dat2[i]+")";
            }
        }

        returned= returned+")";
        //returned= returned;
        return returned;
    }

    public String prepareToQuery(){
        return null;

    }
    public void add(OutputQuery output) {
        this.output=output;
    }


}


