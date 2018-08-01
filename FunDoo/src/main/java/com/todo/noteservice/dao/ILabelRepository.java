package com.todo.noteservice.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.todo.noteservice.model.Label;

/**
 * Purpose:This interface is designed to provide methods to perform label
 * operation
 * 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 20/07/18
 */
@Repository
public interface ILabelRepository extends MongoRepository<Label, String> {
	
	public List<Label> findByLabelName(String labelName);

	public Optional<Label>[] findByNoteId(String noteId);

	public void deleteByNoteId(String noteId);

	public void deleteByLabelName(String labelName);

}
