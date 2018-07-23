package com.todo.noteservice.controller;

import java.util.ArrayList;
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
	 */
	@RequestMapping(value = "/createnote", method = RequestMethod.POST)
	public ResponseEntity<String> createNote(@RequestBody Note note, @RequestHeader(value="jwt") String jwt) {
		logger.info("Create note method starts");

		try {
			noteService.doCreateNote(note.getTitle(), note.getDescription(), JwtTokenBuilder.parseJWT(jwt).getId(),
					note.getArchive(),note.getLabel(), note.getPinned());
		} catch (Exception e) {

			return new ResponseEntity<String>(e + "", HttpStatus.BAD_REQUEST);
		}
		logger.info("Create note method ends");
		return new ResponseEntity<String>("request granted", HttpStatus.OK);
	}

	/**
	 * This method is to read notes
	 * 
	 * @param jwt
	 * @return
	 */
	@RequestMapping(value = "/openinbox", method = RequestMethod.POST)
	public ResponseEntity<String> openInbox(@RequestHeader(value="jwt") String jwt) {
		logger.info("Read note method starts");
		List<Note> notes = new ArrayList<>();
		try {
			notes = noteService.doOpenInbox(JwtTokenBuilder.parseJWT(jwt).getId());

		} catch (Exception e) {
			return new ResponseEntity<>(e + "", HttpStatus.BAD_REQUEST);
		}
		logger.info("Read note method ends");

		return new ResponseEntity<>(notes.toString(), HttpStatus.OK);
	}
	/**
	 * @param jwt
	 * @return
	 */
	@RequestMapping(value = "/openarchive", method = RequestMethod.POST)
	public ResponseEntity<String> openArchive(@RequestHeader(value="jwt") String jwt) {
		logger.info("Read note method starts");
		List<Note> notes = new ArrayList<>();
		try {
			notes = noteService.doOpenArchive(JwtTokenBuilder.parseJWT(jwt).getId());

		} catch (Exception e) {
			return new ResponseEntity<>(e + "", HttpStatus.BAD_REQUEST);
		}
		logger.info("Read note method ends");

		return new ResponseEntity<>(notes.toString(), HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param noteId
	 * @return
	 */
	@RequestMapping(value = "/opennote", method = RequestMethod.POST)
	public ResponseEntity<String> openNote(@RequestHeader(value="jwt") String jwt, @RequestParam String noteId) {
		logger.info("open note method starts");
		List<Note> notes = new ArrayList<>();
		try {
			notes = noteService.doOpenNote(JwtTokenBuilder.parseJWT(jwt).getId(), noteId);

		} catch (Exception e) {
			return new ResponseEntity<>(e + "", HttpStatus.BAD_REQUEST);
		}
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
	@RequestMapping(value = "/updatenotes", method = RequestMethod.POST)
	public ResponseEntity<String> updateNote(@RequestHeader(value="jwt") String jwt, @RequestParam String noteId,
			@RequestBody Note note) throws NoteReaderException {
		logger.info("Update note method starts");

		try {
			noteService.doUpdateNote(JwtTokenBuilder.parseJWT(jwt).getId(), noteId, note.getTitle(),
					note.getDescription());

		} catch (Exception e) {
			return new ResponseEntity<String>(e + "", HttpStatus.BAD_REQUEST);
		}
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
	@RequestMapping(value = "/deletenotes", method = RequestMethod.POST)
	public ResponseEntity<String> deleteNote(@RequestHeader(value="jwt") String jwt, @RequestParam String noteId)
			throws NoteReaderException {
		logger.info("Deleting note method starts");

		try {
			noteService.doDeleteNote(JwtTokenBuilder.parseJWT(jwt).getId(), noteId);

		} catch (Exception e) {
			return new ResponseEntity<String>(e + "", HttpStatus.BAD_REQUEST);
		}
		logger.info("Delete method ends");
		return new ResponseEntity<String>("Note deleted successfully" + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param noteId
	 * @return
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/archivenotes", method = RequestMethod.POST)
	public ResponseEntity<String> archiveNote(@RequestHeader(value="jwt") String jwt, @RequestParam String noteId)
			throws NoteReaderException {
		logger.info("Archive note method starts");

		try {
			noteService.doArchive(JwtTokenBuilder.parseJWT(jwt).getId(), noteId);

		} catch (Exception e) {
			return new ResponseEntity<String>(e + "", HttpStatus.BAD_REQUEST);
		}
		logger.info("Note Archived successfully");
		return new ResponseEntity<String>("Note Archived successfully" + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param noteId
	 * @return
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/unarchivenotes", method = RequestMethod.POST)
	public ResponseEntity<String> unArchiveNote(@RequestHeader(value="jwt") String jwt, @RequestParam String noteId)
			throws NoteReaderException {
		logger.info("Archive note method starts");

		try {
			noteService.doUnarchive(JwtTokenBuilder.parseJWT(jwt).getId(), noteId);

		} catch (Exception e) {
			return new ResponseEntity<String>(e + "", HttpStatus.BAD_REQUEST);
		}
		logger.info("Note Archived successfully");
		return new ResponseEntity<String>("Note Archived successfully" + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param noteId
	 * @return
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/pinnednotes", method = RequestMethod.POST)
	public ResponseEntity<String> pinnedNote(@RequestHeader(value="jwt") String jwt, @RequestParam String noteId)
			throws NoteReaderException {
		logger.info("Pinned note method starts");

		try {
			noteService.doPinned(JwtTokenBuilder.parseJWT(jwt).getId(), noteId);

		} catch (Exception e) {
			return new ResponseEntity<String>(e + "", HttpStatus.BAD_REQUEST);
		}
		logger.info("Note Archived successfully");
		return new ResponseEntity<String>("Note pinned successfully" + "", HttpStatus.OK);
	}

	/**
	 * @param jwt
	 * @param noteId
	 * @return
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/unpinnednotes", method = RequestMethod.POST)
	public ResponseEntity<String> unPinnedNote(@RequestHeader(value="jwt") String jwt, @RequestParam String noteId)
			throws NoteReaderException {
		logger.info("Uninned note method starts");

		try {
			noteService.doUnPinned(JwtTokenBuilder.parseJWT(jwt).getId(), noteId);

		} catch (Exception e) {
			return new ResponseEntity<String>(e + "", HttpStatus.BAD_REQUEST);
		}
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
	@RequestMapping(value = "/addnotetolabel", method = RequestMethod.POST)
	public ResponseEntity<String> addNoteToLabel(@RequestHeader(value="jwt") String jwt, @RequestParam String labelName,@RequestBody NoteInLabel noteLabel  )
			throws NoteReaderException {
		logger.info("adding note method starts");

		try {
			noteService.addNoteToLabel(JwtTokenBuilder.parseJWT(jwt).getId(),labelName,noteLabel);

		} catch (Exception e) {
			return new ResponseEntity<String>(e + "", HttpStatus.BAD_REQUEST);
		}
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
	@RequestMapping(value = "/setlabel", method = RequestMethod.POST)
	public ResponseEntity<String> setLabelToExistingNote(@RequestHeader(value="jwt") String jwt, @RequestParam String labelName,@RequestParam String noteId)
			throws NoteReaderException {
		logger.info("adding label method starts");

		try {
			noteService.doSetLabel(JwtTokenBuilder.parseJWT(jwt).getId(),noteId,labelName);

		} catch (Exception e) {
			return new ResponseEntity<String>(e + "", HttpStatus.BAD_REQUEST);
		}
		logger.info("adding label method ends");
		return new ResponseEntity<String>("Note labelled successfully" + "", HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/setreminder", method = RequestMethod.POST)
	public ResponseEntity<String> setReminder(@RequestHeader(value="jwt") String jwt, @RequestParam String dateAndTime,@RequestParam String noteId)
			throws NoteReaderException {
		logger.info("setting timer method starts");

		try {
			noteService.doSetReminder(JwtTokenBuilder.parseJWT(jwt).getId(), noteId, dateAndTime);
		} catch (Exception e) {
			return new ResponseEntity<String>(e + "", HttpStatus.BAD_REQUEST);
		}
		logger.info("Note reminder added successfully");
		return new ResponseEntity<String>("Note reminder added successfully" + "", HttpStatus.OK);
	}
	
}
