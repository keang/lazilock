package com.usapia.lazilock;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class aboutActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Toast.makeText(this, "ABOUT", Toast.LENGTH_SHORT);
		setContentView(R.layout.about);
	}
	
	

}
