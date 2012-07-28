package com.kakada.brightness.side.slider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*WindowManager.LayoutParams params = new WindowManager.LayoutParams(
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
		
        
        
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        */
        Intent serviceIntent = new Intent(this, BrightnessSliderService.class);
        Log.d("Service", "created");
        startService(serviceIntent);
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
}
