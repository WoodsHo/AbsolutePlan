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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.hubert.library.Controller;
import com.app.hubert.library.HighLight;
import com.app.hubert.library.NewbieGuide;
import com.app.hubert.library.OnGuideChangedListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.woodsho.absoluteplan.adapter.SideAdapter;
import com.woodsho.absoluteplan.bean.PlanTask;
import com.woodsho.absoluteplan.bean.SideItem;
import com.woodsho.absoluteplan.common.AbsPSharedPreference;
import com.woodsho.absoluteplan.common.WallpaperBgManager;
import com.woodsho.absoluteplan.data.CachePlanTaskStore;
import com.woodsho.absoluteplan.listener.IWallpaperBgUpdate;
import com.woodsho.absoluteplan.skinloader.SkinBaseActivity;
import com.woodsho.absoluteplan.skinloader.SkinManager;
import com.woodsho.absoluteplan.ui.AllFragment;
import com.woodsho.absoluteplan.ui.AvatarActivity;
import com.woodsho.absoluteplan.ui.CalendarFragment;
import com.woodsho.absoluteplan.ui.FinishedFragment;
import com.woodsho.absoluteplan.ui.PlanTaskDetailsActivity;
import com.woodsho.absoluteplan.ui.SettingsActivity;
import com.woodsho.absoluteplan.ui.TodayFragment;
import com.woodsho.absoluteplan.ui.TomorrowFragment;
import com.woodsho.absoluteplan.utils.CommonUtil;
import com.woodsho.absoluteplan.utils.StatusBarUtil;
import com.woodsho.absoluteplan.widget.CenteredImageSpan;
import com.woodsho.absoluteplan.widget.SimpleDraweeViewEx;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.woodsho.absoluteplan.ui.AvatarActivity.NAME_AVATAR;

