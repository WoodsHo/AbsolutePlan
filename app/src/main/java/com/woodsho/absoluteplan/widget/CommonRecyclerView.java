package com.woodsho.absoluteplan.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.utils.CommonUtil;

/**
 * Created by hewuzhao on 17/12/20.
 */

public class CommonRecyclerView extends RecyclerView {
    private int mWidth, mHeight;
    private Bitmap mBgBitmap;
    private Paint mBgPaint;

    public CommonRecyclerView(Context context) {
        this(context, null);
    }

    public CommonRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mBgPaint = new Paint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mBgBitmap.recycle();
        mBgBitmap = null;
    }

    @Override
    public void onDraw(Canvas c) {
        if (mBgBitmap == null) {
            Drawable drawable = CommonUtil.getWallpaperDrawable();
            if (drawable != null) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                mBgBitmap = bitmapDrawable.getBitmap();
            }
            if (mBgBitmap == null) {
                mBgBitmap = decodeSampledBitmapFromResource(getResources(), R.drawable.common_bg, mWidth, mHeight);
            }
        }
        c.drawBitmap(mBgBitmap, 0, 0, mBgPaint);
        super.onDraw(c);
    }

    /**
     * 谷歌推荐使用方法，从资源中加载图像，并高效压缩，有效降低OOM的概率
     * @param res 资源
     * @param resId 图像资源的资源id
     * @param reqWidth 要求图像压缩后的宽度
     * @param reqHeight 要求图像压缩后的高度
     * @return
     */
    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // 设置inJustDecodeBounds = true ,表示获取图像信息，但是不将图像的像素加入内存
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // 调用方法计算合适的 inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // inJustDecodeBounds 置为 false 真正开始加载图片
        options.inJustDecodeBounds = false;
        //将options.inPreferredConfig改成Bitmap.Config.RGB_565，
        // 是默认情况Bitmap.Config.ARGB_8888占用内存的一般
        options.inPreferredConfig= Bitmap.Config.RGB_565;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    // 计算 BitmapFactpry 的 inSimpleSize的值的方法
    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {
        if (reqWidth == 0 || reqHeight == 0) {
            return 1;
        }

        // 获取图片原生的宽和高
        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 1;

        // 如果原生的宽高大于请求的宽高,那么将原生的宽和高都置为原来的一半
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // 主要计算逻辑
            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
