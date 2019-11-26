package com.oppscience.sgevt.graph.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "graph")
public class Graph {
   
	private List<Node> nodes;
	
	private List<Edge> edges;
	
	public Graph() {}
	
	public Graph(List<Node> nodes, List<Edge> edges) {
		super();
		this.nodes = nodes;
		this.edges = edges;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}
	
}
