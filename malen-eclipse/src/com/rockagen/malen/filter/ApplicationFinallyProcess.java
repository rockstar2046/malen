package com.rockagen.malen.filter;

import com.rockagen.malen.connector.HttpResponse;

/**
 * <b>Meaning that last filter</b>
 * <p>
 * If current context exist filter,wait all filter process,else,this is that the
 * last filter
 * </p>
 * 
 * @author AGEN
 * 
 */
public class ApplicationFinallyProcess {

	private HttpResponse response;

	/**
	 * 
	 * Initialize inner HttpResponse
	 * 
	 * @param resp
	 */
	public ApplicationFinallyProcess(HttpResponse resp) {
		this.response = resp;
	}

	/**
	 * Response something <br>
	 * <p>
	 * (the last request filter ,the first response filter)
	 * </p>
	 * <br>
	 * 
	 */
	public void applicationFinally() {

		response.handleRequestResource();

	}
}
