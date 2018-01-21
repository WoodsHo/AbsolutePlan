package com.woodsho.absoluteplan.skinloader;

import android.view.View;

import java.util.List;

/**
 * Created by hewuzhao on 18/1/18.
 */

public interface IDynamicNewView {
    void dynamicAddView(View view, List<DynamicAttr> dynamicAttrs, boolean refresh);
    void dynamicAddView(View view, String attrName, int attrValueResId, boolean refresh);
}
