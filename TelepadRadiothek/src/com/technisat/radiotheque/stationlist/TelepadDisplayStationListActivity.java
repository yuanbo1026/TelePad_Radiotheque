package com.technisat.radiotheque.stationlist;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.technisat.radiotheque.Drawer.MyDrawer;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.broadcast.IMediaPlayerBroadcastListener;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.entity.StationList;
import com.technisat.radiotheque.loadstations.ILoadStations;
import com.technisat.radiotheque.loadstations.LoadStationsOfCountryAsyncTask;
import com.technisat.radiotheque.loadstations.LoadStationsOfGenreAsyncTask;
import com.technisat.radiotheque.menu.ActionBarMenuHandler;
import com.technisat.radiotheque.player.PlayerFragement.IPlayerFragment;
import com.technisat.radiotheque.service.MediaPlayerService;
import com.technisat.radiotheque.station.TelepadStationActivity;
import com.technisat.radiotheque.station.util.Globals;
import com.technisat.radiotheque.stationlist.search.ISearchCallback;
import com.technisat.radiotheque.stationlist.search.SearchAsyncTask;
import com.technisat.telepadradiothek.R;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

public class TelepadDisplayStationListActivity extends FragmentActivity implements IPlayerFragment, ISearchCallback, IMediaPlayerBroadcastListener, ILoadStations {

	private MyDrawer mMyDrawer;
	private DrawerLayout mDrawerLayout;

	private ListView mGehoertListView;
	private TelepadItemListAdapter mItemListAdapter;
	private List<Station> stationList;

	private ProgressDialog mProgressBar;
	private boolean mIsFavList = false;

	private ViewSwitcher mViewSwitcher;
	private ItemListAdapter mItemGridAdapter;

	public static final int VIEW_LIST = 0;
	public static final int VIEW_GRID = 1;

	private StationList sList;
	private ImageView mImageView;

	private boolean isGreenStyle;
	private boolean isSizeBig;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isGreenStyle = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("style", true);
		setTheme(isGreenStyle ? R.style.GreenTheme : R.style.InvertTheme);
		setContentView(R.layout.drawer_layout_container);
		Misc.initUIL(getApplicationContext());
		mContext = this;
		mMyDrawer = new MyDrawer(this);
		mDrawerLayout = mMyDrawer.getDrawerLayout();
		getActionBar().setIcon(isGreenStyle ? R.drawable.logo_actionbar : R.drawable.ivt_logo_actionbar);

		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout container = (RelativeLayout) findViewById(R.id.frame_container);
		inflater.inflate(R.layout.telepad_activity_displaystationlist, container);
		isSizeBig = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("size", true);

