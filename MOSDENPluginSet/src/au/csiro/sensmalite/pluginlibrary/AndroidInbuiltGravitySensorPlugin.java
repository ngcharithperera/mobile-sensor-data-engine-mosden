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

public class AndroidInbuiltGravitySensorPlugin extends Service
		implements SensorEventListener {
	static final String LOG_TAG = AndroidInbuiltGravitySensorPlugin.class
			.getSimpleName();
	private SensorManager mSensorManager;
	private Sensor snGravity;
	private float gravityX_axis = 0;
	private float gravityY_axis = 0;
	private float gravityZ_axis = 0;
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
					new DataField4Plugins("gravityX_axis",
							"double",
							"Force of gravity along the x axis meassures in m/s2."),
					new DataField4Plugins("gravityY_axis",
							"double",
							"Force of gravity along the y axis meassures in m/s2."),
					new DataField4Plugins("gravityZ_axis",
							"double",
							"Force of gravity along the z axis meassures in m/s2.") };
			return collection;
		}
		


		@Override
		public StreamElement4Plugins[] getReadings() throws RemoteException {
			mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

			if (mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
				snGravity = mSensorManager
						.getDefaultSensor(Sensor.TYPE_LIGHT);
				mSensorManager.registerListener(
						AndroidInbuiltGravitySensorPlugin.this,
						snGravity,
						SensorManager.SENSOR_DELAY_NORMAL);

			}
			se = new StreamElement4Plugins[] {
					new StreamElement4Plugins(gravityX_axis),
					new StreamElement4Plugins(gravityY_axis),
					new StreamElement4Plugins(gravityZ_axis) };
			return se;
		}

	};


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
			gravityX_axis = event.values[0];
			gravityY_axis = event.values[1];
			gravityZ_axis = event.values[2];
		}
	}
}
