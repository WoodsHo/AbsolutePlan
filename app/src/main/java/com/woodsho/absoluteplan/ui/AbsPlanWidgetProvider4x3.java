package com.woodsho.absoluteplan.ui;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.bean.PlanTask;
import com.woodsho.absoluteplan.data.CachePlanTaskStore;
import com.woodsho.absoluteplan.service.WidgetRemoteViewServiceImp;

import java.util.List;

import static com.woodsho.absoluteplan.ui.AbsPlanWidgetProvider.EXTRA_PLANTASK_ITEM;

/**
 * Created by hewuzhao on 18/3/3.
 */

@SuppressLint("LongLogTag")
public class AbsPlanWidgetProvider4x3 extends AppWidgetProvider {
    public static final String TAG = "AbsPlanWidgetProvider4x3";

    public static final String ACTION_ADD_PLANTASK_CLICK_4x3 = "com.woodsho.absoluteplan.action.APPWIDGET.ADD_PLANTASK_CLICK_4x3"; // 点击事件的广播ACTION
    public static final String ACTION_LISTVIEW_ITEM_CLICK_4x3 = "com.woodsho.absoluteplan.action.APPWIDGET.LISTVIEW_ITEM_CLICK_4x3";

    /**
     * 每次窗口小部件被更新都调用一次该方法
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d(TAG, "onUpdate 4x3");
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.absplan_widget_layout);
            List<PlanTask> list = CachePlanTaskStore.getInstance().getCacheNormalPlanTaskList();
            if (list == null || list.size() <= 0) {
                remoteViews.setViewVisibility(R.id.widget_empty, View.VISIBLE);
            } else {
                remoteViews.setViewVisibility(R.id.widget_empty, View.INVISIBLE);
            }
            Intent intent = new Intent(context, AbsPlanWidgetProvider4x3.class);
            intent.setAction(ACTION_ADD_PLANTASK_CLICK_4x3);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, R.id.widget_add_plantask, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_add_plantask, pendingIntent);

            //绑定service用来填充listview中的视图
            Intent listviewIntent = new Intent(context, WidgetRemoteViewServiceImp.class);
            remoteViews.setRemoteAdapter(R.id.widget_listview, listviewIntent);

            //添加item的点击事件
            Intent intent1 = new Intent(context, AbsPlanWidgetProvider4x3.class);
            intent1.setAction(ACTION_LISTVIEW_ITEM_CLICK_4x3);
            PendingIntent listviewPendingIntent = PendingIntent.getBroadcast(context, R.id.widget_listview, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.widget_listview, listviewPendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    /**
     * 接收窗口小部件点击时发送的广播
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName provideer_4x3 = new ComponentName(context, AbsPlanWidgetProvider4x3.class);
        int[] ids_4x3 = manager.getAppWidgetIds(provideer_4x3);
        manager.notifyAppWidgetViewDataChanged(ids_4x3, R.id.widget_listview);

        String action = intent.getAction();
        Log.d(TAG, "onReceiver 4x3, s = " + action);
        if (TextUtils.equals(action, ACTION_ADD_PLANTASK_CLICK_4x3)) {
            Intent detailIntent = new Intent();
            detailIntent.setAction("com.woodsho.absoluteplan.action.PLANTASKDETAILSACTIVITY");
            detailIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            detailIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            detailIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            detailIntent.putExtra(PlanTaskDetailsActivity.KEY_EXTRA_SHOW_TYPE, PlanTaskDetailsActivity.TYPE_NEW_BUILD);
            try {
                context.startActivity(detailIntent);
            } catch (ActivityNotFoundException e) {
                Log.w(TAG, e);
            }
        } else if (TextUtils.equals(action, ACTION_LISTVIEW_ITEM_CLICK_4x3)) {
            PlanTask planTask = intent.getParcelableExtra(EXTRA_PLANTASK_ITEM);
            Intent detailIntent = new Intent();
            detailIntent.setAction("com.woodsho.absoluteplan.action.PLANTASKDETAILSACTIVITY");
            detailIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            detailIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            detailIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            detailIntent.putExtra(PlanTaskDetailsActivity.KEY_EXTRA_PLANTASK, planTask);
            detailIntent.putExtra(PlanTaskDetailsActivity.KEY_EXTRA_SHOW_TYPE, PlanTaskDetailsActivity.TYPE_MODIFY);
            try {
                context.startActivity(detailIntent);
            } catch (ActivityNotFoundException e) {
                Log.w(TAG, e);
            }
        }
    }

    /**
     * 每删除一次窗口小部件就调用一次
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    /**
     * 当最后一个该窗口小部件删除时调用该方法
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    /**
     * 当该窗口小部件第一次添加到桌面时调用该方法
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    /**
     * 当小部件大小改变时
     */
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    /**
     * 当小部件从备份恢复时调用该方法
     */
    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }
}
