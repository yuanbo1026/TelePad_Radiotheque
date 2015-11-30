package com.technisat.radiotheque.player.error;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import com.technisat.telepadradiothek.R;

/**
 * Created by b.yuan on 27.11.2015.
 */
public class PlayErrorDialog extends Dialog {
	private Context mContext;

	public PlayErrorDialog(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.play_error_dialog);
	}
}