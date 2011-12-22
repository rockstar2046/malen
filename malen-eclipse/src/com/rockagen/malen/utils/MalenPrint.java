package com.rockagen.malen.utils;

/**
 * <b>Print utils</b>
 * 
 * @author AGEN
 * 
 */
public class MalenPrint {

	/**
	 * if debug is true ,we can print some info on console
	 * 
	 * <p>
	 * this is start info
	 * </p>
	 * 
	 */
	public static void startPrint() {
		System.out.println();
		System.out.println("****  client request resource  ****");
		System.out.println("-------------------------START-------------------------");
		System.out.println();
	}

	/**
	 * if debug is true ,we can print some info on console
	 * 
	 * <p>
	 * this is over info
	 * </p>
	 * 
	 */
	public static void overPrint() {
		System.out.println();
		System.out.println("-------------------------OVER-------------------------");
		System.out.println();
	}

	/**
	 * 
	 * warp println() method
	 * 
	 * @param str
	 */
	public static void print(String str) {
		System.out.println(str);
	}
}
