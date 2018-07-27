package com.todo.noteservice.controller;

import java.text.ParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todo.exception.NoteReaderException;
import com.todo.noteservice.model.Label;
import com.todo.noteservice.model.Note;
import com.todo.noteservice.model.NoteInLabel;
import com.todo.noteservice.services.NoteServiceImpl;
import com.todo.userservice.controller.UserController;
import com.todo.utility.JwtTokenBuilder;

/**
 * purpose:This class is designed to control note activity
 * 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 18/07/18
 */
@RestController
@RequestMapping("/fundoo/note")
public class NoteController {
	public static final Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	NoteServiceImpl noteService;

	/**
	 * This method is used to create notes
	 * 
	 * @param title
	 * @param description
	 * @param jwt
	 * @return
	 * @throws NoteReaderException 
	 */
	@RequestMapping(value = "/create-note", method = RequestMethod.POST)
	public ResponseEntity<String> createNote(@RequestBody Note note, @RequestHeader(value = "jwt") String jwt) throws NoteReaderException {
		logger.info("Create note method starts");
		noteService.doCreateNote(note.getTitle(), note.getDescription(), JwtTokenBuilder.parseJWT(jwt).getId(),
				note.getArchive(), note.getLabel(), note.getPinned());

		logger.info("Create note method ends");
		return new ResponseEntity<String>("request granted", HttpStatus.OK);
	}

	/**
	 * This method is to read notes
	 * 
	 * @param jwt
	 * @return
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/open-inbox", method = RequestMethod.POST)
	public ResponseEntity<String> openInbox(@RequestHeader(value = "jwt") String jwt) throws NoteReaderException {
		logger.info("Read note method starts");
		List<Note> notes = noteService.doOpenInbox(JwtTokenBuilder.parseJWT(jwt).getId());

		logger.info("Read note method ends");

		return new ResponseEntity<>(notes.toString(), HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @return
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/open-archive", method = RequestMethod.POST)
	public ResponseEntity<String> openArchive(@RequestHeader(value = "jwt") String jwt) throws NoteReaderException {
		logger.info("Read note method starts");
		List<Note> notes = noteService.doOpenArchive(JwtTokenBuilder.parseJWT(jwt).getId());

		logger.info("Read note method ends");

		return new ResponseEntity<>(notes.toString(), HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param noteId
	 * @return
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/open-note", method = RequestMethod.POST)
	public ResponseEntity<String> openNote(@RequestHeader(value = "jwt") String jwt, @RequestParam String noteId)
			throws NoteReaderException {
		logger.info("open note method starts");
		List<Note> notes = noteService.doOpenNote(JwtTokenBuilder.parseJWT(jwt).getId(), noteId);

		logger.info("Read note method ends");

		return new ResponseEntity<>(notes.toString(), HttpStatus.OK);
	}

	/**
	 * This method is to update notes
	 * 
	 * @param jwt
	 * @param noteId
	 * @param newTitle
	 * @param newDescription
	 * @return
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/update-notes", method = RequestMethod.PUT)
	public ResponseEntity<String> updateNote(@RequestHeader(value = "jwt") String jwt, @RequestParam String noteId,
			@RequestBody Note note) throws NoteReaderException {
		logger.info("Update note method starts");

		noteService.doUpdateNote(JwtTokenBuilder.parseJWT(jwt).getId(), noteId, note.getTitle(), note.getDescription());

		logger.info("Update note method ends");
		return new ResponseEntity<String>("Note Updated successfully" + "", HttpStatus.OK);
	}

	/**
	 * This method is to delete notes
	 * 
	 * @param jwt
	 * @param noteId
	 * @return
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/delete-notes", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteNote(@RequestHeader(value = "jwt") String jwt, @RequestParam String noteId)
			throws NoteReaderException {
		logger.info("Deleting note method starts");

		noteService.doDeleteNote(JwtTokenBuilder.parseJWT(jwt).getId(), noteId);

		logger.info("Delete method ends");
		return new ResponseEntity<String>("Note deleted successfully" + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param noteId
	 * @return
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/archive-notes", method = RequestMethod.POST)
	public ResponseEntity<String> archiveNote(@RequestHeader(value = "jwt") String jwt, @RequestParam String noteId)
			throws NoteReaderException {
		logger.info("Archive note method starts");

		noteService.doArchive(JwtTokenBuilder.parseJWT(jwt).getId(), noteId);

		logger.info("Note Archived successfully");
		return new ResponseEntity<String>("Note Archived successfully" + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param noteId
	 * @return
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/unarchive-notes", method = RequestMethod.POST)
	public ResponseEntity<String> unArchiveNote(@RequestHeader(value = "jwt") String jwt, @RequestParam String noteId)
			throws NoteReaderException {
		logger.info("Archive note method starts");

		noteService.doUnarchive(JwtTokenBuilder.parseJWT(jwt).getId(), noteId);

		logger.info("Note Archived successfully");
		return new ResponseEntity<String>("Note Archived successfully" + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param noteId
	 * @return
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/pinned-notes", method = RequestMethod.POST)
	public ResponseEntity<String> pinnedNote(@RequestHeader(value = "jwt") String jwt, @RequestParam String noteId)
			throws NoteReaderException {
		logger.info("Pinned note method starts");

		noteService.doPinned(JwtTokenBuilder.parseJWT(jwt).getId(), noteId);

		logger.info("Note Archived successfully");
		return new ResponseEntity<String>("Note pinned successfully" + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param noteId
	 * @return
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/unpinned-notes", method = RequestMethod.POST)
	public ResponseEntity<String> unPinnedNote(@RequestHeader(value = "jwt") String jwt, @RequestParam String noteId)
			throws NoteReaderException {
		logger.info("Uninned note method starts");

		noteService.doUnPinned(JwtTokenBuilder.parseJWT(jwt).getId(), noteId);

		logger.info("Note unpinned successfully");
		return new ResponseEntity<String>("Note unpinned successfully" + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param labelName
	 * @param noteLabel
	 * @return
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/add-note-to-label", method = RequestMethod.POST)
	public ResponseEntity<String> addNoteToLabel(@RequestHeader(value = "jwt") String jwt,
			@RequestParam String labelName, @RequestBody NoteInLabel noteLabel) throws NoteReaderException {
		logger.info("adding note method starts");
		noteService.addNoteToLabel(JwtTokenBuilder.parseJWT(jwt).getId(), labelName, noteLabel);

		logger.info("Note added successfully");
		return new ResponseEntity<String>("Note pinned successfully" + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param labelName
	 * @param noteId
	 * @return
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/set-label-into-note", method = RequestMethod.POST)
	public ResponseEntity<String> setLabelToExistingNote(@RequestHeader(value = "jwt") String jwt,
			@RequestParam String labelName, @RequestParam String noteId) throws NoteReaderException {
		logger.info("adding label method starts");
		noteService.doSetLabel(JwtTokenBuilder.parseJWT(jwt).getId(), noteId, labelName);
		logger.info("adding label method ends");
		return new ResponseEntity<String>("Note labelled successfully" + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param label
	 * @return
	 * @throws NoteReaderException
	 * @throws ParseException
	 */

