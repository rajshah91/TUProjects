package com.oppscience.sgevt.webapp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oppscience.sgevt.webapp.dto.UserListDto;
import com.oppscience.sgevt.webapp.model.UserList;
import com.oppscience.sgevt.webapp.repository.UserListRepository;

@Service
public class UserListServiceImpl implements UserListService {

	@Autowired
	private UserListRepository userListRepository;

	@Override
	public UserList save(UserList userList) {
		return userListRepository.save(userList);
	}

	@Override
	public List<UserList> findByListName(String listName) {
		return userListRepository.findByListName(listName);
	}

	@Override
	public List<UserList> findByUserId(Long userId) {
		return userListRepository.findByUserId(userId);
	}

	@Override
	public UserList findById(Long userListId) {
		Optional<UserList> userlist = userListRepository.findById(userListId);
		return userlist.isPresent() ? userlist.get() : null;
	}
	
	@Override
	public boolean deleteById(Long userListId) {
		boolean isDeleted=false;
		try {
			userListRepository.deleteById(userListId);
			isDeleted=true;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return isDeleted;
	}
	
}
