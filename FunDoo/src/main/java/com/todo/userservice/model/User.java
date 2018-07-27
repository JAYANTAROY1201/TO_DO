package com.todo.userservice.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import io.swagger.annotations.ApiModelProperty;

/**
 * purpose: 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 14/07/18
 */
@Document(collection="user")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@ApiModelProperty(hidden = true)
	private String _id;
	private String userName;
	private String email;
	private String mobile;
	private String password;
	@ApiModelProperty(hidden = true)
	private String activation;
	
	public User() {}
	
	/**
	 * @return
	 */
	@Id
	public String get_id() {
		return _id;
	}
	/**
	 * @param _id
	 */
	public void set_id(String _id) {
		this._id = _id;
	}
	/**
	 * @return
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return
	 */
	public String getMobile() {
		return mobile;
	}
	/**
	 * @param mobile
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	/**
	 * @return
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return
	 */
	public String getActivation() {
		return activation;
	}
	/**
	 * @param activation
	 */
	public void setActivation(String activation) {
		this.activation = activation;
	}
	
	/** (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "User [_id=" + _id + ", userName=" + userName + ", email=" + email + ", mobile=" + mobile + ", password="
				+ password + ", activation=" + activation + "]";
	}

}
