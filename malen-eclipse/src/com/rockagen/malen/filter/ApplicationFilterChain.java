package com.rockagen.malen.filter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;

import com.rockagen.malen.connector.ConnectionProcess;
import com.rockagen.malen.connector.HttpRequest;
import com.rockagen.malen.connector.HttpResponse;
import com.rockagen.malen.core.ApplicationContainer;
import com.rockagen.malen.exception.MalenOutputStreamDispatcherException;
import com.rockagen.malen.utils.MalenLog;

/**
 * <b> FilterChain implements class</b>
 * 
 * @author AGEN
 * 
 */
public class ApplicationFilterChain implements FilterChain {

	private static ArrayList<Filter> FILTERLIST = new ArrayList<Filter>();
	private static ApplicationFilterChain FILTERCHAIN = new ApplicationFilterChain();
	private final static FilterConfig FILTERCONFIG = new ApplicationFilterConfig();
	private static Boolean DISPACHER = false;
	private static HttpRequest DISPATCHERREQUEST = null;
	private static Integer COUNT = -1;
	private static HttpResponse INNERRESP=null;
	
	private Log LOG = MalenLog.getLog();

	/**
	 * @param innerResp
	 */
	public static void setInnerResp(HttpResponse innerResp) {
		ApplicationFilterChain.INNERRESP = innerResp;
	}



	private FilterConfig getFilterConfig() {
		return FILTERCONFIG;
	}

	/**
	 * @param countt
	 */
	public static void setCount(int countt) {
		synchronized (COUNT) {
			ApplicationFilterChain.COUNT = countt;
		}
	}

	/**
	 * Add Filter
	 * 
	 * @param filter
	 */
	public static void addFilter(Filter filter) {
		synchronized (FILTERLIST) {
			FILTERLIST.add(filter);
		}

	}

	/**
	 * @return dispatcherRequest
	 */
	public static HttpRequest getDispatcherRequest() {
		return DISPATCHERREQUEST;
	}

	/**
	 * @param dispatcherRequest
	 */
	public static void setDispatcherRequest(HttpRequest dispatcherRequest) {
		synchronized (dispatcherRequest) {
			ApplicationFilterChain.DISPATCHERREQUEST = dispatcherRequest;
		}

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {

		synchronized (FILTERLIST) {
			synchronized (DISPACHER) {
				if (DISPACHER) {
					// Check IO is open (for ServletOutputStream)
					if (!ApplicationContainer.getNoIOopen()) {
						// Reset dispatcher
						DISPACHER = false;
						ApplicationContainer.setNoIOopen(true);
						throw new MalenOutputStreamDispatcherException();
					}
					// Create a new HttpResponse ,but that's inner response
					HttpResponse innerResponse = new HttpResponse(response.getOutputStream());
					innerResponse.setRequest(DISPATCHERREQUEST);
					ApplicationFinallyProcess afs = new ApplicationFinallyProcess(innerResponse);
					afs.applicationFinally();
					// Reset dispatcher
					DISPACHER = false;
					// Reset NoIOopen
					ApplicationContainer.setNoIOopen(true);
					return;
				}
			}

			int index = ++COUNT;
			if (index == FILTERLIST.size()) {
				COUNT = -1;
				// the last filter processed,now ,the finally process is working and
				// view to client
				synchronized (INNERRESP) {
					ConnectionProcess.finalProcess(INNERRESP);
				}
				

				return;
			}

			for (int i = 0; i < FILTERLIST.size(); i++) {
				/*
				 * if ApplicationFinallyProcess.applicationFinally() is over ,
				 * start callback
				 */
				if (COUNT == -1)
					return;

				Filter filter = FILTERLIST.get(index);

				processFilterInit(filter, getFilterConfig());

				filter.doFilter(request, response, this);

				processFilterDestroy(filter);

			}
		}

	}

	private void processFilterDestroy(Filter filter) {

		Method mDestory;
		try {
			mDestory = filter.getClass().getDeclaredMethod("destroy");
			mDestory.invoke(filter);
		} catch (NoSuchMethodException e) {
			LOG.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (SecurityException e) {
			LOG.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOG.error(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			LOG.error(e.getMessage(), e);
		}

	}

	private void processFilterInit(Filter filter, FilterConfig filterConfig) {

		try {
			Method mInit = filter.getClass().getDeclaredMethod("init", FilterConfig.class);
			mInit.invoke(filter, filterConfig);
		} catch (NoSuchMethodException e) {
			LOG.error(e.getMessage(), e);
		} catch (SecurityException e) {
			LOG.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOG.error(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			LOG.error(e.getMessage(), e);
		}

	}

	/**
	 * @param forword
	 */
	public static void setDispacher(boolean forword) {
		synchronized (DISPACHER) {
			ApplicationFilterChain.DISPACHER = forword;
		}
	}

	/**
	 * @return filterChain
	 */
	public static ApplicationFilterChain getFilterChain() {
		synchronized (FILTERCHAIN) {
			return FILTERCHAIN;
		}

	}

	/**
	 * @return filterList
	 */
	public static List<Filter> getFilterlist() {
		synchronized (FILTERLIST) {
			return FILTERLIST;
		}
	}

}
