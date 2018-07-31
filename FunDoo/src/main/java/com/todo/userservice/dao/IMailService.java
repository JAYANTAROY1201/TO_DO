package com.todo.userservice.dao;

import javax.mail.MessagingException;

/**
 * purpose: Sending mail
 * 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 16/07/18
 */

public interface IMailService {
	/**
	 * This method will send mail
	 * 
	 * @param mail
	 * @throws MessagingException
	 */
	public void sendMail(String to, String subject, String body) throws MessagingException;

}
