package com.todo.userservice.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.todo.userservice.model.User;

/**
 * purpose: User repository to implements mongo services
 * 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 27/07/18
 */
public interface IUserRepository extends MongoRepository<User, String> {
	public Optional<User> findByEmail(String email);

}
