package com.technisat.radiotheque.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import com.technisat.radiotheque.Drawer.MyDrawer;
import com.technisat.radiotheque.android.DatabaseHandler;
import com.technisat.radiotheque.constants.FileStorageHelper;
import com.technisat.radiotheque.constants.Nexxoo;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.entity.StationList;
import com.technisat.radiotheque.menu.ActionBarMenuHandler;
import com.technisat.radiotheque.searchview.dialog.SearchViewDialog;
import com.technisat.radiotheque.service.StationService;
import com.technisat.radiotheque.station.util.Globals;
import com.technisat.radiotheque.stationlist.TelepadDisplayStationListActivity;
import com.technisat.radiotheque.update.OnUpdateResult;
import com.technisat.radiotheque.update.UpdateDialog;
import com.technisat.radiotheque.webservice.NexxooWebservice;
import com.technisat.radiotheque.webservice.OnJSONResponse;
import com.technisat.telepadradiothek.R;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TelepadMainActivity extends Activity implements com.jess.ui.TwoWayAdapterView.OnItemClickListener {
	private MyDrawer mMyDrawer;
	private DrawerLayout mDrawerLayout;
	private ListView mList;

	private TelepadGridViewAdapter horzGridViewAdapter;
	private Context mContext;

	public static TwoWayGridView horzGridView;

	// Screen dims
	public final static int COLUMN_PORT = 0;
	public final static int COLUMN_LAND = 1;
	public static int column_selected;
	public static int[] displayWidth;
	public static int[] displayHeight;
	private Boolean isGreenStyle;

	private int currentVersion, lastestVersion;
	private static UpdateDialog dialog;
	private static ProgressBar mProgressSpinner;
	/**
	 * context for update functionality
	 */
	private static int contentType = 4;
	private static String fileName = "TelepadRadiothek.apk";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isGreenStyle = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("style", true);
		setTheme(isGreenStyle ? R.style.GreenTheme : R.style.InvertTheme);
		getActionBar().setIcon(isGreenStyle ? R.drawable.logo_actionbar : R.drawable.ivt_logo_actionbar);
		setContentView(R.layout.drawer_layout_container);
		mContext = this;
		init();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Long next_update_time = prefs.getLong(this.getString(R.string.radiothek_bundle_next_update_time), 0);
		Calendar currentTime = Calendar.getInstance();
		long seconds = currentTime.getTimeInMillis();
		if (next_update_time < seconds)
			checkVersionCode();
		// else
		// Toast.makeText(mContext, "update later", Toast.LENGTH_LONG).show();

	}

	private void init() {
		mMyDrawer = new MyDrawer(this);
		mDrawerLayout = mMyDrawer.getDrawerLayout();

		// getActionBar().setDisplayHomeAsUpEnabled(false);
		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout container = (RelativeLayout) findViewById(R.id.frame_container);
		inflater.inflate(R.layout.activity_main_two_way_grid, container);

		horzGridView = (TwoWayGridView) findViewById(R.id.horz_gridview);
		horzGridView.setPadding(0, 0, 0, 0);
		List<DataObject> horzData = generateGridViewObjects();

		horzGridViewAdapter = new TelepadGridViewAdapter(mContext, horzData);
		mList = (ListView) findViewById(R.id.list_slidermenu);

		horzGridView.setAdapter(horzGridViewAdapter);
		horzGridView.setOnItemClickListener(this);
		// horzGridView.requestFocus();
		mDrawerLayout.getChildAt(0).requestFocus();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(Gravity.RIGHT);
		Boolean isMenuListFocused = false;
		if (mList != null) {
			isMenuListFocused = mList.hasFocus();
		}
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
		if (mMyDrawer != null)
			mMyDrawer.finalize();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		this.invalidateOptionsMenu();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		isGreenStyle = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("style", true);
		getMenuInflater().inflate(isGreenStyle ? R.menu.menu_new : R.menu.ivt_menu_new, menu);

		if (!Globals.isPlaying) {
			MenuItem item = menu.findItem(R.id.action_current_play);
			item.setVisible(false);
			// MenuItem item_divider = menu.findItem(R.id.action_divider);
			// item_divider.setVisible(false);
			this.invalidateOptionsMenu();
		}

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setOnSearchClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
				Boolean isImmer = prefs.getBoolean(mContext.getString(R.string.searchview_checkbox_key), true);
				if (!isImmer) {
					// Toast.makeText(mContext, "not isImmer",
					// Toast.LENGTH_LONG).show();
				} else {
					SearchViewDialog dialog = new SearchViewDialog(mContext);
					dialog.show();
					// Toast.makeText(mContext, "isImmer",
					// Toast.LENGTH_LONG).show();
				}
			}

		});
		return true;
	}

	private List<DataObject> generateGridViewObjects() {

		List<DataObject> allData = new ArrayList<DataObject>();
		DataObject history;
		DataObject favs;
		DataObject genre;
		DataObject country;
		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("style", true)) {
			history = new DataObject(getString(R.string.mainactivity_button_history), R.drawable.home_gridview_item_icon_gehoert,
					R.drawable.home_gridview_item_bg_color_gehoert, R.drawable.home_gridview_item_text_color_selector);
			favs = new DataObject(getString(R.string.mainactivity_button_favs), R.drawable.home_gridview_item_icon_favs,
					R.drawable.home_gridview_item_bg_color_favs, R.drawable.home_gridview_item_text_color_selector);
			genre = new DataObject(getString(R.string.mainactivity_button_genre), R.drawable.home_gridview_item_icon_genre,
					R.drawable.home_gridview_item_bg_color_genre, R.drawable.home_gridview_item_text_color_selector);
			country = new DataObject(getString(R.string.mainactivity_button_country), R.drawable.home_gridview_item_icon_country,
					R.drawable.home_gridview_item_bg_color_country, R.drawable.home_gridview_item_text_color_selector);
		} else {
			history = new DataObject(getString(R.string.mainactivity_button_history),
					R.drawable.ivt_home_gridview_item_icon_gehoert, R.drawable.ivt_home_gridview_item_bg_color_gehoert,
					R.drawable.ivt_home_gridview_item_text_color_selector);
			favs = new DataObject(getString(R.string.mainactivity_button_favs), R.drawable.ivt_home_gridview_item_icon_favs,
					R.drawable.ivt_home_gridview_item_bg_color_favs, R.drawable.ivt_home_gridview_item_text_color_selector);
			genre = new DataObject(getString(R.string.mainactivity_button_genre), R.drawable.ivt_home_gridview_item_icon_genre,
					R.drawable.ivt_home_gridview_item_bg_color_genre, R.drawable.ivt_home_gridview_item_text_color_selector);
			country = new DataObject(getString(R.string.mainactivity_button_country),
					R.drawable.ivt_home_gridview_item_icon_country, R.drawable.ivt_home_gridview_item_bg_color_country,
					R.drawable.ivt_home_gridview_item_text_color_selector);
		}

		allData.add(history);
		allData.add(favs);
		allData.add(genre);
		allData.add(country);
		return allData;
	}

	@Override
	public void onItemClick(TwoWayAdapterView<?> parent, View view, int position, long id) {
		DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());
		List<Station> stationList;
		StationList sList;
		Log.d("MainActivity Click event", "item :" + position);
		Intent i;
		switch (position) {
		case 0:// history
			stationList = dbHandler.getAllHistoryStations();
			sList = new StationList(stationList);
			i = new Intent(TelepadMainActivity.this, TelepadDisplayStationListActivity.class);
			i.setAction(getString(R.string.radiothek_action_history));
			i.putExtra(getString(R.string.radiothek_bundle_stationlistparcelable), sList);
			startActivity(i);
			break;
		case 2:// genre
			i = new Intent(TelepadMainActivity.this, StationService.class);
			i.setAction(getString(R.string.radiothek_stationservice_startgenreactivity));
			startService(i);
			break;
		case 3:// country
			i = new Intent(TelepadMainActivity.this, StationService.class);
			i.setAction(getString(R.string.radiothek_stationservice_startcountryactivity));
			startService(i);
			break;
		case 1:// favs
			stationList = dbHandler.getAllFavoriteStations();
			sList = new StationList(stationList);
			i = new Intent(TelepadMainActivity.this, TelepadDisplayStationListActivity.class);
			i.setAction(getString(R.string.radiothek_action_favorite));
			i.putExtra(getString(R.string.radiothek_bundle_stationlistparcelable), sList);
			startActivity(i);
			break;
		}

	}

	private void checkVersionCode() {
		/**
		 * check the latest version code from database by calling web-service
		 */
		NexxooWebservice.getLatestVersionCode(true, new OnJSONResponse() {

			@Override
			public void onReceivedJSONResponse(JSONObject json) {
				try {
					int versioncode = json.getInt("version");
//					Log.e(Nexxoo.TAG, "Get version from Database: " + versioncode);
					lastestVersion = versioncode;
					/**
					 * check current version code
					 */
					PackageInfo pinfo;
					try {
						pinfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
						int versionCode = pinfo.versionCode;
						String versionName = pinfo.versionName;
//						Log.e(Nexxoo.TAG, "Version Code: " + versionCode);
//						Log.e(Nexxoo.TAG, "Version Name: " + versionName);
						currentVersion = versionCode;
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					/**
					 * get current version code to make sure the textview on
					 * layout has content to display
					 */

					if (lastestVersion > currentVersion) {
						Update(currentVersion, lastestVersion);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onReceivedError(String msg, int code) {
				Toast.makeText(mContext.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
				Log.e(Nexxoo.TAG, msg);
			}
		});
	}

	private void Update(int currentVersion, int lastestVersion) {

		dialog = new UpdateDialog(mContext, Integer.toString(currentVersion), Integer.toString(lastestVersion),
				new UpdateDialog.UpdateActionListener() {
					@Override
					public void onSend(final UpdateDialog dialog, Boolean updateble) {
						if (updateble) {
							List<NameValuePair> mAdditionalParams = new ArrayList<NameValuePair>();
							mAdditionalParams.add(new BasicNameValuePair("requesttask", Integer
									.toString(NexxooWebservice.WEBTASK_GETUPDATE)));
							NexxooWebservice.performUpdate(mContext, mAdditionalParams, getCallback(mContext, false));

							TextView tv_update_title = (TextView) dialog.findViewById(R.id.tv_update_title);
							tv_update_title.setText("Bitte warten....");

							mProgressSpinner = (ProgressBar) dialog.findViewById(R.id.update_spinner);
							mProgressSpinner.setIndeterminate(true);
							mProgressSpinner.setVisibility(View.VISIBLE);

							/**
							 * set update button not clickable while downloading
							 * the apk file
							 */
							Button upt_button = (Button) dialog.findViewById(R.id.btn_update);
							upt_button.setClickable(false);

						} else {

							Calendar currentTime = Calendar.getInstance();
							long seconds = currentTime.getTimeInMillis();
							// long nextTime = seconds +24*60*60;
							long nextTime = seconds + 24 * 60 * 60 * 1000;
							SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
							SharedPreferences.Editor editor = prefs.edit();
							editor.putLong(mContext.getString(R.string.radiothek_bundle_next_update_time), nextTime);
							editor.commit();
							dialog.dismiss();
						}
					}

				});
		dialog.show();
	}

	public static OnUpdateResult getCallback(final Context mContext, boolean reattach) {
		return new OnUpdateResult() {

			@Override
			public void onUpdateSuccess() {
				if (mContext != null) {
					mProgressSpinner.setIndeterminate(false);
					mProgressSpinner.setVisibility(View.INVISIBLE);
					dialog.dismiss();
					File file = new File(FileStorageHelper.getDownloadFolder(mContext) + contentType + File.separator + fileName);
					Intent intentForPrepare = null;

					intentForPrepare = new Intent(Intent.ACTION_VIEW);
					intentForPrepare.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
					intentForPrepare.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(intentForPrepare);
				}

			}
		};
	}

	// @Override
	// public boolean dispatchKeyEvent(KeyEvent event) {
	// // TODO Auto-generated method stub
	// if(event.getKeyCode() ==KeyEvent.KEYCODE_MENU){
	// if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
	// mDrawerLayout.openDrawer(Gravity.RIGHT);
	// } else {
	// mDrawerLayout.closeDrawer(Gravity.RIGHT);
	// }
	// }
	// return super.dispatchKeyEvent(event);
	// }

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

}
