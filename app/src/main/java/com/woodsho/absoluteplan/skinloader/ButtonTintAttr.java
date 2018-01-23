package com.woodsho.absoluteplan.skinloader;

import android.view.View;
import android.widget.CheckBox;

/**
 * Created by hewuzhao on 18/1/23.
 */

public class ButtonTintAttr extends SkinAttr {
    @Override
    public void apply(View view) {
        if (RES_TYPE_NAME_COLOR.equals(attrValueTypeName)) {
            if (view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;
                checkBox.setButtonTintList(SkinManager.getInstance().convertToColorStateList(attrValueRefId));
            }
        }
    }
}
