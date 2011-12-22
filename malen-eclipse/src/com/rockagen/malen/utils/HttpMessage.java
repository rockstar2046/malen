package com.rockagen.malen.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * <b>Http Messages utils</b>
 * 
 * @author AGEN
 * 
 */

public class HttpMessage {

	/**
	 * some final Message
	 * 
	 * @param str
	 * @return message
	 */
	public static String getNotFoundMessage(String str) {
		String message = " <HTML>"
				+ "<HEAD><TITLE>404 Not Found</TITLE></HEAD>"
				+ "<BODY style=\"background-color:#FCFCFC;\"><div style=\"width:100%;\" ><h3>404 Not Found. </h3> <br/><hr/>"
				+ "<font size=\"3\" face=\"arial\">The requested URL</font><font style=\"font-family:verdana;color:red;\"> "
				+ str + "</font> <font size=\"3\" face=\"arial\">was not found on this server</font>"
				+ "</div></BODY></HTML>";
		return message;
	}

	/**
	 * some final message
	 * 
	 * @param str
	 * @return message
	 */
	public static String getInternalServerErrorMessage(String str) {
		String message = "<HTML>"
				+ "<HEAD><TITLE>HTTP Status 500 -</TITLE></HEAD>"
				+ "<BODY style=\"background-color:#FCFCFC;\"><div style=\"width:100%;\" ><h3>HTTP Status 500-. </h3> <br/>"
				+ "<font size=\"3\" face=\"arial\">The server encountered an internal error () that prevented it from fulfilling this request."
				+ "</font><br/><br/><hr/><br/><font size=\"3\" face=\"arial\" color=\"#FF1515\">" + str
				+ "</font></div></BODY></HTML>";
		return message;
	}

	/**
	 * This replaceAll " " to "&nbsp;"
	 * 
	 * @param number
	 * @return htmlSpace
	 */
	public static String getHtmlSpace(int number) {
		StringBuffer sb = new StringBuffer();
		String htmlSpace;
		for (int i = 0; i < number; i++) {
			sb.append("&nbsp;");
		}
		htmlSpace = sb.toString();

		return htmlSpace;
	}

	public static String toHtmlTag(String src) {
		String[] srcTag = { "\n", "\t\n" };
		String[] htmlTag = { "</br>", "</br>" };
		String result = StringUtils.replaceEach(src, srcTag, htmlTag);
		return result;
	}

}
