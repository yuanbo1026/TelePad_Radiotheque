package com.technisat.radiotheque.menu;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import com.technisat.radiotheque.android.DatabaseHandler;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.entity.StationList;
import com.technisat.radiotheque.genre.TelepadGenreActivity;
import com.technisat.radiotheque.main.TelepadMainActivity;
import com.technisat.radiotheque.station.TelepadStationActivity;
import com.technisat.radiotheque.station.util.Globals;
import com.technisat.radiotheque.stationlist.TelepadDisplayStationListActivity;
import com.technisat.telepadradiothek.R;

/**
 * Class to handle ActoinBar Icon Clicks
 *
 */
public class ActionBarMenuHandler {
	
	public static boolean handleActionBarItemClick(Activity activity, MenuItem item){
		Intent intent;
		DatabaseHandler dbHandler = new DatabaseHandler(activity.getApplicationContext());
		List<Station> stationList;
		StationList sList;
		switch (item.getItemId()) {
		case R.id.action_current_play:
			if(!activity.getClass().equals(TelepadStationActivity.class)){ 
				if(Globals.isPlaying){
					intent = new Intent(activity, TelepadStationActivity.class);
					activity.startActivity(intent);
				}else{
					Toast.makeText(activity, activity.getString(R.string.actionbar_not_playing_alert_text), Toast.LENGTH_LONG).show();
//					Dialog mDialog = new Dialog(activity);
//					mDialog.setContentView(R.layout.telepad_playing_alert_layout);
//					mDialog.show();
				}
				
			}
			return true;
		case R.id.action_home:
			if(!activity.getClass().equals(TelepadMainActivity.class)){ 
				intent = new Intent(activity, TelepadMainActivity.class);
				activity.startActivity(intent);
			}
			return true;
		case R.id.action_genres:
			if(!activity.getClass().equals(TelepadGenreActivity.class)){ 
//				intent = new Intent(activity, StationService.class);
//				intent.setAction(activity.getString(R.string.radiothek_stationservice_startgenreactivity));
//				activity.startService(intent);
				stationList = dbHandler.getAllHistoryStations();
				sList = new StationList(stationList);
				intent = new Intent(activity, TelepadDisplayStationListActivity.class);
				intent.setAction(activity.getString(R.string.radiothek_action_history));
				intent.putExtra(activity.getString(R.string.radiothek_bundle_stationlistparcelable), sList);
				activity.startActivity(intent);
			}
			return true;
		case R.id.action_favorites:
			stationList = dbHandler.getAllFavoriteStations();
			sList = new StationList(stationList);
			intent = new Intent(activity, TelepadDisplayStationListActivity.class);
			intent.setAction(activity.getString(R.string.radiothek_action_favorite));
			intent.putExtra(activity.getString(R.string.radiothek_bundle_stationlistparcelable), sList);
			activity.startActivity(intent);
			return true;
		case android.R.id.home:
			activity.finish();
			return true;
		}
		return false;
	}
}