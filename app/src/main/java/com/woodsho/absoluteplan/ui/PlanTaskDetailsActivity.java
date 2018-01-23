package com.woodsho.absoluteplan.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.app.hubert.library.Controller;
import com.app.hubert.library.HighLight;
import com.app.hubert.library.NewbieGuide;
import com.app.hubert.library.OnGuideChangedListener;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.bean.PlanTask;
import com.woodsho.absoluteplan.data.CachePlanTaskStore;
import com.woodsho.absoluteplan.service.UserActionService;
import com.woodsho.absoluteplan.skinloader.SkinBaseActivity;
import com.woodsho.absoluteplan.skinloader.SkinManager;
import com.woodsho.absoluteplan.utils.CommonUtil;
import com.woodsho.absoluteplan.utils.StatusBarUtil;
import com.woodsho.absoluteplan.widget.CenteredImageSpan;
import com.woodsho.absoluteplan.widget.FloatingActionMenu;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hewuzhao on 17/12/14.
 */

public class PlanTaskDetailsActivity extends SkinBaseActivity {
    public static final String TAG = "PlanTaskDetailsActivity";
    public static final String KEY_PLANTASK = "key_plantask";
    public static final String KEY_GUIDE_TIME = "guide_time";
    public static final String KEY_GUIDE_SAVE_PLANTASK = "guide_save_plantask";
    public static final String KEY_SHOW_TYPE = "show_type";

    public static final int TYPE_NEW_BUILD = 1;
    public static final int TYPE_MODIFY = 2;

    public Handler mHandler;
    public Runnable mHideKeyboardRunnable, mShowKeyboardRunnable;
    public TextView mToolbarTitle;
    public EditText mTitle;
    public EditText mDescribe;
    public ImageView mBack;
    public TextView mToolbarDate;
    public TextView mToolbarTime;
    private PlanTask mIntentPlanTask;
    private int mShowType;

    private Calendar mCalendar = Calendar.getInstance(Locale.CHINA);

