package com.technisat.radiotheque.about;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.technisat.radiotheque.Drawer.MyDrawer;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.menu.ActionBarMenuHandler;
import com.technisat.radiotheque.station.util.Globals;
import com.technisat.telepadradiothek.R;

/**
 * Displays the About Screen
 *
 */
public class AboutActivity extends Activity {

	private MyDrawer mMyDrawer;
	private DrawerLayout mDrawerLayout;
	private Boolean isGreenStyle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isGreenStyle = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("style", true);
		setTheme(isGreenStyle ? R.style.GreenTheme : R.style.InvertTheme);
		setContentView(com.technisat.telepadradiothek.R.layout.drawer_layout_container);
		Misc.initUIL(getApplicationContext());
		getActionBar().setIcon(isGreenStyle ? R.drawable.logo_actionbar : R.drawable.ivt_logo_actionbar);

		mMyDrawer = new MyDrawer(this);
		mDrawerLayout = mMyDrawer.getDrawerLayout();

		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout container = (RelativeLayout) findViewById(R.id.frame_container);
		inflater.inflate(R.layout.activity_about, container);

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.about_rl);
		View divider = (View) findViewById(R.id.about_divider);
		TextView caption = (TextView) findViewById(R.id.tv_activity_about_caption);
		TextView about = (TextView) findViewById(R.id.tv_activity_about_text);
		caption.setTypeface(Misc.getCustomFont(this, Misc.FONT_BOLD));
		about.setTypeface(Misc.getCustomFont(this, Misc.FONT_NORMAL));

		layout.setBackground(this.getResources().getDrawable(isGreenStyle ? R.color.RadiothekBlueDark : R.color.ivt_detail_bg));
		divider.setBackground(this.getResources().getDrawable(
				isGreenStyle ? R.color.RadiothekBlueLight : R.color.ivt_general_text_color));
		caption.setTextColor(this.getResources().getColorStateList(
				isGreenStyle ? R.color.RadiothekBlueLight : R.color.ivt_Grid_item_bg_h));
		about.setTextColor(this.getResources().getColorStateList(
				isGreenStyle ? R.color.RadiothekGrey : R.color.ivt_general_text_color));

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
			if (KeyEvent.KEYCODE_MENU == keyCode) {
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(
				PreferenceManager.getDefaultSharedPreferences(this).getBoolean("style", true) ? R.menu.menu_new
						: R.menu.ivt_menu_new, menu);

		if (!Globals.isPlaying) {
			MenuItem item = menu.findItem(R.id.action_current_play);
			item.setVisible(false);
			// MenuItem item_divider = menu.findItem(R.id.action_divider);
			// item_divider.setVisible(false);
			this.invalidateOptionsMenu();
		}

		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

		return true;
	}

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

	/**
	 * Handles Banner Click and starts Website with URI
	 * 
	 * @param view
	 */
	public void bannerClick(View view) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.techniplus.de"));
		startActivity(browserIntent);
	}
}