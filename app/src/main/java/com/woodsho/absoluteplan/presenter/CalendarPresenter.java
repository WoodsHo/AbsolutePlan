package com.woodsho.absoluteplan.presenter;

import android.os.Handler;
import android.os.Looper;

import com.woodsho.absoluteplan.bean.PlanTask;
import com.woodsho.absoluteplan.data.CachePlanTaskStore;
import com.woodsho.absoluteplan.ui.CalendarFragment;

import java.util.List;

/**
 * Created by hewuzhao on 17/12/12.
 */

public class CalendarPresenter implements CachePlanTaskStore.OnPlanTaskChangedListener {
    public static final String TAG = "CalendarPresenter";
    public CalendarFragment mCalendarFragment;
    private boolean mIsDataInited;
    private Handler mUIHandler;

    public CalendarPresenter(CalendarFragment fragment) {
        mCalendarFragment = fragment;
        mUIHandler = new Handler(Looper.getMainLooper());
        CachePlanTaskStore.getInstance().addOnPlanTaskChangedListener(this);
    }

    public void loadData() {
        CachePlanTaskStore planTaskStore = CachePlanTaskStore.getInstance();
        if (planTaskStore.isPlanTaskInitializedFinished()) {
            mIsDataInited = true;
            List<PlanTask> planTaskList = planTaskStore.getCachePlanTaskList();
            mCalendarFragment.loadDataSuccess(planTaskList);
        } else {
            mIsDataInited = false;
        }
    }

    @Override
    public void onPlanTaskChanged() {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mCalendarFragment != null && mCalendarFragment.isAdded()) {
                    //if (!mIsDataInited) {
                    mCalendarFragment.loadDataSuccess(CachePlanTaskStore.getInstance().getCachePlanTaskList());
                    //}
                }
            }
        });
    }

    public void onDestroy() {
        if (mCalendarFragment != null) {
            mCalendarFragment = null;
        }
        CachePlanTaskStore.getInstance().removePlanTaskChangedListener(this);
    }
}
