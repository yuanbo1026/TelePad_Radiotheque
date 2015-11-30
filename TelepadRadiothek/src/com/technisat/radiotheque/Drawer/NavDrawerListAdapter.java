package com.technisat.radiotheque.Drawer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnGenericMotionListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.technisat.radiotheque.android.Misc;
import com.technisat.telepadradiothek.R;

public class NavDrawerListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private ArrayList<NavDrawerItem> navDrawerItemsVisible = new ArrayList<NavDrawerItem>();
	private boolean isGreenStyle;

	public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems) {
		this.context = context;
		this.navDrawerItems = navDrawerItems;

		for (NavDrawerItem item : this.navDrawerItems) {
			if (item.isVisible()) {
				this.navDrawerItemsVisible.add(item);
			}
		}
	}

	@Override
	public int getCount() {
		return navDrawerItemsVisible.size();
	}

	@Override
	public Object getItem(int position) {
		return navDrawerItemsVisible.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setItemVisibility(int id, boolean isVisible) {
		navDrawerItemsVisible.clear();
		for (NavDrawerItem item : navDrawerItems) {
			if (item.getId() == id) {
				item.setVisiblity(isVisible);
			}

			if (item.isVisible()) {
				navDrawerItemsVisible.add(item);
			}
		}
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.drawer_list_item, null);
		}
		RelativeLayout mRelativeLayout = (RelativeLayout) convertView;
		final ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
		final TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
		LinearLayout mLinearLayout = (LinearLayout) mRelativeLayout.findViewById(R.id.telepad_drawer_item_linearlayout);
		View divider = (View)convertView.findViewById(R.id.drawer_divider);
		boolean isGreenStyle = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("style", true);
		
		mRelativeLayout.setBackground(context.getResources().getDrawable(isGreenStyle ? R.color.drawer_listview_bg : R.color.ivt_drawer_listview_bg));
		mLinearLayout.setBackground(context.getResources().getDrawable(isGreenStyle ? R.drawable.drawer_listview_item_bg: R.drawable.ivt_drawer_listview_item_bg));
		divider.setBackground(context.getResources().getDrawable(isGreenStyle?R.color.drawer_listview_bg:R.color.ivt_drawer_listview_bg));
		txtTitle.setTextColor(context.getResources().getColorStateList(
				isGreenStyle ? R.drawable.drawer_listview_item_home_text_color
						: R.drawable.ivt_drawer_listview_item_home_text_color));
		txtTitle.setTypeface(Misc.getCustomFont(context, Misc.FONT_LIGHT));
		imgIcon.setImageResource(navDrawerItemsVisible.get(position).getIcon());
		txtTitle.setText(navDrawerItemsVisible.get(position).getTitle());

		mRelativeLayout.setOnGenericMotionListener(new OnGenericMotionListener() {
			@Override
			public boolean onGenericMotion(View v, MotionEvent event) {
				return false;
			}
		});

		return convertView;
	}

	private View findViewById(int drawerDivider) {
		// TODO Auto-generated method stub
		return null;
	}

}