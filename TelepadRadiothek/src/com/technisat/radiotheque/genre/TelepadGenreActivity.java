package com.technisat.radiotheque.genre;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import com.jess.ui.TwoWayAbsListView;
import com.jess.ui.TwoWayAbsListView.OnScrollListener;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import com.technisat.radiotheque.Drawer.MyDrawer;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.entity.*;
import com.technisat.radiotheque.menu.ActionBarMenuHandler;
import com.technisat.radiotheque.station.util.Globals;
import com.technisat.radiotheque.stationlist.TelepadDisplayStationListActivity;
import com.technisat.telepadradiothek.R;

public class TelepadGenreActivity extends FragmentActivity implements com.jess.ui.TwoWayAdapterView.OnItemClickListener {

	private MyDrawer mMyDrawer;
	private DrawerLayout mDrawerLayout;

	private GenreList mGenreList;
	private TelepadGenreListAdapter mGenreGridAdapter;

	private CountryList mCountryList;
	private TelepadCountryListAdapter mCountryGridAdapter;

	public static TwoWayGridView gridView;
	private Context mContext;

	private boolean mIsScrollingUp;
	private int mLastFirstVisibleItem;

	private Boolean isGreenStyle;
	private ImageView mImageView;

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
		// getActionBar().setHomeButtonEnabled(false);
		// getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setIcon(isGreenStyle ? R.drawable.logo_actionbar : R.drawable.ivt_logo_actionbar);

		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout container = (RelativeLayout) findViewById(R.id.frame_container);
		inflater.inflate(R.layout.activity_telepad_genre, container);

