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

public class AndroidInbuiltMagneticFieldSensorPlugin extends Service
		implements SensorEventListener {
	static final String LOG_TAG = AndroidInbuiltMagneticFieldSensorPlugin.class
			.getSimpleName();
	private SensorManager mSensorManager;
	private Sensor snMagneticField;

	private float geomagneticFieldX_axis = 0;
	private float geomagneticFieldY_axis = 0;
	private float geomagneticFieldZ_axis = 0;
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
					new DataField4Plugins("GeomagneticFieldX_axis",
							"double",
							"Geomagnetic field strength along the x axis meassures in (mu)T."),
					new DataField4Plugins("GeomagneticFieldY_axis",
							"double",
							"Geomagnetic field strength along the y axis meassures in (mu)T."),
					new DataField4Plugins("GeomagneticFieldZ_axis",
							"double",
							"Geomagnetic field strength along the z axis meassures in (mu)T.") };
			return collection;
		}

		@Override
		public StreamElement4Plugins[] getReadings() throws RemoteException {
			mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

			if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
				snMagneticField = mSensorManager
						.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
				mSensorManager.registerListener(
						AndroidInbuiltMagneticFieldSensorPlugin.this,
						snMagneticField,
						SensorManager.SENSOR_DELAY_NORMAL);

			}
			se = new StreamElement4Plugins[] {
					new StreamElement4Plugins(geomagneticFieldX_axis),
					new StreamElement4Plugins(geomagneticFieldY_axis),
					new StreamElement4Plugins(geomagneticFieldZ_axis) };
			return se;
		}
	};


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			geomagneticFieldX_axis = event.values[0];
			geomagneticFieldY_axis = event.values[1];
			geomagneticFieldZ_axis = event.values[2];
		}
	}
}
