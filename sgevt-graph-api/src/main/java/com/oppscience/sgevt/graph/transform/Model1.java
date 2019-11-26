package com.oppscience.sgevt.graph.transform;

public class Model1 extends DefaultGraphResponseTransformer{
	
	@Override
	protected String getKeyForNode(String key) {
		String keyForView=key;
		if("label".equalsIgnoreCase(key)){
			keyForView="text";
		}
		return  keyForView;
	}
	
	@Override
	protected String getKeyForEdge(String key) {
		String keyForView=key;
		if("source".equalsIgnoreCase(key)){
			keyForView="sid";
		}else if("target".equalsIgnoreCase(key)){
			keyForView="tid";
		}
		return  keyForView;
	}
	
	
	
}
