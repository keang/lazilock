package com.kakada.shakelock;


import android.app.ActivityManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;


public class ShakelockService extends Service{
	private static final String PREFS_NAME="MyPrefsFile";
	
	private static final String TAG = ShakelockService.class.getSimpleName();

	private double MINFORCE;

	private static final int MINCOUNT = 1;
	
	ScreenReceiver screenReceiver;
	ShakeEventListener mScreenOffShakeListener;
	ShakeEventListener mScreenOnShakeListener;
	SensorManager sManager ;
	PowerManager powermanager;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		MINFORCE = (double)preferences.getFloat("MinForce", 4.5f);
		double STILL_ACCELERALTION = (double)preferences.getFloat("StillAcceleration", 9.8f);
		Log.i("MINFORCE", "UPDATED to " + Double.toString(MINFORCE));
		
		Log.d(TAG, "service started");
		
		powermanager = (PowerManager) this.getSystemService(Context.POWER_SERVICE); 
		
		
		mScreenOffShakeListener = new ShakeEventListener(MINFORCE, MINCOUNT, STILL_ACCELERALTION, this){
			@Override
			public void onShake(){

				
				if (powermanager.isScreenOn()&&!greenListIsActive()){
					((DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE)).lockNow();
					
				}				
			}
		};
		
		sManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		sManager.registerListener(mScreenOffShakeListener, sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),70000);
		screenReceiver = new ScreenReceiver(){
			
			@Override
			public void screenOnAction(Context context){
				Log.i(TAG, "SCREEN IS ON, REGISTER SENSOR NOW");
				sManager.registerListener(mScreenOffShakeListener, sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),70000);
				
			}
			
			@Override
			public void screenOffAction(Context context){

				//Log.i(TAG, "xxxSCREEN IS OFF, UNREGISTER SENSORxxx");
				//if(greenListIsActive());
				((Vibrator)getSystemService(Context.VIBRATOR_SERVICE)).vibrate(200);
				sManager.unregisterListener(mScreenOffShakeListener);
				
			}
		}; 		
		IntentFilter filter = new IntentFilter (Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(screenReceiver, filter);
		


	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(screenReceiver);
		sManager.unregisterListener(mScreenOffShakeListener);
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

}
