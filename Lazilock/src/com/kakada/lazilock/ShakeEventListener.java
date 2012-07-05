package com.kakada.lazilock;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.view.WindowManager;



public class ShakeEventListener implements SensorEventListener{
	private static final int MAX_PAUSE_BETWEEN_DIRECTION_CHANGE=200;
	private static final int MAX_TOTAL_DURATION_OF_CHANGE = 1000;
	
	private int minForce;
	private int minCount;

	
	private long mFirstDirectionChangeTime=0;
	private long mLastDirectionChangeTime=0;
	private int mDirectionChangeCount =0;
	private float lastX=0;
	private float lastY=0;
	private float lastZ=0;
	
	private Context context;
	
	
	public ShakeEventListener (int minforce, int mincount, Context c){
		minForce=minforce;
		minCount=mincount;
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
		float diffXSqr = (x-lastX)*(x-lastX);
		float diffYSqr = (y-lastY)*(y-lastY);
		float diffZSqr = (z-lastZ)*(z-lastZ);
		
		Log.e("values", Float.toString(z));
		float totalMovement= (float) (Math.sqrt(diffXSqr+diffYSqr+diffZSqr));
		//Log.d("ShakeEventListener", Float.toString(totalMovement));
		if(totalMovement>minForce){
			//Log.d("new A, old A", Double.toString(Math.sqrt((double)(x*x+y*y+z*z)) - Math.sqrt((double)(lastX*lastX-lastY*lastY-lastZ*lastZ))));
			long now = System.currentTimeMillis();
			
			//initialize movement time record
			if(mFirstDirectionChangeTime == 0){
				mFirstDirectionChangeTime = now;
				mLastDirectionChangeTime = now;
			}
			
			//check if movement was not long ago
			long lastChangeWasAgo = now - mLastDirectionChangeTime;
			if(lastChangeWasAgo<MAX_PAUSE_BETWEEN_DIRECTION_CHANGE){
				//Log.d("lastChangeWasAgo to register one change", Long.toString(lastChangeWasAgo));
				//one direction change passes
				mDirectionChangeCount++;
				mLastDirectionChangeTime = now;
				lastX = x;
				lastY = y;
				lastZ = z;
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
		
	}
	
	public void onShake(){
		Log.i("ShakeEventListener", "wake phone here!");
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);  
	}

}
