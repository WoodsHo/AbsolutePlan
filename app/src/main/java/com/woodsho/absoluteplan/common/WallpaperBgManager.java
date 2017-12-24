package com.woodsho.absoluteplan.common;

import android.content.Context;
import android.util.Log;

import com.woodsho.absoluteplan.AbsolutePlanApplication;
import com.woodsho.absoluteplan.listener.IWallpaperBgUpdate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hewuzhao on 17/12/24.
 */

public class WallpaperBgManager {
    public static final String TAG = "WallpaperBgManager";

    private volatile static WallpaperBgManager sWallpaperBgManager = null;

    private List<IWallpaperBgUpdate> mWallpaperBgObservers;

    public WallpaperBgManager(Context context) {
        mWallpaperBgObservers = new ArrayList<>();
    }

    public static WallpaperBgManager getInstance() {
        if (sWallpaperBgManager == null) {
            synchronized (WallpaperBgManager.class) {
                if (sWallpaperBgManager == null) {
                    sWallpaperBgManager = new WallpaperBgManager(AbsolutePlanApplication.sAppContext);
                }
            }
        }
        return sWallpaperBgManager;
    }

    public void attach(IWallpaperBgUpdate observer) {
        if (mWallpaperBgObservers == null) {
            mWallpaperBgObservers = new ArrayList<>();
        }

        if (!mWallpaperBgObservers.contains(observer)) {
            mWallpaperBgObservers.add(observer);
        }
    }

    public void detach(IWallpaperBgUpdate observer) {
        if (mWallpaperBgObservers == null) {
            return;
        }
        if (mWallpaperBgObservers.contains(observer)) {
            mWallpaperBgObservers.remove(observer);
        }
    }

    public void notifyWallpaperBgUpdate() {
        Log.d(TAG, "notifyWallpaperBgUpdate mWallpaperBgObservers:" + mWallpaperBgObservers);

        if (mWallpaperBgObservers == null) {
            Log.d(TAG, "no observer");
            return;
        }

        for (IWallpaperBgUpdate observer : mWallpaperBgObservers) {
            observer.onWallpaperBgUpdate();
        }
    }
}
