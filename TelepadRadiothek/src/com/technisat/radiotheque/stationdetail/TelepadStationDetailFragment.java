package com.technisat.radiotheque.stationdetail;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.technisat.radiotheque.android.DatabaseHandler;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.entity.StationList;
import com.technisat.radiotheque.genre.SquareImageView;
import com.technisat.radiotheque.main.TelepadMainActivity;
import com.technisat.radiotheque.service.MediaPlayerService;
import com.technisat.radiotheque.station.IFullscreenListener;
import com.technisat.radiotheque.station.util.Globals;
import com.technisat.telepadradiothek.R;

import java.util.List;

public class TelepadStationDetailFragment extends Fragment implements IFullscreenListener, OnClickListener{

	private ImageLoader mImageLoader;
	private RelativeLayout mPlayer;

	private LinearLayout mInformation;
	private LinearLayout mFunction;

	private ImageView mIvPlayButton;
	private ImageView mIvForwardButton;
	private ImageView mIvBackButton;

	private ImageView mIvBuyIcon;
	private ImageView mIvFavIcon;
	private ImageView mIvHomeIcon;
	private ImageView mIvReturnIcon;

	private TextView mTvStationName;
	private TextView mTvStationGenre;

	private RelativeLayout rl_stationdetail;
	private RelativeLayout rl_fullscreen;

	private ImageView mFullscreenCover;
	private TextView mFullscreenStationName;
	private TextView mFullscreenStationGenre;

	private Station mCurrentStation;
	private StationList sList;

	private ProgressBar mSpinner;
	private SquareImageView mCover;
	private ImageView logo;

	private OnStationDetailListener mListener;
	private Boolean isFullscreen = false;
	private boolean isGreenStyle;
	private Context mContext;
	
	private Window window;
	
	private Handler mHandler;
	private Runnable mRunnable;

	public Boolean getisFullscreen() {
		return isFullscreen;
	}

	public void setisFullscreen(Boolean iFullscreen) {
		this.isFullscreen = iFullscreen;
	}

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @return A new instance of fragment StationDetailFragment.
	 */
	public static TelepadStationDetailFragment newInstance(Context ctx, Station station) {
		TelepadStationDetailFragment fragment = new TelepadStationDetailFragment();
		Bundle args = new Bundle();
		args.putParcelable(ctx.getString(R.string.radiothek_bundle_station), station);
		fragment.setArguments(args);
		return fragment;
	}

	public TelepadStationDetailFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImageLoader.getInstance();
		Intent intent = getActivity().getIntent();
		if (intent.hasExtra(getString(R.string.radiothek_bundle_station))) {
			mCurrentStation = intent.getParcelableExtra(getString(R.string.radiothek_bundle_station));
			sList = intent.getParcelableExtra(getString(R.string.radiothek_bundle_stationlistparcelable));
			Globals.current_station = mCurrentStation;
			Globals.current_station_list = sList;
		} else if (Globals.current_station != null && Globals.current_station_list != null) {
			mCurrentStation = Globals.current_station;
			sList = Globals.current_station_list;
		}
		
		mImageLoader = ImageLoader.getInstance();
		mContext = getActivity();
		
		window = ((Activity) mContext).getWindow();
		
