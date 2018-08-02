package com.todo.noteservice.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.todo.noteservice.model.Note;

/**
 * purpose:Elastic repository for Note
 * 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 30/07/18
 */
public interface INoteElasticRepository extends ElasticsearchRepository<Note, String> {
	public List<Note> findByAuthorId(String userId);
}
