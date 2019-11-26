package com.oppscience.sgevt.webapp.service;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ComponentScan("com.oppscience.sgevt.webapp")
@PropertySource("classpath:application.properties")
public class PropertyReaderService implements EnvironmentAware {
	
	@Autowired
	private Environment environment;

	
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
	

	public String getEnvironmentValue(String key) {
		return environment.getProperty(key);
	}
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
	    return new PropertySourcesPlaceholderConfigurer();
	}

}
