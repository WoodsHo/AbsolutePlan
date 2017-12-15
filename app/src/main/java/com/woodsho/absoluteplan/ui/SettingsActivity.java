package com.woodsho.absoluteplan.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.utils.CommonUtil;
import com.woodsho.absoluteplan.utils.StatusBarUtil;

/**
 * Created by hewuzhao on 17/12/15.
 */

public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SlidrConfig mConfig = new SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .velocityThreshold(2400)
                .distanceThreshold(.25f)
                .edge(true)
                .touchSize(CommonUtil.dp2px(this, 32))
                .build();
        Slidr.attach(this, mConfig);
        setupActionBar();
        StatusBarUtil statusBarUtil = new StatusBarUtil(this);
        statusBarUtil.setColorBarForDrawer(ContextCompat.getColor(this, R.color.colorPrimary));
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingsFragment()).commit();
    }

    private void setupActionBar() {
        ViewGroup rootView = (ViewGroup) findViewById(R.id.action_bar_root); //id from appcompat

        if (rootView != null) {
            View view = getLayoutInflater().inflate(R.layout.settings_toolbar_layout, rootView, false);
            rootView.addView(view, 0);
            ImageView back = (ImageView) view.findViewById(R.id.img_back);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

}
