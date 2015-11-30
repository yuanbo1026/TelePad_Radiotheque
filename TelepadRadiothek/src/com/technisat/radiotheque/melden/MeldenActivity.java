package com.technisat.radiotheque.melden;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.technisat.radiotheque.Drawer.MyDrawer;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.menu.ActionBarMenuHandler;
import com.technisat.radiotheque.service.MediaPlayerService;
import com.technisat.telepadradiothek.R;

/**
 * Activity that can send an Email to report a problem with the App,
 * containing the currently playing station (that got most likely something to
 * do with the occurring problem and a text typed by the user.
 *
 */
public class MeldenActivity extends Activity {
	
	EditText mEdit;
	Station station;
	private TextView mTvSubCaption;
	private TextView mTvCaption;
	private Button mAbortButton;
	private Button mSendButton;
	private Boolean isGreenStyle;
	private RelativeLayout layout;
	
	private MyDrawer mMyDrawer;
	private DrawerLayout mDrawerLayout;
	
	/**  handles the Intent and de-parcels the given Station (if there is one) */
	private BroadcastReceiver bcr = new BroadcastReceiver(){
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent != null && intent.getAction() != null){
				if(intent.getAction().equals(getString(R.string.radiothek_mediaplayerservice_broadcastmetadata))){
					Station s = intent.getParcelableExtra(getString(R.string.radiothek_bundle_station));
					if(s != null){
						station = s;
						mTvSubCaption.setText(getString(R.string.meldenactivity_text_withstation) + " "+ s.getStationName());
					}
				}
			}
		}
	};
	
	
	@Override
	protected void onPause() {
		unregisterReceiver(bcr);
		super.onPause();
	}

	@Override
	protected void onResume() {
		IntentFilter i = new IntentFilter();
		i.addAction(getString(R.string.radiothek_mediaplayerservice_broadcastmetadata));
		registerReceiver(bcr, i);
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isGreenStyle = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("style", true);
		setTheme(isGreenStyle ? R.style.GreenTheme : R.style.InvertTheme);
		setContentView(R.layout.drawer_layout_container);
		
		Misc.initUIL(getApplicationContext());
		getActionBar().setIcon(isGreenStyle ? R.drawable.logo_actionbar : R.drawable.ivt_logo_actionbar);
		
		mMyDrawer = new MyDrawer(this);
		mDrawerLayout = mMyDrawer.getDrawerLayout();
		
		LayoutInflater inflater = getLayoutInflater();
        RelativeLayout container = (RelativeLayout) findViewById(R.id.frame_container);
    	inflater.inflate(R.layout.activity_melden, container);
		
		layout = (RelativeLayout)findViewById(R.id.melden_layout);
		mEdit   = (EditText)findViewById(R.id.et_activity_melden_textfield);
		mTvSubCaption = (TextView) findViewById(R.id.tv_activity_melden_subcaption);
		mTvCaption = (TextView) findViewById(R.id.tv_activity_melden_caption);
		mAbortButton = (Button) findViewById(R.id.btn_activity_melden_abort);
		mSendButton = (Button) findViewById(R.id.btn_activity_melden_send);
		View divider = (View)findViewById(R.id.view_line);
		divider.setBackground(this.getResources().getDrawable(isGreenStyle?R.color.RadiothekBlueLight:R.color.ivt_general_text_color));
		
		mEdit.setTypeface(Misc.getCustomFont(this, Misc.FONT_NORMAL));
		mTvSubCaption.setTypeface(Misc.getCustomFont(this, Misc.FONT_NORMAL));
		mTvCaption.setTypeface(Misc.getCustomFont(this, Misc.FONT_BOLD));
		mAbortButton.setTypeface(Misc.getCustomFont(this, Misc.FONT_BOLD));
		mSendButton.setTypeface(Misc.getCustomFont(this, Misc.FONT_BOLD));
		
		layout.setBackground(this.getResources().getDrawable(isGreenStyle?R.color.RadiothekBlueDark:R.color.ivt_detail_bg));
		mEdit.setTextColor(this.getResources().getColorStateList(
				isGreenStyle ? R.color.RadiothekBlueLight : R.color.ivt_Grid_item_bg_h));
		mTvSubCaption.setTextColor(this.getResources().getColorStateList(
				isGreenStyle ? R.color.RadiothekBlueLight : R.color.ivt_Grid_item_bg_h));
		mTvCaption.setTextColor(this.getResources().getColorStateList(
				isGreenStyle ? R.color.RadiothekBlueLight : R.color.ivt_Grid_item_bg_h));
		mAbortButton.setTextColor(this.getResources().getColorStateList(
				isGreenStyle ? R.color.RadiothekBlueLight : R.color.ivt_Grid_item_bg_h));
		mSendButton.setTextColor(this.getResources().getColorStateList(
				isGreenStyle ? R.color.RadiothekBlueLight : R.color.ivt_Grid_item_bg_h));
		
		
		
//		Intent intent = new Intent(this, MediaPlayerService.class);
//		intent.setAction(getString(R.string.radiothek_mediaplayerservice_requestcurrentstation));
//		startService(intent);
	}
	
	/**
	 * Method that actually sends the Email on button click
	 * @param view
	 */
	public void senden(View view){
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{getString(R.string.report_problem_mailto)});
		i.putExtra(Intent.EXTRA_SUBJECT, "Radiotheque melden");
		if (station != null)
			i.putExtra(Intent.EXTRA_TEXT   , mEdit.getText().toString() + "\n\n" + " StationName: "+
				station.getStationName()+", StationURL: "+
				station.getStationUrl()+", StationId: "+
				station.getId()+", StationMetadata: "+
				station.getMetadata());
		else
			i.putExtra(Intent.EXTRA_TEXT,mEdit.getText().toString());
		try {
		    startActivity(Intent.createChooser(i, getString(R.string.meldenactivity_text_mailwith)));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(this, getString(R.string.meldenactivity_text_nomailclients), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void abbrechen(View view){
		finish();
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
	        case R.id.action_drawer:
	        	if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
	        		mDrawerLayout.openDrawer(Gravity.RIGHT);
	        		
	        	}else{
	        		mDrawerLayout.closeDrawer(Gravity.RIGHT);
	        	}
	            return true;
	        default:
	        	if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
	        		mDrawerLayout.closeDrawer(Gravity.RIGHT);
	        	}
	        	return ActionBarMenuHandler.handleActionBarItemClick(this, item);
	        }
	}
}