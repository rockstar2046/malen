package com.rockagen.malen.exception;

/**
 * 
 * <b>Wrap dispatcher interrupt exception</b><br>
 * 
 * @author AGEN
 * 
 */
public class MalenDispatcherException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 * 
	 */
	public MalenDispatcherException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param str
	 */
	public MalenDispatcherException(String str) {
		super(str);
	}

}
