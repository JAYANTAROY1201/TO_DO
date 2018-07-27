package com.todo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.todo.userservice.model.ResponseBean;

/**
 * <p>purpose: this class is designed to handle all exceptions</p><br>
 * @author JAYANTA ROY
 * @version 1.0
 * @since 10/07/18
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/**
	 * This method to handle Note exception
	 * @param exception
	 * @return
	 */
	@ExceptionHandler(NoteReaderException.class)
	public ResponseEntity<ResponseBean> handleNoteException(NoteReaderException exception) {
		logger.info("Error Occured While Registration" + exception.getMessage(), exception);
		ResponseBean response = new ResponseBean();
		response.setMessage(exception.getMessage());
		response.setStatus(-1);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	/**
	 * This method handle 
	 * @param exception
	 * @return
	 */
	@ExceptionHandler(LoginException.class)
	public ResponseEntity<ResponseBean> handleLoginException(LoginException exception) {
		logger.info("Error Occured While Registration" + exception.getMessage(), exception);

		ResponseBean response = new ResponseBean();

		response.setMessage(exception.getMessage());
		response.setStatus(-2);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	/**
	 * @param exception
	 * @return
	 */
	@ExceptionHandler(SignupException.class)
	public ResponseEntity<ResponseBean> handleSignupException(SignupException exception) {
		logger.info("Error Occured While Registration" + exception.getMessage(), exception);

		ResponseBean response = new ResponseBean();

		response.setMessage(exception.getMessage());
		response.setStatus(-3);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
}