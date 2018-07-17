package com.todo.userservice.model;

import java.io.Serializable;

/**
 * purpose: 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 10/07/18
 */
public class signUpModel implements Serializable{

	private static final long serialVersionUID = 1L;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	private String userName;
	private String email;
	private String mobile;
	private String password;

}
