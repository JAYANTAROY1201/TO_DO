package com.todo.userservice.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.todo.userservice.model.User;

public interface GeneralMongoRepository extends MongoRepository<User,String> {
	public Optional<User> findByEmail(String email);

}
