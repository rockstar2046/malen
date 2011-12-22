package com.rockagen.malen.connector;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import com.rockagen.malen.properties.ServiceConfig;
import com.rockagen.malen.properties.WebConfig;
import com.rockagen.malen.utils.Deamon;
import com.rockagen.malen.utils.MalenLog;
import com.rockagen.malen.utils.MalenPrint;

/**
 * <b>HttpServletRequest implements class</b>
 * <p>
 * HttpRequest handle server request
 * </p>
 * <p>
 * <b>note:</b> HttRequest has a <b>redirectFlag</b> boolean, that's for
 * {@link HttpResponse } <i>As follows:</i>
 * </p>
 * 
 * <p>
 * we hava to redirect a new page in order to handle embedded frame
 * </p>
 * eg:
 * 
 * <pre>
 * < FRAME src="overview-frame.html" name="packageListFrame" title="All Packages">
 * </pre>
 * 
 * <p>
 * if we don't redirect a new page ,we just input
 * <b>http://localhost:8080/J2EE</b>,we can't get embedded frame source
 * </p>
 * 
 * <p>
 * so, this <b>redirectFlag</b> = true,we can call HttpResponse's
 * handleRequestResource()
 * </p>
 * 
 * <pre>
 * public void handleRequestResource{
 *     ......
 *     
 *   if (file.exists() && !file.isDirectory()) { invalidUrl =	true; 
 *  
 *       if (request.isRedirectFlag()) {      //this we  check main
 * 	            sendRedirect(request.getUri()); 
 *                return; 
 *       }
 * 	 .....
 *   }
 * }
 * </pre>
 * 
 * <p>
 * we will check <b>redirectFlag</b> and call HttpResponse's sendRedirect()
 * redirect a new page
 * </p>
 * 
 * <p>
 * like this <b>http://localhost:8080/J2EE/index.html</b>
 * 
 * this we can get embedded frame
 * </p>
 * 
 * 
 * @author AGEN
 * 
 */
public class HttpRequest implements HttpServletRequest {

	private InputStream input;
	private String uri = "";
	private String params = "";
	private String method = "";
	private String contentType = "";
	private int contentLength = 0;
	private String responsePath = "";
	private String servletContextPath = "";
	private String encoding = "UTF-8";
	private boolean changeEncoding=false;
	protected boolean redirectFlag = false;
	private Map<String, String[]> parameterMap = new HashMap<String, String[]>();
	private Map<String, String> requestHeaders = new HashMap<String, String>();
	private boolean debug = Deamon.isDebug();
	private final static String ROOT = ServiceConfig.getString("Host-root");

	private Log LOG = MalenLog.getLog();
	
	public HttpRequest(InputStream input) {
		this.input = input;
	}

	/**
	 * 
	 * parse request resource and provide some info to HttpServe Class
	 * 
	 */
	public void parse() {

		this.parseUri();

		// this is handle some contentType
		String fileSuffix = uri.substring(uri.lastIndexOf(".") + 1).trim();

		if (!StringUtils.isBlank(ServiceConfig.getContentTypeMap().get(fileSuffix))) {
			contentType = ServiceConfig.getContentTypeMap().get(fileSuffix);
		} else {
			contentType = "application/octet-stream";
		}

	}

