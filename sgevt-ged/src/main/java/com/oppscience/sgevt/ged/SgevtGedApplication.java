package com.oppscience.sgevt.ged;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class SgevtGedApplication {
	
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SgevtGedApplication.class);
    }
	
	public static void main(String[] args) {
		SpringApplication.run(SgevtGedApplication.class, args);
	}

}
