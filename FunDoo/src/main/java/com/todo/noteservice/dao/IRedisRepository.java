package com.todo.noteservice.dao;

import org.springframework.stereotype.Repository;

/**
 * @author jayanta roy
 *
 */

public interface IRedisRepository <String,User>{

	/**
	 * @param token
	 */
	public void setToken(String token);
	/**
	 * @param userId
	 * @return
	 */
	public String getToken(String userId);
	/**
	 * @param userId
	 */
	public void deleteToken(String userId) ;

}
