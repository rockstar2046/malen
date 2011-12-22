package com.rockagen.malen.connector;

import java.io.BufferedOutputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import com.rockagen.malen.core.ApplicationContainer;
import com.rockagen.malen.exception.MalenException;
import com.rockagen.malen.exception.MalenOutputStreamDispatcherException;
import com.rockagen.malen.exception.MalenServletException;
import com.rockagen.malen.properties.ServiceConfig;
import com.rockagen.malen.utils.ConvertChar;
import com.rockagen.malen.utils.HttpMessage;
import com.rockagen.malen.utils.MalenLog;

/**
 * <b> HttpServletResponse implements class </b>
 * <p>
 * HttpResponse is handle server response
 * </p>
 * 
 * @author AGEN
 * 
 */
public class HttpResponse implements HttpServletResponse {
	private HttpRequest request;
	private OutputStream output;
	protected Long contentLength = -1L;
	private BufferedOutputStream bufOutput;
	private String encoding = "UTF-8";
	private Map<String, String> headers = new HashMap<String, String>();
	protected int status = HttpServletResponse.SC_OK;
	protected boolean validUrl = false;
	private final static String ROOT = ServiceConfig.getString("Host-root");

	private Log LOG = MalenLog.getLog();

	public HttpResponse(OutputStream output) {
		this.output = output;
	}

	/**
	 * @param request
	 */
	public void setRequest(HttpRequest request) {
		this.request = request;
	}

	/**
	 * Get status Message
	 * 
	 * @param status
	 * @return status
	 */
	protected String getStatusMessage(int status) {
		switch (status) {
		case SC_OK:
			return ("OK");
		case SC_NOT_FOUND:
			return ("Not Found");
		case SC_FOUND:
			return ("Found");
		case SC_INTERNAL_SERVER_ERROR:
			return ("Internal Server Error");
		default:
			return ("HTTP Response Status " + status);
		}
	}

	/**
	 * This Method send Http Headers
	 * 
	 */
	protected void sendHeaders() {
		PrintWriter outputWriter = new PrintWriter(new OutputStreamWriter(output));
		String protocol = ServiceConfig.getString("Connector-protocol");
		outputWriter.print(protocol);
		outputWriter.print(" ");
		outputWriter.print(status);
		if (getStatusMessage(status) != null) {
			outputWriter.print(" ");
			outputWriter.print(getStatusMessage(status));
		}
		outputWriter.print("\r\n");
		outputWriter.print("Connection: keep-alive");
		outputWriter.print("\r\n");
		outputWriter.print("Date: " + new Date());
		outputWriter.print("\r\n");
		outputWriter.print("Server: " + ServiceConfig.getString("Version-name") + "/"
				+ ServiceConfig.getString("Version-id"));
		outputWriter.print("\r\n");
		if (!StringUtils.isBlank(getContentType())) {
			outputWriter.print("Content-Type: " + getContentType() + "\r\n");
		}
		if (getContentLength() >= 0) {
			outputWriter.print("Content-Length: " + getContentLength() + "\r\n");
		}

		synchronized (headers) {
			Iterator<String> names = headers.keySet().iterator();
			while (names.hasNext()) {
				String name = names.next();
				String values = headers.get(name);
				outputWriter.print(name);
				outputWriter.print(": ");
				outputWriter.print(values);
				outputWriter.print("\r\n");
			}

		}
		outputWriter.print("\r\n");
		outputWriter.flush();

	}

