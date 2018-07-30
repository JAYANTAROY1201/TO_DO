package com.todo.noteservice.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.todo.noteservice.model.Note;

/**
 * purpose:To implements mongo repository service 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 17/07/18
 */
public interface INoteRepository extends MongoRepository<Note,String>
{
   public Optional<Note>[] findByAuthorId(String userId);
}
