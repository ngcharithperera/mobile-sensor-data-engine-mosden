package au.csiro.gsnlite.utils;

import android.util.Log;

public class Logger {
	
	private static Logger logger;
	
	private boolean isDebugEnabled = true;
	private boolean isInfoEnabled = true;
	
	//private String TAG;
	
	public static Logger getInstance(){
		if (logger == null){
			return new Logger();
		}
		else
			return logger;
	}
	
	public Logger(){			
	}
//	public Logger(String className){
//		TAG = className;		
//	}
	
	public void debug(String TAG, String debugMsg, Exception e){
		Log.d(TAG, debugMsg);
	}
	
	public void info(String TAG, String infoMsg, Exception e){
		Log.i(TAG, infoMsg);
	}
	
	public void error(String TAG, String errorMsg, Exception e){
		Log.e(TAG, errorMsg);
	}
	
	public void warn(String TAG, String warnMsg, Exception e){
		Log.e(TAG,warnMsg);
	}

	public void debug(String TAG, String debugMsg){
		Log.d(TAG, debugMsg);
	}
	
	public void info(String TAG, String infoMsg){
		Log.i(TAG, infoMsg);
	}
	
	public void error(String TAG, String errorMsg){
		Log.e(TAG, errorMsg);
	}
	
	public void warn(String TAG, String warnMsg){
		Log.e(TAG,warnMsg);
	}
	
	
	public boolean isDebugEnabled() {
		return isDebugEnabled;
	}

	public void setDebugEnabled(boolean isDebugEnabled) {
		this.isDebugEnabled = isDebugEnabled;
	}

	public boolean isInfoEnabled() {
		return isInfoEnabled;
	}

	public void setInfoEnabled(boolean isInfoEnabled) {
		this.isInfoEnabled = isInfoEnabled;
	}

	
	
}
