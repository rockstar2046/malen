package com.rockagen.malen.exception;

/**
 * <b>Wrap filter runtime exception</b><br>
 * 
 * @author AGEN
 * @see RuntimeException
 */
public class MalenFilterException extends MalenException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 * 
	 */
	public MalenFilterException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param str
	 */
	public MalenFilterException(String str) {
		super(str);
	}

}
