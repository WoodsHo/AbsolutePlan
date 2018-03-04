package com.woodsho.absoluteplan.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.bean.PlanTask;
import com.woodsho.absoluteplan.data.CachePlanTaskStore;
import com.woodsho.absoluteplan.ui.AbsPlanWidgetProvider;
import com.woodsho.absoluteplan.utils.CommonUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.woodsho.absoluteplan.ui.AbsPlanWidgetProvider.ACTION_LISTVIEW_ITEM_CLICK;

/**
 * Created by hewuzhao on 18/2/23.
 */

@SuppressLint("LongLogTag")
public class WidgetRemoteViewServiceImp extends RemoteViewsService {
    public static final String TAG = "WidgetRemoteViewServiceImp";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteviewsFactoryImp(this, intent);
    }

    private int sortByTime(Object o1, Object o2) {
        if (o1 instanceof PlanTask && o2 instanceof PlanTask) {
            PlanTask task1 = (PlanTask) o1;
            PlanTask task2 = (PlanTask) o2;
            if (CommonUtil.isToday(task1.time)) {
                if (CommonUtil.isToday(task2.time)) {
                    return task1.time < task2.time ? 1 : -1;
                } else {
                    return -1;
                }
            } else if (CommonUtil.isTomorrow(task1.time)) {
                if (CommonUtil.isToday(task2.time)) {
                    return 1;
                } else if (CommonUtil.isTomorrow(task2.time)) {
                    return task1.time < task2.time ? 1 : -1;
                } else {
                    return -1;
                }
            } else {
                if (CommonUtil.isToday(task2.time) || CommonUtil.isTomorrow(task2.time)) {
                    return 1;
                } else {
                    return task1.time < task2.time ? 1 : -1;
                }
            }
        }
        return 0;
    }

    private class RemoteviewsFactoryImp implements RemoteViewsFactory {
        private ArrayList<PlanTask> mPlanTaskList;
        private Intent requestIntent;
        private Context requestContext;


        public RemoteviewsFactoryImp(Context context, Intent intent) {
            requestContext = context;
            requestIntent = intent;
        }

        @Override
        public void onCreate() {
            Log.d(TAG, "onCreate");
            mPlanTaskList = (ArrayList<PlanTask>) CachePlanTaskStore.getInstance().getCacheNormalPlanTaskList();
            if (mPlanTaskList == null || mPlanTaskList.size() <= 0) {
                return;
            }
            Collections.sort(mPlanTaskList, new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    return sortByTime(o1, o2);
                }
            });
        }

        @Override
        public void onDataSetChanged() {
            /*
             * appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listview);
             * 使用该通知更新数据源，会调用onDataSetChanged
             */
            Log.d(TAG, "onDataSetChanged");
            CachePlanTaskStore cachePlanTaskStore = CachePlanTaskStore.getInstance();
            mPlanTaskList = (ArrayList<PlanTask>) cachePlanTaskStore.getCacheNormalPlanTaskList();
            if (mPlanTaskList == null || mPlanTaskList.size() <= 0) {
                if (!cachePlanTaskStore.isPlanTaskInitializedFinished()) {
                    Log.d(TAG, "not init finished");
                    return;
                }
                Log.e(TAG, "no cache plantask list");
                return;
            }
            Log.d(TAG, "size: " + mPlanTaskList.size());
            Collections.sort(mPlanTaskList, new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    return sortByTime(o1, o2);
                }
            });
        }

        @Override
        public void onDestroy() {
            Log.d(TAG, "onDestroy service");
            if (mPlanTaskList != null) {
                mPlanTaskList.clear();
            }
        }

        @Override
        public int getCount() {
            if (mPlanTaskList == null || mPlanTaskList.size() <= 0) {
                Log.w(TAG, "app widget no plantask list");
                return 0;
            }
            int size = mPlanTaskList.size();
            Log.d(TAG, "size: " + size);
            return size;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (mPlanTaskList == null || mPlanTaskList.size() <= 0) {
                Log.e(TAG, "getViewAt " + position + ", no list");
                return null;
            }
            RemoteViews remoteViews = new RemoteViews(requestContext.getPackageName(), R.layout.item_widget_layout);
            PlanTask task = mPlanTaskList.get(position);
            remoteViews.setTextViewText(R.id.item_widget_title, task.title);
            remoteViews.setTextViewText(R.id.item_widget_time, getTime(task.time));
            remoteViews.setTextViewText(R.id.item_widget_describle, task.describe);

            Bundle extras = new Bundle();
            extras.putParcelable(AbsPlanWidgetProvider.EXTRA_PLANTASK_ITEM, mPlanTaskList.get(position));
            Intent fillInIntent = new Intent(ACTION_LISTVIEW_ITEM_CLICK);
            fillInIntent.putExtras(extras);
            remoteViews.setOnClickFillInIntent(R.id.item_widget_relativelayout, fillInIntent);
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }

    /**
     * @param time
     * @return
     * 时间间隔
     */
    public String getTime(long time) {
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
        String strBD = sdFormatter.format(time);
        if (CommonUtil.isToday(time)) {
            return "今天";
        } else if (CommonUtil.isTomorrow(time)) {
            return "明天";
        } else if (CommonUtil.isToYear(time)){
            return strBD.substring(5, 11);
        } else {
            return strBD.substring(0, 8);
        }

    }
}
