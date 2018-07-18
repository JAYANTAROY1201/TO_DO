package com.todo.noteservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todo.exception.NoteReaderException;
import com.todo.noteservice.model.Note;
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
	 * This method is used to  create notes
	 * @param title
	 * @param description
	 * @param jwt
	 * @return
	 */
	@RequestMapping(value = "/createnote", method = RequestMethod.POST)
	public ResponseEntity<String> createNote(@RequestParam String title, @RequestParam String description,
			@RequestParam String jwt) {
		logger.info("Create note method starts");

		try {
			noteService.doCreateNote(title, description, JwtTokenBuilder.parseJWT(jwt).getId());
		} catch (Exception e) {
			return new ResponseEntity<String>(e + "", HttpStatus.BAD_REQUEST);
		}
		logger.info("Create note method ends");
		return new ResponseEntity<String>("request granted", HttpStatus.OK);
	}


	/**
	 * This method is to read notes
	 * @param jwt
	 * @return
	 */
	@RequestMapping(value = "/viewnotes", method = RequestMethod.POST)
	public ResponseEntity<String> readNote(@RequestParam String jwt) {
		logger.info("Read note method starts");
		try {
			noteService.doReadNote(JwtTokenBuilder.parseJWT(jwt).getId());

		} catch (Exception e) {
			return new ResponseEntity<>(e + "", HttpStatus.BAD_REQUEST);
		}
		logger.info("Read note method ends");

		return new ResponseEntity<>("Request accepted", HttpStatus.OK);
	}
	
//	@RequestMapping(value = "/viewnotes", method = RequestMethod.POST)
//	public ResponseEntity<String> openNote(@RequestParam String jwt) {
//		logger.info("Read note method starts");
//		try {
//			noteService.doReadNote(JwtTokenBuilder.parseJWT(jwt).getId());
//
//		} catch (Exception e) {
//			return new ResponseEntity<>(e + "", HttpStatus.BAD_REQUEST);
//		}
//		logger.info("Read note method ends");
//
//		return new ResponseEntity<>("Request accepted", HttpStatus.OK);
//	}

	/**
	 * This method is to update notes
	 * @param jwt
	 * @param noteId
	 * @param newTitle
	 * @param newDescription
	 * @return
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/updatenotes", method = RequestMethod.POST)
	public ResponseEntity<String> updateNote(@RequestParam String jwt, @RequestParam String noteId,
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
	 * @param jwt
	 * @param noteId
	 * @return
	 * @throws NoteReaderException
	 */
	@RequestMapping(value = "/deletenotes", method = RequestMethod.POST)
	public ResponseEntity<String> deleteNote(@RequestParam String jwt, @RequestParam String noteId)
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
}
