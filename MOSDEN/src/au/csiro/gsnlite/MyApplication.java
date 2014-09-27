package au.csiro.gsnlite;

import android.content.Context;

public class MyApplication {
	public static GSNLiteMainActivity activity;
	private static Context context;

	public static void setContext(GSNLiteMainActivity gsnLiteMainActivity) {
		activity = gsnLiteMainActivity;
		context = activity.getApplicationContext();

	}

	public static Context getAppContext() {
		return context;
	}
}
