package com.todo.noteservice.dao;

/**
 * @author jayanta roy
 *
 */
public interface IRedisRepository {

	/**
	 * @param token
	 */
	void setToken(String token);
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
