package com.rockagen.malen.core;

import java.io.Serializable;
import java.util.List;

import com.rockagen.malen.properties.FilterIniConfig;

/**
 * 
 * <b>some Serialize value</b> <br>
 * <br>
 * <li>FILTERCLASS</li> <li>FILTERMAPPINGURL</li> <li>NOIOOPEN</li> <li>
 * FORWARDURI</li><br>
 * 
 * @author AGEN
 * 
 */
public class ApplicationContainer implements Serializable {

	private static final long serialVersionUID = 3793937790377016582L;

	private static List<String> FILTERCLASS = FilterIniConfig.getFilterNameClass();
	private static List<String> FILTERMAPPINGURL = FilterIniConfig.getFilterMapingUrl();
	private static Boolean NOIOOPEN = true;
	private static String FORWARDURI = "";

	/**
	 * @return true , false
	 */
	public static Boolean getNoIOopen() {
		synchronized (NOIOOPEN) {
			return NOIOOPEN;
		}

	}

	/**
	 * @param noIOopen
	 */
	public static void setNoIOopen(Boolean noIOopen) {
		ApplicationContainer.NOIOOPEN = noIOopen;
	}

	/**
	 * @return FILTERCLASS
	 */
	public static List<String> getFILTERCLASS() {

		synchronized (FILTERCLASS) {
			return FILTERCLASS;
		}
	}

	/**
	 * @param filter
	 */
	public static void addFILTERCLASS(String filter) {

		FILTERCLASS.add(filter);

	}

	/**
	 * @return FILTERMAPPINGURL
	 */
	public static List<String> getFILTERMAPPINGURL() {

		synchronized (FILTERMAPPINGURL) {
			return FILTERMAPPINGURL;
		}
	}

	/**
	 * @param filter
	 */
	public static void addFILTERMAPPINGURL(String filter) {

		FILTERMAPPINGURL.add(filter);

	}

	/**
	 * @return FORWARDURI
	 */
	public static String getFORWARDURI() {
		synchronized (FORWARDURI) {
			return FORWARDURI;
		}
	}

	/**
	 * @param fORWARDURI
	 */
	public static void setFORWARDURI(String fORWARDURI) {

		FORWARDURI = fORWARDURI;

	}

}
