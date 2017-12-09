package com.woodsho.absoluteplan;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.woodsho.absoluteplan.adapter.SideAdapter;
import com.woodsho.absoluteplan.bean.SideItem;
import com.woodsho.absoluteplan.utils.CommonUtil;
import com.woodsho.absoluteplan.utils.StatusBarUtil;
import com.woodsho.absoluteplan.widget.CenteredImageSpan;
import com.woodsho.absoluteplan.widget.SideNavigationView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    public DrawerLayout mDrawerLayout;
    public FrameLayout mContentLayout;
    public FrameLayout mSideLayout;
    public TextView mToolbarTitle;
    public TextView mToolbarSubTitleYear;
    public TextView mToolbarSubTitleDay;
    public TextView mToolbarToToday;

    public RecyclerView mSideRecyclerView;
    public TextView mSettingBt;
    public TextView mSearchBt;
    public SideNavigationView mSideNavigationView;

    public SideAdapter mSideAdapter;

    public static final int ID_TODAY = 0;
    public static final int ID_TOMORROW = 1;
    public static final int ID_CALENDAR = 2;
    public static final int ID_ALL = 3;
    public static final int ID_FINISHED = 4;

    public static final String TAG_CALENDAR_FRAGMENT = "tag_calendar_fragment";
    public static final String TAG_TODAY_FRAGMENT = "tag_today_fragment";
    public static final String TAG_ALL_FRAGMENT = "tag_all_fragment";
    public static final String TAG_TOMORROW_FRAGMENT = "tag_tomorrow_fragment";
    public static final String TAG_FINISHED_FRAGMENT = "tag_finished_fragment";

    public static final String KEY_GUIDE_BUILD = "guide_build";
    public static final String KEY_GUIDE_SIDE = "guide_side";

    public static final int MSG_CLOSE_DRAWER = 0;

    public int mLastSelectedSideId;
    public List<SideItem> mSideItemList;

    private int mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay;

    public UIHandler mUIHandler;
    public FloatingActionButton mFloatActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil statusbar = new StatusBarUtil(this);
        statusbar.setColorBarForDrawer(ContextCompat.getColor(this, R.color.colorPrimary));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                //showGuideBuild();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        mContentLayout = (FrameLayout) findViewById(R.id.content_frame_layout);
        mSideLayout = (FrameLayout) findViewById(R.id.side_frame_layout);
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        mToolbarSubTitleYear = (TextView) findViewById(R.id.toolbar_sub_title_year);
        mToolbarSubTitleDay = (TextView) findViewById(R.id.toolbar_sub_title_day);
        mToolbarToToday = (TextView) findViewById(R.id.toolbar_to_today);
        mToolbarToToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mLastSelectedSideId == ID_CALENDAR) {
//                    if (mCalendarFragment != null && mCalendarFragment.isAdded() && mCalendarFragment.isVisible()) {
//                        mCalendarFragment.JumpToToday();
//                    }
//                }
            }
        });
        mFloatActionButton = (FloatingActionButton) findViewById(R.id.main_float_action_button);
        mFloatActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, ScheduleTaskDetailsActivity.class);
//                intent.putExtra(ScheduleTaskDetailsActivity.KEY_SHOW_TYPE, ScheduleTaskDetailsActivity.TYPE_NEW_BUILD);
//                startActivity(intent);
            }
        });
        mSideNavigationView = (SideNavigationView) findViewById(R.id.toolbar_slide_navigation_view);
        mSideNavigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(mSideLayout);
            }
        });

        mUIHandler = new UIHandler(this);
        initSideView();
    }

    public void initSideView() {
        View view = View.inflate(this, R.layout.side_layout, null);
        TextView name = (TextView) view.findViewById(R.id.name);
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AbsolutePlanApplication.sAppContext, "开发中，敬请期待！", Toast.LENGTH_SHORT).show();
            }
        });
        final RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.side_layout_relativelayout);
        relativeLayout.setBackgroundResource(R.drawable.side_bg);
        final LinearLayout bottomLayout = (LinearLayout) view.findViewById(R.id.bottom_side_layout);
        mSideRecyclerView = (RecyclerView) view.findViewById(R.id.side_layout_recyclerview);
        mSideItemList = getAllSideItems();
        mSideAdapter = new SideAdapter(AbsolutePlanApplication.sAppContext, mSideItemList, mLastSelectedSideId);
