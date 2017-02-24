package us.asu.emitlab.query;

public class CompleteQuery {
    public FlowrQuery flowr   = null;
    public OutputQuery output = null;
    
    public CompleteQuery(String query) {
        flowr = new FlowrQuery(query);
        output = new OutputQuery(query);
        flowr.add(output);
    }
    
    public boolean storeQuery(){
        boolean flag = false;
        return flag;
    }

    @Override
    public String toString() {
        return flowr.toString() + "<br/>" + output.toString();
    }
    
}
