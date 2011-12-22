package com.rockagen.malen.exception;

/**
 * <b>Wrap server runtime exception</b>
 * 
 * @author AGEN
 * 
 * @see RuntimeException
 */
public class MalenServerException extends MalenException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 * 
	 */
	public MalenServerException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param str
	 */
	public MalenServerException(String str) {
		super(str);
	}

}
