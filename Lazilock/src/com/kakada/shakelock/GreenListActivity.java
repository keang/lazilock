package com.kakada.shakelock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.kakada.shakelock.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GreenListActivity extends Activity implements OnClickListener{
	private String PREFS_NAME = "MyPrefsFile";
	private List<PInfo> installedPackages;
	SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.greenlist);
		
		TextView listTitle = (TextView)findViewById(R.id.green_list_title);
		Typeface robo_font = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
		listTitle.setTypeface(robo_font);
		preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		
		LinearLayout mLinearLayout = (LinearLayout)findViewById(R.id.green_list_scroll);
		
		installedPackages = getPackages();
		 
		for(int i=0; i<installedPackages.size(); i++){
			CheckBox mCheckBox = new CheckBox(this);
			mCheckBox.setText(installedPackages.get(i).appname);
			//set icon:
			Drawable icon = (installedPackages.get(i).icon);
			icon.setBounds(0,0,60,60);
			mCheckBox.setCompoundDrawables(icon, null, null, null);
			
			Log.d("populating", Boolean.toString(preferences.getBoolean(installedPackages.get(i).appname, false)));
			
			//set checked:
			if(preferences.getBoolean(installedPackages.get(i).pname, false)){
				installedPackages.get(i).isChecked=preferences.getBoolean(installedPackages.get(i).pname, false);
			}
			mCheckBox.setChecked(installedPackages.get(i).isChecked);
			mCheckBox.setId(i);
			installedPackages.get(i).Id=i;
			
			//draw in layout:
			mLinearLayout.addView(mCheckBox);
			mCheckBox.setOnClickListener(this);
		}
		
		Button button_done= (Button) findViewById(R.id.done);
		button_done.setOnClickListener(this);
		button_done.setTypeface(robo_font);
		
		Button button_cancel= (Button) findViewById(R.id.cancel);
		button_cancel.setOnClickListener(this);
		button_cancel.setTypeface(robo_font);
		

		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	class PInfo {
	    private String appname = "";
	    private String pname = "";
	    private Drawable icon;
	    private boolean isChecked = false;
	    private int Id=-1;
	}

	private List<PInfo> getPackages() {
	    List<PInfo> apps = getInstalledApps(false); /* false = no system packages */
	    Collections.sort(apps, new Comparator<PInfo>(){
			public int compare(PInfo a, PInfo b) {
				// TODO Auto-generated method stub
				return a.appname.compareToIgnoreCase(b.appname);
			}
	    });
	    return apps;
	}

	private List<PInfo> getInstalledApps(boolean getSysPackages) {
	    List<PInfo> res = new ArrayList<PInfo>();        
	    List<ApplicationInfo> packs = getPackageManager().getInstalledApplications(0);
	    for(int i=0;i<packs.size();i++) {
	        ApplicationInfo p = packs.get(i);
	        if (!getSysPackages) {
	        	
	        	String appname = p.loadLabel(getPackageManager()).toString();
	        	if(appname.length()>5 && appname.substring(0, 4).equals("com.")){
	        		continue ;
	        	}
	        		
	        }
	        PInfo newInfo = new PInfo();
	        newInfo.appname = p.loadLabel(getPackageManager()).toString();
	        newInfo.pname = p.packageName;
	        newInfo.icon = p.loadIcon(getPackageManager());
	        res.add(newInfo);
	    }
	    
	    
	    return res; 
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.i("onclick", v.getClass().getName());
		Log.i("onclick", Integer.toString(v.getId()));
		if(v.getClass().equals(Button.class)){
			
			switch(v.getId()){
				case R.id.done:
					Log.i("onclick", "Done clicked");
				      SharedPreferences.Editor editor = preferences.edit();
				      for(PInfo mPackage: installedPackages){
				    	  editor.putBoolean(mPackage.pname, mPackage.isChecked);
				    	  Log.i("commiting", mPackage.appname + " "+Boolean.toString(mPackage.isChecked));
				      }
				      // Commit the edits!
				      editor.commit();
				      Log.i("onclick","commited");
				      finish();
					  break;
				case R.id.cancel:
					finish();
				}
		} else if(v.getClass().equals(CheckBox.class)){
			if(((CheckBox) v).isChecked()){
				for(int i=0; installedPackages.size()>i; i++){
					if(((CheckBox) v).getId() == installedPackages.get(i).Id){
						installedPackages.get(i).isChecked=true;
						//Log.i("checkbox", Boolean.toString(installedPackages.get(i).isChecked));
					}
				}
			} else {
				for(int i=0; installedPackages.size()>i; i++){
					if(((CheckBox) v).getId() == installedPackages.get(i).Id){
						installedPackages.get(i).isChecked=false;
						//Log.i("checkbox", "make False");
					}
				}
			}
				
			}
		
	}

}
