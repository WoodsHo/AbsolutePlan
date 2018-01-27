package com.woodsho.absoluteplan.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.woodsho.absoluteplan.AbsolutePlanApplication;
import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.adapter.SkinAdapter;
import com.woodsho.absoluteplan.bean.SkinAdapterItem;
import com.woodsho.absoluteplan.listener.PermissionListener;
import com.woodsho.absoluteplan.skinloader.ILoaderListener;
import com.woodsho.absoluteplan.skinloader.SkinBaseActivity;
import com.woodsho.absoluteplan.skinloader.SkinManager;
import com.woodsho.absoluteplan.skinloader.SkinSharedPreferences;
import com.woodsho.absoluteplan.utils.CommonUtil;
import com.woodsho.absoluteplan.utils.FileUtil;
import com.woodsho.absoluteplan.utils.PermissionUtil;
import com.woodsho.absoluteplan.utils.StatusBarUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SkinActivity extends SkinBaseActivity implements SkinAdapter.OnSkinItemClickListener, PermissionListener {
    private static final String TAG = "SkinActivity";

    public SkinAdapter mSkinAdapter;
    private static final String PATH = Environment
            .getExternalStorageDirectory() + File.separator + "com.woodsho.absoluteplan";

    public static final String SKIN_BLUEGREY = "skin_bluegrey.skin";
    public static final String SKIN_GREY = "skin_grey.skin";
    public static final String SKIN_DEEPORANGE = "skin_deeporange.skin";
    public static final String SKIN_TEAL = "skin_teal.skin";
    public static final String SKIN_BLACK = "skin_black.skin";
    public static final String SKIN_BLUE = "skin_blue.skin";
    public static final String SKIN_PURPLE = "skin_purple.skin";
    public static final String SKIN_PINK = "skin_pink.skin";
    public static final String SKIN_RED = "skin_red.skin";

    public TextView mSkinDefault;

    public Handler mUiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);
        SlidrConfig mConfig = new SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .velocityThreshold(2400)
                .distanceThreshold(.25f)
                .edge(true)
                .touchSize(CommonUtil.dp2px(this, 32))
                .build();
        Slidr.attach(this, mConfig);
        setupActionBar();
        StatusBarUtil.setColor(this, SkinManager.getInstance().getColor(R.color.colorPrimary), 0);
        checkPermissions();
    }

    private void setupActionBar() {
        ViewGroup rootView = (ViewGroup) findViewById(R.id.action_bar_root); //id from appcompat

        if (rootView != null) {
            View view = getLayoutInflater().inflate(R.layout.skin_toolbar_layout, rootView, false);
            rootView.addView(view, 0);

            Toolbar toolbar = (Toolbar) findViewById(R.id.skin_toolbar);
            toolbar.setBackgroundColor(SkinManager.getInstance().getColor(R.color.colorPrimary));
            dynamicAddView(toolbar, "background", R.color.colorPrimary, true);
            setSupportActionBar(toolbar);
            ImageView back = (ImageView) findViewById(R.id.back_skin);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            mSkinDefault = (TextView) findViewById(R.id.skin_default);
            if (SkinSharedPreferences.getInstance().isDefaultSkin()) {
                mSkinDefault.setVisibility(View.GONE);
            }
            mSkinDefault.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SkinManager.getInstance().restoreDefaultSkin();
                    SkinSharedPreferences.getInstance().saveApplyingSkinName("");
                    if (mSkinAdapter != null) {
                        mSkinAdapter.restoreDefaultSkin();
                    }
                    mSkinDefault.setVisibility(View.GONE);
                }
            });
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void init() {
        mSkinDefault.setText("恢复默认");
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.skin_recyclerview);
        List<SkinAdapterItem> list = getSkinList();
        mSkinAdapter = new SkinAdapter(AbsolutePlanApplication.sAppContext, list);
        mSkinAdapter.setOnSkinItemClickListener(this);
        recyclerView.setAdapter(mSkinAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 20;
            }
        });
    }

    private List<SkinAdapterItem> getSkinList() {
        List<SkinAdapterItem> list = new ArrayList<>();
        list.add(new SkinAdapterItem("混灰蓝", SKIN_BLUEGREY, R.color.skin_bluegrey_color));
        list.add(new SkinAdapterItem("低调灰", SKIN_GREY, R.color.skin_grey_color));
        list.add(new SkinAdapterItem("幽暗橙", SKIN_DEEPORANGE, R.color.skin_deeporange_color));
        list.add(new SkinAdapterItem("水鸭绿", SKIN_TEAL, R.color.skin_teal_color));
        list.add(new SkinAdapterItem("高端黑", SKIN_BLACK, R.color.skin_black_color));
        list.add(new SkinAdapterItem("知乎蓝", SKIN_BLUE, R.color.skin_blue_color));
        list.add(new SkinAdapterItem("基佬紫", SKIN_PURPLE, R.color.skin_purple_color));
        list.add(new SkinAdapterItem("哔哩粉", SKIN_PINK, R.color.skin_pink_color));
        list.add(new SkinAdapterItem("姨妈红", SKIN_RED, R.color.skin_red_color));
        return list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSkinAdapter != null) {
            mSkinAdapter.removeSkinItemClickListener();
        }
    }

    @Override
    public void onSkinUpdate() {
        super.onSkinUpdate();
        StatusBarUtil.setColor(this, SkinManager.getInstance().getColor(R.color.colorPrimary), 0);
    }

    @Override
    public void onSkinItemClick(final SkinAdapterItem item) {
        String skinFullName = PATH + File.separator + item.path;
        FileUtil.moveRawToDir(AbsolutePlanApplication.sAppContext, item.path, skinFullName);
        File skin = new File(skinFullName);
        if (!skin.exists()) {
            Toast.makeText(AbsolutePlanApplication.sAppContext, "请检查 " + skinFullName + " 是否存在", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mSkinDefault != null) {
            mSkinDefault.setVisibility(View.VISIBLE);
        }
        SkinManager.getInstance().load(skin.getAbsolutePath(), new ILoaderListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess() {
                SkinSharedPreferences.getInstance().saveApplyingSkinName(item.path);
                Log.d(TAG, "load skin success");
            }

            @Override
            public void onFailed() {
                Log.e(TAG, "load skin failed");
            }
        });
    }

    private void checkPermissions() {
        String[] deniedPermissions = PermissionUtil.getDeniedPermissions(this, getPermissions());
        if(deniedPermissions != null && deniedPermissions.length > 0){
            PermissionUtil.requestPermissions(this, deniedPermissions, getPermissionsRequestCode());
        }else{
            requestPermissionsSuccess();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestPermissionsResult(requestCode, permissions, grantResults)){
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public boolean requestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == getPermissionsRequestCode()){
            boolean isAllGranted = true;//是否全部权限已授权
            for(int result : grantResults){
                if(result == PackageManager.PERMISSION_DENIED){
                    isAllGranted = false;
                    break;
                }
            }
            if(isAllGranted){
                //已全部授权
                requestPermissionsSuccess();
            }else{
                //权限有缺失
                requestPermissionsFailed();
            }
            return true;
        }
        return false;
    }

    @Override
    public int getPermissionsRequestCode() {
        return 99;
    }

    @Override
    public String[] getPermissions() {
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
    }

    @Override
    public void requestPermissionsSuccess() {
        init();
    }

    @Override
    public void requestPermissionsFailed() {
        Toast.makeText(this, "申请权限失败，请手动打开权限", Toast.LENGTH_LONG).show();
        if (mUiHandler == null) {
            mUiHandler = new Handler();
        }
        mUiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mUiHandler != null) {
            mUiHandler.removeCallbacksAndMessages(null);
            mUiHandler = null;
        }
    }
}
