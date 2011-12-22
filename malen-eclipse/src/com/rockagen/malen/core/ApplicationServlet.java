package com.rockagen.malen.core;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import com.rockagen.malen.connector.HttpRequest;
import com.rockagen.malen.connector.HttpResponse;
import com.rockagen.malen.connector.ServletFind;
import com.rockagen.malen.exception.MalenDispatcherException;
import com.rockagen.malen.exception.MalenOutputStreamDispatcherException;
import com.rockagen.malen.exception.MalenServletException;
import com.rockagen.malen.properties.ServiceConfig;
import com.rockagen.malen.properties.WebConfig;
import com.rockagen.malen.utils.MalenLog;

/**
 * 
 * <b>Servlet processor class</b>
 * 
 * <p>
 * This class Mainly to process servlet request
 * </p>
 * 
 * @author AGEN
 * 
 */
public class ApplicationServlet implements Serializable {

	private static final long serialVersionUID = 3304100642002413570L;

	private HttpServlet servlet = null;

	private static Log LOG = MalenLog.getLog();

	/**
	 * 
	 * Servlet process
	 * 
	 * @param request
	 * @param response
	 */
	public void process(HttpRequest request, HttpResponse response) {

		try {

			if (WebConfig.getServlet(ServletFind.getRequestServlet()) == null) {
				return;
			}
			String servletName = WebConfig.getServlet(ServletFind.getRequestServlet());
			URLClassLoader loader = null;

			// create a URLClassLoader
			URL[] urls = new URL[1];
			URLStreamHandler streamHandler = null;

			// diff ROOT filter "nullROOT/....." we just get "ROOT/..."
			String servletContextTemp = request.getServletContextPath();

			if (StringUtils.isBlank(servletContextTemp))
				servletContextTemp = "";
			// classpath
			File classPath = new File(ServiceConfig.getRealPath() + ServiceConfig.getString("Host-appBase")
					+ File.separator + servletContextTemp + request.getResponsePath() + "WEB-INF" + File.separator
					+ "classes");

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
			try {
				String temp = servletName.replace(".", "/");
				File isExist = new File((repository + temp + ".class").substring(5));
				if (!isExist.exists()) {
					return;
				}
				clazz = loader.loadClass(servletName);

			} catch (ClassNotFoundException e) {
				LOG.error(e.getMessage(), e);

			}

			try {
				servlet = (HttpServlet) clazz.newInstance();

				// Previous Versions is request methods,but this is poor
				// efficiency

				// methods = servlet.getClass().getMethods();
				try {
					// if this servlet override init method
					Method mInit = servlet.getClass().getDeclaredMethod("init", ServletConfig.class);
					Method mGetServletConfig = servlet.getClass().getDeclaredMethod("getServletConfig");
					ServletConfig sc = (ServletConfig) mGetServletConfig.invoke(servlet);
					mInit.invoke(servlet, sc);
					handlerServices(request, response);

				} catch (NoSuchMethodException e) {
					handlerServices(request, response);
				}
				ApplicationContainer.setNoIOopen(true);
			} catch (InstantiationException e) {
				throw new MalenServletException(e.getMessage());

			} catch (IllegalAccessException e) {
				throw new MalenServletException(e.getMessage());
			} catch (MalenDispatcherException e) {
				// do not things ..
			}  catch (SecurityException e) {
				throw new MalenServletException(e.getMessage());
			} catch (IllegalArgumentException e) {
				throw new MalenServletException(e.getMessage());

			} catch (InvocationTargetException e) {


				String messages="null";
				String temp = e.getCause().toString();
				int i=StringUtils.indexOf(temp, "Exception: ");
				//if have message
				if(i!=-1){
					messages=StringUtils.substringAfterLast(temp, "Exception: ");
					temp=temp.substring(0, i+9);
					temp=StringUtils.substringAfterLast(temp, ".");
				}else{
					temp=StringUtils.substringAfterLast(temp, ".");
				}
			
				

				// That's for check ServletOutputStream has been open before
				// call forward method.
				if (temp.equalsIgnoreCase("MalenOutputStreamDispatcherException")) {
					throw new MalenOutputStreamDispatcherException();
				}

				// This is hook ,process RequestDispathcer.forward()
				if (!temp.equalsIgnoreCase("MalenDispatcherException")) {
					throw new Exception(messages);
				}

			}
		} catch (MalenOutputStreamDispatcherException e) {
			String message = e.getMessage();
			if (message == null)
				message = "You Should not call forward before open ServletOutputStream !";
			throw new MalenOutputStreamDispatcherException(" \"" + message + "\"");
		} catch (Exception e) {
			throw new MalenServletException(" \"" + e.getMessage() + "\"");
		}

	}

	/**
	 * This Method handler Servlet
	 * 
	 * <P>
	 * Calling reflect invoke servlet method and handle it
	 * </p>
	 * 
	 * 
	 * @param request
	 * @param response
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws ServletException
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	private void handlerServices(HttpRequest request, HttpResponse response) throws IllegalAccessException,
			SecurityException, ServletException, IOException, IllegalArgumentException, InvocationTargetException {

		boolean isInvokeDoxx = false;
		// (POST,GET) to (post,get)
		String XXX = request.getMethod().toLowerCase();
		// (post,get) to (ost,et)
		String xxx = XXX.substring(1);
		// (post,get) to (P,G)
		String G_P = XXX.substring(0, 1).toUpperCase();
		// doGet , doPost
		String doXXX = "do" + G_P + xxx;

		Method mDoxxx;
		try {
			mDoxxx = servlet.getClass().getDeclaredMethod(doXXX, HttpServletRequest.class, HttpServletResponse.class);
			isInvokeDoxx = true;
			mDoxxx.invoke(servlet, (HttpServletRequest) request, (HttpServletResponse) response);

		} catch (NoSuchMethodException e) {
			// call servlet.service()
		}

		if (!isInvokeDoxx) {
			servlet.service((HttpServletRequest) request, (HttpServletResponse) response);
		}
		try {
			Method mDestroy;

			// handle destroy method
			mDestroy = servlet.getClass().getDeclaredMethod("destroy");
			mDestroy.invoke(servlet);
			servlet = null;
			return;
		} catch (NoSuchMethodException e) {

		}

	}

}
