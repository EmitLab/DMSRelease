package us.asu.emitlab.clustering;

import org.json.JSONArray;

import us.asu.emitlab.datastructure.JsonList;
import us.asu.emitlab.metric.MetricInterface;


public interface ClusteringInterface {

	public JsonList getObjectClustered(int k,JSONArray Resultset, MetricInterface metric,String features);

	public JsonList getObjectClustered(int k,JsonList Resultset, MetricInterface metric,String features);
	
	
}
