package com.woodsho.absoluteplan;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.woodsho.absoluteplan.utils.StatusBarUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil statusbar = new StatusBarUtil(this);
        statusbar.setColorBarForDrawer(ContextCompat.getColor(this, R.color.colorPrimary));
    }
}
