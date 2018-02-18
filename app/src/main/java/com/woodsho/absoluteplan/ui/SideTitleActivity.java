package com.woodsho.absoluteplan.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.common.AbsPSharedPreference;
import com.woodsho.absoluteplan.skinloader.SkinBaseActivity;
import com.woodsho.absoluteplan.skinloader.SkinManager;
import com.woodsho.absoluteplan.utils.StatusBarUtil;

/**
 * Created by hewuzhao on 18/2/17.
 */

public class SideTitleActivity extends SkinBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sidetitle);
        setupActionBar();
        StatusBarUtil.setColor(this, SkinManager.getInstance().getColor(R.color.colorPrimary), 0);
        init();
    }

    private void setupActionBar() {
        ViewGroup rootView = (ViewGroup) findViewById(R.id.action_bar_root); //id from appcompat

        if (rootView != null) {
            View view = getLayoutInflater().inflate(R.layout.sidetitle_toolbar_layout, rootView, false);
            rootView.addView(view, 0);

            Toolbar toolbar = (Toolbar) findViewById(R.id.sidetitle_toolbar);
            toolbar.setBackgroundColor(SkinManager.getInstance().getColor(R.color.colorPrimary));
            setSupportActionBar(toolbar);
            ImageView back = (ImageView) findViewById(R.id.back_sidetitle);
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
        final EditText editText = (EditText) findViewById(R.id.sidetitle_edit);
        String str = AbsPSharedPreference.getInstanc().getSideTitle();
        editText.setText(str);
        editText.setSelection(str.length());
        Button confirm = (Button) findViewById(R.id.sidetitle_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editText.getText().toString();
                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(getApplicationContext(), "修改不能为空，请填写", Toast.LENGTH_SHORT).show();
                    return;
                }
                AbsPSharedPreference.getInstanc().saveSideTitle(title);
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
