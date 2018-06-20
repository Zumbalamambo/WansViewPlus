package net.ajcloud.wansviewplus.main.device.setting.homeAlert;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.bean.MoveMonitorBean;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import java.util.List;

public class DeviceSettingAlertActivity extends BaseActivity {

    private static final String LOADING = "LOADING";
    private SwitchCompat detectionSwitch;
    private TextView levelTextView;
    private AppCompatSeekBar detectionSeekbar;
    private RelativeLayout sensitivityLayout, timeLayout;
    private TextView timeTextView;

    private String deviceId;
    private Camera camera;
    private MoveMonitorBean cloneBean;
    private DeviceApiUnit deviceApiUnit;

    public static void start(Context context, String deviceId) {
        Intent intent = new Intent(context, DeviceSettingAlertActivity.class);
        intent.putExtra("deviceId", deviceId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_setting_alert;
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
        getToolbar().setTittle("Home alert");
        getToolbar().setLeftImg(R.mipmap.icon_back);

        detectionSwitch = findViewById(R.id.item_detection_switch);
        levelTextView = findViewById(R.id.item_sensitivity_level);
        detectionSeekbar = findViewById(R.id.item_sensitivity_seekbar);
        sensitivityLayout = findViewById(R.id.item_sensitivity);
        timeLayout = findViewById(R.id.item_time);
        timeTextView = findViewById(R.id.item_time_time);
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
        timeLayout.setOnClickListener(this);
        detectionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cloneBean.enable = 1;
                    sensitivityLayout.setVisibility(View.VISIBLE);
                    timeLayout.setVisibility(View.VISIBLE);
                } else {
                    cloneBean.enable = 0;
                    sensitivityLayout.setVisibility(View.GONE);
                    timeLayout.setVisibility(View.GONE);
                }
            }
        });
        detectionSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    levelTextView.setText("low");
                } else if (progress == 1) {

                } else if (progress == 2) {
                    levelTextView.setText("medium");
                } else if (progress == 3) {

                } else if (progress == 4) {
                    levelTextView.setText("high");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                cloneBean.susceptiveness = String.valueOf(progress + 1);
            }
        });
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.item_time:
                DetectionTimeActivity.start(DeviceSettingAlertActivity.this, deviceId);
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
        if (camera != null && camera.moveMonitorConfig != null) {
            if (camera.moveMonitorConfig.enable == 1) {
                detectionSwitch.setChecked(true);
                sensitivityLayout.setVisibility(View.VISIBLE);
                timeLayout.setVisibility(View.VISIBLE);

                if (!TextUtils.isEmpty(camera.moveMonitorConfig.susceptiveness)) {
                    detectionSeekbar.setProgress(Integer.parseInt(camera.moveMonitorConfig.susceptiveness) - 1);
                }

                List<MoveMonitorBean.Policy> policyList = camera.moveMonitorConfig.policies;
                for (MoveMonitorBean.Policy policy : policyList) {
                    if (TextUtils.equals(policy.no, "1")) {
                        if (policy.enable == 1) {
                            timeTextView.setText("24-hour");
                        } else {
                            StringBuilder time = new StringBuilder();
                            for (MoveMonitorBean.Policy policy_2 : policyList) {
                                if (TextUtils.equals(policy_2.no, "2")) {
                                    if (policy_2.enable == 1) {
                                        time.append(" period1");
                                    }
                                } else if (TextUtils.equals(policy.no, "3")) {
                                    if (policy_2.enable == 1) {
                                        time.append(" period2");
                                    }
                                }
                            }
                            timeTextView.setText(time.toString());
                        }
                    }
                }
            } else {
                detectionSwitch.setChecked(false);
                sensitivityLayout.setVisibility(View.GONE);
                timeLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        doSet();
    }
}
