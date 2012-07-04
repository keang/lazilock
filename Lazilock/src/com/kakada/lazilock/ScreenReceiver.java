package com.kakada.lazilock;


import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;
import com.kakada.lazilock.ShakeEventListener;

public class ScreenReceiver extends BroadcastReceiver implements SensorEventListener{
	private static final int MIN_SHAKE_FORCE=10;
	private static final int MIN_SHAKE_COUNT=2;
	private static final String TAG = ScreenReceiver.class.getSimpleName();
	private String CAMERA_PROCESS_NAMES[]; 
	private DevicePolicyManager mDPM;
	private Vibrator myVibrator;
	
	private ActivityManager activityManager;
	
	private SensorManager mManager;
	private ShakeEventListener shakeListener;
	
	@Override
	public void onReceive(Context context, Intent intent) {

		mDPM = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		//mAdminName = new ComponentName(context, AdminReceiver.class);
		
		//move to activity
		mManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

		if(mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)==null){
			Toast.makeText(context, context.getString(R.string.device_unsupported_message), Toast.LENGTH_LONG).show();
		}
		shakeListener = new ShakeEventListener(MIN_SHAKE_FORCE, MIN_SHAKE_COUNT, context);
		
		boolean screenOn=true;
		if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
			screenOffAction(context);	
		} else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
			screenOnAction(context);
		}

		
		Intent serviceIntent = new Intent(context, LazilockService.class);
		serviceIntent.putExtra("screen state", screenOn);
		context.startService(serviceIntent);
	}

	public void screenOnAction(Context context)
	{
		Log.i(TAG, "SCREEN IS ON, REGISTER SENSOR NOW");
		
		//plumbing so lazilockservice can call this the first time: ****
		if(mManager== null)
			mManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

		activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		Resources res = context.getResources();
		CAMERA_PROCESS_NAMES = res.getStringArray(R.array.camera_process_names);
		///                                                         ****
		
		
		//register proxim sensor:
		if (mDPM ==null) mDPM = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		mManager.registerListener(this, mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
		
		//unregister accel sensor:
		mManager.unregisterListener(shakeListener);
	}
	
	public void screenOffAction(Context context)
	{
		
		Log.i(TAG, "xxxSCREEN IS OFF, UNREGISTER SENSORxxx");
		if(cameraActive());
		mManager.unregisterListener(this);
		
		
		context.startActivity(new Intent(context, ScreenOffActivity.class));
		mManager.registerListener(shakeListener, mManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}


	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.sensor.getType()==Sensor.TYPE_PROXIMITY && arg0.values[0]==0){
			if(!cameraActive()){
				Log.d(TAG, Boolean.toString(cameraActive()));
				if(myVibrator!=null)
					myVibrator.vibrate(200);
				mDPM.lockNow();		
				Log.d(TAG, "LOCK PHONE");
			}
		}
	}

	private boolean cameraActive() {
		if(activityManager!=null){
			for(ActivityManager.RunningAppProcessInfo runningApp : activityManager.getRunningAppProcesses()){
				
				if(runningApp.importance==ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
					Log.i("FOREGROUND ACTIVITY", runningApp.processName);
						for(String s: CAMERA_PROCESS_NAMES){
							if(runningApp.processName.equalsIgnoreCase(s))
								return true;
						}
				}
			}
		} else Log.i(TAG, "nULL MANAGER");
		return false;
	}

	public void registerVibrator(Vibrator v) {
		myVibrator = v;
		// TODO Auto-generated method stub
	}
}
