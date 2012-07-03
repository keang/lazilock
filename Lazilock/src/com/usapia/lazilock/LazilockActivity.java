package com.usapia.lazilock;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class LazilockActivity extends Activity implements OnClickListener {

	private static final int REQUEST_ENABLE = 1;
	private static final String TAG = LazilockActivity.class.getSimpleName();
	DevicePolicyManager mDPM;
	ComponentName mAdminName;
	ToggleButton toggle;
	Button button_uninstall;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mAdminName = new ComponentName(this, AdminReceiver.class);
		
		
		
		TextView title = (TextView)findViewById(R.id.title);
		Typeface title_font=Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
		title.setTypeface(title_font);
		
		TextView intro = (TextView)findViewById(R.id.intro);
		Typeface intro_font = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
		intro.setTypeface(intro_font);
		
		toggle = (ToggleButton) findViewById(R.id.toggleButton);
		toggle.setChecked(isMyServiceRunning());
		toggle.setOnClickListener(this);

		button_uninstall = (Button)findViewById(R.id.uninstall);
		button_uninstall.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
				startService(new Intent(this, LazilockService.class));
			} else {
				Log.i(TAG, "Admin enable FAILED!");
				Toast.makeText(this,
						getString(R.string.service_disabled_message),
						Toast.LENGTH_LONG).show();
				toggle.setChecked(false);
			}
			return;
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
					startService(new Intent(this, LazilockService.class)); // is
																			// already
																			// admin!
				}
			} else { // toggle disabled, stop service now AND remove admin
				//Toast.makeText(this,
				//		getString(R.string.service_disabled_message),
				//		Toast.LENGTH_SHORT).show();
				stopService(new Intent(this, LazilockService.class));
			}
			
			break;
			
			
		//uninstall button
		case R.id.uninstall:
			mDPM.removeActiveAdmin(mAdminName);
			stopService(new Intent(this, LazilockService.class));
			ToggleButton status = (ToggleButton)findViewById(R.id.toggleButton);
			status.setChecked(false);
			Log.i(TAG,"Admin removed");
			Toast.makeText(this, getString(R.string.uninstall_instructions), Toast.LENGTH_LONG).show();
			
		case R.id.about:
			Intent aboutIntent = new Intent(this, aboutActivity.class);
			startActivity(aboutIntent);
		}

	}
}