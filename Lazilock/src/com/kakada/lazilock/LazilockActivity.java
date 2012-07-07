package com.kakada.lazilock;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class LazilockActivity extends Activity implements OnClickListener {

	private static final int REQUEST_ENABLE = 1;
	private static final String TAG = LazilockActivity.class.getSimpleName();
	private static final String PREFS_NAME = 	"MyPrefsFile";
	private static final float WEAK_VALUE = 4.0f;
	private static final float STRONG_VALUE = 6.5f;;
	
	ProgressDialog dialog;
	DevicePolicyManager mDPM;
	ComponentName mAdminName;
	ToggleButton toggle; 
	Button button_uninstall;
	Button button_about;
	Button button_greenlist;
	
	SharedPreferences preferences; 
	SharedPreferences.Editor editor;
	boolean Calibrationdone = false;
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mAdminName = new ComponentName(this, AdminReceiver.class);
		
		preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE); 
		editor =  preferences.edit();
		
		TextView title = (TextView)findViewById(R.id.title);
		Typeface title_font=Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
		title.setTypeface(title_font);
		
		TextView intro = (TextView)findViewById(R.id.intro);
		Typeface intro_font = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
		intro.setTypeface(intro_font);
		
		toggle = (ToggleButton) findViewById(R.id.toggleButton);
		if(isMyServiceRunning()){
			toggle.setChecked(true);
			if(getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getFloat("MinForce", STRONG_VALUE)==STRONG_VALUE)
				((RadioButton)findViewById(R.id.radio_strong)).setChecked(true);
			else ((RadioButton)findViewById(R.id.radio_weak)).setChecked(true);
		}
		toggle.setChecked(isMyServiceRunning());
		toggle.setOnClickListener(this);
		
		Button button_calibrate = (Button)findViewById(R.id.btn_calibrate);
		button_calibrate.setOnClickListener(this);
		button_calibrate.setText(R.string.btn_calibrate);
		button_calibrate.setTypeface(intro_font);

		button_uninstall = (Button)findViewById(R.id.btn_uninstall);
		button_uninstall.setOnClickListener(this);
		button_uninstall.setText(R.string.btn_uninstall);
		button_uninstall.setTypeface(intro_font);
		
		button_about = (Button)findViewById(R.id.btn_about);
		button_about.setOnClickListener(this);
		button_about.setText(R.string.btn_about);
		button_about.setTypeface(intro_font);
		
		button_greenlist = (Button)findViewById(R.id.btn_greenlist);
		button_greenlist.setOnClickListener(this);
		button_greenlist.setText(R.string.btn_greenlist);
		button_greenlist.setTypeface(intro_font);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(dialog!= null)
			dialog.dismiss();
		dialog  = null;
		Log.i(TAG, "oneResume");

	}

	private boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
		
		for(RunningServiceInfo service: manager.getRunningServices(Integer.MAX_VALUE)){
			if(LazilockService.class.getName().equals(service.service.getClassName())){
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub

		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE:
			if (resultCode == Activity.RESULT_OK) {
				Log.i(TAG, "Admin enabled!");
				Toast.makeText(this,
						getString(R.string.service_enabled_message),
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(this, LazilockService.class);
				startService(intent);
			} else {
				Log.i(TAG, "Admin enable FAILED!");
				toggle.setChecked(false);
			}
			return;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("Activity", "Destroyed");
		super.onDestroy();
	}

	public void onRadioButtonClicked(View view){
		boolean checked = ((RadioButton)view).isChecked();
		
		if(checked){
			switch(view.getId()){
			case R.id.radio_strong:
				editor.putFloat("MinForce", STRONG_VALUE);
				break;
			case R.id.radio_weak:
				editor.putFloat("MinForce", WEAK_VALUE);
			}
			editor.commit();
			stopService(new Intent(this, LazilockService.class));
			toggleOn();

		}
		}
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		
		//ON/OFF
		case R.id.toggleButton:
			ToggleButton toggle = (ToggleButton) view;
			if (toggle.isChecked()) { // toggle enabled, check if got permission
										// now
				toggleOn();
				((RadioButton)findViewById(R.id.radio_strong)).setChecked(true);

				
			} else { // toggle disabled, stop service now AND remove admin
				//Toast.makeText(this,
				//		getString(R.string.service_disabled_message),
				//		Toast.LENGTH_SHORT).show();
				((RadioGroup)findViewById(R.id.radio_group)).clearCheck();
				stopService(new Intent(this, LazilockService.class));
				Toast.makeText(this, getString(R.string.service_stoped_message), Toast.LENGTH_SHORT).show();
				
			}
			
			break;
			
		//about button
		case R.id.btn_about:
			Intent i = new Intent(this, AboutActivity.class);
			startActivity(i);
			break;
		
		//greenlist button
		case R.id.btn_greenlist:
			dialog = ProgressDialog.show(this, "", 
                    "Loading. Please wait...", true);
			try{
			startActivity(new Intent(this, GreenListActivity.class));
			}catch(Exception s){}

			break;			
			
		//uninstall button
		case R.id.btn_uninstall:
			mDPM.removeActiveAdmin(mAdminName);
			stopService(new Intent(this, LazilockService.class));
			ToggleButton status = (ToggleButton)findViewById(R.id.toggleButton);
			status.setChecked(false);
			Log.i(TAG,"Admin removed");
			Toast.makeText(this, getString(R.string.uninstall_instructions), Toast.LENGTH_LONG).show();
			break;
			
		//calibrate button
		case R.id.btn_calibrate:
			calibrate();
			
		}
	}

	protected void calibrate() {
		final SensorEventListener listener = new SensorEventListener(){

			public void onAccuracyChanged(Sensor arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}

			public void onSensorChanged(SensorEvent arg0) {
				if(arg0.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
					double acc = Math.sqrt(arg0.values[0]*arg0.values[0]+arg0.values[1]*arg0.values[1]
							+arg0.values[2]*arg0.values[2]);
					Log.i("still acc", Double.toString(acc));
					editor.putFloat("StillAcceleration", (float)acc);
					Calibrationdone = true;
				}
				
			}
			
		};
		final SensorManager sManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);


		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Place your phone on a flat surface, and press calibrate.")
		       .setCancelable(true)
		       .setPositiveButton("Calibrate", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		       		sManager.registerListener(listener, sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),70000);
		       		if(Calibrationdone = true){
		                Toast.makeText(getApplicationContext(), R.string.calibrate_finish_message, Toast.LENGTH_SHORT).show();
		                sManager.unregisterListener(listener);
		       		}
		                
		           }
		       })
		       .setNegativeButton("Later", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog dialog = builder.create();
		dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		dialog.show();
		Calibrationdone = false;
	
	}

	private void toggleOn() {
		toggle.setChecked(true);
		
		if (!mDPM.isAdminActive(mAdminName)) {
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
					mAdminName);
			intent.putExtra(
					DevicePolicyManager.EXTRA_ADD_EXPLANATION,
					this.getString(R.string.admin_permission_explanation));
			startActivityForResult(intent, REQUEST_ENABLE);
		} else {
		//	Toast.makeText(this,
		//			getString(R.string.service_enabled_message),
		//			Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this, LazilockService.class);
			startService(intent); // is
		Toast.makeText(this, getString(R.string.service_started_message), Toast.LENGTH_SHORT);						// already
																		// admin!
	}

	}
}