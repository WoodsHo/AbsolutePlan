package com.woodsho.absoluteplan.widget;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by hewuzhao on 17/12/9.
 */

public class MyDrawerLayout extends DrawerLayout {
    private boolean mCanMove;

    public MyDrawerLayout(Context context) {
        super(context);
    }

    public MyDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mCanMove = ev.getX() < 45;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
