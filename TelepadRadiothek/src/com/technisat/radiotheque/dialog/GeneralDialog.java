package com.technisat.radiotheque.dialog;

import com.technisat.radiotheque.android.Misc;
import com.technisat.telepadradiothek.R;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class GeneralDialog extends Dialog{
	private Context mContext;
	private TextView mTextView;
	private Button mButton;
	private GeneralDialog dialogContext;

	public GeneralDialog(Context context) {
		super(context);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dialogContext = this;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.searchview_dialog);
		
		mTextView = (TextView) findViewById(R.id.searchview_text);
		findViewById(R.id.searchview_ok_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogContext.dismiss();
					}
				});
		
		SpannableString ss = new SpannableString(mContext.getString(R.string.general_dialog_text));
		ss.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 22, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		ss.setSpan(new TextAppearanceSpan(mContext, R.style.SearchViewTextAppearance), 23, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		ss.setSpan(new ForegroundColorSpan(Color.BLACK), 28, 50, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		
		mTextView.setText(ss);
		mTextView.setTypeface(Misc.getCustomFont(mContext, Misc.FONT_NORMAL));
		mTextView.setTextSize(20);
		mTextView.setTextColor(R.color.RadiothekWhite);
		
	}

}
