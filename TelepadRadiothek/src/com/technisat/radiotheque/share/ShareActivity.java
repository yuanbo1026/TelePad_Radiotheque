package com.technisat.radiotheque.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.Toast;

import com.technisat.telepadradiothek.R;

public class ShareActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		// i.putExtra(Intent.EXTRA_EMAIL , new
		// String[]{getString(R.string.report_problem_mailto)});
		// i.putExtra(Intent.EXTRA_SUBJECT, "Radiotheque melden");
		// String link_val =
		// "https://play.google.com/store/apps/details?id=com.technisat.radiothek";
		String link_val = "https://play.google.com/store/apps/details?id=com.technisat.radiothek";
		String body = getString(R.string.share_text_3)+" \n\n\n "+" <a href=\"" + link_val + "\">" + link_val + "</a>";

		i.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(body));
		// i.putExtra(
		// Intent.EXTRA_TEXT,
		// getString(R.string.share_text_3)
		// + "\n\n\n"
		// + Html.fromHtml(new StringBuilder().append("<a href=\"" + link_val +
		// "\">" + link_val+ "</a>")
		// .toString()));

		try {
			startActivity(Intent.createChooser(i, getString(R.string.meldenactivity_text_mailwith)));
			finish();
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(this, getString(R.string.meldenactivity_text_nomailclients), Toast.LENGTH_SHORT).show();
		}
	}

}
