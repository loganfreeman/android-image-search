package com.loganfreeman.gallery;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by scheng on 5/25/2017.
 */

public class UserSettingActivity extends PreferenceActivity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.search_setting);
    }



}
