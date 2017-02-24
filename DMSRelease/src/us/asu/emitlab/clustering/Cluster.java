package us.asu.emitlab.clustering;


import java.util.ArrayList;
import java.util.List;

public class Cluster {

    private String name;

    private Cluster parent;

    private List<Cluster> children;

    private double[][] data;

    //private List<Integer> simid;

    String ClusterRepresentative;

    public void setClusterRepresentative(String CR) {
        this.ClusterRepresentative = CR;
    }

    public String getClusterRepresentative() {
        return ClusterRepresentative;
    }


    private List<String> simid;

    public double[][] getData() {
        return data;
    }

    public void setData(double[][] data) {
        this.data = data;
    }

    public List<String> getSimID() {
        return simid;
    }

    public void setSimID(List<String> simid) {
        this.simid = simid;
    }

    public List<Cluster> getChildren() {
        if (children == null) {
            children = new ArrayList<Cluster>();
        }

        return children;
    }

    public void setChildren(List<Cluster> children) {
        this.children = children;
    }

    public Cluster getParent() {
        return parent;
    }

    public void setParent(Cluster parent) {
        this.parent = parent;
    }

    public Cluster(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addChild(Cluster cluster) {
        getChildren().add(cluster);

    }

    public boolean contains(Cluster cluster) {
        return getChildren().contains(cluster);
    }

    @Override
    public String toString() {
        return "Cluster " + name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Cluster other = (Cluster) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return (name == null) ? 0 : name.hashCode();
    }

    public boolean isLeaf() {
        return getChildren().size() == 0;
    }

    public int countLeafs() {
        return countLeafs(this, 0);
    }

    public int countLeafs(Cluster node, int count) {
        if (node.isLeaf())
            count++;
        for (Cluster child : node.getChildren()) {
            count += child.countLeafs();
        }
        return count;
    }


    public String toConsole1() {

        //List<String> NameList = this.getChildrenNameList();
        //int representitive = (int) (Math.random() * NameList.size());
        //String representitiveName = NameList.get(representitive);
        String representitiveName = this.getClusterRepresentative();

        String members = "\"member\":[";
        String clust = "\"cluster\":[";
        int count = 0;
        List<Cluster> LeafC = new ArrayList<Cluster>();
        List<Cluster> ClusterC = new ArrayList<Cluster>();
        for (int i = 0; i < getChildren().size(); i++) {
            Cluster c = this.getChildren().get(i);
            if (c.isLeaf()) {
                LeafC.add(c);
            } else {
                ClusterC.add(c);
            }

        }

        members += prinLeaf(LeafC) + "]";


        for (int i = 0; i < ClusterC.size(); i++) {
            if (i < ClusterC.size() - 1) {
                clust += ClusterC.get(i).toConsole1() + ",";
            } else {
                clust += ClusterC.get(i).toConsole1();
            }
        }
        clust = clust + "]";
        String myRepresentation = "";
        String val[] = representitiveName.split("\\^");
        if (this.isLeaf()) {

            myRepresentation = myRepresentation + "\"name\":" + "\"" + val[0] + "\",\"metric\":\"" + val[2] + "\",\"Index\":\"" + val[1] + "\"";

        } else {
        	myRepresentation = myRepresentation + "\"name\":" + "\"centroid_" + val[0] + "\",\"metric\":\"" + val[2] + "\",\"Index\":\"" + val[1] + "\"";
            //myRepresentation = myRepresentation + "\"name\":" + "\"centroid_" + val[0] + "\",\"metric\":\"" + val[2] + "\",\"Index\":\"" + val[1] + "\"";
            if (ClusterC.size() != 0) {
                myRepresentation = myRepresentation + "," + clust;
            }
            if (LeafC.size() != 0) {
                myRepresentation = myRepresentation + "," + members;
            }
        }


        return "{" + myRepresentation + "}";//returnValue + "}";
    }

    String prinLeaf(List<Cluster> cluster) {
        String members = "";
        String representitiveName = "";
        for (int i = 0; i < cluster.size(); i++) {
            //String representitiveName = cluster.get(i).getChildrenNameList().get(0);
        	if (cluster.get(i).isLeaf())
        		representitiveName = cluster.get(i).getName();
        	else
        		representitiveName = cluster.get(i).getClusterRepresentative();
            String val[] = representitiveName.split("\\^");
            if (i < cluster.size() - 1) {
                members = members + "{\"name\":" + "\"" + val[0] + "\",\"metric\":\"" + val[2] + "\",\"Index\":\"" + val[1] + "\"},";
            } else {
            	if(val.length==1)
            		val =cluster.get(i).getClusterRepresentative().split("\\^");
                members = members + "{\"name\":" + "\"" + val[0] + "\",\"metric\":\"" + val[2] + "\",\"Index\":\"" + val[1] + "\"}";
            }
        }

        return members;
    }

    public List<String> getChildrenNameList() {

        List<String> NameList = new ArrayList<String>();

        List<Cluster> RootChildren = this.getChildren();

        if (RootChildren.isEmpty())
            NameList.add(this.getName());
        else {
            for (int i = 0; i < RootChildren.size(); i++) {
                NameList.addAll(RootChildren.get(i).getChildrenNameList());
            }
        }

        return NameList;

    }


}
