package com.woodsho.absoluteplan.skinloader;

import android.view.View;

/**
 * Created by hewuzhao on 18/1/18.
 */

public abstract class SkinAttr {
    protected static final String RES_TYPE_NAME_COLOR = "color";
    protected static final String RES_TYPE_NAME_DRAWABLE = "drawable";

    /**
     * name of the attr, ex: background or textSize or textColor
     */
    public String attrName;

    /**
     * id of the attr value refer to, such as [0x7f050000]
     */
    public int attrValueRefId;

    /**
     * entry name of the value , such as [app_exit_btn_background]
     */
    public String attrValueRefName;

    /**
     * type of the value , such as color or drawable
     */
    public String attrValueTypeName;

    /**
     * Use to apply view with new TypedValue
     *
     * @param view
     */
    public abstract void apply(View view);


    @Override
    public String toString() {
        return "SkinAttr{" +
                "attrName='" + attrName + '\'' +
                ", attrValueRefId=" + attrValueRefId +
                ", attrValueRefName='" + attrValueRefName + '\'' +
                ", attrValueTypeName='" + attrValueTypeName + '\'' +
                '}';
    }
}
