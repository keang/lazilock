package com.kakada.lazilock;


import android.app.ActivityManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;


public class LazilockService extends Service implements SensorEventListener{
	private static final String PREFS_NAME="MyPrefsFile";
	
	private static final String TAG = LazilockService.class.getSimpleName();
	
	ScreenReceiver screenReceiver;


	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d(TAG, "service started");
		
		screenReceiver = new ScreenReceiver(){
			
			@Override
			public void screenOnAction(Context context){
				Log.i(TAG, "SCREEN IS ON, REGISTER SENSOR NOW");
				//register proxim sensor:
				SensorManager sManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
				sManager.registerListener((SensorEventListener) context, sManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), 350000);
			}
			
			@Override
			public void screenOffAction(Context context){

				//Log.i(TAG, "xxxSCREEN IS OFF, UNREGISTER SENSORxxx");
				//if(greenListIsActive());
				((SensorManager)getSystemService(Context.SENSOR_SERVICE)).unregisterListener((SensorEventListener)context);
			}
		}; 
		
		screenReceiver.screenOnAction(this);
		
		IntentFilter filter = new IntentFilter (Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(screenReceiver, filter);


	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		screenReceiver.screenOffAction(this);
		unregisterReceiver(screenReceiver);
		//Log.d(TAG, "service destroyed");
		super.onDestroy();

	}
	
	private boolean greenListIsActive() {
		SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		for(ActivityManager.RunningAppProcessInfo runningApp : ((ActivityManager)getSystemService(ACTIVITY_SERVICE)).getRunningAppProcesses()){
			
			if(runningApp.importance==ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
				//Log.i("FOREGROUND ACTIVITY", runningApp.processName);
				if(preferences.getBoolean(runningApp.processName, false))
					return true;
				//else Log.i("foreground but not ticked", runningApp.processName);
			}
		}
		return false;
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		if(!greenListIsActive()&&arg0.values[0]==0){
			((Vibrator)getSystemService(Context.VIBRATOR_SERVICE)).vibrate(200);
			((DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE)).lockNow();		
			//Log.d(TAG, "LOCK PHONE");
		}
	}

}
