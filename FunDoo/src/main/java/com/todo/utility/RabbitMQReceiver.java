package com.todo.utility;

import javax.mail.MessagingException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.todo.userservice.model.Mail;

//@Service
public class RabbitMQReceiver {

	@Autowired
	MailFunctionImpl mailService;
	
	@RabbitListener(queues="todo.queue")
	public void receive(Mail mail) throws MessagingException 
	{
		System.out.println("entering receiver");
		mailService.sendMail(mail.getTo(), mail.getSubject(), mail.getBody());
	}
}
