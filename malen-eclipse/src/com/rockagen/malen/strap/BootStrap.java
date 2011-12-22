package com.rockagen.malen.strap;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;

/**
 * <b> Malen server main point</b>
 * 
 * 
 * @author AGEN
 * 
 */
public class BootStrap {

	private static boolean SHUTDOWN = false;

	/**
	 * @return true false
	 */
	protected static boolean isSHUTDOWN() {
		return SHUTDOWN;
	}

	/**
	 * Set SHUTDOWN boolean<br>
	 * 
	 * @param sHUTDOWN
	 */
	protected static void setSHUTDOWN(boolean sHUTDOWN) {
		SHUTDOWN = sHUTDOWN;
	}

	/**
	 * malen main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		PropertyConfigurator.configure(log4jFilePath());

		StartService.bootstrap();
	}

	private static String log4jFilePath() {

		// This is for bootstrap.jar
/*		String currentPath = System.getProperty("user.dir");
		String log4jPath = StringUtils.substringBeforeLast(currentPath, File.separator) + File.separator + "conf"
				+ File.separator;
		File file = new File(log4jPath + "log4j.properties");*/
		
		// For eclipse 
		String currentPath = System.getProperty("user.dir");
		File file = new File(currentPath + File.separator + "conf" + File.separator + "log4j.properties");
		PropertyConfigurator.configure(file.getAbsolutePath());

		return file.getAbsolutePath();
	}

}
