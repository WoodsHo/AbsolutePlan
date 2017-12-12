package com.woodsho.absoluteplan.presenter;

import android.os.Handler;
import android.os.Looper;

import com.woodsho.absoluteplan.bean.PlanTask;
import com.woodsho.absoluteplan.data.CachePlanTaskStore;
import com.woodsho.absoluteplan.ui.TomorrowFragment;

/**
 * Created by hewuzhao on 17/12/10.
 */

public class TomorrowPresenter implements CachePlanTaskStore.OnPlanTaskChangedListener {
    public static final String TAG = "TomorrowPresenter";
    public TomorrowFragment mTomorrowFragment;
    private boolean mIsDataInited;
    private Handler mUIHandler;

    public TomorrowPresenter(TomorrowFragment fragment) {
        mTomorrowFragment = fragment;
        mUIHandler = new Handler(Looper.getMainLooper());
        CachePlanTaskStore.getInstance().addOnPlanTaskChangedListener(this);
    }

    public void loadData() {
        CachePlanTaskStore planTaskStore = CachePlanTaskStore.getInstance();
        if (planTaskStore.isPlanTaskInitializedFinished()) {
            mIsDataInited = true;
            mTomorrowFragment.loadDataSuccess();
        } else {
            mIsDataInited = false;
        }
    }

    @Override
    public void onPlanTaskChanged() {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mTomorrowFragment != null && mTomorrowFragment.isAdded()) {
                    //if (!mIsDataInited) {
                    mTomorrowFragment.loadDataSuccess();
                    //}
                }
            }
        });
    }

    public void onDestroy() {
        if (mTomorrowFragment != null) {
            mTomorrowFragment = null;
        }
        CachePlanTaskStore.getInstance().removePlanTaskChangedListener(this);
    }
}
