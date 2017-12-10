package com.woodsho.absoluteplan.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.woodsho.absoluteplan.AbsolutePlanApplication;
import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.adapter.TomorrowAdapter;
import com.woodsho.absoluteplan.bean.PlanTask;
import com.woodsho.absoluteplan.presenter.TomorrowPresenter;
import com.woodsho.absoluteplan.utils.CommonUtil;

/**
 * Created by hewuzhao on 17/12/10.
 */

public class TomorrowFragment extends BaseFragment implements TomorrowAdapter.OnItemClickListener {
    public TomorrowAdapter mTomorrowAdapter;
    public TomorrowPresenter mTomorrowPresenter;

    @Nullable
    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_tomorrow_layout, container, false);
    }

    @Override
    protected void bindView(View view) {
        Resources res = getResources();
        Context context = AbsolutePlanApplication.sAppContext;
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.tomorrow_recyclerview);

        mTomorrowAdapter = new TomorrowAdapter(context);
        mTomorrowAdapter.addOnItemClickListener(this);
        recyclerView.setAdapter(mTomorrowAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
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
        mTomorrowAdapter.changeAllData(CommonUtil.getTomorrowPlanTaskList());
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
        }
    }

    @Override
    public void onDeleteItemClick(PlanTask task) {
        mTomorrowAdapter.removeItem(task);
    }

    @Override
    public void onContentItemClick(PlanTask task) {
//        Intent intent = new Intent(getActivity(), ScheduleTaskDetailsActivity.class);
//        intent.putExtra(ScheduleTaskDetailsActivity.KEY_SCHEDULETASK, task);
//        intent.putExtra(ScheduleTaskDetailsActivity.KEY_SHOW_TYPE, ScheduleTaskDetailsActivity.TYPE_MODIFY);
//        startActivity(intent);
    }
}
