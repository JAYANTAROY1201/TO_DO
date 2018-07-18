package com.todo.noteservice.dao;

import com.todo.exception.NoteReaderException;

/**
 * purpose: General Note services to set methods
 * @author JAYANTA ROY
 * @version 1.0
 * @since 17/07/18
 */
public interface GeneralNoteService {
	public void doCreateNote(String title, String description, String authorId);

	public void doReadNote(String userId) throws NoteReaderException;

	public void doUpdateNote(String userId,String noteId ,String newTitle,String newDescription) throws NoteReaderException;

	public void doDeleteNote(String userId,String noteId) throws NoteReaderException;
}
