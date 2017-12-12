package com.woodsho.absoluteplan.presenter;

import android.os.Handler;
import android.os.Looper;

import com.woodsho.absoluteplan.bean.PlanTask;
import com.woodsho.absoluteplan.data.CachePlanTaskStore;
import com.woodsho.absoluteplan.ui.AllFragment;

import java.util.List;

/**
 * Created by hewuzhao on 17/12/10.
 */

public class AllPresenter implements CachePlanTaskStore.OnPlanTaskChangedListener {
    public static final String TAG = "CalendarPresenter";
    public AllFragment mAllFragment;
    private boolean mIsDataInited;
    private Handler mUIHandler;

    public AllPresenter(AllFragment fragment) {
        mAllFragment = fragment;
        mUIHandler = new Handler(Looper.getMainLooper());
        CachePlanTaskStore.getInstance().addOnPlanTaskChangedListener(this);
    }

    public void loadData() {
        CachePlanTaskStore planTaskStore = CachePlanTaskStore.getInstance();
        if (planTaskStore.isPlanTaskInitializedFinished()) {
            mIsDataInited = true;
            List<PlanTask> planTaskList = planTaskStore.getCachePlanTaskList();
            mAllFragment.loadDataSuccess(planTaskList);
        } else {
            mIsDataInited = false;
        }
    }

    @Override
    public void onPlanTaskChanged() {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mAllFragment != null && mAllFragment.isAdded()) {
                    //if (!mIsDataInited) {
                    mAllFragment.loadDataSuccess(CachePlanTaskStore.getInstance().getCachePlanTaskList());
                    //}
                }
            }
        });
    }

    public void onDestroy() {
        if (mAllFragment != null) {
            mAllFragment = null;
        }
        CachePlanTaskStore.getInstance().removePlanTaskChangedListener(this);
    }
}
