package com.rockagen.malen.connector;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import com.rockagen.malen.core.ApplicationFilter;
import com.rockagen.malen.exception.MalenFilterException;
import com.rockagen.malen.filter.ApplicationFilterChain;
import com.rockagen.malen.filter.ApplicationFinallyProcess;
import com.rockagen.malen.filter.FilterMatcher;
import com.rockagen.malen.properties.FilterIniConfig;
import com.rockagen.malen.utils.MalenLog;

/**
 * 
 * <b>Connection main class</b><br>
 * <p>
 * the key entry point is <code>processs</code>
 * 
 * @author AGEN
 * 
 */
public class ConnectionProcess {

	private static Log LOG = MalenLog.getLog();

	/**
	 * process MalenStart accept request
	 * 
	 * @param input
	 * @param output
	 */
	public static void process(InputStream input, OutputStream output) {

		// Create Request object

		HttpRequestFacade request = new HttpRequestFacade(input);
		HttpResponseFacade response;

		synchronized (request) {
			// Create Response object
			response = new HttpResponseFacade(output);
			response.setRequest(request.getRequest());
		}

		// Handler filter
		handleFilter(request, response, response.getResponse());

	}

	/**
	 * 
	 * handler filter <br>
	 * <p>
	 * if no filter call
	 * <code> ApplicationFinallyProcess.applicationFinally()</code>
	 * </p>
	 * <p>
	 * else if filter is not null ,call <code>matchFilterUrl(request)</code>
	 * check this request need filter ?
	 * </p>
	 * <li>if <code>matchFilterUrl(request)</code> return true,first call
	 * <code>ApplicationFilter.processFilter(filter, request, response)</code></li>
	 * <br>
	 * <li>else call <code>ApplicationFinallyProcess.applicationFinally()</code>
	 * </li><br>
	 * 
	 * @param request
	 * @param response
	 */
	public static void handleFilter(HttpServletRequest request, HttpServletResponse response, HttpResponse innerResp) {

		List<Filter> filters = ApplicationFilterChain.getFilterlist();
		if (filters.size() == 0) {
			finalProcess(innerResp);
			return;
		}
		try {
			if (matchFilterUrl(request)) {
				synchronized (filters) {
					Filter filter = filters.get(0);
					ApplicationFilterChain.setCount(0);

					ApplicationFilterChain.setInnerResp(innerResp);
					ApplicationFilter.processFilter(filter, request, response);
				}

			} else {

				finalProcess(innerResp);

			}

		} catch (NoSuchMethodException e) {
			throw new MalenFilterException("\"" + e.getMessage() + "\"");
		} catch (SecurityException e) {
			throw new MalenFilterException("\"" + e.getMessage() + "\"");
		} catch (IllegalAccessException e) {
			throw new MalenFilterException("\"" + e.getMessage() + "\"");
		} catch (IllegalArgumentException e) {
			throw new MalenFilterException("\"" + e.getMessage() + "\"");
		} catch (InvocationTargetException e) {
			throw new MalenFilterException("\"" + e.getMessage() + "\"");

		} catch (MalenFilterException e) {
			LOG.error(e.getMessage(), e);
		}

	}

	/**
	 * <p>
	 * Process source
	 * </P>
	 * <P>
	 * Meaning the last filter,if have filter.
	 * </P>
	 * 
	 * @param innerResp
	 */
	public static void finalProcess(HttpResponse innerResp) {
		ApplicationFinallyProcess afp = new ApplicationFinallyProcess(innerResp);
		afp.applicationFinally();
	}

	/**
	 * @param request
	 * @return the request is need filter ?
	 */
	private static boolean matchFilterUrl(HttpServletRequest request) {
		String temp = request.getRequestURL().toString();
		String temp2 = request.getLocalAddr();
		String url = StringUtils.substringAfter(temp, temp2);

		boolean flag = FilterMatcher.urlFilterMachers(url);

		return flag;
	}

	/**
	 * check filter has exist ?
	 * 
	 * @return true false
	 */

	public static boolean checkFilter() {
		Map<String, String> filterNameValue = FilterIniConfig.getFilternamevalue();
		if (filterNameValue.size() > 0)
			return true;
		return false;
	}

}
