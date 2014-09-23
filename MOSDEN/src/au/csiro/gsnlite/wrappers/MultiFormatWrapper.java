//package au.csiro.gsnlite.wrappers;
//
//import java.io.Serializable;
//import java.util.HashMap;
//
//import android.os.Bundle;
//import android.os.DeadObjectException;
//import android.os.IBinder;
//import android.os.RemoteException;
//import android.app.Activity;
//import android.app.Application;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.util.Log;
//import android.view.Menu;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.TextView;
//import au.csiro.gsnlite.IFunction;
//import au.csiro.gsnlite.beans.AddressBean;
//import au.csiro.gsnlite.beans.DataField;
//import au.csiro.gsnlite.utils.ApplicationObject;
//import au.csiro.gsnlite.utils.Logger;
//
///**
// * This wrapper presents a MultiFormat protocol in which the data comes from the
// * system clock. Think about a sensor network which produces packets with
// * several different formats. In this example we have 3 different packets
// * produced by three different types of sensors. Here are the packet structures
// * : [temperature:double] , [light:double] , [temperature:double, light:double]
// * The first packet is for sensors which can only measure temperature while the
// * latter is for the sensors equipped with both temperature and light sensors.
// * 
// */
//public class MultiFormatWrapper extends AbstractWrapper {
//	private DataField[] collection = new DataField[] {
//			new DataField("packet_type", "int", "packet type"),
//			new DataField("temperature", "double",
//					"Presents the temperature sensor."),
//			new DataField("light", "double", "Presents the light sensor.") };
//
//	private static transient Logger logger = Logger.getInstance();
//	private static String TAG = "MultiFormatWrapper.class";
//
//	private int counter;
//	private AddressBean params;
//	private long rate = 1000;
//
//	private OpServiceConnection opServiceConnection;
//	private IFunction opService;
//	private static final String LOG_TAG = "InvokeOp";
//	private String category;
//
//	public boolean initialize() {
//		setName("MultiFormatWrapper" + counter++);
//
//		params = getActiveAddressBean();
//
//		if (params.getPredicateValue("rate") != null) {
//			rate = (long) Integer.parseInt(params.getPredicateValue("rate"));
//
//			logger.info(TAG,
//					"Sampling rate set to " + params.getPredicateValue("rate")
//							+ " msec.");
//		}
//		category = "au.csiro.sensmalite.mainapp.intent.category.ANDROID_INBUILT_ACCELEROMETER_SENSOR_WRAPPER_PLUGIN";
//		bindOpService();
//		return true;
//	}
//
//	public void run() {
//		Double light = 0.0, temperature = 0.0;
//		int packetType = 0;
//
//		while (isActive()) {
//			try {
//				// delay
//				Thread.sleep(rate);
//			} catch (InterruptedException e) {
//				logger.error(TAG, e.getMessage());
//			}
//			
//			// create some random readings
//			light = ((int) (Math.random() * 10000)) / 10.0;
//			temperature = ((int) (Math.random() * 1000)) / 10.0;
//			packetType = 2;
//
//			// post the data to GSN
//			try {
//				postStreamElement(new Serializable[] { packetType, temperature,
//						light });
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			logger.debug(TAG, "Multiformat Wrapper Sending data\n");
//			calculate();
//		}
//	}
//
//	public DataField[] getOutputFormat() {
//		return collection;
//	}
//
//	public String getWrapperName() {
//		return "MultiFormat Sample Wrapper";
//	}
//
//	public void dispose() {
//		counter--;
//	}
//
//	private void bindOpService() {
//		if (category != null) {
//			opServiceConnection = new OpServiceConnection();
//			Intent i = new Intent(
//					"au.csiro.sensmalite.mainapp.intent.action.PICK_PLUGIN");
//			i.addCategory(category);
//			ApplicationObject.getGsnLiteMainActivity().bindService(i, opServiceConnection, Context.BIND_AUTO_CREATE);
//			
//		}
//	}
//
//	private void releaseOpService() {
//		ApplicationObject.getGsnLiteMainActivity().unbindService(opServiceConnection);
//		opServiceConnection = null;
//	}
//
//	private void calculate() {
//		// String num1String = num1Field.getText().toString();
//		// String num2String = num2Field.getText().toString();
//		String message = null;
//		HashMap result = null;
//		try {
//			// message = "Illegal number: '"+num1String+"'";
//			// int i1 = Integer.parseInt( num1String );
//			// message = "Illegal number: '"+num2String+"'";
//			// int i2 = Integer.parseInt( num2String );
//			message = null;
//			try {
//				// result.put("light", opService.getSensorData());
//				result = (HashMap) opService.getSensorData();
//				// resultField.setText(result.get("first").toString());
//			} catch (DeadObjectException ex) {
//				Log.e(LOG_TAG, "DeadObjectException", ex);
//				message = "Service error";
//			} catch (RemoteException ex) {
//				Log.e(LOG_TAG, "RemoteException", ex);
//				message = "Service error";
//			}
//		} catch (NumberFormatException ex) {
//		}
//		if (message != null)
//			// resultField.setText( message );
//
//		else {
//			// resultField.setText( Float.toString((Float) result.get("light"))
//			// );
//			// result.
//
//		}
//	}
//
//	class OpServiceConnection implements ServiceConnection {
//		public void onServiceConnected(ComponentName className,
//				IBinder boundService) {
//			opService = IFunction.Stub.asInterface((IBinder) boundService);
//			Log.d(LOG_TAG, "onServiceConnected");
//			// opButton.setEnabled(true);
//		}
//
//		public void onServiceDisconnected(ComponentName className) {
//			opService = null;
//			Log.d(LOG_TAG, "onServiceDisconnected");
//			// opButton.setEnabled(false);
//		}
//	};
//}
