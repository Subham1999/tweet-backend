package com.tweetapp.backend.exceptions;

public class InvalidRequest extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidRequest(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidRequest(String string) {
		super(string);
	}

}
