package com.woodsho.absoluteplan.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.woodsho.absoluteplan.skinloader.SkinBaseFragment;

/**
 * Created by hewuzhao on 17/12/10.
 */

public abstract class BaseFragment extends SkinBaseFragment {
    protected static final String TAG = "BaseFragment";

    protected Activity mActivity;
    protected View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        mView = initContentView(inflater, container);
        if (mView == null)
            throw new NullPointerException("Fragment content view is null.");
        bindView(mView);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Nullable
    protected abstract View initContentView(LayoutInflater inflater, @Nullable ViewGroup container);

    @Override
    public void onResume() {
        super.onResume();
        bindData();
    }

    protected abstract void bindView(View viw);

    /**
     * 请求动态数据
     */
    protected void initData() {

    }

    /**
     * 绑定静态数据
     */
    protected void bindData() {

    }
}
