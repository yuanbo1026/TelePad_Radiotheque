package com.technisat.radiotheque.splash;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.constants.FileStorageHelper;
import com.technisat.radiotheque.constants.Nexxoo;
import com.technisat.radiotheque.entity.Country;
import com.technisat.radiotheque.entity.CountryList;
import com.technisat.radiotheque.entity.Genre;
import com.technisat.radiotheque.entity.GenreList;
import com.technisat.radiotheque.global.Global;
import com.technisat.radiotheque.metadataretriver.IcyMetaDataRetriever;
import com.technisat.radiotheque.radiodb.JSONParser;
import com.technisat.radiotheque.radiodb.NexxooWebservice;
import com.technisat.radiotheque.tracking.TechniTracker;
import com.technisat.radiotheque.update.UpdateDialog;
import com.technisat.telepadradiothek.R;

public class StartUpAsync extends AsyncTask<String, Integer, Bundle>{
	
	private ISplash mCallback;
	private Context mContext;
	
	/**
	 * Check device and get all stations
	 * @author b.yuan
	 *
	 */
	public StartUpAsync(Context ctx, ISplash callback){
		mContext = ctx;
		mCallback = callback;
	}

	@Override
	protected Bundle doInBackground(String... params) {
		//perform tracking
		if (TechniTracker.isDeviceWithApp(mContext)){
			Log.d("Radiotheque_device_check", "Response: " + NexxooWebservice.trackAppStart(TechniTracker.getHashedDeviceSerial(mContext)) );
		}		
		
		//testMetaRetriever();

		//start app, perform actions
		Bundle result =  new Bundle();		
		Global g = Global.getInstance();
		List<Genre> gList = getAllStationsGenres();		
		List<Country> cList = getAllStationsCountries();
		g.setgList(gList);
		g.setcList(cList);
		
		if (cList.isEmpty() || gList.isEmpty()){
//			Log.d("Nexxoo", "Empty gList: " + Boolean.toString(gList.isEmpty()));
//			Log.d("Nexxoo", "Empty cList: " + Boolean.toString(cList.isEmpty()));
			return null;
		}
		/**
		 * already worked well, not sure whether it is useful or not.
		 */
//		if (gList.size() > 0) {
//		    Collections.sort(gList, new Comparator<Genre>() {
//				@Override
//				public int compare(Genre lhs, Genre rhs) {
//					return lhs.getName().compareTo(rhs.getName());
//				}
//		       } );
//		   }
//		
//		if (cList.size() > 0) {
//		    Collections.sort(cList, new Comparator<Country>() {
//				@Override
//				public int compare(Country lhs, Country rhs) {
//					return lhs.getCountry().compareTo(rhs.getCountry());
//				}
//		       } );
//		   }
		
		
		
		result.putParcelable(mContext.getString(R.string.radiothek_bundle_genreparceable), new GenreList(gList));
		result.putParcelable(mContext.getString(R.string.radiothek_bundle_countryparceable), new CountryList(cList));
		
		return result;		
	}
	
	private List<Genre> getAllStationsGenres(){
		List<Genre> gList =  new ArrayList<Genre>();
		
		String json = NexxooWebservice.getStationsForAllGenres(Misc.getLanguageCode(mContext), 8);
		if (json != null){
			try {
				JSONObject jsonObj = new JSONObject(json);
				JSONObject genreObj = null;
				
				String genreName = null;
				long genreId = -1;
				
				String stationJson = null;
				
				int countGenres = jsonObj.getInt("count");
				
				for (int i = 0; i < countGenres; i++) {					
					genreObj = jsonObj.getJSONObject(Integer.toString(i));
					genreName = genreObj.getString("name");
					genreId = genreObj.getLong("id");
					stationJson = genreObj.getString("stations");

					Genre g = new Genre(genreId, genreName, null);
					g.setsList(JSONParser.parseJsonToStationList(stationJson));					
					if (g.getsList().size() > 0)
						gList.add(g);
				}
				
			} catch (JSONException e) {
				//trouble with decoding json
			}
		}
		
		return gList;
	}
	