	@RequestMapping(value = "/make-new-label", method = RequestMethod.POST)
	public ResponseEntity<String> makeLabel(@RequestHeader(value = "jwt") String jwt, @RequestBody Label label)
			throws NoteReaderException, ParseException {
		logger.info("Label making process start");

		noteService.doMakeLabel(JwtTokenBuilder.parseJWT(jwt).getId(), label);

		logger.info("Label making process end");
		return new ResponseEntity<String>("Label added successfully" + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param labelName
	 * @return
	 * @throws NoteReaderException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/show-label", method = RequestMethod.POST)
	public ResponseEntity<String> showLabel(@RequestHeader(value = "jwt") String jwt, @RequestParam String labelName)
			throws NoteReaderException, ParseException {
		logger.info("Label searching process start");

		List<Note> noteList = noteService.doSearchNoteFromLabel(JwtTokenBuilder.parseJWT(jwt).getId(), labelName);

		logger.info("Label seaching process end");
		return new ResponseEntity<String>(noteList.toString(), HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param dateAndTime
	 * @param noteId
	 * @return
	 * @throws NoteReaderException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/set-reminder", method = RequestMethod.POST)
	public ResponseEntity<String> setReminder(@RequestHeader(value = "jwt") String jwt,
			@RequestParam String dateAndTime, @RequestParam String noteId) throws NoteReaderException, ParseException {
		logger.info("setting timer method starts");

		noteService.doSetReminder(JwtTokenBuilder.parseJWT(jwt).getId(), noteId, dateAndTime);

		logger.info("Note reminder added successfully");
		return new ResponseEntity<String>("Note reminder added successfully" + "", HttpStatus.OK);
	}

}
