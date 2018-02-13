package com.woodsho.absoluteplan.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.woodsho.absoluteplan.AbsolutePlanApplication;
import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.skinloader.SkinBaseActivity;
import com.woodsho.absoluteplan.skinloader.SkinManager;
import com.woodsho.absoluteplan.utils.CommonUtil;
import com.woodsho.absoluteplan.utils.StatusBarUtil;

public class AboutActivity extends SkinBaseActivity {
    private static final String GITHUB = "https://github.com/WoodsHo/AbsolutePlan";

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
        StatusBarUtil.setColor(this, SkinManager.getInstance().getColor(R.color.colorPrimary), 0);
        init();
    }

    private void setupActionBar() {
        ViewGroup rootView = (ViewGroup) findViewById(R.id.action_bar_root); //id from appcompat

        if (rootView != null) {
            View view = getLayoutInflater().inflate(R.layout.about_toolbar_layout, rootView, false);
            rootView.addView(view, 0);

            Toolbar toolbar = (Toolbar) findViewById(R.id.about_toolbar);
            toolbar.setBackgroundColor(SkinManager.getInstance().getColor(R.color.colorPrimary));
            setSupportActionBar(toolbar);
            ImageView back = (ImageView) findViewById(R.id.back_about);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
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
        TextView textView = (TextView) findViewById(R.id.github_title);
        SpannableString githubTitle = new SpannableString("开源GitHub, 欢迎star！");
        githubTitle.setSpan(new AbsoluteSizeSpan(25, true), 2, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        githubTitle.setSpan(new ForegroundColorSpan(SkinManager.getInstance().getColor(R.color.colorPrimary)), 2, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(githubTitle);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setData(Uri.parse(GITHUB));
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
            }
        });
        ImageView github = (ImageView) findViewById(R.id.github_absplan);
        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setData(Uri.parse(GITHUB));
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
            }
        });
    }
}