		Intent intent = getIntent();
		handleIntent(intent);

	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
			if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
				mDrawerLayout.openDrawer(Gravity.RIGHT);
			} else {
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
			}
		}
		return super.dispatchKeyEvent(event);
	}

	/**
	 * Checks if the Intent got an Action and basically initiates the
	 * {@link ItemListAdapter} and fills the {@link ListView} with Data
	 *
	 * @param intent
	 */
	private void handleIntent(Intent intent) {
		TextView emptyListTV = (TextView) findViewById(android.R.id.empty);
		emptyListTV.setTextColor(mContext.getResources().getColorStateList(R.color.ivt_general_text_color));
		mGehoertListView = (ListView) findViewById(R.id.telepad_lv_displaystationlist_list);
		mGehoertListView.setBackground(mContext.getResources().getDrawable(isGreenStyle ? R.color.detail_bg : R.color.ivt_detail_bg));
		ColorDrawable sage = new ColorDrawable(this.getResources().getColor(isGreenStyle ? R.color.RadiothekBlueDark : R.color.ivt_action_bar_bg));
		mGehoertListView.setDivider(sage);
		mGehoertListView.setDividerHeight(1);
		mImageView = (ImageView) findViewById(R.id.station_list_return_button);
		mImageView.setImageResource(isGreenStyle ? R.drawable.station_list_return_button : R.drawable.ivt_station_list_return_button);
		mImageView.setBackground(mContext.getResources().getDrawable(isGreenStyle ? R.drawable.station_list_return_button_bg : R.drawable.ivt_station_list_return_button_bg));

		mImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		/**set station list empty, grab all stations on async task and show spinner.*/
		sList = new StationList();
		if (intent == null) return;

		/** Handle Search Action Intent */
		if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEARCH)) {
			emptyListTV.setText(getString(R.string.radiothek_displaystationlist_text_search));
			mGehoertListView.setEmptyView(emptyListTV);

			// This got called as search result activity
			String query = intent.getStringExtra(SearchManager.QUERY);

			mProgressBar = new ProgressDialog(this);
			mProgressBar.setCancelable(true);
			mProgressBar.setMessage(getString(R.string.displaystationlistactivity_text_searchingdb));
			mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressBar.show();

			SearchAsyncTask task = new SearchAsyncTask(this, query, this);
			try {
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} catch (RejectedExecutionException e) {
				// handle this in v2
			}
		}

		/** Handle MoreStationsOfGenre Action Intent */
		else if (intent.getAction() != null && intent.getAction().equals(getString(R.string.radiothek_action_morestationsofgenre))) {

			mProgressBar = new ProgressDialog(this);
			mProgressBar.setCancelable(true);
			mProgressBar.setMessage(getString(R.string.displaystationlistactivity_text_loadstations));
			mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressBar.show();

			long genreId = intent.getLongExtra(getString(R.string.radiothek_bundle_long), 9);
			int count = intent.getIntExtra(getString(R.string.radiothek_bundle_int), 20);

			LoadStationsOfGenreAsyncTask task = new LoadStationsOfGenreAsyncTask(genreId, count, this, this);
			try {
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} catch (RejectedExecutionException e) {
			}
		}

		/** Handle MoreStationsOfCountry Action Intent */
		else if (intent.getAction() != null && intent.getAction().equals(getString(R.string.radiothek_action_morestationsofcountry))) {
			mProgressBar = new ProgressDialog(this);
			mProgressBar.setCancelable(true);
			mProgressBar.setMessage(getString(R.string.displaystationlistactivity_text_loadstations));
			mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressBar.show();

			String iso = "en";
			if (intent.hasExtra(getString(R.string.radiothek_bundle_string)))
				iso = intent.getStringExtra(getString(R.string.radiothek_bundle_string));
			int count = intent.getIntExtra(getString(R.string.radiothek_bundle_int), 20);

			LoadStationsOfCountryAsyncTask task = new LoadStationsOfCountryAsyncTask(iso, count, this, this);
			try {
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} catch (RejectedExecutionException e) {
			}
		} else if (intent.getAction() != null && intent.getAction().equals(getString(R.string.radiothek_action_favorite))) {
			mIsFavList = true;
			emptyListTV.setText(getString(R.string.radiothek_displaystationlist_text_favorite));
			mGehoertListView.setEmptyView(emptyListTV);
			sList = intent.getParcelableExtra(getString(R.string.radiothek_bundle_stationlistparcelable));
		} else if (intent.getAction() != null && intent.getAction().equals(getString(R.string.radiothek_action_history))) {
			emptyListTV.setText(getString(R.string.radiothek_displaystationlist_text_history));
			mGehoertListView.setEmptyView(emptyListTV);
			sList = intent.getParcelableExtra(getString(R.string.radiothek_bundle_stationlistparcelable));
		} else if (intent.getAction() != null && intent.getAction().equals(getString(R.string.radiothek_action_search_done))) {
			sList = intent.getParcelableExtra(getString(R.string.radiothek_bundle_stationlistparcelable));
		}


		if (sList != null && sList.getStationList() != null && !sList.getStationList().isEmpty()) {
			stationList = sList.getStationList();

			mItemListAdapter = new TelepadItemListAdapter(this, R.layout.telepad_stationlist_item, stationList, mIsFavList, isSizeBig ? R.layout.telepad_stationlist_item : R.layout.telepad_stationlist_item_small);
			mGehoertListView.setAdapter(mItemListAdapter);
		}
		mGehoertListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mGehoertListView.setOnItemClickListener(new OnItemClickListener() {

			/** Handles ItemClick and starts/stops player only for */
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Station s = mItemListAdapter.getItem(position);
				if (s != null) {
					if (Globals.isPlaying) {
						if (s.getId() != Globals.current_station.getId()) {
							Intent i = new Intent(TelepadDisplayStationListActivity.this, MediaPlayerService.class);
							i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
							i.putExtra(getString(R.string.radiothek_bundle_station), s);
							i.putExtra(getString(R.string.radiothek_bundle_stationlistparcelable), sList);
							startService(i);
						}
					} else {
						Intent i = new Intent(TelepadDisplayStationListActivity.this, MediaPlayerService.class);
						i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
						i.putExtra(getString(R.string.radiothek_bundle_station), s);
						i.putExtra(getString(R.string.radiothek_bundle_stationlistparcelable), sList);
						startService(i);
					}

					Intent intent = new Intent(TelepadDisplayStationListActivity.this, TelepadStationActivity.class);
					intent.putExtra(getString(R.string.radiothek_bundle_station), s);
					intent.putExtra(getString(R.string.radiothek_bundle_stationlistparcelable), sList);
					startActivity(intent);
				}
			}
		});

		mGehoertListView.requestFocus();

	}

	@Override
	public void onMoreStationsLoaded(final List<Station> stationList) {
		if (stationList != null) {
			/**
			 * this click event does performance the event from History and
			 * Favs. cause the History and Favs intent do not have the action
			 * "load more stations"
			 */
			isSizeBig = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("size", true);
			mItemListAdapter = new TelepadItemListAdapter(this, R.layout.telepad_stationlist_item, stationList, false, isSizeBig ? R.layout.telepad_stationlist_item : R.layout.telepad_stationlist_item_small);
			mGehoertListView.setAdapter(mItemListAdapter);
			// TODO : set listener here to get whole station list
			mGehoertListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			mGehoertListView.setOnItemClickListener(new OnItemClickListener() {

				/** Handles ItemClick and starts/stops player */
				@Override
				public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
					Station s = mItemListAdapter.getItem(position);
					if (s != null) {
						if (Globals.isPlaying) {
							if (s.getId() != Globals.current_station.getId()) {
								Intent i = new Intent(TelepadDisplayStationListActivity.this, MediaPlayerService.class);
								i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
								i.putExtra(getString(R.string.radiothek_bundle_station), s);
								i.putExtra(getString(R.string.radiothek_bundle_stationlistparcelable), sList);
								startService(i);
							}
						} else {
							Intent i = new Intent(TelepadDisplayStationListActivity.this, MediaPlayerService.class);
							i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
							i.putExtra(getString(R.string.radiothek_bundle_station), s);
							i.putExtra(getString(R.string.radiothek_bundle_stationlistparcelable), sList);
							startService(i);
						}

						Intent intent = new Intent(TelepadDisplayStationListActivity.this, TelepadStationActivity.class);
						intent.putExtra(getString(R.string.radiothek_bundle_station), s);
						intent.putExtra(getString(R.string.radiothek_bundle_stationlistparcelable), sList);
						startActivity(intent);

					}
				}
			});

			if (mProgressBar != null && mProgressBar.isShowing()) mProgressBar.dismiss();
		}
	}

	@SuppressLint("RtlHardcoded")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		Boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(Gravity.RIGHT);
		View view = this.getCurrentFocus();

		if (isDrawerOpen) {
			if (!view.getClass().getName().equalsIgnoreCase("com.android.internal.view.menu.ActionMenuItemView")) {
				mDrawerLayout.closeDrawers();
				mDrawerLayout.getChildAt(0).requestFocus();
			}
		} else {
			if (KeyEvent.KEYCODE_DPAD_UP == keyCode) {
				// action bar
				mDrawerLayout.getChildAt(1).requestFocus();
			} else if (KeyEvent.KEYCODE_DPAD_DOWN == keyCode) {
				// grid view
				mDrawerLayout.getChildAt(0).requestFocus();
			} else if (KeyEvent.KEYCODE_DPAD_LEFT == keyCode) {
			} else if (KeyEvent.KEYCODE_MENU == keyCode) {
				if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
					mDrawerLayout.openDrawer(Gravity.RIGHT);
				} else {
					mDrawerLayout.closeDrawer(Gravity.RIGHT);

				}
			}
		}

		return super.onKeyDown(keyCode, event);

	}

	@Override
	protected void onDestroy() {
		if (mMyDrawer != null) mMyDrawer.finalize();
		// unregisterReceiver(mBroadcast.getReceiver());
		super.onDestroy();
		mProgressBar= null;
	}

	@Override
	protected void onResume() {
		if (mItemListAdapter != null) this.mItemListAdapter.notifyDataSetChanged();
		this.invalidateOptionsMenu();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(isGreenStyle ? R.menu.menu_new : R.menu.ivt_menu_new, menu);
		if (!Globals.isPlaying) {
			MenuItem item = menu.findItem(R.id.action_current_play);
			item.setVisible(false);
//			MenuItem item_divider = menu.findItem(R.id.action_divider);
//			item_divider.setVisible(false);
			this.invalidateOptionsMenu();
		}

		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

		return true;
	}

	@SuppressLint("RtlHardcoded")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar actions click
		switch (item.getItemId()) {
			case R.id.action_drawer:
				if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
					mDrawerLayout.openDrawer(Gravity.RIGHT);

				} else {
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

	/* Interfaces */

	@Override
	public void onTogglePlay(Station station) {
		Intent i = new Intent(TelepadDisplayStationListActivity.this, MediaPlayerService.class);
		i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
		i.putExtra(getString(R.string.radiothek_bundle_station), station);
		startService(i);
	}

	/* ISearchCallback */

	@Override
	public void onSearchDone(List<Station> stationList) {
		if (mProgressBar != null && mProgressBar.isShowing()) {
			Intent i = new Intent(TelepadDisplayStationListActivity.this, TelepadDisplayStationListActivity.class);
			i.setAction(getString(R.string.radiothek_action_search_done));
			i.putExtra(getString(R.string.radiothek_bundle_stationlistparcelable), new StationList(stationList));
			handleIntent(i);
			mProgressBar.dismiss();
		}
	}

	/* IMediaPlayerBroadcastListener */

	@Override
	public void onStartedPlayingStation(Station station) {
		if (mViewSwitcher.getDisplayedChild() == 1) {
			if (mItemGridAdapter != null) {
				mItemGridAdapter.showPauseButton(station);
			}
		} else {
			if (mItemListAdapter != null) {
				mItemListAdapter.showPauseButton(station);
			}
		}
	}

	@Override
	public void onStoppedPlayingStation(Station station) {
		if (mViewSwitcher.getDisplayedChild() == 1) {
			if (mItemGridAdapter != null) {
				mItemGridAdapter.hidePauseButton(station);
			}
		} else {
			if (mItemListAdapter != null) {
				mItemListAdapter.hidePauseButton(station);
			}
		}
	}

	@Override
	public void onCurrentlyPlaying(Station station) {
	}

	@Override
	public void onErrorPlaying(Station station, int errorCode) {
		onStoppedPlayingStation(station);
	}

	/* Interface load more stations */


}
