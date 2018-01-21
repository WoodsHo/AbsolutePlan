package com.woodsho.absoluteplan.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.skinloader.DynamicAttr;
import com.woodsho.absoluteplan.skinloader.IDynamicNewView;
import com.woodsho.absoluteplan.skinloader.ISkinUpdate;
import com.woodsho.absoluteplan.skinloader.SkinInflaterFactory;
import com.woodsho.absoluteplan.skinloader.SkinManager;
import com.woodsho.absoluteplan.utils.CommonUtil;
import com.woodsho.absoluteplan.utils.StatusBarUtil;

import java.util.List;

/**
 * Created by hewuzhao on 17/12/15.
 */

public class SettingsActivity extends AppCompatPreferenceActivity implements ISkinUpdate, IDynamicNewView {
    private static final String TAG = "SettingsActivity";

    private boolean isResponseOnSkinChange = true;
    private SkinInflaterFactory mSkinInflaterFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSkinInflaterFactory = new SkinInflaterFactory();
        getLayoutInflater().setFactory(mSkinInflaterFactory);
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().attach(this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSkinInflaterFactory.clean();
        SkinManager.getInstance().detach(this);
    }

    @Override
    public void onSkinUpdate() {
        if (!isResponseOnSkinChange) {
            return;
        }
        Log.d(TAG, "onSkinUpdate");
        mSkinInflaterFactory.applySkin();
        StatusBarUtil.setColor(this, SkinManager.getInstance().getColor(R.color.colorPrimary), 0);
    }

    /**
     * used for view that is added in code, call onSkinUpdate() to make the added view apply skin
     *
     * @param view         view that need to be skin
     * @param dynamicAttrs attrs that need to be skin
     */
    @Override
    public void dynamicAddView(View view, List<DynamicAttr> dynamicAttrs, boolean refresh) {
        mSkinInflaterFactory.dynamicAddSkinEnableView(this, view, dynamicAttrs, refresh);
    }

    /**
     * @param view
     * @param attrName
     * @param attrValueResId
     */
    public void dynamicAddView(View view, String attrName, int attrValueResId, boolean refresh) {
        mSkinInflaterFactory.dynamicAddSkinEnableView(this, view, attrName, attrValueResId, refresh);
    }

    /**
     * switch for response on skin change
     *
     * @param enable true to response on skin change, false otherwise
     */
    final protected void enableResponseOnSkinChange(boolean enable) {
        isResponseOnSkinChange = enable;
    }
}