		mHandler = new Handler();
		mRunnable = new Runnable() {
			public void run() {
				LayoutParams layoutpars = window.getAttributes();
				layoutpars.screenBrightness = 20 / (float)255;
				window.setAttributes(layoutpars);
			}
		};
	}

	public void setNewStation(Station station) {
		// TODO Update UI for both StationActivity and StationDetailFragment
		if (station != null) {
			mCurrentStation = station;
			Globals.current_station = station;
			initUI();
			if (mListener != null) {
				mListener.onNewStation(station);
			}

		}
	}

	private void setComponentResource(boolean isGreenStyle) {
		// mPlayer
		// mFunction
		mFunction.setBackground(mContext.getResources().getDrawable(isGreenStyle ? R.color.detail_bg : R.color.ivt_detail_bg));
		// mInformation
		//
		mIvPlayButton.setBackground(mContext.getResources().getDrawable(
				isGreenStyle ? R.color.station_detail_play_button_bg : R.color.ivt_station_detail_play_button_bg));
		mIvPlayButton.setImageResource(isGreenStyle ? R.drawable.detail_icon_button_play_icon
				: R.drawable.ivt_detail_icon_button_play_icon);
		// mIvForwardButton
		mIvForwardButton.setBackground(mContext.getResources().getDrawable(
				isGreenStyle ? R.color.station_detail_forward_button_bg : R.color.ivt_station_detail_forward_button_bg));
		mIvForwardButton.setImageResource(isGreenStyle ? R.drawable.detail_icon_button_playforward_icon
				: R.drawable.ivt_detail_icon_button_playforward_icon);
		// mIvBackButton
		mIvBackButton.setBackground(mContext.getResources().getDrawable(
				isGreenStyle ? R.color.station_detail_forward_button_bg : R.color.ivt_station_detail_forward_button_bg));
		mIvBackButton.setImageResource(isGreenStyle ? R.drawable.detail_icon_button_playback_icon
				: R.drawable.ivt_detail_icon_button_playback_icon);
		//
		// mIvBuyIcon
		mIvBuyIcon.setBackground(mContext.getResources().getDrawable(
				isGreenStyle ? R.drawable.detail_icon_button_bg : R.drawable.ivt_detail_icon_button_bg));
		mIvBuyIcon.setImageResource(isGreenStyle ? R.drawable.detail_icon_button_basket_bg
				: R.drawable.ivt_detail_icon_button_basket_bg);
		// mIvFavIcon
		mIvFavIcon.setBackground(mContext.getResources().getDrawable(
				isGreenStyle ? R.drawable.detail_icon_button_bg : R.drawable.ivt_detail_icon_button_bg));
		mIvFavIcon.setImageResource(isGreenStyle ? R.drawable.detail_icon_button_fav_bg
				: R.drawable.ivt_detail_icon_button_fav_bg);
		// mIvHomeIcon
		mIvHomeIcon.setBackground(mContext.getResources().getDrawable(
				isGreenStyle ? R.drawable.detail_icon_button_bg : R.drawable.ivt_detail_icon_button_bg));
		mIvHomeIcon.setImageResource(isGreenStyle ? R.drawable.detail_icon_button_home_bg
				: R.drawable.ivt_detail_icon_button_home_bg);
		// mIvReturnIcon
		mIvReturnIcon.setBackground(mContext.getResources().getDrawable(
				isGreenStyle ? R.drawable.detail_icon_button_bg : R.drawable.ivt_detail_icon_button_bg));
		mIvReturnIcon.setImageResource(isGreenStyle ? R.drawable.detail_icon_button_back_bg
				: R.drawable.ivt_detail_icon_button_back_bg);
		//
		// mTvStationName
		mTvStationName.setTextColor(mContext.getResources().getColorStateList(
				isGreenStyle ? R.drawable.general_text_color : R.drawable.ivt_general_text_color));
		// mTvStationGenre
		mTvStationGenre.setTextColor(mContext.getResources().getColorStateList(
				isGreenStyle ? R.color.station_detail_text_color : R.color.ivt_station_detail_text_color));
		//
		// rl_stationdetail
		rl_stationdetail.setBackground(mContext.getResources().getDrawable(
				isGreenStyle ? R.color.detail_bg : R.color.ivt_detail_bg));
		// rl_fullscreen
		rl_fullscreen
				.setBackground(mContext.getResources().getDrawable(isGreenStyle ? R.color.detail_bg : R.color.ivt_detail_bg));
		//
		// mFullscreenStationName
		mFullscreenStationName.setTextColor(mContext.getResources().getColorStateList(
				isGreenStyle ? R.drawable.general_text_color : R.drawable.ivt_general_text_color));
		// mFullscreenStationGenre
		mFullscreenStationGenre.setTextColor(mContext.getResources().getColorStateList(
				isGreenStyle ? R.color.station_detail_text_color : R.color.ivt_station_detail_text_color));

		logo.setBackground(mContext.getResources().getDrawable(isGreenStyle ? R.color.detail_bg : R.color.ivt_detail_bg));
		logo.setImageResource(isGreenStyle ? R.drawable.logo_player : R.drawable.ivt_logo_player);
		
		mTvStationName.setTypeface(Misc.getCustomFont(getActivity(), Misc.FONT_NORMAL));
		mTvStationGenre.setTypeface(Misc.getCustomFont(getActivity(), Misc.FONT_NORMAL));
		mFullscreenStationName.setTypeface(Misc.getCustomFont(getActivity(), Misc.FONT_NORMAL));
		mFullscreenStationGenre.setTypeface(Misc.getCustomFont(getActivity(), Misc.FONT_NORMAL));
		
		setFavIcon(mCurrentStation.isFav());
		setPlayButton(mCurrentStation.isPlaying());
	}

	private void initUI() {
		if (mCurrentStation != null) {
			setComponentResource(isGreenStyle);
			
			mIvForwardButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int index = 0;
					List<Station> stationList = sList.getStationList();
					for (Station s : stationList) {
						if (mCurrentStation != null)
							if (s.getId() == mCurrentStation.getId()) {
								index = stationList.indexOf(s);
							}
					}
					if (index + 1 <= (stationList.size() - 1) && mCurrentStation != null) {
						Intent i = new Intent(getActivity(), MediaPlayerService.class);
						i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
						i.putExtra(getString(R.string.radiothek_bundle_station), stationList.get(index + 1));
						getActivity().startService(i);

					}
				}
			});
			mIvBackButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int index = 0;
					List<Station> stationList = sList.getStationList();
					for (Station s : stationList) {
						if (mCurrentStation != null)
							if (s.getId() == mCurrentStation.getId()) {
								index = stationList.indexOf(s);
							}
					}
					if (index - 1 >= 0 && mCurrentStation != null) {
						Intent i = new Intent(getActivity(), MediaPlayerService.class);
						i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
						i.putExtra(getString(R.string.radiothek_bundle_station), stationList.get(index - 1));
						getActivity().startService(i);

					}
				}
			});
			mIvPlayButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onPlayButtonPressed(mCurrentStation);
				}
			});
			mIvFavIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mCurrentStation.setFav(!mCurrentStation.isFav());
					DatabaseHandler dbhandler = new DatabaseHandler(getActivity());
					dbhandler.insertStation(mCurrentStation);
					/**
					 * refresh current station status to globals current station
					 */
					if (Globals.isPlaying && Globals.current_station.getId() == mCurrentStation.getId())
						Globals.current_station = mCurrentStation;
					setFavIcon(mCurrentStation.isFav());
				}
			});
			mIvHomeIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!getActivity().getClass().equals(TelepadMainActivity.class)) {
						Intent intent = new Intent(getActivity(), TelepadMainActivity.class);
						getActivity().startActivity(intent);
					}
				}
			});

			if (Misc.isValidMetaData(mCurrentStation.getMetadata())) {
				mTvStationGenre.setText(mCurrentStation.getMetadata());
			} else {
				mTvStationGenre.setText(mCurrentStation.getStationName());
			}
			if (mCurrentStation.getBuyLinkAmazon() == null) {
				// mIvBuyIcon.setOnClickListener(null);
				// mIvBuyIcon.setImageResource(R.drawable.ic_player_basket_h);
				mIvBuyIcon.setVisibility(View.GONE);
			} else {
				mIvBuyIcon.setImageResource(R.drawable.detail_icon_button_basket_bg);
				mIvBuyIcon.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mCurrentStation != null && mCurrentStation.getBuyLinkAmazon() != null) {
							Intent i = new Intent(Intent.ACTION_VIEW);
							i.setData(Uri.parse(mCurrentStation.getBuyLinkAmazon()));
							startActivity(i);
						}
					}
				});
			}

			mTvStationGenre.setVisibility(View.VISIBLE);
			mTvStationName.setText(mCurrentStation.getStationName());
			mIvPlayButton.setClickable(true);
			mIvForwardButton.setClickable(true);
			mIvBackButton.setClickable(true);
			
