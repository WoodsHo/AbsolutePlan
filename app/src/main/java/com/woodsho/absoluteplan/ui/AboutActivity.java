package com.woodsho.absoluteplan.ui;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.woodsho.absoluteplan.AbsolutePlanApplication;
import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.utils.CommonUtil;
import com.woodsho.absoluteplan.utils.StatusBarUtil;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
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
        init();
    }

    private void setupActionBar() {
        ViewGroup rootView = (ViewGroup) findViewById(R.id.action_bar_root); //id from appcompat

        if (rootView != null) {
            View view = getLayoutInflater().inflate(R.layout.about_toolbar_layout, rootView, false);
            rootView.addView(view, 0);

            Toolbar toolbar = (Toolbar) findViewById(R.id.about_toolbar);
            toolbar.setPadding(0, CommonUtil.getStatusBarHeight(AbsolutePlanApplication.sAppContext), 0, 0);
            setSupportActionBar(toolbar);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }



    private void init() {
        TextView versionName = (TextView) findViewById(R.id.version_name);
        versionName.setText("版本号：" + CommonUtil.getVersionName(AbsolutePlanApplication.sAppContext));
        TextView aboutOpenSource = (TextView) findViewById(R.id.about_open_source);
        aboutOpenSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this, OpenSourceActivity.class));
            }
        });
    }
}
