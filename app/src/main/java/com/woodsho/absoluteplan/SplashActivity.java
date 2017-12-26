package com.woodsho.absoluteplan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.woodsho.absoluteplan.utils.StatusBarUtil;

/**
 * Created by hewuzhao on 17/12/9.
 */

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        ImageView imageView = (ImageView) findViewById(R.id.splash_view);
//        imageView.setImageResource(R.drawable.splash_view);
//        StatusBarUtil statusBar = new StatusBarUtil(this);
//        statusBar.setImmersionBar();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 850);
    }
}
