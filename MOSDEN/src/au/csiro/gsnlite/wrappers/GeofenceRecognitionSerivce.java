package au.csiro.gsnlite.wrappers;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

public class GeofenceRecognitionSerivce extends IntentService {

	public static int within_area = 0;
	public static String ACTION = "au.csiro.gsnlite.wrappers.GeofenceEvents";
	
	public GeofenceRecognitionSerivce() {
		super("GeofenceIntentSerivce");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		int transitionType = LocationClient.getGeofenceTransition(intent);
		Intent i = new Intent();
		i.addCategory(Intent.CATEGORY_DEFAULT);
		if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
			within_area = 1;
		} else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
			within_area = 0;
		} else {
		}
		i.putExtra("within_area", within_area);
		i.setAction(ACTION); 
		sendBroadcast(i);
	}
}
