package com.woodsho.absoluteplan.ui;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.woodsho.absoluteplan.AbsolutePlanApplication;
import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.adapter.TodayAdapter;
import com.woodsho.absoluteplan.bean.PlanTask;
import com.woodsho.absoluteplan.presenter.TodayPresenter;
import com.woodsho.absoluteplan.utils.CommonUtil;

/**
 * Created by hewuzhao on 17/12/10.
 */

public class TodayFragment extends BaseFragment implements TodayAdapter.OnItemClickListener {
    public TodayAdapter mTodayAdapter;
    public TodayPresenter mTodayPresenter;

    @Nullable
    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_today_layout, container, false);
    }

    @Override
    protected void bindView(View view) {
        Resources res = getResources();
        Context context = AbsolutePlanApplication.sAppContext;
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.today_recyclerview);

        mTodayAdapter = new TodayAdapter(context);
        mTodayAdapter.addOnItemClickListener(this);
        recyclerView.setAdapter(mTodayAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
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
        mTodayAdapter.changeAllData(CommonUtil.getTodayPlanTaskList());
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
        }
    }

    @Override
    public void onDeleteItemClick(final PlanTask task) {
        mTodayAdapter.removeItem(task);
    }

    @Override
    public void onContentItemClick(PlanTask task) {
//        Intent intent = new Intent(getActivity(), ScheduleTaskDetailsActivity.class);
//        intent.putExtra(ScheduleTaskDetailsActivity.KEY_SCHEDULETASK, task);
//        intent.putExtra(ScheduleTaskDetailsActivity.KEY_SHOW_TYPE, ScheduleTaskDetailsActivity.TYPE_MODIFY);
//        startActivity(intent);
    }
}
