package com.todo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.todo.utility.ToDoInterceptor;

public class InterceptorConfig implements WebMvcConfigurer{
	@Autowired
	private ToDoInterceptor toDoInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	registry.addInterceptor(toDoInterceptor).addPathPatterns("/fundoo/note/**");									
	}
}


