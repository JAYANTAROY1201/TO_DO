package com.todo.userservice.model;

import java.io.Serializable;

/**
 * purpose: 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 10/07/18
 */
public class Mail implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String to;
	private String subject;
	private String body;
	
	public Mail() {}
	
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "Mail [to=" + to + ", subject=" + subject + ", body=" + body + "]";
	}
	
	
}
