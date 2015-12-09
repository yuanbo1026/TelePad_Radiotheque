package com.technisat.radiotheque.station;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.technisat.radiotheque.android.ImageLoaderSpinner;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.broadcast.IMediaPlayerBroadcastListener;
import com.technisat.radiotheque.broadcast.MediaPlayerBroadcastReceiver;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.entity.StationList;
import com.technisat.radiotheque.genre.SquareImageView;
import com.technisat.radiotheque.service.MediaPlayerService;
import com.technisat.radiotheque.station.util.Globals;
import com.technisat.radiotheque.stationdetail.TelepadStationDetailFragment;
import com.technisat.radiotheque.stationdetail.TelepadStationDetailFragment.OnStationDetailListener;
import com.technisat.telepadradiothek.R;

public class TelepadStationActivity extends FragmentActivity implements OnStationDetailListener, IMediaPlayerBroadcastListener {

	private SquareImageView mCover,mFullscreenCover;
	private ProgressBar mSpinner;

	private MediaPlayerBroadcastReceiver mBroadcast;
	private TelepadStationDetailFragment mStationDetailFragment;

	private Station mStation;
	private ImageLoader mImageLoader;
	private Handler mHandler;
	private Runnable mRunnable;
	private IFullscreenListener iFullscreenListener;

	private RelativeLayout mPlayer;
	private LinearLayout mFunction;

	private ImageView mIvReturnIcon;

	private Context mContext;
	private boolean isFullscreen = false;
	private StationList sList;

	private Window window;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		Log.d("Fullscreen Test", "isFullscreen: " + isFullscreen);
		if (isFullscreen) {
			if (KeyEvent.KEYCODE_DPAD_UP == keyCode) {
				iFullscreenListener.onChangingFullscreen();
				isFullscreen = !isFullscreen;
			} else if (KeyEvent.KEYCODE_DPAD_DOWN == keyCode) {
				iFullscreenListener.onChangingFullscreen();
				isFullscreen = !isFullscreen;
			} else if (KeyEvent.KEYCODE_DPAD_LEFT == keyCode) {
				iFullscreenListener.onChangingFullscreen();
				isFullscreen = !isFullscreen;
			} else if (KeyEvent.KEYCODE_DPAD_RIGHT == keyCode) {
				iFullscreenListener.onChangingFullscreen();
				isFullscreen = !isFullscreen;
			}

			mHandler.removeCallbacks(mRunnable);
			LayoutParams layoutpars = window.getAttributes();
			layoutpars.screenBrightness = 255 / (float) 255;
			window.setAttributes(layoutpars);

			//set fullscreen again
//			mHandler.removeCallbacks(mRunnable);
//			mHandler.postDelayed(mRunnable, 5000);
		}

		if (KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE == keyCode) {
			if(mStation!=null){
				onTogglePlay(mStation);
			}
		}


