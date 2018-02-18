package com.woodsho.absoluteplan.wxapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.listener.OnResponseListener;
import com.woodsho.absoluteplan.utils.CommonUtil;

/**
 * Created by hewuzhao on 18/2/15.
 */

public class WXShare {
    public static final String TAG = "WXShare";

    public static final String APP_ID = "wx1fbcf4791a36d519";
    public static final String ACTION_SHARE_RESPONSE = "action_wx_share_response";
    public static final String EXTRA_RESULT = "result";
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

    private static final int THUMB_SIZE = 300;
    private static final int WIDTH_BITMAP = 840;
    private int mHeightBitmap;

    private final Context context;
    private final IWXAPI api;
    private OnResponseListener listener;
    private ResponseReceiver receiver;

    public WXShare(Context context) {
        api = WXAPIFactory.createWXAPI(context, APP_ID);
        this.context = context;
    }

    public WXShare register() {
        // 微信分享
        api.registerApp(APP_ID);
        receiver = new ResponseReceiver();
        IntentFilter filter = new IntentFilter(ACTION_SHARE_RESPONSE);
        context.registerReceiver(receiver, filter);
        return this;
    }

    public void unregister() {
        try {
            api.unregisterApp();
            context.unregisterReceiver(receiver);
        } catch (Exception e) {
            Log.e(TAG, "ex: " + e);
        }
    }

    public boolean checkTimeLine() {
        int wxSdkVersion = api.getWXAppSupportAPI();
        return wxSdkVersion >= TIMELINE_SUPPORTED_VERSION;
    }

    public void share2Wx(String title, String content, boolean isShareFriend) {
        Bitmap bitmap = getBitmap(title, content);
        WXImageObject imgObj = new WXImageObject(bitmap);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, (THUMB_SIZE * mHeightBitmap / WIDTH_BITMAP), true);//缩略图大小
        bitmap.recycle();
        msg.thumbData = CommonUtil.bmpToByteArray(thumbBmp, 32);  // 设置缩略图不能超过32k

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = isShareFriend ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }

    public Bitmap getBitmap(String title, String content) {
        Resources res = context.getResources();
        float bitmapWidth = WIDTH_BITMAP;
        float padding = 35;

        boolean hasTitle = !TextUtils.isEmpty(title);
        boolean hasContent = !TextUtils.isEmpty(content);

        if (!hasTitle && !hasContent) {
            return null;
        }

        StaticLayout titleLayout = null;
        if (hasTitle) {
            TextPaint titlePaint = new TextPaint();
            titlePaint.setColor(res.getColor(R.color.share_other_color));
            titlePaint.setTextSize(res.getDimensionPixelSize(R.dimen.text_size_17));
            titlePaint.setFakeBoldText(true);
            titlePaint.setAntiAlias(true);
            titleLayout = new StaticLayout(title, titlePaint,
                    (int) (bitmapWidth - padding * 4), Layout.Alignment.ALIGN_NORMAL,
                    1.5F, 0.0F, true);
        }
        int titleLayoutHeight = hasTitle ? titleLayout.getHeight() : 0;

        StaticLayout contentLayout = null;
        if (hasContent) {
            TextPaint contentPaint = new TextPaint();
            contentPaint.setColor(res.getColor(R.color.share_other_color));
            contentPaint.setTextSize(res.getDimensionPixelSize(R.dimen.text_size_12));
            contentPaint.setAntiAlias(true);
            contentLayout = new StaticLayout(content, contentPaint,
                    (int) (bitmapWidth - padding * 4), Layout.Alignment.ALIGN_NORMAL,
                    1.5F, 0.0F, true);
        }

        int contentLayoutHeight = hasContent ? contentLayout.getHeight() : 0;
        int title_content_padding = (hasTitle && hasContent) ? 30 : 0;

        float bitmapHeight = titleLayoutHeight + contentLayoutHeight + padding * 9 + title_content_padding;
        mHeightBitmap = (int) bitmapHeight;

        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap((int) bitmapWidth, (int) bitmapHeight,
                    Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "ex： " + e);
            return null;
        }
        Canvas paper = new Canvas(bitmap);
        paper.drawColor(res.getColor(R.color.share_bg_color));

        paper.translate(padding * 2, padding * 4);

        if (hasTitle) {
            titleLayout.draw(paper);
        }

        paper.translate(0, titleLayoutHeight + title_content_padding);

        if (hasContent) {
            contentLayout.draw(paper);
        }

        paper.translate(-padding * 2, -padding * 4 - titleLayoutHeight - title_content_padding);

        Paint mPaint = new Paint(Paint.DITHER_FLAG);
        mPaint.setColor(res.getColor(R.color.share_other_color));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);

        float offset = 6;

        //外圈
        paper.drawRect(new Rect(
                (int) (padding),
                (int) (padding * 2),
                (int) (bitmapWidth - padding),
                (int) (bitmapHeight - padding * 4)), mPaint);

        //左上角小正方形
