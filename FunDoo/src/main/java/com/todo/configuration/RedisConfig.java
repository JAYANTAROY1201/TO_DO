package com.todo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.todo.userservice.model.User;

/**
 * purpose: This class is designed to Configure Redis
 * 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 25/07/18
 */
@Configuration
@Component
public class RedisConfig {
	/**
	 * @return jedisConFactory
	 */
	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory jedisConFactory = new JedisConnectionFactory();
//		jedisConFactory.setHostName("localhost");
//		jedisConFactory.setPort(6379);
		return jedisConFactory;
	}

	/**
	 * @return redis template
	 */
	@Bean
	public RedisTemplate<String, User> redisTemplate() {
		RedisTemplate<String, User> redisTemplate = new RedisTemplate<String, User>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory());
		return redisTemplate;
	}
}
