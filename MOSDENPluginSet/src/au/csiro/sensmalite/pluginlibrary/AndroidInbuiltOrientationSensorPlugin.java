package au.csiro.sensmalite.pluginlibrary;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.RemoteException;
import au.csiro.gsnlite.IFunction;
import au.csiro.gsnlite.beans.DataField4Plugins;
import au.csiro.gsnlite.beans.StreamElement4Plugins;

public class AndroidInbuiltOrientationSensorPlugin extends Service implements
		SensorEventListener {
	static final String LOG_TAG = AndroidInbuiltOrientationSensorPlugin.class
			.getSimpleName();
	private SensorManager mSensorManager;
	private Sensor snOrientation;

	private float azimuthX_axis = 0;
	private float pitchY_axis = 0;
	private float rollZ_axis = 0;
	private StreamElement4Plugins[] se;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	public void onDestroy() {
		super.onDestroy();
	}

	public IBinder onBind(Intent intent) {
		return mulBinder;
	}

	private final IFunction.Stub mulBinder = new IFunction.Stub() {

		@Override
		public DataField4Plugins[] getDataStructure() throws RemoteException {
			DataField4Plugins[] collection = new DataField4Plugins[] {
					new DataField4Plugins("azimuthX_axis", "double",
							"Azimuth (angle around the z-axis) meassures in Degrees."),
					new DataField4Plugins("pitchY_axis", "double",
							"Pitch (angle around the x-axis) meassures in Degrees."),
					new DataField4Plugins("rollZ_axis", "double",
							"Roll (angle around the y-axis) meassures in Degrees.") };
			return collection;
		}

		@Override
		public StreamElement4Plugins[] getReadings() throws RemoteException {
			mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

			if (mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) != null) {
				snOrientation = mSensorManager
						.getDefaultSensor(Sensor.TYPE_ORIENTATION);
				mSensorManager.registerListener(
						AndroidInbuiltOrientationSensorPlugin.this,
						snOrientation, SensorManager.SENSOR_DELAY_NORMAL);
			}
			se = new StreamElement4Plugins[] {
					new StreamElement4Plugins(azimuthX_axis),
					new StreamElement4Plugins(pitchY_axis),
					new StreamElement4Plugins(rollZ_axis) };
			return se;

		}
	};

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			azimuthX_axis = event.values[0];
			pitchY_axis = event.values[1];
			rollZ_axis = event.values[2];
		}
	}
}
