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
import net.ajcloud.wansviewplus.support.customview.dialog.WeekDayDialog;
import net.ajcloud.wansviewplus.support.customview.dialog.timePicker.TimePickerDialog;
import net.ajcloud.wansviewplus.support.customview.dialog.timePicker.data.Type;
import net.ajcloud.wansviewplus.support.customview.dialog.timePicker.listener.OnDateSetListener;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TFTimePeriodActivity extends BaseActivity implements OnDateSetListener {

    private TextView periodTipsTextView;
    private SwitchCompat periodSwitch;
    private RelativeLayout startLayout, endLayout, repeatLayout;
    private TextView startTextView, endTextView, repeatTextView;
    private String period;
    private TimePickerDialog startTimeDialog;
    private TimePickerDialog endTiemDialog;
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
        getToolbar().setLeftImg(R.mipmap.icon_back);
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
            getToolbar().setTittle("Time Period 1");
            periodTipsTextView.setText("Time Period 1");
        } else if (TextUtils.equals(period, "3")) {
            getToolbar().setTittle("Time Period 2");
            periodTipsTextView.setText("Time Period 2");
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
            startTimeDialog = new TimePickerDialog.Builder()
                    .setCallBack(this)
                    .setType(Type.HOURS_MINS)
                    .build();
        }
        startTimeDialog.show(getSupportFragmentManager(), "start");
    }

    private void showEndTimeDialog() {
        if (endTiemDialog == null) {
            endTiemDialog = new TimePickerDialog.Builder()
                    .setCallBack(this)
                    .setType(Type.HOURS_MINS)
                    .build();
        }
        endTiemDialog.show(getSupportFragmentManager(), "end");
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        String time = getDateToString(millseconds);
        ToastUtil.single(getDateToString(millseconds));
        for (LocalStorBean.Policy policy : cloneBean.policies) {
            if (TextUtils.equals(policy.no, period)) {
                if (timePickerView == startTimeDialog) {
                    policy.startTime = time.replace(":", "");
                } else if (timePickerView == endTiemDialog) {
                    policy.endTime = time.replace(":", "");
                }
                break;
            }
        }
        refreshUI();
    }

    SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sf.format(d);
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
