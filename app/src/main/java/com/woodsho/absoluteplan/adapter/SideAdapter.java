package com.woodsho.absoluteplan.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.woodsho.absoluteplan.MainActivity;
import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.bean.SideItem;
import com.woodsho.absoluteplan.widget.SimpleDraweeViewEx;

import java.util.List;

/**
 * Created by hewuzhao on 17/12/9.
 */

public class SideAdapter extends RecyclerView.Adapter {
    public List<SideItem> mSideItemList;
    public Context mContext;
    public OnSideItemClickListener mOnSideItemClickListener;
    public SideViewHolder mLastClickedViewHolder;
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
                if (mLastClickedViewHolder != null) {
                    //mLastClickedViewHolder.itemView.setBackground(mContext.getDrawable(R.color.black_10));
                    mLastClickedViewHolder.mTitle.setTextColor(res.getColor(R.color.black));
                    mLastClickedViewHolder.mCount.setTextColor(res.getColor(R.color.black_30));
                }
                //v.setBackground(mContext.getDrawable(R.color.side_selected_item_bg_color));
                sideViewHolder.mTitle.setTextColor(res.getColor(R.color.colorPrimary));
                sideViewHolder.mCount.setTextColor(res.getColor(R.color.colorPrimary));
                mLastClickedViewHolder = sideViewHolder;
                if (mOnSideItemClickListener != null) {
                    mOnSideItemClickListener.onSideItemClick(sideItem);
                    mInitSelectedSideId = sideItem.id;
                }
            }
        });
        if (sideItem.id == mInitSelectedSideId) {
            //sideViewHolder.itemView.setBackground(mContext.getDrawable(R.color.side_selected_item_bg_color));
            mLastClickedViewHolder = sideViewHolder;
            sideViewHolder.mTitle.setTextColor(res.getColor(R.color.colorPrimary));
            sideViewHolder.mCount.setTextColor(res.getColor(R.color.colorPrimary));
        } else {
            //sideViewHolder.itemView.setBackground(mContext.getDrawable(R.color.black_10));
            sideViewHolder.mTitle.setTextColor(res.getColor(R.color.black));
            sideViewHolder.mCount.setTextColor(res.getColor(R.color.black_30));
        }
        sideViewHolder.mIcon.setImageURI(sideItem.iconId);
        sideViewHolder.mTitle.setText(sideItem.title);
        if (sideItem.id == MainActivity.ID_ALL || sideItem.id == MainActivity.ID_TODAY
                || sideItem.id == MainActivity.ID_TOMORROW || sideItem.id == MainActivity.ID_FINISHED) {
            sideViewHolder.mCount.setText(String.valueOf(sideItem.count));
        } else {
            sideViewHolder.mCount.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return mSideItemList.size();
    }

    private class SideViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeViewEx mIcon;
        public TextView mTitle;
        public TextView mCount;

        public SideViewHolder(View itemView) {
            super(itemView);
            mIcon = (SimpleDraweeViewEx) itemView.findViewById(R.id.side_item_icon);
            mTitle = (TextView) itemView.findViewById(R.id.side_item_title);
            mCount = (TextView) itemView.findViewById(R.id.side_item_count);
        }
    }
}
