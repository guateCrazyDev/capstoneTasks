package com.supportTicket.supportTicket.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalException {
	@ExceptionHandler(ElementAlreadyExistsException.class)
	public ResponseEntity<String> elementAlreadyExistsException(
			ElementAlreadyExistsException exc){
		return new ResponseEntity<>(exc.getMessage(),HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(PasswordException.class)
	public ResponseEntity<String>passwordException(
			PasswordException exc){
		return new ResponseEntity<>(exc.getMessage(),HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ElementNotFoundException.class)
	public ResponseEntity<String> elementNotFoundException(
			ElementNotFoundException exc){
		return new ResponseEntity<>(exc.getMessage(),HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(ImageNotFoundException.class)
	public ResponseEntity<String> imageNotFoundException(
			ImageNotFoundException exc){
		return new ResponseEntity<>(exc.getMessage(),HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> exception(RuntimeException ex){
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> excep(Exception e){
		return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
	}
}
