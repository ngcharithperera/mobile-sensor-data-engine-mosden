package au.csiro.gsnlite.utils;

import au.csiro.gsnlite.GSNLiteMainActivity;

public class ApplicationObject {
	static GSNLiteMainActivity gsnLiteMainActivity;

	public static GSNLiteMainActivity getGsnLiteMainActivity() {
		return gsnLiteMainActivity;
	}

	public static void setGsnLiteMainActivity(
			GSNLiteMainActivity gsnLiteMainActivity) {
		ApplicationObject.gsnLiteMainActivity = gsnLiteMainActivity;
	}
	



}
