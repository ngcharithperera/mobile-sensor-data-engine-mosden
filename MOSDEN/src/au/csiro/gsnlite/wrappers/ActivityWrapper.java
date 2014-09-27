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
			new DataField("packet_type", "int", "packet type"),
			new DataField("temperature", "double",
					"Presents the temperature sensor."),
			new DataField("light", "double", "Presents the light sensor.") };

	private static transient Logger logger = Logger.getInstance();
	private static String TAG = "MultiFormatWrapper.class";

	private int counter;
	private AddressBean params;
	private long rate = 1000;

	private ActivityRecognitionClient arclient;
	private PendingIntent pIntent;
	private BroadcastReceiver receiver;

	public int activityType;

	public boolean initialize() {
		setName("MultiFormatWrapper" + counter++);

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
				String v = "";
				v = "Activity :" + intent.getStringExtra("Activity") + " "
						+ "Confidence : "
						+ intent.getExtras().getInt("Confidence") + "\n";
				activityType = intent.getExtras().getInt("ActivityType");
				Log.d("charith", v);
			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction("au.csiro.gsnlite.wrappers.ACTIVITY_RECOGNITION_DATA");
		MyApplication.getAppContext().registerReceiver(receiver, filter);

		return true;
	}

	public void run() {
		Double light = 0.0, temperature = 0.0;
		int packetType = 0;

		while (isActive()) {
			try {
				// delay
				Thread.sleep(rate);
			} catch (InterruptedException e) {
				logger.error(TAG, e.getMessage());
			}

			// create some random readings
			light = ((int) (Math.random() * 10000)) / 10.0;
			//temperature = ((int) (Math.random() * 1000)) / 10.0;
			
			packetType = activityType;

			// post the data to GSN
			try {
				postStreamElement(new Serializable[] { packetType, temperature,
						light });
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.debug(TAG, "Multiformat Wrapper Sending data\n");
		}
	}

	public DataField[] getOutputFormat() {
		return collection;
	}

	public String getWrapperName() {
		return "MultiFormat Sample Wrapper";
	}

	public void dispose() {
		counter--;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onConnected(Bundle arg0) {

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}
}
