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
import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.adapter.AllAdapter;
import com.woodsho.absoluteplan.bean.PlanTask;
import com.woodsho.absoluteplan.presenter.AllPresenter;
import com.woodsho.absoluteplan.widget.CommonRecyclerView;

import java.util.List;

/**
 * Created by hewuzhao on 17/12/10.
 */

public class AllFragment extends BaseFragment implements AllAdapter.OnItemClickListener {
    public static final String TAG = "AllFragment";
    public AllAdapter mAllAdapter;
    public AllPresenter mAllPresenter;
    public List<PlanTask> mPlanTaskList;
    public CommonRecyclerView mRecyclerView;

    @Nullable
    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_all_layout, container, false);
    }

    @Override
    protected void bindView(View view) {
        Resources res = getResources();
        Context context = AbsolutePlanApplication.sAppContext;
        mRecyclerView = (CommonRecyclerView) view.findViewById(R.id.all_recyclerview);
        mAllAdapter = new AllAdapter(mActivity);
        mAllAdapter.addOnItemClickListener(this);
        mRecyclerView.setAdapter(mAllAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(manager);
        mAllPresenter = new AllPresenter(this);
    }

    @Override
    protected void bindData() {
        loadPlanTaskList();
    }

    public void loadPlanTaskList() {
        if (mAllPresenter != null) {
            mAllPresenter.loadData();
        }
    }

    public void loadDataSuccess(List<PlanTask> allList) {
        mPlanTaskList = allList;
        mAllAdapter.changeAllData(allList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAllPresenter != null) {
            mAllPresenter.onDestroy();
            mAllPresenter = null;
        }
        if (mAllAdapter != null) {
            mAllAdapter.removeOnItemClickListener();
            mAllAdapter.releaseActivity();
        }
    }

    @Override
    public void onDeleteItemClick(final PlanTask task) {

    }

    @Override
    public void onContentItemClick(PlanTask task) {
        PlanTaskDetailsActivity.startActivity(getActivity(), task, false, PlanTaskDetailsActivity.TYPE_MODIFY);
    }
}
