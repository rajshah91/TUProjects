package com.oppscience.sgevt.graph.model;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "nodes")
public class Node extends HashMap<String, Object> {

	private static final long serialVersionUID = -8386055001048395126L;

	public String id() {
		return (String) this.get("id");
	}

	public String label() {
		 return (String) this.get("label");
	}

	public String type() {
		 return (String) this.get("type");
	}

}
