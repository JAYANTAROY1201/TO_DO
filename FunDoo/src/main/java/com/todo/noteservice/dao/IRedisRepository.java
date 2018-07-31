package com.todo.noteservice.dao;

/**
 * purpose: Repository for redis
 * 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 10/07/18
 */
@SuppressWarnings("hiding")
public interface IRedisRepository<String, User> {

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
	public void deleteToken(String userId);

}
