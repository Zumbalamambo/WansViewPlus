package net.ajcloud.wansviewplus.main.device.setting.tfcardStorage;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.core.bean.LocalStorBean;
import net.ajcloud.wansviewplus.support.customview.dialog.TimePickerDialog;
import net.ajcloud.wansviewplus.support.customview.dialog.WeekDayDialog;

import java.util.List;

public class TFTimePeriodActivity extends BaseActivity {

    private TextView periodTipsTextView;
    private SwitchCompat periodSwitch;
    private RelativeLayout startLayout, endLayout, repeatLayout;
    private TextView startTextView, endTextView, repeatTextView;
    private String period;
    private TimePickerDialog startTimeDialog;
    private TimePickerDialog endTimeDialog;
    private WeekDayDialog weekDayDialog;

    private LocalStorBean cloneBean;

    public static void start(Activity context, String period, LocalStorBean bean) {
        Intent intent = new Intent(context, TFTimePeriodActivity.class);
        intent.putExtra("period", period);
        intent.putExtra("LocalStorBean", bean);
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
        getToolbar().setLeftImg(R.mipmap.ic_back);
        periodTipsTextView = findViewById(R.id.item_switch_time_tips);
        periodSwitch = findViewById(R.id.item_switch_switch);
        startLayout = findViewById(R.id.item_start);
        endLayout = findViewById(R.id.item_end);
        repeatLayout = findViewById(R.id.item_repeat);
        startTextView = findViewById(R.id.item_start_time);
        endTextView = findViewById(R.id.item_end_time);
        repeatTextView = findViewById(R.id.item_repeat_time);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            period = getIntent().getStringExtra("period");
            cloneBean = (LocalStorBean) getIntent().getSerializableExtra("LocalStorBean");

        }
        if (TextUtils.equals(period, "2")) {
            getToolbar().setTittle(getResources().getString(R.string.device_setting_period_1));
            periodTipsTextView.setText(getResources().getString(R.string.device_setting_period_1));
        } else if (TextUtils.equals(period, "3")) {
            getToolbar().setTittle(getResources().getString(R.string.device_setting_period_2));
            periodTipsTextView.setText(getResources().getString(R.string.device_setting_period_2));
        }
        for (LocalStorBean.Policy policy : cloneBean.policies) {
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
                for (LocalStorBean.Policy policy : cloneBean.policies) {
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
                for (LocalStorBean.Policy policy :
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
                showStartTimeDialog();
                break;
            case R.id.item_end:
                showEndTimeDialog();
                break;
            case R.id.item_repeat:
                if (!weekDayDialog.isShowing()) {
                    weekDayDialog.show();
                }
                break;
        }
    }

    private void showStartTimeDialog() {
        if (startTimeDialog == null) {
            startTimeDialog = new TimePickerDialog(TFTimePeriodActivity.this);
            startTimeDialog.setOnTimeSelectListener(new TimePickerDialog.OnTimeSelectListener() {
                @Override
                public void onTimeSelected(String time) {
                    for (LocalStorBean.Policy policy : cloneBean.policies) {
                        if (TextUtils.equals(policy.no, period)) {
                            policy.startTime = time;
                            break;
                        }
                    }
                    refreshUI();
                }
            });
        }
        String time = "000000";
        for (LocalStorBean.Policy policy : cloneBean.policies) {
            if (TextUtils.equals(policy.no, period)) {
                if (!TextUtils.isEmpty(policy.startTime))
                    time = policy.startTime;
                break;
            }
        }
        startTimeDialog.setDate(Integer.valueOf(time.substring(0, 2)), Integer.valueOf(time.substring(2, 4)));
        startTimeDialog.show();
    }

    private void showEndTimeDialog() {
        if (endTimeDialog == null) {
            endTimeDialog = new TimePickerDialog(TFTimePeriodActivity.this);
            endTimeDialog.setOnTimeSelectListener(new TimePickerDialog.OnTimeSelectListener() {
                @Override
                public void onTimeSelected(String time) {
                    for (LocalStorBean.Policy policy : cloneBean.policies) {
                        if (TextUtils.equals(policy.no, period)) {
                            policy.endTime = time;
                            break;
                        }
                    }
                    refreshUI();
                }
            });
        }
        String time = "000000";
        for (LocalStorBean.Policy policy : cloneBean.policies) {
            if (TextUtils.equals(policy.no, period)) {
                if (!TextUtils.isEmpty(policy.endTime))
                    time = policy.endTime;
                break;
            }
        }
        endTimeDialog.setDate(Integer.valueOf(time.substring(0, 2)), Integer.valueOf(time.substring(2, 4)));
        endTimeDialog.show();
    }

    private void refreshUI() {
        for (LocalStorBean.Policy policy : cloneBean.policies) {
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
        intent.putExtra("LocalStorBean", cloneBean);
        setResult(RESULT_OK, intent);
        finish();
    }
}
