package com.rockagen.malen.connector;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.rockagen.malen.core.ApplicationContainer;
import com.rockagen.malen.filter.ApplicationFilterChain;

/**
 * <b>DispatcherFilter class</b>, this class implements {@link Filter} interface
 * 
 * <pre>
 * At {@link ApplicationFilterChain} has a dispacher boolean,that's for this filter
 * </pre>
 * 
 * @author AGEN
 * 
 */
public class ApplicationDispatcherFilter implements Filter {
	private HttpRequest request;

	public ApplicationDispatcherFilter(HttpRequest request) {
		this.request = request;

	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException,
			ServletException {
		String requestUri = ApplicationContainer.getFORWARDURI();
		request.setUri(requestUri);
		ApplicationFilterChain.setDispatcherRequest(request);
		arg2.doFilter(arg0, arg1);

	}

	/*
	 * These no implements(non-Javadoc)
	 */
	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	@Override
	public void destroy() {
	}

}
