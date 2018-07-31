package com.todo.noteservice.dao;

/**
 * @author jayanta roy
 *
 */

@SuppressWarnings("hiding")
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
