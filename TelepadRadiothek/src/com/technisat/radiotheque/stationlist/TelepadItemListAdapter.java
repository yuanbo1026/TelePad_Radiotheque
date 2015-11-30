package com.technisat.radiotheque.stationlist;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.technisat.telepadradiothek.R;
import com.technisat.radiotheque.android.DatabaseHandler;
import com.technisat.radiotheque.android.ImageLoaderSpinner;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.entity.IStationMetadataUpdate;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.service.MediaPlayerService;
import com.technisat.radiotheque.station.StationActivity;
import com.technisat.radiotheque.station.util.Globals;

public class TelepadItemListAdapter extends ArrayAdapter<Station> implements IStationMetadataUpdate {

	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	private DatabaseHandler mDBhandler;
	private Context mContext;
	private List<Station> mStationList;
	private boolean mIsFavList;
	private int mLayoutId;
	private boolean isGreenStyle;
	private boolean isSizeBig;

	public TelepadItemListAdapter(Context context, int textViewResourceId, List<Station> objects, boolean isFavList, int layoutId) {
		super(context, textViewResourceId, objects);
		mInflater = LayoutInflater.from(context);
		mImageLoader = ImageLoader.getInstance();
		mDBhandler = new DatabaseHandler(context);
		mContext = context;
		mStationList = new ArrayList<Station>(objects);
		mIsFavList = isFavList;
		mLayoutId = layoutId;
		isGreenStyle = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("style", true);
		isSizeBig = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("size", true);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Station s = getItem(position);
		s.updateMetaData();
		s.addMetadataListener(this);

		if (mDBhandler != null) {
			s.setFav(mDBhandler.isFav(s));
		}

		Item item = null;
		View v = convertView;
		

		if (v == null) {
			v = mInflater.inflate(isSizeBig?R.layout.telepad_stationlist_item:R.layout.telepad_stationlist_item_small, parent, false);
			v.setBackground(mContext.getResources().getDrawable(
					isGreenStyle ? R.drawable.station_list_item_bg_seletor : R.drawable.ivt_station_list_item_bg_seletor));
			item = new Item();
			item.ll = (LinearLayout) v.findViewById(R.id.telepad_tv_stationlist_item_linearlayout);
			item.ivFavIcon = (ImageView) v.findViewById(R.id.telepad_iv_stationlist_item_favicon);
			item.tvStationGenre = (TextView) v.findViewById(R.id.telepad_tv_stationlist_item_stationgenre);
			item.tvStationName = (TextView) v.findViewById(R.id.telepad_tv_stationlist_item_stationname);
			item.tvStationGenre.setTypeface(Misc.getCustomFont(mContext, Misc.FONT_NORMAL));
			item.tvStationName.setTypeface(Misc.getCustomFont(mContext, Misc.FONT_NORMAL));
			v.setTag(item);
		} else {
			item = (Item) v.getTag();
		}

		item.ll.setBackground(mContext.getResources().getDrawable(
				isGreenStyle ? R.drawable.station_list_item_bg_seletor : R.drawable.ivt_station_list_item_bg_seletor));
		item.ivFavIcon.setClickable(true);
		if (s.isFav()) {
			item.ivFavIcon.setImageResource(isGreenStyle ? R.drawable.station_list_item_icon_fav_h
					: R.drawable.ivt_station_list_item_icon_fav_h);
		} else {
			item.ivFavIcon.setImageResource(isGreenStyle ? R.drawable.station_list_item_icon_fav
					: R.drawable.ivt_station_list_item_icon_fav);
		}

		if (Misc.isValidMetaData(s.getMetadata()))
			item.tvStationGenre.setText(s.getMetadata());
		else
			item.tvStationGenre.setText("");
		item.tvStationName.setText(s.getStationName());

		item.tvStationName.setTextColor(mContext.getResources().getColorStateList(
				isGreenStyle ? R.drawable.general_text_color : R.drawable.ivt_general_text_color));
		item.tvStationGenre.setTextColor(mContext.getResources().getColorStateList(
				isGreenStyle ? R.color.station_detail_text_color : R.color.ivt_station_detail_text_color));

		if (s != null) {
			final Station sFinal = s;
			item.ivFavIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					toggleFavStatus(sFinal);
//					if(Globals.current_station.getId() == sFinal.getId())
//						Globals.current_station = sFinal;
					if (mIsFavList && !sFinal.isFav()) {
						remove(sFinal);
					}
				}
			});
		}

		return v;
	}

	@Override
	public int getCount() {
		return mStationList.size();
	}

	@Override
	public Station getItem(int position) {
		return mStationList.get(position);
	}

	@Override
	public int getPosition(Station item) {
		return mStationList.indexOf(item);
	}

	@Override
	public void remove(Station object) {
		mStationList.remove(object);
		notifyDataSetChanged();
		super.remove(object);
	}

	private static class Item {
		TextView tvStationName;
		TextView tvStationGenre;
		ImageView ivFavIcon;
		LinearLayout ll;
	}

	public void toggleFavStatus(Station station) {
		for (Station s : mStationList) {
			if (s.getId() == station.getId()) {
				s.setFav(!mDBhandler.isFav(s));
				mDBhandler.insertStation(s);
				break;
			}
		}
		notifyDataSetChanged();
	}

	public void showPauseButton(Station station) {
		for (Station s : mStationList) {
			if (s.getId() == station.getId()) {
				s.setPlaying(true);
			}
		}
		notifyDataSetChanged();
	}

	public void hidePauseButton(Station station) {
		for (Station s : mStationList) {
			if (s.getId() == station.getId()) {
				s.setPlaying(false);
			}
		}
		notifyDataSetChanged();
	}

	/* Interface IStationMetaUpdate */

	@Override
	public void onMetadataUpdate(Station station) {
		notifyDataSetChanged();
	}
}
