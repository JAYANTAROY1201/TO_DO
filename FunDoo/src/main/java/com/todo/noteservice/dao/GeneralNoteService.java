package com.todo.noteservice.dao;

import java.util.List;

import com.todo.exception.NoteReaderException;
import com.todo.noteservice.model.Note;

/**
 * purpose: General Note services to set methods
 * @author JAYANTA ROY
 * @version 1.0
 * @since 17/07/18
 */
public interface GeneralNoteService {
	public void doCreateNote(String title, String description, String authorId,
			String archive,String label,String pinned);

	public List<Note> doOpenInbox(String userId) throws NoteReaderException;

	public void doUpdateNote(String userId,String noteId ,String newTitle,String newDescription) throws NoteReaderException;

	public void doDeleteNote(String userId,String noteId) throws NoteReaderException;
}
