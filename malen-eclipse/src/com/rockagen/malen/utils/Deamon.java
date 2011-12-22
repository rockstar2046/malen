package com.rockagen.malen.utils;

import com.rockagen.malen.properties.ServiceConfig;

/**
 * <b>Some deamon info config utils</b>
 * 
 * @author AGEN
 * 
 */
public class Deamon {

	/**
	 * <p>
	 * Decide debug is true ?
	 * </p>
	 * 
	 * if true come message will print on console
	 * 
	 * @return true ,false
	 */
	public static boolean isDebug() {
		if (ServiceConfig.getString("Debug-daemonPrint").equalsIgnoreCase("true"))
			return true;
		return false;
	}

}
