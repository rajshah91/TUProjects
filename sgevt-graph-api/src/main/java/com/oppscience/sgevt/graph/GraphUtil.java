package com.oppscience.sgevt.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.oppscience.sgevt.graph.model.Edge;
import com.oppscience.sgevt.graph.model.Graph;
import com.oppscience.sgevt.graph.model.Node;

@SuppressWarnings("all")
public class GraphUtil {
	
	private final static String KEY_JOINING_PATTERN="###";
	
	public static Graph mergeGraphs(List<Graph> graphList) {
		List<Node> nodeList=new ArrayList<>();
		List<Edge> edgeList=new ArrayList<>();
		
		Set<String> uniqueNodeKeySet=new HashSet<>();
		Set<String> uniqueEdgeKeySet=new HashSet<>();
		
		Set<String> duplicateNodeKey=new HashSet<>();
		Set<String> duplicateEdgeKey=new HashSet<>();
		
		if(graphList != null && graphList.size()>0) {
			for(Graph graph : graphList) {
				
				Iterator<Node> nodeItr=graph.getNodes().iterator();
				while(nodeItr.hasNext()) {
					Node node=nodeItr.next();
					if(node != null && doesNodeContainAllUniqueKeys(node) && !uniqueNodeKeySet.contains(getNodeUniqueValue(node))){
						nodeList.add(node);
						uniqueNodeKeySet.add(getNodeUniqueValue(node));
					}else {
						duplicateNodeKey.add(getNodeUniqueValue(node));
					}
				}
				
				Iterator<Edge> edgeItr=graph.getEdges().iterator();
				while(edgeItr.hasNext()) {
					Edge edge=edgeItr.next();
					if(edge != null && doesEdgeContainAllUniqueKeys(edge) && !uniqueEdgeKeySet.contains(getEdgeUniqueValue(edge))){
						edgeList.add(edge);
						uniqueEdgeKeySet.add(getEdgeUniqueValue(edge));
					}else {
						duplicateEdgeKey.add(getEdgeUniqueValue(edge));
					}
				}
				
			}
			if(nodeList.size()>0 && graphList.size()>0) {
//				printDuplicateValueOfMergingGraphs(duplicateNodeKey,duplicateEdgeKey);
				return new Graph(nodeList, edgeList);
			}
		}
		return null;
	}
	
	private static boolean doesNodeContainAllUniqueKeys(Node node) {
		return (node.containsKey("id"));
	}
	
	private static String getNodeUniqueValue(Node node) {
		return node.get("id").toString();
	}
	
	private static boolean doesEdgeContainAllUniqueKeys(Edge edge) {
		return (edge.containsKey("source") && edge.containsKey("target"));
	}
	
	private static String getEdgeUniqueValue(Edge edge) {
		return edge.get("source").toString()+KEY_JOINING_PATTERN+edge.get("target").toString();
	}
	
	private static void printDuplicateValueOfMergingGraphs(Set<String> duplicateNodeKey,Set<String> duplicateEdgeKey) {
		System.out.println(duplicateNodeKey);
		System.out.println("****************");
		System.out.println(duplicateEdgeKey);
	}
}
