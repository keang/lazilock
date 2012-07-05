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
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;


public class LazilockService extends Service{
	private static final String PREFS_NAME="MyPrefsFile";
	
	private static final String TAG = LazilockService.class.getSimpleName();

	private static final double MINFORCE = 3;

	private static final int MINCOUNT = 2;
	
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
		Log.d(TAG, "service started");
		
		
		powermanager = (PowerManager) this.getSystemService(Context.POWER_SERVICE); 
		
		
		mScreenOffShakeListener = new ShakeEventListener(MINFORCE, MINCOUNT, this){
			@Override
			public void onShake(){
				((Vibrator)getSystemService(Context.VIBRATOR_SERVICE)).vibrate(200);
				
				if (powermanager.isScreenOn()&&!greenListIsActive()){
					((DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE)).lockNow();
				}
				else {
					Intent wakeIntent = new Intent(getApplicationContext(), ScreenWakerActivity.class);
					wakeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getApplicationContext().startActivity(wakeIntent);
				}				
			}
		};
		
		sManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		sManager.registerListener(mScreenOffShakeListener, sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),90000);
		/*screenReceiver = new ScreenReceiver(){
			
			@Override
			public void screenOnAction(Context context){
				Log.i(TAG, "SCREEN IS ON, REGISTER SENSOR NOW");
				//register proxim sensor:
				mScreenOffShakeListener.onShake();
				
			}
			
			@Override
			public void screenOffAction(Context context){

				//Log.i(TAG, "xxxSCREEN IS OFF, UNREGISTER SENSORxxx");
				//if(greenListIsActive());
				mScreenOffShakeListener.offShake();
				
			}
		}; 		
		IntentFilter filter = new IntentFilter (Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(screenReceiver, filter);*/


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

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
