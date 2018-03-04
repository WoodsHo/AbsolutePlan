package com.woodsho.absoluteplan.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by hewuzhao on 17/12/20.
 */

public class CommonRecyclerView extends RecyclerView {
    private int mWidth, mHeight;
    private Paint mBgPaint;

    public CommonRecyclerView(Context context) {
        this(context, null);
    }

    public CommonRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mBgPaint = new Paint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onDraw(Canvas c) {

        super.onDraw(c);
    }
}
