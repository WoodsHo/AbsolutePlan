package com.woodsho.absoluteplan.presenter;

import android.os.Handler;
import android.os.Looper;

import com.woodsho.absoluteplan.bean.PlanTask;
import com.woodsho.absoluteplan.data.CachePlanTaskStore;
import com.woodsho.absoluteplan.ui.TodayFragment;

/**
 * Created by hewuzhao on 17/12/10.
 */

public class TodayPresenter implements CachePlanTaskStore.OnPlanTaskChangedListener {
    public static final String TAG = "TodayPresenter";
    public TodayFragment mTodayFragment;
    private boolean mIsDataInited;
    private Handler mUIHandler;

    public TodayPresenter(TodayFragment fragment) {
        mTodayFragment = fragment;
        mUIHandler = new Handler(Looper.getMainLooper());
        CachePlanTaskStore.getInstance().addOnPlanTaskChangedListener(this);
    }

    public void loadData() {
        CachePlanTaskStore cachePlanTaskStore = CachePlanTaskStore.getInstance();
        if (cachePlanTaskStore.isPlanTaskInitializedFinished()) {
            mIsDataInited = true;
            mTodayFragment.loadDataSuccess();
        } else {
            mIsDataInited = false;
        }
    }

    public void addPlanTask(PlanTask task) {
        CachePlanTaskStore planTaskStore = CachePlanTaskStore.getInstance();
        planTaskStore.addPlanTask(task, true);
        // TODO: 17/8/20 add to database
    }

    @Override
    public void onPlanTaskChanged() {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mTodayFragment != null && mTodayFragment.isAdded()) {
                    //if (!mIsDataInited) {
                    mTodayFragment.loadDataSuccess();
                    //}
                }
            }
        });
    }

    public void onDestroy() {
        if (mTodayFragment != null) {
            mTodayFragment = null;
        }
        CachePlanTaskStore.getInstance().removePlanTaskChangedListener(this);
    }
}
