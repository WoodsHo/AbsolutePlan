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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hewuzhao on 17/12/10.
 */

public class FinishedAdapter extends RecyclerView.Adapter {
    private static final String TAG = "FinishedAdapter";

    private static final int PLANTASK_TYPE_FINISHED = 0;
    private static final int PLANTASK_TYPE_EMPTY = 1;
    private static final int PLANTASK_TYPE_HEADER = 2;

    private Context mContext;
    private List<PlanTask> mFinishedPlanTaskList;
    private FinishedAdapter.OnItemClickListener mOnItemClickListener;

    public FinishedAdapter(Context context) {
        mContext = context;
        mFinishedPlanTaskList = new ArrayList<>();
    }

    public void releaseActivity() {
        mContext = null;
    }

    public interface OnItemClickListener {
        void onDeleteItemClick(PlanTask task);
        void onContentItemClick(PlanTask task);
    }

    public void addOnItemClickListener(FinishedAdapter.OnItemClickListener listener) {
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
        if (viewType == PLANTASK_TYPE_FINISHED) {
            return new PlanTaskFinishedViewHolder(inflater.inflate(R.layout.item_plantask_finished_layout, parent, false));
        } else if (viewType == PLANTASK_TYPE_EMPTY){
            return new PlanTaskEmptyViewHolder(inflater.inflate(R.layout.empty_layout, parent, false));
        } else if (viewType == PLANTASK_TYPE_HEADER){
            return new PlanTaskHeaderViewHolder(inflater.inflate(R.layout.item_plantask_header_layout, parent, false));
        } else {
            Log.e(TAG, "error viewType: " + viewType);
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Resources res = mContext.getResources();
        if (holder instanceof PlanTaskFinishedViewHolder) {
            final PlanTaskFinishedViewHolder viewHolder = (PlanTaskFinishedViewHolder) holder;
            final PlanTask planTask = mFinishedPlanTaskList.get(getRealPos().get(position));
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
                        notifyItemRemoved(position);
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
                    updatePlanTaskState(planTask);
                }
            });
        } else if (holder instanceof PlanTaskBottomViewHolder){

        } else if (holder instanceof PlanTaskHeaderViewHolder) {
            PlanTaskHeaderViewHolder viewHolder = (PlanTaskHeaderViewHolder) holder;
            List<String> headers = getHeaders();
            int pso = getRealPos().get(position);
            String header = headers.get(pso);
            String str = header.substring(0, header.length() - 1);
            Spannable spanStrikethroughTitel = new SpannableString(str);
            StrikethroughSpan stSpan = new StrikethroughSpan();
            spanStrikethroughTitel.setSpan(stSpan, 0, str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            viewHolder.mCardView.setCardBackgroundColor(res.getColor(R.color.item_bg_finished));
            viewHolder.mTextView.setTextColor(res.getColor(R.color.black_50));
            viewHolder.mTextView.setText(spanStrikethroughTitel);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int finishedSize = mFinishedPlanTaskList.size();
        if (finishedSize <= 0) {
            return PLANTASK_TYPE_EMPTY;
        }
        List<Integer> list = getItemType();
        return list.get(position);
    }

    private List<Integer> getItemType() {
        List<Integer> list = new ArrayList<>();
        int finishedSize = mFinishedPlanTaskList.size();
        String preTime = "";
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy年MM月dd日");
        for (int i = 0; i < finishedSize; i++) {
            PlanTask task = mFinishedPlanTaskList.get(i);
            String time = sdFormatter.format(task.time);
            if (!time.equals(preTime)) {
                list.add(PLANTASK_TYPE_HEADER);
                list.add(PLANTASK_TYPE_FINISHED);
            } else {
                list.add(PLANTASK_TYPE_FINISHED);
            }
            preTime = time;
        }
        return list;
    }

    private Map<Integer, Integer> getRealPos() {
        Map<Integer, Integer> map = new HashMap<>();
        int finishedSize = mFinishedPlanTaskList.size();
        if (finishedSize <= 0) {
            return map;
        }

        int pos = 0;
        int headerPos = 0;
        String preTime = "";
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy年MM月dd日");
        for (int i = 0; i < finishedSize; i++) {
            PlanTask task = mFinishedPlanTaskList.get(i);
            String time = sdFormatter.format(task.time);
            if (!time.equals(preTime)) {
                map.put(pos++, headerPos++);
                map.put(pos++, i);
            } else {
                map.put(pos++, i);
            }
            preTime = time;
        }
        return map;
    }

    public List<String> getHeaders() {
        List<String> list = new ArrayList<>();
        int finishedSize = mFinishedPlanTaskList.size();
        if (finishedSize <= 0) {
            return list;
        }

        String preTime = "";
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy年MM月dd日");
        for (int i = 0; i < finishedSize; i++) {
            PlanTask task = mFinishedPlanTaskList.get(i);
            String time = sdFormatter.format(task.time);
            if (CommonUtil.isToYear(task.time)) {
                time = time.split("年")[1];
            }
            if (!time.equals(preTime)) {
                if (CommonUtil.isToday(task.time)) {
                    list.add("今天");
                } else if (CommonUtil.isTomorrow(task.time)) {
                    list.add("明天");
                } else {
                    list.add(time);
                }
            }
            preTime = time;
        }

        return list;
    }

    @Override
    public int getItemCount() {
        int finishedSize = mFinishedPlanTaskList.size();
        if (finishedSize <= 0) {
            return 1;
        }
        return finishedSize + getHeaders().size();
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

    public class PlanTaskHeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public CardView mCardView;

        public PlanTaskHeaderViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.item_plantask_header_text);
            mCardView = (CardView) itemView.findViewById(R.id.header_card_view);
        }
    }

    public void changeAllData(List<PlanTask> planTasks) {
        distinguishData(planTasks);
    }

    public void removeItem(PlanTask planTask) {
        if (mFinishedPlanTaskList.remove(planTask)) {
//            notifyDataSetChanged();
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

    private void updatePlanTaskState(final PlanTask planTask) {
        CachePlanTaskStore planTaskStore = CachePlanTaskStore.getInstance();
        switch (planTask.state) {
            case PlanTaskState.STATE_FINISHED:
                planTask.state = PlanTaskState.STATE_NORMAL;
                mFinishedPlanTaskList.remove(planTask);
                break;
            default:
                Log.e(TAG, "error state: " + planTask.state);
                return;
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
            }
        }
        notifyDataSetChanged();
    }
}
