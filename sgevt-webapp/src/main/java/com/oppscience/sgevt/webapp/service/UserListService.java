package com.oppscience.sgevt.webapp.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.oppscience.sgevt.webapp.dto.UserListDto;
import com.oppscience.sgevt.webapp.model.UserList;

@Component
public interface UserListService {

	public UserList save(UserList userList);

	public List<UserList> findByListName(String listName);

	public List<UserList> findByUserId(Long userId);

	public UserList findById(Long id);

	boolean deleteById(Long userListId);

	public static UserList getUserListObjectFromDTO(UserListDto uld) {
		UserList ul = new UserList();
		ul.setListName(uld.getName());
		ul.setId(uld.getId());

		StringBuilder sb = new StringBuilder();
		StringUtils.join(uld.getIds(), ',', sb);
		ul.setListIds(sb.toString());

		return ul;
	}

	public static UserListDto getUserListDtoObjFromOriginal(UserList userList) {
		UserListDto uld = new UserListDto();
		if (userList != null && userList.getId() != null) {
			uld.setId(userList.getId());
			uld.setName(userList.getListName());
			uld.setIds(userList.getListIds().split(","));
		}
		return uld;
	}

	public static List<UserListDto> getUserListDtoObjFromOriginal(List<UserList> userList) {
		List<UserListDto> uld = new ArrayList<UserListDto>();
		if (userList != null && userList.size() > 0) {
			for (int i = 0; i < userList.size(); i++) {
				UserList ul = userList.get(i);
				UserListDto uuld = getUserListDtoObjFromOriginal(ul);
				if (uuld != null)
					uld.add(uuld);
			}
		}
		return uld;
	}

}