		Log.d("nexxoo", "starte telepad_genreActivity");
		if (getIntent().getExtras() != null) {
			gridView = (TwoWayGridView) findViewById(R.id.telepad_genre_gridview);
			mGenreList = getIntent().getExtras().getParcelable(getString(R.string.radiothek_bundle_genreparceable));
			mCountryList = getIntent().getExtras().getParcelable(getString(R.string.radiothek_bundle_countryparceable));

			if (mGenreList != null) {
				mCountryGridAdapter = null;
				mGenreGridAdapter = new TelepadGenreListAdapter(this, mGenreList.getGenreList());
				gridView.setAdapter(mGenreGridAdapter);
				Log.d("TelepadGenre Activity", "mGenreGridAdapter");
			} else {
				if (mCountryList != null) {
					mGenreGridAdapter = null;
					mCountryGridAdapter = new TelepadCountryListAdapter(this, mCountryList.getCountryList());
					gridView.setAdapter(mCountryGridAdapter);
					Log.d("TelepadGenre Activity", "mCountryGridAdapter");
				}
			}

			gridView.requestFocus();
			gridView.setOnItemClickListener(this);
			gridView.setOnScrollListener(new OnScrollListener() {
				// private int currentVisibleItemCount;
				// private int currentScrollState;
				// private int currentFirstVisibleItem;
				// private int totalItem;

				// private void isScrollCompleted(boolean isScrollUp) {
				// if (totalItem - currentFirstVisibleItem ==
				// currentVisibleItemCount
				// && this.currentScrollState == SCROLL_STATE_IDLE) {
				// /** To do code here */
				// if (isScrollUp)
				// mGenreGridAdapter.pageIndex++;
				// else
				// mGenreGridAdapter.pageIndex--;
				// Log.e("Telepad",
				// "OnScrollListener mGenreGridAdapter.pageIndex :"
				// + mGenreGridAdapter.pageIndex);
				// mGenreGridAdapter.notifyDataSetChanged();
				//
				// }
				//
				// }

				@Override
				public void onScrollStateChanged(TwoWayAbsListView view, int scrollState) {

					// if (view.getId() == gridView.getId()) {
					// final int currentFirstVisibleItem =
					// gridView.getFirstVisiblePosition();
					//
					// if (currentFirstVisibleItem > mLastFirstVisibleItem) {
					// mIsScrollingUp = true;
					// } else if (currentFirstVisibleItem <
					// mLastFirstVisibleItem) {
					// mIsScrollingUp = false;
					// }
					//
					// mLastFirstVisibleItem = currentFirstVisibleItem;
					// }

					// this.currentScrollState = scrollState;
					/**
					 * detect the scrolling direction
					 */
					// if (scrollState == 0)// scrolling stopped
					// {
					// if (view.getId() == gridView.getId()) {
					// final int currentFirstVisibleItem = gridView
					// .getFirstVisiblePosition();
					// if (currentFirstVisibleItem > mLastFirstVisibleItem)
					// {//scrolling down
					// mIsScrollingUp = true;
					// } else if (currentFirstVisibleItem <
					// mLastFirstVisibleItem) {//scrolling up
					// mIsScrollingUp = false;
					// }else if(currentFirstVisibleItem ==
					// mLastFirstVisibleItem){
					// return;
					// }
					//
					// mLastFirstVisibleItem = currentFirstVisibleItem;
					//
					// }
					// // this.isScrollCompleted(mIsScrollingUp);
					// }

				}

				@Override
				public void onScroll(TwoWayAbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					// this.currentFirstVisibleItem = firstVisibleItem;
					// this.currentVisibleItemCount = visibleItemCount;
					// this.totalItem = totalItemCount;

				}
			});
		}
		
//		mImageView = (ImageView) findViewById(R.id.genre_back_button);
//		mImageView.setImageResource(isGreenStyle ? R.drawable.station_list_return_button
//				: R.drawable.ivt_station_list_return_button);
//		mImageView.setBackground(mContext.getResources().getDrawable(
//				isGreenStyle ? R.drawable.station_list_return_button_bg : R.drawable.ivt_station_list_return_button_bg));
//
//		mImageView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});

	}

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
		super.onDestroy();
		if (mMyDrawer != null)
			mMyDrawer.finalize();
	}

	@Override
	protected void onResume() {
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
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if(event.getKeyCode() ==KeyEvent.KEYCODE_MENU){
			if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
				mDrawerLayout.openDrawer(Gravity.RIGHT);
			} else {
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
			}
		}
		return super.dispatchKeyEvent(event);
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

	// private OnItemClickListener onItemClickListener = new
	// OnItemClickListener() {
	//
	// @Override
	// public void onItemClick(AdapterView<?> parent, View v, int position,
	// long id) {
	//
	// if(mGenreGridAdapter != null){
	// //we have a Genre
	// Genre genre = (Genre) mGenreGridAdapter.getItem(position);
	// if (genre.getsList() != null && genre.getsList().size() > 0){
	// Intent i = new Intent(TelepadGenreActivity.this,
	// MediaPlayerService.class);
	// i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
	// i.putExtra(getString(R.string.radiothek_bundle_station),
	// genre.getsList().get(0));
	// startService(i);
	// } else {
	// Toast.makeText(TelepadGenreActivity.this,
	// getString(R.string.genreactivity_text_nostationavailable),
	// Toast.LENGTH_SHORT).show();
	// }
	// return;
	// }
	//
	// if (mCountryGridAdapter != null){
	// //we have a Country
	// Country country = (Country) mCountryGridAdapter.getItem(position);;
	// if (country.getsList() != null && country.getsList().size() > 0){
	// Intent i = new Intent(TelepadGenreActivity.this,
	// MediaPlayerService.class);
	// i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
	// i.putExtra(getString(R.string.radiothek_bundle_station),
	// country.getsList().get(0));
	// startService(i);
	// } else {
	// Toast.makeText(TelepadGenreActivity.this,
	// getString(R.string.genreactivity_text_nostationavailable),
	// Toast.LENGTH_SHORT).show();
	// }
	// return;
	// }
	// }
	// };

	@Override
	public void onItemClick(TwoWayAdapterView<?> parent, View view, int position, long id) {
		/**
		 * This click event is for navigator to control the App
		 */
		if (mGenreGridAdapter != null) {
			// we have a Genre
			Genre g = (Genre) mGenreGridAdapter.getItem(position);
			Intent i = new Intent(mContext, TelepadDisplayStationListActivity.class);
			i.putExtra(mContext.getString(R.string.radiothek_bundle_stationlistparcelable), new StationList(g.getsList()));
			i.setAction(mContext.getString(R.string.radiothek_action_morestationsofgenre));
			i.putExtra(mContext.getString(R.string.radiothek_bundle_long), g.getId());
			i.putExtra(mContext.getString(R.string.radiothek_bundle_int), 500);
			mContext.startActivity(i);
			return;
		}

		if (mCountryGridAdapter != null) {
			// we have a Country
			Country c = (Country) mCountryGridAdapter.getItem(position);
			Intent i = new Intent(mContext, TelepadDisplayStationListActivity.class);
			i.putExtra(mContext.getString(R.string.radiothek_bundle_stationlistparcelable), new StationList(c.getsList()));
			i.setAction(mContext.getString(R.string.radiothek_action_morestationsofcountry));
			i.putExtra(mContext.getString(R.string.radiothek_bundle_string), c.getIsoCode());
			i.putExtra(mContext.getString(R.string.radiothek_bundle_int), 500);

			mContext.startActivity(i);
			return;
		}

	}

}
