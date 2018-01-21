package com.woodsho.absoluteplan.skinloader;

import android.view.View;
import android.widget.TextView;

/**
 * Created by hewuzhao on 18/1/18.
 */

public class TextColorHintAttr extends SkinAttr {
    @Override
    public void apply(View view) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;

            if (RES_TYPE_NAME_COLOR.equals(attrValueTypeName)) {
                textView.setHintTextColor(SkinManager.getInstance()
                        .convertToColorStateList(attrValueRefId));
            }
        }
    }
}
