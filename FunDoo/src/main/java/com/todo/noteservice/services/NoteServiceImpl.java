package com.todo.noteservice.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.todo.exception.NoteReaderException;
import com.todo.noteservice.dao.GeneralMongoNote;
import com.todo.noteservice.dao.GeneralNoteService;
import com.todo.noteservice.model.Note;

/**
 * purpose:This class is to implements all note service
 * 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 17/07/18
 */
@Service
public class NoteServiceImpl implements GeneralNoteService {
	@Autowired
	private GeneralMongoNote repo;
	public static final Logger logger = LoggerFactory.getLogger(NoteServiceImpl.class);
	/**
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.todo.noteservice.dao.GeneralNoteService#doCreateNote(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void doCreateNote(String title, String description, String authorId) {
		Note note = new Note();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmssMs");    
		note.set_id(sdf.format(new Date()));
		note.setAuthorId(authorId);
		note.setTitle(title);
		note.setDescription(description);
		SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
		note.setDateOfCreation(formatter.format(new Date()));
		note.setLastDateOfModified(formatter.format(new Date()));
		repo.save(note);
		logger.info("note created successfully");
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.todo.noteservice.dao.GeneralNoteService#doReadNote(java.lang.String)
	 */
	@Override
	public void doReadNote(String userID) throws NoteReaderException {
		System.out.println("enteerrrrrr");
		Optional<Note>[] noteOptional = repo.findByAuthorId(userID);
		
		if (noteOptional.length == 0)
			throw new NoteReaderException("No note found");
		for (Optional<Note> note : noteOptional) {
			logger.info(note.get().toString());			
		}		
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @throws NoteReaderException
	 * @see com.todo.noteservice.dao.GeneralNoteService#doUpdateNote()
	 */
	@Override
	public void doUpdateNote(String userId, String noteId, String newTitle, String newDescription)
			throws NoteReaderException {
		int count = 0;
		Optional<Note>[] noteOptional = repo.findByAuthorId(userId);
		if (noteOptional.length == 0) {
			logger.error("No note saved by this user");
			throw new NoteReaderException("No note saved by this user");
		} else {

			for (int i = 0; i < noteOptional.length; i++) {
				if (noteOptional[i].get().get_id().equals(noteId)) {
					count++;
					if (!newTitle.equals("")) {
						noteOptional[i].get().setTitle(newTitle);
					}
					if (!newDescription.equals("")) {
						noteOptional[i].get().setDescription(newDescription);
					}
					SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");

					noteOptional[i].get().setLastDateOfModified(formatter.format(new Date()));
					logger.info("Note updated successfully");
					repo.save(noteOptional[i].get());
				}
			}
			if (count == 0) {
				logger.error("Note id not found");
				throw new NoteReaderException("Note id not found");
			}
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.todo.noteservice.dao.GeneralNoteService#doDeleteNote()
	 */
	@Override
	public void doDeleteNote(String userId,String noteId) throws NoteReaderException {
		int count=0;
		Optional<Note>[] noteOptional = repo.findByAuthorId(userId);
		if (noteOptional.length == 0) {
			logger.error("No note saved by this user");
			throw new NoteReaderException("No note saved by this user");
		}
		for(int i=0;i<noteOptional.length;i++)
		{
			if(noteOptional[i].get().get_id().equals(noteId))
			{
				count++;
			}
		}
		if(count>0)
		{
			repo.deleteById(noteId);
			logger.info("Note deleted succesfully");
		}
		else
		{
			logger.error("Note id not found");
			throw new NoteReaderException("Note id not found"); 
		}		
	}
}
