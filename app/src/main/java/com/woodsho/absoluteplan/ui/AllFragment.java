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
import com.woodsho.absoluteplan.adapter.AllAdapter;
import com.woodsho.absoluteplan.bean.PlanTask;
import com.woodsho.absoluteplan.presenter.AllPresenter;

import java.util.List;

/**
 * Created by hewuzhao on 17/12/10.
 */

public class AllFragment extends BaseFragment implements AllAdapter.OnItemClickListener {
    public static final String TAG = "AllFragment";
    public AllAdapter mAllAdapter;
    public AllPresenter mAllPresenter;
    public List<PlanTask> mPlanTaskList;

    @Nullable
    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_all_layout, container, false);
    }

    @Override
    protected void bindView(View view) {
        Resources res = getResources();
        Context context = AbsolutePlanApplication.sAppContext;
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.all_recyclerview);

        mAllAdapter = new AllAdapter(context);
        mAllAdapter.addOnItemClickListener(this);
        recyclerView.setAdapter(mAllAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
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
        }
    }

    @Override
    public void onDeleteItemClick(final PlanTask task) {
        mAllAdapter.removeItem(task);
    }

    @Override
    public void onContentItemClick(PlanTask task) {
//        Intent intent = new Intent(getActivity(), PlanTaskDetailsActivity.class);
//        intent.putExtra(PlanTaskDetailsActivity.KEY_PLANTASK, task);
//        intent.putExtra(PlanTaskDetailsActivity.KEY_SHOW_TYPE, PlanTaskDetailsActivity.TYPE_MODIFY);
//        startActivity(intent);
    }
}
