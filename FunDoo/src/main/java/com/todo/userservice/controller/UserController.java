package com.todo.userservice.controller;

import java.net.UnknownHostException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todo.exception.AccountActivationException;
import com.todo.exception.LoginException;
import com.todo.exception.SignupException;
import com.todo.userservice.model.LoginDTO;
import com.todo.userservice.model.User;
import com.todo.userservice.services.UserServiceImpl;
import com.todo.utility.JwtTokenBuilder;
import com.todo.utility.Messages;

/**
 * <p>
 * Purpose: This class is designed to control functionality of user
 * </p>
 * <br>
 * 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 10/07/18
 */
@RestController
@RequestMapping("/fundoo/user")
public class UserController {
	public static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserServiceImpl userService;

	@Autowired
	Messages messages;

	/**
	 * Method to control signup service
	 * 
	 * @param user
	 * @return
	 * @throws SignupException
	 * @throws MessagingException
	 * @throws UnknownHostException
	 */
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public ResponseEntity<String> signUp(@RequestBody User user)
			throws SignupException, MessagingException, UnknownHostException {

		userService.doSignUp(user);
		logger.info(messages.get("200"), user.getEmail());

		JwtTokenBuilder jwt = new JwtTokenBuilder();
		userService.sendActivationLink(user.getEmail(), jwt.createJWT(user));
		logger.info(messages.get("202"));

		return new ResponseEntity<String>(messages.get("201"), HttpStatus.OK);
	}

	/**
	 * @param email
	 * @param password
	 * @param hsr
	 * @return
	 * @throws LoginException
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<String> logIn(@RequestBody LoginDTO loginCredentials, HttpServletResponse hsr)
			throws LoginException {

		String JWTToken = userService.doLogIn(loginCredentials);

		hsr.setHeader("JWTToken", JWTToken);

		return new ResponseEntity<String>(messages.get("203"), HttpStatus.OK);
	}

	/**
	 * this method is written to make account activated after successful sign in
	 * 
	 * @param hsr
	 * @return response entity
	 * @throws AccountActivationException
	 */
	@RequestMapping(value = "/activateaccount", method = RequestMethod.GET)
	public ResponseEntity<String> activateAccount(HttpServletRequest hsr) throws AccountActivationException {
		String token = hsr.getQueryString();

		userService.doActivateEmail(token);

		String message = "Account activated successfully";
		logger.info(message);
		return new ResponseEntity<String>(messages.get("204"), HttpStatus.OK);
	}

	/**
	 * This method is written to get forgotten password
	 * 
	 * @param eb
	 * @return response entity
	 * @throws LoginException
	 * @throws MessagingException
	 */
	@RequestMapping(value = "/forgetpassword", method = RequestMethod.POST)
	public ResponseEntity<String> forgetPassword(@RequestParam String email) throws LoginException, MessagingException {

		userService.doSendNewPasswordLink(email);

		logger.info(messages.get("205"));
		return new ResponseEntity<String>(messages.get("206"), HttpStatus.OK);
	}

	/**
	 * This method is written to get forgotten password
	 * 
	 * @param eb
	 * @return response entity
	 * @throws LoginException
	 */
	@RequestMapping(value = "/resetpassword", method = RequestMethod.POST)
	public ResponseEntity<String> resetPassword(@RequestBody User user, HttpServletRequest hsr)

	{
		String jwtToken = hsr.getQueryString();
		userService.doResetPassword(jwtToken, user.getPassword());
		logger.info(messages.get("207"));
		return new ResponseEntity<String>(messages.get("208"), HttpStatus.OK);
	}

}
