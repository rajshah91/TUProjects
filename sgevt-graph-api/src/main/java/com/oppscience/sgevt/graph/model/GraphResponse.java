package com.oppscience.sgevt.graph.model;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.oppscience.sgevt.graph.transform.DefaultGraphResponseTransformer;;

//@JsonSerialize(using = DefaultGraphResponseTransformer.class)
public class GraphResponse {
	
	List<Graph> graphs;

	public List<Graph> getGraphs() {
		return graphs;
	}

	public void setGraphs(List<Graph> graphs) {
		this.graphs = graphs;
	}
}
