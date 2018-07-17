package com.todo.exception;

/**
 * purpose:To handle the exception that occurs during signup
 * 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 10/07/18
 */
public class SignupException extends Exception {

	private static final long serialVersionUID = 1L;

	public SignupException(String message) {
		super(message);
	}
}