//			mTvStationName.setTypeface(Misc.getCustomFont(getActivity(), Misc.FONT_NORMAL));
//			mTvStationGenre.setTypeface(Misc.getCustomFont(getActivity(), Misc.FONT_NORMAL));
//			mFullscreenStationName.setTypeface(Misc.getCustomFont(getActivity(), Misc.FONT_NORMAL));
//			mFullscreenStationGenre.setTypeface(Misc.getCustomFont(getActivity(), Misc.FONT_NORMAL));


			rl_fullscreen.setOnClickListener(this);
			mFullscreenCover.setOnClickListener(this);
			mFullscreenStationName.setOnClickListener(this);
			mFullscreenStationGenre.setOnClickListener(this);

			if (isFullscreen) {
				if (mCurrentStation.hasStationLogoAvailable())
					mImageLoader.displayImage(mCurrentStation.getStationLogoUrl(), mFullscreenCover);
				else
					mFullscreenCover.setImageResource(R.drawable.ic_default_station);

				mFullscreenStationName.setText(mCurrentStation.getStationName());

				if (Misc.isValidMetaData(mCurrentStation.getMetadata())) {
					mFullscreenStationGenre.setText(mCurrentStation.getMetadata());
				} else {
					mFullscreenStationGenre.setText(mCurrentStation.getStationName());
				}

			}
			
			mTvStationName.setTypeface(Misc.getCustomFont(getActivity(), Misc.FONT_NORMAL));
			mTvStationGenre.setTypeface(Misc.getCustomFont(getActivity(), Misc.FONT_NORMAL));
			mFullscreenStationName.setTypeface(Misc.getCustomFont(getActivity(), Misc.FONT_NORMAL));
			mFullscreenStationGenre.setTypeface(Misc.getCustomFont(getActivity(), Misc.FONT_NORMAL));

			if (isFullscreen)
				mFullscreenCover.requestFocus();
			else {
				mIvPlayButton.setClickable(true);
				mIvPlayButton.requestFocus();
			}

		}
	}

	public void setLoading() {
		mCover.setImageResource(R.drawable.ic_default_station);
		mSpinner.setVisibility(View.VISIBLE);
		mIvPlayButton.setImageResource(R.drawable.ic_player_play);
		mTvStationName.setText(getString(R.string.playerfragment_text_loading));
		mTvStationGenre.setText("");
	}

	public void setErrorMessage(String msg) {
		/**
		 * pop up dialog for error playing
		 */
		mCurrentStation = null;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		isGreenStyle = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("style", true);
		View v = inflater.inflate(R.layout.telepad_fragment_station_detail, container, false);

		mPlayer = (RelativeLayout) v.findViewById(R.id.telepad_stationdetail_player_rl);
		mInformation = (LinearLayout) v.findViewById(R.id.telepad_stationdetail_information_ll);
		mFunction = (LinearLayout) v.findViewById(R.id.telepad_stationdetail_function_ll);

		mIvPlayButton = (ImageView) mPlayer.findViewById(R.id.telepad_iv_stationdetail_player_play);
		mIvForwardButton = (ImageView) mPlayer.findViewById(R.id.telepad_iv_stationdetail_player_forward);
		mIvBackButton = (ImageView) mPlayer.findViewById(R.id.telepad_iv_stationdetail_player_backward);

		mIvBuyIcon = (ImageView) mFunction.findViewById(R.id.telepad_iv_stationdetail_function_buy);
		mIvFavIcon = (ImageView) mFunction.findViewById(R.id.telepad_iv_stationdetail_function_fav);
		mIvHomeIcon = (ImageView) mFunction.findViewById(R.id.telepad_iv_stationdetail_function_home);
		mIvReturnIcon = (ImageView) mFunction.findViewById(R.id.telepad_iv_stationdetail_function_return);

		mTvStationName = (TextView) mInformation.findViewById(R.id.telepad_tv_stationdetail_item_stationname);
		mTvStationGenre = (TextView) mInformation.findViewById(R.id.telepad_tv_stationdetail_item_stationgenre);
		

		rl_stationdetail = (RelativeLayout) v.findViewById(R.id.rl_stationdetail);
		rl_fullscreen = (RelativeLayout) v.findViewById(R.id.rl_fullscreen);

		mFullscreenCover = (ImageView) rl_fullscreen.findViewById(R.id.fullscreen_cover);
		mFullscreenStationName = (TextView) rl_fullscreen.findViewById(R.id.fullscreen_stationname);
		mFullscreenStationGenre = (TextView) rl_fullscreen.findViewById(R.id.fullscreen_stationgenre);
		
		mTvStationName.setTypeface(Misc.getCustomFont(getActivity(), Misc.FONT_NORMAL));
		mTvStationGenre.setTypeface(Misc.getCustomFont(getActivity(), Misc.FONT_NORMAL));
		mFullscreenStationName.setTypeface(Misc.getCustomFont(getActivity(), Misc.FONT_NORMAL));
		mFullscreenStationGenre.setTypeface(Misc.getCustomFont(getActivity(), Misc.FONT_NORMAL));
		

		mSpinner = (ProgressBar) mPlayer.findViewById(R.id.telepad_stationd_spinner);
		mCover = (SquareImageView) mPlayer.findViewById(R.id.telepad_iv_station_cover);
		logo = (ImageView) v.findViewById(R.id.station_detail_logo);

		initUI();

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	public void onPlayButtonPressed(Station station) {
		if (mListener != null) {
			mListener.onTogglePlay(station);
		}
	}

	public void setPlayButton(boolean isPlaying) {
		if (isPlaying)
			mIvPlayButton.setImageResource(isGreenStyle ? R.drawable.detail_icon_button_pause_icon
					: R.drawable.ivt_detail_icon_button_pause_icon);
		else
			mIvPlayButton.setImageResource(isGreenStyle ? R.drawable.detail_icon_button_play_icon
					: R.drawable.ivt_detail_icon_button_play_icon);
	}

	public void setFavIcon(boolean isFav) {
		if (!isFav) {
			mIvFavIcon.setImageResource(isGreenStyle ? R.drawable.detail_icon_button_fav_bg
					: R.drawable.ivt_detail_icon_button_fav_bg);
		} else {
			mIvFavIcon.setImageResource(isGreenStyle ? R.drawable.detail_icon_button_fav_bg_h
					: R.drawable.ivt_detail_icon_button_fav_bg_h);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnStationDetailListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnStationDetailListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
		mHandler.removeCallbacks(mRunnable);
	}

	public interface OnStationDetailListener {
		public void onTogglePlay(Station station);

		public void onNewStation(Station station);
	}

	@Override
	public void onChangingFullscreen() {
		setisFullscreen(!isFullscreen);
		if (isFullscreen) {
			rl_stationdetail.setVisibility(View.GONE);
			rl_fullscreen.setVisibility(View.VISIBLE);
			
			
			mHandler.removeCallbacks(mRunnable);
			mHandler.postDelayed(mRunnable, 300000);
		} else {
			rl_stationdetail.setVisibility(View.VISIBLE);
			rl_fullscreen.setVisibility(View.GONE);
			
			mHandler.removeCallbacks(mRunnable);
			LayoutParams layoutpars = window.getAttributes();
			layoutpars.screenBrightness = 255 / (float)255;
			window.setAttributes(layoutpars);
		}

		initUI();
	}

	@Override
	public void onClick(View v) {
		setisFullscreen(!isFullscreen);
		rl_stationdetail.setVisibility(View.VISIBLE);
		rl_fullscreen.setVisibility(View.GONE);
		setPlayButton(mCurrentStation.isPlaying());
		mIvPlayButton.requestFocus();
		
		mHandler.removeCallbacks(mRunnable);
		LayoutParams layoutpars = window.getAttributes();
		layoutpars.screenBrightness = 255 / (float)255;
		window.setAttributes(layoutpars);
	}

}
