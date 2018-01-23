package com.woodsho.absoluteplan.skinloader;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.View;

import com.woodsho.absoluteplan.R;

/**
 * Created by hewuzhao on 18/1/22.
 */

public class SwitchColorAttr extends SkinAttr {
    @Override
    public void apply(View view) {
        if (RES_TYPE_NAME_COLOR.equals(attrValueTypeName)) {
            if (view instanceof SwitchCompat) {
                SwitchCompat switchCompat = (SwitchCompat) view;
                setSwitchColor(switchCompat, SkinManager.getInstance().getColor(attrValueRefId), SkinManager.getInstance().getColor(R.color.switch_thumb_unchecked_color));
            }
        }
    }

    public void setSwitchColor(SwitchCompat v, int checkedColor, int unCheckedColor) {
        // set the thumb color (圆点)
        DrawableCompat.setTintList(v.getThumbDrawable(), new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        checkedColor,
                        unCheckedColor
                }));

        // set the track color (横条)
        DrawableCompat.setTintList(v.getTrackDrawable(), new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        Color.argb(100, Color.red(checkedColor), Color.green(checkedColor), Color.blue(checkedColor)),
                        Color.argb(100, Color.red(unCheckedColor), Color.green(unCheckedColor), Color.blue(unCheckedColor))
                }));
    }
}
