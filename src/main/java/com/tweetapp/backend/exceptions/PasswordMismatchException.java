package com.tweetapp.backend.exceptions;

public class PasswordMismatchException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PasswordMismatchException() {
		super();
	}

	public PasswordMismatchException(String message) {
		super(message);
	}

	public PasswordMismatchException(Throwable cause) {
		super(cause);
	}

}
