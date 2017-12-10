package com.woodsho.absoluteplan.presenter;

import android.os.Handler;
import android.os.Looper;

import com.woodsho.absoluteplan.bean.PlanTask;
import com.woodsho.absoluteplan.data.CachePlanTaskStore;
import com.woodsho.absoluteplan.ui.FinishedFragment;

/**
 * Created by hewuzhao on 17/12/10.
 */

public class FinishedPresenter implements CachePlanTaskStore.OnPlanTaskChangedListener {
    public static final String TAG = "FinishedPresenter";
    public FinishedFragment mFinishedFragment;
    private boolean mIsDataInited;
    private Handler mUIHandler;

    public FinishedPresenter(FinishedFragment fragment) {
        mFinishedFragment = fragment;
        mUIHandler = new Handler(Looper.getMainLooper());
        CachePlanTaskStore.getInstance().addOnPlanTaskChangedListener(this);
    }

    public void loadData() {
        CachePlanTaskStore planTaskStore = CachePlanTaskStore.getInstance();
        if (planTaskStore.isPlanTaskInitializedFinished()) {
            mIsDataInited = true;
            mFinishedFragment.loadDataSuccess();
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
                if (mFinishedFragment != null && mFinishedFragment.isAdded()) {
                    //if (!mIsDataInited) {
                    mFinishedFragment.loadDataSuccess();
                    //}
                }
            }
        });
    }

    public void onDestroy() {
        if (mFinishedFragment != null) {
            mFinishedFragment = null;
        }
        CachePlanTaskStore.getInstance().removePlanTaskChangedListener(this);
    }
}
