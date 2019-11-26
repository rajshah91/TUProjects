package com.oppscience.sgevt.graph.controller;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oppscience.sgevt.graph.CustomException;
import com.oppscience.sgevt.graph.model.GraphResponse;

@RunWith(SpringRunner.class)
@WebMvcTest(value = GraphApiController.class, secure = false)
@ActiveProfiles("test")
@SuppressWarnings("all")
public class GraphApiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ResourceLoader resourceLoader;
	
	private final static String GET_INPUT_SINGLE_PARAM = "test1";
	private final static String GET_INPUT_MULTIPLE_PARAM = "test1,test2";
	private final static String GET_INPUT_INVALID_PARAM = "test";
	private final static String VALID_OUTPUT_PARAM="?output=model1";
	private final static String INVALID_OUTPUT_PARAM="?output=testmodel";
	private final static String GET_GRAPH_URL="/graph/{docids}";
	private final static String MERGE_GRAPH_URL="/graph/merge/{docids}";
	

	/******************************************
	 * 
	 * Test Cases for Get Graph
	 * 
	 ******************************************/
	
	@Test
	public void testGetGraph_OnValidInput_SingleParam() throws Exception {
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get(GET_GRAPH_URL, GET_INPUT_SINGLE_PARAM)
				.accept(MediaType.APPLICATION_JSON))
//				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		String response=mvcResult.getResponse().getContentAsString();
		assertResponseForGraph(response,false,false,false);
	}
	
	@Test
	public void testGetGraph_OnValidInput_SingleParam_With_ValidOutputRequestParam() throws Exception {
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get(GET_GRAPH_URL+VALID_OUTPUT_PARAM, GET_INPUT_SINGLE_PARAM)
				.accept(MediaType.APPLICATION_JSON))
//				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		String response=mvcResult.getResponse().getContentAsString();
		assertResponseForGraph(response,false,true,false);
	}
	
	@Test
	public void testGetGraph_OnValidInput_SingleParam_With_InValidOutputRequestParam(){
		try {
			mockMvc
					.perform(MockMvcRequestBuilders.get(GET_GRAPH_URL+INVALID_OUTPUT_PARAM, GET_INPUT_SINGLE_PARAM)
					.accept(MediaType.APPLICATION_JSON))
					.andDo(print())
					.andExpect(status().isInternalServerError());
		}catch(Exception e) {
			assertEquals("Request processing failed; nested exception is com.oppscience.sgevt.graph.CustomException: Output : testmodel is not valid", e.getMessage());
		}
	}
	
	@Test
	public void testGetGraph_OnValidInput_MultipleParam() throws Exception {
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get(GET_GRAPH_URL, GET_INPUT_MULTIPLE_PARAM)
				.accept(MediaType.APPLICATION_JSON))
//				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		String response=mvcResult.getResponse().getContentAsString();
		assertResponseForGraph(response,true,false,false);
	}
	
	@Test
	public void testGetGraph_OnValidInput_MultipleParam_With_ValidOutputRequestParam() throws Exception {
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get(GET_GRAPH_URL+VALID_OUTPUT_PARAM, GET_INPUT_MULTIPLE_PARAM)
				.accept(MediaType.APPLICATION_JSON))
//				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		String response=mvcResult.getResponse().getContentAsString();
		assertResponseForGraph(response,true,true,false);
	}
	
	@Test
	public void testGetGraph_OnValidInput_MultipleParam_With_InValidOutputRequestParam(){
		try {
			mockMvc
					.perform(MockMvcRequestBuilders.get(GET_GRAPH_URL+INVALID_OUTPUT_PARAM, GET_INPUT_MULTIPLE_PARAM)
					.accept(MediaType.APPLICATION_JSON))
//					.andDo(print())
					.andExpect(status().isInternalServerError());
		}catch(Exception e) {
			assertEquals("Request processing failed; nested exception is com.oppscience.sgevt.graph.CustomException: Output : testmodel is not valid", e.getMessage());
		}
	}
	
	@Test
	public void testGetGraph_OnInValidInput() throws Exception {
		
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get(GET_GRAPH_URL, GET_INPUT_INVALID_PARAM)
				.accept(MediaType.APPLICATION_JSON))
//				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		String response=mvcResult.getResponse().getContentAsString();
		GraphResponse graphResponse=mapFromJson(response, GraphResponse.class);
		assertTrue(graphResponse != null);
		assertTrue(graphResponse.getGraphs() != null);
		assertTrue(graphResponse.getGraphs().size()==0);
	}
	
	/******************************************
	 * 
	 * Test Cases for Merge Graph
	 * 
	 ******************************************/
	@Test
	public void testMergeGraph_OnValidInput_SingleParam() throws Exception {
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get(MERGE_GRAPH_URL, GET_INPUT_SINGLE_PARAM)
				.accept(MediaType.APPLICATION_JSON))
