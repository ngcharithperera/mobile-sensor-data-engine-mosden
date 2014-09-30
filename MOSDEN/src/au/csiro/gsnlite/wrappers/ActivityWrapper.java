package au.csiro.gsnlite.wrappers;

import java.io.Serializable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.ActivityRecognitionClient;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import au.csiro.gsnlite.MyApplication;
import au.csiro.gsnlite.beans.AddressBean;
import au.csiro.gsnlite.beans.DataField;
import au.csiro.gsnlite.utils.Logger;

/**
 * This wrapper presents a MultiFormat protocol in which the data comes from the
 * system clock. Think about a sensor network which produces packets with
 * several different formats. In this example we have 3 different packets
 * produced by three different types of sensors. Here are the packet structures
 * : [temperature:double] , [light:double] , [temperature:double, light:double]
 * The first packet is for sensors which can only measure temperature while the
 * latter is for the sensors equipped with both temperature and light sensors.
 * 
 */
public class ActivityWrapper extends AbstractWrapper implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {
	private DataField[] collection = new DataField[] {
			new DataField("activity", "int", "Current user activity"),
			new DataField("confidence", "int", "Confidence of the reasoning")};

	private static transient Logger logger = Logger.getInstance();
	private static String TAG = "ActivityWrapper.class";
	private static String ACTION = "au.csiro.gsnlite.wrappers.ACTIVITY_RECOGNITION_DATA";

	private int counter;
	private AddressBean params;
	private long rate = 1000;

	private ActivityRecognitionClient arclient;
	private PendingIntent pIntent;
	private BroadcastReceiver receiver;

	public static int activity_type = 99;
	public static int confidence = 99;
	public boolean initialize() {
		setName("ActivityWrapper" + counter++);

		params = getActiveAddressBean();

		if (params.getPredicateValue("rate") != null) {
			rate = (long) Integer.parseInt(params.getPredicateValue("rate"));

			logger.info(TAG,
					"Sampling rate set to " + params.getPredicateValue("rate")
							+ " msec.");
		}

		int resp = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(MyApplication.getAppContext());
		if (resp == ConnectionResult.SUCCESS) {
			arclient = new ActivityRecognitionClient(
					MyApplication.getAppContext(), this, this);
			arclient.connect();
			Intent intent = new Intent(MyApplication.getAppContext(),
					ActivityRecognitionService.class);
			pIntent = PendingIntent.getService(MyApplication.getAppContext(),
					0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			arclient.requestActivityUpdates(1000, pIntent);

		} else {

		}

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				activity_type = intent.getExtras().getInt("ActivityType");
				confidence =  intent.getExtras().getInt("Confidence");
				
			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION);
		MyApplication.getAppContext().registerReceiver(receiver, filter);

		return true;
	}

	public void run() {
		Log.d("ActivityWrapper", Integer.toString(activity_type));

		while (isActive()) {
			try {
				// delay
				Thread.sleep(rate);
			} catch (InterruptedException e) {
				logger.error(TAG, e.getMessage());
			}


			// post the data to GSN
			try {
				postStreamElement(new Serializable[] { activity_type, confidence });
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.debug(TAG, "Activity Wrapper Sending data\n");
		}
	}

	public DataField[] getOutputFormat() {
		return collection;
	}

	public String getWrapperName() {
		return "Activity Wrapper";
	}

	public void dispose() {
		counter--;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
	}

	@Override
	public void onConnected(Bundle arg0) {
	}

	@Override
	public void onDisconnected() {
	}
}
