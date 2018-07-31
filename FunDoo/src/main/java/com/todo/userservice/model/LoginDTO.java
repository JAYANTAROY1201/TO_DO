package com.todo.userservice.model;

import java.io.Serializable;

/**
 * purpose: This class is designed to take login credentials
 * 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 10/07/18
 */
public class LoginDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String email;
	private String password;

	public LoginDTO() {
	};

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LoginDTO [email=" + email + ", password=" + password + "]";
	}

}
