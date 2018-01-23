package com.woodsho.absoluteplan.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.woodsho.absoluteplan.AbsolutePlanApplication;
import com.woodsho.absoluteplan.MainActivity;
import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.adapter.TodayAdapter;
import com.woodsho.absoluteplan.bean.PlanTask;
import com.woodsho.absoluteplan.common.WallpaperBgManager;
import com.woodsho.absoluteplan.listener.IWallpaperBgUpdate;
import com.woodsho.absoluteplan.presenter.TodayPresenter;
import com.woodsho.absoluteplan.utils.CommonUtil;
import com.woodsho.absoluteplan.widget.CommonRecyclerView;

import java.util.List;

/**
 * Created by hewuzhao on 17/12/10.
 */

public class TodayFragment extends BaseFragment implements TodayAdapter.OnItemClickListener, IWallpaperBgUpdate {
    public TodayAdapter mTodayAdapter;
    public TodayPresenter mTodayPresenter;
    private CommonRecyclerView mRecyclerView;

    @Nullable
    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_today_layout, container, false);
    }

    @Override
    protected void bindView(View view) {
        Resources res = getResources();
        Context context = AbsolutePlanApplication.sAppContext;
        mRecyclerView = (CommonRecyclerView) view.findViewById(R.id.today_recyclerview);
        mTodayAdapter = new TodayAdapter(mActivity);
        mTodayAdapter.addOnItemClickListener(this);
        mRecyclerView.setAdapter(mTodayAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(manager);
        mTodayPresenter = new TodayPresenter(this);
    }

    @Override
    protected void bindData() {
        loadPlanTaskList();
    }

    public void loadPlanTaskList() {
        if (mTodayPresenter != null) {
            mTodayPresenter.loadData();
        }
    }

    public void loadDataSuccess() {
        List<PlanTask> planTasks = CommonUtil.getTodayPlanTaskList();
        if (planTasks != null) {
            ((MainActivity) getActivity()).updateSideItemOfToday(planTasks.size());
        }
        mTodayAdapter.changeAllData(planTasks);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        WallpaperBgManager.getInstance().attach(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTodayPresenter != null) {
            mTodayPresenter.onDestroy();
            mTodayPresenter = null;
        }
        if (mTodayAdapter != null) {
            mTodayAdapter.removeOnItemClickListener();
            mTodayAdapter.releaseActivity();
        }
        WallpaperBgManager.getInstance().detach(this);
    }

    @Override
    public void onDeleteItemClick(final PlanTask task) {
        mTodayAdapter.removeItem(task);
    }

    @Override
    public void onContentItemClick(PlanTask task) {
        Intent intent = new Intent(getActivity(), PlanTaskDetailsActivity.class);
        intent.putExtra(PlanTaskDetailsActivity.KEY_PLANTASK, task);
        intent.putExtra(PlanTaskDetailsActivity.KEY_SHOW_TYPE, PlanTaskDetailsActivity.TYPE_MODIFY);
        startActivity(intent);
    }

    @Override
    public void onWallpaperBgUpdate() {
        Log.d(TAG, "today fragment, recycl: " + mRecyclerView);
        if (mRecyclerView != null) {
            mRecyclerView.updateBackground();
        }
    }
}
