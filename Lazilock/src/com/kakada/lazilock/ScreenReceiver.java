package com.kakada.lazilock;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.widget.Toast;

public class ScreenReceiver extends BroadcastReceiver{
	
	private SensorManager mManager;

	
	@Override
	public void onReceive(Context context, Intent intent) {
		//mAdminName = new ComponentName(context, AdminReceiver.class);
		
		//move to activity
		mManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

		if(mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)==null){
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
		//to be overwritten when used
	}
	
	public void screenOffAction(Context context)
	{
		//to be overwritten when used
	}

}
