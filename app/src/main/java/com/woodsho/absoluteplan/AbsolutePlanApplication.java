package com.woodsho.absoluteplan;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilderSupplier;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.woodsho.absoluteplan.fresco.BitmapMemoryCacheParamsSupplier;
import com.woodsho.absoluteplan.fresco.ImageNetworkFetcherEx;
import com.woodsho.absoluteplan.utils.CommonUtil;
import com.woodsho.absoluteplan.widget.SimpleDraweeViewEx;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by hewuzhao on 17/12/8.
 */

public class AbsolutePlanApplication extends Application {
    public static final String TAG = "AbsolutePlanApplication";
    public static Context sAppContext;

    public static boolean sFrescoInitialized;
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
                        initializeFrescoUI();
                        sAppInitailized = true;
                        sInitailizedLock.notifyAll();
                        long t2 = System.currentTimeMillis();
                        Log.d(TAG, "init app take about : " + (t2 - t1) + "ms");
                    }
                    //CachePlanTaskStore.initialize(sAppContext);
                }
            }).start();
        }
    }

    public static void initializeFrescoUI() {
        long t1 = System.currentTimeMillis();
        FLog.setMinimumLoggingLevel(FLog.ERROR);
        Set<RequestListener> listeners = new HashSet<>();
        listeners.add(new RequestLoggingListener());
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(sAppContext)
                .setDownsampleEnabled(true)
                .setRequestListeners(listeners)
                .setBitmapsConfig(Bitmap.Config.ARGB_8888)
                .setBitmapMemoryCacheParamsSupplier(new BitmapMemoryCacheParamsSupplier((ActivityManager) sAppContext.getSystemService(Context.ACTIVITY_SERVICE)))
                .setNetworkFetcher(new ImageNetworkFetcherEx())
                .build();
        Fresco.initialize(sAppContext, config);
        SimpleDraweeViewEx.initialize(new PipelineDraweeControllerBuilderSupplier(sAppContext));
        sFrescoInitialized = true;
        long t2 = System.currentTimeMillis();
        Log.d(TAG, "fresco initialized... " + (t2 - t1) + "ms");
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
