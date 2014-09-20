package au.csiro.gsnlite;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import au.csiro.gsnlite.utils.Logger;
import au.csiro.gsnlite.utils.ValidityTools;
import au.csiro.gsnlite.vsensor.VSensorLoader;

public class GSNController extends Thread {

	private ServerSocket mySocket;

	private static int gsnControllerPort;

	private static final int GSN_CONTROL_READ_TIMEOUT = 20000;

	public static final String GSN_CONTROL_SHUTDOWN = "GSN STOP";

	public static final String GSN_CONTROL_LIST_LOADED_VSENSORS = "LIST LOADED VSENSORS";

	private static transient Logger logger             = Logger.getInstance();
	private static String TAG = "GSNController.class";

	private VSensorLoader vsLoader;

	public GSNController(VSensorLoader vsLoader, int gsnControllerPort) throws UnknownHostException, IOException {
		this.vsLoader = vsLoader;
		this.gsnControllerPort = gsnControllerPort ;
		mySocket = new ServerSocket(gsnControllerPort);
		//mySocket = new ServerSocket(gsnControllerPort, 0, InetAddress.getByName("localhost"));
		this.start();
	}

	public void run() {
		logger.info(TAG,"Started GSN Controller on port " + gsnControllerPort);
		while (true) {
			try {
				Socket socket = mySocket.accept();
				if (logger.isDebugEnabled())
					logger.debug (TAG,"Opened connection on control socket.");
				socket.setSoTimeout(GSN_CONTROL_READ_TIMEOUT);

				// Only connections from localhost are allowed
//				if (ValidityTools.isLocalhost(socket.getInetAddress().getHostAddress()) == false) {
//					try {
//						logger.warn(TAG"Connection request from IP address >" + socket.getInetAddress().getHostAddress() + "< was denied.");
//						socket.close();
//					} catch (IOException ioe) {
//						// do nothing
//					}
//					continue;
//				}
				new StopManager().start();
			} catch (SocketTimeoutException e) {
				if (logger.isDebugEnabled())
					logger.debug (TAG,"Connection timed out. Message was: " + e.getMessage());
			} catch (IOException e) {
				logger.warn(TAG,"Error while accepting control connection: " + e.getMessage());
			}
		}
	}

	/*
	 * This method must be called after virtual sensors initialization. It
	 * allows GSNController to shut down properly all the virtual sensors in
	 * use.
	 */
	public void setLoader(VSensorLoader vsLoader) {
		if (this.vsLoader == null) // override protection
			this.vsLoader = vsLoader;
	}

	private class StopManager extends Thread {

		public void run() {

//			new Thread(new Runnable() {

//				public void run() {
//					try {
//						Thread.sleep(7000);
//					} catch (InterruptedException e) {
//
//					}finally {
//						logger.warn(TAG"Forced exit...");
//						System.exit(1);
//					}
//				}}).start();

			try {
				// We  stop  GSN  here
				logger.warn(TAG,"Shutting down GSN...");
				
				

				if (vsLoader != null) {
					vsLoader.stopLoading();
					logger.warn(TAG,"All virtual sensors have been stopped, shutting down virtual machine.");				
				} else {
					logger.warn(TAG,"Could not shut down virtual sensors properly. We are probably exiting GSN before it has been completely initialized.");
				}
				
				
			} catch (Exception e) {
				logger.warn(TAG,"Error while reading from or writing to control connection: " + e.getMessage(), e);
			}finally {
										  
				System.exit(0);
			    
			}
		}
	}
}
