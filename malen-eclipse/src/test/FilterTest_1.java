package test;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FilterTest_1 implements Filter {

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) servletRequest;
		HttpServletResponse httpResp = (HttpServletResponse) servletResponse;
		System.out.println("### Filter1 is working ####");
		filterChain.doFilter(httpReq, httpResp);
		System.out.println("### Filter1 is over    ####");
	}

	public void destroy() {

	}

	public void init(FilterConfig filterConfig) throws ServletException {

	}

}