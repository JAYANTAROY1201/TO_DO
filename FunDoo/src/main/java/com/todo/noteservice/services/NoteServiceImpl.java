package com.todo.noteservice.services;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

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
import com.todo.noteservice.model.Description;
import com.todo.noteservice.model.Label;
import com.todo.noteservice.model.Link;
import com.todo.noteservice.model.Note;
import com.todo.noteservice.model.NoteDTO;
import com.todo.noteservice.model.NoteInLabelDTO;
import com.todo.utility.JsoupImpl;
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
	 * @throws IOException
	 * 
	 * @see com.todo.noteservice.services.IGeneralNoteService#doCreateNote(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public String doCreateNote(NoteDTO note, String authorId) throws NoteReaderException, IOException {
		logger.debug(messages.get("115"));
		Preconditions.checkNotNull(note.getTitle(), note.getDescription(), messages.get("111"));
		Note notes = new Note();
		if (!note.getDescription().equals("")) {
			String noteDescription = note.getDescription();
			notes.setDescription(makeDescription(noteDescription));
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmssMs");
		String noteid = sdf.format(new Date());
		notes.setId(noteid);
		notes.setAuthorId(authorId);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		notes.setDateOfCreation(formatter.format(new Date()));
		notes.setLastDateOfModified(formatter.format(new Date()));
		notes.setTrash("false");
		notes.setTitle(note.getTitle());
		notes.setArchive(note.getArchive());
		notes.setPinned(note.getPinned());

		for (int i = 0; i < note.getLabel().size(); i++) {
			note.getLabel().get(i).setNoteId(noteid);
			note.getLabel().get(i).setUserId(authorId);
			repoLabel.save(note.getLabel().get(i));
			repoLabelElastic.save(note.getLabel().get(i));
		}
		notes.setLabel(note.getLabel());
		if (note.getArchive().equals("true") && note.getPinned().equals("true")) {
			note.setArchive("true");
			note.setPinned("false");
		}
		repo.save(notes);
		noteRepositoryElastic.save(notes);
		logger.debug(messages.get("116"));
		return noteid;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.todo.noteservice.services.IGeneralNoteService#doOpenInbox(java.lang.String)
	 */
	@Override
	public List<Note> viewAllNotes(String userID) throws NoteReaderException {
		logger.debug(messages.get("117"));
		List<Note> noteList = Preconditions.checkNotNull(noteRepositoryElastic.findByAuthorId(userID),
				messages.get("112"));
		List<Note> notes = new ArrayList<>();
		noteList.stream()
				.filter(streamNote -> (streamNote.getPinned().equals("true"))
						&& (streamNote.getArchive().equals("false")) && (streamNote.getTrash().equals("false")))
				.forEach(noteFilter -> notes.add(noteFilter));
		noteList.stream()
				.filter(streamNote -> (streamNote.getPinned().equals("false"))
						&& (streamNote.getArchive().equals("false")) && (streamNote.getTrash().equals("false")))
				.forEach(noteFilter -> notes.add(noteFilter));

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
		List<Note> noteList = Preconditions.checkNotNull(noteRepositoryElastic.findByAuthorId(userID), "No note found");
		noteList.stream().filter(
				streamNote -> (streamNote.getArchive().equals("true")) && (streamNote.getTrash().equals("false")))
				.forEach(noteFilter -> notes.add(noteFilter));
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
		List<Note> userNotesList = Preconditions.checkNotNull(noteRepositoryElastic.findByAuthorId(userID),
				messages.get("112"));
		userNotesList.stream()
				.filter(streamNote -> (streamNote.getId().equals(noteId)) && (streamNote.getTrash().equals("false")))
				.forEach(filterNote -> notes.add(filterNote));
		logger.debug(messages.get("122"));
		return notes;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @throws NoteReaderException
	 * @throws IOException 
	 * @see com.todo.noteservice.services.IGeneralNoteService#doUpdateNote()
	 */
	@Override
	public void doUpdateNote(String userId, String noteId, String newTitle, String newDescription)
			throws NoteReaderException, IOException {
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
					 noteOptional[i].get().setDescription(makeDescription(newDescription));
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
		// int count = 0;
		Optional<Note>[] noteOptional = Preconditions.checkNotNull(repo.findByAuthorId(userId), messages.get("112"));

		for (int i = 0; i < noteOptional.length; i++) {
			if (noteOptional[i].get().getId().equals(noteId)) {

				if (noteOptional[i].get().getTrash().equals("true")) {
					repo.deleteById(noteId);
					noteRepositoryElastic.deleteById(noteId);
					return;
				}
				repo.findById(noteId).get().setTrash("true");
				repo.save(repo.findById(noteId).get());
				repoLabel.deleteByNoteId(noteId);
				repoLabelElastic.deleteByNoteId(noteId);
				logger.info(messages.get("126"));
			} else {
				logger.error(messages.get("113"));
				throw new NoteReaderException(messages.get("113"));
			}

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
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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

	/* (non-Javadoc)
	 * @see com.todo.noteservice.services.IGeneralNoteService#doDeleteNoteFromTrash(java.lang.String, java.lang.String)
	 */
	@Override
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

	public static Description makeDescription(String noteDescription) throws IOException {
		Description desc = new Description();
		List<Link> linkList = new ArrayList<>();
		List<String> simpleList = new ArrayList<>();
		String[] descriptionArray = noteDescription.split(" ");
		for (int i = 0; i < descriptionArray.length; i++) {
			if (descriptionArray[i].startsWith("http://") || descriptionArray[i].startsWith("https://")) {
				Link link = new Link();
				link.setLinkTitle(JsoupImpl.getTitle(descriptionArray[i]));
				link.setLinkDomainName(JsoupImpl.getDomain(descriptionArray[i]));
				link.setLinkImage(JsoupImpl.getImage(descriptionArray[i]));
				System.out.println(link);
				linkList.add(link);
			} else if (!descriptionArray[i].equals("")
					&& (!descriptionArray[i].startsWith("http://") || !descriptionArray[i].startsWith("https://"))) {
				simpleList.add(descriptionArray[i]);
			}
		}
		desc.setSimpleDescription(simpleList);
		desc.setLinkDescription(linkList);
		return desc;
	}

}
