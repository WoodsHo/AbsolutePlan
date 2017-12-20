package com.woodsho.absoluteplan.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.necer.ncalendar.calendar.NCalendar;
import com.necer.ncalendar.listener.OnCalendarChangedListener;
import com.woodsho.absoluteplan.AbsolutePlanApplication;
import com.woodsho.absoluteplan.MainActivity;
import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.adapter.PlanTaskAdapter;
import com.woodsho.absoluteplan.bean.PlanTask;
import com.woodsho.absoluteplan.data.CachePlanTaskStore;
import com.woodsho.absoluteplan.presenter.CalendarPresenter;
import com.woodsho.absoluteplan.widget.PlanTaskRecyclerView;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hewuzhao on 17/12/12.
 */

public class CalendarFragment extends BaseFragment implements PlanTaskAdapter.OnItemClickListener {
    public static final String TAG = "CalendarFragment";

    private NCalendar mNCalendar;
    private PlanTaskRecyclerView mPlanTaskRecyclerView;
    private PlanTaskAdapter mPlanTaskAdapter;
    private int mCurrentSelectedYear, mCurrentSelectedMonth, mCurrentSelectedDay;

    public CalendarPresenter mCalendarPresenter;

    public Handler mHandler;
    @Nullable
    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_calendar_layout, container, false);
    }

    @Override
    protected void bindView(View view) {
        mHandler = new Handler();
        mNCalendar = (NCalendar) view.findViewById(R.id.ncalendar);
        mNCalendar.setOnCalendarChangedListener(new OnCalendarChangedListener() {
            @Override
            public void onCalendarChanged(DateTime dateTime) {
                setCurrentSelectDate(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
                loadPlanTaskList();
            }
        });
        mPlanTaskRecyclerView = (PlanTaskRecyclerView) view.findViewById(R.id.recyclerView);
        initPlanTaskList();
        mCalendarPresenter = new CalendarPresenter(this);
    }

    public void JumpToToday() {
        mNCalendar.toToday();
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void bindData() {
        super.bindData();
        loadPlanTaskList();
    }

    public void loadPlanTaskList() {
        if (mCalendarPresenter != null) {
            mCalendarPresenter.loadData();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public void loadDataSuccess(List<PlanTask> allList) {
        mPlanTaskAdapter.resetData();
        List<PlanTask> list = new ArrayList<>();
        if (allList != null && allList.size() > 0) {
            String monthStr = mCurrentSelectedMonth < 10 ? "0" + mCurrentSelectedMonth : "" + mCurrentSelectedMonth;
            String dayStr = mCurrentSelectedDay < 10 ? "0" + mCurrentSelectedDay : "" + mCurrentSelectedDay;
            String timeStr = mCurrentSelectedYear + monthStr + dayStr;
            SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyyMMdd");
            for (int i = 0; i < allList.size(); i++) {
                PlanTask task = allList.get(i);
                String strBD = sdFormatter.format(task.time);
                if (strBD.equals(timeStr)) {
                    list.add(task);
                }
            }
        }

        mPlanTaskAdapter.changeAllData(list);

        List<String> strList = new ArrayList<>();
        List<PlanTask> all = CachePlanTaskStore.getInstance().getCachePlanTaskList();
        if (all != null && all.size() > 0) {
            SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd");
            for (PlanTask task : all) {
                strList.add(sdFormatter.format(task.time));
            }
            mNCalendar.setPoint(strList);
        }
    }

    public void loadDataFailed() {

    }

    private void initPlanTaskList() {
        mPlanTaskRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        mPlanTaskRecyclerView.setItemAnimator(itemAnimator);
        mPlanTaskAdapter = new PlanTaskAdapter(AbsolutePlanApplication.sAppContext);
        mPlanTaskAdapter.addOnItemClickListener(this);
        mPlanTaskRecyclerView.setAdapter(mPlanTaskAdapter);
    }

    private void setCurrentSelectDate(int year, int month, int day) {
        mCurrentSelectedYear = year;
        mCurrentSelectedMonth = month;
        mCurrentSelectedDay = day;
        if (mActivity instanceof MainActivity) {
            ((MainActivity) mActivity).updateToolbarDate(year, month, day);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCalendarPresenter != null) {
            mCalendarPresenter.onDestroy();
            mCalendarPresenter = null;
        }
        if (mPlanTaskAdapter != null) {
            mPlanTaskAdapter.removeOnItemClickListener();
        }
    }

    @Override
    public void onDeleteItemClick(final PlanTask task) {
        mPlanTaskAdapter.removeItem(task);
    }

    @Override
    public void onContentItemClick(PlanTask task) {
        Intent intent = new Intent(getActivity(), PlanTaskDetailsActivity.class);
        intent.putExtra(PlanTaskDetailsActivity.KEY_PLANTASK, task);
        intent.putExtra(PlanTaskDetailsActivity.KEY_SHOW_TYPE, PlanTaskDetailsActivity.TYPE_MODIFY);
        startActivity(intent);
    }
}
