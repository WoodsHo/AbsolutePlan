package com.woodsho.absoluteplan.ui;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.widget.AbsPlanPreference;
import com.woodsho.absoluteplan.widget.AbsPlanPreferenceCategory;
import com.woodsho.absoluteplan.widget.AbsPlanSwitchPreference;

/**
 * Created by hewuzhao on 17/12/15.
 */

public class SettingsFragment extends PreferenceFragment {
    public AbsPlanPreference mAboutPreference;
    public AbsPlanPreference mSkinPreference;
    public AbsPlanPreference mFeedbackSuggestionPreference;
    public AbsPlanPreferenceCategory mPreferencePreferenceCategory;
    public AbsPlanPreferenceCategory mServiceSupportPreferenceCategory;
    public AbsPlanSwitchPreference mWallpaperBgPreference;
    public int mOrder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);
        mPreferencePreferenceCategory = (AbsPlanPreferenceCategory) findPreference(getString(R.string.key_settings_preference));
        mServiceSupportPreferenceCategory = (AbsPlanPreferenceCategory) findPreference(getString(R.string.key_settings_service_support));
        mAboutPreference = (AbsPlanPreference) findPreference(getString(R.string.key_settings_about));
        mSkinPreference = (AbsPlanPreference) findPreference(getString(R.string.key_settings_skin));
        mFeedbackSuggestionPreference = (AbsPlanPreference) findPreference(getString(R.string.key_settings_feedback_suggestion));
        mWallpaperBgPreference = (AbsPlanSwitchPreference) findPreference(getString(R.string.key_settings_wallpaper_bg));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            view.setBackgroundResource(R.color.settings_bg_color);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceScreen prefScreen = getPreferenceScreen();
        prefScreen.removeAll();
        mSkinPreference.setOrder(mOrder++);
        mWallpaperBgPreference.setOrder(mOrder++);
        mPreferencePreferenceCategory.addPreference(mSkinPreference);
        mPreferencePreferenceCategory.addPreference(mWallpaperBgPreference);
        mPreferencePreferenceCategory.setOrder(mOrder++);
        prefScreen.addPreference(mPreferencePreferenceCategory);
        mAboutPreference.setOrder(mOrder++);
        mFeedbackSuggestionPreference.setOrder(mOrder++);
        mServiceSupportPreferenceCategory.addPreference(mAboutPreference);
        mServiceSupportPreferenceCategory.addPreference(mFeedbackSuggestionPreference);
        mServiceSupportPreferenceCategory.setOrder(mOrder++);
        prefScreen.addPreference(mServiceSupportPreferenceCategory);

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mAboutPreference) {
            Toast.makeText(getActivity(), "开发中，敬请期待！", Toast.LENGTH_SHORT).show();
        } else if (preference == mSkinPreference) {
            Toast.makeText(getActivity(), "开发中，敬请期待！", Toast.LENGTH_SHORT).show();
        } else if (preference == mFeedbackSuggestionPreference) {
            Toast.makeText(getActivity(), "开发中，敬请期待！", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
