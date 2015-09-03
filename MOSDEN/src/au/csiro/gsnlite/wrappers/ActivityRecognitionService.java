package au.csiro.gsnlite.wrappers;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class ActivityRecognitionService extends IntentService	 {

	private String TAG = this.getClass().getSimpleName();
	private static String ACTION = "au.csiro.gsnlite.wrappers.ACTIVITY_RECOGNITION_DATA";
	public ActivityRecognitionService() {
		super("ActivityRecognitionService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if(ActivityRecognitionResult.hasResult(intent)){
			ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
			Log.i(TAG, result.getMostProbableActivity().getType() +"\t" + result.getMostProbableActivity().getConfidence());
			Intent i = new Intent(ACTION);
			i.putExtra("ActivityType", result.getMostProbableActivity().getType());
			i.putExtra("Confidence", result.getMostProbableActivity().getConfidence());
			sendBroadcast(i);
		}
	}
	
	// Not used,  but can be used to convert the INTEGER to STRING that represents the Activity
	private String getType(int type){
		if(type == DetectedActivity.UNKNOWN)
			return "Unknown";
		else if(type == DetectedActivity.IN_VEHICLE)
			return "In Vehicle";
		else if(type == DetectedActivity.ON_BICYCLE)
			return "On Bicycle";
		else if(type == DetectedActivity.ON_FOOT)
			return "On Foot";
		else if(type == DetectedActivity.STILL)
			return "Still";
		else if(type == DetectedActivity.TILTING)
			return "Tilting";
		else
			return "";
	}

}
