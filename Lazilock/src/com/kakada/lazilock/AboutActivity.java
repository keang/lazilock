package com.kakada.lazilock;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AboutActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		TextView aboutText = (TextView)findViewById(R.id.about);
		aboutText.setOnClickListener(this);

		aboutText.setMovementMethod(LinkMovementMethod.getInstance());
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		finish(); 
		
	}
	

}
