package com.todo.noteservice.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.ThrowsAdvice;
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
	 * @see com.todo.noteservice.dao.GeneralNoteService#doCreateNote(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public void doCreateNote(String title, String description, String authorId, String archive, String label,
			String pinned) {
		Note note = new Note();
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmssMs");
		note.set_id(sdf.format(new Date()));
		note.setAuthorId(authorId);
		note.setTitle(title);
		note.setDescription(description);
		SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
		note.setDateOfCreation(formatter.format(new Date()));
		note.setLastDateOfModified(formatter.format(new Date()));
		note.setArchive(archive);
		note.setPinned(pinned);
		note.setLabel(label);
		if (archive.equals("true") && pinned.equals("true")) {
			note.setArchive("true");
			note.setPinned("false");
		}
		repo.save(note);
		logger.info("note created successfully");
	}


	/** (non-Javadoc)
	 * @see com.todo.noteservice.dao.GeneralNoteService#doOpenInbox(java.lang.String)
	 */
	@Override
	public List<Note> doOpenInbox(String userID) throws NoteReaderException {
		Optional<Note>[] noteOptional = repo.findByAuthorId(userID);
        List<Note> notes= new ArrayList<>();
		if (noteOptional.length == 0)
			throw new NoteReaderException("No note found");
		for (Optional<Note> note : noteOptional) {
			if(note.get().getPinned().equals("true") && (note.get().getArchive().equals("true")==false))
			{					
			logger.info(note.get().toString());
			notes.add(note.get());
			}
		}
		for (Optional<Note> note : noteOptional) {
			if(!note.get().getPinned().equals("true") && !note.get().getArchive().equals("true"))
			{					
			logger.info(note.get().toString());
			notes.add(note.get());
			}
		}
		return notes;
	}
	
	public List<Note> doOpenArchive(String userID) throws NoteReaderException {
		Optional<Note>[] noteOptional = repo.findByAuthorId(userID);
        List<Note> notes= new ArrayList<>();
		if (noteOptional.length == 0)
			throw new NoteReaderException("No note found");
		for (Optional<Note> note : noteOptional) {
			if(note.get().getArchive().equals("true"))
			{					
			logger.info(note.get().toString());
			notes.add(note.get());
			}
		}
		
		return notes;
	}

	public List<Note> doOpenNote(String userID, String noteId) throws NoteReaderException {

		Optional<Note>[] userNotes = repo.findByAuthorId(userID);
		List<Note> notes= new ArrayList<>();
		if (userNotes.length == 0)
			throw new NoteReaderException("No note found");
		else {
			for (int i = 0; i < userNotes.length; i++) {
				if (userNotes[i].get().get_id().equals(noteId))

				{
					logger.info(userNotes[i].get().toString());
					notes.add(userNotes[i].get());
				}
			}
		}
		return notes;
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
	public void doDeleteNote(String userId, String noteId) throws NoteReaderException {
		int count = 0;
		Optional<Note>[] noteOptional = repo.findByAuthorId(userId);
		if (noteOptional.length == 0) {
			logger.error("No note saved by this user");
			throw new NoteReaderException("No note saved by this user");
		}
		for (int i = 0; i < noteOptional.length; i++) {
			if (noteOptional[i].get().get_id().equals(noteId)) {
				count++;
			}
		}
		if (count > 0) {
			repo.deleteById(noteId);
			logger.info("Note deleted succesfully");
		} else {
			logger.error("Note id not found");
			throw new NoteReaderException("Note id not found");
		}
	}

	public void doArchive(String userId, String noteId) throws NoteReaderException {
		int count = 0;
		Optional<Note>[] noteOptional = repo.findByAuthorId(userId);
		if (noteOptional.length == 0) {
			logger.error("No note saved by this user");
			throw new NoteReaderException("No note saved by this user");
		}
		for (int i = 0; i < noteOptional.length; i++) {
			if (noteOptional[i].get().get_id().equals(noteId)) {
				count++;
			}
		}
		if (count == 0) {
			logger.error("No note saved by this user");
			throw new NoteReaderException("No note saved by this user");
		}

		else {
			for (int i = 0; i < noteOptional.length; i++) {
				if (noteOptional[i].get().get_id().equals(noteId)) {
					noteOptional[i].get().setArchive("true");
					noteOptional[i].get().setPinned("false");
					repo.save(noteOptional[i].get());
				}

			}
		}
	}
	
	public void doUnarchive(String userId, String noteId) throws NoteReaderException {
		int count = 0;
		Optional<Note>[] noteOptional = repo.findByAuthorId(userId);
		if (noteOptional.length == 0) {
			logger.error("No note saved by this user");
			throw new NoteReaderException("No note saved by this user");
		}
		for (int i = 0; i < noteOptional.length; i++) {
			if (noteOptional[i].get().get_id().equals(noteId)) {
				count++;
			}
		}
		if (count == 0) {
			logger.error("No note saved by this user");
			throw new NoteReaderException("No note saved by this user");
		}

		else {
			for (int i = 0; i < noteOptional.length; i++) {
				if (noteOptional[i].get().get_id().equals(noteId)) {
					noteOptional[i].get().setArchive("false");
					repo.save(noteOptional[i].get());
				}

			}
		}
	}
	
	public void doPinned(String userId, String noteId) throws NoteReaderException {
		int count = 0;
		Optional<Note>[] noteOptional = repo.findByAuthorId(userId);
		if (noteOptional.length == 0) {
			logger.error("No note saved by this user");
			throw new NoteReaderException("No note saved by this user");
		}
		for (int i = 0; i < noteOptional.length; i++) {
			if (noteOptional[i].get().get_id().equals(noteId)) {
				count++;
			}
		}
		if (count == 0) {
			logger.error("No note saved by this user");
			throw new NoteReaderException("No note saved by this user");
		}

		else {
			for (int i = 0; i < noteOptional.length; i++) {
				if (noteOptional[i].get().get_id().equals(noteId)) {
					if(noteOptional[i].get().getArchive().equals("true"))
					{
						logger.error("Archived note cannot be pinned");
						throw new NoteReaderException("Archived note cannot be pinned");
					}
					noteOptional[i].get().setPinned("true");
					repo.save(noteOptional[i].get());
				}
			}
		}
	}
	public void doUnPinned(String userId, String noteId) throws NoteReaderException {
		int count = 0;
		Optional<Note>[] noteOptional = repo.findByAuthorId(userId);
		if (noteOptional.length == 0) {
			logger.error("No note saved by this user");
			throw new NoteReaderException("No note saved by this user");
		}
		for (int i = 0; i < noteOptional.length; i++) {
			if (noteOptional[i].get().get_id().equals(noteId)) {
				count++;
			}
		}
		if (count == 0) {
			logger.error("No note saved by this user");
			throw new NoteReaderException("No note saved by this user");
		}

		else {
			for (int i = 0; i < noteOptional.length; i++) {
				if (noteOptional[i].get().get_id().equals(noteId)) {					
					noteOptional[i].get().setPinned("false");
					repo.save(noteOptional[i].get());
				}
			}
		}
	}
	
	public void doSetLabel(String userId, String noteId,String label) throws NoteReaderException {
		int count = 0;
		Optional<Note>[] noteOptional = repo.findByAuthorId(userId);
		if (noteOptional.length == 0) {
			logger.error("No note saved by this user");
			throw new NoteReaderException("No note saved by this user");
		}
		for (int i = 0; i < noteOptional.length; i++) {
			if (noteOptional[i].get().get_id().equals(noteId)) {
				count++;
			}
		}
		if (count == 0) {
			logger.error("No note saved by this user");
			throw new NoteReaderException("No note saved by this user");
		}

		else {
			for (int i = 0; i < noteOptional.length; i++) {
				if (noteOptional[i].get().get_id().equals(noteId)) {					
					noteOptional[i].get().setLabel(label);
					repo.save(noteOptional[i].get());
				}
			}
		}
	}
	
	public void doRemoveLabel(String userId, String noteId,String label) throws NoteReaderException {
		int count = 0;
		Optional<Note>[] noteOptional = repo.findByAuthorId(userId);
		if (noteOptional.length == 0) {
			logger.error("No note saved by this user");
			throw new NoteReaderException("No note saved by this user");
		}
		for (int i = 0; i < noteOptional.length; i++) {
			if (noteOptional[i].get().get_id().equals(noteId)) {
				count++;
			}
		}
		if (count == 0) {
			logger.error("No note saved by this user");
			throw new NoteReaderException("No note saved by this user");
		}

		else {
			for (int i = 0; i < noteOptional.length; i++) {
				if (noteOptional[i].get().get_id().equals(noteId)) {					
					noteOptional[i].get().setLabel(null);
					repo.save(noteOptional[i].get());
				}
			}
		}
	}
}
