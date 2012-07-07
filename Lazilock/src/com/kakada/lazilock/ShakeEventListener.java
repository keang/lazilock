package com.kakada.lazilock;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;



public class ShakeEventListener implements SensorEventListener{
	private static final int MAX_PAUSE_BETWEEN_DIRECTION_CHANGE=200;
	private static final int MAX_TOTAL_DURATION_OF_CHANGE = 900;
	private static final String PREFS_NAME="MyPrefsFile";
	
	
	private double minForce;
	private int minCount;

	private double STILL_ACCELERATION = 0;
	private long mFirstDirectionChangeTime=0;
	private long mLastDirectionChangeTime=0;
	private int mDirectionChangeCount =0;
	private float lastX=0;
	private float lastY=0;
	private float lastZ=0;
	private float lastAcc;
	
	private Context context;
	
	
	public ShakeEventListener (double minforce2, int mincount, double stillAccel, Context c){
		minForce=minforce2;
		minCount=mincount;
		STILL_ACCELERATION = stillAccel;
		Log.i("ShakeListener creation", "still updated " + Double.toString(STILL_ACCELERATION));
		context = c;
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent se) {
		float x= se.values[0];
		float y= se.values[1];
		float z= se.values[2];
		
		//calculate movement: 
		float thisAcc = (float) (Math.sqrt(x*x+y*y+z*z));
		float totalMovement=  Math.abs(thisAcc - lastAcc);

		
		//Log.d("ShakeEventListener", Float.toString(totalMovement));
		if( totalMovement>minForce){
			//Log.d("new A, old A", Double.toString(Math.sqrt((double)(x*x+y*y+z*z)) - Math.sqrt((double)(lastX*lastX-lastY*lastY-lastZ*lastZ))));
			long now = System.currentTimeMillis();
			Log.d("acc raw", Float.toString(totalMovement));
			//initialize movement time record
			if(mFirstDirectionChangeTime == 0){
				mFirstDirectionChangeTime = now;
				mLastDirectionChangeTime = now;
			}
			
			//check if movement was not long ago
			long lastChangeWasAgo = now - mLastDirectionChangeTime;
			if(lastChangeWasAgo<MAX_PAUSE_BETWEEN_DIRECTION_CHANGE){
				Log.d("lastChangeWasAgo to register one change", Long.toString(lastChangeWasAgo));
				//one direction change passes
				mDirectionChangeCount++;
				Log.d("count", Integer.toString(mDirectionChangeCount));
				mLastDirectionChangeTime = now;
				lastX = x;
				lastY = y;
				lastZ = z;
				lastAcc = thisAcc;
			}
			
			//check how many direction change so far
			if(mDirectionChangeCount > minCount){
				Log.d("DIR change count that exceeds min", Integer.toString(mDirectionChangeCount));
				//shake almost registers!
				long totalDurationChange = now - mFirstDirectionChangeTime;
				Log.d("shaking", Long.toString(totalDurationChange));
				if(totalDurationChange< MAX_TOTAL_DURATION_OF_CHANGE){
					//shake finally registers!
					Log.d("shaking", "YAY");
					onShake();
					resetParameter();
				}
			}
		} else {
			resetParameter();
		}
	}

	private void resetParameter() {
		mFirstDirectionChangeTime = 0;
		mDirectionChangeCount = 0;
		mLastDirectionChangeTime = 0;
		lastX = 0;
		lastY = 0;
		lastZ = 0;
		lastAcc = (float) STILL_ACCELERATION;
		
	}
	
	public void onShake(){
		Log.i("ShakeEventListener", "wake phone here!");
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);  
	}

	public void offShake() {
		// TODO Auto-generated method stub
		
	}

}
