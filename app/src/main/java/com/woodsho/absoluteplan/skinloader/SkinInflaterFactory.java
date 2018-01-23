package com.woodsho.absoluteplan.skinloader;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.woodsho.absoluteplan.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hewuzhao on 18/1/18.
 */

public class SkinInflaterFactory implements LayoutInflater.Factory {
    private static final String TAG = "SkinInflaterFactory";
    private List<SkinItem> mSkinItems = new ArrayList<>();

    @Override
    public View onCreateView(String s, Context context, AttributeSet attributeSet) {
        TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.SkinAttr);
//        boolean isSkinEnable = array.getBoolean(R.styleable.SkinAttr_enable, false);
        array.recycle();

        /**
         * if this is no enable attribute, return null to use the android default onCreateView()
         */
//        if (!isSkinEnable) {
//            return null;
//        }

        View view = createView(s, context, attributeSet);
        if (view == null) {
            Log.d(TAG, "view is null tagName:" + s);
            return null;
        }

        if (attributeSet.getStyleAttribute() == 0) {
            parseAttr(context, attributeSet, view);
        } else {
            parseStyleAttr(context, attributeSet, view);
        }
        return view;
    }


    /**
     * Invoke low-level function for instantiating a view by name. This attempts to
     * instantiate a view class of the given <var>name</var> found in this
     * LayoutInflater's ClassLoader.
     *
     * @param context
     * @param name         The full name of the class to be instantiated.
     * @param attributeSet The XML attributes supplied for this instance.
     * @return View The newly instantiated view, or null.
     */
    private View createView(String name, Context context, AttributeSet attributeSet) {
        View view = null;
        try {
            if (-1 == name.indexOf('.')) {
                if ("View".equals(name)) {
                    view = LayoutInflater.from(context).createView(name, "android.view.", attributeSet);
                }
                if (view == null) {
                    view = LayoutInflater.from(context).createView(name, "android.widget.", attributeSet);
                }
                if (view == null) {
                    view = LayoutInflater.from(context).createView(name, "android.webkit.", attributeSet);
                }
            } else {
                view = LayoutInflater.from(context).createView(name, null, attributeSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
            view = null;
        }
        return view;
    }


    /**
     * parse skin attribute, such as background, textColor. it can also get these attribute in
     * style
     *
     * @param context
     * @param attributeSet
     * @param view
     */
    private void parseStyleAttr(Context context, AttributeSet attributeSet, View view) {

        List<SkinAttr> viewAttrs = new ArrayList<>();
        TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.SkinAttr);

        int textColorResId = array.getResourceId(R.styleable.SkinAttr_android_textColor, 0);
        if (textColorResId != 0) {
            String entryName = context.getResources().getResourceEntryName(textColorResId);
            String typeName = context.getResources().getResourceTypeName(textColorResId);
            SkinAttr themeAttr = AttrFactory.get(AttrFactory.TEXT_COLOR, textColorResId, entryName, typeName);
            if (themeAttr != null) {
                viewAttrs.add(themeAttr);
            }
        }

        int backgroundResId = array.getResourceId(R.styleable.SkinAttr_android_background, 0);
        if (backgroundResId != 0) {
            String entryName = context.getResources().getResourceEntryName(backgroundResId);
            String typeName = context.getResources().getResourceTypeName(backgroundResId);
            SkinAttr themeAttr = AttrFactory.get(AttrFactory.BACKGROUND, backgroundResId, entryName, typeName);
            if (themeAttr != null) {
                viewAttrs.add(themeAttr);
            }
        }

        int backgroundTintResId = array.getResourceId(R.styleable.SkinAttr_android_backgroundTint, 0);
        if (backgroundTintResId != 0) {
            String entryName = context.getResources().getResourceEntryName(backgroundTintResId);
            String typeName = context.getResources().getResourceTypeName(backgroundTintResId);
            SkinAttr themeAttr = AttrFactory.get(AttrFactory.BACKGROUND_TINT, backgroundTintResId, entryName, typeName);
            if (themeAttr != null) {
                viewAttrs.add(themeAttr);
            }
        }

        int srcResId = array.getResourceId(R.styleable.SkinAttr_android_src, 0);
        if (srcResId != 0) {
            String entryName = context.getResources().getResourceEntryName(srcResId);
            String typeName = context.getResources().getResourceTypeName(srcResId);
            SkinAttr themeAttr = AttrFactory.get(AttrFactory.SRC, srcResId, entryName, typeName);
            if (themeAttr != null) {
                viewAttrs.add(themeAttr);
            }
        }

        int colorHitResId = array.getResourceId(R.styleable.SkinAttr_android_textColorHint, 0);
        if (colorHitResId != 0) {
            String entryName = context.getResources().getResourceEntryName(colorHitResId);
            String typeName = context.getResources().getResourceTypeName(colorHitResId);
            SkinAttr themeAttr = AttrFactory.get(AttrFactory.TEXT_COLOR_HINT, colorHitResId, entryName, typeName);
            if (themeAttr != null) {
                viewAttrs.add(themeAttr);
            }
        }

        int drawableStartResId = array.getResourceId(R.styleable.SkinAttr_android_drawableStart, 0);
        if (drawableStartResId != 0) {
            String entryName = context.getResources().getResourceEntryName(drawableStartResId);
            String typeName = context.getResources().getResourceTypeName(drawableStartResId);
            SkinAttr themeAttr = AttrFactory.get(AttrFactory.DRAWABLE_START, drawableStartResId, entryName, typeName);
            if (themeAttr != null) {
                viewAttrs.add(themeAttr);
            }
        }

        int switchColor = array.getResourceId(R.styleable.SkinAttr_switchColor, 0);
        if (switchColor != 0) {
            String entryName = context.getResources().getResourceEntryName(switchColor);
            String typeName = context.getResources().getResourceTypeName(switchColor);
            SkinAttr themeAttr = AttrFactory.get(AttrFactory.SWITCH_COLOR, switchColor, entryName, typeName);
            if (themeAttr != null) {
                viewAttrs.add(themeAttr);
            }
        }

        int buttonTintResId = array.getResourceId(R.styleable.SkinAttr_android_buttonTint, 0);
        if (buttonTintResId != 0) {
            String entryName = context.getResources().getResourceEntryName(buttonTintResId);
            String typeName = context.getResources().getResourceTypeName(buttonTintResId);
            SkinAttr themeAttr = AttrFactory.get(AttrFactory.BUTTON_TINT, buttonTintResId, entryName, typeName);
            if (themeAttr != null) {
                viewAttrs.add(themeAttr);
            }
        }

        array.recycle();

        if (viewAttrs.size() > 0) {
            SkinItem themeItem = new SkinItem();
            themeItem.mView = view;
            themeItem.mAttrs = viewAttrs;

            mSkinItems.add(themeItem);

            if (!SkinManager.getInstance().isDefaultSkin()) {
                themeItem.apply();
            }
        }

    }

    /**
     * parse theme attribute, such as background, textColor and so on
     *
     * @param context
     * @param attributeSet
     * @param view
     */
    private void parseAttr(Context context, AttributeSet attributeSet, View view) {

        List<SkinAttr> viewAttrs = new ArrayList<>();

        for (int i = 0; i < attributeSet.getAttributeCount(); i++) {
            String attrName = attributeSet.getAttributeName(i);
            String attrValue = attributeSet.getAttributeValue(i);

            if (!AttrFactory.isSupportedAttr(attrName)) {
                continue;
            }

            if (attrValue.startsWith("@")) {
                try {
                    int id = Integer.parseInt(attrValue.substring(1));
                    String entryName = context.getResources().getResourceEntryName(id);
                    String typeName = context.getResources().getResourceTypeName(id);

                    SkinAttr skinAttr = AttrFactory.get(attrName, id, entryName, typeName);
                    if (skinAttr != null) {
                        viewAttrs.add(skinAttr);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (attrValue.startsWith("?")) {
                int id = Integer.parseInt(attrValue.substring(1));

                TypedValue outValue = new TypedValue();
                boolean got = context.getTheme().resolveAttribute(id, outValue, false);

                if (!got) {
                    Log.e(TAG, "can not resolve attribute attr id:" + id);
                    return;
                }


                id = outValue.data;
                String entryName = context.getResources().getResourceEntryName(id);
                String typeName = context.getResources().getResourceTypeName(id);

                SkinAttr skinAttr = AttrFactory.get(attrName, id, entryName, typeName);
                if (skinAttr != null) {
                    viewAttrs.add(skinAttr);
                }
            }
        }

        if (viewAttrs.size() > 0) {
            SkinItem skinItem = new SkinItem();
            skinItem.mView = view;
            skinItem.mAttrs = viewAttrs;

            mSkinItems.add(skinItem);

            if (!SkinManager.getInstance().isDefaultSkin()) {
                skinItem.apply();
            }
        }

    }

    public void applySkin() {
        if (mSkinItems == null || mSkinItems.size() == 0) {
            return;
        }

        for (SkinItem item : mSkinItems) {
            if (item.mView == null) {
                continue;
            }
            item.apply();
        }
    }

    public void clean() {
        Log.d(TAG, "clean theme");

        if (mSkinItems != null) {
            mSkinItems.clear();
            mSkinItems = null;
        }
    }

    public void addSkinView(SkinItem item) {
        mSkinItems.add(item);
    }


    /**
     * view need to be themed
     *
     * @param context
     * @param view
     * @param dynamicAttrs
     * @param refresh      true to refresh added view right now, false otherwise
     */
    public void dynamicAddSkinEnableView(Context context, View view, List<DynamicAttr> dynamicAttrs,
                                         boolean refresh) {
        List<SkinAttr> viewAttrs = new ArrayList<>();
        SkinItem skinItem = new SkinItem();
        skinItem.mView = view;

        for (DynamicAttr attr : dynamicAttrs) {
            int id = attr.mRefResId;
            String entryName = context.getResources().getResourceEntryName(id);
            String typeName = context.getResources().getResourceTypeName(id);
            SkinAttr themeAttr = AttrFactory.get(attr.mAttrName, id, entryName, typeName);
            viewAttrs.add(themeAttr);
            Log.d(TAG, "dynamicAddSkinEnableView entryName:" + entryName + " typeName:" + typeName);
        }

        skinItem.mAttrs = viewAttrs;
        addSkinView(skinItem);

        if (refresh) {
            skinItem.apply();
        }

        Log.d(TAG, "dynamicAddThemeEnableView themeItem:" + skinItem.toString());
    }

    /***
     * view need to be themed
     *
     * @param context
     * @param view
     * @param attrName
     * @param attrValueResId
     * @param refresh        true to refresh added view right now, false otherwise
     */
    public void dynamicAddSkinEnableView(Context context, View view,
                                         String attrName, int attrValueResId, boolean refresh) {
        int id = attrValueResId;
        String entryName = context.getResources().getResourceEntryName(id);
        String typeName = context.getResources().getResourceTypeName(id);
        SkinAttr skinAttr = AttrFactory.get(attrName, id, entryName, typeName);
        if (skinAttr == null) {
            return;
        }
        SkinItem skinItem = new SkinItem();
        skinItem.mView = view;
        List<SkinAttr> viewAttrs = new ArrayList<>();
        viewAttrs.add(skinAttr);
        skinItem.mAttrs = viewAttrs;
        addSkinView(skinItem);

        if (refresh) {
            skinItem.apply();
        }

        Log.d(TAG, "dynamicAddThemeEnableView themeItem:" + skinItem.toString());
    }
}

