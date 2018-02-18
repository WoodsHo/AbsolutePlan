package com.woodsho.absoluteplan.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.hubert.library.Controller;
import com.app.hubert.library.HighLight;
import com.app.hubert.library.NewbieGuide;
import com.app.hubert.library.OnGuideChangedListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.woodsho.absoluteplan.R;
import com.woodsho.absoluteplan.bean.PlanTask;
import com.woodsho.absoluteplan.data.CachePlanTaskStore;
import com.woodsho.absoluteplan.service.UserActionService;
import com.woodsho.absoluteplan.skinloader.SkinBaseActivity;
import com.woodsho.absoluteplan.skinloader.SkinManager;
import com.woodsho.absoluteplan.utils.CommonUtil;
import com.woodsho.absoluteplan.utils.StatusBarUtil;
import com.woodsho.absoluteplan.widget.AbsPlanEditText;
import com.woodsho.absoluteplan.widget.CenteredImageSpan;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hewuzhao on 17/12/14.
 */

public class PlanTaskDetailsActivity extends SkinBaseActivity {
    public static final String TAG = "PlanTaskDetailsActivity";
    public static final String KEY_PLANTASK = "key_plantask";
    public static final String KEY_GUIDE_TIME = "guide_time";
    public static final String KEY_GUIDE_SAVE_PLANTASK = "guide_save_plantask";
    public static final String KEY_SHOW_TYPE = "show_type";
    public static final String KEY_IS_TOMORROW = "key_is_tomorrow";

    public static final String TAG_DATEPICKERDIALOG = "Datepickerdialog";
    public static final String TAG_TIMEPICKERDIALOG = "Timepickerdialog";

    public static final int TYPE_NEW_BUILD = 1;
    public static final int TYPE_MODIFY = 2;

    public Handler mHandler;
    public Runnable mHideKeyboardRunnable, mShowKeyboardRunnable;
    public TextView mToolbarTitle;
    public EditText mTitle;
    public AbsPlanEditText mDescribe;
    public ImageView mBack;
    public TextView mToolbarDate;
    public TextView mToolbarTime;
    private PlanTask mIntentPlanTask;
    private int mShowType;

    private Calendar mCalendar = Calendar.getInstance(Locale.CHINA);

    private ImageView mSave;
    private ImageView mShare;
    private ImageView mList;
    private ImageView mList123;

    public static final String TAG_LIST = "● ";
    public static final String TAG_LIST_123 = "1. ";
    public static final String TAG_LIST_SIGN = "●";

    private boolean flag = false;

