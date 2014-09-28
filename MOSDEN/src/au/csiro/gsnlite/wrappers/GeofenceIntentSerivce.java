package au.csiro.gsnlite.wrappers;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

public class GeofenceIntentSerivce extends IntentService {

	public static String status = "";
	public GeofenceIntentSerivce() {
		super("GeofenceIntentSerivce");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		int transitionType = LocationClient.getGeofenceTransition(intent);
		Intent i = new Intent();
		i.addCategory(Intent.CATEGORY_DEFAULT);
		i.putExtra("Location", "NCKU");

		if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
			status = "inside";
//			i.putExtra("status", status);
		} else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
			status = "outside";
//			i.putExtra("status", status);
		} else {
//			status = "outside";
		}
		i.putExtra("status", status);

		i.setAction("au.csiro.gsnlite.wrappers.GeofenceEvents"); // Specify the
																	// action,
																	// a.k.a.
																	// receivers

		sendBroadcast(i);

		// Handler handler = new Handler(Looper.getMainLooper());
		// handler.post(new Runnable() {
		//
		// @Override
		// public void run() {
		// //Toast.makeText(getApplicationContext(), "Arrived at NCKU!",
		// Toast.LENGTH_LONG).show();
		// // Broadcast the event back to the main thread
		// Intent intent = new Intent();
		// intent.setAction("com.androidclass.locationaware.GeofenceEvents"); //
		// Specify the action, a.k.a. receivers
		// intent.addCategory(Intent.CATEGORY_DEFAULT);
		// intent.putExtra("Location", "NCKU");
		// sendBroadcast(intent);
		// }
		//
		// });
	}

}
