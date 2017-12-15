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

/**
 * Created by hewuzhao on 17/12/15.
 */

public class SettingsFragment extends PreferenceFragment {
    public AbsPlanPreference mAboutPreference;
    public AbsPlanPreference mSkinPreference;
    public AbsPlanPreference mFeedbackSuggestionPreference;
    public int mOrder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);
        mAboutPreference = (AbsPlanPreference) findPreference(getString(R.string.key_settings_about));
        mSkinPreference = (AbsPlanPreference) findPreference(getString(R.string.key_settings_skin));
        mFeedbackSuggestionPreference = (AbsPlanPreference) findPreference(getString(R.string.key_settings_feedback_suggestion));
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
        mAboutPreference.setOrder(mOrder++);
        mSkinPreference.setOrder(mOrder++);
        mFeedbackSuggestionPreference.setOrder(mOrder++);
        prefScreen.addPreference(mAboutPreference);
        prefScreen.addPreference(mSkinPreference);
        prefScreen.addPreference(mFeedbackSuggestionPreference);
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
