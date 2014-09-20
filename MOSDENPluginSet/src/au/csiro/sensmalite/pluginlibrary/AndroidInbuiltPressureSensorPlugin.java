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

public class AndroidInbuiltPressureSensorPlugin extends Service
		implements SensorEventListener {
	static final String LOG_TAG = AndroidInbuiltPressureSensorPlugin.class
			.getSimpleName();
	private SensorManager mSensorManager;
	private Sensor snPressure;
	private float pressure = 0;

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
					new DataField4Plugins("pressure",
							"double",
							"Ambient air pressure meassures inhPa or mbar.") };
			return collection;
		}
		
	

		@Override
		public StreamElement4Plugins[] getReadings() throws RemoteException {
			mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

			if (mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
				snPressure = mSensorManager
						.getDefaultSensor(Sensor.TYPE_PRESSURE);
				mSensorManager.registerListener(
						AndroidInbuiltPressureSensorPlugin.this,
						snPressure,
						SensorManager.SENSOR_DELAY_NORMAL);
			}
			se = new StreamElement4Plugins[] {
					new StreamElement4Plugins(pressure) };
			return se;
		}
	};

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
			pressure = event.values[0];
		}
	}
}
