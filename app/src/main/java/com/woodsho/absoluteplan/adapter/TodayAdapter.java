package com.woodsho.absoluteplan.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.bean.PlanTask;
import com.woodsho.absoluteplan.common.PlanTaskState;
import com.woodsho.absoluteplan.data.CachePlanTaskStore;
import com.woodsho.absoluteplan.service.UserActionService;
import com.woodsho.absoluteplan.utils.CommonUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by hewuzhao on 17/12/10.
 */

public class TodayAdapter extends RecyclerView.Adapter {
    private static final String TAG = "TodayAdapter";

    private static final int PLANTASK_TYPE_NORMAL = 0;
    private static final int PLANTASK_TYPE_EMPTY = 2;

    private Context mContext;
    private List<PlanTask> mNormalPlanTaskList;
    private OnItemClickListener mOnItemClickListener;

    private boolean mIsDeleteAble = true;

    public TodayAdapter(Context context) {
        mContext = context;
        mNormalPlanTaskList = new ArrayList<>();
    }

    public void releaseActivity() {
        mContext = null;
    }

    public interface OnItemClickListener {
        void onDeleteItemClick(PlanTask task);
        void onContentItemClick(PlanTask task);
    }

    public void addOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void removeOnItemClickListener() {
        mOnItemClickListener = null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater;
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            inflater = activity.getLayoutInflater();
        } else {
            inflater = LayoutInflater.from(mContext);
        }
        if (viewType == PLANTASK_TYPE_NORMAL) {
            return new PlanTaskNormalViewHolder(inflater.inflate(R.layout.item_plantask_normal_layout, parent, false));
        } else if (viewType == PLANTASK_TYPE_EMPTY){
            return new PlanTaskEmptyViewHolder(inflater.inflate(R.layout.empty_layout, parent, false));
        } else {
            Log.e(TAG, "error viewType: " + viewType);
            return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Resources res = mContext.getResources();
        if (holder instanceof PlanTaskNormalViewHolder) {
            final PlanTaskNormalViewHolder viewHolder = (PlanTaskNormalViewHolder) holder;
            final PlanTask planTask = mNormalPlanTaskList.get(position);
            viewHolder.mContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onContentItemClick(planTask);
                    }
                }
            });
            viewHolder.mContent.setBackground(res.getDrawable(R.drawable.item_plantask_normal_bg_selector));

            viewHolder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        removeItem(planTask, holder);
                        mOnItemClickListener.onDeleteItemClick(planTask);
                    }
                }
            });

            viewHolder.mTitle.setText(planTask.title);
            viewHolder.mDescrible.setText(planTask.describe);
            SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy/MM/dd&HH:mm");
            String strBD = sdFormatter.format(planTask.time);
            strBD = strBD.split("&")[1];
            viewHolder.mTime.setText(strBD);
            viewHolder.mCheckBox.setChecked(false);
            viewHolder.mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePlanTaskState(planTask, holder);
                }
            });

        } else if (holder instanceof PlanTaskBottomViewHolder){

        }
    }

    @Override
    public int getItemViewType(int position) {
        int normalSize = mNormalPlanTaskList.size();
        if (normalSize <= 0) {
            return PLANTASK_TYPE_EMPTY;
        }

        return PLANTASK_TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        int normalSize = mNormalPlanTaskList.size();
        if (normalSize <= 0) {
            return 1;
        }
        return normalSize;
    }

    private class PlanTaskNormalViewHolder extends RecyclerView.ViewHolder {
        public View mPriorityView;
        public CheckBox mCheckBox;
        public TextView mTitle;
        public TextView mDescrible;
        public TextView mTime;
        public TextView mDelete;
        public RelativeLayout mContent;

        public PlanTaskNormalViewHolder(View itemView) {
            super(itemView);
            mPriorityView = itemView.findViewById(R.id.item_plantask_priority_view);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.item_plantask_checkbox);
            mTitle = (TextView) itemView.findViewById(R.id.item_plantask_title);
            mDescrible = (TextView) itemView.findViewById(R.id.item_plantask_describle);
            mTime = (TextView) itemView.findViewById(R.id.item_plantask_time);
            mDelete = (TextView) itemView.findViewById(R.id.slide_delete_item_plantask);
            mContent = (RelativeLayout) itemView.findViewById(R.id.slide_content_item_plantask_normal);
        }
    }

    public class PlanTaskBottomViewHolder extends RecyclerView.ViewHolder {

        public PlanTaskBottomViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class PlanTaskEmptyViewHolder extends RecyclerView.ViewHolder {

        public PlanTaskEmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void changeAllData(List<PlanTask> planTasks) {
        distinguishData(planTasks);
    }

    public void removeItem(PlanTask planTask, RecyclerView.ViewHolder holder) {
        if (mIsDeleteAble) {//此时为增加动画效果，刷新部分数据源，防止删除错乱
            mIsDeleteAble = false;//初始值为true,当点击删除按钮以后，休息0.5秒钟再让他为
            mNormalPlanTaskList.remove(planTask);//删除数据源

            int pos = holder.getAdapterPosition();
            notifyItemRemoved(pos);//刷新被删除的地方
            notifyItemRangeChanged(pos, getItemCount()); //刷新被删除数据，以及其后面的数据

            CachePlanTaskStore planTaskStore = CachePlanTaskStore.getInstance();
            planTaskStore.removePlanTask(planTask, true);

            Intent intent = new Intent(mContext, UserActionService.class);
            intent.setAction(UserActionService.INTENT_ACTION_REMOVE_ONE_PLANTASK);
            intent.putExtra(UserActionService.EXTRA_PLANTASK, planTask);
            Log.d(TAG, "removeItem , start intent service: UserActionService");
            mContext.startService(intent);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(120);//休息
                        mIsDeleteAble = true;//可点击按钮
                    } catch (Exception ex) {
                        Log.e(TAG, "ex: " + ex);
                    }
                }
            }).start();
        }
    }

    private void updatePlanTaskState(final PlanTask planTask, RecyclerView.ViewHolder holder) {
        if (mIsDeleteAble) {//此时为增加动画效果，刷新部分数据源，防止删除错乱
            mIsDeleteAble = false;//初始值为true,当点击删除按钮以后，休息0.5秒钟再让他为
            mNormalPlanTaskList.remove(planTask);//删除数据源
            planTask.state = PlanTaskState.STATE_FINISHED;
            int pos = holder.getAdapterPosition();
            notifyItemRemoved(pos);//刷新被删除的地方
            notifyItemRangeChanged(pos, getItemCount()); //刷新被删除数据，以及其后面的数据

            CachePlanTaskStore planTaskStore = CachePlanTaskStore.getInstance();
            planTaskStore.updatePlanTaskState(planTask, true);

            Intent intent = new Intent(mContext, UserActionService.class);
            intent.setAction(UserActionService.INTENT_ACTION_UPDATE_ONE_PLANTASK_STATE);
            intent.putExtra(UserActionService.EXTRA_PLANTASK, planTask);
            Log.d(TAG, "updatePlanTaskState , start intent service: UserActionService");
            mContext.startService(intent);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(120);//休息
                        mIsDeleteAble = true;//可点击按钮
                    } catch (Exception ex) {
                        Log.e(TAG, "ex: " + ex);
                    }
                }
            }).start();
        }
    }

    private void distinguishData(List<PlanTask> planTasks) {
        if (planTasks == null || planTasks.size() <= 0) {
            mNormalPlanTaskList.clear();
            notifyDataSetChanged();
            return;
        }

        Collections.sort(planTasks, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof PlanTask && o2 instanceof PlanTask) {
                    PlanTask task1 = (PlanTask) o1;
                    PlanTask task2 = (PlanTask) o2;
                    return task1.time < task2.time ? 1 : -1;
                }
                return 0;
            }
        });
        if (CommonUtil.isTheSame(planTasks, mNormalPlanTaskList)) {
            return;
        }
        mNormalPlanTaskList.clear();
        mNormalPlanTaskList = planTasks;
        notifyDataSetChanged();
    }
}
