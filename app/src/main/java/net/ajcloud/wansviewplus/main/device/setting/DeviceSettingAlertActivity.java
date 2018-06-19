package net.ajcloud.wansviewplus.main.device.setting;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

public class DeviceSettingAlertActivity extends BaseActivity {

    private SwitchCompat detectionSwitch;
    private TextView levelTextView;
    private AppCompatSeekBar detectionSeekbar;
    private RelativeLayout timeLayout;
    private TextView timeTextView;
    private String deviceId;
    private Camera camera;

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
    protected void initView() {
        getToolbar().setTittle("Home alert");
        getToolbar().setLeftImg(R.mipmap.icon_back);

        detectionSwitch = findViewById(R.id.item_detection_switch);
        levelTextView = findViewById(R.id.item_sensitivity_level);
        detectionSeekbar = findViewById(R.id.item_sensitivity_seekbar);
        timeLayout = findViewById(R.id.item_time);
        timeTextView = findViewById(R.id.item_time_time);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
        }
        if (camera != null && camera.moveMonitorConfig != null) {
            if (camera.moveMonitorConfig.enable == 1) {
                detectionSwitch.setChecked(true);
                detectionSeekbar.setProgress(camera.moveMonitorConfig.susceptiveness + 1);
            }

        }
    }

    @Override
    protected void initListener() {
        timeLayout.setOnClickListener(this);
        detectionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        detectionSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ToastUtil.single(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.item_time:
                startActivity(new Intent(DeviceSettingAlertActivity.this, DetectionTimeActivity.class));
                break;
            default:
                break;
        }
    }
}
