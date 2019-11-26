package com.oppscience.sgevt.graph.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.oppscience.sgevt.graph.CustomException;
import com.oppscience.sgevt.graph.GraphUtil;
import com.oppscience.sgevt.graph.model.Edge;
import com.oppscience.sgevt.graph.model.Graph;
import com.oppscience.sgevt.graph.model.GraphResponse;
import com.oppscience.sgevt.graph.model.Node;
import com.oppscience.sgevt.graph.transform.DefaultGraphResponseTransformer;
import com.oppscience.sgevt.graph.transform.Model1;

import static com.oppscience.sgevt.graph.AnnotationHelper.changeAnnotationValue;

@RestController
@SuppressWarnings("all")
public class GraphApiController {

	@Autowired
	ResourceLoader resourceLoader;

	protected static final Logger logger = LoggerFactory.getLogger(GraphApiController.class);

	@GetMapping("/")
	public String sayHello() {
		return "Hello User";
	}

	@GetMapping({"/graph/{docids}","/graph/{docids}?output="})
	@ResponseBody
	public String getGraph(@PathVariable("docids") String[] docids,@RequestParam(value="output",required = false) String output)
			throws CustomException, JsonProcessingException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		GraphResponse graphResponse = new GraphResponse();
		
		changeResponseTransformer(objectMapper,output);

		List<Graph> graphList = new ArrayList<>();
		File file = null;
		for (String doc : docids) {
			try {
				file = getFileFromResource("classpath:graph_" + doc + ".json");

				if (file != null && file.exists() && file.isFile()) {
					JsonNode rootNode = objectMapper.readTree(file);
					createGraphFromJsonRootNode(graphList, objectMapper, rootNode);
				} else {
					graphList.clear();
				}
			} catch (IOException ioe) {
				graphList.clear();
			}
		}
		graphResponse.setGraphs(graphList);
		return objectMapper.writeValueAsString(graphResponse);
	}
	
	@GetMapping({"/graph/merge/{docids}","/graph/merge/{docids}?output="})
	@ResponseBody
	public String mergeGraph(@PathVariable("docids") String[] docids,@RequestParam(value="output",required = false) String output)
			throws CustomException, JsonProcessingException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		GraphResponse graphResponse = new GraphResponse();
		
		changeResponseTransformer(objectMapper,output);
		
		List<Graph> graphList = new ArrayList<>();
		File file = null;
		for (String doc : docids) {
			try {
				file = getFileFromResource("classpath:graph_" + doc + ".json");

				if (file != null && file.exists() && file.isFile()) {
					JsonNode rootNode = objectMapper.readTree(file);
					createGraphFromJsonRootNode(graphList, objectMapper, rootNode);
				} else {
					graphList.clear();
				}
			} catch (IOException ioe) {
				graphList.clear();
			}
		}
		Graph mergedGraph = GraphUtil.mergeGraphs(graphList); 
		graphResponse.setGraphs(Arrays.asList(mergedGraph));
		return objectMapper.writeValueAsString(graphResponse);
	}

	private void createGraphFromJsonRootNode(List<Graph> graphList, ObjectMapper objectMapper, JsonNode rootNode)
			throws JsonParseException, JsonMappingException, IOException {
		JsonNode graphNode = rootNode.get("graph");

		List<Node> nodeList = new ArrayList();
		List<Edge> edgeList = new ArrayList();
		Graph graph = new Graph();
		if (doesNodeContainValue(graphNode)) {
			JsonNode edges = graphNode.get("edges");
			JsonNode nodes = graphNode.get("nodes");
			if (doesNodeContainValue(nodes) && doesNodeContainValue(edges)) {
				nodeList = objectMapper.readValue(nodes.toString(), new TypeReference<List<Node>>() {
				});
				edgeList = objectMapper.readValue(edges.toString(), new TypeReference<List<Edge>>() {
				});
				graph.setEdges(edgeList);
				graph.setNodes(nodeList);
				graphList.add(graph);
			}
		}
	}
	
	public File getFileFromResource(String resourceLocation) throws IOException {
		return resourceLoader.getResource(resourceLocation).getFile();
	}

	private boolean doesNodeContainValue(JsonNode jsonNode) {
		return (jsonNode != null && jsonNode.size() > 0) ? true : false;
	}
	
	private DefaultGraphResponseTransformer getClassByModelName(String modelName) throws CustomException {
		if(null == modelName || "".equals(modelName)) {
			return new DefaultGraphResponseTransformer();
		}else if("model1".equalsIgnoreCase(modelName)) {
			return new Model1();
		}else {
			throw new CustomException("Output : " +modelName +" is not valid");
		}
	}
	
	private void changeResponseTransformer(ObjectMapper objectMapper,String output) throws CustomException {
		SimpleModule module = new SimpleModule();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		module.addSerializer(GraphResponse.class, getClassByModelName(output));
		objectMapper.registerModule(module);
	}
	
}
