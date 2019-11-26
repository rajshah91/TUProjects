package com.oppscience.sgevt.webapp.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.oppscience.sgevt.webapp.model.User;
import com.oppscience.sgevt.webapp.service.SecurityService;
import com.oppscience.sgevt.webapp.service.UserService;

import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

//    @Autowired
//    private UserValidator userValidator;
//    <!-- https://www.onlinetutorialspoint.com/spring-boot/spring-boot-validation-login-form-example.html -->
  /*  @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());

        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult) {
//        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        userService.save(userForm);

        securityService.autoLogin(userForm.getUsername(), userForm.getPasswordConfirm());

        return "redirect:/welcome";
    }
*/
    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        
        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "login";
    }
}