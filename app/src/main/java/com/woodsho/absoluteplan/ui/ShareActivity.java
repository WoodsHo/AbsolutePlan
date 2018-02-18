package com.woodsho.absoluteplan.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.bean.BottomShareItem;
import com.woodsho.absoluteplan.listener.OnResponseListener;
import com.woodsho.absoluteplan.skinloader.SkinBaseActivity;
import com.woodsho.absoluteplan.skinloader.SkinManager;
import com.woodsho.absoluteplan.utils.CommonUtil;
import com.woodsho.absoluteplan.utils.StatusBarUtil;
import com.woodsho.absoluteplan.widget.BottomShareDialog;
import com.woodsho.absoluteplan.wxapi.WXShare;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hewuzhao on 18/2/13.
 */

public class ShareActivity extends SkinBaseActivity {
    public static final String TAG = "ShareActivity";

    public static final String KEY_TITLE = "key_title";
    public static final String KEY_CONTENT = "key_content";

    private WXShare mWXShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        mWXShare = new WXShare(this);
        mWXShare.setListener(new OnResponseListener() {
            @Override
            public void onSuccess() {
                // 分享成功
                Log.d(TAG, "share success");
                Toast.makeText(getApplicationContext(), "分享成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                // 分享取消
                Log.d(TAG, "share cancel");
                Toast.makeText(getApplicationContext(), "分享取消", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(String message) {
                // 分享失败
                Log.d(TAG, "share fail, message: " + message);
                Toast.makeText(getApplicationContext(), "分享失败, 信息: " + message, Toast.LENGTH_SHORT).show();
            }
        });
        setupActionBar();
        StatusBarUtil.setColor(this, SkinManager.getInstance().getColor(R.color.colorPrimary), 0);
        init();
    }

    private void setupActionBar() {
        ViewGroup rootView = (ViewGroup) findViewById(R.id.action_bar_root); //id from appcompat

        if (rootView != null) {
            View view = getLayoutInflater().inflate(R.layout.share_toolbar_layout, rootView, false);
            rootView.addView(view, 0);

            Toolbar toolbar = (Toolbar) findViewById(R.id.share_toolbar);
            toolbar.setBackgroundColor(SkinManager.getInstance().getColor(R.color.colorPrimary));
            setSupportActionBar(toolbar);
            ImageView back = (ImageView) findViewById(R.id.back_share);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            ImageView share = (ImageView) findViewById(R.id.share_bt);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String title = getIntent().getStringExtra(KEY_TITLE);
                    final String content = getIntent().getStringExtra(KEY_CONTENT);

                    new BottomShareDialog(ShareActivity.this, getBottomShareItems(), new BottomShareDialog.OnItemClickListener() {
                                @Override
                                public void click(BottomShareItem item) {
                                    switch (item.getId()) {
                                        case R.id.moments:
                                            if (mWXShare.checkTimeLine()) {
                                                mWXShare.share2Wx(title, content, false);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "当前微信版本不支持分享到朋友圈", Toast.LENGTH_SHORT).show();
                                            }
                                            break;
                                        case R.id.wechat:
                                            if (CommonUtil.isWxInstall(getApplicationContext())) {
                                                mWXShare.share2Wx(title, content, true);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "请先安装微信", Toast.LENGTH_SHORT).show();
                                            }
                                            break;
                                    }
                                }
                            })
                            .show();
                }
            });
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private List<BottomShareItem> getBottomShareItems() {
        List<BottomShareItem> list = new ArrayList<>();
        list.add(new BottomShareItem(R.id.wechat, "微信", R.drawable.ic_share_wechat));
        list.add(new BottomShareItem(R.id.moments, "朋友圈", R.drawable.ic_share_moments));
        return list;
    }

    private void init() {
        String title = getIntent().getStringExtra(KEY_TITLE);
        String content = getIntent().getStringExtra(KEY_CONTENT);
        ImageView imageView = (ImageView) findViewById(R.id.share_imageview);
        imageView.setImageBitmap(mWXShare.getBitmap(title, content));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mWXShare.register();
    }

    @Override
    protected void onDestroy() {
        mWXShare.unregister();
        super.onDestroy();
    }

}
