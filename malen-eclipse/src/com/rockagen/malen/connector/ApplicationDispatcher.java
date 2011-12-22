package com.rockagen.malen.connector;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.rockagen.malen.core.ApplicationContainer;
import com.rockagen.malen.core.ApplicationServlet;
import com.rockagen.malen.core.FilterContainer;
import com.rockagen.malen.exception.MalenDispatcherException;
import com.rockagen.malen.filter.ApplicationFilterChain;
import com.rockagen.malen.properties.ServiceConfig;

/**
 * 
 * <b> RequestDispatcher implements class</b><br>
 * <br>
 * 
 * First, get the request uri,then Analyze the contentType<br>
 * <p>
 * If the above conditions is right,call
 * {@link ApplicationDispatcherFilter #doFilter(ServletRequest, ServletResponse, FilterChain)}
 * </p>
 * <p>
 * The "throw new MalenDispatcherException()" is a hook,because,if servlet call
 * forward,the servlet method after forward is trashy,these<br>
 * can't be call !
 * </p>
 * <p>
 * if this forward is over ,ok, connection is over the more infomation see
 * {@link ApplicationServlet #process(HttpRequest, HttpResponse)}
 * InvocationTargetException
 * </p>
 * 
 * @author AGEN
 * 
 */
public class ApplicationDispatcher implements RequestDispatcher {

	private String forwardUri;
	private HttpRequest request;

	public ApplicationDispatcher(String forwardUri, HttpRequest request) {
		this.forwardUri = forwardUri;
		this.request = request;
	}

	/** YES
	 * @see javax.servlet.RequestDispatcher#forward(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	@Override
	public void forward(ServletRequest req, ServletResponse resp) throws ServletException, IOException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		// Get absolute Path
		String absolutePath;
		String contextPath = request.getContextPath() + request.getServletPath();
		if (forwardUri.startsWith("/")) {
			absolutePath = ServiceConfig.getString("Host-root") + forwardUri;
		} else {
			absolutePath = contextPath + forwardUri;

		}
		// Analyze contentType
		String suffix = absolutePath.substring(absolutePath.lastIndexOf(".") + 1).trim();

		if (!StringUtils.isBlank(ServiceConfig.getContentTypeMap().get(suffix))) {
			// Set contentType
			response.setContentType(ServiceConfig.getContentTypeMap().get(suffix));
		} else {
			response.setContentType(ServiceConfig.getContentTypeMap().get("application/octet-stream"));
		}

		// Set forward URI to ApplicationContainer
		ApplicationContainer.setFORWARDURI(absolutePath);
		ApplicationDispatcherFilter disFilter = new ApplicationDispatcherFilter(this.request);
		FilterChain filterChain = FilterContainer.getFilterChain();
		ApplicationFilterChain.setDispacher(true);
		disFilter.doFilter(request, response, filterChain);

		throw new MalenDispatcherException();

	}

	/*
	 * These no implements(non-Javadoc)
	 * 
	 * @see javax.servlet.RequestDispatcher
	 */

	@Override
	public void include(ServletRequest arg0, ServletResponse arg1) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

}
