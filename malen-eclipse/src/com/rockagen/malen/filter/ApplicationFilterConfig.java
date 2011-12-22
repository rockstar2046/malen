package com.rockagen.malen.filter;

import java.io.Serializable;
import java.util.Enumeration;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;

import com.rockagen.malen.core.MalenFilterEnum;
import com.rockagen.malen.core.MalenServletContext;
import com.rockagen.malen.properties.FilterIniConfig;

/**
 * 
 * <b>FilterConfig class</b>
 * <p>
 * do some filter configuration things
 * </p>
 * <br>
 * 
 * @author AGEN
 * 
 */
public class ApplicationFilterConfig implements FilterConfig, Serializable {

	private static final long serialVersionUID = -8962365896670240000L;

	@Override
	public String getInitParameter(String paramName) {

		if (StringUtils.isBlank(paramName))
			return "";

		String initParameter = FilterIniConfig.getFilterInitParamValue(paramName);

		return initParameter;
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		Enumeration<String> enmu = new MalenFilterEnum();

		return enmu;
	}

	@Override
	public ServletContext getServletContext() {

		ServletContext context = new MalenServletContext();

		return context;
	}

	/*
	 * null implements
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.FilterConfig#getFilterName()
	 */
	@Override
	public String getFilterName() {
		// TODO Auto-generated method stub
		return null;
	}

}
