package com.woodsho.absoluteplan.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

//    public static List<ScheduleTask> getTodayScheduleTaskList() {
//        List<ScheduleTask> allList = CacheScheduleTaskStore.getInstance().getCacheScheduleTaskList();
//        List<ScheduleTask> list = new ArrayList<>();
//        if (allList != null && allList.size() > 0) {
//            SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd");
//            String today = sdFormatter.format(System.currentTimeMillis());
//            for (int i = 0; i < allList.size(); i++) {
//                ScheduleTask task = allList.get(i);
//                String strBD = sdFormatter.format(task.time);
//                if (strBD.equals(today)) {
//                    list.add(task);
//                }
//            }
//        }
//        return list;
//    }

//    public static List<ScheduleTask> getTomorrowScheduleTaskList() {
//        List<ScheduleTask> allList = CacheScheduleTaskStore.getInstance().getCacheScheduleTaskList();
//        List<ScheduleTask> list = new ArrayList<>();
//        if (allList != null && allList.size() > 0) {
//            for (int i = 0; i < allList.size(); i++) {
//                ScheduleTask task = allList.get(i);
//                if (CalendarUtil.isTomorrow(task.time)) {
//                    list.add(task);
//                }
//            }
//        }
//        return list;
//    }
//
//    public static List<ScheduleTask> getFinishedScheduleTaskList() {
//        List<ScheduleTask> allList = CacheScheduleTaskStore.getInstance().getCacheScheduleTaskList();
//        List<ScheduleTask> list = new ArrayList<>();
//        if (allList != null && allList.size() > 0) {
//            for (int i = 0; i < allList.size(); i++) {
//                ScheduleTask task = allList.get(i);
//                if (task.state == 1) {
//                    list.add(task);
//                }
//            }
//        }
//        return list;
//    }

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
     * 截取掉前缀0以便转换为整数
     *
     * @see #fillZero(int)
     */
    public static int trimZero(@NonNull String text) {
        try {
            if (text.startsWith("0")) {
                text = text.substring(1);
            }
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 根据年份及月份计算每月的天数
     */
    public static int calculateDaysInMonth(int year, int month) {
        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] bigMonths = {"1", "3", "5", "7", "8", "10", "12"};
        String[] littleMonths = {"4", "6", "9", "11"};
        List<String> bigList = Arrays.asList(bigMonths);
        List<String> littleList = Arrays.asList(littleMonths);
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (bigList.contains(String.valueOf(month))) {
            return 31;
        } else if (littleList.contains(String.valueOf(month))) {
            return 30;
        } else {
            if (year <= 0) {
                return 29;
            }
            // 是否闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                return 29;
            } else {
                return 28;
            }
        }
    }

    public static long converToLongTime(String year, String month, String day, String hour, String minute) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
        String strTime = year + month + day + hour + minute + "00";
        long time = System.currentTimeMillis();
        try {
            time = dateformat.parse(strTime).getTime();
            Log.d("fuck", "time: " + time);
        } catch (Exception ex) {
            Log.e("fuck", "converToLongTime, ex: " + ex);
        }
        return time;
    }

    /**
     * 兼容处理
     *
     * @param context 上下文
     * @param bitmap  模糊位图
     * @param view    模糊区域
     * @param radius  模糊半径
     */
    public static void blur(Context context, Bitmap bitmap, View view, int radius) {
        if (Build.VERSION.SDK_INT > 17) {
            blurByRender(context, bitmap, view, radius);
        } else {
            blurByGauss(context, bitmap, view, radius);
        }
    }

    /**
     * 兼容处理
     *
     * @param context 上下文
     * @param bitmap  模糊位图
     * @param radius  模糊半径
     * @return bitmap
     */
    public static Bitmap blur(Context context, Bitmap bitmap, int radius) {
        if (Build.VERSION.SDK_INT > 17) {
            return blurBitmapByRender(context, bitmap, radius);
        } else {
            return blurByGauss(bitmap, radius);
        }
    }

    /**
     * 高斯模糊
     *
     * @param srcBitmap 源位图
     * @param radius    模糊半径
     * @return bitmap
     */
    public static Bitmap blurByGauss(Bitmap srcBitmap, int radius) {

        Bitmap bitmap = srcBitmap.copy(srcBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int temp = 256 * divsum;
        int dv[] = new int[temp];
        for (i = 0; i < temp; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return bitmap;
    }

    /**
     * 高斯局部模糊
     *
     * @param context 上下文
     * @param bitmap  模糊位图
     * @param view    模糊区域
     * @param radius  模糊半径
     */
    public static void blurByGauss(Context context, Bitmap bitmap, View view, float radius) {
        // 得到要处理的区域
        Bitmap dstArea = getDstArea(bitmap, view);

        // 作模糊处理
        dstArea = blurByGauss(zoomImage(dstArea, 0.8f), (int) radius);

        // 设置背景
        view.setBackground(new BitmapDrawable(context.getResources(), dstArea));

        bitmap.recycle();
    }

    /**
     * RenderScript模糊
     *
     * @param context 上下文
     * @param bitmap  源位图
     * @param radius  模糊半径
     * @return bitmap
     */
    @SuppressLint("NewApi")
    public static Bitmap blurBitmapByRender(Context context, Bitmap bitmap, float radius) {
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
        blurScript.setRadius(radius);
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);
        allOut.copyTo(outBitmap);

        bitmap.recycle();
        rs.destroy();
        allIn.destroy();
        allOut.destroy();
        blurScript.destroy();

        return outBitmap;
    }

    /**
     * RenderScript局部模糊
     *
     * @param context 上下文
     * @param bitmap  模糊位图
     * @param view    模糊区域
     * @param radius  模糊半径
     */
    @SuppressLint("NewApi")
    public static void blurByRender(Context context, Bitmap bitmap, View view, float radius) {
        // 得到要处理的区域
        Bitmap dstArea = getDstArea(bitmap, view);
        dstArea = zoomImage(dstArea, 0.8f);

        // 作模糊处理
        RenderScript rs = RenderScript.create(context);
        Allocation overlayAlloc = Allocation.createFromBitmap(rs, dstArea);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.getElement());
        blur.setInput(overlayAlloc);
        blur.setRadius(radius);
        blur.forEach(overlayAlloc);
        overlayAlloc.copyTo(dstArea);

        // 设置背景
        view.setBackground(new BitmapDrawable(context.getResources(), dstArea));

        bitmap.recycle();
        overlayAlloc.destroy();
        blur.destroy();
        rs.destroy();
    }

    /**
     * 得到待处理的位图
     *
     * @param bitmap 模糊位图
     * @param view   模糊区域
     * @return bitmap
     */
    public static Bitmap getDstArea(Bitmap bitmap, View view) {
        Bitmap dstArea = Bitmap.createBitmap((int) (view.getMeasuredWidth()), (int) (view.getMeasuredHeight()),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dstArea);
        canvas.translate(-view.getLeft(), -view.getTop());
        canvas.drawBitmap(bitmap, 0, 0, null);
        return dstArea;
    }

    /**
     * 缩放图片
     *
     * @param srcBitmap 源图
     * @return bitmap
     */
    public static Bitmap zoomImage(Bitmap srcBitmap, float scale) {
        // 获取这个图片的宽和高
        float width = srcBitmap.getWidth();
        float height = srcBitmap.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 缩放图片动作
        matrix.postScale(scale, scale);
        Bitmap bitmap = Bitmap.createBitmap(srcBitmap, 0, 0, (int) width, (int) height, matrix, true);
        return bitmap;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
