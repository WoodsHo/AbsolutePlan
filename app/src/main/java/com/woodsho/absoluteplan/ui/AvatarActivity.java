package com.woodsho.absoluteplan.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.listener.PermissionListener;
import com.woodsho.absoluteplan.skinloader.SkinManager;
import com.woodsho.absoluteplan.utils.CommonUtil;
import com.woodsho.absoluteplan.utils.PermissionUtil;
import com.woodsho.absoluteplan.utils.StatusBarUtil;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class AvatarActivity extends AppCompatActivity implements PermissionListener {
    public static final String TAG = "AvatarActivity";

    private static final int REQUEST_SELECT_PICTURE = 0x01;
    private static final String CROPPED_IMAGE_NAME = "CropImage";

    public static final String NAME_AVATAR = "avatar.png";

    public ImageView mAvatarImageView;

    public Handler mUiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);
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
        init();
    }

    private void setupActionBar() {
        ViewGroup rootView = (ViewGroup) findViewById(R.id.action_bar_root); //id from appcompat

        if (rootView != null) {
            View view = getLayoutInflater().inflate(R.layout.avatar_toolbar_layout, rootView, false);
            rootView.addView(view, 0);

            Toolbar toolbar = (Toolbar) findViewById(R.id.avatar_toolbar);
            toolbar.setBackgroundColor(SkinManager.getInstance().getColor(R.color.colorPrimary));
            setSupportActionBar(toolbar);
            ImageView back = (ImageView) findViewById(R.id.back_avatar);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
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
        Button avatarChooseBt = (Button) findViewById(R.id.avatar_choose_button);
        GradientDrawable bgDrawable = new GradientDrawable();
        bgDrawable.setCornerRadius(CommonUtil.dp2px(this, 2));
        bgDrawable.setColor(SkinManager.getInstance().getColor(R.color.colorPrimary));
        avatarChooseBt.setBackground(bgDrawable);
        avatarChooseBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
            }
        });
        mAvatarImageView = (ImageView) findViewById(R.id.avatar);
        File externalFilesDir = getExternalFilesDir(null);
        File imageFile = new File(externalFilesDir.getPath(), NAME_AVATAR);
        if (imageFile.exists()) {
            String uri = imageFile.getPath();
            mAvatarImageView.setImageURI(Uri.parse(uri));
        }
        mUiHandler = new Handler();
    }

    private void checkPermissions() {
        String[] deniedPermissions = PermissionUtil.getDeniedPermissions(this, getPermissions());
        if (deniedPermissions != null && deniedPermissions.length > 0) {
            PermissionUtil.requestPermissions(this, deniedPermissions, getPermissionsRequestCode());
        } else {
            requestPermissionsSuccess();
        }
    }

    private void pickFromGalleryWithPermissons() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "请选择图片"), REQUEST_SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SELECT_PICTURE) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startCropActivity(data.getData());
                } else {
                    Toast.makeText(AvatarActivity.this, "无法取回选择的图片，请重试", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data);
        }
    }

    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = CROPPED_IMAGE_NAME + ".png";
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));

        uCrop = basisConfig(uCrop);

        uCrop.start(AvatarActivity.this);
    }

    private UCrop basisConfig(UCrop uCrop) {
        uCrop = uCrop.withAspectRatio(1, 1); //正方形比例
        uCrop = uCrop.withMaxResultSize(1920, 1080); //限制最大size

        UCrop.Options options = new UCrop.Options();
        int colorPrimary = SkinManager.getInstance().getColor(R.color.colorPrimary);
        options.setActiveWidgetColor(colorPrimary);
        options.setStatusBarColor(colorPrimary);
        options.setToolbarColor(colorPrimary);
        uCrop.withOptions(options);
        return uCrop;
    }

    private void handleCropResult(@NonNull Intent result) {
        final Uri imageUri = UCrop.getOutput(result);
        Log.d(TAG, "imageUri: " + imageUri);
        if (imageUri != null && imageUri.getScheme().equals("file")) {
            FileInputStream inStream = null;
            FileOutputStream outStream = null;
            boolean success = false;
            try {
                File externalFilesDir = getExternalFilesDir(null);
                File saveFile = new File(externalFilesDir.getPath(), NAME_AVATAR);
                inStream = new FileInputStream(new File(imageUri.getPath()));
                outStream = new FileOutputStream(saveFile);
                FileChannel inChannel = inStream.getChannel();
                FileChannel outChannel = outStream.getChannel();
                inChannel.transferTo(0, inChannel.size(), outChannel);
                success = true;
            } catch (Exception e) {
                Toast.makeText(AvatarActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, imageUri.toString());
                success = false;
            } finally {
                CommonUtil.closeSafely(inStream);
                CommonUtil.closeSafely(outStream);
                if (success) {
                    File externalFilesDir = getExternalFilesDir(null);
                    File saveFile = new File(externalFilesDir.getPath(), NAME_AVATAR);
                    if (saveFile.exists() && mAvatarImageView != null) {
                        mAvatarImageView.setImageURI(null);
                        mAvatarImageView.setImageURI(Uri.parse(saveFile.getPath()));
                        setResult(RESULT_OK);
                    }
                }
            }
        } else {
            Toast.makeText(AvatarActivity.this, "无法取回裁剪图片", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e(TAG, "handleCropError: ", cropError);
            Toast.makeText(AvatarActivity.this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(AvatarActivity.this, "意外错误", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestPermissionsResult(requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public boolean requestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == getPermissionsRequestCode()) {
            boolean isAllGranted = true;//是否全部权限已授权
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                //已全部授权
                requestPermissionsSuccess();
            } else {
                //权限有缺失
                requestPermissionsFailed();
            }
            return true;
        }
        return false;
    }

    @Override
    public int getPermissionsRequestCode() {
        return 98;
    }

    @Override
    public String[] getPermissions() {
        return new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
    }

    @Override
    public void requestPermissionsSuccess() {
        pickFromGalleryWithPermissons();
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
    protected void onDestroy() {
        super.onDestroy();
        if (mUiHandler != null) {
            mUiHandler.removeCallbacksAndMessages(null);
            mUiHandler = null;
        }
    }
}
