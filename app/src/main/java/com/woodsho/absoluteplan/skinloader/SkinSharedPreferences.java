package com.woodsho.absoluteplan.skinloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.woodsho.absoluteplan.AbsolutePlanApplication;

/**
 * Created by hewuzhao on 18/1/18.
 */

@SuppressLint("LongLogTag")
public class SkinSharedPreferences {
    public static final String TAG = "SkinSharedPreferences";

    private static volatile SkinSharedPreferences sInstance;
    private SharedPreferences mSharedPreferences;
    public Context mContext;

    private static final String SKIN_SHARED_PREFERECES = "skin_shared_preferences";
    private static final String KEY_APPLYING_SKIN_PATH = "applying_skin_path";
    private static final String KEY_IS_DEFAULT_SKIN = "is_default_skin";

    private SkinSharedPreferences() {
        mContext = AbsolutePlanApplication.sAppContext;
        mSharedPreferences = mContext.getSharedPreferences(SKIN_SHARED_PREFERECES, Context.MODE_PRIVATE);
    }

    public static SkinSharedPreferences getInstance() {
        if (sInstance == null) {
            synchronized (SkinSharedPreferences.class) {
                if (sInstance == null) {
                    sInstance = new SkinSharedPreferences();
                }
            }
        }
        return sInstance;
    }

    public void saveApplyingSkinPath(String path) {
        if (path == null) {
            Log.e(TAG, "path is null, can not save it.");
            return;
        }
        if (mSharedPreferences != null) {
            mSharedPreferences.edit().putString(KEY_APPLYING_SKIN_PATH, path).apply();
        }
    }

    public String getApplyingSkinPath() {
        String path = "";
        if (mSharedPreferences != null) {
            path = mSharedPreferences.getString(KEY_APPLYING_SKIN_PATH, "");
        }
        return path;
    }

    public boolean isDefaultSkin() {
        if (mSharedPreferences != null) {
            String s = mSharedPreferences.getString(KEY_IS_DEFAULT_SKIN, "");
            if (TextUtils.isEmpty(s)) {
                return true;
            }
            return Boolean.parseBoolean(s);
        }
        return true;
    }

    public void setIsDefaultSkin(boolean value) {
        if (mSharedPreferences != null) {
            mSharedPreferences.edit().putString(KEY_IS_DEFAULT_SKIN, String.valueOf(value)).apply();
        }
    }
}
