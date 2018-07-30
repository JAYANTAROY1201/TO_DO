package com.todo.noteservice.controller;

import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import com.todo.noteservice.model.NoteInLabelDTO;
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
	 * @param title
	 * @param description
	 * @param jwt
	 * @return response entity
	 * @throws NoteReaderException 
	 */
	@RequestMapping(value = "/create_note", method = RequestMethod.POST)
	public ResponseEntity<String> createNote(@RequestBody Note note, HttpServletRequest hsr) throws NoteReaderException {
		logger.info("Create note method starts");
		noteService.doCreateNote(note,(String)hsr.getAttribute("userId"));

		logger.info("Create note method ends");
		return new ResponseEntity<String>("Note created", HttpStatus.OK);
	}

	/**
	 * This method is to open inbox 
	 * @param jwt
	 * @return response entity
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/open_inbox", method = RequestMethod.POST)
	public ResponseEntity<?> openInbox(HttpServletRequest hsr) throws NoteReaderException {
		logger.info("Read note method starts");
		List<Note> notes = noteService.doOpenInbox(hsr.getAttribute("userId").toString());

		logger.info("Read note method ends");

		return new ResponseEntity<List<Note>>(notes, HttpStatus.OK);
	}

	/**
	 * This method to open archive
	 * @param jwt
	 * @return response entity
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/open_archive", method = RequestMethod.POST)
	public ResponseEntity<?> openArchive( HttpServletRequest hsr) throws NoteReaderException {
		logger.info("Read note method starts");
		List<Note> notes = noteService.doOpenArchive(hsr.getAttribute("userId").toString());

		logger.info("Read note method ends");

		return new ResponseEntity<List<Note>>(notes,HttpStatus.ACCEPTED);
	}

	/**
	 * @param jwt
	 * @param noteId
	 * @return response entity
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/open_note", method = RequestMethod.POST)
	public ResponseEntity<?> openNote( HttpServletRequest hsr, @RequestParam String noteId)
			throws NoteReaderException {
		logger.info("open note method starts");
		List<Note> notes = noteService.doOpenNote(hsr.getAttribute("userId").toString(), noteId);

		logger.info("Read note method ends");

		return new ResponseEntity<List<Note>>(notes,HttpStatus.ACCEPTED);
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
	@RequestMapping(value = "/update_notes", method = RequestMethod.PUT)
	public ResponseEntity<String> updateNote( HttpServletRequest hsr, @RequestParam String noteId,
			@RequestBody Note note) throws NoteReaderException {
		logger.info("Update note method starts");

		noteService.doUpdateNote(hsr.getAttribute("userId").toString(), noteId, note.getTitle(), note.getDescription());

		logger.info("Update note method ends");
		return new ResponseEntity<String>("Note Updated successfully" + "", HttpStatus.OK);
	}

	/**
	 * This method is to delete notes
	 * 
	 * @param jwt
	 * @param noteId
	 * @return response entity
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/delete_notes", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteNote( HttpServletRequest hsr, @RequestParam String noteId)
			throws NoteReaderException {
		logger.info("Deleting note method starts");

		noteService.doDeleteNote(hsr.getAttribute("userId").toString(), noteId);

		logger.info("Delete method ends");
		return new ResponseEntity<String>("Note deleted successfully" + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param noteId
	 * @return response entity
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/archive_notes", method = RequestMethod.POST)
	public ResponseEntity<String> archiveNote( HttpServletRequest hsr, @RequestParam String noteId)
			throws NoteReaderException {
		logger.info("Archive note method starts");

		noteService.doArchive(hsr.getAttribute("userId").toString(), noteId);

		logger.info("Note Archived successfully");
		return new ResponseEntity<String>("Note Archived successfully" + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param noteId
	 * @return response entity
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/unarchive_notes", method = RequestMethod.POST)
	public ResponseEntity<String> unArchiveNote( HttpServletRequest hsr, @RequestParam String noteId)
			throws NoteReaderException {
		logger.info("Archive note method starts");

		noteService.doUnarchive(hsr.getAttribute("userId").toString(), noteId);

		logger.info("Note Archived successfully");
		return new ResponseEntity<String>("Note Archived successfully" + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param noteId
	 * @return response entity
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/pinned_notes", method = RequestMethod.POST)
	public ResponseEntity<String> pinnedNote( HttpServletRequest hsr, @RequestParam String noteId)
			throws NoteReaderException {
		logger.info("Pinned note method starts");

		noteService.doPinned(hsr.getAttribute("userId").toString(), noteId);

		logger.info("Note Archived successfully");
		return new ResponseEntity<String>("Note pinned successfully" + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param noteId
	 * @return response entity
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/unpinned_notes", method = RequestMethod.POST)
	public ResponseEntity<String> unPinnedNote( HttpServletRequest hsr, @RequestParam String noteId)
			throws NoteReaderException {
		logger.info("Uninned note method starts");

		noteService.doUnPinned(hsr.getAttribute("userId").toString(), noteId);

		logger.info("Note unpinned successfully");
		return new ResponseEntity<String>("Note unpinned successfully" + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param labelName
	 * @param noteLabel
	 * @return response entity
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/add_note_to_label", method = RequestMethod.POST)
	public ResponseEntity<String> addNoteToLabel( HttpServletRequest hsr,
			@RequestParam String labelName, @RequestBody NoteInLabelDTO noteLabel) throws NoteReaderException {
		logger.info("adding note method starts");
		noteService.addNoteToLabel(hsr.getAttribute("userId").toString(), labelName, noteLabel);

		logger.info("Note added successfully");
		return new ResponseEntity<String>("Note pinned successfully" + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param labelName
	 * @param noteId
	 * @return response entity
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/set_label_into_note", method = RequestMethod.POST)
	public ResponseEntity<String> setLabelToExistingNote( HttpServletRequest hsr,
			@RequestParam String labelName, @RequestParam String noteId) throws NoteReaderException {
		logger.info("adding label method starts");
		noteService.doSetLabel(hsr.getAttribute("userId").toString(), noteId, labelName);
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

	@RequestMapping(value = "/make_new_label", method = RequestMethod.POST)
	public ResponseEntity<String> makeLabel( HttpServletRequest hsr, @RequestBody Label label)
			throws NoteReaderException, ParseException {
		logger.info("Label making process start");

		noteService.doMakeLabel(hsr.getAttribute("userId").toString(), label);

		logger.info("Label making process end");
		return new ResponseEntity<String>("Label added successfully" + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param labelName
	 * @return response entity
	 * @throws NoteReaderException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/show_label", method = RequestMethod.POST)
	public ResponseEntity<?> showLabel( HttpServletRequest hsr, @RequestParam String labelName)
			throws NoteReaderException, ParseException {
		logger.info("Label searching process start");

		List<Note> noteList = noteService.doSearchNoteFromLabel(hsr.getAttribute("userId").toString(), labelName);
		logger.info("Label seaching process end");
		return new ResponseEntity<List<Note>>(noteList,HttpStatus.ACCEPTED);
	}

	/**
	 * @param jwt
	 * @param dateAndTime
	 * @param noteId
	 * @return response entity
	 * @throws NoteReaderException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/set_reminder", method = RequestMethod.POST)
	public ResponseEntity<String> setReminder( HttpServletRequest hsr,
			@RequestParam String dateAndTime, @RequestParam String noteId) throws NoteReaderException, ParseException {
		logger.info("setting timer method starts");

		noteService.doSetReminder(hsr.getAttribute("userId").toString(), noteId, dateAndTime);

		logger.info("Note reminder added successfully");
		return new ResponseEntity<String>("Note reminder added successfully" + "", HttpStatus.OK);
	}

}
