package com.rockagen.malen.strap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;

import com.rockagen.malen.connector.ConnectionProcess;
import com.rockagen.malen.properties.ServiceConfig;
import com.rockagen.malen.utils.Deamon;
import com.rockagen.malen.utils.MalenLog;
import com.rockagen.malen.utils.MalenPrint;

/**
 * 
 * <b>Server start class</b>
 * <p>
 * Listing server port
 * </p>
 * <br>
 * 
 * @author AGEN
 * 
 */
public class MalenStart extends Thread {

	private final static int PORT = Integer.parseInt(ServiceConfig.getString("Connector-port"));
	private final static int MAXCONNECT = Integer.parseInt(ServiceConfig.getString("Connector-maxConnect"));
	private static ServerSocket SERVERSOCKET;
	private static boolean SHUTDOWN = BootStrap.isSHUTDOWN();
	private boolean DEBUG = Deamon.isDebug();

	private static Log LOG = MalenLog.getLog();

	/**
	 * <p>
	 * initialize MalenStart
	 * </p>
	 * <br>
	 */
	protected static void startServices() {

		if (PORT == 0) {
			LOG.error("Service error!");
			System.exit(1);
		}
		try {
			SERVERSOCKET = new ServerSocket(PORT, MAXCONNECT);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			System.exit(1);
		}
	}

	@Override
	public void run() {
		while (!SHUTDOWN) {
			InputStream input = null;
			OutputStream output = null;

			try {
				Socket socket = SERVERSOCKET.accept();
				if (socket != null)
					// --------daemon print if debug=true -------------
					if (DEBUG) {
						MalenPrint.print("Current Connection:" + socket);

					}

				input = socket.getInputStream();
				output = socket.getOutputStream();
				// process connection ...
				ConnectionProcess.process(input, output);
				socket.close();

			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

}
