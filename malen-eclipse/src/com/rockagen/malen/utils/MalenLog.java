package com.rockagen.malen.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * <b>Wrap log configuration</b>
 * 
 * @author AGEN
 * 
 */
public class MalenLog {

	private final static Log log = LogFactory.getLog(MalenLog.class);

	/**
	 * Get log
	 * 
	 * @return log
	 */
	public static Log getLog() {
		return log;
	}

}
