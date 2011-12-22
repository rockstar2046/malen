package com.rockagen.malen.exception;

/**
 * 
 * <b>Wrap servletOutputStream in Dispatcher Runtime call interrupt
 * exception</b><br>
 * 
 * @author AGEN
 * 
 */

public class MalenOutputStreamDispatcherException extends MalenException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 * 
	 */
	public MalenOutputStreamDispatcherException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param str
	 */
	public MalenOutputStreamDispatcherException(String str) {
		super(str);
	}

}
