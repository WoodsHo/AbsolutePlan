package com.woodsho.absoluteplan.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.woodsho.absoluteplan.AbsolutePlanApplication;

/**
 * Created by hewuzhao on 17/12/10.
 */

public class AbsPSharedPreference {
    private static final String NAME_SP = "absoluteplann_sp";
    private static final String NAME_LAST_SELECTED_SIDE_ID = "last_selected_side_id";
    private static final String NAME_SELECTED_WALLPAPER_BG = "selected_wallpaper_bg";

    private volatile static AbsPSharedPreference sSPInstance = null;
    private SharedPreferences mSharedPreferences;

    private int mLastSelectedSideId;
    private boolean mSelectedWallpaperBg;

    private AbsPSharedPreference(Context context) {
        mSharedPreferences = context.getSharedPreferences(NAME_SP, Context.MODE_PRIVATE);
        String id = mSharedPreferences.getString(NAME_LAST_SELECTED_SIDE_ID, String.valueOf(0));
        if (TextUtils.isEmpty(id)) {
            mLastSelectedSideId = 0;
        } else {
            mLastSelectedSideId = Integer.parseInt(id);
        }

        String bg = mSharedPreferences.getString(NAME_SELECTED_WALLPAPER_BG, String.valueOf(true));
        if (TextUtils.isEmpty(bg)) {
            mSelectedWallpaperBg = true;
        } else {
            mSelectedWallpaperBg = Boolean.parseBoolean(bg);
        }
    }

    public static AbsPSharedPreference getInstanc() {
        if (sSPInstance == null) {
            synchronized (AbsPSharedPreference.class) {
                if (sSPInstance == null) {
                    sSPInstance = new AbsPSharedPreference(AbsolutePlanApplication.sAppContext);
                }
            }
        }
        return sSPInstance;
    }

    public void saveLastSelectedSideId(int id) {
        mLastSelectedSideId = id;
        mSharedPreferences.edit().putString(NAME_LAST_SELECTED_SIDE_ID, String.valueOf(id)).apply();
    }

    public int getLastSelectedSideId(int defaultValue) {
        return mLastSelectedSideId;
    }

    public void saveSelectedWallpaperBg(boolean value) {
        mSelectedWallpaperBg = value;
        mSharedPreferences.edit().putString(NAME_SELECTED_WALLPAPER_BG, String.valueOf(value)).apply();
    }

    public boolean getSelectedWallpaperBg() {
        return mSelectedWallpaperBg;
    }
}
