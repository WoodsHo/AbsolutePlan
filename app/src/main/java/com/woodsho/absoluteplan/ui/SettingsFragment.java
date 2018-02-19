package com.woodsho.absoluteplan.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.skinloader.DynamicAttr;
import com.woodsho.absoluteplan.skinloader.IDynamicNewView;
import com.woodsho.absoluteplan.skinloader.ISkinUpdate;
import com.woodsho.absoluteplan.skinloader.SkinInflaterFactory;
import com.woodsho.absoluteplan.skinloader.SkinManager;
import com.woodsho.absoluteplan.widget.AbsPlanPreference;
import com.woodsho.absoluteplan.widget.AbsPlanPreferenceCategory;
import com.woodsho.absoluteplan.widget.AbsPlanSwitchPreference;

import java.util.List;

/**
 * Created by hewuzhao on 17/12/15.
 */

public class SettingsFragment extends PreferenceFragment implements IDynamicNewView, ISkinUpdate {
    private static final String TAG = "SettingsFragment";
    private IDynamicNewView mIDynamicNewView;
    private boolean isResponseOnSkinChange = true;

    public AbsPlanPreference mAboutPreference;
    public AbsPlanPreference mSkinPreference;
    public AbsPlanPreference mFeedbackSuggestionPreference;
    public AbsPlanPreferenceCategory mPreferencePreferenceCategory;
    public AbsPlanPreferenceCategory mServiceSupportPreferenceCategory;
    public AbsPlanSwitchPreference mWallpaperBgPreference;

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
        View view = super.onCreateView(getActivity().getLayoutInflater(), container, savedInstanceState);
        if (view != null) {
            dynamicAddView(view, "background", R.color.settings_bg_color, true);
        }
        return view;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mAboutPreference) {
            startActivity(new Intent(getActivity(), AboutActivity.class));
        } else if (preference == mSkinPreference) {
            startActivity(new Intent(getActivity(), SkinActivity.class));
        } else if (preference == mFeedbackSuggestionPreference) {
            startActivity(new Intent(getActivity(), FeedbackSuggestionActivity.class));
        }
        return true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(getActivity());
        try {
            mIDynamicNewView = (IDynamicNewView) activity;
        } catch (ClassCastException e) {
            mIDynamicNewView = null;
        }

        SkinManager.getInstance().attach(this);
    }

    @Override
    public void dynamicAddView(View view, List<DynamicAttr> pDAttrs, boolean refresh) {
        if (mIDynamicNewView == null) {
            throw new RuntimeException("IDynamicNewView should be implements !");
        } else {
            mIDynamicNewView.dynamicAddView(view, pDAttrs, refresh);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        SkinManager.getInstance().detach(this);
    }

    /**
     * @param view
     * @param attrName
     * @param attrValueResId
     */
    public void dynamicAddView(View view, String attrName, int attrValueResId, boolean refresh) {
        ((SkinInflaterFactory) getActivity().getLayoutInflater().getFactory())
                .dynamicAddSkinEnableView(getActivity(), view, attrName, attrValueResId, refresh);
    }

    @Override
    public void onSkinUpdate() {
        if (!isResponseOnSkinChange) {
            return;
        }
        Log.d(TAG, "onSkinUpdate");
        ((SkinInflaterFactory) getActivity().getLayoutInflater().getFactory()).applySkin();
    }


    /**
     * switch for response on skin change
     *
     * @param enable true to response on skin change, false otherwise
     */
    final protected void enableResponseOnSkinChange(boolean enable) {
        isResponseOnSkinChange = enable;
    }
}