	@SuppressWarnings("unused")
	private void testMetaRetriever(){
		//stats
		String json = NexxooWebservice.getRandomStations(100);		
		List<String> stations = JSONParser.parseJsonToStationListRandom(json);
		Log.d("Nexxoo", "Stations: " + stations.size());
		
		int countSucNew = 0;
		int countFailNew = 0;
		int countNoUrlNew = 0;
		
		int countSuc = 0;
		int countFail = 0;
		int countNoUrl = 0;
		int countSucNativeArtist = 0;
		int countSucNativeTitle = 0;
		
		int index = 0;
		
		for (String s : stations) {
			index++;
			Log.d("Nexxoo", "(" + index + ") Station " + s);
			try {
				URL url = new URL(s);
				if (url != null){
					IcyMetaDataRetriever it = new IcyMetaDataRetriever(url);
					if (!it.isError() && (it.getTitle().length() > 3 || it.getArtist().length() > 3))
						countSucNew++;
					else
						countFailNew++;
				}
			} catch (MalformedURLException e) {
				countNoUrlNew++;
			} catch (IOException e) {
				countFailNew++;
			} catch (Exception e) {

			}
			
//			FFmpegMediaMetadataRetriever mmr = null;
//			MediaMetadataRetriever mdr = null;
//			
//			try {
//				mmr = new FFmpegMediaMetadataRetriever();
//				mdr = new MediaMetadataRetriever();
//				mdr.setDataSource(s, new HashMap<String, String>());
//				
//				mmr.setDataSource(s);
//				String metadata = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ICY_METADATA);
//				
//				if (metadata != null && Misc.cutOutTitleAndArtist(metadata).length() > 3){
//					countSuc++;
//					
//				} else {
//					countFail++;
//					
//					metadata = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
//					if (metadata != null && metadata.length() > 0)
//						countSucNativeArtist++;
//					
//					metadata = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
//					if (metadata != null && metadata.length() > 0)
//						countSucNativeTitle++;
//				}					
//				
//			} catch (IllegalArgumentException e) {
//				countNoUrl++;
//			} catch (Exception e){
//				
//			} finally {
//				if (mmr != null)
//					mmr.release();
//			}
						
		}
		
		
		Log.d("nexxoo2", "Success new: " + countSucNew);
		Log.d("nexxoo2", "Failed new: " + countFailNew);
		Log.d("nexxoo2", "No Url new: " + countNoUrlNew);
		
		Log.d("nexxoo2", "Success old: " + countSuc);
		Log.d("nexxoo2", "Failed old: " + countFail);
		Log.d("nexxoo2", "No Url old: " + countNoUrl);
		
		Log.d("nexxoo2", "Count artist: " + countSucNativeArtist);
		Log.d("nexxoo2", "Count title: " + countSucNativeTitle);
	}
	
	private List<Country> getAllStationsCountries(){
		List<Country> cList =  new ArrayList<Country>();
		
		String json = NexxooWebservice.getStationsForAllCountries(Misc.getLanguageCode(mContext), 8);
		if (json != null){
			try {
				JSONObject jsonObj = new JSONObject(json);
				JSONObject countryObj = null;
				
				String country = null;
				String iso = null;
				long id = -1;
				
				String stationJson = null;
				
				int countCountries = jsonObj.getInt("count");
				
				for (int i = 0; i < countCountries; i++) {					
					countryObj = jsonObj.getJSONObject(Integer.toString(i));
					country = countryObj.getString("country");
					id = countryObj.getLong("id");
					iso = countryObj.getString("iso");
					stationJson = countryObj.getString("stations");
					Country c = new Country(id, country, iso);
					c.setStationList(JSONParser.parseJsonToStationList(stationJson));					
					if (c.getsList().size() > 0)
						cList.add(c);
				}
				
			} catch (JSONException e) {
				//trouble with decoding json
			}
		}
		
		return cList;
	}
	
	
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		if (mCallback != null){
			mCallback.onStatusUpdate("");
		}
	}

	@Override
	protected void onPostExecute(Bundle result) {
		if (mCallback != null && result != null){
			mCallback.onFinishLoading(result);
		} else {
			mCallback.onCouldNotConnectToDB();
		}
	}
}