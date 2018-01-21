package com.woodsho.absoluteplan.skinloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * Created by hewuzhao on 18/1/18.
 */

public class SkinBaseActivity extends AppCompatActivity implements ISkinUpdate, IDynamicNewView {
    private static final String TAG = "SkinBaseActivity";
    private boolean isResponseOnSkinChange = true;
    private SkinInflaterFactory mSkinInflaterFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSkinInflaterFactory = new SkinInflaterFactory();
        getLayoutInflater().setFactory(mSkinInflaterFactory);
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().attach(this);
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
