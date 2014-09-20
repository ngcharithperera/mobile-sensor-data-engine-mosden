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

public class AndroidInbuiltLightSensorPlugin extends Service implements
		SensorEventListener {

	static final String LOG_TAG = AndroidInbuiltLightSensorPlugin.class
			.getSimpleName();
	private SensorManager mSensorManager;
	private Sensor snLight;

	private float light = 0;
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
			DataField4Plugins[] collection = new DataField4Plugins[] { new DataField4Plugins(
					"light", "double", "Illuminance meassures in lx.") };
			return collection;
		}

		@Override
		public StreamElement4Plugins[] getReadings() throws RemoteException {
			mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

			if (mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
				snLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
				mSensorManager.registerListener(
						AndroidInbuiltLightSensorPlugin.this, snLight,
						SensorManager.SENSOR_DELAY_NORMAL);

			}
			se = new StreamElement4Plugins[] { new StreamElement4Plugins(light) };
			return se;
		}
	};

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
			light = event.values[0];
		}
	}
}
