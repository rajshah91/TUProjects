package com.oppscience.sgevt.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
//@ComponentScan(basePackages = {"com.oppscience.sgevt.webapp","com.oppscience.sgevt.webapp.controller",
//		"com.oppscience.sgevt.webapp.model","com.oppscience.sgevt.webapp.repository",
//		"com.oppscience.sgevt.webapp.service","com.oppscience.sgevt.webapp.config"})
public class SgevtWebappApplication {

	public static void main(String[] args) {
		SpringApplication.run(SgevtWebappApplication.class, args);
	}

}