    private FloatingActionMenu mFloatingActionMenu;
    private FloatingActionButton mMainFab;
    private FloatingActionButton mSaveFab;
    private FloatingActionButton mSaveAndExitFab;
    private FloatingActionButton mExitFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantaskdetails);
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
        showGuideTime();
    }

    private void setupActionBar() {
        ViewGroup rootView = (ViewGroup) findViewById(R.id.action_bar_root); //id from appcompat

        if (rootView != null) {
            View view = getLayoutInflater().inflate(R.layout.plantaskdetails_toolbar_layout, rootView, false);
            rootView.addView(view, 0);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void showGuideTime() {
        Controller controller = NewbieGuide.with(this)
                .setOnGuideChangedListener(new OnGuideChangedListener() {//add listener
                    @Override
                    public void onShowed(Controller controller) {
                        //引导层显示
                    }

                    @Override
                    public void onRemoved(Controller controller) {
                        mToolbarDate.callOnClick();
                    }
                })
                .setBackgroundColor(getResources().getColor(R.color.guide_bg_color))//设置引导层背景色，建议有透明度，默认背景色为：0xb2000000
                .setEveryWhereCancelable(true)//设置点击任何区域消失，默认为true
                .setLayoutRes(R.layout.guide_time_view_layout)//自定义的提示layout,第二个可变参数为点击隐藏引导层view的id
                .alwaysShow(false)//是否每次都显示引导层，默认false
                .addHighLight(mToolbarDate)
                .addHighLight(mToolbarTime)
                .setLabel(KEY_GUIDE_TIME)
                .build();//构建引导层的控制器
        //controller.resetLabel(KEY_GUIDE_TIME);//重置该引导层为未显示过
        //controller.remove();//移除引导层
        controller.show();//显示引导层
    }

    private void showGuideSave() {
        Controller controller = NewbieGuide.with(this)
                .setOnGuideChangedListener(new OnGuideChangedListener() {
                    @Override
                    public void onShowed(Controller controller) {
                        //when guide layer display
                    }

                    @Override
                    public void onRemoved(Controller controller) {
                        //when guide layer dismiss
                    }
                })
                .setBackgroundColor(getResources().getColor(R.color.guide_bg_color))
                .setEveryWhereCancelable(true)
                .setLayoutRes(R.layout.guide_save_plantask_view_layout)
                .alwaysShow(false)
                .addHighLight(mMainFab, HighLight.Type.CIRCLE)
                .setLabel(KEY_GUIDE_SAVE_PLANTASK)
                .build();
        controller.show();
    }

    private void init() {
        final Resources res = getResources();
        mHandler = new Handler();
        mToolbarTitle = (TextView) findViewById(R.id.head_plantaskdetails);
        mTitle = (EditText) findViewById(R.id.title_plantaskdetails);
        mTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mShowType != TYPE_MODIFY) {
                    mTitle.setFocusable(true);
                    mTitle.setFocusableInTouchMode(true);
                    return false;
                }
                return true;
            }
        });
        mDescribe = (EditText) findViewById(R.id.describe_plantaskdetails);
        mDescribe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mShowType != TYPE_MODIFY) {
                    mDescribe.setFocusable(true);
                    mDescribe.setFocusableInTouchMode(true);
                    return false;
                }
                return true;
            }
        });
        mBack = (ImageView) findViewById(R.id.back_plantaskdetails);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mToolbarDate = (TextView) findViewById(R.id.date_plantaskdetails);
        mToolbarTime = (TextView) findViewById(R.id.time_plantaskdetails);

        mToolbarDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(PlanTaskDetailsActivity.this, R.style.MyDatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, month);
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        setYearMonthDay();
                        showGuideSave();
                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        showGuideSave();
                    }
                });
                datePickerDialog.show();
            }
        });
        mToolbarTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(PlanTaskDetailsActivity.this, R.style.MyDatePickerDialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        mCalendar.set(Calendar.MINUTE, minute);
                        setHourMinute();
                    }
                }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });

        Intent intent = getIntent();
        mShowType = intent.getIntExtra(KEY_SHOW_TYPE, TYPE_NEW_BUILD);
        mFloatingActionMenu = (FloatingActionMenu) findViewById(R.id.float_action_menu);
        mMainFab = (FloatingActionButton) findViewById(R.id.main_float_action_menu);
        mMainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShowType == TYPE_MODIFY) {
                    mShowType = TYPE_NEW_BUILD;
                    mToolbarTitle.setText("修改计划");
                    mMainFab.setImageDrawable(res.getDrawable(R.drawable.ic_fab_menu_normal));
                    mTitle.setFocusable(true);
                    mDescribe.setFocusable(true);
                    mToolbarDate.setClickable(true);
                    mToolbarTime.setClickable(true);
                } else {
                    mMainFab.setImageDrawable(res.getDrawable(R.drawable.ic_fab_menu_normal));
                    mFloatingActionMenu.toggle();
                }
            }
        });
        mSaveFab = (FloatingActionButton) findViewById(R.id.button_item_save_float_action_menu);
        mSaveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFloatingActionMenu.toggle();
                savePlanTask();
            }
        });
        mSaveAndExitFab = (FloatingActionButton) findViewById(R.id.button_item_save_exit_float_action_menu);
        mSaveAndExitFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePlanTask();
                finish();
            }
        });
        mExitFab = (FloatingActionButton) findViewById(R.id.button_item_exit_float_action_menu);
        mExitFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (mShowType == TYPE_MODIFY) {
            mToolbarTitle.setText("查看计划");
            mMainFab.setImageDrawable(res.getDrawable(R.drawable.ic_fab_menu_modify));
            mTitle.setFocusable(false);
            mDescribe.setFocusable(false);
            mToolbarDate.setClickable(false);
            mToolbarTime.setClickable(false);
        } else {
            mToolbarTitle.setText("新建计划");
            mMainFab.setImageDrawable(res.getDrawable(R.drawable.ic_fab_menu_normal));
            mTitle.setFocusable(true);
            mDescribe.setFocusable(true);
            mToolbarDate.setClickable(true);
            mToolbarTime.setClickable(true);
        }
        PlanTask task = intent.getParcelableExtra(KEY_PLANTASK);
        mIntentPlanTask = task;
        if (task != null) {
            mTitle.setText(task.title);
            mDescribe.setText(task.describe);
            mCalendar.setTime(new Date(task.time));
        }
        setYearMonthDay();
        setHourMinute();
    }

    private void setYearMonthDay() {
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH) + 1;
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        String timeStr = CommonUtil.fillZero(month) + "月" + CommonUtil.fillZero(day) + "日";
        if (!CommonUtil.isToYear(year)) {
            timeStr = year + "年" + timeStr;
        }
        long time = mCalendar.getTimeInMillis();
        if (CommonUtil.isToday(time)) {
            timeStr = "今天";
        } else if (CommonUtil.isTomorrow(time)) {
            timeStr = "明天";
        }
        mToolbarDate.setText(createStringWithLeftPicture(R.drawable.ic_date_plantaskdetails, timeStr));
    }

    private void setHourMinute() {
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);
        mToolbarTime.setText(createStringWithLeftPicture(R.drawable.ic_time_plantaskdetails, CommonUtil.fillZero(hour) + ":" + CommonUtil.fillZero(minute)));
    }

    public SpannableString createStringWithLeftPicture(int drawableId, String str) {
        Resources res = getResources();
        String replacedStr = "image";
        final SpannableString spannableString = new SpannableString(replacedStr + str);
        Drawable drawable = res.getDrawable(drawableId);
        drawable.setBounds(0, 0,
                res.getDimensionPixelSize(R.dimen.plantaskdetails_time_right),
                res.getDimensionPixelSize(R.dimen.plantaskdetails_time_bottom));
        CenteredImageSpan span = new CenteredImageSpan(drawable);
        spannableString.setSpan(span, 0, replacedStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    @SuppressLint("LongLogTag")
    private void savePlanTask() {
        String title = mTitle.getText().toString();
        String describe = mDescribe.getText().toString();
        if (!TextUtils.isEmpty(title) || !TextUtils.isEmpty(describe)) {
            PlanTask task = new PlanTask();
            if (mIntentPlanTask != null) {
                task.id = mIntentPlanTask.id;
                task.state = mIntentPlanTask.state;
                task.priority = mIntentPlanTask.priority;
            } else {
                task.id = String.valueOf(System.currentTimeMillis());
            }
            task.time = mCalendar.getTimeInMillis();
            if (TextUtils.isEmpty(title)) {
                task.title = "\"标题\"";
            } else {
                task.title = title;
            }
            if (TextUtils.isEmpty(describe)) {
                task.describe = "\"描述\"";
            } else {
                task.describe = describe;
            }
            hideSoftInput();

            if (task.equals(mIntentPlanTask)) {
                return;
            } else {
                mIntentPlanTask = task;
            }
            addPlanTask(task);

            Intent intent = new Intent(PlanTaskDetailsActivity.this, UserActionService.class);
            intent.setAction(UserActionService.INTENT_ACTION_ADD_ONE_PLANTASK);
            intent.putExtra(UserActionService.EXTRA_PLANTASK, task);
            Log.d(TAG, "savePlanTask , start intent service: UserActionService");
            startService(intent);
        }
    }

    public void addPlanTask(PlanTask task) {
        CachePlanTaskStore planTaskStore = CachePlanTaskStore.getInstance();
        planTaskStore.addPlanTask(task, true);
        // TODO: 17/12/14 add to server database
    }

    public void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    public void showSoftInput(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }

    @Override
    public void onResume() {
        super.onResume();
        final View decorView = getWindow().getDecorView();
        if (decorView != null) {
            decorView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    //获取View可见区域的bottom
                    Rect rect = new Rect();
                    if (decorView != null) {
                        decorView.getWindowVisibleDisplayFrame(rect);
                    }
                    if (bottom != 0 && oldBottom != 0 && bottom - rect.bottom <= 0) {
                        if (mHideKeyboardRunnable == null) {
                            mHideKeyboardRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    mTitle.setFocusable(false);
                                    mDescribe.setFocusable(false);
                                }
                            };
                        }
                        mHandler.removeCallbacks(mHideKeyboardRunnable);
                        mHandler.post(mHideKeyboardRunnable);
                    } else {
                        if (mShowKeyboardRunnable == null) {
                            mShowKeyboardRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    mTitle.setFocusable(true);
                                    mDescribe.setFocusable(true);
                                }
                            };
                        }
                        mHandler.removeCallbacks(mShowKeyboardRunnable);
                        mHandler.post(mShowKeyboardRunnable);
                    }
                }
            });
        }
    }

}
