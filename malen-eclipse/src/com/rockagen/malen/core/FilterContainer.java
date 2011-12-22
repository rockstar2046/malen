package com.rockagen.malen.core;

import java.io.Serializable;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;

import com.rockagen.malen.filter.ApplicationFilterChain;
import com.rockagen.malen.filter.ApplicationFilterConfig;

/**
 * <b>Filter Container </b><br>
 * <p>
 * fill with some filter config
 * 
 * @author AGEN
 * 
 */
public class FilterContainer implements Serializable {

	private static final long serialVersionUID = -1027501840563378151L;

	private static FilterChain filterChain = ApplicationFilterChain.getFilterChain();
	private static FilterConfig filterConfig = new ApplicationFilterConfig();

	/**
	 * @return filterChain
	 */
	public static FilterChain getFilterChain() {

		return filterChain;

	}

	/**
	 * @return filterConfig
	 */
	public static FilterConfig getFilterConfig() {

		return filterConfig;

	}
}
