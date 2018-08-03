package com.todo.userservice.services;

import java.net.UnknownHostException;

import javax.mail.MessagingException;

import com.todo.exception.AccountActivationException;
import com.todo.exception.LoginException;
import com.todo.exception.SignupException;
import com.todo.userservice.model.LoginDTO;
import com.todo.userservice.model.User;

/**
 * purpose: Interface to defice general user services
 * 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 10/07/18
 */
public interface IGeneralUserService {

	/**
	 * @param user
	 * @throws SignupException
	 */
	public void doSignUp(User user) throws SignupException;

	/**
	 * @param loginCredentials
	 * @return
	 * @throws LoginException
	 */
	public String doLogIn(LoginDTO loginCredentials) throws LoginException;

	/**
	 * @param jwtToken
	 * @param newPassword
	 */
	public void doResetPassword(String jwtToken, String newPassword);

	/**
	 * @param to
	 * @param jwt
	 * @throws MessagingException
	 * @throws UnknownHostException
	 */
	public void sendActivationLink(String to, String jwt) throws MessagingException, UnknownHostException;

	/**
	 * @param seqName
	 * @return
	 */
	public String getNextSequence(String seqName);

	/**
	 * @param email
	 * @throws LoginException
	 * @throws MessagingException
	 */
	public void doSendNewPasswordLink(String email) throws LoginException, MessagingException;

	/**
	 * @param jwt
	 * @throws AccountActivationException
	 */
	public void doActivateEmail(String jwt) throws AccountActivationException;
}
