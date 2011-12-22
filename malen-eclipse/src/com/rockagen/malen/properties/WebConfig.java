package com.rockagen.malen.properties;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.rockagen.malen.filter.FilterMatcher;
import com.rockagen.malen.utils.MalenLog;

/**
 * <b>Load context web.xml Configuration</b>
 * <p>
 * This class have five map.
 * </p>
 * <b> <li>HASHMAP</li> <li>SERVLETMAP</li><li>NAME_VALUE</li><li>CONTEXT</li>
 * <li>
 * ALLCONTEXT</li><br>
 * <br>
 * </b>
 * <ul>
 * <i>HASHMAP</i> puts Context/WEB-INF/web.xml configuration information
 * </ul>
 * <br>
 * <ul>
 * <i>SERVLETMAP</i> puts servlet configuration information
 * </ul>
 * <br>
 * <ul>
 * <i>NAME_VALUE</i> puts some name-value information
 * </ul>
 * <br>
 * <ul>
 * <i>CONTEXT </i> puts Context name information
 * </ul>
 * <br>
 * <ul>
 * <i>ALLCONTEXT </i> puts Context information
 * </ul>
 * <br>
 * 
 * @author AGEN
 * 
 */
public class WebConfig {
	private static final Map<String, String> HASHMAP = new HashMap<String, String>();
	private static final Map<String, String> SERVLETMAP = new HashMap<String, String>();
	private static final Map<String, String> NAME_VALUE = new HashMap<String, String>();
	private static final Map<String, String> CONTEXT = new HashMap<String, String>();
	private static final List<String> ALLCONTEXT = new ArrayList<String>();

	private static Log LOG = MalenLog.getLog();

	/**
	 * <p>
	 * This Method initial webapps/.../WEB-INF/web.xml
	 * </p>
	 */
	public static void init() {
		File appBase = new File(ServiceConfig.getRealPath() + ServiceConfig.getString("Host-appBase"));
		String[] listFile = appBase.list();

		for (String context : listFile) {
			CONTEXT.put(context, context);
			File file = new File(appBase + File.separator + context + File.separator + "WEB-INF" + File.separator
					+ "web.xml");
			LOG.info("Deploying web application directory " + context);

			if (!file.exists())
				continue;
			LOG.info("loding... " + context + File.separator + "WEB-INF" + File.separator + "web.xml");

			resolveXml(file, context);

			FilterIniConfig.resolveXmlFilter(file, context);
		}
		FilterCollection.deployFilter();
		// The last initialize (init filterMatcher properties)
		FilterMatcher.loadProperties();

	}

	/**
	 * ResolveXml config inner method
	 * 
	 * @param file
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public static void resolveXml(File file, String context) {

		/*
		 * nameVale is servlet mapping info
		 * 
		 * like this: ttt,test.ServletTest_2,ttt,/servlet/test2...
		 */

		List<String> servletNameValue = new ArrayList<String>();