//        mSideAdapter.setOnSideItemClickListener(this);
        mSideRecyclerView.setAdapter(mSideAdapter);
        mSideRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mSideRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int pos = parent.getChildAdapterPosition(view);
                //if (pos == 3) {
                outRect.bottom = 20;
                //}
            }
        });

        ViewTreeObserver vto = relativeLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 保证只调用一次
                relativeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // 组件生成cache（组件显示内容）
                relativeLayout.buildDrawingCache();
                // 得到组件显示内容
                Bitmap bitmap = CommonUtil.drawableToBitmap(relativeLayout.getBackground());
                // 局部模糊处理
                CommonUtil.blur(getApplicationContext(), bitmap, bottomLayout, 18);
            }
        });

        mSettingBt = (TextView) view.findViewById(R.id.setting_side_layout);
        mSettingBt.setText(createStringWithLeftPicture(R.drawable.ic_side_settings, "  设置"));
        mSettingBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
//                startActivity(intent);
//                mUIHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mDrawerLayout.closeDrawer(mSideLayout);
//                    }
//                }, 200);
            }
        });
        mSearchBt = (TextView) view.findViewById(R.id.search_side_layout);
        mSearchBt.setText(createStringWithLeftPicture(R.drawable.ic_side_search, "  搜索"));
        mSearchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AbsolutePlanApplication.sAppContext, "开发中，敬请期待！", Toast.LENGTH_SHORT).show();
            }
        });
        mSideLayout.addView(view);
    }

    public SpannableString createStringWithLeftPicture(int drawableId, String str) {
        Resources res = getResources();
        String replacedStr = "image";
        final SpannableString spannableString = new SpannableString(replacedStr + str);
        Drawable drawable = res.getDrawable(drawableId);
        drawable.setBounds(0, 0, 50, 50);
        CenteredImageSpan span = new CenteredImageSpan(drawable);
        spannableString.setSpan(span, 0, replacedStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private List<SideItem> getAllSideItems() {
        List<SideItem> sideItemList = new ArrayList<>();
        int allCount = 0;
        int tomorrowCount = 0;
        int todayCount = 0;
        int finishedCount = 0;
//        CacheScheduleTaskStore scheduleTaskStore = CacheScheduleTaskStore.getInstance();
//        if (scheduleTaskStore.isScheduleTaskInitializedFinished()) {
//            allCount = scheduleTaskStore.getCacheScheduleTaskList().size();
//            if (allCount > 0) {
//                todayCount = CommonUtil.getTodayScheduleTaskList().size();
//                tomorrowCount = CommonUtil.getTomorrowScheduleTaskList().size();
//                finishedCount = CommonUtil.getFinishedScheduleTaskList().size();
//            }
//        }

        sideItemList.add(new SideItem(ID_TODAY, String.valueOf(getLocalUri(R.drawable.ic_side_today)), "今天", todayCount));
        sideItemList.add(new SideItem(ID_TOMORROW, String.valueOf(getLocalUri(R.drawable.ic_side_tomorrow)), "明天", tomorrowCount));
        sideItemList.add(new SideItem(ID_CALENDAR, String.valueOf(getLocalUri(R.drawable.ic_side_calendar)), "日历", 0));
        sideItemList.add(new SideItem(ID_ALL, String.valueOf(getLocalUri(R.drawable.ic_side_all)), "所有", allCount));
        sideItemList.add(new SideItem(ID_FINISHED, String.valueOf(getLocalUri(R.drawable.ic_side_finished)), "已完成", finishedCount));

        return sideItemList;
    }

    protected Uri getLocalUri(int resId) {
        StringBuilder strBuilder = new StringBuilder("res://");
        strBuilder.append(AbsolutePlanApplication.sAppContext.getPackageName());
        strBuilder.append("/");
        strBuilder.append(resId);
        return Uri.parse(strBuilder.toString());
    }

    private static class UIHandler extends Handler {
        private WeakReference<MainActivity> mWRef;

        public UIHandler(MainActivity activity) {
            mWRef = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mainActivity = mWRef.get();
            if (mainActivity == null) {
                return;
            }
            switch (msg.arg1) {
                case MSG_CLOSE_DRAWER:
                    if (mainActivity.mDrawerLayout != null) {
                        mainActivity.mDrawerLayout.closeDrawer(mainActivity.mSideLayout);
                    }
                    break;
            }

            super.handleMessage(msg);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mSideLayout)) {
            mDrawerLayout.closeDrawer(mSideLayout);
            return;
        }

        //只显示一次启动页（ App 没被 kill 的情况下）
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