//				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		String response=mvcResult.getResponse().getContentAsString();
		assertResponseForGraph(response,false,false,true);
	}
	
	@Test
	public void testMergeGraph_OnValidInput_SingleParam_With_ValidOutputRequestParam() throws Exception {
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get(MERGE_GRAPH_URL+VALID_OUTPUT_PARAM, GET_INPUT_SINGLE_PARAM)
				.accept(MediaType.APPLICATION_JSON))
//				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		String response=mvcResult.getResponse().getContentAsString();
		assertResponseForGraph(response,false,true,true);
	}
	
	@Test
	public void testMergeGraph_OnValidInput_SingleParam_With_InValidOutputRequestParam(){
		try {
			mockMvc
					.perform(MockMvcRequestBuilders.get(MERGE_GRAPH_URL+INVALID_OUTPUT_PARAM, GET_INPUT_SINGLE_PARAM)
					.accept(MediaType.APPLICATION_JSON))
					.andDo(print())
					.andExpect(status().isInternalServerError());
		}catch(Exception e) {
			assertEquals("Request processing failed; nested exception is com.oppscience.sgevt.graph.CustomException: Output : testmodel is not valid", e.getMessage());
		}
	}
	
	@Test
	public void testMergeGraph_OnValidInput_MultipleParam() throws Exception {
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get(MERGE_GRAPH_URL, GET_INPUT_MULTIPLE_PARAM)
				.accept(MediaType.APPLICATION_JSON))
//				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		String response=mvcResult.getResponse().getContentAsString();
		assertResponseForGraph(response,true,false,true);
	}
	
	@Test
	public void testMergeGraph_OnValidInput_MultipleParam_With_ValidOutputRequestParam() throws Exception {
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get(MERGE_GRAPH_URL+VALID_OUTPUT_PARAM, GET_INPUT_MULTIPLE_PARAM)
				.accept(MediaType.APPLICATION_JSON))
//				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		String response=mvcResult.getResponse().getContentAsString();
		assertResponseForGraph(response,true,true,true);
	}
	
	@Test
	public void testMergeGraph_OnValidInput_MultipleParam_With_InValidOutputRequestParam(){
		try {
			mockMvc
					.perform(MockMvcRequestBuilders.get(MERGE_GRAPH_URL+INVALID_OUTPUT_PARAM, GET_INPUT_MULTIPLE_PARAM)
					.accept(MediaType.APPLICATION_JSON))
//					.andDo(print())
					.andExpect(status().isInternalServerError());
		}catch(Exception e) {
			assertEquals("Request processing failed; nested exception is com.oppscience.sgevt.graph.CustomException: Output : testmodel is not valid", e.getMessage());
		}
	}
	
	
	/******************************************
	 * 
	 * Helper Test function
	 * 
	 ******************************************/
	
	private File getDemoFile() throws IOException {
		return resourceLoader.getResource("classpath:" + GET_INPUT_SINGLE_PARAM + ".json").getFile();
	}
	
	private void assertResponseForGraph(String response,boolean checkForMultiple,boolean checkForOutputReqParam,boolean isMergedGraph) 
			throws JsonParseException, JsonMappingException, IOException {
		GraphResponse graphResponse=mapFromJson(response, GraphResponse.class);
		assertTrue(graphResponse != null);
		assertTrue(graphResponse.getGraphs() != null);
		assertTrue(graphResponse.getGraphs().size() >= 1);
		assertTrue(graphResponse.getGraphs().get(0).getNodes() != null);
		assertTrue(graphResponse.getGraphs().get(0).getNodes().size() >= 1);
		assertTrue(graphResponse.getGraphs().get(0).getEdges() != null);
		assertTrue(graphResponse.getGraphs().get(0).getEdges().size() >= 1);
		
		if(checkForMultiple && !isMergedGraph) {
			assertTrue(graphResponse.getGraphs().get(1).getNodes() != null);
			assertTrue(graphResponse.getGraphs().get(1).getNodes().size() >= 1);
			assertTrue(graphResponse.getGraphs().get(1).getEdges() != null);
			assertTrue(graphResponse.getGraphs().get(1).getEdges().size() >= 1);
		}
		
		if(checkForOutputReqParam) {
			assertTrue(graphResponse.getGraphs().get(0).getNodes().get(0).containsKey("text"));
			assertTrue(graphResponse.getGraphs().get(0).getEdges().get(0).containsKey("sid"));
			assertTrue(graphResponse.getGraphs().get(0).getEdges().get(0).containsKey("tid"));
		}
		
		if(isMergedGraph && checkForMultiple) {
			assertTrue(graphResponse.getGraphs().get(0).getNodes().size() == 7);
			assertTrue(graphResponse.getGraphs().get(0).getEdges().size() == 7);
		}
	}
	
	protected <T> T mapFromJson(String json, Class<T> clazz)
			throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, clazz);
	}
	
}
