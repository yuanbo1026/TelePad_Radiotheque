package com.technisat.radiotheque.searchview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.technisat.radiotheque.android.Misc;
import com.technisat.telepadradiothek.R;

public class SearchViewDialog extends Dialog {
	private Context mContext;
	private TextView mTextView;
	private CheckBox mCheckBox;
	private Button mButton;
	private SearchViewDialog dialogContext;

	public SearchViewDialog(Context context) {
		super(context);
		mContext  = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dialogContext = this;
		
		/**
		 * Initialize all components
		 */
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.searchview_dialog);
		
		mTextView = (TextView) findViewById(R.id.searchview_text);
		mCheckBox =(CheckBox) findViewById(R.id.searchview_checkbox);
		findViewById(R.id.searchview_ok_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if(mCheckBox.isChecked()){ 
							SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
							SharedPreferences.Editor editor = prefs.edit();
							editor.putBoolean(mContext.getString(R.string.searchview_checkbox_key), true);
							editor.commit();
//							Toast.makeText(mContext, "nix", Toast.LENGTH_LONG).show();
							dialogContext.dismiss();
						}
						else{
//							Toast.makeText(mContext, "immer", Toast.LENGTH_LONG).show();
							SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
							SharedPreferences.Editor editor = prefs.edit();
							editor.putBoolean(mContext.getString(R.string.searchview_checkbox_key), false);
							editor.commit();
							dialogContext.dismiss();
						}
							
					}
				});
		
//		SpannableString ss = new SpannableString(mContext.getString(R.string.searchview_text));
//		ss.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 22, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
////		ss.setSpan(new ForegroundColorSpan(R.color.searchview_button_color), 23, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//		ss.setSpan(new TextAppearanceSpan(mContext, R.style.SearchViewTextAppearance), 23, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
////		new TextAppearanceSpan(mContext, R.style.SearchViewTextAppearance);
//		ss.setSpan(new ForegroundColorSpan(Color.BLACK), 28, 50, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		
		mTextView.setText(mContext.getString(R.string.searchview_text));
		mTextView.setTypeface(Misc.getCustomFont(mContext, Misc.FONT_NORMAL));
		mTextView.setTextSize(50);
		mTextView.setTextColor(Color.BLACK);
		
		mCheckBox.setText(mContext.getString(R.string.searchview_checkbox));
		mCheckBox.setTypeface(Misc.getCustomFont(mContext, Misc.FONT_NORMAL));
		mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if(isChecked){
//					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
//					SharedPreferences.Editor editor = prefs.edit();
//					editor.putBoolean(mContext.getString(R.string.searchview_checkbox_key), true);
//					editor.commit();
//				}
			}
		});
		
		
		
	}

}
