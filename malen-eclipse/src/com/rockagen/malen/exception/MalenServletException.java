package com.rockagen.malen.exception;

/**
 * <b>Wrap servlet runtime exception</b>
 * 
 * @author AGEN
 * @see RuntimeException
 */

public class MalenServletException extends MalenException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 * 
	 */
	public MalenServletException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param str
	 */
	public MalenServletException(String str) {
		super(str);
	}

}
