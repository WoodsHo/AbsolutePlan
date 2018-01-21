package com.woodsho.absoluteplan.skinloader;

/**
 * Created by hewuzhao on 18/1/18.
 */

public class DynamicAttr {
    /**
     * attribute name, such as {@link AttrFactory#TEXT_COLOR},{@link AttrFactory#BACKGROUND}
     */
    public String mAttrName;
    public int mRefResId;

    public DynamicAttr(String attrName, int refResId) {
        mAttrName = attrName;
        mRefResId = refResId;
    }
}
