package com.oppscience.sgevt.ged.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oppscience.sgevt.ged.FileHandler;
import com.oppscience.sgevt.ged.model.ScotDataSheet;
import com.oppscience.sgevt.ged.model.ScotFileMetadata;
import com.oppscience.sgevt.ged.service.ScotService;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ScotController.class)
@ActiveProfiles("test")
public class ScotControllerTest {
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ScotService scotService;
	
	@Autowired
    private WebApplicationContext webApplicationContext;


	@Value("${scotfile.upload.basedir.path}")
	String scotFolder;

	
	private static final String TEST_SIREN_CP="123";
	private static final String TEMP_UPLOAD_DIR="C:/temp/";
	private static final String FULL_PATH= TEMP_UPLOAD_DIR+"SCOT_"+TEST_SIREN_CP;
	
	@Before
	public void init() {
//		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).alwaysDo(MockMvcResultHandlers.print()).build();
	}
	
	@After
	public void destroy() throws IOException {
		scotService.deleteById(TEST_SIREN_CP);
		FileHandler.deleteDirectoryIfExist(FULL_PATH);
	}
	
	@Test
	public void testSaveScot_OnValidInput() throws Exception {
		String inputJson = mapToJson(getDummyScotDataSheetObject());
		
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/scot/savescot")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

		assertEquals(201, mvcResult.getResponse().getStatus());
	}
	
	@Test
	public void testUploadScotFiles_OnValidInput() throws Exception {
		MockMultipartFile firstFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
		
		mockMvc.perform(MockMvcRequestBuilders.multipart("/scot/addscot/uploadfiles/{siren_cp}",TEST_SIREN_CP)
                .file(firstFile))
            	.andExpect(status().is(200))
            	.andExpect(content().string("success"));
	}
	
	@Test
//	@Ignore
	public void testGetAllScot_WithZeroLimit() throws Exception {
		List<ScotDataSheet> ul= new ArrayList<>();
		ul.add(getDummyScotDataSheetObject());
		when(scotService.findAll(0,0)).thenReturn(null);
		
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get("/scot/getall?from=0&size=0").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		assertTrue(mvcResult.getResponse().getContentAsString().isEmpty());
	}
	
	@Test
//	@Ignore
	public void testGetAllScot_WithValidLimit() throws Exception {
		List<ScotDataSheet> ul= new ArrayList<>();
		ul.add(getDummyScotDataSheetObject());
		when(scotService.findAll(0,1)).thenReturn(ul);
		
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get("/scot/getall?from=0&size=1").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		assertTrue(mvcResult.getResponse().getContentAsString() != null && !mvcResult.getResponse().getContentAsString().isEmpty());
		assertTrue(mvcResult.getResponse().getContentAsString().contains("\"sirenCP\":\"123\""));
	}
	
	@Test
//	@Ignore
	public void testGetScot() throws Exception {
		ScotDataSheet sds=getDummyScotDataSheetObject();
		when(scotService.findById(TEST_SIREN_CP)).thenReturn(sds);
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get("/scot/getscot/{siren_cp}", TEST_SIREN_CP).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		
		assertTrue(mvcResult.getResponse().getContentAsString() != null && !mvcResult.getResponse().getContentAsString().isEmpty());
		assertTrue(mvcResult.getResponse().getContentAsString().contains("\"sirenCP\":\"123\""));
	}
	
	@Test
//	@Ignore
	public void testGetScot_InvalidSirenCP() throws Exception {
		when(scotService.findById(TEST_SIREN_CP)).thenReturn(null);
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get("/scot/getscot/{siren_cp}", TEST_SIREN_CP).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();
		
		assertTrue(mvcResult.getResponse().getContentAsString().isEmpty());
	}
	
	@Test
//	@Ignore
	public void testDeleteScot() throws Exception {
		when(scotService.existsById(TEST_SIREN_CP)).thenReturn(true);
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.delete("/scot/deletescot/{siren_cp}", TEST_SIREN_CP).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		
		assertEquals("Deleted", mvcResult.getResponse().getContentAsString());
	}
	
	@Test
//	@Ignore
	public void testDeleteScot_InvalidSirenCP() throws Exception {
		when(scotService.existsById(TEST_SIREN_CP)).thenReturn(false);
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.delete("/scot/deletescot/{siren_cp}", TEST_SIREN_CP).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();
		
		assertEquals("Does not exist", mvcResult.getResponse().getContentAsString());
	}
	
	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String mapToJson(Object obj) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(obj);
	}

	protected <T> T mapFromJson(String json, Class<T> clazz)
			throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, clazz);
	}
	
	private static ScotDataSheet getDummyScotDataSheetObject() {
		ScotDataSheet sdt=new ScotDataSheet();
		sdt.setSirenCP(TEST_SIREN_CP);
		sdt.setStatus("Pending");
		sdt.setComment("test_comment");
		sdt.setApprobationDate(new Date());
		sdt.setCollectingDate(new Date());
		sdt.setFilesMetadata(getDummyScotDataSheetFileMetadataObject());
		return sdt;
	}
	
	private static final List<ScotFileMetadata> getDummyScotDataSheetFileMetadataObject() {
		List<ScotFileMetadata> slist=new ArrayList<>();
		
		ScotFileMetadata sfmd=new ScotFileMetadata();
		sfmd.setFileName("test_1.pdf");
		sfmd.setOriginalURL("https://www.antennahouse.com/XSLsample/pdf/sample-link_1.pdf");
		sfmd.setType("Report");
		slist.add(sfmd);
		
		ScotFileMetadata sfmd1=new ScotFileMetadata();
		sfmd1.setFileName("test_2.pdf");
		sfmd1.setOriginalURL("http://www.africau.edu/images/default/sample.pdf");
		sfmd1.setType("Padd");
		slist.add(sfmd1);
		
		return slist;

	}
	
	
}
