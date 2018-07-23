package com.todo.noteservice.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.todo.exception.NoteReaderException;
import com.todo.noteservice.dao.GeneralMongoLabel;
import com.todo.noteservice.dao.GeneralMongoNote;
import com.todo.noteservice.dao.GeneralNoteService;
import com.todo.noteservice.model.Label;
import com.todo.noteservice.model.Note;
import com.todo.noteservice.model.NoteInLabel;


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
	
	Timer timer;
	@Autowired
	GeneralMongoLabel repoLabel;
	@Autowired
	ModelMapper mapper;
	public static final Logger logger = LoggerFactory.getLogger(NoteServiceImpl.class);

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.todo.noteservice.dao.GeneralNoteService#doCreateNote(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public void doCreateNote(String title, String description, String authorId, String archive, List<Label> label,
			String pinned) {
		Note note = new Note();
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmssMs");
		String noteid = sdf.format(new Date());
		note.set_id(noteid);
		note.setAuthorId(authorId);
		note.setTitle(title);
		note.setDescription(description);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		note.setDateOfCreation(formatter.format(new Date()));
		note.setLastDateOfModified(formatter.format(new Date()));
		note.setArchive(archive);
		note.setPinned(pinned);

		for (int i = 0; i < label.size(); i++) {
			label.get(i).setNoteId(noteid);
			repoLabel.save(label.get(i));
		}
		note.setLabel(label);
		if (archive.equals("true") && pinned.equals("true")) {
			note.setArchive("true");
			note.setPinned("false");
		}
		repo.save(note);
		logger.info("note created successfully");
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.todo.noteservice.dao.GeneralNoteService#doOpenInbox(java.lang.String)
	 */
	@Override
	public List<Note> doOpenInbox(String userID) throws NoteReaderException {
		Optional<Note>[] noteOptional = Preconditions.checkNotNull(repo.findByAuthorId(userID),
				"No note found for this user");
		List<Note> notes = new ArrayList<>();
		for (Optional<Note> note : noteOptional) {
			if (note.get().getPinned().equals("true") && (note.get().getArchive().equals("true") == false)) {
				logger.info(note.get().toString());
				notes.add(note.get());
			}
		}
		for (Optional<Note> note : noteOptional) {
			if (!note.get().getPinned().equals("true") && !note.get().getArchive().equals("true")) {
				logger.info(note.get().toString());
				notes.add(note.get());
			}
		}
		return notes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.todo.noteservice.dao.GeneralNoteService#doOpenArchive(java.lang.String)
	 */
	@Override
	public List<Note> doOpenArchive(String userID) throws NoteReaderException {
		List<Note> notes = new ArrayList<>();
		Optional<Note>[] noteOptional = Preconditions.checkNotNull(repo.findByAuthorId(userID), "No note found");
		for (Optional<Note> note : noteOptional) {
			if (note.get().getArchive().equals("true")) {
				logger.info(note.get().toString());
				notes.add(note.get());
			}
		}

		return notes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.todo.noteservice.dao.GeneralNoteService#doOpenNote(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<Note> doOpenNote(String userID, String noteId) throws NoteReaderException {

		List<Note> notes = new ArrayList<>();
		Optional<Note>[] userNotes = Preconditions.checkNotNull(repo.findByAuthorId(userID), "No note found");
		for (int i = 0; i < userNotes.length; i++) {
			if (userNotes[i].get().get_id().equals(noteId))

			{
				logger.info(userNotes[i].get().toString());
				notes.add(userNotes[i].get());
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
		Optional<Note>[] noteOptional = Preconditions.checkNotNull(repo.findByAuthorId(userId),
				"No note saved by this user");
		for (int i = 0; i < noteOptional.length; i++) {
			if (noteOptional[i].get().get_id().equals(noteId)) {
				count++;
				if (!newTitle.equals("")) {
					noteOptional[i].get().setTitle(newTitle);
				}
				if (!newDescription.equals("")) {
					noteOptional[i].get().setDescription(newDescription);
				}
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

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

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.todo.noteservice.dao.GeneralNoteService#doDeleteNote()
	 */
	@Override
	public void doDeleteNote(String userId, String noteId) throws NoteReaderException {
		int count = 0;
		Optional<Note>[] noteOptional = Preconditions.checkNotNull(repo.findByAuthorId(userId),
				"No note saved by this user");

		for (int i = 0; i < noteOptional.length; i++) {
			if (noteOptional[i].get().get_id().equals(noteId)) {
				count++;
			}
		}
		if (count > 0) {
			repo.deleteById(noteId);
			repoLabel.deleteByNoteId(noteId);
			logger.info("Note deleted succesfully");
		} else {
			logger.error("Note id not found");
			throw new NoteReaderException("Note id not found");
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.todo.noteservice.dao.GeneralNoteService#doArchive(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void doArchive(String userId, String noteId) throws NoteReaderException {
		int count = 0;
		Optional<Note>[] noteOptional = Preconditions.checkNotNull(repo.findByAuthorId(userId),
				"No note saved by this user");

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

	/**
	 * @param userId
	 * @param noteId
	 * @throws NoteReaderException
	 */
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

	/**
	 * @param userId
	 * @param noteId
	 * @throws NoteReaderException
	 */
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
					if (noteOptional[i].get().getArchive().equals("true")) {
						logger.error("Archived note cannot be pinned");
						throw new NoteReaderException("Archived note cannot be pinned");
					}
					noteOptional[i].get().setPinned("true");
					repo.save(noteOptional[i].get());
				}
			}
		}
	}

	/**
	 * @param userId
	 * @param noteId
	 * @throws NoteReaderException
	 */
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

	/**
	 * @param userId
	 * @param labelName
	 * @param noteLabel
	 * @throws NoteReaderException
	 */
	public void addNoteToLabel(String userId, String labelName, NoteInLabel noteLabel) throws NoteReaderException {
		Optional<Note>[] noteOptional = repo.findByAuthorId(userId);
		if (noteOptional.length == 0) {
			logger.error("No note saved by this user");
			throw new NoteReaderException("No note saved by this user");
		} else {

			Note note = mapper.map(noteLabel, Note.class);
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmssMs");
			String noteid = sdf.format(new Date());
			note.set_id(noteid);
			note.setAuthorId(userId);
			note.setDateOfCreation(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
			note.setLastDateOfModified(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
			Label label = new Label();
			label.setLabelName(labelName);
			label.setNoteId(noteid);
			List<Label> labelList = new ArrayList<>();
			labelList.add(label);
			note.setLabel(labelList);
			repo.save(note);
			repoLabel.save(label);
			logger.info("Note added successfully");
		}
	}

	/**
	 * @param userId
	 * @param noteId
	 * @param labelName
	 * @throws NoteReaderException
	 */
	public void doSetLabel(String userId, String noteId, String labelName) throws NoteReaderException {

		int count = 0;
		Optional<Note>[] noteOptional = Preconditions.checkNotNull(repo.findByAuthorId(userId), "No user");

		for (int i = 0; i < noteOptional.length; i++) {
			if (noteOptional[i].get().get_id().equals(noteId)) {
				count++;
			}
		}
		if (count == 0) {
			logger.error("No note found by this user");
			throw new NoteReaderException("No note found by this user");
		}

		else {
			for (int i = 0; i < noteOptional.length; i++) {
				if (noteOptional[i].get().get_id().equals(noteId)) {
					Label label = new Label();
					label.setNoteId(noteId);
					label.setLabelName(labelName);
					List<Label> labelList = noteOptional[i].get().getLabel();
					labelList.add(label);
					noteOptional[i].get().setLabel(labelList);
					repo.save(noteOptional[i].get());
					repoLabel.save(label);
					logger.info("Note labelled successfully");
				}
			}
		}

	}

	public void doSetReminder(String userId, String noteId, String reminderTime) throws ParseException {
		Optional<Note>[] noteOptional = Preconditions.checkNotNull(repo.findByAuthorId(userId), "No user");

		for (Optional<Note> note : noteOptional) {
			if (note.get().get_id().equals(noteId)) {

				Date reminder = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(reminderTime);
				long timeDifference = reminder.getTime() - new Date().getTime();
				timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						logger.info("Reminder task:" + note.get().toString());						
					}
				}, timeDifference);
			}
		}
//		timer.cancel();
	}
}
