package com.oppscience.sgevt.webapp.service;

import com.oppscience.sgevt.webapp.model.User;

public interface UserService {
    
	public void save(User user);

    public User findByUsername(String username);
    
    public User findByUserId(Long id);
    
    
   
}
