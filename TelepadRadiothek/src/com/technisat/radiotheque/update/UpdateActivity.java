package com.technisat.radiotheque.update;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.technisat.radiotheque.constants.FileStorageHelper;
import com.technisat.radiotheque.constants.Nexxoo;
import com.technisat.radiotheque.webservice.NexxooWebservice;
import com.technisat.radiotheque.webservice.OnJSONResponse;
import com.technisat.telepadradiothek.R;



public class UpdateActivity extends Activity {
	private int currentVersion, lastestVersion;

	private TextView tv_current_version_code_text;
	private TextView tv_lastest_version_code_text;
	private Context mContext;
	private static ProgressBar mProgressSpinner;

	private static int contentType = 4;
	private static String fileName = "TelepadRadiothek.apk";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
		mContext = this;
//		Crittercism.initialize(getApplicationContext(),
//				"549002233cf56b9e0457cac4");

		tv_current_version_code_text = (TextView) findViewById(R.id.tv_current_version_code_text);
		tv_lastest_version_code_text = (TextView) findViewById(R.id.tv_lastest_version_code_text);

		/**
		 * check the latest version code from database by calling web-service
		 */
		NexxooWebservice.getLatestVersionCode(true, new OnJSONResponse() {

			@Override
			public void onReceivedJSONResponse(JSONObject json) {
				try {
					int versioncode = json.getInt("version");
//					Log.e(Nexxoo.TAG, "Get version from Database: "
//							+ versioncode);
					lastestVersion = versioncode;
					/**
					 * check current version code
					 */
					PackageInfo pinfo;
					try {
						pinfo = getPackageManager().getPackageInfo(
								getPackageName(), 0);
						int versionCode = pinfo.versionCode;
						String versionName = pinfo.versionName;
//						Log.e(Nexxoo.TAG, "Version Code: " + versionCode);
//						Log.e(Nexxoo.TAG, "Version Name: " + versionName);
						currentVersion = versionCode;
						tv_current_version_code_text
								.setText("Current Version Code: "
										+ currentVersion);
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					/**
					 * get current version code to make sure the textview on
					 * layout has content to display
					 */
					tv_lastest_version_code_text
							.setText("Lastest Version Code: " + lastestVersion);
					if (lastestVersion > currentVersion) {
						Update(currentVersion, lastestVersion);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onReceivedError(String msg, int code) {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
						.show();
				Log.e(Nexxoo.TAG, msg);
			}
		});

	}

	public int getApplicationVersionCode(Context context) {
		PackageManager packageManager = context.getPackageManager();
		try {
			PackageInfo pi = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException ex) {
		} catch (Exception e) {
		}
		return 0;
	}

	
	private void Update(int currentVersion, int lastestVersion) {

		UpdateDialog dialog = new UpdateDialog(this, "" + currentVersion, ""
				+ lastestVersion, new UpdateDialog.UpdateActionListener() {
			@Override
			public void onSend(final UpdateDialog dialog, Boolean updateble) {
				if (updateble) {
					List<NameValuePair> mAdditionalParams = new ArrayList<NameValuePair>();
					mAdditionalParams.add(new BasicNameValuePair("requesttask",
							Integer.toString(31)));// protected static final int
													// WEBTASK_GETUPDATE = 31;
					NexxooWebservice.performUpdate(mContext, mAdditionalParams,
							getCallback(mContext, false));
					
					dialog.dismiss();
					
					mProgressSpinner = (ProgressBar) ((Activity) mContext).findViewById(R.id.update_progress_spinner);
					mProgressSpinner.setIndeterminate(true);
					mProgressSpinner.setVisibility(View.VISIBLE);
					
				} else {
					Calendar currentTime = Calendar.getInstance(); 
					long seconds = currentTime.get(Calendar.SECOND);
					long nextTime = seconds +24*60*60;
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

	public static OnUpdateResult getCallback(final Context mContext,
			boolean reattach) {

		return new OnUpdateResult() {

			@Override
			public void onUpdateSuccess() {
				if (mContext != null) {
					mProgressSpinner.setIndeterminate(false);
					mProgressSpinner.setVisibility(View.INVISIBLE);
					File file = new File(
							FileStorageHelper.getDownloadFolder(mContext)
									+ contentType + File.separator + fileName);
					Intent intentForPrepare = null;

					try {
						intentForPrepare = new Intent(Intent.ACTION_VIEW);
						intentForPrepare.setDataAndType(Uri.fromFile(file),
								"application/vnd.android.package-archive");
						intentForPrepare
								.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						mContext.startActivity(intentForPrepare);

					} catch (ActivityNotFoundException e) {
						Log.d(Nexxoo.TAG2,
								"Activity for pending intent not found: "
										+ e.getMessage());
					}
				}

			}

		};
	}
}