		return super.onKeyDown(keyCode, event);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout_container);

		Misc.initUIL(getApplicationContext());

		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout container = (RelativeLayout) findViewById(R.id.frame_container);
		inflater.inflate(R.layout.telepad_activity_station, container);

		mContext = this;

		mImageLoader = ImageLoader.getInstance();

		Intent intent = this.getIntent();
		Station temp = intent.getParcelableExtra(getString(R.string.radiothek_bundle_station));
		if (intent.hasExtra(getString(R.string.radiothek_bundle_station))) {
			if (Globals.current_station != null) {// any station was playing
				if (temp.getId() != Globals.current_station.getId()) {// new station
					mStation = intent.getParcelableExtra(getString(R.string.radiothek_bundle_station));
					sList = intent.getParcelableExtra(getString(R.string.radiothek_bundle_stationlistparcelable));
					Globals.current_station = mStation;
					Globals.current_station_list = sList;
				} else {// same station
					mStation = Globals.current_station;
					sList = Globals.current_station_list;
				}
			} else {// first play station
				mStation = intent.getParcelableExtra(getString(R.string.radiothek_bundle_station));
				sList = intent.getParcelableExtra(getString(R.string.radiothek_bundle_stationlistparcelable));
				Globals.current_station = mStation;
				Globals.current_station_list = sList;
			}

		} else {// station is playing, go back to detail fragment.
			if (Globals.current_station != null && Globals.current_station_list != null) {
				mStation = Globals.current_station;
				sList = Globals.current_station_list;
			}
		}

		mStationDetailFragment = (TelepadStationDetailFragment) getSupportFragmentManager().findFragmentById(R.id.telepad_fg_stationdetail);
		iFullscreenListener = mStationDetailFragment;

		mCover = (SquareImageView) findViewById(R.id.telepad_iv_station_cover);
		mSpinner = (ProgressBar) findViewById(R.id.telepad_stationd_spinner);

		if (mStation != null && mCover != null && mSpinner != null) {
			if (mStation.hasCoverAvailable()) {
				mImageLoader.displayImage(mStation.getCoverUrl(), mCover, new ImageLoaderSpinner(mSpinner));
			} else if (mStation.hasStationLogoAvailable()) {
				mImageLoader.displayImage(mStation.getStationLogoUrl(), mCover, new ImageLoaderSpinner(mSpinner));
			} else {
				mCover.setImageResource(R.drawable.ic_default_station);
			}
		}
		// TODO initial all component on fragment for controlling the focus
		initFragmentUI();
		// for change fullscreen
		mHandler = new Handler();
		mRunnable = new Runnable() {
			public void run() {
				iFullscreenListener.onChangingFullscreen();
				isFullscreen = true;
			}
		};

		mBroadcast = new MediaPlayerBroadcastReceiver(this, mStationDetailFragment);
		mBroadcast.addListener(this);
		registerReceiver(mBroadcast.getReceiver(), mBroadcast.getIntentFiler());
		mBroadcast.requestUpdateFromService();

		window = ((Activity) mContext).getWindow();

		if (Globals.isPlaying) {
			mHandler.removeCallbacks(mRunnable);
			mHandler.postDelayed(mRunnable, 30 * 000);
		}

	}

	private void initFragmentUI() {
		mPlayer = (RelativeLayout) findViewById(R.id.telepad_stationdetail_player_rl);
		mFunction = (LinearLayout) findViewById(R.id.telepad_stationdetail_function_ll);

		mIvReturnIcon = (ImageView) mFunction.findViewById(R.id.telepad_iv_stationdetail_function_return);
		mIvReturnIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mBroadcast.getReceiver());
		mHandler.removeCallbacks(mRunnable);
		super.onDestroy();
	}

	/* Fragment Interface */

	@Override
	public void onTogglePlay(Station station) {
		Intent i = new Intent(TelepadStationActivity.this, MediaPlayerService.class);
		i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
		i.putExtra(getString(R.string.radiothek_bundle_station), station);
		startService(i);

		LayoutParams layoutpars = window.getAttributes();
		layoutpars.screenBrightness = 255 / (float) 255;
		window.setAttributes(layoutpars);
	}

	@Override
	public void onNewStation(Station station) {
		mStation = station;
		if (mStation != null && mCover != null && mSpinner != null) {
			if (mStation.hasCoverAvailable()) {
				mImageLoader.displayImage(mStation.getCoverUrl(), mCover, new ImageLoaderSpinner(mSpinner));
			} else if (mStation.hasStationLogoAvailable()) {
				mImageLoader.displayImage(mStation.getStationLogoUrl(), mCover, new ImageLoaderSpinner(mSpinner));
			} else {
				mCover.setImageResource(R.drawable.ic_default_station);
			}
		}
	}

	/* Broadcast Interface */

	@Override
	public void onStartedPlayingStation(Station station) {
		if (mStation != null && mStation.getId() == station.getId()) {
			mStationDetailFragment.setPlayButton(true);
			mStation.setPlaying(true);
			Globals.isPlaying = true;
			Globals.current_station = station;
		}

		mHandler.removeCallbacks(mRunnable);
		mHandler.postDelayed(mRunnable, 30 * 1000);

	}

	@Override
	public void onStoppedPlayingStation(Station station) {
		if (mStation != null && mStation.getId() == station.getId()) {
			mStationDetailFragment.setPlayButton(false);
			mStation.setPlaying(false);
			Globals.isPlaying = false;

			mHandler.removeCallbacks(mRunnable);
			LayoutParams layoutpars = window.getAttributes();
			layoutpars.screenBrightness = 255 / (float) 255;
			window.setAttributes(layoutpars);
		}
	}

	@Override
	public void onCurrentlyPlaying(Station station) {

	}

	@Override
	public void onErrorPlaying(Station station, int errorCode) {
		onStoppedPlayingStation(station);
		Log.d("Nexxoo_TalePad_Radiotheque", "play stream, and get error.");
		final AlertDialog mAlertDialog = new AlertDialog.Builder(mContext).create();
		mAlertDialog.show();
		Window window = mAlertDialog.getWindow();
		window.setContentView(R.layout.play_error_dialog);
		TextView tv_title = (TextView) window.findViewById(R.id.error_text);
		tv_title.setText(getString(R.string.radiothek_mediaplayerservice_error_unknown));
		Button mButton_OK = (Button) window.findViewById(R.id.error_ok_button);
		mButton_OK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mAlertDialog.dismiss();
				finish();
			}
		});

	}


}
