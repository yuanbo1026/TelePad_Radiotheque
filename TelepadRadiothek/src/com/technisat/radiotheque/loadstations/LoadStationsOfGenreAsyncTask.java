package com.technisat.radiotheque.loadstations;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.radiodb.JSONParser;
import com.technisat.radiotheque.radiodb.NexxooWebservice;

public class LoadStationsOfGenreAsyncTask extends AsyncTask<String, Integer, Boolean> {
	
	private ILoadStations mCallback;
	private long mGenreId;
	private int mCount;
	private List<Station> mStationList;
	private Context mContext;
	private boolean isSortUp;
	
	public LoadStationsOfGenreAsyncTask(long genreId, int count, ILoadStations callback,Context context){
		mCallback = callback;
		mGenreId = genreId;
		mCount = count;
		mContext = context;
		isSortUp = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("sort", true);
	}

	@Override
	protected Boolean doInBackground(String... params) {
//		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		String json = NexxooWebservice.getStationsByGenre(mGenreId, mCount);
		
		mStationList = JSONParser.parseJsonToStationList(json);
		/**
		 * sort station list alphabetically  
		 */
		if (mStationList.size() > 0) {
		    Collections.sort(mStationList, new Comparator<Station>() {
				@Override
				public int compare(Station lhs, Station rhs) {
					return isSortUp?lhs.getStationName().compareTo(rhs.getStationName()):rhs.getStationName().compareTo(lhs.getStationName());
				}
		       } );
		   }
		
		
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (mCallback != null && mStationList != null){
			mCallback.onMoreStationsLoaded(mStationList);
		}
	}
	
	

}
