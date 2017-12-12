package com.woodsho.absoluteplan.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.woodsho.absoluteplan.R;

/**
 * Created by hewuzhao on 17/12/12.
 */

public class PlanTaskRecyclerView extends RecyclerView {
    public Paint mEmptyViewPain;
    public Rect mEmptyViewRect;

    public PlanTaskRecyclerView(Context context) {
        this(context, null);
    }

    public PlanTaskRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlanTaskRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Resources res = getResources();
        int width = res.getDisplayMetrics().widthPixels;
        mEmptyViewRect = new Rect(0, 0, width, 2000);

        mEmptyViewPain = new Paint();
        mEmptyViewPain.setColor(res.getColor(R.color.calendar_empty_view_color));
    }

    public boolean isScrollTop() {
        return computeVerticalScrollOffset() == 0;
    }

    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (getOnFocusChangeListener() != null) {
            getOnFocusChangeListener().onFocusChange(child, false);
            getOnFocusChangeListener().onFocusChange(focused, true);
        }
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        Adapter adapter = getAdapter();
        if (adapter != null) {
            if (adapter.getItemCount() == 0) {
                c.drawRect(mEmptyViewRect, mEmptyViewPain);
            }
        }
    }
}
