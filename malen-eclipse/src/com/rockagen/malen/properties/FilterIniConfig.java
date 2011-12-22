package com.rockagen.malen.properties;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.rockagen.malen.utils.MalenLog;

/**
 * <b>FilterConfig loading</b> <b><li>filterMap</li> <li>filterNameVale</li> <li>
 * filterValue</li> <li>paramMap</li><li>filterList</li><li>filterUrlpatterns</li>
 * <br>
 * <br>
 * </b>
 * <ul>
 * filterMap is mapping filterconfig <br>
 * <p>
 * <br>
 * like this:
 * </p>
 * <br>
 * <li>J2EE_filter-mapping_filter-name_characterEncoding---->characterEncoding</li>
 * <li>J2EE_filter_filter-name_characterEncoding---->characterEncoding</li>
 * </ul>
 * <ul>
 * filterNameValue is mapping real map<br>
 * <p>
 * <br>
 * like this:
 * </p>
 * <br>
 * <li>
 * J2EE_filter_characterEncoding_/*---->J2EE_com.agen
 * .filter.CharacterEncodingFilter</li>
 * 
 * </ul>
 * <br>
 * <ul>
 * paramMap is filled with parameters
 * <p>
 * <br>
 * <li>
 * like : J2EE_encoding---->gb2312</li>
 * </p>
 * <br>
 * </ul>
 * <br>
 * <ul>
 * FilterList is filling with ALL Filter classname <br>
 * <p>
 * <br>
 * <li>like this : J2EE_com.xxxxxxx.filter</li>
 * </p>
 * </ul>
 * <br>
 * <ul>
 * filterUrlpatterns is filling with ALL Filter urlPattern <br>
 * <p>
 * <br>
 * <li>like this : /J2EE_/*</li>
 * </p>
 * </ul>
 * <br>
 * 
 * @author AGEN
 * 
 */
public class FilterIniConfig {

	private static List<String> FILTERVALUE = new ArrayList<String>();
	private final static Map<String, String> FILTERNAMEVALUE = new HashMap<String, String>();
	private final static Map<String, String> FILTERMAP = new HashMap<String, String>();
	private final static Map<String, String> NAMEVALUE = new HashMap<String, String>();
	private final static Map<String, String> PARAMMAP = new HashMap<String, String>();
	private final static List<String> FILTERLIST = new ArrayList<String>();
	private final static Set<String> FILTERURLPATTERNS = new HashSet<String>();

	private static Log LOG = MalenLog.getLog();

	/**
	 * resolve xml if filter is exist ?
	 * 
	 * @param file
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	protected static void resolveXmlFilter(File file, String context) {

		SAXReader reader = new SAXReader();
		Document doc;

		try {
			doc = reader.read(file);
			Element root = doc.getRootElement();
			List<Element> list = root.elements();
			for (int i = 0; i < list.size(); i++) {
				Element element = list.get(i);
				// Filtering useless stuff
				if (!element.getName().startsWith("filter"))
					continue;

				if (element.getName().equals("filter")) {
					List<Element> filterChild = element.elements();
					for (int j = 0; j < filterChild.size(); j++) {
						Element filterChildValue = filterChild.get(j);

						if (filterChildValue.getName().contains("init-param")) {
							addFilterInitParam(context, filterChildValue);
							continue;
						}
						checkBlank(context, filterChildValue);

						String temp = context + "_filter_" + filterChildValue.getName() + "_"
								+ filterChildValue.getTextTrim();
						isExist(temp);
						FILTERMAP.put(temp, filterChildValue.getTextTrim());
						if (j == 0) {
							FILTERVALUE.add("#name#" + filterChildValue.getTextTrim());
							continue;
						}
						FILTERVALUE.add(filterChildValue.getTextTrim());

					}

				}

				if (element.getName().equals("filter-mapping")) {
					List<Element> filterMChild = element.elements();
					for (int j = 0; j < filterMChild.size(); j++) {
						Element filterMChildValue = filterMChild.get(j);

						checkBlank(context, filterMChildValue);

						String temp = context + "_filter-mapping_" + filterMChildValue.getName() + "_"
								+ filterMChildValue.getTextTrim();
						isExist(temp);
						FILTERMAP.put(temp, filterMChildValue.getTextTrim());
						if (j == 0) {
							FILTERVALUE.add("#mname#" + filterMChildValue.getTextTrim());
							continue;
						}
						FILTERVALUE.add(filterMChildValue.getTextTrim());
					}
				}
				// Add Filter Name-Value
				addFilterNameValue(context);
				// Add filter url-pattern
				addFilterUrlPattern();
			}

		} catch (DocumentException e) {
			LOG.error("At : web.xml Syntax Can not be resolved", e);
		}

	}

	/**
	 * Add FilterNameValue<br>
	 * <p>
	 * filterNameValue like :
	 * </p>
	 * <ul>
	 * J2EE_filter_characterEncoding_/*---->J2EE_com.agen.filter.
	 * CharacterEncodingFilter
	 * </ul>
	 * <br>
	 * 
	 * @param context
	 */
	public static void addFilterNameValue(String context) throws DocumentException {

		if (FILTERVALUE.size() > 0) {

			for (String str : FILTERVALUE) {
				if (str.contains("#name#")) {
					int index1 = FILTERVALUE.indexOf(str);
					String filterClass = FILTERVALUE.get(index1 + 1);
					String realName = str.substring(str.lastIndexOf('_') + 1).replaceAll("#name#", "");
					NAMEVALUE.put(context + "_" + str, filterClass);
					String temp = str.replaceFirst("#name#", "#mname#");
					int index2 = FILTERVALUE.indexOf(temp);
					String filterUrl = FILTERVALUE.get(index2 + 1);
					FILTERNAMEVALUE.put(context + "_filter_" + realName + "_" + filterUrl, context + "_" + filterClass);
					NAMEVALUE.put(context + "_" + temp, filterUrl);
					if (FILTERLIST.contains(context + "_" + filterClass))
						continue;
					FILTERLIST.add(context + "_" + filterClass);

				}
				if (str.contains("#initName#")) {
					int index = FILTERVALUE.indexOf(str);
					String initParamValue = FILTERVALUE.get(index + 1);
					PARAMMAP.put(str.replaceAll("#initName#", ""), initParamValue);
				}
			}

		}
	}

