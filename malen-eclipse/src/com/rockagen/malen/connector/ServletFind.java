package com.rockagen.malen.connector;

import java.io.IOException;

import com.rockagen.malen.core.ApplicationServlet;
import com.rockagen.malen.properties.ServiceConfig;
import com.rockagen.malen.properties.WebConfig;
import com.rockagen.malen.utils.ConvertChar;

/**
 * <b>Check url is servlet request ?</b>
 * 
 * @author AGEN
 * 
 */
public class ServletFind {
	private static String requestServlet = "";
	private final static String ROOT = ServiceConfig.getString("Host-root");

	/**
	 * Get request servlet
	 * 
	 * @return requestServlet
	 */
	public static String getRequestServlet() {
		return requestServlet;

	}

	/**
	 * <p>
	 * Decide request servlet is exist ?
	 * </p>
	 * <p>
	 * if exist,call
	 * {@link ApplicationServlet #process(HttpRequest, HttpResponse)}
	 * </p>
	 * else, throw a exception
	 * 
	 * @param request
	 * @param response
	 * @param uri
	 * @throws IOException
	 */
	public static void process(HttpRequest request, HttpResponse response, String uri) throws IOException {
		String temp = request.getResponsePath();
		if (temp == null) {
			return;
		}

		// convert '\\' to '/'
		temp = ConvertChar.convertSeparator(temp);

		synchronized (requestServlet) {

			requestServlet = temp + uri.substring(0);

			if (WebConfig.getServlet(requestServlet) != null) {
				ApplicationServlet apps = new ApplicationServlet();
				apps.process(request, response);

				response.validUrl = true;

			} else {

				if (WebConfig.getServlet(ROOT + "/" + requestServlet) != null) {
					requestServlet = ROOT + "/" + requestServlet;
					request.setServletCOntextPath(ROOT + "/");
					ApplicationServlet apps = new ApplicationServlet();
					apps.process(request, response);
					response.validUrl = true;
				}

			}
		}
	}

}
