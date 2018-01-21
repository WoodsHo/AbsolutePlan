package com.woodsho.absoluteplan.skinloader;

import android.view.View;

/**
 * Created by hewuzhao on 18/1/22.
 */

public class BackgroundTintAttr extends SkinAttr {
    @Override
    public void apply(View view) {
        if (RES_TYPE_NAME_COLOR.equals(attrValueTypeName)) {
            view.setBackgroundTintList(SkinManager.getInstance().convertToColorStateList(attrValueRefId));
        }
    }
}
