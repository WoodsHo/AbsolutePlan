package com.woodsho.absoluteplan;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.woodsho.absoluteplan.utils.CommonUtil;

/**
 * Created by hewuzhao on 17/12/8.
 */

public class AbsolutePlanApplication extends Application {
    public static final String TAG = "AbsolutePlanApplication";
    public static Context sAppContext;

    public static volatile boolean sAppInitailized;
    public static final Object sInitailizedLock = new Object();

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = this;
        sAppInitailized = false;
        String processName = CommonUtil.getProcessName(this);
        Log.d(TAG, "onCreate processName : " + processName);
        if (this.getPackageName().equals(processName)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (sInitailizedLock) {
                        long t1 = System.currentTimeMillis();
                        sAppInitailized = true;
                        sInitailizedLock.notifyAll();
                        long t2 = System.currentTimeMillis();
                        Log.d(TAG, "init app take about : " + (t2 - t1) + "ms");
                    }
                    //CacheScheduleTaskStore.initialize(sAppContext);
                }
            }).start();
        }
    }

    public static void checkAppInitializedBlock() {
        synchronized (sInitailizedLock) {
            if (!sAppInitailized) {
                try {
                    sInitailizedLock.wait();
                } catch (Exception ex) {
                    Log.e(TAG, "checkAppInitializedBlock, ex: " + ex);
                }
            }
        }
    }

    public static boolean isAppInitialized() {
        return sAppInitailized;
    }
}
