package com.todo.utility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import com.todo.noteservice.dao.IRedisRepository;
import com.todo.userservice.model.User;

/**
 * purpose: 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 10/07/18
 */
public class ToDoInterceptor implements HandlerInterceptor {


	@Autowired
	private IRedisRepository<String, User> redisRepository;

	/** (non-Javadoc)
	 * @see org.springframework.web.servlet.HandlerInterceptor#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) {

		String tokenFromHeader = request.getHeader("JWTToken");
		String userId = JwtTokenBuilder.parseJWT(tokenFromHeader).getId();
		String tokenFromRedis = redisRepository.getToken(userId);
		if (tokenFromRedis == null) {
			return false;
		}
		request.setAttribute("userId", userId);

		return true;

	}

}
