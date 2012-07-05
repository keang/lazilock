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

	private static final int MINFORCE = 8;

	private static final int MINCOUNT = 4;
	
	ScreenReceiver screenReceiver;
	ShakeEventListener mShakeListener;
	SensorManager sManager ;

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
		sManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		mShakeListener = new ShakeEventListener(MINFORCE, MINCOUNT, this){
			@Override
			public void onShake(){
				Intent wakeIntent = new Intent(getApplicationContext(), ScreenWakerActivity.class);
				wakeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getApplicationContext().startActivity(wakeIntent);
			}
		};
		
		screenReceiver = new ScreenReceiver(){
			
			@Override
			public void screenOnAction(Context context){
				Log.i(TAG, "SCREEN IS ON, REGISTER SENSOR NOW");
				//register proxim sensor:
				sManager.registerListener((SensorEventListener) context, sManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), 1000000);
				sManager.unregisterListener(mShakeListener);
			}
			
			@Override
			public void screenOffAction(Context context){

				//Log.i(TAG, "xxxSCREEN IS OFF, UNREGISTER SENSORxxx");
				//if(greenListIsActive());
				sManager.unregisterListener((SensorEventListener)context);
				sManager.registerListener(mShakeListener, sManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
				
				
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
		sManager.unregisterListener(mShakeListener);
		sManager.unregisterListener(this);
		
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
		if(!greenListIsActive()){
			((Vibrator)getSystemService(Context.VIBRATOR_SERVICE)).vibrate(400);
			((DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE)).lockNow();		
			//Log.d(TAG, "LOCK PHONE");
		}
	}

}
