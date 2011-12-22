package com.rockagen.malen.exception;

/**
 * <b> Wrap ALl Runtime exception</b>
 * 
 * @author AGEN
 * @see RuntimeException
 * 
 */
public class MalenException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 * 
	 */
	public MalenException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param str
	 */
	public MalenException(String str) {
		super(str);
	}

}
