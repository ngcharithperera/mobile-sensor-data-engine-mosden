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
	private DataField[] collection = new DataField[] { new DataField(
			"within_area", "int", "packet type") };

	private static transient Logger logger = Logger.getInstance();
	private static String TAG = "GeofenceWrapper.class";
	public static String ACTION = "au.csiro.gsnlite.wrappers.GeofenceEvents";
	private int counter;
	private AddressBean params;
	private long rate = 1000;

	private PendingIntent pIntent;
	private BroadcastReceiver receiver;

	LocationClient mClient;
	LocationRequest mRequest;
	ArrayList<Geofence> mList;
	IntentFilter mFilter;

	public String locationInfo = "";
	private double latitude = 0.0;
	private double longitude = 0.0;
	private int radius = 0;
	private static int within_area = 99;

	public boolean initialize() {
		setName("GeofenceWrapper" + counter++);

		params = getActiveAddressBean();

		if (params.getPredicateValue("rate") != null) {
			rate = (long) Integer.parseInt(params.getPredicateValue("rate"));

			logger.info(TAG,
					"Sampling rate set to " + params.getPredicateValue("rate")
							+ " msec.");
		}
		latitude = Double.parseDouble(params.getPredicateValue("latitude"));
		longitude = Double.parseDouble(params.getPredicateValue("longitude"));
		radius = Integer.parseInt(params.getPredicateValue("radius"));

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
			Geofence fence = new Geofence.Builder()
					.setRequestId("1")
					.setCircularRegion(latitude, longitude, radius)
					.setTransitionTypes(
							Geofence.GEOFENCE_TRANSITION_ENTER
									| Geofence.GEOFENCE_TRANSITION_EXIT)
					.setExpirationDuration(Geofence.NEVER_EXPIRE).build();
			mList.add(fence);
			Intent intent = new Intent(MyApplication.getAppContext(),
					GeofenceRecognitionSerivce.class);
			PendingIntent pendingIntent = PendingIntent.getService(
					MyApplication.getAppContext(), 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			mClient.requestLocationUpdates(mRequest, pendingIntent);
			mClient.addGeofences(mList, pendingIntent, this);

		} else {

		}

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				within_area =  intent.getIntExtra("within_area", 0);
				
			}
		};

		mFilter = new IntentFilter(ACTION);
		mFilter.addCategory(Intent.CATEGORY_DEFAULT);
		MyApplication.getAppContext().registerReceiver(receiver, mFilter);
		return true;
	}

	public void run() {
		Log.i("GeofenceWrapper", Integer.toString(within_area));

		while (isActive()) {
			try {
				// delay
				Thread.sleep(rate);
			} catch (InterruptedException e) {
				logger.error(TAG, e.getMessage());
			}

			try {
				postStreamElement(new Serializable[] { within_area });
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.debug(TAG, "Geofence Wrapper Sending data\n");
		}
	}

	public DataField[] getOutputFormat() {
		return collection;
	}

	public String getWrapperName() {
		return "Geofence Wrapper";
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

	@Override
	public void onAddGeofencesResult(int arg0, String[] arg1) {
	}

	@Override
	public void onLocationChanged(Location arg0) {
	}
}
