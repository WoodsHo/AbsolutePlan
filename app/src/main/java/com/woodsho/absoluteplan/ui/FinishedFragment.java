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
import com.woodsho.absoluteplan.adapter.FinishedAdapter;
import com.woodsho.absoluteplan.bean.PlanTask;
import com.woodsho.absoluteplan.presenter.FinishedPresenter;
import com.woodsho.absoluteplan.utils.CommonUtil;

/**
 * Created by hewuzhao on 17/12/10.
 */

public class FinishedFragment extends BaseFragment implements FinishedAdapter.OnItemClickListener {
    public FinishedAdapter mFinishedAdapter;
    public FinishedPresenter mFinishedPresenter;

    @Nullable
    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_finished_layout, container, false);
    }

    @Override
    protected void bindView(View view) {
        Resources res = getResources();
        Context context = AbsolutePlanApplication.sAppContext;
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.finished_recyclerview);
        mFinishedAdapter = new FinishedAdapter(context);
        mFinishedAdapter.addOnItemClickListener(this);
        recyclerView.setAdapter(mFinishedAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        mFinishedPresenter = new FinishedPresenter(this);
    }

    @Override
    protected void bindData() {
        loadPlanTaskList();
    }

    public void loadPlanTaskList() {
        if (mFinishedPresenter != null) {
            mFinishedPresenter.loadData();
        }
    }

    public void loadDataSuccess() {
        mFinishedAdapter.changeAllData(CommonUtil.getFinishedPlanTaskList());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFinishedPresenter != null) {
            mFinishedPresenter.onDestroy();
            mFinishedPresenter = null;
        }
        if (mFinishedAdapter != null) {
            mFinishedAdapter.removeOnItemClickListener();
        }
    }

    @Override
    public void onDeleteItemClick(final PlanTask task) {
        mFinishedAdapter.removeItem(task);
    }

    @Override
    public void onContentItemClick(PlanTask task) {
        Intent intent = new Intent(getActivity(), PlanTaskDetailsActivity.class);
        intent.putExtra(PlanTaskDetailsActivity.KEY_PLANTASK, task);
        intent.putExtra(PlanTaskDetailsActivity.KEY_SHOW_TYPE, PlanTaskDetailsActivity.TYPE_MODIFY);
        startActivity(intent);
    }
}
