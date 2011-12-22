package com.rockagen.malen.utils;

/**
 * <b>Convert util class</b>
 * 
 * @author AGEN
 * 
 */
public class ConvertChar {

	/**
	 * convert '\\' to '/'
	 * 
	 * @param str
	 * @return converted char
	 */
	public static String convertSeparator(String str) {

		if (str.contains("\\") || str.contains("\\\\")) {
			str = str.replace("\\", "/");
			return str;
		}
		return str;

	}

}
