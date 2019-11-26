package com.oppscience.sgevt.graph.transform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.oppscience.sgevt.graph.model.Edge;
import com.oppscience.sgevt.graph.model.Graph;
import com.oppscience.sgevt.graph.model.GraphResponse;
import com.oppscience.sgevt.graph.model.Node;

@SuppressWarnings("all")
public class DefaultGraphResponseTransformer extends JsonSerializer<GraphResponse> {

	private static final long serialVersionUID = 8061530736096503063L;

	@Override
	public void serialize(GraphResponse value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {

		if (value != null) {
			List<Graph> graphs = value.getGraphs();
			List<Graph> newGraph=new ArrayList<>();
			if(graphs != null && graphs.size()>0) {
				for (Graph graph : graphs) {
					if(graph != null) {
						List<Node> nodeList=handleNodes(graph, jgen);
						List<Edge> edgeList=handleEdges(graph, jgen);
						newGraph.add(new Graph(nodeList,edgeList));
					}
				}
			}
			jgen.writeStartObject();
			jgen.writeObjectField("graphs",newGraph);
			jgen.writeEndObject();
		}

	}

	@Override
	public Class<GraphResponse> handledType() {
		return GraphResponse.class;
	}

	private List<Node> handleNodes(Graph graph,JsonGenerator jgen) throws IOException {
		List<Node> nodeList=graph.getNodes();
		List<Node> newNodeList=new ArrayList<>();
		for(Node node : nodeList) {
			Set<String> keys = node.keySet();
			Node newNode= new Node();
			for (String key : keys) {
				newNode.put(getKeyForNode(key), node.get(key));
			}
			newNodeList.add(newNode);
		}
		return newNodeList;
	}

	private List<Edge> handleEdges(Graph graph, JsonGenerator jgen) throws IOException {
		List<Edge> edgeList = graph.getEdges();
		List<Edge> newEdgeList = new ArrayList<>();
		for(Edge edge : edgeList) {
			Set<String> keys = edge.keySet();
			Edge newEdge=new Edge();
			for (String key : keys) {
				newEdge.put(getKeyForEdge(key), edge.get(key));
			}
			newEdgeList.add(newEdge);
		}
		return newEdgeList;
	}
	
	protected String getKeyForNode(String key) {
		return  key;
	}
	
	protected String getKeyForEdge(String key) {
		return  key;
	}
	
	

}
