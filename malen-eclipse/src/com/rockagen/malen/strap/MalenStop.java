package com.rockagen.malen.strap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import org.apache.commons.logging.Log;

import com.rockagen.malen.exception.MalenServerException;
import com.rockagen.malen.properties.ServiceConfig;
import com.rockagen.malen.utils.MalenLog;

/**
 * <b>Server stop class</b>
 * <p>
 * Listing remote shutdown port
 * </p>
 * <br>
 * 
 * @author AGEN
 * 
 */
public class MalenStop extends Thread {

	private static ServerSocket SHUTDOWNSOCKET = null;
	private final static int SHUTDOWNPORT = Integer.parseInt(ServiceConfig.getString("Remote-port"));
	private final static String SHUTDOWNLINE = ServiceConfig.getString("Remote-shutdown");

	private static Log LOG = MalenLog.getLog();

	/**
	 * <p>
	 * initialize MalenStop
	 * </p>
	 * <br>
	 * 
	 */
	protected static void stopServices() {
		if (SHUTDOWNPORT == 0) {
			LOG.error("Service error!");
			System.exit(1);
		}
		try {

			SHUTDOWNSOCKET = new ServerSocket(SHUTDOWNPORT);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			System.exit(1);
		}
	}

	@Override
	public void run() {
		Socket shutdown_socket = null;
		while (true) {
			try {
				shutdown_socket = SHUTDOWNSOCKET.accept();

				String remote = new BufferedReader(new InputStreamReader(shutdown_socket.getInputStream())).readLine();
				if (SHUTDOWNLINE.equals(remote)) {
					shutdown_socket.close();
					LOG.warn("remote socket connection...");
					LOG.warn("Sever will shut down in 3 second...");
					try {
						Thread.sleep(1000);
						LOG.warn("Sever will shut down in 2 second...");
						Thread.sleep(1000);
						LOG.warn("Sever will shut down in 1 second...");
						BootStrap.setSHUTDOWN(true);
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						throw new MalenServerException(e.getMessage());
					}
					LOG.warn("Sever destroy...");
					System.exit(0);
				} else {
					LOG.warn(new Date() + " remote receive " + "\"" + remote + "\"");
					shutdown_socket.close();
				}
			} catch (IOException e) {
				throw new MalenServerException("\"" + e.getMessage() + "\"");
			} catch (MalenServerException e) {
				LOG.error(e.getMessage(), e);
			}
		}

	}

}
