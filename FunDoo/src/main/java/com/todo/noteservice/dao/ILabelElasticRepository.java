package com.todo.noteservice.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.todo.noteservice.model.Label;

/**
 * purpose: Elastic repository for Label
 * 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 30/07/18
 */
public interface ILabelElasticRepository extends ElasticsearchRepository<Label, String> {

	public List<Label> findByLabelName(String labelName);

	public Optional<Label>[] findByNoteId(String noteId);

	public void deleteByNoteId(String noteId);

	public void deleteByLabelName(String labelName);
}
