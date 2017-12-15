package com.woodsho.absoluteplan.widget;

import android.content.Context;
import android.content.res.Resources;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.woodsho.absoluteplan.R;

/**
 * Created by hewuzhao on 17/12/16.
 */

public class AbsPlanPreference extends Preference {
    public AbsPlanPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AbsPlanPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AbsPlanPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbsPlanPreference(Context context) {
        super(context);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        Resources res = view.getResources();
        view.setBackgroundColor(res.getColor(R.color.white));
        final TextView titleView = (TextView) view.findViewById(Resources.getSystem().getIdentifier("title", "id", "android"));
        if (titleView != null) {
            titleView.setTextColor(res.getColor(R.color.black));
        }

        final TextView summaryView = (TextView) view.findViewById(Resources.getSystem().getIdentifier("summary", "id", "android"));
        if (summaryView != null) {
            summaryView.setTextColor(res.getColor(R.color.black_30));
        }
    }
}
