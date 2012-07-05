package com.kakada.lazilock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class ScreenWakerActivity extends Activity {
	private ScreenReceiver mScreenReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mScreenReceiver = new ScreenReceiver(){
			@Override 
			public void screenOnAction(Context context){
				finish();				
			}
		};
		Log.d("WAKEACTIVITY", "created");
		IntentFilter filter = new IntentFilter (Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mScreenReceiver, filter);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mScreenReceiver!=null)
		unregisterReceiver(mScreenReceiver);
	}
	

	
}
