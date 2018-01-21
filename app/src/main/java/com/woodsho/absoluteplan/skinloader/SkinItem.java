package com.woodsho.absoluteplan.skinloader;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hewuzhao on 18/1/18.
 */

public class SkinItem {
    public View mView;
    public List<SkinAttr> mAttrs;

    public SkinItem() {
        mAttrs = new ArrayList<>();
    }


    /**
     * apply all defined skin attrs to the view
     */
    public void apply() {
        if (mAttrs == null || mAttrs.size() == 0) {
            return;
        }

        for (SkinAttr attr : mAttrs) {
            if (attr != null) {
                attr.apply(mView);
            }
        }
    }

    @Override
    public String toString() {
        return "SkinItem{" +
                "mView=" + mView +
                ", mAttrs=" + mAttrs +
                '}';
    }
}
