package com.usapia.lazilock;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

public class ScreenReceiver extends BroadcastReceiver implements SensorEventListener {
	private static final String TAG = ScreenReceiver.class.getSimpleName();
	private DevicePolicyManager mDPM;
	//private ComponentName mAdminName;
	
	private SensorManager mManager;
	private Sensor mSensor;
	@Override
	public void onReceive(Context context, Intent intent) {
		
		mDPM = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		//mAdminName = new ComponentName(context, AdminReceiver.class);
		
		
		mManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		if(mSensor==null){
			Toast.makeText(context, context.getString(R.string.device_unsupported_message), Toast.LENGTH_LONG).show();
		}
		
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
		if(mManager== null)
			mManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		if(mSensor==null)
			mSensor = mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		Log.i(TAG, "SCREEN IS ON, REGISTER SENSOR NOW");
		mManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void screenOffAction(Context context)
	{
		if(mManager== null)
			mManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		if(mSensor==null)
			mSensor = mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		Log.i(TAG, "xxxSCREEN IS OFF, UNREGISTER SENSORxxx");
		mManager.unregisterListener(this);
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}


	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.values[0]==0){
			mDPM.lockNow();		
			//Log.d(TAG, "LOCK PHONE");
		}
	}

	

}
