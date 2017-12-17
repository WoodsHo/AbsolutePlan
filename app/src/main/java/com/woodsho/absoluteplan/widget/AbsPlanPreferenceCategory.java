package com.woodsho.absoluteplan.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.woodsho.absoluteplan.R;

/**
 * Created by hewuzhao on 17/12/17.
 */

public class AbsPlanPreferenceCategory extends PreferenceCategory {
    private String mTitle;

    public AbsPlanPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public AbsPlanPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public AbsPlanPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.settings_preference);
        mTitle = array.getString(R.styleable.settings_preference_title);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        return LayoutInflater.from(getContext()).inflate(R.layout.item_settings_preferencecategory_layout, parent, false);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView textView = (TextView) view.findViewById(R.id.item_settings_preferencecategory_title);
        textView.setText(mTitle);
    }
}
