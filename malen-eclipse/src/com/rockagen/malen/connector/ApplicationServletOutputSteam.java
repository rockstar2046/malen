package com.rockagen.malen.connector;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

import com.rockagen.malen.core.ApplicationContainer;

/**
 * 
 * <b> ServletOutputStream override</b><br>
 * <p>
 * <b>notice:</b> this OutputServletStream is use BufferedOutputStream
 * implements<br>
 * {@link BufferedOutputStream #write(int)} must be flush after write ,
 * because,at RequestDispatcher ,we will call outputstream,if not flush , the
 * headers and file maybe buffer,so ,we need flush after writer(b)
 * <p>
 * 
 * @author AGEN
 * 
 */
public class ApplicationServletOutputSteam extends ServletOutputStream {

	private BufferedOutputStream bos;
	private HttpResponse response;
	private int count = -1;

	public ApplicationServletOutputSteam(OutputStream out) {
		bos = new BufferedOutputStream(out);

	}

	public void setResponse(HttpResponse response) {
		this.response = response;
	}

	@Override
	public void write(int b) throws IOException {

		if (count > 0) {
			ApplicationContainer.setNoIOopen(false);
		}
		bos.write(b);
		// notice: this bos must be flush , because,at RequestDispatcher ,we
		// will call outputstream,if not flush ,
		// the headers and file wmaybe buffer,so ,we need flush after writer(b)
		bos.flush();

	}

	@Override
	public void print(java.lang.String s) throws java.io.IOException {
		count++;
		if (count == 0) {
			response.setContentLength(s.length());
			response.sendHeaders();
		}
		super.print(s);
	}

	@Override
	public void print(boolean b) throws java.io.IOException {
		count++;
		if (count == 0) {
			response.setContentLength(1);
			response.sendHeaders();
		}
		super.print(b);

	}

	@Override
	public void print(char c) throws java.io.IOException {
		count++;
		if (count == 0) {
			response.setContentLength(1);
			response.sendHeaders();
		}
		super.print(c);

	}

	@Override
	public void print(int i) throws java.io.IOException {
		count++;
		if (count == 0) {
			response.setContentLength(1);
			response.sendHeaders();
		}
		super.print(i);

	}

	@Override
	public void print(long l) throws java.io.IOException {

		count++;
		if (count == 0) {
			response.setContentLength(1);
			response.sendHeaders();
		}
		super.print(l);
	}

	@Override
	public void print(float f) throws java.io.IOException {

		count++;
		if (count == 0) {
			response.setContentLength(1);
			response.sendHeaders();
		}
		super.print(f);
	}

	@Override
	public void print(double d) throws java.io.IOException {
		count++;
		if (count == 0) {
			response.setContentLength(1);
			response.sendHeaders();
		}
		super.print(d);
	}

	@Override
	public void println() throws java.io.IOException {
		count++;
		if (count == 0) {
			response.setContentLength(0);
			response.sendHeaders();
		}
		super.println();
	}

	@Override
	public void println(java.lang.String s) throws java.io.IOException {
		count++;
		if (count == 0) {
			response.setContentLength(s.length());
			response.sendHeaders();
		}
		super.println(s);
	}

	@Override
	public void println(boolean b) throws java.io.IOException {

		count++;
		if (count == 0) {
			response.setContentLength(1);
			response.sendHeaders();
		}
		super.println(b);
	}

	@Override
	public void println(char c) throws java.io.IOException {

		count++;
		if (count == 0) {
			response.setContentLength(1);
			response.sendHeaders();
		}
		super.println(c);
	}

	@Override
	public void println(int i) throws java.io.IOException {
		count++;
		if (count == 0) {
			response.setContentLength(1);
			response.sendHeaders();
		}
		super.println(i);
	}

	@Override
	public void println(long l) throws java.io.IOException {
		count++;
		if (count == 0) {
			response.setContentLength(1);
			response.sendHeaders();
		}
		super.println(l);
	}

	@Override
	public void println(float f) throws java.io.IOException {
		count++;
		if (count == 0) {
			response.setContentLength(1);
			response.sendHeaders();
		}
		super.println(f);
	}

	@Override
	public void println(double d) throws java.io.IOException {

		count++;
		if (count == 0) {
			response.setContentLength(1);
			response.sendHeaders();
		}
		super.println(d);
	}

}
