package com.woodsho.absoluteplan.ui;

import android.graphics.Rect;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.adapter.OpenSourceAdapter;
import com.woodsho.absoluteplan.bean.OpenSource;
import com.woodsho.absoluteplan.utils.CommonUtil;
import com.woodsho.absoluteplan.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class OpenSourceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_source);
        SlidrConfig mConfig = new SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .velocityThreshold(2400)
                .distanceThreshold(.25f)
                .edge(true)
                .touchSize(CommonUtil.dp2px(this, 32))
                .build();
        Slidr.attach(this, mConfig);
        setupActionBar();
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 0);
        init();
    }

    private void setupActionBar() {
        ViewGroup rootView = (ViewGroup) findViewById(R.id.action_bar_root); //id from appcompat

        if (rootView != null) {
            View view = getLayoutInflater().inflate(R.layout.opensource_toolbar_layout, rootView, false);
            rootView.addView(view, 0);

            Toolbar toolbar = (Toolbar) findViewById(R.id.opensource_toolbar);
            setSupportActionBar(toolbar);
            ImageView back = (ImageView) findViewById(R.id.back_opensource);
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
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.opensource_recyclerview);
        OpenSourceAdapter sourceAdapter = new OpenSourceAdapter(this, getOpenSource());
        recyclerView.addItemDecoration(new SpaceItemDecoration(30));
        recyclerView.setAdapter(sourceAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
    }

    private List<OpenSource> getOpenSource() {
        List<OpenSource> list = new ArrayList<>();
        list.add(new OpenSource("NCalendar", "yannecer", "一款仿miui，仿小米，日历，周日历，月日历，月视图、周视图滑动切换，农历,Andriod Calendar , MIUI Calendar,小米日历", "https://github.com/yannecer/NCalendar"));
        list.add(new OpenSource("SwipeDelMenuLayout", "mcxtzhang", "The most simple SwipeMenu in the history, 0 coupling, support any ViewGroup. Step integration swipe (delete) menu, high imitation QQ, iOS.", "https://github.com/mcxtzhang/SwipeDelMenuLayout"));
        list.add(new OpenSource("Slidr", "r0adkll", "Easily add slide to dismiss functionality to an Activity", "https://github.com/r0adkll/Slidr"));
        list.add(new OpenSource("NewbieGuide", "huburt-Hu", "Android 快速实现新手引导层的库", "https://github.com/huburt-Hu/NewbieGuide"));
        return list;
    }

    private class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        int mSpace ;

        public SpaceItemDecoration(int space) {
            mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if(parent.getChildPosition(view) != 0)
                outRect.top = mSpace;
        }
    }
}
