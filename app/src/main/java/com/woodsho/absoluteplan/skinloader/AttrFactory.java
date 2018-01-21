package com.woodsho.absoluteplan.skinloader;

/**
 * Created by hewuzhao on 18/1/18.
 */

public class AttrFactory {
    public static final String BACKGROUND = "background";
    public static final String BACKGROUND_TINT = "backgroundTint";
    public static final String TEXT_COLOR = "textColor";
    public static final String SRC = "src";
    public static final String TEXT_COLOR_HINT = "textColorHint";

    public static final String DRAWABLE_START = "drawableStart";

    public static SkinAttr get(String attrName, int attrValueRefId, String attrValueRefName,
                               String typeName) {

        SkinAttr attr;
        if (BACKGROUND.equals(attrName)) {
            attr = new BackgroundAttr();
        } else if (TEXT_COLOR.equals(attrName)) {
            attr = new TextColorAttr();
        } else if (SRC.equals(attrName)) {
            attr = new SrcAttr();
        } else if (TEXT_COLOR_HINT.equals(attrName)) {
            attr = new TextColorHintAttr();
        } else if (DRAWABLE_START.equals(attrName)) {
            attr = new DrawableAttr();
        } else if (BACKGROUND_TINT.equals(attrName)) {
            attr = new BackgroundTintAttr();
        } else {
            return null;
        }

        attr.attrName = attrName;
        attr.attrValueRefId = attrValueRefId;
        attr.attrValueRefName = attrValueRefName;
        attr.attrValueTypeName = typeName;
        return attr;
    }

    /**
     * check whether this attribute is supported by theme
     *
     * @param attrName background, textColor are supported
     * @return true for there is supported by theme, false otherwise
     */
    public static boolean isSupportedAttr(String attrName) {
        if (BACKGROUND.equals(attrName)) {
            return true;
        }

        if (BACKGROUND_TINT.equals(attrName)) {
            return true;
        }

        if (TEXT_COLOR.equals(attrName)) {
            return true;
        }

        if (SRC.equals(attrName)) {
            return true;
        }

        if (TEXT_COLOR_HINT.equals(attrName)) {
            return true;
        }

        if (DRAWABLE_START.equals(attrName)) {
            return true;
        }

        return false;
    }
}
