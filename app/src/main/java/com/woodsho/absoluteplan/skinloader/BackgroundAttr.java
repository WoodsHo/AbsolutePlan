package com.woodsho.absoluteplan.skinloader;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

/**
 * Created by hewuzhao on 18/1/18.
 */

public class BackgroundAttr extends SkinAttr {
    @Override
    public void apply(View view) {
        if (RES_TYPE_NAME_DRAWABLE.equals(attrValueTypeName)) {
            Drawable drawable = SkinManager.getInstance().getDrawable(attrValueRefId);
            if (view instanceof TextView) {
                if (AttrFactory.DRAWABLE_START.equals(attrName)) {
                    ((TextView) view).setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
                }
            } else {
                view.setBackground(drawable);
            }
        } else if (RES_TYPE_NAME_COLOR.equals(attrValueTypeName)) {
            view.setBackgroundColor(SkinManager.getInstance().getColor(attrValueRefId));
        }
    }
}
