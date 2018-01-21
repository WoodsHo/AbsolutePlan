package com.woodsho.absoluteplan.skinloader;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by hewuzhao on 18/1/18.
 */

public class SrcAttr extends SkinAttr {
    @Override
    public void apply(View view) {
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            if (RES_TYPE_NAME_COLOR.equals(attrValueTypeName)) {
                imageView.setImageDrawable(SkinManager.getInstance().getDrawable(attrValueRefId));
            } else if (RES_TYPE_NAME_DRAWABLE.equals(attrValueTypeName)) {
                Drawable drawable = SkinManager.getInstance().getDrawable(attrValueRefId);
                imageView.setImageDrawable(drawable);
            }
        }
    }
}