public class MainActivity extends SkinBaseActivity implements SideAdapter.OnSideItemClickListener,
        CachePlanTaskStore.OnPlanTaskChangedListener, IWallpaperBgUpdate {
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
    public ImageView mSideNavigationView;

    public SimpleDraweeViewEx mAvatar;

    public TodayFragment mTodayFragment;
    public AllFragment mAllFragment;
    public TomorrowFragment mTomorrowFragment;
    public FinishedFragment mFinishedFragment;
    public CalendarFragment mCalendarFragment;
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

    public static final int REQUEST_CODE_AVATAR_ACTIVITY = 10;

    public static final int MSG_CLOSE_DRAWER = 0;

    public int mLastSelectedSideId;
    public List<SideItem> mSideItemList;

    private int mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay;

    public UIHandler mUIHandler;
    public FloatingActionButton mFloatActionButton;

    private RelativeLayout mSideRelativeLayout;
    private LinearLayout mSideBottomLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CachePlanTaskStore.getInstance().addOnPlanTaskChangedListener(this);

        View statusView = findViewById(R.id.view_status);
        ViewGroup.LayoutParams layoutParams = statusView.getLayoutParams();
        layoutParams.height = StatusBarUtil.getStatusBarHeight(this);
        statusView.setLayoutParams(layoutParams);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        StatusBarUtil.setColorNoTranslucentForDrawerLayout(MainActivity.this, mDrawerLayout,
                SkinManager.getInstance().getColor(R.color.colorPrimary));
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                showGuideBuild();
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
                if (mLastSelectedSideId == ID_CALENDAR) {
                    if (mCalendarFragment != null && mCalendarFragment.isAdded() && mCalendarFragment.isVisible()) {
                        mCalendarFragment.JumpToToday();
                    }
                }
            }
        });
        mFloatActionButton = (FloatingActionButton) findViewById(R.id.main_float_action_button);
        mFloatActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlanTaskDetailsActivity.class);
                intent.putExtra(PlanTaskDetailsActivity.KEY_SHOW_TYPE, PlanTaskDetailsActivity.TYPE_NEW_BUILD);
                if (mLastSelectedSideId == ID_TOMORROW) {
                    intent.putExtra(PlanTaskDetailsActivity.KEY_IS_TOMORROW, true);
                }
                startActivity(intent);
            }
        });
        mSideNavigationView = (ImageView) findViewById(R.id.toolbar_slide_menu);
        mSideNavigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(mSideLayout);
            }
        });

        mUIHandler = new UIHandler(this);
        getLastSelectedSideId();
        initSideView();
        changeMainView(mLastSelectedSideId, true);
        showGuideSide();
        WallpaperBgManager.getInstance().attach(this);
    }

    public void getLastSelectedSideId() {
        mLastSelectedSideId = AbsPSharedPreference.getInstanc().getLastSelectedSideId(ID_TODAY);
    }

    public void initSideView() {
        View view = getLayoutInflater().inflate(R.layout.side_layout, null);
        mAvatar = (SimpleDraweeViewEx) view.findViewById(R.id.avatar);
        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, AvatarActivity.class), REQUEST_CODE_AVATAR_ACTIVITY);
                mUIHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDrawerLayout.closeDrawer(mSideLayout);
                    }
                }, 200);
            }
        });
        File externalFilesDir = getExternalFilesDir(null);
        File imageFile = new File(externalFilesDir.getPath(), NAME_AVATAR);
        if (imageFile.exists()) {
            String uri = "file://" + imageFile.getPath();
            mAvatar.setImageURI(Uri.parse(uri));
        } else {
            mAvatar.setImageURI(getLocalUri(R.drawable.default_avatar));
        }

        mSideRelativeLayout = (RelativeLayout) view.findViewById(R.id.side_layout_relativelayout);
        Drawable wallpaperDrawable = CommonUtil.getWallpaperDrawable();
        if (wallpaperDrawable != null) {
            mSideRelativeLayout.setBackground(wallpaperDrawable);
        } else {
            mSideRelativeLayout.setBackgroundResource(R.drawable.common_bg);
        }
        mSideBottomLayout = (LinearLayout) view.findViewById(R.id.bottom_side_layout);
        mSideRecyclerView = (RecyclerView) view.findViewById(R.id.side_layout_recyclerview);
        mSideItemList = getAllSideItems();
        mSideAdapter = new SideAdapter(AbsolutePlanApplication.sAppContext, mSideItemList, mLastSelectedSideId);
        mSideAdapter.setOnSideItemClickListener(this);
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

        ViewTreeObserver vto = mSideRelativeLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 保证只调用一次
                mSideRelativeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // 组件生成cache（组件显示内容）
                mSideRelativeLayout.buildDrawingCache();
                // 得到组件显示内容
                Bitmap bitmap = CommonUtil.drawableToBitmap(mSideRelativeLayout.getBackground());
                // 局部模糊处理
                CommonUtil.blur(AbsolutePlanApplication.sAppContext, bitmap, mSideBottomLayout, 18);
            }
        });

        mSettingBt = (TextView) view.findViewById(R.id.setting_side_layout);
        mSettingBt.setText(createStringWithLeftPicture(R.drawable.ic_side_setting2, "  设置"));
        mSettingBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                mUIHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDrawerLayout.closeDrawer(mSideLayout);
                    }
                }, 200);
            }
        });
        mSideLayout.addView(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_AVATAR_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    if (mAvatar != null) {
                        File externalFilesDir = getExternalFilesDir(null);
                        final File imageFile = new File(externalFilesDir.getPath(), NAME_AVATAR);
                        if (imageFile.exists()) {
                            Uri url = Uri.parse("file://" + imageFile.getPath());
                            Fresco.getImagePipeline().evictFromMemoryCache(url);
                            Fresco.getImagePipeline().evictFromDiskCache(url);
                            Fresco.getImagePipeline().evictFromCache(url);
                            mAvatar.setImageURI(url);
                        }
                    }
                }
                break;
        }
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
        CachePlanTaskStore planTaskStore = CachePlanTaskStore.getInstance();
        if (planTaskStore.isPlanTaskInitializedFinished()) {
            allCount = planTaskStore.getCachePlanTaskList().size();
            if (allCount > 0) {
                todayCount = CommonUtil.getTodayPlanTaskList().size();
                tomorrowCount = CommonUtil.getTomorrowPlanTaskList().size();
                finishedCount = CommonUtil.getFinishedPlanTaskList().size();
            }
        }

        sideItemList.add(new SideItem(ID_TODAY, R.drawable.ic_side_today, "今天", todayCount));
        sideItemList.add(new SideItem(ID_TOMORROW, R.drawable.ic_side_tomorrow, "明天", tomorrowCount));
        sideItemList.add(new SideItem(ID_CALENDAR, R.drawable.ic_side_calendar, "日历", 0));
        sideItemList.add(new SideItem(ID_ALL, R.drawable.ic_side_all, "所有", allCount));
        sideItemList.add(new SideItem(ID_FINISHED, R.drawable.ic_side_finished, "已完成", finishedCount));

        return sideItemList;
    }

    public void changeMainView(int id, boolean init) {
        String fragmentTag = getFragmentTagBySideId(mLastSelectedSideId);
        if (!init && (id == mLastSelectedSideId || TextUtils.isEmpty(fragmentTag))) {
            mDrawerLayout.closeDrawer(mSideLayout);
            return;
        }
        SideItem sideItem = null;
        for (int i = 0; i < mSideItemList.size(); i++) {
            if (mSideItemList.get(i).id == id) {
                sideItem = mSideItemList.get(i);
                break;
            }
        }
        if (sideItem == null) {
            Log.e(TAG, "error, id: " + id + ", can not find");
            return;
        }

        mLastSelectedSideId = id;
        if (id == ID_CALENDAR) {
            mToolbarTitle.setText(mCurrentSelectMonth + "月");
            mToolbarSubTitleDay.setText(mCurrentSelectDay + "日");
            mToolbarSubTitleYear.setVisibility(View.VISIBLE);
            mToolbarSubTitleDay.setVisibility(View.VISIBLE);
            if (CommonUtil.isToday(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay)) {
                mToolbarToToday.setVisibility(View.GONE);
            } else {
                mToolbarToToday.setVisibility(View.VISIBLE);
            }
        } else {
            mToolbarTitle.setText(sideItem.title);
            mToolbarSubTitleYear.setVisibility(View.GONE);
            mToolbarSubTitleDay.setVisibility(View.GONE);
            mToolbarToToday.setVisibility(View.GONE);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);
        if (fragment != null) {
            fragmentTransaction.hide(fragment);
        }

        switch (id) {
            case ID_ALL:
                Fragment allFragment = fragmentManager.findFragmentByTag(TAG_ALL_FRAGMENT);
                if (allFragment == null) {
                    if (mAllFragment == null) {
                        mAllFragment = new AllFragment();
                    }
                    allFragment = mAllFragment;
                    fragmentTransaction.add(R.id.content_frame_layout, mAllFragment, TAG_ALL_FRAGMENT);
                }

                fragmentTransaction.show(allFragment);
                fragmentTransaction.commitAllowingStateLoss();
                break;
            case ID_TODAY:
                Fragment todayFragment = fragmentManager.findFragmentByTag(TAG_TODAY_FRAGMENT);
                if (todayFragment == null) {
                    if (mTodayFragment == null) {
                        mTodayFragment = new TodayFragment();
                    }
                    todayFragment = mTodayFragment;
                    fragmentTransaction.add(R.id.content_frame_layout, mTodayFragment, TAG_TODAY_FRAGMENT);
                }

                fragmentTransaction.show(todayFragment);
                fragmentTransaction.commitAllowingStateLoss();
                break;
            case ID_TOMORROW:
                Fragment tomorrowFragment = fragmentManager.findFragmentByTag(TAG_TOMORROW_FRAGMENT);
                if (tomorrowFragment == null) {
                    if (mTomorrowFragment == null) {
                        mTomorrowFragment = new TomorrowFragment();
                    }
                    tomorrowFragment = mTomorrowFragment;
                    fragmentTransaction.add(R.id.content_frame_layout, mTomorrowFragment, TAG_TOMORROW_FRAGMENT);
                }

                fragmentTransaction.show(tomorrowFragment);
                fragmentTransaction.commitAllowingStateLoss();
                break;
            case ID_CALENDAR:
                Fragment calendarFragment = fragmentManager.findFragmentByTag(TAG_CALENDAR_FRAGMENT);
                if (calendarFragment == null) {
                    if (mCalendarFragment == null) {
                        mCalendarFragment = new CalendarFragment();
                    }
                    calendarFragment = mCalendarFragment;
                    fragmentTransaction.add(R.id.content_frame_layout, mCalendarFragment, TAG_CALENDAR_FRAGMENT);
                }

                fragmentTransaction.show(calendarFragment);
                fragmentTransaction.commitAllowingStateLoss();
                break;
            case ID_FINISHED:
                Fragment finishedFragment = fragmentManager.findFragmentByTag(TAG_FINISHED_FRAGMENT);
                if (finishedFragment == null) {
                    if (mFinishedFragment == null) {
                        mFinishedFragment = new FinishedFragment();
                    }
                    finishedFragment = mFinishedFragment;
                    fragmentTransaction.add(R.id.content_frame_layout, mFinishedFragment, TAG_FINISHED_FRAGMENT);
                }

                fragmentTransaction.show(finishedFragment);
                fragmentTransaction.commitAllowingStateLoss();
                break;
        }
        mUIHandler.removeMessages(MSG_CLOSE_DRAWER);
        mUIHandler.sendEmptyMessageDelayed(MSG_CLOSE_DRAWER, 100);
    }

    private String getFragmentTagBySideId(int id) {
        switch (id) {
            case ID_ALL:
                return TAG_ALL_FRAGMENT;
            case ID_TODAY:
                return TAG_TODAY_FRAGMENT;
            case ID_TOMORROW:
                return TAG_TOMORROW_FRAGMENT;
            case ID_CALENDAR:
                return TAG_CALENDAR_FRAGMENT;
            case ID_FINISHED:
                return TAG_FINISHED_FRAGMENT;
        }
        return null;
    }

    protected Uri getLocalUri(int resId) {
        StringBuilder strBuilder = new StringBuilder("res://");
        strBuilder.append(AbsolutePlanApplication.sAppContext.getPackageName());
        strBuilder.append("/");
        strBuilder.append(resId);
        return Uri.parse(strBuilder.toString());
    }

    @Override
    public void onWallpaperBgUpdate() {
        if (mSideRelativeLayout == null || mSideBottomLayout == null)
            return;

        Log.d(TAG, "main activity, mSidelayou and side bottom layout is not null");
        Drawable wallpaperDrawable = CommonUtil.getWallpaperDrawable();
        if (wallpaperDrawable != null) {
            mSideRelativeLayout.setBackground(wallpaperDrawable);
        } else {
            mSideRelativeLayout.setBackgroundResource(R.drawable.common_bg);
        }
        ViewTreeObserver vto = mSideRelativeLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 保证只调用一次
                mSideRelativeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // 组件生成cache（组件显示内容）
                mSideRelativeLayout.buildDrawingCache();
                // 得到组件显示内容
                Bitmap bitmap = CommonUtil.drawableToBitmap(mSideRelativeLayout.getBackground());
                // 局部模糊处理
                CommonUtil.blur(AbsolutePlanApplication.sAppContext, bitmap, mSideBottomLayout, 18);
            }
        });
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
    protected void onPause() {
        super.onPause();
        AbsPSharedPreference.getInstanc().saveLastSelectedSideId(mLastSelectedSideId);
    }

    @Override
    public void onSideItemClick(SideItem sideItem) {
        changeMainView(sideItem.id, false);
    }

    @Override
    public void onPlanTaskChanged() {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                CachePlanTaskStore planTaskStore = CachePlanTaskStore.getInstance();
                List<PlanTask> allTask = planTaskStore.getCachePlanTaskList();
                updateSideItemOfAll(allTask.size());

                updateSideItemOfToday(CommonUtil.getTodayPlanTaskList().size());
                updateSideItemOfTomorrow(CommonUtil.getTomorrowPlanTaskList().size());
                updateSideItemOfFinished(CommonUtil.getFinishedPlanTaskList().size());
            }
        });
    }

    public void updateSideItemOfAll(int count) {
        if (mSideItemList == null || mSideAdapter == null)
            return;

        SideItem item = mSideItemList.get(ID_ALL);
        if (count == item.count)
            return;

        item.count = count;
        mSideItemList.set(ID_ALL, item);
        mSideAdapter.notifyItemChanged(ID_ALL, "pos: " + ID_ALL);
    }

    public void updateSideItemOfToday(int count) {
        if (mSideItemList == null || mSideAdapter == null)
            return;

        SideItem item = mSideItemList.get(ID_TODAY);
        if (count == item.count)
            return;

        item.count = count;
        mSideItemList.set(ID_TODAY, item);
        mSideAdapter.notifyItemChanged(ID_TODAY, "pos: " + ID_TODAY);
    }

    public void updateSideItemOfTomorrow(int count) {
        if (mSideItemList == null || mSideAdapter == null)
            return;

        SideItem item = mSideItemList.get(ID_TOMORROW);
        if (count == item.count)
            return;

        item.count = count;
        mSideItemList.set(ID_TOMORROW, item);
        mSideAdapter.notifyItemChanged(ID_TOMORROW, "pos: " + ID_TOMORROW);
    }

    public void updateSideItemOfFinished(int count) {
        if (mSideItemList == null || mSideAdapter == null)
            return;

        SideItem item = mSideItemList.get(ID_FINISHED);
        if (count == item.count)
            return;

        item.count = count;
        mSideItemList.set(ID_FINISHED, item);
        mSideAdapter.notifyItemChanged(ID_FINISHED, "pos: " + ID_FINISHED);
    }

    public void updateToolbarDate(int year, int month, int day) {
        mToolbarTitle.setVisibility(View.VISIBLE);
        mToolbarToToday.setText(String.valueOf(CommonUtil.getToday()));
        if (CommonUtil.isToday(year, month, day)) {
            mToolbarToToday.setVisibility(View.GONE);
        } else {
            mToolbarToToday.setVisibility(View.VISIBLE);
        }
        mToolbarSubTitleYear.setVisibility(View.VISIBLE);
        mToolbarSubTitleDay.setVisibility(View.VISIBLE);
        mToolbarSubTitleDay.setText(day + "日");
        mToolbarTitle.setText(month + "月");
        mToolbarSubTitleYear.setText(year + "年");
        setCurrentSelectDate(year, month, day);
    }

    private void setCurrentSelectDate(int year, int month, int day) {
        mCurrentSelectYear = year;
        mCurrentSelectMonth = month;
        mCurrentSelectDay = day;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSideAdapter != null) {
            mSideAdapter.removeOnSideItemClickListener();
        }
        if (mUIHandler != null) {
            mUIHandler.removeCallbacksAndMessages(null);
            mUIHandler = null;
        }
        CachePlanTaskStore.getInstance().removePlanTaskChangedListener(this);
        WallpaperBgManager.getInstance().detach(this);
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

    private void showGuideBuild() {
        Controller controller = NewbieGuide.with(this)
                .setOnGuideChangedListener(new OnGuideChangedListener() {
                    @Override
                    public void onShowed(Controller controller) {
                        //when guide layer display
                    }

                    @Override
                    public void onRemoved(Controller controller) {
                        Intent intent = new Intent(MainActivity.this, PlanTaskDetailsActivity.class);
                        intent.putExtra(PlanTaskDetailsActivity.KEY_SHOW_TYPE, PlanTaskDetailsActivity.TYPE_NEW_BUILD);
                        startActivity(intent);
                    }
                })
                .setBackgroundColor(SkinManager.getInstance().getColor(R.color.guide_bg_color))
                .setEveryWhereCancelable(true)
                .setLayoutRes(R.layout.guide_build_view_layout)
                .alwaysShow(false)
                .addHighLight(mFloatActionButton, HighLight.Type.CIRCLE)
                .setLabel(KEY_GUIDE_BUILD)
                .build();
        controller.show();
    }

    private void showGuideSide() {
        Controller controller = NewbieGuide.with(this)
                .setOnGuideChangedListener(new OnGuideChangedListener() {
                    @Override
                    public void onShowed(Controller controller) {
                        //when guide layer display
                    }

                    @Override
                    public void onRemoved(Controller controller) {
                        mDrawerLayout.openDrawer(mSideLayout);
                    }
                })
                .setBackgroundColor(SkinManager.getInstance().getColor(R.color.guide_bg_color))
                .setEveryWhereCancelable(true)
                .setLayoutRes(R.layout.guide_side_view_layout)
                .alwaysShow(false)
                .addHighLight(mSideNavigationView, HighLight.Type.CIRCLE)
                .setLabel(KEY_GUIDE_SIDE)
                .build();
        controller.show();
    }

    @Override
    public void onSkinUpdate() {
        super.onSkinUpdate();
        if (mDrawerLayout != null) {
            StatusBarUtil.setColorNoTranslucentForDrawerLayout(MainActivity.this, mDrawerLayout,
                    SkinManager.getInstance().getColor(R.color.colorPrimary));
        }
        if (mSideAdapter != null) {
            mSideAdapter.updateSkin();
        }
    }
}
