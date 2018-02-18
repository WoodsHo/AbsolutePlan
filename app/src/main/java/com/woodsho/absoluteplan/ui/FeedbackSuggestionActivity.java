package com.woodsho.absoluteplan.ui;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.skinloader.SkinBaseActivity;
import com.woodsho.absoluteplan.skinloader.SkinManager;
import com.woodsho.absoluteplan.utils.StatusBarUtil;

public class FeedbackSuggestionActivity extends SkinBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_suggestion);
        setupActionBar();
        StatusBarUtil.setColor(this, SkinManager.getInstance().getColor(R.color.colorPrimary), 0);
        init();
    }

    private void setupActionBar() {
        ViewGroup rootView = (ViewGroup) findViewById(R.id.action_bar_root); //id from appcompat

        if (rootView != null) {
            View view = getLayoutInflater().inflate(R.layout.feedback_suggestion_toolbar_layout, rootView, false);
            rootView.addView(view, 0);

            Toolbar toolbar = (Toolbar) findViewById(R.id.feedback_suggestion_toolbar);
            setSupportActionBar(toolbar);
            ImageView back = (ImageView) findViewById(R.id.back_feedback_suggestion);
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
        Resources res = getResources();
        CardView qqCadView = (CardView) findViewById(R.id.qq_cardview);
        qqCadView.setBackground(res.getDrawable(R.drawable.item_feedback_suggestion_bg_selector));
        qqCadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText("1797856713");
                Toast.makeText(getApplicationContext(), "复制成功", Toast.LENGTH_SHORT).show();
            }
        });
        CardView emailCardView = (CardView) findViewById(R.id.email_cardview);
        emailCardView.setBackground(res.getDrawable(R.drawable.item_feedback_suggestion_bg_selector));
        emailCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("mailto:woodsho@163.com");
                Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(it);
            }
        });
    }
}
