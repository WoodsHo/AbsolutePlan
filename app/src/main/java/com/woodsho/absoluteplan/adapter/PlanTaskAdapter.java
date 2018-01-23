package com.woodsho.absoluteplan.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.bean.PlanTask;
import com.woodsho.absoluteplan.common.PlanTaskState;
import com.woodsho.absoluteplan.data.CachePlanTaskStore;
import com.woodsho.absoluteplan.service.UserActionService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by hewuzhao on 17/12/12.
 */

public class PlanTaskAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = "PlanTaskAdapter";

    private static final int PLANTASK_TYPE_NORMAL = 0;
    private static final int PLANTASK_TYPE_FINISHED = 1;
    private static final int PLANTASK_TYPE_EMPTY = 2;

    private Context mContext;
    private List<PlanTask> mNormalPlanTaskList;
    private List<PlanTask> mFinishedPlanTaskList;
    private OnItemClickListener mOnItemClickListener;

    public PlanTaskAdapter(Context context) {
        mContext = context;
        mNormalPlanTaskList = new ArrayList<>();
        mFinishedPlanTaskList = new ArrayList<>();
    }

    public void releaseActivity() {
        mContext = null;
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
            return new PlanTaskNormalViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_plantask_normal_layout, parent, false));
        } else if (viewType == PLANTASK_TYPE_FINISHED) {
            return new PlanTaskFinishedViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_plantask_finished_layout, parent, false));
        } else if (viewType == PLANTASK_TYPE_EMPTY){
            return new PlanTaskEmptyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.calendar_empty_layout, parent, false));
        } else {
            Log.e(TAG, "error viewType: " + viewType);
            return null;
        }
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
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
                    updateSPlanTaskState(planTask);
                }
            });

        } else if (holder instanceof PlanTaskFinishedViewHolder) {
            final PlanTaskFinishedViewHolder viewHolder = (PlanTaskFinishedViewHolder) holder;
            final PlanTask planTask = mFinishedPlanTaskList.get(position - mNormalPlanTaskList.size());
            viewHolder.mContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onContentItemClick(planTask);
                    }
                }
            });
            viewHolder.mContent.setBackground(res.getDrawable(R.drawable.item_plantask_finished_bg_selector));

            viewHolder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onDeleteItemClick(planTask);
                    }
                }
            });

            Spannable spanStrikethroughTitel = new SpannableString(planTask.title);
            StrikethroughSpan stSpan = new StrikethroughSpan();  //设置删除线样式
            spanStrikethroughTitel.setSpan(stSpan, 0, planTask.title.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            viewHolder.mTitle.setText(spanStrikethroughTitel);

            Spannable spanStrikethroughDescrible = new SpannableString(planTask.describe);
            spanStrikethroughDescrible.setSpan(stSpan, 0, planTask.describe.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            viewHolder.mDescrible.setText(spanStrikethroughDescrible);

            SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy/MM/dd&HH:mm");
            String strBD = sdFormatter.format(planTask.time);
            strBD = strBD.split("&")[1];
            Spannable spanStrikethroughTime = new SpannableString(strBD);
            spanStrikethroughTime.setSpan(stSpan, 0, strBD.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            viewHolder.mTime.setText(spanStrikethroughTime);

            viewHolder.mCheckBox.setChecked(true);
            viewHolder.mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateSPlanTaskState(planTask);
                }
            });
        } else if (holder instanceof PlanTaskFinishedHintViewHolder) {
            final PlanTaskFinishedHintViewHolder viewHolder = (PlanTaskFinishedHintViewHolder) holder;
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
            if (mFinishedPlanTaskList.size() <= 0) {
                params.height = 0;
            } else {
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            }
            viewHolder.itemView.setLayoutParams(params);
        } else if (holder instanceof PlanTaskBottomViewHolder){

        } else if (holder instanceof PlanTaskEmptyViewHolder) {

        }
    }


    @Override
    public int getItemViewType(int position) {
        int normalSize = mNormalPlanTaskList.size();
        int finishedSize = mFinishedPlanTaskList.size();
        if (normalSize <= 0 && finishedSize <= 0) {
            return PLANTASK_TYPE_EMPTY;
        }

        if (position < normalSize) {
            return PLANTASK_TYPE_NORMAL;
        } else {
            return PLANTASK_TYPE_FINISHED;
        }
    }

    @Override
    public int getItemCount() {
        if (mNormalPlanTaskList.size() <= 0 && mFinishedPlanTaskList.size() <= 0) {
            return 1;
        }
        return mNormalPlanTaskList.size() + mFinishedPlanTaskList.size();
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

    private class PlanTaskFinishedViewHolder extends RecyclerView.ViewHolder {
        public View mPriorityView;
        public CheckBox mCheckBox;
        public TextView mTitle;
        public TextView mDescrible;
        public TextView mTime;
        public TextView mDelete;
        public RelativeLayout mContent;

        public PlanTaskFinishedViewHolder(View itemView) {
            super(itemView);
            mPriorityView = itemView.findViewById(R.id.item_plantask_priority_view);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.item_plantask_checkbox);
            mTitle = (TextView) itemView.findViewById(R.id.item_plantask_title);
            mDescrible = (TextView) itemView.findViewById(R.id.item_plantask_describle);
            mTime = (TextView) itemView.findViewById(R.id.item_plantask_time);
            mDelete = (TextView) itemView.findViewById(R.id.slide_delete_item_plantask);
            mContent = (RelativeLayout) itemView.findViewById(R.id.slide_content_item_plantask_finished);
        }
    }

    public class PlanTaskFinishedHintViewHolder extends RecyclerView.ViewHolder {

        public PlanTaskFinishedHintViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class PlanTaskBottomViewHolder extends RecyclerView.ViewHolder {

        public PlanTaskBottomViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class PlanTaskEmptyViewHolder extends RecyclerView.ViewHolder {
        public CardView mEmptyView;

        public PlanTaskEmptyViewHolder(View itemView) {
            super(itemView);
            mEmptyView = (CardView) itemView.findViewById(R.id.calendar_empty_view);
        }
    }

    public void changeAllData(List<PlanTask> planTasks) {
        distinguishData(planTasks);
    }

    public void resetData() {
        mNormalPlanTaskList.clear();
        mFinishedPlanTaskList.clear();
    }

    public void insertItem(PlanTask planTask) {
        mNormalPlanTaskList.add(planTask);
        notifyItemInserted(mNormalPlanTaskList.size() - 1);
    }

    public void removeItem(PlanTask planTask) {
        if (mNormalPlanTaskList.remove(planTask)) {
            notifyDataSetChanged();
        } else if (mFinishedPlanTaskList.remove(planTask)) {
            notifyDataSetChanged();
        } else {
            return;
        }
        CachePlanTaskStore.getInstance().removePlanTask(planTask, true);
        Intent intent = new Intent(mContext, UserActionService.class);
        intent.setAction(UserActionService.INTENT_ACTION_REMOVE_ONE_PLANTASK);
        intent.putExtra(UserActionService.EXTRA_PLANTASK, planTask);
        Log.d(TAG, "removeItem , start intent service: UserActionService");
        mContext.startService(intent);
    }

    private void changePlanItem(PlanTask planTask) {
        int i = mNormalPlanTaskList.indexOf(planTask);
        if (i != -1) {
            notifyItemChanged(i);
        }
    }

    private void updateSPlanTaskState(final PlanTask planTask) {
        CachePlanTaskStore planTaskStore = CachePlanTaskStore.getInstance();
        switch (planTask.state) {
            case PlanTaskState.STATE_NORMAL:
                planTask.state = PlanTaskState.STATE_FINISHED;
                mFinishedPlanTaskList.add(planTask);
                mNormalPlanTaskList.remove(planTask);
                break;
            case PlanTaskState.STATE_FINISHED:
                planTask.state = PlanTaskState.STATE_NORMAL;
                mFinishedPlanTaskList.remove(planTask);
                mNormalPlanTaskList.add(planTask);
                break;
            default:
                Log.e(TAG, "error state: " + planTask.state);
                return;
        }

        if (mNormalPlanTaskList.size() > 0) {
            Collections.sort(mNormalPlanTaskList,  new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    if (o1 instanceof PlanTask && o2 instanceof PlanTask) {
                        PlanTask task1 = (PlanTask) o1;
                        PlanTask task2 = (PlanTask) o2;
                        return task1.time > task2.time ? 1 : -1;
                    }
                    return 0;
                }
            });
        }
        if (mFinishedPlanTaskList.size() > 0) {
            Collections.sort(mFinishedPlanTaskList,  new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    if (o1 instanceof PlanTask && o2 instanceof PlanTask) {
                        PlanTask task1 = (PlanTask) o1;
                        PlanTask task2 = (PlanTask) o2;
                        return task1.time > task2.time ? 1 : -1;
                    }
                    return 0;
                }
            });
        }
        notifyDataSetChanged();

        planTaskStore.updatePlanTaskState(planTask, true);
        Intent intent = new Intent(mContext, UserActionService.class);
        intent.setAction(UserActionService.INTENT_ACTION_UPDATE_ONE_PLANTASK_STATE);
        intent.putExtra(UserActionService.EXTRA_PLANTASK, planTask);
        Log.d(TAG, "updatePlanTaskState , start intent service: UserActionService");
        mContext.startService(intent);
    }

    private void distinguishData(List<PlanTask> planTasks) {
        mNormalPlanTaskList.clear();
        mFinishedPlanTaskList.clear();
        if (planTasks == null || planTasks.size() <= 0) {
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

        for (int i = 0; i < planTasks.size(); i++) {
            PlanTask planTask = planTasks.get(i);
            if (planTask.state == PlanTaskState.STATE_FINISHED) {
                mFinishedPlanTaskList.add(planTask);
            } else {
                mNormalPlanTaskList.add(planTask);
            }

        }
        notifyDataSetChanged();
    }

}
