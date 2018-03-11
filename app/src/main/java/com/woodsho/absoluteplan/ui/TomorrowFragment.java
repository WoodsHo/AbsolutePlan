package com.woodsho.absoluteplan.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.woodsho.absoluteplan.AbsolutePlanApplication;
import com.woodsho.absoluteplan.MainActivity;
import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.adapter.TomorrowAdapter;
import com.woodsho.absoluteplan.bean.PlanTask;
import com.woodsho.absoluteplan.presenter.TomorrowPresenter;
import com.woodsho.absoluteplan.utils.CommonUtil;
import com.woodsho.absoluteplan.widget.CommonRecyclerView;

import java.util.List;

/**
 * Created by hewuzhao on 17/12/10.
 */

public class TomorrowFragment extends BaseFragment implements TomorrowAdapter.OnItemClickListener {
    public TomorrowAdapter mTomorrowAdapter;
    public TomorrowPresenter mTomorrowPresenter;
    private CommonRecyclerView mRecyclerView;

    @Nullable
    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_tomorrow_layout, container, false);
    }

    @Override
    protected void bindView(View view) {
        Resources res = getResources();
        Context context = AbsolutePlanApplication.sAppContext;
        mRecyclerView = (CommonRecyclerView) view.findViewById(R.id.tomorrow_recyclerview);
        mTomorrowAdapter = new TomorrowAdapter(mActivity);
        mTomorrowAdapter.addOnItemClickListener(this);
        mRecyclerView.setAdapter(mTomorrowAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(manager);
        mTomorrowPresenter = new TomorrowPresenter(this);
    }

    @Override
    protected void bindData() {
        loadPlanTaskList();
    }

    public void loadPlanTaskList() {
        if (mTomorrowPresenter != null) {
            mTomorrowPresenter.loadData();
        }
    }

    public void loadDataSuccess() {
        List<PlanTask> planTasks = CommonUtil.getTomorrowPlanTaskList();
        if (planTasks != null) {
            ((MainActivity) getActivity()).updateSideItemOfTomorrow(planTasks.size());
        }
        mTomorrowAdapter.changeAllData(planTasks);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTomorrowPresenter != null) {
            mTomorrowPresenter.onDestroy();
            mTomorrowPresenter = null;
        }
        if (mTomorrowAdapter != null) {
            mTomorrowAdapter.removeOnItemClickListener();
            mTomorrowAdapter.releaseActivity();
        }
    }

    @Override
    public void onDeleteItemClick(PlanTask task) {

    }

    @Override
    public void onContentItemClick(PlanTask task) {
        PlanTaskDetailsActivity.startActivity(getActivity(), task, false, PlanTaskDetailsActivity.TYPE_MODIFY);
    }
}
