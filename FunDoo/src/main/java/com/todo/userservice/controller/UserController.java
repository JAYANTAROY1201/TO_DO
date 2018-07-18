package com.todo.userservice.controller;

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
import com.todo.userservice.model.User;
import com.todo.userservice.services.UserServiceImpl;
import com.todo.utility.JwtTokenBuilder;

@RestController
@RequestMapping("/fundoo/user")
public class UserController {
	public static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserServiceImpl userService = new UserServiceImpl();

	/**
	 * Method to control signup service
	 * 
	 * @param user
	 * @return
	 * @throws SignupException
	 * @throws MessagingException
	 */
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public ResponseEntity<String> signUp(@RequestBody User user) {
		try {
			userService.doSignUp(user);
		} catch (Exception e) {
			return new ResponseEntity<String>(e + "", HttpStatus.BAD_REQUEST);
		}
		logger.info("Employee registered with : {}", user.getEmail());
		String message = "Sign Up Successful";
		JwtTokenBuilder jwt = new JwtTokenBuilder();
		try {
			userService.sendActivationLink(user.getEmail(), jwt.createJWT(user));
		} catch (Exception e) {
			return new ResponseEntity<String>(e + "", HttpStatus.BAD_REQUEST);
		}
		logger.info("Activation link sent to email");
		return new ResponseEntity<String>(message, HttpStatus.OK);
	}

	/**
	 * @param emp
	 * @return
	 * @throws LoginException
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<String> logIn(@RequestParam String email, @RequestParam String password,
			HttpServletResponse hsr) {
		String LoginJwt;
		try {
			LoginJwt = userService.doLogIn(email, password);
		} catch (LoginException e) {
			return new ResponseEntity<String>(e + "", HttpStatus.BAD_REQUEST);
		}
		hsr.setHeader("jwt", LoginJwt);
		return new ResponseEntity<String>("login successful:\n", HttpStatus.OK);
	}

	/**
	 * this method is written to make account activated after successful sign in
	 * 
	 * @param hsr
	 * @return response entity
	 * @throws AccountActivationException
	 */
	@RequestMapping(value = "/activateaccount", method = RequestMethod.GET)
	public ResponseEntity<String> activateAccount(HttpServletRequest hsr) {
		String token = hsr.getQueryString();
		try {
			userService.doActivateEmail(token);
		} catch (AccountActivationException e) {
			return new ResponseEntity<String>(e + "", HttpStatus.OK);
		}
		String message = "Account activated successfully";
		logger.info(message);
		return new ResponseEntity<String>(message, HttpStatus.OK);
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
	public ResponseEntity<String> forgetPassword(@RequestParam String email) {

		try {
			userService.doSendNewPasswordLink(email);
		} catch (LoginException | MessagingException e) {
			return new ResponseEntity<String>(e + "", HttpStatus.BAD_REQUEST);
		}
		logger.info("New password reset link sent to email");
		return new ResponseEntity<String>("Password sent to email", HttpStatus.OK);
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
		logger.info("New password set successfully");
		return new ResponseEntity<String>("New password set successfully", HttpStatus.OK);
	}

}
