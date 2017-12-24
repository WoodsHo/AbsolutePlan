package com.woodsho.absoluteplan.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import com.woodsho.absoluteplan.R;

/**
 * Created by hewuzhao on 17/12/23.
 */

public class FloatingActionMenu extends ViewGroup {
    public static final String TAG = "FloatingActionMenu";

    private static final int ANIMATION_DURATION = 300;//动画持续时间（毫秒ms）
    private AnimatorSet mExpandAnimation = new AnimatorSet().setDuration(ANIMATION_DURATION);
    private AnimatorSet mCollapseAnimation = new AnimatorSet().setDuration(ANIMATION_DURATION);

    private View mBaseView;//菜单中放入的第一个基础控件
    private int mChildviewSpacing = 30;//空白距离
    private boolean mExpanded;//菜单是否打开
    private int mMaxnWidth;//总的最大宽度
    private int mChildCount;//item数

    private int mChildViewIconWidth;//子控件的图标的宽度
    private int mMainViewIconWidht;//主按钮的图标的宽度

    private OnFloatingActionsMenuUpdateListener mListener;//展开或收起更新监听

    public interface OnFloatingActionsMenuUpdateListener {
        void onMenuExpanded();//打开

        void onMenuCollapsed();//收起
    }


    public FloatingActionMenu(Context context) {
        this(context, null);
    }

