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

public class AndroidInbuiltGyroscopeSensorPlugin extends Service
		implements SensorEventListener {
	static final String LOG_TAG = AndroidInbuiltGyroscopeSensorPlugin.class
			.getSimpleName();
	private SensorManager mSensorManager;
	private Sensor snGyroscope;

	private float rotationX_axis = 0;
	private float rotationY_axis = 0;
	private float rotationZ_axis = 0;
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
					new DataField4Plugins("rotationX_axis",
							"double",
							"Rate of rotation around the x axis meassures in rad/s."),
					new DataField4Plugins("rotationY_axis",
							"double",
							"Rate of rotation around the y axis meassures in rad/s."),
					new DataField4Plugins("rotationZ_axis",
							"double",
							"Rate of rotation around the z axis meassures in rad/s.") };
			return collection;
		}
		
		
		
		@Override
		public StreamElement4Plugins[] getReadings() throws RemoteException {

			mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

			if (mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
				snGyroscope = mSensorManager
						.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
				mSensorManager.registerListener(
						AndroidInbuiltGyroscopeSensorPlugin.this,
						snGyroscope,
						SensorManager.SENSOR_DELAY_NORMAL);
			}
			se = new StreamElement4Plugins[] {
					new StreamElement4Plugins(rotationX_axis),
					new StreamElement4Plugins(rotationY_axis),
					new StreamElement4Plugins(rotationZ_axis) };
			return se;
		}
	};


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
			rotationX_axis = event.values[0];
			rotationY_axis = event.values[1];
			rotationZ_axis = event.values[2];
		}

	}

}