    private String mOriginTitle = "";
    private String mOriginContent = "";
    private long mOriginTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantaskdetails);
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
                .addHighLight(mSave, HighLight.Type.CIRCLE)
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
        mDescribe = (AbsPlanEditText) findViewById(R.id.describe_plantaskdetails);
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
        mDescribe.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mList.setClickable(true);
                    mList123.setClickable(true);
                    mList.setAlpha(1f);
                    mList123.setAlpha(1f);
                } else {
                    mList.setClickable(false);
                    mList123.setClickable(false);
                    mList.setAlpha(0.5f);
                    mList123.setAlpha(0.5f);
                }
            }
        });

        mDescribe.addTextChangedListener(new Watcher());

        mList = (ImageView) findViewById(R.id.list_plantaskdetails);
        mList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performList(TAG_LIST);
            }
        });

        mList123 = (ImageView) findViewById(R.id.list_123_plantaskdetails);
        mList123.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performList(TAG_LIST_123);
            }
        });

        mList.setClickable(false);
        mList123.setClickable(false);
        mList.setAlpha(0.5f);
        mList123.setAlpha(0.5f);

        mSave = (ImageView) findViewById(R.id.save_plantaskdetails);

        mBack = (ImageView) findViewById(R.id.back_plantaskdetails);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveDialog();
            }
        });
        mToolbarDate = (TextView) findViewById(R.id.date_plantaskdetails);
        mToolbarTime = (TextView) findViewById(R.id.time_plantaskdetails);

        mToolbarDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int dayOfMonth) {
                                mCalendar.set(Calendar.YEAR, year);
                                mCalendar.set(Calendar.MONTH, month);
                                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                setYearMonthDay();
                                showGuideSave();
                            }
                        },
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        showGuideSave();
                    }
                });
                dpd.setThemeDark(false);
                dpd.vibrate(false);
                dpd.dismissOnPause(true);
                dpd.showYearPickerFirst(false);
                dpd.setVersion(DatePickerDialog.Version.VERSION_2);
                dpd.setAccentColor(SkinManager.getInstance().getColor(R.color.colorPrimary));
                dpd.show(getFragmentManager(), TAG_DATEPICKERDIALOG);
            }
        });
        mToolbarTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePickerDialog timePickerDialog, int hourOfDay, int minute, int second) {
                                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                mCalendar.set(Calendar.MINUTE, minute);
                                setHourMinute();
                            }
                        },
                        mCalendar.get(Calendar.HOUR_OF_DAY),
                        mCalendar.get(Calendar.MINUTE),
                        true //24 hour
                );
                tpd.setThemeDark(false);
                tpd.vibrate(false);
                tpd.dismissOnPause(true);
                tpd.enableSeconds(false);
                tpd.setVersion(TimePickerDialog.Version.VERSION_2);
                tpd.setAccentColor(SkinManager.getInstance().getColor(R.color.colorPrimary));
                tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                    }
                });
                tpd.show(getFragmentManager(), TAG_TIMEPICKERDIALOG);
            }
        });

        Intent intent = getIntent();
        mShowType = intent.getIntExtra(KEY_SHOW_TYPE, TYPE_NEW_BUILD);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShowType == TYPE_MODIFY) {
                    mShowType = TYPE_NEW_BUILD;
                    mToolbarTitle.setText("修改计划");
                    mSave.setImageDrawable(res.getDrawable(R.drawable.ic_save_plantaskdetails));
                    mTitle.setFocusable(true);
                    mDescribe.setFocusable(true);
                    mToolbarDate.setClickable(true);
                    mToolbarTime.setClickable(true);
                } else {
                    savePlanTask();
                    finish();
                }
            }
        });

        mShare = (ImageView) findViewById(R.id.share_plantaskdetails);
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasTitle = false;
                boolean hasDescribe = false;
                Intent intent = new Intent();
                intent.setClass(PlanTaskDetailsActivity.this, ShareActivity.class);
                if (mTitle != null) {
                    String title = mTitle.getText().toString();
                    if (!TextUtils.isEmpty(title) && !TextUtils.equals(title, "\"标题\"")) {
                        intent.putExtra(ShareActivity.KEY_TITLE, title);
                        hasTitle = true;
                    }
                }
                if (mDescribe != null) {
                    String describe = mDescribe.getText().toString();
                    if (!TextUtils.isEmpty(describe) && !TextUtils.equals(describe, "\"描述\"")) {
                        intent.putExtra(ShareActivity.KEY_CONTENT, describe);
                        hasDescribe = true;
                    }
                }
                if (!hasTitle && !hasDescribe) {
                    Toast.makeText(getApplicationContext(), "没有内容可分享呢，添加内容先吧！", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(intent);
            }
        });

        if (mShowType == TYPE_MODIFY) {
            mToolbarTitle.setText("查看计划");
            mSave.setImageDrawable(res.getDrawable(R.drawable.ic_modify_plantaskdetails));
            mTitle.setFocusable(false);
            mDescribe.setFocusable(false);
            mToolbarDate.setClickable(false);
            mToolbarTime.setClickable(false);
        } else {
            mToolbarTitle.setText("新建计划");
            mSave.setImageDrawable(res.getDrawable(R.drawable.ic_save_plantaskdetails));
            mTitle.setFocusable(true);
            mDescribe.setFocusable(true);
            mToolbarDate.setClickable(true);
            mToolbarTime.setClickable(true);
        }
        PlanTask task = intent.getParcelableExtra(KEY_PLANTASK);
        mIntentPlanTask = task;
        if (task != null) {
            mTitle.setText(task.title);
            mOriginTitle = task.title;
            mDescribe.setText(task.describe);
            mOriginContent = task.describe;
            mCalendar.setTime(new Date(task.time));
            mOriginTime = task.time;
        } else {
            boolean isTomorrow = intent.getBooleanExtra(KEY_IS_TOMORROW, false);
            if (isTomorrow) {
                mCalendar.setTime(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24));
            }
            mOriginTime = mCalendar.getTimeInMillis();
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

    private class Watcher implements TextWatcher {

        /**
         * Before text changed.
         *
         * @param s     the s
         * @param start the start 起始光标
         * @param count the count 选择数量
         * @param after the after 替换增加的文字数
         */
        @Override
        public final void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (flag) return;
            int end = start + count;
            if (end > start && end <= s.length()) {
                CharSequence charSequence = s.subSequence(start, end);
                if (charSequence.length() > 0) {
                    onSubText(s, charSequence, start);

                }
            }
        }

        /**
         * On text changed.
         *
         * @param s      the s
         * @param start  the start 起始光标
         * @param before the before 选择数量
         * @param count  the count 添加的数量
         */
        @Override
        public final void onTextChanged(CharSequence s, int start, int before, int count) {
            if (flag) return;
            int end = start + count;
            if (end > start) {
                CharSequence charSequence = s.subSequence(start, end);
                if (charSequence.length() > 0) {
                    onAddText(s, charSequence, start);
                }
            }
        }

        @Override
        public final void afterTextChanged(Editable s) {

        }

    }

    private void onAddText(CharSequence source, CharSequence charSequence, int start) {
        flag = true;
        if ("\n".equals(charSequence.toString())) {
            //用户输入回车
            performAddEnter(mDescribe.getText(), source, start);
        }
        flag = false;
    }

    private void onSubText(CharSequence source, CharSequence charSequence, int start) {
        flag = true;
        //操作代码

        flag = false;
    }

    /**
     * 处理回车操作
     *
     * @param editable
     * @param source
     * @param start
     */
    private void performAddEnter(Editable editable, CharSequence source, int start) {
        //获取回车之前的字符
        String tempStr = source.subSequence(0, start).toString();
        //查找最后一个回车
        int lastEnter = tempStr.lastIndexOf(10);
        if (lastEnter > 0) {
            //最后一个回车到输入回车之间的字符
            tempStr = tempStr.substring(lastEnter + 1, start);
        }

        String mString = tempStr.trim();
        String firstStr = null;
        if (mString.length() > 0) {
            firstStr = String.valueOf(mString.charAt(0));
        }

        if (TextUtils.equals(firstStr, TAG_LIST_SIGN)) {
            editable.insert(start + 1, TAG_LIST);
        } else {
            Pattern pattern = Pattern.compile("^(\\d+)(.*)");
            Matcher matchers = pattern.matcher(mString);
            if (matchers.matches()) {//数字开头
                editable.insert(start + 1, (Integer.parseInt(matchers.group(1)) + 1) + ". ");
            }
        }
    }

    private void performList(String tag) {
        String source = mDescribe.getText().toString();
        int selectionStart = mDescribe.getSelectionStart();
        int selectionEnd = mDescribe.getSelectionEnd();
        String substring = source.substring(0, selectionStart);
        int line = substring.lastIndexOf(10);


        if (line != -1) {
            selectionStart = line + 1;
        } else {
            selectionStart = 0;
        }
        substring = source.substring(selectionStart, selectionEnd);

        String[] split = substring.split("\n");
        StringBuffer stringBuffer = new StringBuffer();

        if (split != null && split.length > 0)
            for (String s : split) {
                if (s.length() == 0 && stringBuffer.length() != 0) {
                    stringBuffer.append("\n");
                    continue;
                }
                if (!s.trim().startsWith(tag)) {
                    //不是 空行或者已经是序号开头
                    if (stringBuffer.length() > 0) stringBuffer.append("\n");
                    stringBuffer.append(tag).append(s);
                } else {
                    if (stringBuffer.length() > 0) stringBuffer.append("\n");
                    stringBuffer.append(s);
                }
            }

        if (stringBuffer.length() == 0) {
            stringBuffer.append(tag);
        }
        mDescribe.getText().replace(selectionStart, selectionEnd, stringBuffer.toString());
        mDescribe.setSelection(stringBuffer.length() + selectionStart);
    }

    @Override
    public void onBackPressed() {
        showSaveDialog();
    }

    private void showSaveDialog() {
        boolean titleChanged = false;
        boolean contentChanged = false;
        boolean timeChanged = false;
        if (!TextUtils.equals(mOriginTitle, mTitle.getText().toString())) {
            titleChanged = true;
        }
        if (!TextUtils.equals(mOriginContent, mDescribe.getText().toString())) {
            contentChanged = true;
        }

        if (mOriginTime != mCalendar.getTimeInMillis()) {
            timeChanged = true;
        }

        if (titleChanged || contentChanged || timeChanged) {
            final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
            View view = View.inflate(this, R.layout.plantaskdetails_dialog_layout, null);
            TextView subTitle = (TextView) view.findViewById(R.id.dialog_subtitle);
            subTitle.setText(getDialogSubTitle(titleChanged, contentChanged, timeChanged));
            TextView save = (TextView) view.findViewById(R.id.dialog_save);
            TextView confirm = (TextView) view.findViewById(R.id.dialog_confirm);
            dialog.setContentView(view);
            //使得点击对话框外部不消失对话框
            dialog.setCanceledOnTouchOutside(true);
            //设置对话框的大小
            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            Resources resources = this.getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            int width = dm.widthPixels;
            view.setMinimumHeight((int) (width * 0.23f));
            lp.width = (int) (width * 0.75f);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            dialogWindow.setAttributes(lp);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    savePlanTask();
                    Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    finish();
                }
            });
            dialog.show();
        } else {
            finish();
        }
    }

    private SpannableString getDialogSubTitle(boolean titleChanged, boolean contentChanged, boolean timeChanged) {
        StringBuilder str = new StringBuilder("修改了");
        if (titleChanged) {
            str.append("标题");
        }
        if (contentChanged) {
            if (titleChanged) {
                str.append("、");
            }
            str.append("内容");
        }
        if (timeChanged) {
            if (titleChanged || contentChanged) {
                str.append("、");
            }
            str.append("时间");
        }
        str.append("还没保存呢");

        int size = 16;

        SpannableString dialogSubTitle = new SpannableString(str.toString());
        dialogSubTitle.setSpan(new AbsoluteSizeSpan(size, true), 3, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        dialogSubTitle.setSpan(new ForegroundColorSpan(SkinManager.getInstance().getColor(R.color.colorPrimary)), 3, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (titleChanged) {
            if (contentChanged) {
                dialogSubTitle.setSpan(new AbsoluteSizeSpan(size, true), 6, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                dialogSubTitle.setSpan(new ForegroundColorSpan(SkinManager.getInstance().getColor(R.color.colorPrimary)), 6, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                if (timeChanged) {
                    dialogSubTitle.setSpan(new AbsoluteSizeSpan(size, true), 9, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    dialogSubTitle.setSpan(new ForegroundColorSpan(SkinManager.getInstance().getColor(R.color.colorPrimary)), 9, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                if (timeChanged) {
                    dialogSubTitle.setSpan(new AbsoluteSizeSpan(size, true), 6, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    dialogSubTitle.setSpan(new ForegroundColorSpan(SkinManager.getInstance().getColor(R.color.colorPrimary)), 6, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        } else if (contentChanged) {
            if (timeChanged) {
                dialogSubTitle.setSpan(new AbsoluteSizeSpan(size, true), 6, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                dialogSubTitle.setSpan(new ForegroundColorSpan(SkinManager.getInstance().getColor(R.color.colorPrimary)), 6, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return dialogSubTitle;
    }
}
