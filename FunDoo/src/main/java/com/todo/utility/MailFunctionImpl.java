package com.todo.utility;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.todo.userservice.dao.IMailService;



/**
 * purpose: Implementation class for sending mail
 * @author JAYANTA ROY
 * @version 1.0
 * @since 10/07/18
 */
@Component
public class MailFunctionImpl implements IMailService{

	@Autowired
	private JavaMailSender javaMailSender;
	

	
	/** (non-Javadoc)
	 * @see com.todo.userservice.dao.IMailService#sendMail(com.todo.userservice.model.Mail)
	 */
	@Override
	public void sendMail(String to,String subject,String body) throws MessagingException {
		
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setSubject(subject);
		helper.setTo(to);
		helper.setText(body);
		
		javaMailSender.send(message);
	}
}