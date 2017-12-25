package com.woodsho.absoluteplan.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.bean.OpenSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hewuzhao on 17/12/24.
 */

public class OpenSourceAdapter extends RecyclerView.Adapter {
    public static final String TAG = "OpenSourceAdapter";

    private Context mContext;
    private List<OpenSource> mOpenSourceList;

    public OpenSourceAdapter(Context context, List<OpenSource> list) {
        mContext = context;
        mOpenSourceList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OpenSourceViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_opensource_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mOpenSourceList == null)
            return;

        Resources res = mContext.getResources();
        OpenSourceViewHolder viewHolder = (OpenSourceViewHolder) holder;
        final OpenSource openSource = mOpenSourceList.get(position);
        viewHolder.mName.setText(openSource.name);
        viewHolder.mAuthor.setText(openSource.author);
        viewHolder.mDescribe.setText(openSource.describe);
        viewHolder.mCardView.setBackground(res.getDrawable(R.drawable.item_opensource_bg_selector));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setData(Uri.parse(openSource.link));
                intent.setAction(Intent.ACTION_VIEW);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mOpenSourceList == null)
            return 0;

        return mOpenSourceList.size();
    }

    private class OpenSourceViewHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public TextView mAuthor;
        public TextView mDescribe;
        public CardView mCardView;

        public OpenSourceViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.item_opensource_name);
            mAuthor = (TextView) itemView.findViewById(R.id.item_opensource_author);
            mDescribe = (TextView) itemView.findViewById(R.id.item_opensource_describe);
            mCardView = (CardView) itemView.findViewById(R.id.item_opensource_cardview);
        }
    }
}
