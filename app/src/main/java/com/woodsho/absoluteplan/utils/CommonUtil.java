package com.woodsho.absoluteplan.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import com.woodsho.absoluteplan.bean.PlanTask;
import com.woodsho.absoluteplan.common.PlanTaskState;
import com.woodsho.absoluteplan.data.CachePlanTaskStore;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    public static List<PlanTask> getTodayPlanTaskList() {
        List<PlanTask> allList = CachePlanTaskStore.getInstance().getCachePlanTaskList();
        List<PlanTask> list = new ArrayList<>();
        if (allList != null && allList.size() > 0) {
            SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdFormatter.format(System.currentTimeMillis());
            for (int i = 0; i < allList.size(); i++) {
                PlanTask task = allList.get(i);
                String strBD = sdFormatter.format(task.time);
                if (strBD.equals(today) && task.state == PlanTaskState.STATE_NORMAL) {
                    list.add(task);
                }
            }
        }
        return list;
    }

    public static List<PlanTask> getTomorrowPlanTaskList() {
        List<PlanTask> allList = CachePlanTaskStore.getInstance().getCachePlanTaskList();
        List<PlanTask> list = new ArrayList<>();
        if (allList != null && allList.size() > 0) {
            for (int i = 0; i < allList.size(); i++) {
                PlanTask task = allList.get(i);
                if (CalendarUtil.isTomorrow(task.time) && task.state == PlanTaskState.STATE_NORMAL) {
                    list.add(task);
                }
            }
        }
        return list;
    }

    public static boolean isTheSame(List<PlanTask> planTaskList1, List<PlanTask> planTaskList2) {
        if (planTaskList1 == null || planTaskList1.size() <= 0 || planTaskList2 == null || planTaskList2.size() <= 0) {
            return false;
        }
        int size = planTaskList1.size();
        if (size != planTaskList2.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            PlanTask planTask1 = planTaskList1.get(i);
            PlanTask planTask2 = planTaskList2.get(i);
            if (!planTask1.equals(planTask2)) {
                return false;
            }
        }
        return true;
    }

    public static List<PlanTask> getNormalPlanTaskList() {
        return CachePlanTaskStore.getInstance().getCacheNormalPlanTaskList();
    }

    public static List<PlanTask> getFinishedPlanTaskList() {
        List<PlanTask> allList = CachePlanTaskStore.getInstance().getCachePlanTaskList();
        List<PlanTask> list = new ArrayList<>();
        if (allList == null || allList.size() <= 0) {
            return list;
        }

        for (PlanTask task : allList) {
            if (task.state == PlanTaskState.STATE_FINISHED) {
                list.add(task);
            }
        }
        return list;
    }

    public static boolean isToday(int year, int month, int day) {
        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);
        if (year == (pre.get(Calendar.YEAR))) {
            if (month == (pre.get(Calendar.MONTH) + 1)) {
                if (day == pre.get(Calendar.DAY_OF_MONTH)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isToday(long time) {
        return DateUtils.isToday(time);
    }

    public static boolean isToYear(long time) {
        Calendar c = Calendar.getInstance();//
        int toyear = c.get(Calendar.YEAR); // 获取当前年份
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String year = sdFormatter.format(time).split("-")[0];
        return TextUtils.equals(year, String.valueOf(toyear));
    }

    public static boolean isToYear(int year) {
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String toyear = sdFormatter.format(System.currentTimeMillis()).split("-")[0];
        return TextUtils.equals(toyear, String.valueOf(year));
    }

    public static boolean isTomorrow(long date) {
        Date afterDay = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String strBD = sdFormatter.format(afterDay);
        Date theTime = new Date(date);
        String strTheTime = sdFormatter.format(theTime);
        return strTheTime.equals(strBD);
    }

    public static int getToday() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 对TextView、Button等设置不同状态时其文字颜色。
     * 参见：http://blog.csdn.net/sodino/article/details/6797821
     * Modified by liyujiang at 2015.08.13
     */
    public static ColorStateList toColorStateList(@ColorInt int normalColor, @ColorInt int pressedColor,
                                                  @ColorInt int focusedColor, @ColorInt int unableColor) {
        int[] colors = new int[]{pressedColor, focusedColor, normalColor, focusedColor, unableColor, normalColor};
        int[][] states = new int[6][];
        states[0] = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_enabled, android.R.attr.state_focused};
        states[2] = new int[]{android.R.attr.state_enabled};
        states[3] = new int[]{android.R.attr.state_focused};
        states[4] = new int[]{android.R.attr.state_window_focused};
        states[5] = new int[]{};
        return new ColorStateList(states, colors);
    }

    public static ColorStateList toColorStateList(@ColorInt int normalColor, @ColorInt int pressedColor) {
        return toColorStateList(normalColor, pressedColor, pressedColor, normalColor);
    }

    /**
     * dp转换为px
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int pxValue = (int) (dpValue * scale + 0.5f);
        return pxValue;
    }

    /**
     * 月日时分秒，0-9前补0
     */
    @NonNull
    public static String fillZero(int number) {
        return number < 10 ? "0" + number : "" + number;
    }

    /**
     * get App versionCode
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        int versionCode = 1;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (Exception ex) {
            Log.e(TAG, "ex: " + ex);
        }
        return versionCode;
    }

    /**
     * get App versionName
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionName = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (Exception ex) {
            Log.e(TAG, "ex: " + ex);
        }
        return versionName;
    }

    //通过反射获取状态栏高度，默认25dp
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = CommonUtil.dp2px(context, 25);
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    public static void closeSafely(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (Exception ex) {
                Log.e(TAG, "ex: " + ex);
            }
        }
    }

    public static byte[] bmpToByteArray(Bitmap bitmap, int maxkb) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        int options = 100;
        while (output.toByteArray().length > maxkb && options != 10) {
            output.reset(); //清空output
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, output);//这里压缩options%，把压缩后的数据存放到output中
            options -= 10;
        }
        bitmap.recycle();

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 检测是否安装微信
     *
     * @param context
     * @return
     */
    public static boolean isWxInstall(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }

        return false;
    }
}
