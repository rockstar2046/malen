package com.rockagen.malen.core;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rockagen.malen.properties.FilterIniConfig;

/**
 * <b>MalenFilterEnum</b> <br>
 * <p>
 * Just for ApplicationFilterConfig class
 * </p>
 * <br>
 * 
 * @author AGEN
 * 
 */
public class MalenFilterEnum implements Enumeration<String> {

	private List<String> list = new ArrayList<String>();
	private int index = 0;

	public MalenFilterEnum() {

		Map<String, String> tempMap = FilterIniConfig.getParammap();
		Set<String> set = tempMap.keySet();
		for (String str : set) {
			list.add(str);
		}
	}

	/** YES
	 * @see java.util.Enumeration#hasMoreElements()
	 */
	@Override
	public boolean hasMoreElements() {
		int count = list.size() - index;
		if (count == 0)
			return false;
		return true;
	}

	/** YES
	 * @see java.util.Enumeration#nextElement()
	 */
	@Override
	public String nextElement() {
		String next = list.get(index);
		index++;
		return next;
	}

}
