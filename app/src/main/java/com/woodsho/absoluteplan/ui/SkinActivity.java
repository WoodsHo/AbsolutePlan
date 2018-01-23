package com.woodsho.absoluteplan.ui;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelUuid;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.woodsho.absoluteplan.AbsolutePlanApplication;
import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.adapter.SkinAdapter;
import com.woodsho.absoluteplan.bean.SkinAdapterItem;
import com.woodsho.absoluteplan.skinloader.ILoaderListener;
import com.woodsho.absoluteplan.skinloader.SkinBaseActivity;
import com.woodsho.absoluteplan.skinloader.SkinManager;
import com.woodsho.absoluteplan.skinloader.SkinSharedPreferences;
import com.woodsho.absoluteplan.utils.CommonUtil;
import com.woodsho.absoluteplan.utils.FileUtil;
import com.woodsho.absoluteplan.utils.StatusBarUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SkinActivity extends SkinBaseActivity implements SkinAdapter.OnSkinItemClickListener {
    private static final String TAG = "SkinActivity";

    public SkinAdapter mSkinAdapter;
    private static final String PATH = Environment
            .getExternalStorageDirectory() + File.separator + "com.woodsho.absoluteplan";

    public static final String SKIN_BLUEGREY = "skin_bluegrey.skin";
    public static final String SKIN_GREY = "skin_grey.skin";
    public static final String SKIN_DEEPORANGE = "skin_deeporange.skin";
    public static final String SKIN_TEAL = "skin_teal.skin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);
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
            View view = getLayoutInflater().inflate(R.layout.skin_toolbar_layout, rootView, false);
            rootView.addView(view, 0);

            Toolbar toolbar = (Toolbar) findViewById(R.id.skin_toolbar);
            toolbar.setBackgroundColor(SkinManager.getInstance().getColor(R.color.colorPrimary));
            dynamicAddView(toolbar, "background", R.color.colorPrimary, true);
            setSupportActionBar(toolbar);
            ImageView back = (ImageView) findViewById(R.id.back_skin);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            TextView skinDefault = (TextView) findViewById(R.id.skin_default);
            skinDefault.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SkinManager.getInstance().restoreDefaultSkin();
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
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.skin_recyclerview);
        List<SkinAdapterItem> list = getSkinList();
        mSkinAdapter = new SkinAdapter(AbsolutePlanApplication.sAppContext, list);
        mSkinAdapter.setOnSkinItemClickListener(this);
        recyclerView.setAdapter(mSkinAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 20;
            }
        });
    }

    private List<SkinAdapterItem> getSkinList() {
        List<SkinAdapterItem> list = new ArrayList<>();
        list.add(new SkinAdapterItem("混灰蓝", SKIN_BLUEGREY, R.color.skin_bluegrey_color));
        list.add(new SkinAdapterItem("低调灰", SKIN_GREY, R.color.skin_grey_color));
        list.add(new SkinAdapterItem("幽暗橙", SKIN_DEEPORANGE, R.color.skin_deeporange_color));
        list.add(new SkinAdapterItem("水鸭绿", SKIN_TEAL, R.color.skin_teal_color));
        return list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSkinAdapter != null) {
            mSkinAdapter.removeSkinItemClickListener();
        }
    }

    @Override
    public void onSkinUpdate() {
        super.onSkinUpdate();
        StatusBarUtil.setColor(this, SkinManager.getInstance().getColor(R.color.colorPrimary), 0);
    }

    @Override
    public void onSkinItemClick(final SkinAdapterItem item) {
        String skinFullName = PATH + File.separator + item.path;
        FileUtil.moveRawToDir(AbsolutePlanApplication.sAppContext, item.path, skinFullName);
        File skin = new File(skinFullName);
        if (!skin.exists()) {
            Toast.makeText(AbsolutePlanApplication.sAppContext, "请检查 " + skinFullName + " 是否存在", Toast.LENGTH_SHORT).show();
            return;
        }

        SkinManager.getInstance().load(skin.getAbsolutePath(), new ILoaderListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess() {
                SkinSharedPreferences.getInstance().saveApplyingSkinName(item.path);
                Log.d(TAG, "load skin success");
            }

            @Override
            public void onFailed() {
                Log.e(TAG, "load skin failed");
            }
        });
    }
}
