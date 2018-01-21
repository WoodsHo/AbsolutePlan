package com.woodsho.absoluteplan.skinloader;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by hewuzhao on 18/1/18.
 */

public class DrawableAttr extends SkinAttr {
    @Override
    public void apply(View view) {
        if (RES_TYPE_NAME_COLOR.equals(attrValueTypeName)) {
            view.setBackgroundColor(SkinManager.getInstance().getColor(attrValueRefId));
        } else if (RES_TYPE_NAME_DRAWABLE.equals(attrValueTypeName)) {
            Drawable bg = SkinManager.getInstance().getDrawable(attrValueRefId);
            view.setBackground(bg);
        }
    }
}
