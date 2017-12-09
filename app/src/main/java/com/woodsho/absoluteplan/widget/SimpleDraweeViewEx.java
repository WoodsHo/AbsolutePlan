package com.woodsho.absoluteplan.widget;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Created by hewuzhao on 17/12/9.
 */

public class SimpleDraweeViewEx extends SimpleDraweeView {
    private static final String TAG = "SimpleDraweeViewEx";

    public SimpleDraweeViewEx(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    public SimpleDraweeViewEx(Context context) {
        super(context);
    }

    public SimpleDraweeViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleDraweeViewEx(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageURI(Uri uri) {
        setImageURI(uri, null);
    }

    public void setImageURI(Uri uri, ControllerListener controllerListener) {
        setImageURI(uri, controllerListener, null);
    }

    public void prefetchImageURI(Uri uri) {
        ViewGroup.LayoutParams ltParams = getLayoutParams();
        int width = ltParams.width;
        int height = ltParams.height;
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri).setResizeOptions(new
                ResizeOptions(width, height)).build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.prefetchToBitmapCache(imageRequest, null);
    }

    public void setImageURI(Uri uri, ControllerListener controllerListener, BasePostprocessor postprocessor) {
        ViewGroup.LayoutParams ltParams = getLayoutParams();
        int width = ltParams.width;
        int height = ltParams.height;
        Log.d(TAG, " w : " + width + " h : " + height);
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .setPostprocessor(postprocessor)
                .build();
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setOldController(getController())
                .setAutoPlayAnimations(true)
                .setControllerListener(controllerListener)
                .build();
        setController(draweeController);
    }
}
