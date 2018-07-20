package com.todo.noteservice.dao;

import java.util.Optional;
//import static org.springframework.data.mongodb.core.query.Criteria.where;
//import static org.springframework.data.mongodb.core.query.Query.query;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.todo.noteservice.model.Label;
@Repository
	public interface GeneralMongoLabel extends MongoRepository<Label,String>
	{   
	public Optional<Label>[] findByLabelName(String labelName);
	public Optional<Label>[] findByNoteId(String noteId);
	//public Optional<Label> find((query(where("labelName").is("h").and("noteId").is("g"))));
	}

