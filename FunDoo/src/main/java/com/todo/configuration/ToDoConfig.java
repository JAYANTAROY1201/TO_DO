//package com.todo.configuration;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//@Configuration
//public class ToDoConfig {
//	 @Bean
//	  public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
//	    Resource resource;
//	    String activeProfile;
//	     
//	    PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer =  new PropertySourcesPlaceholderConfigurer();
//	     
//	    // get active profile
//	    activeProfile = System.getProperty("spring.profiles.active");
//	 
//	    // choose different property files for different active profile
//	    if ("development".equals(activeProfile)) {
//	      resource = new ClassPathResource("/META-INF/development.properties");
//	    } else if ("test".equals(activeProfile)) {
//	      resource = new ClassPathResource("/META-INF/test.properties");
//	    } else {
//	      resource = new ClassPathResource("/META-INF/production.properties");
//	    }
//	     
//	    // load the property file
//	    propertySourcesPlaceholderConfigurer.setLocation(resource);
//	     
//	    return propertySourcesPlaceholderConfigurer;
//	  }
//	}

