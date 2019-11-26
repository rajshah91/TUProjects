package com.oppscience.sgevt.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oppscience.sgevt.webapp.model.UserList;

@Repository
public interface UserListRepository extends JpaRepository<UserList, Long>{
	
	List<UserList> findByListName(String listName);
	
	List<UserList> findByUserId(Long userId);
	
}
