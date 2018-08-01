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
import com.todo.noteservice.dao.ILabelRepository;
import com.todo.noteservice.dao.ILabelElasticRepository;
import com.todo.noteservice.dao.INoteElasticRepository;
import com.todo.noteservice.dao.INoteRepository;
import com.todo.noteservice.model.Label;
import com.todo.noteservice.model.Note;
import com.todo.noteservice.model.NoteInLabelDTO;
import com.todo.utility.Messages;

/**
 * purpose:This class is to implements all note service
 * 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 17/07/18
 */
@Service
public class NoteServiceImpl implements IGeneralNoteService {
	@Autowired
	private INoteRepository repo;
	@Autowired
	private INoteElasticRepository noteRepositoryElastic;

	Timer timer;
	@Autowired
	ILabelRepository repoLabel;
	@Autowired
	ILabelElasticRepository repoLabelElastic;
	@Autowired
	ModelMapper mapper;
	@Autowired
	Messages messages;
	public static final Logger logger = LoggerFactory.getLogger(NoteServiceImpl.class);

	/**
	 * this method is written to create Note
	 * 
	 * @throws NoteReaderException
	 * 
	 * @see com.todo.noteservice.services.IGeneralNoteService#doCreateNote(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public void doCreateNote(Note note, String authorId) throws NoteReaderException {
		logger.debug(messages.get("115"));
		Preconditions.checkNotNull(note.getTitle(), note.getDescription(), messages.get("111"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmssMs");
		String noteid = sdf.format(new Date());
		note.setId(noteid);
		note.setAuthorId(authorId);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		note.setDateOfCreation(formatter.format(new Date()));
		note.setLastDateOfModified(formatter.format(new Date()));
		note.setTrash("false");

		for (int i = 0; i < note.getLabel().size(); i++) {
			note.getLabel().get(i).setNoteId(noteid);
			note.getLabel().get(i).setUserId(authorId);
			repoLabel.save(note.getLabel().get(i));
			repoLabelElastic.save(note.getLabel().get(i));
		}
		if (note.getArchive().equals("true") && note.getPinned().equals("true")) {
			note.setArchive("true");
			note.setPinned("false");
		}
		repo.save(note);
		noteRepositoryElastic.save(note);
		logger.debug(messages.get("116"));
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.todo.noteservice.services.IGeneralNoteService#doOpenInbox(java.lang.String)
	 */
	@Override
	public List<Note> viewAllNotes(String userID) throws NoteReaderException {
		logger.debug(messages.get("117"));
		//Optional<Note>[] noteOptional = Preconditions.checkNotNull(repo.findByAuthorId(userID), messages.get("112"));
		Optional<Note>[] noteOptional = Preconditions.checkNotNull(noteRepositoryElastic.findByAuthorId(userID), messages.get("112"));
		List<Note> notes = new ArrayList<>();
		for (Optional<Note> note : noteOptional) {
			if (note.get().getPinned().equals("true") && (note.get().getArchive().equals("true") == false)
					&& (note.get().getTrash().equals("false"))) {
				logger.info(note.get().toString());
				notes.add(note.get());
			}
		}
		for (Optional<Note> note : noteOptional) {
			if (!note.get().getPinned().equals("true") && !note.get().getArchive().equals("true")
					&& (note.get().getTrash().equals("false"))) {
				logger.info(note.get().toString());
				notes.add(note.get());
			}
		}
		logger.debug(messages.get("118"));
		return notes;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.todo.noteservice.services.IGeneralNoteService#doOpenArchive(java.lang.String)
	 */
	@Override
	public List<Note> doOpenArchive(String userID) throws NoteReaderException {
		logger.debug(messages.get("119"));
		List<Note> notes = new ArrayList<>();
		Optional<Note>[] noteOptional = Preconditions.checkNotNull(repo.findByAuthorId(userID), "No note found");
		for (Optional<Note> note : noteOptional) {
			if (note.get().getArchive().equals("true") && note.get().getTrash().equals("false")) {
				logger.info(note.get().toString());
				notes.add(note.get());
			}
		}
		logger.debug(messages.get("120"));
		return notes;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.todo.noteservice.services.IGeneralNoteService#doOpenNote(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public List<Note> doOpenNote(String userID, String noteId) throws NoteReaderException {
		logger.debug(messages.get("121"));
		List<Note> notes = new ArrayList<>();
		Optional<Note>[] userNotes = Preconditions.checkNotNull(repo.findByAuthorId(userID), messages.get("112"));
		for (int i = 0; i < userNotes.length; i++) {
			if (userNotes[i].get().getId().equals(noteId) && userNotes[i].get().getTrash().equals("false")) {
				logger.info(userNotes[i].get().toString());
				notes.add(userNotes[i].get());
			}
		}
		logger.debug(messages.get("122"));
		return notes;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @throws NoteReaderException
	 * @see com.todo.noteservice.services.IGeneralNoteService#doUpdateNote()
	 */
	@Override
	public void doUpdateNote(String userId, String noteId, String newTitle, String newDescription)
			throws NoteReaderException {
		logger.debug(messages.get("123"));
		int count = 0;
		Optional<Note>[] noteOptional = Preconditions.checkNotNull(repo.findByAuthorId(userId), messages.get("112"));
		for (int i = 0; i < noteOptional.length; i++) {
			if (noteOptional[i].get().getId().equals(noteId) && noteOptional[i].get().getTrash().equals("false")) {
				count++;
				if (!newTitle.equals("")) {
					noteOptional[i].get().setTitle(newTitle);
				}
				if (!newDescription.equals("")) {
					noteOptional[i].get().setDescription(newDescription);
				}
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

				noteOptional[i].get().setLastDateOfModified(formatter.format(new Date()));
				logger.info(messages.get("101"));
				repo.save(noteOptional[i].get());
				noteRepositoryElastic.save(noteOptional[i].get());
			}
		}
		if (count == 0) {
			logger.error(messages.get("113"));
			throw new NoteReaderException(messages.get("113"));
		}
		logger.debug(messages.get("124"));
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.todo.noteservice.services.IGeneralNoteService#doDeleteNote()
	 */
	@Override
	public void doDeleteNote(String userId, String noteId) throws NoteReaderException {
		logger.debug(messages.get("125"));
		int count = 0;
		Optional<Note>[] noteOptional = Preconditions.checkNotNull(repo.findByAuthorId(userId), messages.get("112"));

		for (int i = 0; i < noteOptional.length; i++) {
			if (noteOptional[i].get().getId().equals(noteId)) {
				count++;
			}
		}
		if (count > 0) {
			repo.findById(noteId).get().setTrash("true");
			repo.save(repo.findById(noteId).get());
			repoLabel.deleteByNoteId(noteId);
			repoLabelElastic.deleteByNoteId(noteId);
			logger.info(messages.get("126"));
		} else {
			logger.error(messages.get("113"));
			throw new NoteReaderException(messages.get("113"));
		}
		logger.debug(messages.get("127"));
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.todo.noteservice.services.IGeneralNoteService#doArchive(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void doArchive(String userId, String noteId) throws NoteReaderException {
		logger.debug(messages.get("128"));
		int count = 0;

		Optional<Note>[] noteOptional = Preconditions.checkNotNull(repo.findByAuthorId(userId), messages.get("112"));

		for (int i = 0; i < noteOptional.length; i++) {
			if (noteOptional[i].get().getId().equals(noteId) && noteOptional[i].get().getTrash().equals("false")) {
				count++;
			}
		}
		if (count == 0) {
			logger.error(messages.get("112"));
			throw new NoteReaderException(messages.get("112"));
		}

		else {
			for (int i = 0; i < noteOptional.length; i++) {
				if (noteOptional[i].get().getId().equals(noteId) && noteOptional[i].get().getTrash().equals("false")) {
					noteOptional[i].get().setArchive("true");
					noteOptional[i].get().setPinned("false");
					repo.save(noteOptional[i].get());
					noteRepositoryElastic.save(noteOptional[i].get());
				}

			}
		}
		logger.debug(messages.get("129"));
	}

	/**
	 * @param userId
	 * @param noteId
	 * @throws NoteReaderException
	 */
	public void doUnarchive(String userId, String noteId) throws NoteReaderException {
		logger.debug(messages.get("130"));
		int count = 0;
		Optional<Note>[] noteOptional = Preconditions.checkNotNull(repo.findByAuthorId(userId), messages.get("112"));

		for (int i = 0; i < noteOptional.length; i++) {
			if (noteOptional[i].get().getId().equals(noteId) && noteOptional[i].get().getTrash().equals("false")) {
				count++;
			}
		}
		if (count == 0) {
			logger.error(messages.get("112"));
			throw new NoteReaderException(messages.get("112"));
		}

		else {
			for (int i = 0; i < noteOptional.length; i++) {
				if (noteOptional[i].get().getId().equals(noteId) && noteOptional[i].get().getTrash().equals("false")) {
					noteOptional[i].get().setArchive("false");
					repo.save(noteOptional[i].get());
					noteRepositoryElastic.save(noteOptional[i].get());
				}

			}
		}
		logger.debug(messages.get("131"));
	}

	/**
	 * @param userId
	 * @param noteId
	 * @throws NoteReaderException
	 */
	public void doPinned(String userId, String noteId) throws NoteReaderException {
		logger.debug(messages.get("132"));
		int count = 0;
		Optional<Note>[] noteOptional = Preconditions.checkNotNull(repo.findByAuthorId(userId), messages.get("112"));

		for (int i = 0; i < noteOptional.length; i++) {
			if (noteOptional[i].get().getId().equals(noteId) && noteOptional[i].get().getTrash().equals("false")) {
				count++;
			}
		}
		if (count == 0) {
			logger.error(messages.get("113"));
			throw new NoteReaderException(messages.get("113"));
		}

		else {
			for (int i = 0; i < noteOptional.length; i++) {
				if (noteOptional[i].get().getId().equals(noteId) && noteOptional[i].get().getTrash().equals("false")) {
					if (noteOptional[i].get().getArchive().equals("true")) {
						logger.error(messages.get("136"));
						throw new NoteReaderException(messages.get("136"));
					}
					noteOptional[i].get().setPinned("true");
					repo.save(noteOptional[i].get());
					noteRepositoryElastic.save(noteOptional[i].get());
					
				}
			}
		}
		logger.debug(messages.get("133"));
	}

	/**
	 * @param userId
	 * @param noteId
	 * @throws NoteReaderException
	 */
	public void doUnPinned(String userId, String noteId) throws NoteReaderException {
		logger.debug(messages.get("134"));
		int count = 0;
		Optional<Note>[] noteOptional = Preconditions.checkNotNull(repo.findByAuthorId(userId), messages.get("112"));

		for (int i = 0; i < noteOptional.length; i++) {
			if (noteOptional[i].get().getId().equals(noteId) && noteOptional[i].get().getTrash().equals("false")) {
				count++;
			}
		}
		if (count == 0) {
			logger.error(messages.get("113"));
			throw new NoteReaderException(messages.get("113"));
		}

		else {
			for (int i = 0; i < noteOptional.length; i++) {
				if (noteOptional[i].get().getId().equals(noteId) && noteOptional[i].get().getTrash().equals("false")) {
					noteOptional[i].get().setPinned("false");
					repo.save(noteOptional[i].get());
					noteRepositoryElastic.save(noteOptional[i].get());
				}
			}
		}
		logger.debug(messages.get("135"));
	}

	/**
	 * @param userId
	 * @param labelName
	 * @param noteLabel
	 * @throws NoteReaderException
	 */
	public void addNoteToLabel(String userId, String labelName, NoteInLabelDTO noteLabel) throws NoteReaderException {
		logger.debug("adding note to level process starts");
		Preconditions.checkNotNull(labelName, messages.get("137"));
		Preconditions.checkNotNull(repo.findByAuthorId(userId), messages.get("112"));

		Note note = mapper.map(noteLabel, Note.class);
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmssMs");
		String noteid = sdf.format(new Date());
		note.setId(noteid);
		note.setAuthorId(userId);
		note.setDateOfCreation(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
		note.setLastDateOfModified(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
		note.setTrash("false");
		Label label = new Label();
		label.setLabelName(labelName);
		label.setNoteId(noteid);
		label.setUserId(userId);
		List<Label> labelList = new ArrayList<>();
		labelList.add(label);
		note.setLabel(labelList);
		repo.save(note);
		noteRepositoryElastic.save(note);
		repoLabel.save(label);
		repoLabelElastic.save(label);
		logger.info("Note added successfully");
		logger.debug("adding note to lavel process ends");
	}

	/**
	 * @param userId
	 * @param noteId
	 * @param labelName
	 * @throws NoteReaderException
	 */
	public void doSetLabel(String userId, String noteId, String labelName) throws NoteReaderException {
		logger.debug("adding level process starts");
		Preconditions.checkNotNull(labelName, messages.get("137"));
		int count = 0;
		Optional<Note>[] noteOptional = Preconditions.checkNotNull(repo.findByAuthorId(userId), "No user");

		for (int i = 0; i < noteOptional.length; i++) {
			if (noteOptional[i].get().getId().equals(noteId)) {
				count++;
			}
		}
		if (count == 0) {
			logger.error(messages.get("113"));
			throw new NoteReaderException(messages.get("113"));
		}

		else {
			for (int i = 0; i < noteOptional.length; i++) {
				if (noteOptional[i].get().getId().equals(noteId)) {
					Label label = new Label();
					label.setNoteId(noteId);
					label.setUserId(userId);
					label.setLabelName(labelName);
					List<Label> labelList = noteOptional[i].get().getLabel();
					labelList.add(label);
					noteOptional[i].get().setLabel(labelList);
					repo.save(noteOptional[i].get());
					noteRepositoryElastic.save(noteOptional[i].get());
					repoLabel.save(label);
					repoLabelElastic.save(label);
					logger.info("Note labelled successfully");
				}
			}
		}
		logger.debug("adding level process starts");
	}

	/**
	 * @param userId
	 * @param label
	 * @throws NoteReaderException
	 */
	public void doMakeLabel(String userId, Label label) throws NoteReaderException {
		logger.debug("making level process starts");
		Preconditions.checkNotNull(label.getLabelName(), messages.get("137"));
		if (repoLabel.findByLabelName(label.getLabelName()).isEmpty()) {
			label.setUserId(userId);
			repoLabel.save(label);
			repoLabelElastic.save(label);
		} else {
			throw new NoteReaderException(messages.get("114"));
		}
		logger.debug("making level process ends");
	}

	/**
	 * @param userId
	 * @param labelName
	 * @return
	 */
	public List<Note> doSearchNoteFromLabel(String userId, String labelName) {
		logger.debug("seaching note from level process starts");
		Preconditions.checkNotNull(repo.findByAuthorId(userId), messages.get("112"));
		List<Label> labelList = repoLabel.findByLabelName(labelName);
		List<Note> noteList = new ArrayList<>();
		for (Label label : labelList) {
			noteList.add(repo.findById(label.getNoteId()).get());
		}
		logger.debug("seaching note from level process ends");
		return noteList;
	}

	/**
	 * @param userId
	 * @param noteId
	 * @param reminderTime
	 * @throws ParseException
	 */
	public void doSetReminder(String userId, String noteId, String reminderTime) throws ParseException {
		logger.debug("setting reminder process starts");
		Optional<Note>[] noteOptional = Preconditions.checkNotNull(repo.findByAuthorId(userId), messages.get("112"));

		for (Optional<Note> note : noteOptional) {
			if (note.get().getId().equals(noteId)) {

				Date reminder = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(reminderTime);
				long timeDifference = reminder.getTime() - new Date().getTime();
				timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						logger.info("Reminder Task:" + note.toString());
					}
				}, timeDifference);
			}
		}
		logger.debug("setting reminder process ends");
	}
	public void doDeleteNoteFromTrash(String userId, String noteId) throws NoteReaderException {
		logger.debug(messages.get("125"));
		int count = 0;
		Optional<Note>[] noteOptional = Preconditions.checkNotNull(repo.findByAuthorId(userId), messages.get("112"));

		for (int i = 0; i < noteOptional.length; i++) {
			if (noteOptional[i].get().getId().equals(noteId) && noteOptional[i].get().getTrash().equals("true")) {
				count++;
			}
		}
		if (count > 0) {
			repo.deleteById(noteId);
			noteRepositoryElastic.deleteById(noteId);
			logger.info(messages.get("126"));
		} else {
			logger.error(messages.get("113"));
			throw new NoteReaderException(messages.get("113"));
		}
		logger.debug(messages.get("127"));
	}
	
}
