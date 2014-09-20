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

public class AndroidInbuiltLinearAccelerationSensorPlugin extends Service
		implements SensorEventListener {
	static final String LOG_TAG = AndroidInbuiltLinearAccelerationSensorPlugin.class
			.getSimpleName();
	private SensorManager mSensorManager;
	private Sensor snLinearAcceleration;
	
	private float accelerationX_axis_excl_gravity = 0;
	private float accelerationY_axis_excl_gravity = 0;
	private float accelerationZ_axis_excl_gravity = 0;
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
					new DataField4Plugins("accelerationX_axis_excl_gravity",
							"double",
							"Acceleration force along the x axis (excluding gravity) meassures in m/s2."),
					new DataField4Plugins("accelerationY_axis_excl_gravity",
							"double",
							"Acceleration force along the y axis (excluding gravity) meassures in m/s2."),
					new DataField4Plugins("accelerationZ_axis_excl_gravity",
							"double",
							"Acceleration force along the x axis (excluding gravity) meassures in m/s2.") };
			return collection;
		}
		
		


		@Override
		public StreamElement4Plugins[] getReadings() throws RemoteException {
			mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

			if (mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
				snLinearAcceleration = mSensorManager
						.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
				mSensorManager.registerListener(
						AndroidInbuiltLinearAccelerationSensorPlugin.this,
						snLinearAcceleration,
						SensorManager.SENSOR_DELAY_NORMAL);

			}
			se = new StreamElement4Plugins[] {
					new StreamElement4Plugins(accelerationX_axis_excl_gravity),
					new StreamElement4Plugins(accelerationY_axis_excl_gravity),
					new StreamElement4Plugins(accelerationZ_axis_excl_gravity) };
			return se;
		}
	};


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
			accelerationX_axis_excl_gravity = event.values[0];
			accelerationY_axis_excl_gravity = event.values[1];
			accelerationZ_axis_excl_gravity = event.values[2];
		}
	}
}
