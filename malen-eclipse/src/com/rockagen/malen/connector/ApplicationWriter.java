package com.rockagen.malen.connector;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * <b>Override PrintWriter </b>
 * <p>
 * execute {@link HttpResponse # sendHeaders() } Before first call
 * println(),printf(),format()
 * </p>
 * 
 * @author AGEN
 * 
 */
public class ApplicationWriter extends PrintWriter {

	private HttpResponse response;
	private int count = -1;

	/**
	 * @param response
	 */
	public void setResponse(HttpResponse response) {
		this.response = response;
	}

	public ApplicationWriter(OutputStreamWriter outputStreamWriter, boolean flush) {
		super(outputStreamWriter, flush);
	}

	@Override
	public void println() {
		count++;
		if (count == 0) {
			response.setContentLength(0);
			response.sendHeaders();
		}
		super.println();

	}

	@Override
	public void println(boolean x) {
		count++;
		if (count == 0) {
			response.setContentLength(1);
			response.sendHeaders();
		}
		super.println(x);

	}

	@Override
	public void println(char x) {
		count++;
		if (count == 0) {
			response.setContentLength(1);
			response.sendHeaders();
		}
		super.println(x);

	}

	@Override
	public void println(String x) {
		count++;
		if (count == 0) {
			response.setContentLength(x.length());
			response.sendHeaders();
		}
		super.println(x);

	}

	@Override
	public void println(char[] x) {
		count++;
		if (count == 0) {
			response.setContentLength(x.length);
			response.sendHeaders();
		}
		super.println(x);

	}

	@Override
	public void println(float x) {
		count++;
		if (count == 0) {
			response.setContentLength(1);
			response.sendHeaders();
		}
		super.println(x);

	}

	@Override
	public void println(double x) {
		count++;
		if (count == 0) {
			response.setContentLength(1);
			response.sendHeaders();
		}
		super.println(x);

	}

	@Override
	public void println(long x) {
		count++;
		if (count == 0) {
			response.setContentLength(1);
			response.sendHeaders();
		}
		super.println(x);

	}

	@Override
	public void println(int x) {
		count++;
		if (count == 0) {
			response.setContentLength(1);
			response.sendHeaders();
		}
		super.println(x);

	}

	@Override
	public PrintWriter printf(Locale l, String format, Object... args) {
		count++;
		if (count == 0) {
			response.setContentLength(format.length());
			response.sendHeaders();
		}
		return super.printf(l, format, args);
	}

	@Override
	public PrintWriter printf(String format, Object... args) {
		count++;
		if (count == 0) {
			response.setContentLength(format.length());
			response.sendHeaders();
		}
		return super.printf(format, args);

	}

	@Override
	public void println(Object x) {
		count++;
		if (count == 0) {
			response.setContentLength(1);
			response.sendHeaders();
		}
		super.println(x);

	}

	@Override
	public PrintWriter format(Locale l, String format, Object... args) {
		count++;
		if (count == 0) {
			response.setContentLength(format.length());
			response.sendHeaders();
		}
		return super.format(l, format, args);

	}

	@Override
	public PrintWriter format(String format, Object... args) {
		count++;
		if (count == 0) {
			response.setContentLength(format.length());
			response.sendHeaders();
		}
		return super.format(format, args);
	}

}
