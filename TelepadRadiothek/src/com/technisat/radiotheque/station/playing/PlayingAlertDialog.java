package com.technisat.radiotheque.station.playing;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.technisat.telepadradiothek.R;


public class PlayingAlertDialog extends Dialog {
	TextView mUpdateTitle;
	ProgressBar mProgressBar;

	public PlayingAlertDialog(Context context) {
		super(context);
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_playing_alert);
		
		mUpdateTitle = (TextView) findViewById(R.id.tv_alert_title);
		
		mProgressBar = (ProgressBar) findViewById(R.id.alert_spinner);
		

		findViewById(R.id.btn_alert_ok).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						
					}
				});

		
	}

}
