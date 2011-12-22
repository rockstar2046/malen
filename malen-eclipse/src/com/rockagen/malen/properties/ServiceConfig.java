package com.rockagen.malen.properties;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.rockagen.malen.utils.MalenLog;

/**
 * 
 * <b> Load Service Configuration</b>
 * <p>
 * This class have two map.
 * </p>
 * <b> <li>SERVERMAP</li> <li>contentType</li><br>
 * <br>
 * </b>
 * <ul>
 * <i>SERVERMAP</i> puts conf/server.xml configuration information
 * <p>
 * they like this:<br>
 * <br>
 * <ol>
 * Connector-port------>8080
 * </ol>
 * </p>
 * <ol>
 * Host-name------>localhost
 * </ol>
 * </ul>
 * <br>
 * <ul>
 * <i>CONTENTTYPE</i> puts conf/web.xml configuration information
 * </ul>
 * <br>
 * 
 * @author AGEN
 * 
 */
public class ServiceConfig {
	private static final Map<String, String> SERVERMAP = new HashMap<String, String>();
	private static final Map<String, String> CONTENTTYPE = new HashMap<String, String>();

	private static Log LOG = MalenLog.getLog();

	public static void init() {
		initServer();
		initWeb();
	}

	/**
	 * <p>
	 * This Method is initial conf/web.xml data
	 * </P>
	 * 
	 * The major just for get contentType
	 * 
	 */
	public static void initWeb() {
		File file = new File(getRealPath() + "conf/mime.xml");
		SAXReader reader = new SAXReader();
		Document doc;
		try {
			doc = reader.read(file);
			Element root = doc.getRootElement();
			@SuppressWarnings("unchecked")
			Iterator<Element> iter = root.elementIterator();
			while (iter.hasNext()) {
				Element element = (Element) iter.next();
				String extension = element.elementTextTrim("extension");
				String mime_type = element.elementTextTrim("mime-type");
				if (StringUtils.isBlank(extension) || StringUtils.isBlank(mime_type)) {
					throw new DocumentException("At: \"conf/mime.xml\" has a error!");
				}
				CONTENTTYPE.put(extension, mime_type);
			}
		} catch (DocumentException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * <p>
	 * This Method is initial conf/server.xml data
	 * </p>
	 * 
	 * initial some server bootstrap data
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static void initServer() {

		File file = new File(getRealPath() + "conf/server.xml");
		SAXReader reader = new SAXReader();
		Document doc;
		try {
			doc = reader.read(file);
			Element root = doc.getRootElement();
			List<Element> list = root.elements();
			for (int i = 0; i < list.size(); i++) {
				Element element = list.get(i);
				List<Attribute> attrs = element.attributes();
				for (int j = 0; j < attrs.size(); j++) {
					Attribute attr = attrs.get(j);
					SERVERMAP.put(element.getName() + "-" + attr.getName(), attr.getValue());
				}
			}
		} catch (DocumentException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * Get ContentTypeMap object
	 * 
	 * @return contentType
	 */
	public static Map<String, String> getContentTypeMap() {
		return CONTENTTYPE;
	}

	/**
	 * 
	 * Get server.xml configuration information
	 * 
	 * @param key
	 * @return serverMap value
	 */
	public static String getString(String key) {
		if (SERVERMAP.get(key) != null) {
			return SERVERMAP.get(key);
		}
		return "";

	}

	/**
	 * This get malen path
	 * 
	 * @return realPath
	 */
	public static String getRealPath() {
		File path = new File(System.getProperty("user.dir"));
		return path.toString() + File.separator;
		//return path.getParent() + File.separator;
	}

}
