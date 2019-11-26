package com.oppscience.sgevt.webapp.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//@Controller
public class ErrorPageController implements ErrorController{
    private final static String PATH = "/error";
    @Override
    @RequestMapping(PATH)
    @ResponseBody
    public String getErrorPath() {
        // TODO Auto-generated method stub
        return "No Mapping Found";
    }

}