	/**
	 * Handler socket InputStream get request resource
	 */
	private synchronized void parseUri() {
		byte[] buffer = new byte[4096];
		try {
			int a;
			a = input.read(buffer);
			if (a == -1) {
				input.close();
			}
		} catch (IOException e) {
			LOG.error(e);
		}
		// we wrap a BufferedReader from above buffer
		BufferedReader httpRequest = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer)));

		String request_resource;
		try {
			// a temp variable as for follow variable "uri" refer
			request_resource = httpRequest.readLine();
			// --------daemon print if debug=true -------------
			if (debug) {

				MalenPrint.startPrint();

				// this for daemon system out print
				MalenPrint.print(request_resource);
			}
			String line;

			while ((line = httpRequest.readLine()) != null) {
				// --------daemon print if debug=true -------------
				if (debug) {
					MalenPrint.print(line);
				}
				// add RequestHeaders

				if (!StringUtils.isBlank(line)) {
					addRequestHeaders(line);
				}

				if (StringUtils.isBlank(line)) {
					params = URLDecoder.decode(httpRequest.readLine().trim(), encoding);

					// --------daemon print if debug=true -------------
					if (debug) {
						MalenPrint.print(params);
						MalenPrint.print("");
						MalenPrint.overPrint();
						MalenPrint.print("");
					}
					// this for HttpResponse that we need some parameters ex:
					// post parameters...
					break;

				}

			}

			/*
			 * page eg: "GET /foo/ha HTTP/1.1" we need "/foo/ha"
			 */
			uri = request_resource.substring(request_resource.indexOf("/") + 1, request_resource.lastIndexOf("/") - 5)
					.trim();

			// handle uri
			resolveUri();

			// decoding

			uri = URLDecoder.decode(uri, encoding);

			// set Method
			String[] request_method = request_resource.split(" ");
			method = request_method[0];

			// This is accept some get parameters
			if (uri.indexOf("?") > -1) {
				params = URLDecoder.decode(params.trim(), encoding);
				requestParams(params);
				uri = uri.substring(0, uri.indexOf("?"));
			}

			// This is accept some post parameters
			if (this.getMethod().equalsIgnoreCase("POST")) {
				requestParams(params);
			}

		} catch (Exception e) {
			// Do nothing here
		}
	}

	/**
	 * add request headers<br>
	 * like this:<br>
	 * <li>Host: localhost:8080</li><br>
	 * <li>User-Agent: Mozilla/5.0 (Ubuntu; X11; Linux i686; rv:8.0)
	 * Gecko/20100101 Firefox/8.0</li>
	 * 
	 * @param line
	 */
	private void addRequestHeaders(String line) {

		String[] headers = StringUtils.split(line, ":", 2);
		synchronized (requestHeaders) {
			if (!StringUtils.isBlank(headers[0]) && !StringUtils.isBlank((headers[1]).trim()))
				requestHeaders.put(headers[0], headers[1]);
		}
	}

	/**
	 * <b>This Method handle request uri and return a resolved uri</b>
	 * 
	 */
	private void resolveUri() {

		/*
		 * this handle ROOT index
		 */
		if (uri.equals("")) {

			for (int j = 0; j < 1; j++) {
				// if not config index page
				if (WebConfig.getString(ROOT + "_welcome-file-list_welcome-file_0") == null) {
					uri = "index.html";
					responsePath = ROOT + "/";
					break;
				}
				for (int i = 0; i < 5; i++) {

					if (WebConfig.getString(ROOT + "_welcome-file-list_welcome-file_" + i) != null) {
						uri = WebConfig.getString(ROOT + "_welcome-file-list_welcome-file_" + i);
						responsePath = ROOT + "/";
						break;
					}
				}
			}
		}
		/*
		 * index page we order to differentiate "GET /  HTTP/1.1" eg:
		 * "GET /  HTTP/1.1" VS "GET /A HTTP/1.1"
		 */

		if (uri.indexOf('.', 1) == -1 && !StringUtils.isBlank(uri.substring(1)) && uri.indexOf("/", 1) == -1) {
			String temp = uri;

			if (WebConfig.getString(temp + "_welcome-file-list_welcome-file_0") != null) {
				for (int i = 0; i < 5; i++) {
					if (WebConfig.getString(temp + "_welcome-file-list_welcome-file_" + i) != null) {
						uri = WebConfig.getString(temp + "_welcome-file-list_welcome-file_" + i);
						responsePath = temp + "/";

						/*
						 * we hava to redirect a new page in order to handle
						 * embedded frame
						 * 
						 * eg: <FRAME src="overview-frame.html"
						 * name="packageListFrame" title="All Packages">
						 * 
						 * if we don't redirect a new page ,we just input
						 * http://localhost:8080/J2EE
						 * 
						 * we can't get embedded frame source
						 * 
						 * this redirectFlag = true,we can call HttpResponse's
						 * handleRequestResource
						 * 
						 * 
						 * public void handleRequestResource ...... if
						 * (file.exists() && !file.isDirectory()) { invalidUrl =
						 * true; if (request.isRedirectFlag()) { ////this we
						 * check main sendRedirect(request.getUri()); return; }
						 * .....
						 * 
						 * we will check redirectFlag and call HttpResponse's
						 * sendRedirect() redirect a new page
						 * 
						 * like this http://localhost:8080/J2EE/index.html
						 * 
						 * this we can get embedded frame
						 */
						redirectFlag = true;
						break;
					}

				}

			}
		} else {
			// ROOT source
			if (uri.indexOf("/", 1) < 0) {
				responsePath = ROOT + "/";
			} else {

				// GET Context name
				String temp = uri.substring(0, uri.indexOf("/", 1));
				// set ROOT Context
				if (!StringUtils.isBlank(temp) && WebConfig.getContext(temp) == null) {
					responsePath = ROOT + "/";
				}

				// if uri != /Context and uri request is other source
				if (!StringUtils.isBlank(temp) && WebConfig.getContext(temp) != null) {
					responsePath = "";
					servletContextPath = temp + "/";
				}
			}
		}

	}

	/**
	 * Split params by &
	 * @param params
	 * @return keys
	 */
	private String[] getParams(String params) {
		String[] keys = null;

		if (StringUtils.isBlank(params))
			return null;
		keys = params.split("&");
		return keys;
	}

	/**
	 * <pre>
	 * This handle order to function getParameters and get parameterMap init
	 * some params
	 * </pre>
	 * 
	 * @param params
	 */
	protected void requestParams(String params) {
		if (params.indexOf("=") > -1) {
			String[] requestParams = getParams(params);
			for (String par : requestParams) {
				String[] values = par.split("=");
				String[] value = { values[1] };
				if (parameterMap.get(values[0]) != null) {
					List<String> list = Arrays.asList(parameterMap.get(values[0]));
					list.add(values[1]);
					String[] temp = (String[]) list.toArray();
					parameterMap.put(values[0], temp);

				} else {
					parameterMap.put(values[0], value);
				}
			}

		}
	}

	/**
	 * Inner set ContentType
	 * @param contentType
	 */
	protected void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Inner get URI
	 * @return uri
	 */
	protected String getUri() {

		return uri;
	}

	/**
	 * Response path ex: ROOT/ xxx/
	 * 
	 * @return Context path
	 */
	public String getResponsePath() {
		return responsePath;
	}


	/**
	 * @return servlet Context Path
	 */
	public String getServletContextPath() {
		return servletContextPath;
	}

	/**
	 * Inner set ServletContextPath
	 * @param src
	 */
	protected void setServletCOntextPath(String src) {
		this.servletContextPath = src;
	}


	/**
	 * 
	 * Inner set URI
	 * @param uri
	 */
	protected void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * @return redirectFlag
	 */
	public boolean isRedirectFlag() {
		return redirectFlag;
	}
	
	/**
	 * Inner set ContentLength
	 * @param contentLength
	 */
	protected void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	/**
	 *  Inner set Response Path
	 * @param str
	 */
	protected void setResponsePath(String str) {

		this.responsePath = str;

	}
	
	/**
	 * YES
	 * @see javax.servlet.http.HttpServletRequest#getMethod()
	 */
	@Override
	public String getMethod() {
		
		return this.method;
	}

	/** YES
	 * @see javax.servlet.ServletRequest#getContentType()
	 */
	@Override
	public String getContentType() {
		return contentType;
	}


	/** YES
	 * @see javax.servlet.ServletRequest#getLocalPort()
	 */
	@Override
	public int getLocalPort() {
		return Integer.parseInt(ServiceConfig.getString("Connector-port"));
	}

	/** YES
	 * @see javax.servlet.ServletRequest#getParameter(java.lang.String)
	 */
	@Override
	public String getParameter(String arg) {
		String parameter = "";
		String[] temp = parameterMap.get(arg);
		if (temp == null)
			return "";
		if (temp.length <= 1) {
			parameter = temp[0];
		} else {
			for (String str : temp) {
				parameter += str + " ";
			}
		}
		if(changeEncoding){
			byte[] b;
			try {
				b = parameter.getBytes("UTF-8");
				parameter = new String(b, encoding);
			} catch (UnsupportedEncodingException e) {

			}

		}
		return parameter;
	}

	/** YES
	 * @see javax.servlet.ServletRequest#getParameterMap()
	 */
	@Override
	public Map<String, String[]> getParameterMap() {
		return parameterMap;
	}

	/** YES
	 * @see javax.servlet.ServletRequest#getParameterValues(java.lang.String)
	 */
	@Override
	public String[] getParameterValues(String arg0) {

		String[] temp;
		List<String[]> list = new ArrayList<String[]>();
		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			list.add(entry.getValue());
		}
		temp = (String[]) list.toArray();
		return temp;
	}

	/** YES
	 * @see javax.servlet.ServletRequest#getReader()
	 */
	@Override
	public BufferedReader getReader() throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(input));
		return br;
	}

	/** YES
	 * @see javax.servlet.ServletRequest#getServerPort()
	 */
	@Override
	public int getServerPort() {

		return Integer.parseInt(ServiceConfig.getString("Remote-port"));
	}

	/** YES
	 * @see javax.servlet.ServletRequest#getContentLength()
	 */
	@Override
	public int getContentLength() {

		File file = new File(ServiceConfig.getRealPath() + ServiceConfig.getString("Host-appBase") + "/"
				+ getResponsePath() + getUri());
		int len = (int) file.length();
		if (len == 0)
			return contentLength;

		return len;
	}

	/** YES
	 * @see javax.servlet.ServletRequest#getInputStream()
	 */
	@Override
	public ServletInputStream getInputStream() throws IOException {

		ServletInputStream sis = new ApplicationServletInputStream(input);

		return sis;
	}

	/** YES
	 * @see javax.servlet.ServletRequest#getLocalAddr()
	 */
	@Override
	public String getLocalAddr() {
		String addr = requestHeaders.get("Host").trim();
		return addr;
	}

	/** YES
	 * @see javax.servlet.ServletRequest#getLocalName()
	 */
	@Override
	public String getLocalName() {
		String addr = getLocalAddr();
		String localName = addr.substring(0, addr.indexOf(":"));

		return localName;
	}

	/** YES
	 * @see javax.servlet.ServletRequest#getProtocol()
	 */
	@Override
	public String getProtocol() {

		return ServiceConfig.getString("Connector-protocol");
	}

	/** YES
	 * @see javax.servlet.ServletRequest#getRemoteHost()
	 */
	@Override
	public String getRemoteHost() {
		String remoteHost = requestHeaders.get("Host");
		return remoteHost;
	}

	/** YES
	 * @see javax.servlet.ServletRequest#getRemotePort()
	 */
	@Override
	public int getRemotePort() {
		String remoteHost = requestHeaders.get("Host");
		String[] temps = StringUtils.split(remoteHost, ":");
		if (!StringUtils.isBlank(temps[1])) {
			int remotePort = Integer.valueOf(temps[1]);
			return remotePort;
		}
		return 0;
	}

	/** YES
	 * @see javax.servlet.http.HttpServletRequest#getHeader(java.lang.String)
	 */
	@Override
	public String getHeader(String name) {
		String header = requestHeaders.get(name);
		return header;
	}

	/** YES
	 * @see javax.servlet.http.HttpServletRequest#getRequestURI()
	 */
	@Override
	public String getRequestURI() {

		if (method.startsWith("POST")) {
			if (uri.contains("?")) {
				String temp = uri.substring(0, uri.indexOf("?"));
				String realUri = temp.substring(temp.lastIndexOf("/"));
				return realUri;
			}
			return uri.substring(uri.lastIndexOf("/"));

		}
		return "/" + uri;

	}

	/** YES
	 * @see javax.servlet.http.HttpServletRequest#getRequestURL()
	 */
	@Override
	public StringBuffer getRequestURL() {
		StringBuffer sb = new StringBuffer();
		String localhost = ServiceConfig.getString("Host-name");
		int port = Integer.valueOf(ServiceConfig.getString("Connector-port"));
		String str = "/" + uri;
		try {
			URL url = new URL("HTTP", localhost, port, str);
			sb.append(url.toString());
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage(), e);
		}

		return sb;
	}

	/** YES
	 * @see javax.servlet.http.HttpServletRequest#getContextPath()
	 */
	@Override
	public String getContextPath() {
		return getResponsePath();
	}

	/** YES
	 * @see javax.servlet.ServletRequest#getCharacterEncoding()
	 */
	@Override
	public String getCharacterEncoding() {

		return encoding;
	}

	/** YES
	 * @see javax.servlet.ServletRequest#setCharacterEncoding(java.lang.String)
	 */
	@Override
	public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
		if (!CharEncoding.isSupported(arg0)) {
			throw new UnsupportedEncodingException("\"" + arg0 + " is UnsupportedEncoding !\"");
		}
		encoding = arg0;
		changeEncoding=true;

	}

	/** YES
	 * @see javax.servlet.ServletRequest#getRequestDispatcher(java.lang.String)
	 */
	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		RequestDispatcher reqDis = new ApplicationDispatcher(arg0, this);
		return reqDis;
	}

	/** YES
	 * @see javax.servlet.http.HttpServletRequest#getServletPath()
	 */
	@Override
	public String getServletPath() {
		
		return this.getServletContextPath();
	}
	
	/*
	 * These no implements(non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest
	 */

	@Override
	public Enumeration<String> getParameterNames() {
		return null;
	}

	@Override
	public AsyncContext getAsyncContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAttribute(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DispatcherType getDispatcherType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Locale getLocale() {

		return null;
	}

	@Override
	public Enumeration<Locale> getLocales() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRealPath(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getScheme() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAsyncStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAsyncSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeAttribute(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean authenticate(HttpServletResponse arg0) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getAuthType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cookie[] getCookies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getDateHeader(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getIntHeader(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Part getPart(String arg0) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPathInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPathTranslated() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQueryString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteUser() {
		return null;
	}

	@Override
	public String getRequestedSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpSession getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpSession getSession(boolean arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUserInRole(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void login(String arg0, String arg1) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void logout() throws ServletException {
		// TODO Auto-generated method stub

	}

}
