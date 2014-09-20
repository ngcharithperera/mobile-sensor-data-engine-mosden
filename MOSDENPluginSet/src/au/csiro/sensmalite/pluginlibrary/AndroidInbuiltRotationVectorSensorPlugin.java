package au.csiro.sensmalite.pluginlibrary;

import java.util.HashMap;
import java.util.Map;

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

public class AndroidInbuiltRotationVectorSensorPlugin extends Service
		implements SensorEventListener {
	static final String LOG_TAG = AndroidInbuiltRotationVectorSensorPlugin.class
			.getSimpleName();
	private SensorManager mSensorManager;
	private float rotationVectorX_axis;
	private float rotationVectorY_axis;
	private float rotationVectorZ_axis;
	private Sensor snRotationVector;
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
					new DataField4Plugins("RotationVectorX_axis", "double", "Rotation vector component along the x axis (x * sin(Theta/2))."),
					new DataField4Plugins("RotationVectorY_axis", "double", "Rotation vector component along the y axis (y * sin(Theta/2))."),
					new DataField4Plugins("RotationVectorZ_axis", "double", "Rotation vector component along the z axis (z * sin(Theta/2)).")};
			return collection;
		}

	


		@Override
		public StreamElement4Plugins[] getReadings() throws RemoteException {
			mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

			if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null) {
				snRotationVector = mSensorManager
						.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
				mSensorManager.registerListener(
						AndroidInbuiltRotationVectorSensorPlugin.this,
						snRotationVector,
						SensorManager.SENSOR_DELAY_NORMAL);

			}
			se = new StreamElement4Plugins[] {
					new StreamElement4Plugins(rotationVectorX_axis),
					new StreamElement4Plugins(rotationVectorY_axis),
					new StreamElement4Plugins(rotationVectorZ_axis) };
			return se;	
		}

	};


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
			rotationVectorX_axis = event.values[0];
			rotationVectorY_axis = event.values[1];
			rotationVectorZ_axis = event.values[2];
		}
	}
}
