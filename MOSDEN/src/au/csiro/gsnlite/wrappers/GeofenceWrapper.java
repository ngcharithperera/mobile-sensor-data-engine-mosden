package au.csiro.gsnlite.wrappers;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.location.Location;
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
public class GeofenceWrapper extends AbstractWrapper implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener,
		OnAddGeofencesResultListener {
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

	// private ActivityRecognitionClient arclient;
	private PendingIntent pIntent;
	private BroadcastReceiver receiver;

	LocationClient mClient;
	LocationRequest mRequest;
	ArrayList<Geofence> mList;
	// GeofenceEventReceiver mReceiver;
	IntentFilter mFilter;

	public String locationInfo = "";

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
			mClient = new LocationClient(MyApplication.getAppContext(), this,
					this);
			mClient.connect();
			mRequest = LocationRequest.create();
			mRequest.setInterval(1000);
			mRequest.setFastestInterval(500);
			mRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

			mList = new ArrayList<Geofence>();
			Geofence fence = new Geofence.Builder().setRequestId("1")
					.setCircularRegion(-35.280199, 149.112325, 2000)
					.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
			                Geofence.GEOFENCE_TRANSITION_EXIT)
					.setExpirationDuration(Geofence.NEVER_EXPIRE).build();
			mList.add(fence);
			Intent intent = new Intent(MyApplication.getAppContext(),
					GeofenceIntentSerivce.class);
			PendingIntent pendingIntent = PendingIntent.getService(
					MyApplication.getAppContext(), 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			mClient.requestLocationUpdates(mRequest, pendingIntent);
			mClient.addGeofences(mList, pendingIntent, this);
			// Register the broadcast receiver

			// mFilter.addCategory(Intent.CATEGORY_DEFAULT);

		} else {

		}

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				locationInfo = "Arraived at "
						+ intent.getStringExtra("status");
				// Toast.makeText(context, locationInfo,
				// Toast.LENGTH_LONG).show();
				Log.i("charith", locationInfo);
			}
		};

		mFilter = new IntentFilter("au.csiro.gsnlite.wrappers.GeofenceEvents");
		mFilter.addCategory(Intent.CATEGORY_DEFAULT);
		MyApplication.getAppContext().registerReceiver(receiver, mFilter);

		// mClient.requestLocationUpdates(mRequest, this);

		// Build a Geofence

		// IntentFilter filter = new IntentFilter();
		// filter.addAction("au.csiro.gsnlite.wrappers.ACTIVITY_RECOGNITION_DATA");
		// MyApplication.getAppContext().registerReceiver(receiver, filter);

		// Method 2: Using Broadcast
		// Intent intent = new Intent();
		// intent.setAction("com.androidclass.locationaware.GeofenceEvents"); //
		// Specify
		// the
		// action,
		// a.k.a.
		// receivers
		// intent.addCategory(Intent.CATEGORY_DEFAULT);
		// intent.putExtra("Location", "NCKU");
		// PendingIntent pendingIntent = PendingIntent.getBroadcast(
		// MyApplication.getAppContext(), 0, intent,
		// PendingIntent.FLAG_UPDATE_CURRENT);
		// Send out the Geofence request
		//mClient.addGeofences(mList, pendingIntent, this);

		// // Method 1: Using IntentService
		// // Create a Intent to be sent to IntentService
		// Intent intent = new Intent(this, GeofenceIntentSerivce.class);
		// intent.setAction("GeofenceIntentSerivce");
		// // Start a PendingIntent service
		// PendingIntent pendingIntent = PendingIntent.getService(this, 0,
		// intent, PendingIntent.FLAG_UPDATE_CURRENT);

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
			// temperature = ((int) (Math.random() * 1000)) / 10.0;

			packetType = activityType;
			Log.d("charith", locationInfo);
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

	@Override
	public void onAddGeofencesResult(int arg0, String[] arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}
}
