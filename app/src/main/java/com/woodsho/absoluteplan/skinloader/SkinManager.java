package com.woodsho.absoluteplan.skinloader;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hewuzhao on 18/1/18.
 */

public class SkinManager implements ISkinLoader {
    private static String TAG = "SkinManager";
    private static final int MSG_UPDATE = 1;

    private static volatile SkinManager sInstance;
    private Context mContext;
    private Resources mResources;
    private String mSkinPackageName;
    private List<ISkinUpdate> mSkinObservers;
    private boolean isDefaultSkin = true;
    private Handler mHandler;

    public static SkinManager getInstance() {
        if (sInstance == null) {
            synchronized (SkinManager.class) {
                if (sInstance == null) {
                    sInstance = new SkinManager();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
        SkinSharedPreferences preferences = SkinSharedPreferences.getInstance();
        isDefaultSkin = preferences.isDefaultSkin();
        mHandler = new MyHandler(this);
        if (!isDefaultSkin) {
            String skinPath = preferences.getApplyingSkinPath();
            load(skinPath, null, true);
        }
    }

    /**
     * @return return value of {@link #isDefaultSkin}
     */
    public boolean getIsDefaultSkin() {
        return isDefaultSkin;
    }

    public boolean isDefaultSkin() {
        if (mContext == null) {
            return true;
        }
        isDefaultSkin = SkinSharedPreferences.getInstance().isDefaultSkin();
        return isDefaultSkin;
    }

    static class MyHandler extends Handler {
        WeakReference<SkinManager> weakReference;

        MyHandler(SkinManager skinManager) {
            weakReference = new WeakReference<>(skinManager);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SkinManager current = weakReference.get();
            if (current == null) {
                return;
            }

            if (msg.what == MSG_UPDATE) {
                current.notifySkinUpdate();
            }
        }
    }

    /**
     * for app that does not support multiple skin, return false
     *
     * @return true for app that support tskin, otherwise false
     */
    public boolean isInit() {
        return mContext != null;
    }

    /**
     * reset to default skin
     */
    public void restoreDefaultSkin() {
        if (mContext == null) {
            Log.e(TAG, "mContext is null, can not restore default skin.");
            return;
        }
        SkinSharedPreferences preferences = SkinSharedPreferences.getInstance();
        preferences.saveApplyingSkinPath("");
        preferences.setIsDefaultSkin(true);
        mResources = mContext.getResources();
        isDefaultSkin = true;
        notifySkinUpdate();
    }

    public void load(final String skinPackagePath, final ILoaderListener callBack) {
        load(skinPackagePath, callBack, false);
    }

    /**
     * @param skinPackagePath
     * @param callBack
     * @param isInit          true for init, false for change skin
     */
    private void load(final String skinPackagePath, final ILoaderListener callBack, final boolean isInit) {
        if (TextUtils.isEmpty(skinPackagePath)) {
            Log.e(TAG, "skin path is empty");
            return;
        }
        File file = new File(skinPackagePath);
        if (!file.exists()) {
            Log.e(TAG, "doInBackground skin file is not exist: " + skinPackagePath);
            return;
        }

        if (callBack != null) {
            callBack.onStart();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PackageManager packageManager = mContext.getPackageManager();
                    PackageInfo packageInfo = packageManager.getPackageArchiveInfo(skinPackagePath,
                            PackageManager.GET_ACTIVITIES);
                    mSkinPackageName = packageInfo.packageName;

                    AssetManager assetManager = AssetManager.class.newInstance();
                    Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
                    addAssetPath.invoke(assetManager, skinPackagePath);

                    Resources superRes = mContext.getResources();
                    mResources = new Resources(assetManager,
                            superRes.getDisplayMetrics(), superRes.getConfiguration());

                    SkinSharedPreferences preferences = SkinSharedPreferences.getInstance();
                    preferences.saveApplyingSkinPath(skinPackagePath);
                    preferences.setIsDefaultSkin(false);
                    isDefaultSkin = false;


                    if (callBack != null) {
                        callBack.onSuccess();
                    }

                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(MSG_UPDATE);
                    }

                } catch (Exception e) {
                    Log.e(TAG, "ex: " + e);
                    if (callBack != null) {
                        callBack.onFailed();
                    }
                }
            }
        }).start();
    }

    @Override
    public void attach(ISkinUpdate observer) {
        if (mSkinObservers == null) {
            mSkinObservers = new ArrayList<>();
        }

        if (!mSkinObservers.contains(observer)) {
            mSkinObservers.add(observer);
        }
    }

    @Override
    public void detach(ISkinUpdate observer) {
        if (mSkinObservers == null) {
            return;
        }
        if (mSkinObservers.contains(observer)) {
            mSkinObservers.remove(observer);
        }
    }

    @Override
    public void notifySkinUpdate() {
        Log.d(TAG, "notifyThemeUpdate mThemeObservers:" + mSkinObservers);

        if (mSkinObservers == null) {
            return;
        }

        for (ISkinUpdate observer : mSkinObservers) {
            observer.onSkinUpdate();
        }
    }

    /**
     * get theme color for the given resource id, resource name mus be same in theme package
     *
     * @param resId resource id in application
     * @return
     */
    public int getColor(int resId) {
        if (mResources == null || isDefaultSkin) {
            return mContext.getResources().getColor(resId);
        }

        String resName = mContext.getResources().getResourceEntryName(resId);
        int themeResId = mResources.getIdentifier(resName, "color",
                mSkinPackageName);
        int themeColor;

        if (themeResId != 0) {
            themeColor = mResources.getColor(themeResId);
            Log.d(TAG, "get color in theme success resName:" + resName);
        } else {
            themeColor = mContext.getResources().getColor(resId);
        }

        return themeColor;
    }

    /**
     * get drawable in theme, resource name must be same in theme package
     *
     * @param resId resource id in application
     * @return
     */
    public Drawable getDrawable(int resId) {
        if (mResources == null || isDefaultSkin) {
            return mContext.getResources().getDrawable(resId);
        }

        String resName = mContext.getResources().getResourceEntryName(resId);
        int themeResId = mResources.getIdentifier(resName, "drawable", mSkinPackageName);
        Drawable themeDrawable;

        if (themeResId != 0) {
            themeDrawable = mResources.getDrawable(themeResId, null);
            Log.d(TAG, "get drawable in theme success resName:" + resName);
        } else {
            themeDrawable = mContext.getResources().getDrawable(resId);
        }
        return themeDrawable;
    }

    /**
     * get color state from theme, resource name must be same in theme package
     *
     * @param resId resource id in application
     * @return color state in theme
     */
    public ColorStateList convertToColorStateList(int resId) {
        boolean isExtendSkin = true;

        if (mResources == null || isDefaultSkin) {
            isExtendSkin = false;
        }

        String resName = mContext.getResources().getResourceEntryName(resId);
        if (isExtendSkin) {
            int trueResId = mResources.getIdentifier(resName, "color", mSkinPackageName);
            ColorStateList trueColorList;
            if (trueResId == 0) {
                try {
                    //color is not in theme package, but it may be color state list
                    ColorStateList originColorList = mContext.getResources().getColorStateList(resId);
                    return originColorList;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "color is not found :" + resName);
                }
            } else {
                try {
                    trueColorList = mResources.getColorStateList(trueResId);
                    Log.d(TAG, "get color state list in theme success resName:" + resName);
                    return trueColorList;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                ColorStateList originColorList = mContext.getResources().getColorStateList(resId);
                return originColorList;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int[][] states = new int[1][1];
        return new ColorStateList(states, new int[]{mContext.getResources().getColor(resId)});
    }

    /**
     * get drawable in theme, resource name must be same in theme package
     *
     * @param resName resource name in application
     * @return
     */
    public Drawable getDrawable(String resName) {
        int resId = mContext.getResources().getIdentifier(resName, "drawable", mContext.getPackageName());

        if (mResources == null || isDefaultSkin) {
            if (resId != 0) {
                return mContext.getResources().getDrawable(resId, null);
            }
            return null;
        }

        int themeResId = mResources.getIdentifier(resName, "drawable", mSkinPackageName);
        Drawable themeDrawable;

        if (themeResId != 0) {
            try {
                themeDrawable = mResources.getDrawable(themeResId, null);
                Log.d(TAG, "get drawable by resource name in theme success resName:" + resName);
            } catch (Exception e) {
                themeDrawable = mContext.getResources().getDrawable(resId, null);
            }
        } else {
            themeDrawable = mContext.getResources().getDrawable(resId, null);
        }
        return themeDrawable;
    }

    /**
     * get String from theme package
     *
     * @param resId
     */
    public String getDimenString(int resId) {
        if (mResources == null || isDefaultSkin) {
            return mContext.getResources().getString(resId);
        }

        String result;
        String resName = mContext.getResources().getResourceName(resId);
        int themeResId = mResources.getIdentifier(resName, "dimen", mSkinPackageName);

        if (themeResId != 0) {
            try {
                result = mResources.getString(themeResId);
                Log.d(TAG, "get dimen string success:" + resName);
            } catch (Exception e) {
                result = mContext.getResources().getString(resId);
            }
        } else {
            result = mContext.getResources().getString(resId);
        }

        return result;
    }

}