    public FloatingActionMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources res = context.getResources();
        mChildViewIconWidth = res.getDimensionPixelSize(R.dimen.button_item_float_action_menu_width);
        mMainViewIconWidht = res.getDimensionPixelSize(R.dimen.main_float_action_menu_width);
    }

    private void getFrontView() {
        mBaseView = getChildAt(0);
    }

    /**
     * 添加控件（或组合控件）
     *
     * @param view
     */
    public void addActionsView(View view) {
        addView(view, mChildCount - 1);
        mChildCount++;

    }

    public void toggle() {
        if (mExpanded) {
            collapse();
        } else {
            expand();
            ((FloatingActionButton)mBaseView).setImageDrawable(getResources().getDrawable(R.drawable.ic_fab_menu_down));
        }
    }

    public void expand() {
        if (!mExpanded) {
            mExpanded = true;
            mCollapseAnimation.cancel();
            mExpandAnimation.start();

            if (mListener != null) {
                mListener.onMenuExpanded();
            }
        }
    }


    public void collapse() {
        collapse(false);
    }

    private void collapse(boolean immediately) {
        if (mExpanded) {
            mExpanded = false;
            mCollapseAnimation.setDuration(immediately ? 0 : ANIMATION_DURATION);
            mCollapseAnimation.start();
            mExpandAnimation.cancel();

            if (mListener != null) {
                mListener.onMenuCollapsed();
            }
        }
    }

    /**
     * 测量
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int width = 0;
        int height = 0;
        mMaxnWidth = 0;
        for (int i = 0; i < mChildCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            mMaxnWidth = Math.max(mMaxnWidth, child.getMeasuredWidth());
            height += child.getMeasuredHeight();
        }
        // 此处多加是因为下面childview的绘制的最右边right是根据baseview的宽与childview的icon的宽差决定的, 20是为阴影而留的空间见onLayout
        mMaxnWidth += ((mMainViewIconWidht - mChildViewIconWidth) / 2 + 20);
        width = (mMaxnWidth > 0 ? mMaxnWidth : 0);
        height += mChildviewSpacing * (mChildCount - 1);
        //弹出一段距离，然后回到原处（一种反弹效果）
        height = height * 12 / 10;

        setMeasuredDimension(width, height);
    }

    /**
     * 布局
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 这里减去20是为了留空间显示floatactionbutton的阴影
        int addButtonY = b - t - mBaseView.getMeasuredHeight() - 30;

        mBaseView.layout(mMaxnWidth - mBaseView.getMeasuredWidth() - 20, addButtonY, mMaxnWidth - 20, addButtonY + mBaseView.getMeasuredHeight());

        int nextY = addButtonY - mChildviewSpacing;

        //子view中，保持icon都处于中心列
        int childEnd = mMaxnWidth - (mBaseView.getMeasuredWidth() - mChildViewIconWidth) / 2 - 20;

        for (int i = mChildCount - 1; i >= 0; i--) {
            final View child = getChildAt(i);

            if (child == mBaseView || child.getVisibility() == GONE)
                continue;

            int childY = nextY - child.getMeasuredHeight();
            child.layout(childEnd - child.getMeasuredWidth(), childY, childEnd, childY + child.getMeasuredHeight());

            float collapsedTranslation = addButtonY - childY;
            float expandedTranslation = 0f;

            //Y轴上移动
            child.setTranslationY(mExpanded ? expandedTranslation : collapsedTranslation);
            child.setAlpha(mExpanded ? 1f : 0f);

            LayoutParams params = (LayoutParams) child.getLayoutParams();
            params.mCollapseDir.setFloatValues(expandedTranslation, collapsedTranslation);
            params.mExpandDir.setFloatValues(collapsedTranslation, expandedTranslation);
            params.setAnimationsTarget(child);

            nextY = childY - mChildviewSpacing;
        }
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(super.generateDefaultLayoutParams());
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(super.generateLayoutParams(attrs));
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(super.generateLayoutParams(p));
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return super.checkLayoutParams(p);
    }

    //OvershootInterpolator向前甩一定值后再回到原来位置
    private static Interpolator sExpandInterpolator = new OvershootInterpolator();
    private static Interpolator sCollapseInterpolator = new DecelerateInterpolator(3f);
    private static Interpolator sAlphaExpandInterpolator = new DecelerateInterpolator();

    private class LayoutParams extends ViewGroup.LayoutParams {

        private ObjectAnimator mExpandDir = new ObjectAnimator();
        private ObjectAnimator mExpandAlpha = new ObjectAnimator();
        private ObjectAnimator mCollapseDir = new ObjectAnimator();
        private ObjectAnimator mCollapseAlpha = new ObjectAnimator();
        private boolean animationsSetToPlay;

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);

            //android interpolator含义和用法Interpolator 被用来修饰动画效果，定义动画的变化率，
            //可以使存在的动画效果accelerated(加速)，decelerated(减速),repeated(重复),bounced(弹跳)等。
            mExpandDir.setInterpolator(sExpandInterpolator);//设置甩出一段距离再回到原来位置
            mExpandAlpha.setInterpolator(sAlphaExpandInterpolator);
            mCollapseDir.setInterpolator(sCollapseInterpolator);
            mCollapseAlpha.setInterpolator(sCollapseInterpolator);

            mCollapseAlpha.setProperty(View.ALPHA);
            mCollapseAlpha.setFloatValues(1f, 0f);

            mExpandAlpha.setProperty(View.ALPHA);
            mExpandAlpha.setFloatValues(0f, 1f);

            mCollapseDir.setProperty(View.TRANSLATION_Y);
            mExpandDir.setProperty(View.TRANSLATION_Y);
        }

        public void setAnimationsTarget(View view) {
            mCollapseAlpha.setTarget(view);
            mCollapseDir.setTarget(view);
            mExpandAlpha.setTarget(view);
            mExpandDir.setTarget(view);

            // Now that the animations have targets, set them to be played
            if (!animationsSetToPlay) {
                addLayerTypeListener(mExpandDir, view);
                addLayerTypeListener(mCollapseDir, view);

                mCollapseAnimation.play(mCollapseAlpha);
                mCollapseAnimation.play(mCollapseDir);
                mExpandAnimation.play(mExpandAlpha);
                mExpandAnimation.play(mExpandDir);
                animationsSetToPlay = true;
            }
        }

        private void addLayerTypeListener(Animator animator, final View view) {
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setLayerType(LAYER_TYPE_NONE, null);
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    view.setLayerType(LAYER_TYPE_HARDWARE, null);
                }
            });
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        getFrontView();
        bringChildToFront(mBaseView);
        mChildCount = getChildCount();
    }
}
