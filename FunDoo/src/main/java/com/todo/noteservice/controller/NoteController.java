package com.todo.noteservice.controller;

import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todo.exception.NoteReaderException;
import com.todo.noteservice.model.Label;
import com.todo.noteservice.model.Note;
import com.todo.noteservice.model.NoteInLabelDTO;
import com.todo.noteservice.services.NoteServiceImpl;
import com.todo.userservice.controller.UserController;
import com.todo.utility.Messages;

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
	@Autowired
	Messages messages;

	/**
	 * This method is used to create notes
	 * 
	 * @param title
	 * @param description
	 * @param jwt
	 * @return response entity
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/create_note", method = RequestMethod.POST)
	public ResponseEntity<String> createNote(@RequestBody Note note, HttpServletRequest hsr)
			throws NoteReaderException {
		logger.info("Create note method starts");
		noteService.doCreateNote(note, (String) hsr.getAttribute("userId"));

		logger.info("Create note method ends");
		logger.info(messages.get("100"));
		return new ResponseEntity<String>(messages.get("100"), HttpStatus.OK);
	}

	/**
	 * This method is to view all notes that is not archived
	 * 
	 * @param jwt
	 * @return response entity
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/view_notes", method = RequestMethod.POST)
	public ResponseEntity<?> viewAll(HttpServletRequest hsr) throws NoteReaderException {
		logger.info("Read note method starts");
		List<Note> notes = noteService.viewAllNotes((String)hsr.getAttribute("userId"));

		logger.info("Read note method ends");

		return new ResponseEntity<List<Note>>(notes, HttpStatus.OK);
	}

	/**
	 * This method to open archive
	 * 
	 * @param jwt
	 * @return response entity
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/open_archive", method = RequestMethod.POST)
	public ResponseEntity<?> openArchive(HttpServletRequest hsr) throws NoteReaderException {
		logger.info("Read note method starts");
		List<Note> notes = noteService.doOpenArchive((String)hsr.getAttribute("userId"));

		logger.info("Read note method ends");

		return new ResponseEntity<List<Note>>(notes, HttpStatus.ACCEPTED);
	}

	/**
	 * @param jwt
	 * @param noteId
	 * @return response entity
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/open_note/{noteId}", method = RequestMethod.POST)
	public ResponseEntity<?> openNote(HttpServletRequest hsr, @PathVariable("noteId") String noteId) throws NoteReaderException {
		logger.info("open note method starts");
		List<Note> notes = noteService.doOpenNote((String)hsr.getAttribute("userId"), noteId);

		logger.info("Read note method ends");

		return new ResponseEntity<List<Note>>(notes, HttpStatus.ACCEPTED);
	}

	/**
	 * This method is to update notes
	 * 
	 * @param jwt
	 * @param noteId
	 * @param newTitle
	 * @param newDescription
	 * @return response entity
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/update_notes/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<String> updateNote(HttpServletRequest hsr, @PathVariable("noteId") String noteId,
			@RequestBody Note note) throws NoteReaderException {
		logger.info("Update note method starts");

		noteService.doUpdateNote((String)hsr.getAttribute("userId"), noteId, note.getTitle(), note.getDescription());

		logger.info("Update note method ends");
		return new ResponseEntity<String>(messages.get("101") + "", HttpStatus.OK);
	}

	/**
	 * This method is to delete notes
	 * 
	 * @param jwt
	 * @param noteId
	 * @return response entity
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/delete_notes/{noteId}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteNote(HttpServletRequest hsr, @PathVariable("noteId") String noteId)
			throws NoteReaderException {
		logger.info("Deleting note method starts");

		noteService.doDeleteNote((String)hsr.getAttribute("userId"), noteId);

		logger.info("Delete method ends");
		return new ResponseEntity<String>(messages.get("102") + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param noteId
	 * @return response entity
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/archive_notes/{noteId}", method = RequestMethod.POST)
	public ResponseEntity<String> archiveNote(HttpServletRequest hsr, @PathVariable("noteId") String noteId)
			throws NoteReaderException {
		logger.info("Archive note method starts");

		noteService.doArchive((String)hsr.getAttribute("userId"), noteId);

		logger.info("Note Archived successfully");
		return new ResponseEntity<String>(messages.get("103") + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param noteId
	 * @return response entity
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/unarchive_notes/{noteId}", method = RequestMethod.POST)
	public ResponseEntity<String> unArchiveNote(HttpServletRequest hsr,@PathVariable("noteId") String noteId)
			throws NoteReaderException {
		logger.info("Archive note method starts");

		noteService.doUnarchive((String)hsr.getAttribute("userId"), noteId);

		logger.info("Note Archived successfully");
		return new ResponseEntity<String>(messages.get("104") + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param noteId
	 * @return response entity
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/pinned_notes/", method = RequestMethod.POST)
	public ResponseEntity<String> pinnedNote(HttpServletRequest hsr, @RequestParam String noteId)
			throws NoteReaderException {
		logger.info("Pinned note method starts");

		noteService.doPinned((String)hsr.getAttribute("userId"), noteId);

		logger.info("Note Archived successfully");
		return new ResponseEntity<String>(messages.get("105") + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param noteId
	 * @return response entity
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/unpinned_notes/{noteId}", method = RequestMethod.POST)
	public ResponseEntity<String> unPinnedNote(HttpServletRequest hsr, @PathVariable("noteId") String noteId)
			throws NoteReaderException {
		logger.info("Uninned note method starts");

		noteService.doUnPinned((String)hsr.getAttribute("userId"), noteId);

		logger.info("Note unpinned successfully");
		return new ResponseEntity<String>(messages.get("106") + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param labelName
	 * @param noteLabel
	 * @return response entity
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/add_note_to_label", method = RequestMethod.POST)
	public ResponseEntity<String> addNoteToLabel(HttpServletRequest hsr, @RequestParam String labelName,
			@RequestBody NoteInLabelDTO noteLabel) throws NoteReaderException {
		logger.info("adding note method starts");
		noteService.addNoteToLabel((String)hsr.getAttribute("userId"), labelName, noteLabel);

		logger.info("Note added successfully");
		return new ResponseEntity<String>(messages.get("107") + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param labelName
	 * @param noteId
	 * @return response entity
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/set_label_into_note/{noteId}", method = RequestMethod.POST)
	public ResponseEntity<String> setLabelToExistingNote(HttpServletRequest hsr, @RequestParam String labelName,
			@PathVariable("noteId") String noteId) throws NoteReaderException {
		logger.info("adding label method starts");
		noteService.doSetLabel((String)hsr.getAttribute("userId"), noteId, labelName);
		logger.info("adding label method ends");
		return new ResponseEntity<String>(messages.get("108") + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param label
	 * @return
	 * @throws NoteReaderException
	 * @throws ParseException
	 */

	@RequestMapping(value = "/make_new_label", method = RequestMethod.POST)
	public ResponseEntity<String> makeLabel(HttpServletRequest hsr, @RequestBody Label label)
			throws NoteReaderException, ParseException {
		logger.info("Label making process start");

		noteService.doMakeLabel((String)hsr.getAttribute("userId"), label);

		logger.info("Label making process end");
		return new ResponseEntity<String>(messages.get("109") + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param labelName
	 * @return response entity
	 * @throws NoteReaderException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/show_label", method = RequestMethod.POST)
	public ResponseEntity<?> showLabel(HttpServletRequest hsr, @RequestParam String labelName)
			throws NoteReaderException, ParseException {
		logger.info("Label searching process start");

		List<Note> noteList = noteService.doSearchNoteFromLabel((String)hsr.getAttribute("userId"), labelName);
		logger.info("Label seaching process end");
		return new ResponseEntity<List<Note>>(noteList, HttpStatus.ACCEPTED);
	}

	/**
	 * @param jwt
	 * @param dateAndTime
	 * @param noteId
	 * @return response entity
	 * @throws NoteReaderException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/set_reminder/{noteId}", method = RequestMethod.POST)
	public ResponseEntity<String> setReminder(HttpServletRequest hsr, @RequestParam String dateAndTime,
			@PathVariable("noteId") String noteId) throws NoteReaderException, ParseException {
		logger.info("setting timer method starts");

		noteService.doSetReminder((String)hsr.getAttribute("userId"), noteId, dateAndTime);

		logger.info("Note reminder added successfully");
		return new ResponseEntity<String>(messages.get("110") + "", HttpStatus.OK);
	}

}
