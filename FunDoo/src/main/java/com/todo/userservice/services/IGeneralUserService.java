package com.todo.userservice.services;

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

	public void doSignUp(User user) throws SignupException;

	public String doLogIn(LoginDTO loginCredentials) throws LoginException;

	public void doResetPassword(String jwtToken, String newPassword);
}
