package com.technisat.radiotheque.setting;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.technisat.telepadradiothek.R;

public class ItemSizeFragment extends PreferenceFragment {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.item_size_pref);
    }
}
