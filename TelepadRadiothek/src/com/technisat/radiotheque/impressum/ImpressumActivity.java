package com.technisat.radiotheque.impressum;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
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

public class ImpressumActivity extends Activity {
	
	private MyDrawer mMyDrawer;
	private DrawerLayout mDrawerLayout;
	private Boolean isGreenStyle;
	
	private RelativeLayout layout;
	private TextView intro_text;
	private View divider;
	private TextView imprint_content;
	
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
    	inflater.inflate(R.layout.activity_impressum, container);
    	
    	layout = (RelativeLayout)findViewById(R.id.imprint_layout);
    	intro_text = (TextView)findViewById(R.id.intro_text);
    	divider = (View)findViewById(R.id.imprint_divider);
    	imprint_content = (TextView)findViewById(R.id.imprint_content);
    	
    	layout.setBackground(this.getResources().getDrawable(isGreenStyle?R.color.RadiothekBlueDark:R.color.ivt_detail_bg));
    	divider.setBackground(this.getResources().getDrawable(isGreenStyle?R.color.RadiothekBlueLight:R.color.ivt_general_text_color));
    	intro_text.setTextColor(this.getResources().getColorStateList(
				isGreenStyle ? R.color.RadiothekBlueLight : R.color.ivt_Grid_item_bg_h));
    	imprint_content.setTextColor(this.getResources().getColorStateList(
				isGreenStyle ? R.color.RadiothekGrey : R.color.ivt_general_text_color));
    	
	}
	
	@Override
	protected void onDestroy() {
		if (mMyDrawer != null)
			mMyDrawer.finalize();
		super.onDestroy();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(
				PreferenceManager.getDefaultSharedPreferences(this).getBoolean("style", true) ? R.menu.menu_new
						: R.menu.ivt_menu_new, menu);
//		if(!Globals.isPlaying){
//			MenuItem item = menu.findItem(R.id.action_current_play);
//			item.setVisible(false);
//			MenuItem item_divider = menu.findItem(R.id.action_divider);
//			item_divider.setVisible(false);
//			this.invalidateOptionsMenu();
//		}
	    
		// Associate searchable configuration with the SearchView
	    SearchManager searchManager =
	           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView =
	            (SearchView) menu.findItem(R.id.search).getActionView();
	    searchView.setSearchableInfo(
	            searchManager.getSearchableInfo(getComponentName()));
	    
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar actions click
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