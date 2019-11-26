package com.oppscience.sgevt.graph.model;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "edges")
public class Edge extends HashMap<String, Object>{

	private static final long serialVersionUID = -7427418815801102449L;
	
	public String source() {
		return (String) this.get("source");
	}

	public String relation() {
		 return (String) this.get("relation");
	}

	public String target() {
		 return (String) this.get("target");
	}
}
