package com.rockagen.malen.strap;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rockagen.malen.properties.ServiceConfig;
import com.rockagen.malen.properties.WebConfig;
import com.rockagen.malen.utils.MalenPrint;

/**
 * 
 * <b>Start service</b> <br>
 * 
 * @author AGEN
 * 
 */
public class StartService {

	/**
	 * initialize services<br>
	 */
	public static void initServices() {

		Long time = System.currentTimeMillis();
		ServiceConfig.init();
		String protocol = ServiceConfig.getString("Connector-protocol");
		int port = Integer.parseInt(ServiceConfig.getString("Connector-port"));
		MalenPrint.print("server starting..." + "  " + protocol + " port:" + port);
		MalenPrint.print(new Date().toString() + "\n");
		WebConfig.init();

		MalenStart.startServices();

		MalenStop.stopServices();

		MalenPrint.print("Server startup in " + String.valueOf(System.currentTimeMillis() - time) + " ms" + "\r\n");

	}

	/**
	 * bootstrap ..<br>
	 * <p>
	 * do some main work
	 * </p>
	 * 
	 */
	public static void bootstrap() {
		initServices();

		int maxThread = Integer.parseInt(ServiceConfig.getString("Executor-maxThreads"));
		// Shutdown Thread (singleThread)
		new MalenStop().start();
		// Socket Accept Thread
		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i = 0; i < maxThread; i++) {
			exec.execute(new MalenStart());
		}
	}
}