//        paper.drawRect(new Rect(
//                (int) (padding - offset),
//                (int) (padding * 2 - offset),
//                (int) (padding),
//                (int) (padding * 2)), mPaint);
//
//        //右上角小正方形
//        paper.drawRect(new Rect((int) (bitmapWidth - padding),
//                        (int) (padding * 2 - offset),
//                        (int) (bitmapWidth - padding + offset), (int) (padding * 2)),
//                mPaint);
//
//        //左下角小正方形
//        paper.drawRect(new Rect((int) (padding - offset),
//                (int) (bitmapHeight - padding * 4), (int) (padding),
//                (int) (bitmapHeight - padding * 4 + offset)), mPaint);
//
//        //右下角小正方形
//        paper.drawRect(new Rect((int) (bitmapWidth - padding),
//                (int) (bitmapHeight - padding * 4), (int) (bitmapWidth
//                - padding + offset),
//                (int) (bitmapHeight - padding * 4 + offset)), mPaint);
//
//        //内圈
//        paper.drawRect(new Rect((int) (padding + offset),
//                (int) (padding * 2 + offset),
//                (int) (bitmapWidth - padding - offset), (int) (bitmapHeight
//                - padding * 4 - offset)), mPaint);
        int rectSize = 35;
        int rectPadding = 15;

        paper.drawRect(new Rect(
                (int) (padding),
                (int) (bitmapHeight - padding * 3),
                (int) (padding + rectSize),
                (int) (bitmapHeight - padding * 3 + rectSize)), mPaint);

        paper.drawRect(new Rect(
                (int) (padding + rectSize + rectPadding),
                (int) (bitmapHeight - padding * 3),
                (int) (padding + rectSize + rectPadding + rectSize),
                (int) (bitmapHeight - padding * 3 + rectSize)), mPaint);

        paper.drawRect(new Rect(
                (int) (padding + rectSize + rectPadding + rectSize + rectPadding),
                (int) (bitmapHeight - padding * 3),
                (int) (padding + rectSize + rectPadding + rectSize + rectPadding + rectSize),
                (int) (bitmapHeight - padding * 3 + rectSize)), mPaint);

        paper.drawRect(new Rect(
                (int) (padding + rectSize + rectPadding + rectSize + rectPadding + rectSize + rectPadding),
                (int) (bitmapHeight - padding * 3),
                (int) (padding + rectSize + rectPadding + rectSize + rectPadding + rectSize + rectPadding + rectSize),
                (int) (bitmapHeight - padding * 3 + rectSize)), mPaint);

        TextPaint fromTextPaint = new TextPaint();
        fromTextPaint.setColor(res.getColor(R.color.share_other_color));
        fromTextPaint.setTextSize(res.getDimensionPixelSize(R.dimen.text_size_10));
        fromTextPaint.setStyle(Paint.Style.FILL);
        fromTextPaint.setAntiAlias(true);
        StaticLayout fromLayout = new StaticLayout("来自：Abs计划",
                fromTextPaint, (int) (bitmapWidth - padding * 2),
                Layout.Alignment.ALIGN_OPPOSITE, 1.0F, 0.0F, true);
        paper.translate(padding, bitmapHeight - padding * 3);
        fromLayout.draw(paper);



        return bitmap;
    }

    public IWXAPI getApi() {
        return api;
    }

    public void setListener(OnResponseListener listener) {
        this.listener = listener;
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Response response = intent.getParcelableExtra(EXTRA_RESULT);
            String result;
            if (listener != null) {
                if (response.errCode == BaseResp.ErrCode.ERR_OK) {
                    listener.onSuccess();
                } else if (response.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
                    listener.onCancel();
                } else {
                    switch (response.errCode) {
                        case BaseResp.ErrCode.ERR_AUTH_DENIED:
                            result = "发送被拒绝";
                            break;
                        case BaseResp.ErrCode.ERR_UNSUPPORT:
                            result = "不支持错误";
                            break;
                        default:
                            result = "发送返回";
                            break;
                    }
                    Log.d(TAG, "result: " + result);
                    listener.onFail(result);
                }
            }
        }
    }

    public static class Response extends BaseResp implements Parcelable {

        public int errCode;
        public String errStr;
        public String transaction;
        public String openId;

        private int type;
        private boolean checkResult;

        public Response(BaseResp baseResp) {
            errCode = baseResp.errCode;
            errStr = baseResp.errStr;
            transaction = baseResp.transaction;
            openId = baseResp.openId;
            type = baseResp.getType();
            checkResult = baseResp.checkArgs();
        }

        @Override
        public int getType() {
            return type;
        }

        @Override
        public boolean checkArgs() {
            return checkResult;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.errCode);
            dest.writeString(this.errStr);
            dest.writeString(this.transaction);
            dest.writeString(this.openId);
            dest.writeInt(this.type);
            dest.writeByte(this.checkResult ? (byte) 1 : (byte) 0);
        }

        protected Response(Parcel in) {
            this.errCode = in.readInt();
            this.errStr = in.readString();
            this.transaction = in.readString();
            this.openId = in.readString();
            this.type = in.readInt();
            this.checkResult = in.readByte() != 0;
        }

        public static final Creator<Response> CREATOR = new Creator<Response>() {
            @Override
            public Response createFromParcel(Parcel source) {
                return new Response(source);
            }

            @Override
            public Response[] newArray(int size) {
                return new Response[size];
            }
        };
    }

}
