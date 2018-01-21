package com.woodsho.absoluteplan.skinloader;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * Created by hewuzhao on 18/1/18.
 */

public class SkinBaseFragment extends Fragment implements IDynamicNewView, ISkinUpdate {
    private static final String TAG = "SkinBaseFragment";
    private IDynamicNewView mIDynamicNewView;
    private boolean isResponseOnSkinChange = true;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(getActivity());
        try {
            mIDynamicNewView = (IDynamicNewView) activity;
        } catch (ClassCastException e) {
            mIDynamicNewView = null;
        }

        SkinManager.getInstance().attach(this);
    }

    @Override
    public void dynamicAddView(View view, List<DynamicAttr> pDAttrs, boolean refresh) {
        if (mIDynamicNewView == null) {
            throw new RuntimeException("IDynamicNewView should be implements !");
        } else {
            mIDynamicNewView.dynamicAddView(view, pDAttrs, refresh);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        SkinManager.getInstance().detach(this);
    }

    /**
     * @param view
     * @param attrName
     * @param attrValueResId
     */
    public void dynamicAddView(View view, String attrName, int attrValueResId, boolean refresh) {
        ((SkinInflaterFactory) getActivity().getLayoutInflater().getFactory())
                .dynamicAddSkinEnableView(getActivity(), view, attrName, attrValueResId, refresh);
    }

    @Override
    public void onSkinUpdate() {
        if (!isResponseOnSkinChange) {
            return;
        }
        Log.d(TAG, "onSkinUpdate");
        ((SkinInflaterFactory) getActivity().getLayoutInflater().getFactory()).applySkin();
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
