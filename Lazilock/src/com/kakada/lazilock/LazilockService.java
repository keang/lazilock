package com.kakada.lazilock;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;


public class LazilockService extends Service{
	private static final String TAG = LazilockService.class.getSimpleName();
	
	ScreenReceiver screenReceiver = new ScreenReceiver();


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
		
		IntentFilter filter = new IntentFilter (Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(screenReceiver, filter);
		
		Vibrator myVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		screenReceiver.registerVibrator(myVibrator);
		screenReceiver.screenOnAction(this);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		screenReceiver.screenOffAction(this);
		unregisterReceiver(screenReceiver);
		Log.d(TAG, "service destroyed");
		super.onDestroy();

	}

}
