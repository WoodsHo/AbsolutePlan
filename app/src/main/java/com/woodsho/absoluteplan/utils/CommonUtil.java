package com.woodsho.absoluteplan.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

/**
 * Created by hewuzhao on 17/12/8.
 */

public class CommonUtil {
    public static final String TAG = "CommonUtil";

    public static String getProcessName(Context context) {
        try {
            int pid = android.os.Process.myPid();
            ActivityManager mActivityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                    .getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "getProcessName, ex: " + ex);
        }
        return "";
    }
}
