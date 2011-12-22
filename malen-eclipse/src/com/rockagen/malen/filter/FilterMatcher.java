package com.rockagen.malen.filter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import com.rockagen.malen.connector.ConnectionProcess;
import com.rockagen.malen.properties.FilterIniConfig;
import com.rockagen.malen.utils.MalenLog;

/**
 * <b>FilterMatcher class</b>
 * <p>
 * process {@link ConnectionProcess#process(InputStream, java.io.OutputStream)}
 * matcher filter
 * </p>
 * 
 * <p>
 * The key entry point is <code>urlFilterMachers(String url) </code>
 * 
 * @author AGEN
 * 
 */
public class FilterMatcher {

	private static Set<String> URLPATTERNS = FilterIniConfig.getFilterUrlPattern();
	private static Properties P;

	private static Log LOG = MalenLog.getLog();

	/**
	 * check this url is need filter <br>
	 * <p>
	 * if return true, need filter
	 * </p>
	 * 
	 * @param url
	 * @return is need filter
	 */
	public static boolean urlFilterMachers(String url) {
		if (noNeedFilter(url))
			return false;
		for (String urlpattern : URLPATTERNS) {
			String regex = urlpattern;
			if (regex.contains("*")) {
				regex = StringUtils.replace(regex, "*", ".*", -1);
			}

			Pattern pattern = Pattern.compile("^" + regex + "$");
			Matcher matcher = pattern.matcher(url);
			boolean flag = matcher.matches();
			return flag;

		}

		return false;
	}

	/**
	 * check this is some no need filter ?
	 * 
	 * @param url
	 * @return true false
	 */
	private static boolean noNeedFilter(String url) {
		String mime = url.substring(url.lastIndexOf(".") + 1);
		boolean flag = P.containsValue(mime);
		return flag;
	}

	/**
	 * 
	 * Load mime properties<br>
	 * <p>
	 * because some things not need filter
	 * </p>
	 * The conf/filtermime.properties is some not need filter fileSuffix data<br>
	 * 
	 */
	public static void loadProperties() {
		InputStream is;
		try {
			is = new BufferedInputStream(new FileInputStream(filterMimeFilePath()));
			P = new Properties();
			P.load(is);
		} catch (FileNotFoundException e) {

			LOG.error(e.getMessage(), e);
		} catch (IOException e) {

			LOG.error(e.getMessage(), e);
		}

	}
	private static String filterMimeFilePath(){
		
		// This is for bootstrap.jar
/*		String currentPath=System.getProperty("user.dir");
		String filtermimePath=StringUtils.substringBeforeLast(currentPath, File.separator)+File.separator+"conf"+File.separator;
		File file=new File(filtermimePath+"filtermime.properties");*/
		
		// For eclipse
		String currentPath=System.getProperty("user.dir");
		File file=new File(currentPath+File.separator+"conf"+File.separator+"filtermime.properties");
		
		return file.getAbsolutePath();
	}
}
