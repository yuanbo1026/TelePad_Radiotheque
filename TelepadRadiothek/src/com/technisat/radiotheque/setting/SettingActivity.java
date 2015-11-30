package com.technisat.radiotheque.setting;

import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.technisat.telepadradiothek.R;

public class SettingActivity extends PreferenceActivity {
	private Boolean isGreenStyle;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isGreenStyle = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("style", true);
		setTheme(R.style.PrefTheme);
//		getActionBar().setDisplayHomeAsUpEnabled(false);
//		getActionBar().setHomeButtonEnabled(false);
//		getActionBar().setDisplayShowTitleEnabled(false);
		addPreferencesFromResource(R.xml.preference);
		setContentView(R.layout.activity_setting);
		Button button = (Button)findViewById(R.id.preference_button);
		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
    			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(i);
				
			}
			
		});
//		Preference button = (Preference)findPreference(getString(R.string.preference_button));
//		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//		                @Override
//		                public boolean onPreferenceClick(Preference preference) {   
//		                	Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
//		        			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		        			startActivity(i);
//		                	return true;
//		                }
//		            });

	}

	// Called only on Honeycomb and later
	@Override
	public void onBuildHeaders(List<Header> target) {
		// loadHeadersFromResource(R.xml.header, target);
	}

	OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
		public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
			// Toast.makeText(SettingActivity.this, "GreenTheme: " +
			// prefs.getBoolean("style", true), Toast.LENGTH_LONG).show();
			
		}
	};

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
	}

}
