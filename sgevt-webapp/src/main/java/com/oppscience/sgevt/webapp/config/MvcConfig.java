package com.oppscience.sgevt.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.oppscience.sgevt.webapp.service.PropertyReaderService;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

	@Autowired
	private PropertyReaderService propertyReaderService;
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").
		addResourceLocations(propertyReaderService.getEnvironmentValue("spring.resources.static-locations"));

		registry.addResourceHandler("swagger-ui.html").
		addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");
		
		registry.addResourceHandler("/webjars/**").
		addResourceLocations("classpath:/META-INF/resources/webjars/");

	}
}
