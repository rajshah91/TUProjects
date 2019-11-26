package com.oppscience.sgevt.webapp.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oppscience.sgevt.webapp.dto.UserListDto;
import com.oppscience.sgevt.webapp.model.User;
import com.oppscience.sgevt.webapp.model.UserList;

import com.oppscience.sgevt.webapp.service.PropertyReaderService;
import com.oppscience.sgevt.webapp.service.UserListService;
import com.oppscience.sgevt.webapp.service.UserService;

@RunWith(SpringRunner.class)
@WebMvcTest(value = RestApiController.class, secure = false)
@ActiveProfiles("test")
//@TestPropertySource("classpath:application.properties")
public class RestApiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserListService userListService;

	@MockBean
	private UserService userService;
	
	@MockBean
	private PropertyReaderService propertyReaderService;

	@Before
	public void init() {
		when(propertyReaderService.getEnvironmentValue("api.user.userlist.save.success")).thenReturn("success");
		when(propertyReaderService.getEnvironmentValue("api.user.userlist.save.failure")).thenReturn("fail");
		when(propertyReaderService.getEnvironmentValue("api.user.userlist.save.exists")).thenReturn("exists");
		when(propertyReaderService.getEnvironmentValue("api.user.userlist.delete.success")).thenReturn("success");
		when(propertyReaderService.getEnvironmentValue("api.user.userlist.delete.failure")).thenReturn("fail");
	}
	
	private final static Long USER_LIST_ID = 1L;
	private final static Long USER_ID = 1L;

	private static final String USER_LIST_NAME = "mylist";

	@Test
	public void testSaveUserList_OnValidInput() throws Exception {
		String inputJson = mapToJson(getDummyUserListDtoObject());

		when(userListService.findById(USER_LIST_ID)).thenReturn(getDummyUserListObject());
		when(userService.findByUserId(USER_ID)).thenReturn(getDummyUserObject());
		when(userListService.save(getDummyUserListObject())).thenReturn(getDummyUserListObject());

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/add/userlist/user/{userId}", USER_ID)
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

		assertEquals(200, mvcResult.getResponse().getStatus());
		assertEquals("success", mvcResult.getResponse().getContentAsString());
		
	}
	
	@Test
	public void testSaveUserList_OnInvalidInput_WithNullName() throws Exception {
		UserListDto uldo=getDummyUserListDtoObject();
		uldo.setName(null);
		String inputJson = mapToJson(uldo);

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/add/userlist/user/{userId}", USER_ID)
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

		assertEquals(200, mvcResult.getResponse().getStatus());
		assertEquals("fail", mvcResult.getResponse().getContentAsString());
		
	}
	
	@Test
	public void testSaveUserList_OnInvalidInput_WithExistingListName() throws Exception {
		UserListDto uldo=getDummyUserListDtoObject();
		String inputJson = mapToJson(uldo);
		List<UserList> ul= new ArrayList<>();
		ul.add(getDummyUserListObject());
		
		when(userListService.findByListName(uldo.getName())).thenReturn(ul);
		
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/add/userlist/user/{userId}", USER_ID)
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

		assertEquals(200, mvcResult.getResponse().getStatus());
		assertEquals("exists", mvcResult.getResponse().getContentAsString());
		
	}
	
	@Test
	public void testSaveUserList_OnInvalidInput_WithNullUser() throws Exception {
		UserListDto uldo=getDummyUserListDtoObject();
		String inputJson = mapToJson(uldo);
		
		when(userListService.findByUserId(uldo.getId())).thenReturn(null);
		
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/add/userlist/user/{userId}", USER_ID)
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

		assertEquals(200, mvcResult.getResponse().getStatus());
		assertEquals("fail", mvcResult.getResponse().getContentAsString());
		
	}
	
	@Test
	public void testGetUserListByUserId_WithValidInput() throws Exception {
		List<UserList> ul= new ArrayList<>();
		ul.add(getDummyUserListObject());
		when(userListService.findByUserId(USER_ID)).thenReturn(ul);
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get("/get/userlist/userId/{userId}", USER_ID).accept(MediaType.APPLICATION_JSON))
//				.andDo(print())
				.andExpect(status().isOk()).andReturn();
		
		assertEquals("[{\"id\":1,\"name\":\"mylist\",\"ids\":[\"id1\",\"id2\"]}]", mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void testGetUserListByUserId_WithInValidInput_UserNotExists() throws Exception {
		when(userListService.findByUserId(USER_ID)).thenReturn(new ArrayList<>());
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get("/get/userlist/userId/{userId}", USER_ID).accept(MediaType.APPLICATION_JSON))
//				.andDo(print())
				.andExpect(status().isOk()).andReturn();
		
		assertEquals("[]", mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void testGetUserListById_WithValidInput() throws Exception {
		when(userListService.findById(USER_LIST_ID)).thenReturn(getDummyUserListObject());
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get("/get/userlist/id/{id}", USER_LIST_ID).accept(MediaType.APPLICATION_JSON))
//				.andDo(print())
				.andExpect(status().isOk()).andReturn();
		
		assertEquals("{\"id\":1,\"name\":\"mylist\",\"ids\":[\"id1\",\"id2\"]}", mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void testGetUserListById_WithInValidInput() throws Exception {
		when(userListService.findById(USER_LIST_ID)).thenReturn(null);
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get("/get/userlist/id/{id}", USER_LIST_ID).accept(MediaType.APPLICATION_JSON))
//				.andDo(print())
				.andExpect(status().isOk()).andReturn();
		
		assertEquals("", mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void testGetUserListByListName_WithValidInput() throws Exception {
		List<UserList> ul= new ArrayList<>();
		ul.add(getDummyUserListObject());
		when(userListService.findByListName(USER_LIST_NAME)).thenReturn(ul);
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get("/get/userlist/name/{listName}", USER_LIST_NAME).accept(MediaType.APPLICATION_JSON))
//				.andDo(print())
				.andExpect(status().isOk()).andReturn();
		
		assertEquals("[{\"id\":1,\"name\":\"mylist\",\"ids\":[\"id1\",\"id2\"]}]", mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void testGetUserListByListName_WithInValidInput() throws Exception {
		List<UserList> ul= new ArrayList<>();
		when(userListService.findByListName(USER_LIST_NAME)).thenReturn(ul);
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get("/get/userlist/name/{listName}", USER_LIST_NAME).accept(MediaType.APPLICATION_JSON))
//				.andDo(print())
				.andExpect(status().isOk()).andReturn();
		
		assertEquals("[]", mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void testDeleteUserListById_WithValidInput() throws Exception {
		when(userListService.deleteById(USER_LIST_ID)).thenReturn(Boolean.TRUE);
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.delete("/delete/userlist/id/{id}", USER_LIST_ID).accept(MediaType.APPLICATION_JSON))
//				.andDo(print())
				.andExpect(status().isOk()).andReturn();
		
		assertEquals("success", mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void testDeleteUserListById_WithInValidInput() throws Exception {
		when(userListService.deleteById(USER_LIST_ID)).thenReturn(Boolean.FALSE);
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.delete("/delete/userlist/id/{id}", USER_LIST_ID).accept(MediaType.APPLICATION_JSON))
//				.andDo(print())
				.andExpect(status().isOk()).andReturn();
		
		assertEquals("fail", mvcResult.getResponse().getContentAsString());
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
	
	private static final User getDummyUserObject() {
		User user = new User();
		user.setId(1L);
		user.setUsername("testuser");
		return user;
	}

	private static final UserList getDummyUserListObject() {
		User user = getDummyUserObject();
		UserList ul = new UserList(USER_LIST_ID,USER_LIST_NAME,"id1,id2",user);
		return ul;
	}
	
	private static final List<UserList> getMultipleDummyUserList(){
		List<UserList> ulList= new ArrayList<>();
		User user = getDummyUserObject();
		UserList ul1 = new UserList(USER_LIST_ID,USER_LIST_NAME,"id1,id2",user);
		UserList ul2 = new UserList(2L,"mylist2","id3,id4",user);
		ulList.add(ul1);
		ulList.add(ul2);
		return ulList;
	}
	
	private UserListDto getDummyUserListDtoObject() {
		return new UserListDto(1L, "mylist", new String[] { "id1", "id2" });
	}
}
