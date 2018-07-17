package com.todo.exception;

/**
 * purpose: To handle the exception that occurs during login
 * @author JAYANTA ROY
 * @version 1.0
 * @since 10/07/18
 */
public class LoginException extends Exception {

	private static final long serialVersionUID = 1L;

	public LoginException(String message) {
		super(message);
	}
}
