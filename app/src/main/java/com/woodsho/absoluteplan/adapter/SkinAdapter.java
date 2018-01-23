package com.woodsho.absoluteplan.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.bean.SkinAdapterItem;
import com.woodsho.absoluteplan.skinloader.SkinSharedPreferences;

import java.util.List;

/**
 * Created by hewuzhao on 18/1/21.
 */

public class SkinAdapter extends RecyclerView.Adapter {
    public static final String TAG = "SkinAdapter";

    private SkinViewHolder mLastSelectedSkin;

    public Context mContext;
    public List<SkinAdapterItem> mSkinAdapterItemList;
    public OnSkinItemClickListener mOnSkinItemClickListener;

    public SkinAdapter(Context context, List<SkinAdapterItem> list) {
        mContext = context;
        mSkinAdapterItemList = list;
    }

    public interface OnSkinItemClickListener {
        void onSkinItemClick(SkinAdapterItem item);
    }

    public void setOnSkinItemClickListener(OnSkinItemClickListener listener) {
        mOnSkinItemClickListener = listener;
    }

    public void removeSkinItemClickListener() {
        mOnSkinItemClickListener = null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.item_skin_layout, null);
        return new SkinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final SkinViewHolder skinViewHolder = (SkinViewHolder) holder;
        final SkinAdapterItem item = mSkinAdapterItemList.get(position);
        final Resources res = mContext.getResources();
        skinViewHolder.mTitle.setText(item.name);
        skinViewHolder.mTitle.setTextColor(res.getColor(item.skinColor));
        skinViewHolder.mIcon.setBackgroundColor(res.getColor(item.skinColor));
        if (item.path.equals(SkinSharedPreferences.getInstance().getApplyingSkinName())) {
            mLastSelectedSkin = skinViewHolder;
            skinViewHolder.mSelected.setText("使用中");
            skinViewHolder.mSelected.setTextColor(res.getColor(item.skinColor));
        }
        skinViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSkinItemClickListener != null) {
                    mOnSkinItemClickListener.onSkinItemClick(item);
                }
                if (mLastSelectedSkin != null) {
                    mLastSelectedSkin.mSelected.setText("");
                }
                skinViewHolder.mSelected.setText("使用中");
                skinViewHolder.mSelected.setTextColor(res.getColor(item.skinColor));
                mLastSelectedSkin = skinViewHolder;
            }
        });
    }

    public void restoreDefaultSkin() {
        if (mLastSelectedSkin != null) {
            mLastSelectedSkin.mSelected.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return mSkinAdapterItemList.size();
    }

    private class SkinViewHolder extends RecyclerView.ViewHolder {
        public ImageView mIcon;
        public TextView mTitle;
        public TextView mSelected;

        public SkinViewHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.skin_item_icon);
            mTitle = (TextView) itemView.findViewById(R.id.skin_item_title);
            mSelected = (TextView) itemView.findViewById(R.id.skin_item_selected);
        }
    }
}
