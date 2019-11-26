package com.oppscience.sgevt.webapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.oppscience.sgevt.webapp.dto.UserListDto;
import com.oppscience.sgevt.webapp.model.User;
import com.oppscience.sgevt.webapp.model.UserList;
import com.oppscience.sgevt.webapp.service.PropertyReaderService;
import com.oppscience.sgevt.webapp.service.UserListService;
import com.oppscience.sgevt.webapp.service.UserService;

import io.swagger.annotations.ApiOperation;

@RestController
public class RestApiController {

	@Autowired
	private UserListService userListService;

	@Autowired
	private UserService userService;

	@Autowired
	private PropertyReaderService propertyReaderService;

	@ApiOperation(value = "saveUserList", notes = "<b>Save userlist for logged in user.</b>")
	@PostMapping(path = "/add/userlist")
	@ResponseBody
	public String saveUserList(@RequestBody UserListDto userListDto) {
		return handleUserListSave(userListDto, null);
	}
	
	@ApiOperation(value = "saveUserList", notes = "<b>Save userlist for given User Id.</b>")
	@PostMapping(path = "/add/userlist/user/{userId}")
	@ResponseBody
	public String saveUserListWithUserId(@RequestBody UserListDto userListDto , @PathVariable("userId") Long userId) {
		return handleUserListSave(userListDto, userId);
	}
	
	private String handleUserListSave(UserListDto userListDto, Long userId) {
		if (userListDto != null && !StringUtils.isEmpty(userListDto.getName())) {

			List<UserList> ul = userListService.findByListName(userListDto.getName());
			if (ul != null && ul.size() > 0) {
				return getProperty("api.user.userlist.save.exists");
			} else {
				UserList ulist = UserListService.getUserListObjectFromDTO(userListDto);
				User user= (userId == null ? getUserFromContext() : userService.findByUserId(userId));
				if(user != null) {
					ulist.setUser(user);
					UserList ulst = userListService.save(ulist);
					if (ulst != null && ulst.getId() != null) {
						return getProperty("api.user.userlist.save.success");
					} else {
						return getProperty("api.user.userlist.save.failure");
					}
				}else {
					return getProperty("api.user.userlist.save.failure");
				}
				
			}

		} else {
			return getProperty("api.user.userlist.save.failure");
		}
	}
	
	@ApiOperation(value = "getUserListByUserId", notes = "<b>Get userlist by User Id.</b>")
	@GetMapping(path = { "/get/userlist/userId/{userId}", "/get/userlist/" })
	@ResponseBody
	public List<UserListDto> getUserListByUserId(@PathVariable(value = "userId", required = false) Long userId) {
		if (userId == null) {
			userId = getUserIdFromContext();
		}
		List<UserList> ul = userListService.findByUserId(userId);
		return UserListService.getUserListDtoObjFromOriginal(ul);
	}
	
	@ApiOperation(value = "getUserListByUserListId", notes = "<b>Get userlist by UserList Id.</b>")
	@GetMapping(path = "/get/userlist/id/{id}")
	@ResponseBody
	public UserListDto getUserListById(@PathVariable("id") Long id) {
		UserList ul = userListService.findById(id);
		return ul != null ? UserListService.getUserListDtoObjFromOriginal(ul) : null;
	}
	
	@ApiOperation(value = "getUserListByListName", notes = "<b>Get userlist by UserList Name.</b>")
	@GetMapping(path = "/get/userlist/name/{listName}")
	@ResponseBody
	public List<UserListDto> getUserListByListName(@PathVariable("listName") String listName) {
		List<UserList> ul = userListService.findByListName(listName);
		return UserListService.getUserListDtoObjFromOriginal(ul);
	}
	
	@ApiOperation(value = "deleteUserListById", notes = "<b>Delete userlist by UserList Id.</b>")
	@DeleteMapping(path = "/delete/userlist/id/{id}")
	public String deleteUserListById(@PathVariable("id") Long id) {
		boolean isDeleted = userListService.deleteById(id);
		return isDeleted ? getProperty("api.user.userlist.delete.success")
				: getProperty("api.user.userlist.delete.failure");
	}

	private String getProperty(String property) {
		return propertyReaderService.getEnvironmentValue(property);
	}

	private User getUserFromContext() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return userService.findByUsername(auth.getName());
	}

	private Long getUserIdFromContext() {
		User user = getUserFromContext();
		return (user != null && user.getId() != null) ? user.getId() : null;
	}

}
