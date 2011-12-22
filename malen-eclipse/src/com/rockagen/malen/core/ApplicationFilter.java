package com.rockagen.malen.core;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;

import com.rockagen.malen.exception.MalenFilterException;
import com.rockagen.malen.filter.ApplicationFilterChain;
import com.rockagen.malen.properties.ServiceConfig;
import com.rockagen.malen.utils.MalenLog;

/**
 * 
 * <b> Filter process class</b><br>
 * 
 * @author AGEN
 * 
 */
public class ApplicationFilter implements Serializable {

	private static final long serialVersionUID = -8937470741245184761L;
	private static List<String> filterClass = ApplicationContainer.getFILTERCLASS();
	private static FilterChain filterChain = FilterContainer.getFilterChain();
	private static FilterConfig filterConfig = FilterContainer.getFilterConfig();

	private static Log LOG = MalenLog.getLog();

	/**
	 * <p>
	 * Filter process entry point
	 * </p>
	 * <br>
	 */
	public static void addFilter() {
		for (String str : filterClass) {
			// J2EE_com.rockagen.filter.xxx--->com.rockagen.filter.xxx
			String className = str.substring(str.indexOf("_") + 1);
			String filterContext = str.substring(0, str.indexOf("_"));
			// String filterContext=context.replaceAll("/", "");
			if (!checkFilter(filterContext, className))
				continue;
			try {
				loadFilter(filterContext, className);

				// handle Filter
				// processFilter(filter, request, response);
			} catch (ClassNotFoundException e) {
				throw new MalenFilterException("\"" + e.getMessage() + "\"");
			} catch (InstantiationException e) {
				throw new MalenFilterException("\"" + e.getMessage() + "\"");
			} catch (IllegalAccessException e) {
				throw new MalenFilterException("\"" + e.getMessage() + "\"");
			} catch (IOException e) {
				throw new MalenFilterException("\"" + e.getMessage() + "\"");
			} catch (MalenFilterException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 
	 * Load Filter and instance
	 * 
	 * @param context
	 * @param filterName
	 * @return filter
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private static void loadFilter(String context, String filterName) throws IOException, ClassNotFoundException,
			InstantiationException, IllegalAccessException {

		// String filterName = filter;
		URLClassLoader loader = null;

		// create a URLClassLoader
		URL[] urls = new URL[1];
		URLStreamHandler streamHandler = null;

		// classpath
		File classPath = new File(ServiceConfig.getRealPath() + ServiceConfig.getString("Host-appBase")
				+ File.separator + context + File.separator + "WEB-INF" + File.separator + "classes");

		String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();

		/*
		 * this streamHandletr role is diff URL Constructor method
		 * 
		 * URL(String protocol, String host, String file)
		 * 
		 * URL(URL context, String spec, URLStreamHandler handler)
		 * 
		 * we need the second URL(URL context, String spec, URLStreamHandler
		 * handler)
		 * 
		 * so we add a streamHandler params
		 */

		urls[0] = new URL(null, repository, streamHandler);

		// load...
		loader = new URLClassLoader(urls);

		Class<?> clazz = null;

		String temp = filterName.replace(".", "/");
		File isExist = new File((repository + temp + ".class").substring(5));
		if (!isExist.exists()) {
			throw new ClassNotFoundException("filterClass is not exist !");
		}
		LOG.info("deploy " + context + " Filter  " + filterName);
		clazz = loader.loadClass(filterName);

		Filter filter = (Filter) clazz.newInstance();

		// Add Filter
		ApplicationFilterChain.addFilter(filter);

	}

	/**
	 * Reflect invoke method
	 * 
	 * @param filter
	 * @param request
	 * @param response
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void processFilter(Filter filter, ServletRequest request, ServletResponse response)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {

		Method mInit, mDoFilter, mDestory;

		// init filter
		mInit = filter.getClass().getDeclaredMethod("init", FilterConfig.class);
		mInit.invoke(filter, filterConfig);

		// process doFilter
		mDoFilter = filter.getClass().getDeclaredMethod("doFilter", ServletRequest.class, ServletResponse.class,
				FilterChain.class);

		mDoFilter.invoke(filter, request, response, filterChain);

		// process destory
		mDestory = filter.getClass().getDeclaredMethod("destroy");

		mDestory.invoke(filter);
		
	}
	
	public static void filterInit(Filter filter){
		
	}

	/**
	 * check filter is valid ?<br>
	 * 
	 * @param filterContext
	 * @param className
	 * @return true of false
	 */
	private static boolean checkFilter(String filterContext, String className) {

		boolean flag = filterClass.contains(filterContext + "_" + className);

		return flag;

	}
}
