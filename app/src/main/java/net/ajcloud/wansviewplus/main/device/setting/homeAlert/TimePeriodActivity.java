package net.ajcloud.wansviewplus.main.device.setting.homeAlert;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.bean.MoveMonitorBean;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.customview.dialog.WeekDayDialog;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TimePeriodActivity extends BaseActivity implements OnDateSetListener {

    private static final String LOADING = "LOADING";
    private TextView periodTipsTextView;
    private SwitchCompat periodSwitch;
    private RelativeLayout startLayout, endLayout, repeatLayout;
    private TextView startTextView, endTextView, repeatTextView;
    private String period;
    private TimePickerDialog startTimeDialog;
    private TimePickerDialog endTiemDialog;
    private WeekDayDialog weekDayDialog;

    private String deviceId;
    private Camera camera;
    private MoveMonitorBean cloneBean;
    private DeviceApiUnit deviceApiUnit;

    public static void start(Context context, String deviceId, String period) {
        Intent intent = new Intent(context, TimePeriodActivity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("period", period);
        context.startActivity(intent);
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
    protected void onResume() {
        super.onResume();
        if (camera != null && camera.moveMonitorConfig != null) {
            cloneBean = (MoveMonitorBean) camera.moveMonitorConfig.deepClone();
        }
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
        deviceApiUnit = new DeviceApiUnit(this);
        if (getIntent() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            period = getIntent().getStringExtra("period");
            camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
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
                for (MoveMonitorBean.Policy policy : cloneBean.policies) {
                    if (TextUtils.equals(policy.no, period)) {
                        if (isChecked) {
                            policy.enable = 1;
                            startLayout.setVisibility(View.VISIBLE);
                            endLayout.setVisibility(View.VISIBLE);
                            repeatLayout.setVisibility(View.VISIBLE);
                            if (!TextUtils.isEmpty(policy.startTime)) {
                                String time = policy.startTime;
                                startTextView.setText(time.substring(0, time.length() - 2).replaceAll("(.{2})", ":"));
                            }
                            if (!TextUtils.isEmpty(policy.endTime)) {
                                String time = policy.endTime;
                                endTextView.setText(time.substring(0, time.length() - 2).replaceAll("(.{2})", ":"));
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
                            policy.enable = 0;
                            startLayout.setVisibility(View.GONE);
                            endLayout.setVisibility(View.GONE);
                            repeatLayout.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        weekDayDialog.setDialogClickListener(new WeekDayDialog.OnDialogClickListener() {
            @Override
            public void confirm(List<Integer> weekdays) {
                for (MoveMonitorBean.Policy policy :
                        cloneBean.policies) {
                    if (TextUtils.equals(policy.no, period)) {
                        policy.weekDays = weekdays;
                    }
                }
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
        for (MoveMonitorBean.Policy policy : cloneBean.policies) {
            if (TextUtils.equals(policy.no, period)) {
                if (timePickerView == startTimeDialog) {
                    policy.startTime = time.replace(":", "");
                    startTextView.setText(time.substring(0, time.length() - 3));
                } else if (timePickerView == endTiemDialog) {
                    policy.endTime = time.replace(":", "");
                    endTextView.setText(time.substring(0, time.length() - 3));
                }
            }
        }
    }

    SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sf.format(d);
    }

    private void doSet() {
        progressDialogManager.showDialog(LOADING, this);
        deviceApiUnit.setMoveDetection(camera.getGatewayUrl(), deviceId, cloneBean, new OkgoCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                progressDialogManager.dimissDialog(LOADING, 0);
                camera.moveMonitorConfig = cloneBean;
                finish();
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(LOADING, 0);
                ToastUtil.single(msg);
                finish();
            }
        });
    }

    private void refreshUI() {
        if (TextUtils.equals(period, "2")) {
            getToolbar().setTittle("Time Period 1");
            periodTipsTextView.setText("Time Period 1");
            for (MoveMonitorBean.Policy policy : camera.moveMonitorConfig.policies) {
                if (TextUtils.equals(policy.no, "2")) {
                    if (policy.enable == 1) {
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
                            weekDayDialog = new WeekDayDialog(this, policy.weekDays);
                        }
                    } else {
                        periodSwitch.setChecked(false);
                        startLayout.setVisibility(View.GONE);
                        endLayout.setVisibility(View.GONE);
                        repeatLayout.setVisibility(View.GONE);
                    }
                }
            }
        } else if (TextUtils.equals(period, "3")) {
            getToolbar().setTittle("Time Period 2");
            periodTipsTextView.setText("Time Period 2");
            for (MoveMonitorBean.Policy policy : camera.moveMonitorConfig.policies) {
                if (TextUtils.equals(policy.no, "3")) {
                    if (policy.enable == 1) {
                        periodSwitch.setChecked(true);
                        startLayout.setVisibility(View.VISIBLE);
                        endLayout.setVisibility(View.VISIBLE);
                        repeatLayout.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(policy.startTime)) {
                            String time = policy.startTime;
                            startTextView.setText(time.substring(0, time.length() - 2).replaceAll("(.{2})", ":"));
                        }
                        if (!TextUtils.isEmpty(policy.endTime)) {
                            String time = policy.endTime;
                            endTextView.setText(time.substring(0, time.length() - 2).replaceAll("(.{2})", ":"));
                        }
                        if (policy.weekDays != null) {
                            StringBuilder weeks = new StringBuilder();
                            for (Integer num : policy.weekDays) {
                                weeks.append(num);
                                weeks.append(" ");
                            }
                            repeatTextView.setText(weeks.toString());
                            weekDayDialog = new WeekDayDialog(this, policy.weekDays);
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
    }

    @Override
    public void onBackPressed() {
        doSet();
    }
}
