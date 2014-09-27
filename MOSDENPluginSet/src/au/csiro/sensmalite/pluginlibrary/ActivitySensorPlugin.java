package au.csiro.sensmalite.pluginlibrary;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;
import au.csiro.gsnlite.IFunction;
import au.csiro.gsnlite.beans.DataField4Plugins;
import au.csiro.gsnlite.beans.StreamElement4Plugins;
import au.csiro.gsnlite.util.ActivityRecognitionService;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.ActivityRecognitionClient;

public class ActivitySensorPlugin extends Service implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {
	static final String LOG_TAG = ActivitySensorPlugin.class.getSimpleName();

	private StreamElement4Plugins[] se;

	private ActivityRecognitionClient arclient;
	private PendingIntent pIntent;
	private BroadcastReceiver receiver;
	public String activity="ToBeReplaced";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("charith", "onStartCommand");
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resp == ConnectionResult.SUCCESS) {
			arclient = new ActivityRecognitionClient(this, this, this);
			arclient.connect();
		} else {
			Toast.makeText(this, "Please install Google Play Service.",
					Toast.LENGTH_SHORT).show();
		}
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				activity = "Activity :" + intent.getStringExtra("Activity")
						+ " " + "Confidence : "
						+ intent.getExtras().getInt("Confidence") + "\n";
				Log.d("charith", activity);

			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction("au.csiro.sensmalite.pluginlibrary.ACTIVITY_RECOGNITION_DATA");
		registerReceiver(receiver, filter);
		return super.onStartCommand(intent, flags, startId);

	}

	public void onDestroy() {
		super.onDestroy();
		if (arclient != null) {
			arclient.removeActivityUpdates(pIntent);
			arclient.disconnect();
		}
		unregisterReceiver(receiver);
	}

	public IBinder onBind(Intent intent) {
		Log.d("charith", "onBind");
		return mulBinder;
	}

	private final IFunction.Stub mulBinder = new IFunction.Stub() {

		@Override
		public DataField4Plugins[] getDataStructure() throws RemoteException {
			DataField4Plugins[] collection = new DataField4Plugins[] { new DataField4Plugins(
					"ambient_temperature", "double",
					"Ambient air temperature in Celcius."), };
			return collection;
		}

		@Override
		public StreamElement4Plugins[] getReadings() throws RemoteException {
			double x = 56.0;
			se = new StreamElement4Plugins[] { new StreamElement4Plugins(x) };
			Log.d("charith", activity);
			return se;
		}
	};

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.d("charith", "onConnectionFailed");
		Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.d("charith", "onConnected");
		Intent intent = new Intent(this, ActivityRecognitionService.class);
		pIntent = PendingIntent.getService(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		arclient.requestActivityUpdates(1000, pIntent);
	}

	@Override
	public void onDisconnected() {
		Log.d("charith", "onDisconnected");
		// TODO Auto-generated method stub

	}
}
