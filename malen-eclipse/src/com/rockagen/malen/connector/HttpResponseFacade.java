package com.rockagen.malen.connector;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * <b> HttpResponse Facade </b>protected some inner HttpResponse method
 * <p>
 * By constructor initialize HttpResponse and set HttpRequest
 * </p>
 * 
 * @author AGEN
 * 
 */
public class HttpResponseFacade implements HttpServletResponse {

	private HttpResponse response;
	private HttpRequest request;

	public HttpResponseFacade(OutputStream output) {
		response = new HttpResponse(output);
	}

	/**
	 * @return response
	 */
	protected HttpResponse getResponse() {
		return response;
	}

	/**
	 * @return request
	 */
	protected HttpRequest getRequest() {
		return request;
	}

	/**
	 * @param request
	 */
	protected void setRequest(HttpRequest request) {
		this.request = request;
		response.setRequest(this.request);
	}

	@Override
	public void sendRedirect(String arg0) throws IOException {

		response.sendRedirect(arg0);

	}

	@Override
	public void addHeader(String arg0, String arg1) {

		response.addHeader(arg0, arg1);

	}

	@Override
	public void setHeader(String arg0, String arg1) {

		response.setHeader(arg0, arg1);

	}

	@Override
	public PrintWriter getWriter() throws IOException {

		return response.getWriter();
	}

	@Override
	public void setContentLength(int arg0) {

		response.setContentLength(arg0);

	}

	@Override
	public void setContentType(String arg0) {

		response.setContentType(arg0);

	}

	@Override
	public void flushBuffer() throws IOException {

		response.flushBuffer();

	}

	@Override
	public String getContentType() {

		return response.getContentType();
	}

	@Override
	public String getCharacterEncoding() {

		return response.getCharacterEncoding();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return response.getOutputStream();
	}

	@Override
	public void setCharacterEncoding(String arg0) {
		response.setCharacterEncoding(arg0);

	}

	/*
	 * These no implements(non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getParameterNames()
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