	/**
	 * Add filter init params
	 * 
	 * @param filterChildValue
	 * @throws DocumentException
	 */
	@SuppressWarnings("unchecked")
	public static void addFilterInitParam(String context, Element filterChildValue) throws DocumentException {

		List<Element> filterInitChild = filterChildValue.elements();
		for (int k = 0; k < filterInitChild.size(); k++) {

			Element filterInitChildValue = filterInitChild.get(k);

			checkBlank(context, filterInitChildValue);

			String temp = context + "_filter_init-param_" + filterInitChildValue.getName() + "_"
					+ filterInitChildValue.getTextTrim();
			isExist(temp);
			FILTERMAP.put(temp, filterInitChildValue.getTextTrim());

			if ((k & 1) == 0) {
				FILTERVALUE.add("#initName#" + filterInitChildValue.getTextTrim());
				continue;
			}
			FILTERVALUE.add(filterInitChildValue.getTextTrim());
		}

	}

	/**
	 * 
	 * Check filter configuration is right ?
	 * 
	 * @param context
	 * @param elemnet
	 * @throws DocumentException
	 */
	public static void checkBlank(String context, Element elemnet) throws DocumentException {
		if (StringUtils.isBlank(elemnet.getTextTrim())) {
			throw new DocumentException("At: " + context + "  " + elemnet.getName() + " is null !");
		}
	}

	/**
	 * check is exist ?
	 * 
	 * @param str
	 * @throws DocumentException
	 */
	public static void isExist(String str) throws DocumentException {
		boolean flag = FILTERMAP.containsKey(str);
		if (flag) {
			if (!str.contains("filter-mapping_url-pattern"))
				throw new DocumentException(str + " filter has exist !");
			// throw new DocumentException(str.substring(str.lastIndexOf('_')) +
			// " filter has exist !");
		}
	}

	/**
	 * Get filterNameValue<br>
	 * 
	 * @return filterNameValue
	 */
	public static Map<String, String> getFilternamevalue() {

		return FILTERNAMEVALUE;

	}

	/**
	 * Get filter-mapping url-pattern value<br>
	 * 
	 * @param context
	 * @param mapingName
	 * @return filter-mapping value
	 */
	public static String getFilterMValue(String context, String mapingName) {

		if (!StringUtils.isBlank(mapingName) && !StringUtils.isBlank(context)) {

			String index = context + "_#mname#" + mapingName;
			return NAMEVALUE.get(index);
		}

		return "";

	}

	/**
	 * Get filter URl-pattern
	 * 
	 * @return FILTERURLPATTERN
	 */
	public static Set<String> getFilterUrlPattern() {
		return FILTERURLPATTERNS;
	}

	/**
	 * 
	 * Add filter URl-pattern
	 * 
	 */
	public static void addFilterUrlPattern() {

		for (Map.Entry<String, String> entry : FILTERNAMEVALUE.entrySet()) {
			String temp = entry.getKey();
			if (temp.contains("#name#"))
				continue;
			// eg: J2EE_filter_characterEncoding2_/* we need /J2EE/*

			// Get /J2EE
			String context = "/" + temp.substring(0, temp.indexOf("_"));
			// Get /*
			String url = temp.substring(temp.lastIndexOf("_") + 1);
			String filterUrl = context + url;
			FILTERURLPATTERNS.add(filterUrl);

		}
	}

	/**
	 * Get filter class value<br>
	 * 
	 * @param context
	 * @param filterName
	 * @return filter class value
	 */
	public static String getFilterNValue(String context, String filterName) {

		if (!StringUtils.isBlank(filterName) && !StringUtils.isBlank(context)) {
			// J2EE_filter_characterEncoding_/*---->com.agen.filter.xxx
			String index = context + "_#name#" + filterName;
			return NAMEVALUE.get(index);
		}

		return "";

	}

	/**
	 * Get Filter ParamValue<br>
	 * 
	 * @param paramName
	 * @return parameter value
	 */
	public static String getFilterInitParamValue(String paramName) {

		if (!StringUtils.isBlank(paramName)) {

			return PARAMMAP.get(paramName);
		}

		return "";

	}

	/**
	 * 
	 * Get paramMap <br>
	 * 
	 * @return paramMap
	 */
	public static Map<String, String> getParammap() {
		return PARAMMAP;
	}

	/**
	 * Get filterMapingUrl <br>
	 * 
	 * @return filterMapingUrl
	 */
	public static List<String> getFilterMapingUrl() {
		List<String> filterMapingUrl = new ArrayList<String>();

		for (Map.Entry<String, String> entry : FILTERNAMEVALUE.entrySet()) {
			filterMapingUrl.add(entry.getKey());
		}

		return filterMapingUrl;
	}

	/**
	 * Get filterNameClass<br>
	 * 
	 * @return filterNameClass
	 */
	public static List<String> getFilterNameClass() {

		return FILTERLIST;
	}

}
