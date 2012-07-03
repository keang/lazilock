package com.usapia.lazilock;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


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
		screenReceiver.screenOnAction(this);
		registerReceiver(screenReceiver, filter);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		screenReceiver.screenOffAction(this);
		unregisterReceiver(screenReceiver);
		Toast.makeText(this, getString(R.string.service_disabled_message), Toast.LENGTH_SHORT);
		Log.d(TAG, "service destroyed");
		super.onDestroy();

	}

}