	/**
	 * @return headers map
	 */
	protected Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * @param headers
	 */
	protected void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	/**
	 * Handler request
	 * <p>
	 * if file exist ,read it
	 * </p>
	 * <p>
	 * else call ServletFind.process(Request request,Response response,String
	 * uri)
	 * </p>
	 * 
	 * @see ServletFind
	 */
	public void handleRequestResource() {
		PrintWriter errorWriter = new PrintWriter(new OutputStreamWriter(output));
		FileInputStream fis = null;
		try {

			File file = new File(ServiceConfig.getRealPath() + ServiceConfig.getString("Host-appBase") + "/"
					+ request.getResponsePath() + request.getUri());
			// File file = request.getRequestFile();

			try {
				if (!file.exists() && !file.isDirectory()) {
					// handle serverl
					ServletFind.process(request, this, request.getUri());
					if (validUrl) {
						setContentType("text/html");
						sendHeaders();
					}

				}

				if (file.exists() && !file.isDirectory()) {
					validUrl = true;
					if (request.isRedirectFlag()) {
						sendRedirect(request.getUri());
						sendHeaders();
						return;
					}
					bufOutput = new BufferedOutputStream(output);
					// Send http headers
					sendHeaders();
					fis = new FileInputStream(file);
					byte buf[] = new byte[fis.available()];
					fis.read(buf);

					bufOutput.write(buf);

					bufOutput.flush();
					fis.close();

				}
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
			/*
			 * check Url is valid
			 */
			if (!validUrl) {
				throw new MalenException("The requested URL \"" + request.getUri() + "\" IS NOT FOUND !");
			}
		}

		catch (MalenOutputStreamDispatcherException e) {
			LOG.error(e.getMessage(), e);
		} catch (MalenServletException e) {
			CharArrayWriter caw = new CharArrayWriter();
			e.printStackTrace(new PrintWriter(caw));
			String stacktrace = caw.toString().replace(" ", "&nbsp;").replaceAll("\t", HttpMessage.getHtmlSpace(10));
			stacktrace = HttpMessage.toHtmlTag(stacktrace);
			String errorMessage = HttpMessage.getInternalServerErrorMessage(stacktrace);
			handleErrorHeaders(errorWriter, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
			LOG.error(e.getMessage(), e);

		}
		/*
		 * we order to simple we catch all Exception to this and alert some
		 * errorMessage to Client
		 */
		catch (MalenException e) {

			String errorMessage = HttpMessage.getNotFoundMessage(request.getUri());
			handleErrorHeaders(errorWriter, HttpServletResponse.SC_NOT_FOUND, errorMessage);
			LOG.error(e.getMessage(), e);
		}

	}

	/**
	 * 
	 * Handle some error headers
	 * 
	 * @param errorWriter
	 * @param status
	 * @param errorMessage
	 */
	private void handleErrorHeaders(PrintWriter errorWriter, int status, String errorMessage) {
		this.status = status;
		setContentType("text/html");
		setContentLength(errorMessage.length());
		sendHeaders();
		errorWriter.print(errorMessage);
		errorWriter.flush();
		errorWriter.close();

	}

	/**
	 * Inner ContentLength
	 * 
	 * @return ContentLength
	 */
	private int getContentLength() {
		return request.getContentLength();
	}

	/**
	 * YES
	 * 
	 * @see javax.servlet.http.HttpServletResponse#sendRedirect(java.lang.String)
	 */
	@Override
	public void sendRedirect(String arg) throws IOException {

		/*
		 * we just send client a 302 response
		 */
		String localhost = ServiceConfig.getString("Host-name");
		int port = Integer.valueOf(ServiceConfig.getString("Connector-port"));
		if (arg.startsWith("/")) {
			URL url = new URL("HTTP", localhost, port, arg);
			status = HttpServletResponse.SC_FOUND;
			setHeader("Location", url.toString());
		} else {
			// getResponsePath()
			String temp = request.getResponsePath();
			// convert '\\' to '/'
			temp = ConvertChar.convertSeparator(temp);
			String rootPath = "";
			if (temp.equals(ROOT + "/")) {
				rootPath = "/";

			} else if (temp == "" && !StringUtils.isBlank(request.getServletContextPath())) {

				String temp2 = request.getServletContextPath();
				temp2 = ConvertChar.convertSeparator(temp2);
				if (temp2.equalsIgnoreCase(ROOT + "/")) {
					rootPath = "/";
				} else {
					rootPath = "/" + temp2;
				}

			} else {
				rootPath = "/" + temp;
			}
			URL url = new URL("HTTP", localhost, port, rootPath + arg);

			status = HttpServletResponse.SC_FOUND;
			setHeader("Location", url.toString());
		}
	}

	/**
	 * YES
	 * 
	 * @see javax.servlet.http.HttpServletResponse#addHeader(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void addHeader(String name, String value) {
		synchronized (headers) {
			String values = headers.get(name);
			if (values == null) {
				headers.put(name, value);
			}
		}

	}

	/**
	 * YES
	 * 
	 * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void setHeader(String name, String value) {

		synchronized (headers) {
			if (!StringUtils.isBlank(name) && !StringUtils.isBlank(value)) {
				headers.put(name, value);
			}
		}

	}

	/**
	 * YES
	 * 
	 * @see javax.servlet.ServletResponse#getWriter()
	 */
	@Override
	public ApplicationWriter getWriter() throws IOException {

		setContentType("text/html");
		ApplicationWriter writer = new ApplicationWriter(new OutputStreamWriter(output, encoding), true);
		writer.setResponse(this);
		ApplicationContainer.setNoIOopen(false);
		return writer;
	}

	/**
	 * YES
	 * 
	 * @see javax.servlet.ServletResponse#setContentLength(int)
	 */
	@Override
	public void setContentLength(int arg0) {
		request.setContentLength(arg0);

	}

	/**
	 * YES
	 * 
	 * @see javax.servlet.ServletResponse#setContentType(java.lang.String)
	 */
	@Override
	public void setContentType(String arg0) {
		request.setContentType(arg0);

	}

	/**
	 * YES
	 * 
	 * @see javax.servlet.ServletResponse#getContentType()
	 */
	@Override
	public String getContentType() {
		return request.getContentType();
	}

	/**
	 * YES
	 * 
	 * @see javax.servlet.ServletResponse#flushBuffer()
	 */
	@Override
	public void flushBuffer() throws IOException {
		getWriter().flush();

	}

	/**
	 * YES
	 * 
	 * @see javax.servlet.ServletResponse#getCharacterEncoding()
	 */
	@Override
	public String getCharacterEncoding() {
		return encoding;
	}

	/**
	 * YES
	 * 
	 * @see javax.servlet.ServletResponse#setCharacterEncoding(java.lang.String)
	 */
	@Override
	public void setCharacterEncoding(String arg0) {
		try {
			if (!CharEncoding.isSupported(arg0)) {
				throw new UnsupportedEncodingException("\"" + arg0 + " is UnsupportedEncoding !\"");
			}
			encoding = arg0;

		} catch (UnsupportedEncodingException e) {
			LOG.error(e.getMessage(), e);
		}

	}

	/**
	 * YES
	 * 
	 * @see javax.servlet.ServletResponse#getOutputStream()
	 */
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		setContentType("text/html");
		ApplicationServletOutputSteam sos = new ApplicationServletOutputSteam(output);
		sos.setResponse(this);
		ApplicationContainer.setNoIOopen(false);
		return sos;
	}

	/*
	 * These no implements(non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponse
	 */

	@Override
	public int getBufferSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCommitted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetBuffer() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBufferSize(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLocale(Locale arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCookie(Cookie arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addDateHeader(String arg0, long arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addIntHeader(String arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean containsHeader(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String encodeRedirectURL(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeRedirectUrl(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeURL(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeUrl(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHeader(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getHeaderNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getHeaders(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void sendError(int arg0) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendError(int arg0, String arg1) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDateHeader(String arg0, long arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setIntHeader(String arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStatus(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStatus(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

}