		SAXReader reader = new SAXReader();
		Document doc;
		try {
			doc = reader.read(file);
			Element root = doc.getRootElement();
			List<Element> list = root.elements();
			for (int i = 0; i < list.size(); i++) {
				Element element = list.get(i);
				List<Element> attrs = element.elements();
				int count = 0;
				for (int j = 0; j < attrs.size(); j++) {
					Element attr = attrs.get(j);

					/*
					 * this get weclome-file
					 * 
					 * like this:
					 * 
					 * " ROOT_welcome-file-list_welcome-file_0-----index.html "
					 * 
					 * " J2EE_welcome-file-list_welcome-file_0-----index.html "
					 */
					if ((context.toString() + "_" + element.getName() + "_" + attr.getName()).contains("welcome-file")) {
						HASHMAP.put(context.toString() + "_" + element.getName() + "" + "_" + attr.getName() + "_"
								+ count, attr.getTextTrim());
						count++;
						continue;
					}

					// check exist servlet
					/*
					 * if (!StringUtils.isBlank(meanning.get(context.toString()
					 * + "_" + element.getName() + "" + "_" + attr.getName() +
					 * "_" + attr.getTextTrim()))) { throw new
					 * Exception("A servlet class has exist !!"); }
					 */

					/*
					 * this get some mapping
					 * 
					 * like this:
					 * 
					 * J2EE_servlet_servlet-name_ttt ----- ttt
					 * J2EE_servlet_servlet-class_test.ServletTest_2 -----
					 * test.ServletTest_2
					 * J2EE_servlet-mapping_servlet-name_ttt----- ttt
					 * J2EE_servlet-mapping_url-pattern_/servlet/test2 -----
					 * /servlet/test2
					 */
					HASHMAP.put(
							context.toString() + "_" + element.getName() + "" + "_" + attr.getName() + "_"
									+ attr.getTextTrim(), attr.getTextTrim());
					// this add like this: "ttt" "test.ServletTest_2"
					servletNameValue.add(attr.getTextTrim());
				}
			}
		} catch (DocumentException e) {
			LOG.error(e.getMessage(), e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		// set SERVLETMAP eg: url-->class
		addServletMap(context, servletNameValue);

	}

	/**
	 * Set SERVLETMAP eg: url-->class
	 * 
	 * @param context
	 * @param valueName
	 */
	public static void addServletMap(String context, List<String> valueName) {

		// filter
		for (Map.Entry<String, String> entry : HASHMAP.entrySet()) {
			String temp_key = entry.getKey();
			if (temp_key.contains("_servlet")) {
				String index1 = "";
				String index2 = "";
				// this for name-->class or name -->url
				/*
				 * for example: HASHMAP key is "context_elemenet_attr_value" we
				 * get the value ,then make the NAME_VALUE is
				 * "servlrt: context_s_servlet-name ---> servlet-class"
				 * "maping: context_m_servlet-name--->url-pattern"
				 */
				for (int i = 0; i < valueName.size(); i++) {

					if (!StringUtils.isBlank(HASHMAP.get(context.toString() + "_servlet_servlet-name_"
							+ valueName.get(i)))) {
						/*
						 * index1 like this : ROOT_s_ttt J2EE_s_ttt
						 */
						index1 = context.toString() + "_s_"
								+ HASHMAP.get(context.toString() + "_servlet_servlet-name_" + valueName.get(i));

						if (StringUtils.isBlank(NAME_VALUE.get(index1))) {
							// filter some null object

							if (!StringUtils.isBlank(HASHMAP.get(context.toString() + "_servlet_servlet-class_"
									+ valueName.get(i + 1)))) {
								/*
								 * This NAME_VALUE like:
								 * 
								 * ROOT_s_ttt----test.ServletTest_1
								 * 
								 * J2EE_s_ttt----test.ServletTest_2
								 */
								NAME_VALUE.put(
										index1,
										HASHMAP.get(context.toString() + "_servlet_servlet-class_"
												+ valueName.get(i + 1)));
							}
						}
					}

					if (!StringUtils.isBlank(HASHMAP.get(context.toString() + "_servlet-mapping_servlet-name_"
							+ valueName.get(i)))) {
						/*
						 * index2 like this: ROOT_m_ttt J2EE_m_ttt
						 */
						index2 = context.toString() + "_m_"
								+ HASHMAP.get(context.toString() + "_servlet-mapping_servlet-name_" + valueName.get(i));

						if (StringUtils.isBlank(NAME_VALUE.get(index2))) {
							// filter some null object

							if (!StringUtils.isBlank(HASHMAP.get(context.toString() + "_servlet-mapping_url-pattern_"
									+ valueName.get(i + 1)))) {

								/*
								 * this NAME_VALUE like :
								 * 
								 * ROOT_m_ttt---/servlet/test1
								 * 
								 * J2EE_m_ttt---/servlet/test2
								 */
								NAME_VALUE.put(
										index2,
										HASHMAP.get(context.toString() + "_servlet-mapping_url-pattern_"
												+ valueName.get(i + 1)));
							}
						}

					}

				}
				/*
				 * servletName : context/url-parten ---> servlet class
				 * 
				 * just like this:
				 * 
				 * ROOT/servlet/test1-------test.ServletTest_1
				 * 
				 * J2EE/servlet/test2-------test.ServletTest_2
				 */
				for (Map.Entry<String, String> result : NAME_VALUE.entrySet()) {
					String index = result.getKey().substring(0, result.getKey().indexOf("_"));

					// if context has handle ,continue
					if (ALLCONTEXT.contains(index))
						continue;

					String temp1 = result.getKey();
					if (temp1.contains("_s_")) {
						String temp2 = temp1.replace("_s_", "_m_");
						SERVLETMAP.put(context.toString() + NAME_VALUE.get(temp2), NAME_VALUE.get(temp1));
					}
				}
				// context count
				ALLCONTEXT.add(context.toString());
			}

		}

	}

	/**
	 * 
	 * The arg like : "ROOT/servlet/test1" "J2EE/servlet/test2"
	 * 
	 * @param key
	 * @return SERVLETMAP value
	 * @throws IOException
	 */
	public static String getServlet(String key) throws IOException {

		if (!StringUtils.isBlank(key)) {
			return SERVLETMAP.get(key);
		}
		return "";
	}

	/**
	 * This Method can get all configuration info by key
	 * 
	 * @param key
	 * @return HASHMAP value
	 */
	public static String getString(String key) {
		if (!StringUtils.isBlank(key)) {
			return HASHMAP.get(key);
		}
		return "";
	}

	/**
	 * This Method is no meaning,just for some method invoke
	 * 
	 * GET context name
	 * 
	 * @param key
	 * @return Context value
	 */
	public static String getContext(String key) {
		if (!StringUtils.isBlank(key)) {
			return CONTEXT.get(key);
		}
		return "";
	}

}
