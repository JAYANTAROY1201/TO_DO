package com.todo.utility;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.todo.userservice.model.Mail;

/**
 * purpose:This class take messages from service class and set it into queue
 * 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 20/07/18
 */
@Service
public class RabbitMQSender {
	@Autowired
	private AmqpTemplate rabbitTemplate;

	Mail mail = new Mail();

	@Value("${todo.rabbitmq.exchange}")
	private String exchange;

	@Value("${todo.rabbitmq.routingkey}")
	private String routingkey;

	public void send(String to, String subject, String body) {
		System.out.println("Entering sender");
		mail.setBody(body);
		mail.setTo(to);
		mail.setSubject(subject);
		rabbitTemplate.convertAndSend(exchange, routingkey, mail);

	}
}
