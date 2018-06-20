package net.ajcloud.wansviewplus.main.device.setting.homeAlert;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.bean.MoveMonitorBean;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

public class DetectionTimeActivity extends BaseActivity {

    private static final String LOADING = "LOADING";
    public static String PERIOD_ONE = "2";
    public static String PERIOD_TWO = "3";
    private SwitchCompat alltimeSwitch;
    private RelativeLayout periodOneLayout, periodTwoLayout;
    private TextView periodOneTextView, periodTwoTextView;
    private String deviceId;
    private Camera camera;
    private MoveMonitorBean cloneBean;
    private DeviceApiUnit deviceApiUnit;

    public static void start(Context context, String deviceId) {
        Intent intent = new Intent(context, DetectionTimeActivity.class);
        intent.putExtra("deviceId", deviceId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detection_time;
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
        getToolbar().setTittle("Detection time");
        getToolbar().setLeftImg(R.mipmap.icon_back);

        alltimeSwitch = findViewById(R.id.item_all_time_switch);
        periodOneLayout = findViewById(R.id.item_period_one);
        periodTwoLayout = findViewById(R.id.item_period_two);
        periodOneTextView = findViewById(R.id.item_period_one_time);
        periodTwoTextView = findViewById(R.id.item_period_two_time);
    }

    @Override
    protected void initData() {
        deviceApiUnit = new DeviceApiUnit(this);
        if (getIntent() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
        }
        refreshUI();
    }

    @Override
    protected void initListener() {
        periodOneLayout.setOnClickListener(this);
        periodTwoLayout.setOnClickListener(this);
        alltimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (MoveMonitorBean.Policy policy : cloneBean.policies) {
                    if (TextUtils.equals(policy.no, "1")) {
                        if (isChecked) {
                            policy.enable = 1;
                            periodOneLayout.setVisibility(View.GONE);
                            periodTwoLayout.setVisibility(View.GONE);
                        } else {
                            policy.enable = 0;
                            periodOneLayout.setVisibility(View.VISIBLE);
                            periodTwoLayout.setVisibility(View.VISIBLE);
                            if (!TextUtils.isEmpty(policy.startTime)) {
                                String time = policy.startTime;
                                periodOneTextView.setText(time.substring(0, time.length() - 2).replaceAll("(.{2})", ":"));
                            }
                            if (!TextUtils.isEmpty(policy.endTime)) {
                                String time = policy.endTime;
                                periodTwoTextView.setText(time.substring(0, time.length() - 2).replaceAll("(.{2})", ":"));
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.item_period_one:
                TimePeriodActivity.start(DetectionTimeActivity.this, deviceId, PERIOD_ONE);
                break;
            case R.id.item_period_two:
                TimePeriodActivity.start(DetectionTimeActivity.this, deviceId, PERIOD_TWO);
                break;
            default:
                break;
        }
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
        if (camera.moveMonitorConfig != null) {
            for (MoveMonitorBean.Policy policy : camera.moveMonitorConfig.policies) {
                if (TextUtils.equals(policy.no, "1")) {
                    if (policy.enable == 1) {
                        alltimeSwitch.setChecked(true);
                        periodOneLayout.setVisibility(View.GONE);
                        periodTwoLayout.setVisibility(View.GONE);
                    } else {
                        alltimeSwitch.setChecked(false);
                        periodOneLayout.setVisibility(View.VISIBLE);
                        periodTwoLayout.setVisibility(View.VISIBLE);
                        for (MoveMonitorBean.Policy item : camera.moveMonitorConfig.policies) {
                            if (TextUtils.equals(item.no, "2")) {
                                if (item.enable == 0) {
                                    periodOneTextView.setText("off");
                                } else if (!TextUtils.isEmpty(item.startTime) && !TextUtils.isEmpty(item.endTime)) {
                                    StringBuilder startTime = new StringBuilder(item.startTime.substring(0, item.startTime.length() - 2));
                                    StringBuilder endTime = new StringBuilder(item.endTime.substring(0, item.endTime.length() - 2));
                                    periodOneTextView.setText(startTime.insert(2, ":").toString() + " - " + endTime.insert(2, ":").toString());
                                }
                            }
                            if (TextUtils.equals(item.no, "3")) {
                                if (item.enable == 0) {
                                    periodTwoTextView.setText("off");
                                } else if (!TextUtils.isEmpty(item.startTime) && !TextUtils.isEmpty(item.endTime)) {
                                    StringBuilder startTime = new StringBuilder(item.startTime.substring(0, item.startTime.length() - 2));
                                    StringBuilder endTime = new StringBuilder(item.endTime.substring(0, item.endTime.length() - 2));
                                    periodTwoTextView.setText(startTime.insert(2, ":").toString() + " - " + endTime.insert(2, ":").toString());
                                }
                            }
                        }
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
