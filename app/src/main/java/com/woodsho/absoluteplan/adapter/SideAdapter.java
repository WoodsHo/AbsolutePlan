package com.woodsho.absoluteplan.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.woodsho.absoluteplan.AbsolutePlanApplication;
import com.woodsho.absoluteplan.MainActivity;
import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.bean.SideItem;
import com.woodsho.absoluteplan.widget.SimpleDraweeViewEx;

import java.util.List;

/**
 * Created by hewuzhao on 17/12/9.
 */

public class SideAdapter extends RecyclerView.Adapter {
    public static final String TAG = "SideAdapter";

    public List<SideItem> mSideItemList;
    public Context mContext;
    public OnSideItemClickListener mOnSideItemClickListener;
    public SideViewHolder mLastClickedViewHolder;
    public SideItem mLastClickedSideItem;
    public int mInitSelectedSideId;

    public SideAdapter(Context context, List<SideItem> sideItemList, int initId) {
        mContext = context;
        mSideItemList = sideItemList;
        mInitSelectedSideId = initId;
    }

    public interface OnSideItemClickListener {
        void onSideItemClick(SideItem sideItem);
    }

    public void setOnSideItemClickListener(OnSideItemClickListener listener) {
        mOnSideItemClickListener = listener;
    }

    public void removeOnSideItemClickListener() {
        mOnSideItemClickListener = null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.item_side_layout, null);
        return new SideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final SideViewHolder sideViewHolder = (SideViewHolder) holder;
        final Resources res = mContext.getResources();
        final SideItem sideItem = mSideItemList.get(position);
        sideViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastClickedViewHolder != null && mLastClickedSideItem != null) {
                    //mLastClickedViewHolder.itemView.setBackground(mContext.getDrawable(R.color.black_10));
                    mLastClickedViewHolder.mTitle.setTextColor(res.getColor(R.color.black));
                    mLastClickedViewHolder.mCount.setTextColor(res.getColor(R.color.black));
                    mLastClickedViewHolder.mIcon.setImageResource(mLastClickedSideItem.iconId);
                }
                //v.setBackground(mContext.getDrawable(R.color.side_selected_item_bg_color));
                sideViewHolder.mTitle.setTextColor(res.getColor(R.color.colorPrimary));
                sideViewHolder.mCount.setTextColor(res.getColor(R.color.colorPrimary));
                sideViewHolder.mIcon.setImageResource(getSelectedDrawableResId(sideItem.iconId, res));
                mLastClickedViewHolder = sideViewHolder;
                mLastClickedSideItem = sideItem;
                if (mOnSideItemClickListener != null) {
                    mOnSideItemClickListener.onSideItemClick(sideItem);
                    mInitSelectedSideId = sideItem.id;
                }
            }
        });
        if (sideItem.id == mInitSelectedSideId) {
            //sideViewHolder.itemView.setBackground(mContext.getDrawable(R.color.side_selected_item_bg_color));
            mLastClickedViewHolder = sideViewHolder;
            mLastClickedSideItem = sideItem;
            sideViewHolder.mTitle.setTextColor(res.getColor(R.color.colorPrimary));
            sideViewHolder.mCount.setTextColor(res.getColor(R.color.colorPrimary));
            sideViewHolder.mIcon.setImageResource(getSelectedDrawableResId(sideItem.iconId, res));
        } else {
            //sideViewHolder.itemView.setBackground(mContext.getDrawable(R.color.black_10));
            sideViewHolder.mTitle.setTextColor(res.getColor(R.color.black));
            sideViewHolder.mCount.setTextColor(res.getColor(R.color.black));
            sideViewHolder.mIcon.setImageResource(sideItem.iconId);
        }
        sideViewHolder.mTitle.setText(sideItem.title);
        if (sideItem.id == MainActivity.ID_ALL || sideItem.id == MainActivity.ID_TODAY
                || sideItem.id == MainActivity.ID_TOMORROW || sideItem.id == MainActivity.ID_FINISHED) {
            sideViewHolder.mCount.setText(String.valueOf(sideItem.count));
        } else {
            sideViewHolder.mCount.setText("");
        }
    }

    private int getSelectedDrawableResId(int id, Resources res) {
        String name = res.getResourceName(id);
        try {
            name = String.format("%1s_highlight", name);
            id = res.getIdentifier(name, "drawable", AbsolutePlanApplication.sAppContext.getPackageName());
        } catch (Exception ex) {
            Log.e(TAG, "name: " + name + ", ex: " + ex);
        }
        return id;
    }

    @Override
    public int getItemCount() {
        return mSideItemList.size();
    }

    private class SideViewHolder extends RecyclerView.ViewHolder {
        public ImageView mIcon;
        public TextView mTitle;
        public TextView mCount;

        public SideViewHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.side_item_icon);
            mTitle = (TextView) itemView.findViewById(R.id.side_item_title);
            mCount = (TextView) itemView.findViewById(R.id.side_item_count);
        }
    }
}
