package com.woodsho.absoluteplan.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.woodsho.absoluteplan.AbsolutePlanApplication;

/**
 * Created by hewuzhao on 17/12/10.
 */

public class AbsPSharedPreference {
    private static final String NAME_SP = "absoluteplann_sp";
    private static final String NAME_LAST_SELECTED_SIDE_ID = "last_slected_side_id";

    private volatile static AbsPSharedPreference sSPInstance = null;
    private SharedPreferences mSharedPreferences;

    private AbsPSharedPreference(Context context) {
        mSharedPreferences = context.getSharedPreferences(NAME_SP, Context.MODE_PRIVATE);
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
        mSharedPreferences.edit().putInt(NAME_LAST_SELECTED_SIDE_ID, id).apply();
    }

    public int getLastSelectedSideId(int defaultValue) {
        return mSharedPreferences.getInt(NAME_LAST_SELECTED_SIDE_ID, defaultValue);
    }
}
