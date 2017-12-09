package com.woodsho.absoluteplan.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.woodsho.absoluteplan.R;

/**
 * Created by hewuzhao on 17/12/9.
 */

public class SideNavigationView extends View {
    private Paint mPaint;
    private int mWidth;
    private int mItemWidth;
    private int mHeight;
    private int mHeightForRect;

    public SideNavigationView(Context context) {
        super(context);
        init();
    }

    public SideNavigationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SideNavigationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SideNavigationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mPaint = new Paint();
        Resources res = getResources();
        mPaint.setColor(res.getColor(R.color.white));
        mItemWidth = 30;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mWidth == 0 && mHeight == 0) {
            mWidth = getWidth();
            mHeight = getHeight();
            mHeightForRect = mHeight / 5;
        }
        canvas.drawRect(mWidth / 2 - mItemWidth / 2, 0, mWidth / 2 + mItemWidth / 2, mHeightForRect - 9, mPaint);
        canvas.drawRect(mWidth / 2 - mItemWidth / 2, mHeightForRect * 2, mWidth / 2 + mItemWidth / 2, mHeightForRect * 3 - 9, mPaint);
        canvas.drawRect(mWidth / 2 - mItemWidth / 2, mHeightForRect * 4, mWidth / 2 + mItemWidth / 2, mHeight - 9, mPaint);
    }
}
