package com.kakada.brightness.side.slider;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

public class BrightnessSliderService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
       	     LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
       	     LayoutParams.TYPE_SYSTEM_ALERT,
       	     LayoutParams.FLAG_NOT_FOCUSABLE|LayoutParams.FLAG_NOT_TOUCH_MODAL|LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH|LayoutParams.FLAG_NOT_TOUCHABLE,
       	     PixelFormat.TRANSLUCENT);
       	WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
       	LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
       	View myView = inflater.inflate(R.layout.activity_main, null);
       	myView.setOnTouchListener(new OnTouchListener() {
       	   @Override
       	   public boolean onTouch(View v, MotionEvent event) {
       	       Log.d("Main activity", "touch me");
       	       return false;
       	   }
       	 });

       	// Add layout to window manager

       wm.addView(myView, params);
       Log.d("Service", "created");
       Toast.makeText(getApplicationContext(), "Started!", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i("Service", "destroyed");
	       Toast.makeText(getApplicationContext(), "Stopped!", Toast.LENGTH_SHORT).show();

	}
}

