package au.csiro.gsnlite.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Logger {
	


	private static Logger logger;
	
	private boolean isDebugEnabled = false;
	private boolean isInfoEnabled = true;
	private static Handler uilistener;
	
	//private String TAG;
	
	public static Logger getInstance(){
		if (logger == null){
			return new Logger();
		}
		else
			return logger;
	}
	
	public Handler getUilistener() {
		return uilistener;
	}

	public void setUilistener(Handler uilistener) {
		this.uilistener = uilistener;
	}
	
	
	public Logger(){			
	}
//	public Logger(String className){
//		TAG = className;		
//	}
	
	public void debug(String TAG, String debugMsg, Exception e){
		uilistener.sendMessage(createBundle(debugMsg));
		Log.d(TAG, debugMsg);		
	}
	
	
	public void info(String TAG, String infoMsg, Exception e){
		uilistener.sendMessage(createBundle(infoMsg));
		Log.i(TAG, infoMsg);
	}
	
	public void error(String TAG, String errorMsg, Exception e){
		uilistener.sendMessage(createBundle(errorMsg));
		Log.e(TAG, errorMsg);
		
	}
	
	public void warn(String TAG, String warnMsg, Exception e){		
		try {
			uilistener.sendMessage(createBundle(warnMsg));
			Log.w(TAG,warnMsg);
		} catch (Exception e2) {
			// TODO: handle exception
		}
		
	}

	public void debug(String TAG, String debugMsg){
		uilistener.sendMessage(createBundle(debugMsg));
		Log.d(TAG, debugMsg);
	}
	
	public void info(String TAG, String infoMsg){
		uilistener.sendMessage(createBundle(infoMsg));
		Log.i(TAG, infoMsg);
	}
	
	public void error(String TAG, String errorMsg){
		uilistener.sendMessage(createBundle(errorMsg));
		Log.e(TAG, errorMsg);
		
	}
	
	public void warn(String TAG, String warnMsg){
		uilistener.sendMessage(createBundle(warnMsg));
		Log.w(TAG,warnMsg);
	}
	
	public Message createBundle(String _msg){
		Message msg = uilistener.obtainMessage();;
		Bundle bundle = new Bundle();
		bundle.putString("message", _msg);
		msg.setData(bundle);
		return msg;
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
