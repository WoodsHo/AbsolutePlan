package com.woodsho.absoluteplan.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.bean.BottomShareItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hewuzhao on 18/2/16.
 */

public class BottomShareDialog {

    private CustomDialog mCustomDialog;

    public BottomShareDialog(Context context, List<BottomShareItem> list, OnItemClickListener onItemClickListener) {
        mCustomDialog = new CustomDialog(context);
        mCustomDialog.addItems(list, onItemClickListener);
    }

    public interface OnItemClickListener {
        void click(BottomShareItem item);
    }

    public void show() {
        mCustomDialog.show();
    }

    private final class CustomDialog extends Dialog {
        private RecyclerView mRecyclerView;

        private DialogAdapter mAdapter;

        CustomDialog(Context context) {
            super(context, R.style.BottomShareDialog);
            init(context);
        }

        private void init(Context context) {
            setContentView(R.layout.bottom_share_dialog_layout);
            setCancelable(true);
            setCanceledOnTouchOutside(true);
            Window window = getWindow();
            if (window != null) {
                window.setGravity(Gravity.BOTTOM);
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            }
            mRecyclerView = (RecyclerView) findViewById(R.id.share_recyclerView);
        }

        void addItems(List<BottomShareItem> items, OnItemClickListener onItemClickListener) {
            mAdapter = new DialogAdapter(items, getContext());
            mAdapter.setItemClick(onItemClickListener);
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

            mRecyclerView.setAdapter(mAdapter);
        }

        private class DialogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
            private Context mContext;
            private List<BottomShareItem> mItems = Collections.emptyList();
            private OnItemClickListener itemClickListener;

            DialogAdapter(List<BottomShareItem> mItems, Context context) {
                setList(mItems);
                mContext = context;
            }

            private void setList(List<BottomShareItem> items) {
                mItems = items == null ? new ArrayList<BottomShareItem>() : items;
            }

            void setItemClick(OnItemClickListener onItemClickListener) {
                this.itemClickListener = onItemClickListener;
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = View.inflate(mContext, R.layout.bottom_share_item_layout, null);
                return new ShareHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                final BottomShareItem item = mItems.get(position);
                ShareHolder shareHolder;
                shareHolder = (ShareHolder) holder;
                shareHolder.mIcon.setImageDrawable(mContext.getResources().getDrawable(item.getIcon()));
                shareHolder.mTitle.setText(item.getTitle());

                shareHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (itemClickListener != null) {
                            itemClickListener.click(item);
                        }
                        dismiss();
                    }
                });

            }

            @Override
            public int getItemCount() {
                return mItems.size();
            }

            class ShareHolder extends RecyclerView.ViewHolder {
                public ImageView mIcon;
                private TextView mTitle;

                ShareHolder(View view) {
                    super(view);
                    mIcon = (ImageView) view.findViewById(R.id.share_ic);
                    mTitle = (TextView) view.findViewById(R.id.share_title);
                }
            }
        }
    }
}
