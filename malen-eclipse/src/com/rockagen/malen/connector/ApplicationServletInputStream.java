package com.rockagen.malen.connector;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;

/**
 * <b>ServletInputStream override</b><br>
 * 
 * @author AGEN
 * 
 */
public class ApplicationServletInputStream extends ServletInputStream {

	private BufferedInputStream bis;

	public ApplicationServletInputStream(InputStream input) {

		bis = new BufferedInputStream(input);

	}

	@Override
	public int read() throws IOException {
		return bis.read();
	}

}
