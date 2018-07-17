package com.todo.globalexception;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.todo.exception.AccountActivationException;
import com.todo.exception.LoginException;
import com.todo.exception.SignupException;
import com.todo.userservice.model.ResponseBean;

/**
 * purpose: 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 10/07/18
 */
@ControllerAdvice
public class GlobalExceptionHandler {
ResponseBean response= new ResponseBean();
private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

/**
 * @param exception
 * @param request
 * @return
 */
@ExceptionHandler(SignupException.class)
public ResponseEntity<ResponseBean> registrationExceptionHandler(SignupException exception,
		HttpServletRequest request) {
	logger.info("Exception encountered at " + request.getRequestURI() + exception.getMessage());
	response.setMessage(exception.getMessage());
	response.setStatus(-1);
	return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
}

/**
 * @param exception
 * @param request
 * @return
 */
@ExceptionHandler(LoginException.class)
public ResponseEntity<ResponseBean> loginExceptionHandler(LoginException exception, HttpServletRequest request) {
	logger.info("Exception encountered at " + request.getRequestURI() + ":  "+ exception.getMessage());
	response.setMessage(exception.getMessage());
	response.setStatus(-2);
	return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
}

@ExceptionHandler(AccountActivationException.class)
public ResponseEntity<ResponseBean> activationExceptionHandler(AccountActivationException exception, HttpServletRequest request) {
	logger.info("Exception encountered at " + request.getRequestURI() + ":  "+ exception.getMessage());
	response.setMessage(exception.getMessage());
	response.setStatus(-3);
	return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); 
}
}
