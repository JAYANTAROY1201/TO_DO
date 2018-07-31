package com.todo.exception;

/**
 * <p>
 * purpose: This exception class is designed to throw exception that occur
 * during activation of account
 * </p>
 * 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 12/07/18
 */
public class AccountActivationException extends Exception {
	private static final long serialVersionUID = 1L;

	public AccountActivationException(String message) {
		super(message);
	}
}
