package com.todo.utility;

import javax.mail.MessagingException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.todo.userservice.model.Mail;

/**
 * purpose: This class recieve the messages from rabbitMQ queue and tranfer to
 * client
 * 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 20/07/18
 */
@Service
public class RabbitMQReceiver {

	@Autowired
	MailFunctionImpl mailService;

	@RabbitListener(queues = "${todo.rabbitmq.queue}")
	public void receive(Mail mail) throws MessagingException {
		System.out.println("entering receiver");
		mailService.sendMail(mail.getTo(), mail.getSubject(), mail.getBody());
	}
}
