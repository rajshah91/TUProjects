package com.oppscience.sgevt.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.oppscience.sgevt.webapp.model.User;
import com.oppscience.sgevt.webapp.service.UserService;

import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Controller
public class AppController {

	@Autowired
	private UserService userService;

	@GetMapping({ "/", "/index" })
	public String indexPage() {
		return "index";
	}

	@GetMapping({ "/temp" })
	public ModelAndView temp() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByUsername(auth.getName());
		modelAndView.addObject("userName",user.getUsername() );
		modelAndView.addObject("message","Welcome " + user.getUsername() );
		modelAndView.setViewName("temp");
		return modelAndView;
	}
}
