package net.ajcloud.wansviewplus.main.device.setting.cloudStorage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.core.bean.CloudStorBean;
import net.ajcloud.wansviewplus.support.customview.dialog.WeekDayDialog;
import net.ajcloud.wansviewplus.support.customview.picker.pickerview.TimePickerView;
import net.ajcloud.wansviewplus.support.customview.picker.pickerview.builder.TimePickerBuilder;
import net.ajcloud.wansviewplus.support.customview.picker.pickerview.listener.OnTimeSelectListener;
import net.ajcloud.wansviewplus.support.tools.WLog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CloudTimePeriodActivity extends BaseActivity {

    private TextView periodTipsTextView;
    private SwitchCompat periodSwitch;
    private RelativeLayout startLayout, endLayout, repeatLayout;
    private TextView startTextView, endTextView, repeatTextView;
    private String period;
    private TimePickerView startTimeView;
    private TimePickerView endTimeView;
    private WeekDayDialog weekDayDialog;

    private CloudStorBean cloneBean;

    public static void start(Activity context, String period, CloudStorBean bean) {
        Intent intent = new Intent(context, CloudTimePeriodActivity.class);
        intent.putExtra("period", period);
        intent.putExtra("CloudStorBean", bean);
        context.startActivityForResult(intent, 0);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_time_period;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setLeftImg(R.mipmap.icon_back);
        periodTipsTextView = findViewById(R.id.item_switch_time_tips);
        periodSwitch = findViewById(R.id.item_switch_switch);
        startLayout = findViewById(R.id.item_start);
        endLayout = findViewById(R.id.item_end);
        repeatLayout = findViewById(R.id.item_repeat);
        startTextView = findViewById(R.id.item_start_time);
        endTextView = findViewById(R.id.item_end_time);
        repeatTextView = findViewById(R.id.item_repeat_time);
        initStartTimeDialog();
        initEndTimeDialog();
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            period = getIntent().getStringExtra("period");
            cloneBean = (CloudStorBean) getIntent().getSerializableExtra("CloudStorBean");

        }
        if (TextUtils.equals(period, "2")) {
            getToolbar().setTittle("Time Period 1");
            periodTipsTextView.setText("Time Period 1");
        } else if (TextUtils.equals(period, "3")) {
            getToolbar().setTittle("Time Period 2");
            periodTipsTextView.setText("Time Period 2");
        }
        for (CloudStorBean.Policy policy : cloneBean.policies) {
            if (TextUtils.equals(policy.no, period)) {
                weekDayDialog = new WeekDayDialog(this, policy.weekDays);
            }
        }
        refreshUI();
    }

    @Override
    protected void initListener() {
        startLayout.setOnClickListener(this);
        endLayout.setOnClickListener(this);
        repeatLayout.setOnClickListener(this);
        periodSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (CloudStorBean.Policy policy : cloneBean.policies) {
                    if (TextUtils.equals(policy.no, period)) {
                        if (isChecked) {
                            policy.enable = "1";
                        } else {
                            policy.enable = "0";
                        }
                        break;
                    }
                }
                refreshUI();
            }
        });
        weekDayDialog.setDialogClickListener(new WeekDayDialog.OnDialogClickListener() {
            @Override
            public void confirm(List<Integer> weekdays) {
                for (CloudStorBean.Policy policy :
                        cloneBean.policies) {
                    if (TextUtils.equals(policy.no, period)) {
                        policy.weekDays = weekdays;
                        break;
                    }
                }
                refreshUI();
            }
        });
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.item_start:
                startTimeView.setDate(Calendar.getInstance());
                startTimeView.show();
                break;
            case R.id.item_end:
                endTimeView.setDate(Calendar.getInstance());
                endTimeView.show();
                break;
            case R.id.item_repeat:
                if (!weekDayDialog.isShowing()) {
                    weekDayDialog.show();
                }
                break;
        }
    }

    private void initStartTimeDialog() {
        if (startTimeView == null) {
            startTimeView = new TimePickerBuilder(this, new OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    String[] times = sf.format(date).split(":");
                    StringBuilder time = new StringBuilder();
                    time.append(times[0]);
                    time.append(times[1]);
                    time.append("00");
                    WLog.d(TAG, time.toString());
                    for (CloudStorBean.Policy policy : cloneBean.policies) {
                        if (TextUtils.equals(policy.no, period)) {
                            policy.startTime = time.toString();
                            break;
                        }
                    }
                    refreshUI();
                }
            })
                    .setCancelText("Cancel")
                    .setCancelColor(getResources().getColor(R.color.gray_second))
                    .setSubmitText("Confirm")
                    .setSubmitColor(getResources().getColor(R.color.gray_first))
                    .setTitleBgColor(getResources().getColor(R.color.white))
                    .setLabel("", "", "", "", "", "")
                    .setType(new boolean[]{false, false, false, true, true, false})
                    .isDialog(true)
                    .setLineSpacingMultiplier(4.0f)
                    .isCyclic(true)
                    .build();
        }
        Dialog mDialog = startTimeView.getDialog();
        if (mDialog != null) {

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            startTimeView.getDialogContainerLayout().setLayoutParams(params);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
            }
        }
    }

    private void initEndTimeDialog() {
        if (endTimeView == null) {
            endTimeView = new TimePickerBuilder(this, new OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    String[] times = sf.format(date).split(":");
                    StringBuilder time = new StringBuilder();
                    time.append(times[0]);
                    time.append(times[1]);
                    time.append("00");
                    WLog.d(TAG, time.toString());
                    for (CloudStorBean.Policy policy : cloneBean.policies) {
                        if (TextUtils.equals(policy.no, period)) {
                            policy.endTime = time.toString();
                            break;
                        }
                    }
                    refreshUI();
                }
            })
                    .setCancelText("Cancel")
                    .setCancelColor(getResources().getColor(R.color.gray_second))
                    .setSubmitText("Confirm")
                    .setSubmitColor(getResources().getColor(R.color.gray_first))
                    .setTitleBgColor(getResources().getColor(R.color.white))
                    .setLabel("", "", "", "", "", "")
                    .setType(new boolean[]{false, false, false, true, true, false})
                    .isDialog(true)
                    .setLineSpacingMultiplier(4.0f)
                    .isCyclic(true)
                    .build();
        }
        Dialog mDialog = endTimeView.getDialog();
        if (mDialog != null) {

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            endTimeView.getDialogContainerLayout().setLayoutParams(params);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
            }
        }
    }

    SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");

    private void refreshUI() {
        for (CloudStorBean.Policy policy : cloneBean.policies) {
            if (TextUtils.equals(policy.no, period)) {
                if (TextUtils.equals(policy.enable, "1")) {
                    periodSwitch.setChecked(true);
                    startLayout.setVisibility(View.VISIBLE);
                    endLayout.setVisibility(View.VISIBLE);
                    repeatLayout.setVisibility(View.VISIBLE);

                    if (!TextUtils.isEmpty(policy.startTime)) {
                        StringBuilder time = new StringBuilder(policy.startTime.substring(0, policy.startTime.length() - 2));
                        startTextView.setText(time.insert(2, ":"));
                    }

                    if (!TextUtils.isEmpty(policy.endTime)) {
                        StringBuilder time = new StringBuilder(policy.endTime.substring(0, policy.endTime.length() - 2));
                        endTextView.setText(time.insert(2, ":"));
                    }

                    if (policy.weekDays != null) {
                        StringBuilder weeks = new StringBuilder();
                        for (Integer num : policy.weekDays) {
                            weeks.append(num);
                            weeks.append(" ");
                        }
                        repeatTextView.setText(weeks.toString());
                    }
                } else {
                    periodSwitch.setChecked(false);
                    startLayout.setVisibility(View.GONE);
                    endLayout.setVisibility(View.GONE);
                    repeatLayout.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("CloudStorBean", cloneBean);
        setResult(RESULT_OK, intent);
        finish();
    }
}
