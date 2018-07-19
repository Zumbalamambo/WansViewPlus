package net.ajcloud.wansviewplus.main.device.setting;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.entity.CapabilityInfo;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.main.device.addDevice.AddDeviceModeActivity;
import net.ajcloud.wansviewplus.main.device.addDevice.cable.AddDeviceCableConfirmActivity;
import net.ajcloud.wansviewplus.main.device.addDevice.wifi.AddDeviceCameraSettingActivity;
import net.ajcloud.wansviewplus.main.device.setting.homeAlert.DeviceSettingAlertActivity;
import net.ajcloud.wansviewplus.main.device.setting.tfcardStorage.TFCardActivity;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.core.device.DeviceInfoDictionary;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

public class DeviceSettingActivity extends BaseActivity {

    public static int RENAME = 0;
    public static int NETWORK = 1;
    public static int TIMEZONE = 2;
    public static String LOADING = "LOADING";
    private RelativeLayout nameLayout, infoLayout, networkLayout, alertLayout,
            imageLayout, timezoneLayout, tfLayout, maintenanceLayout;
    private TextView nameTextView, networkTextView, timezoneTextView;
    private String deviceId;
    private Camera camera;

    public static void start(Context context, String deviceId) {
        Intent intent = new Intent(context, DeviceSettingActivity.class);
        intent.putExtra("deviceId", deviceId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_setting;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle(getResources().getString(R.string.device_setting));
        getToolbar().setLeftImg(R.mipmap.ic_back);

        nameLayout = findViewById(R.id.item_name);
        infoLayout = findViewById(R.id.item_info);
        networkLayout = findViewById(R.id.item_network);
        alertLayout = findViewById(R.id.item_alert);
        imageLayout = findViewById(R.id.item_image);
        timezoneLayout = findViewById(R.id.item_timezone);
        tfLayout = findViewById(R.id.item_tf_storage);
        maintenanceLayout = findViewById(R.id.item_maintenance);
        nameTextView = findViewById(R.id.item_name_name);
        networkTextView = findViewById(R.id.item_network_name);
        timezoneTextView = findViewById(R.id.item_timezone_name);
    }

    @Override
    protected void initListener() {
        nameLayout.setOnClickListener(this);
        infoLayout.setOnClickListener(this);
        networkLayout.setOnClickListener(this);
        alertLayout.setOnClickListener(this);
        imageLayout.setOnClickListener(this);
        timezoneLayout.setOnClickListener(this);
        tfLayout.setOnClickListener(this);
        maintenanceLayout.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
        }

        if (camera != null) {
            nameTextView.setText(DeviceInfoDictionary.getNameByDevice(camera));
            if (camera.networkConfig != null) {
                networkTextView.setText(camera.networkConfig.ssid);
            }
            if (camera.timeConfig != null) {
                timezoneTextView.setText(camera.timeConfig.tzName);
            }
        }
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.item_name:
                DeviceSettingNameActivity.startForResult(DeviceSettingActivity.this, deviceId, nameTextView.getText().toString());
                break;
            case R.id.item_info:
                DeviceSettingInfoActivity.start(DeviceSettingActivity.this, deviceId);
                break;
            case R.id.item_network:
                startNetworkConfig();
                break;
            case R.id.item_alert:
                DeviceSettingAlertActivity.start(DeviceSettingActivity.this, deviceId);
                break;
            case R.id.item_image:
                ImageAndAudioActivity.start(DeviceSettingActivity.this, deviceId);
                break;
            case R.id.item_timezone:
                TimeZoneActivity.start(DeviceSettingActivity.this, deviceId);
                break;
            case R.id.item_tf_storage:
                TFCardActivity.start(DeviceSettingActivity.this, deviceId);
                break;
            case R.id.item_maintenance:
                MaintenanceActivity.start(DeviceSettingActivity.this, deviceId);
                break;
            default:
                break;
        }
    }

    private void startNetworkConfig() {
        if (camera.capability != null && !TextUtils.isEmpty(camera.capability.networkConfig)) {
            String[] configs = camera.capability.networkConfig.split(",");
            if (configs.length == 1) {
                if (TextUtils.equals(configs[0], "qr")) {
                    AddDeviceCameraSettingActivity.start(DeviceSettingActivity.this);
                } else if (TextUtils.equals(configs[0], "eth")) {
                    startActivity(new Intent(DeviceSettingActivity.this, AddDeviceCableConfirmActivity.class));
                }
            } else {
                AddDeviceModeActivity.start(DeviceSettingActivity.this);
            }
        } else {
            progressDialogManager.showDialog(LOADING, this);
            new DeviceApiUnit(DeviceSettingActivity.this).getCapability(camera.deviceMode, new OkgoCommonListener<CapabilityInfo>() {
                @Override
                public void onSuccess(CapabilityInfo bean) {
                    progressDialogManager.dimissDialog(LOADING, 0);
                    if (bean != null) {
                        String[] networkConfigs = bean.getNetworkConfigs();
                        if (networkConfigs != null) {
                            if (networkConfigs.length == 2) {
                                AddDeviceModeActivity.start(DeviceSettingActivity.this);
                            } else if (networkConfigs.length == 1) {
                                if (TextUtils.equals(networkConfigs[0], "qr")) {
                                    AddDeviceCameraSettingActivity.start(DeviceSettingActivity.this);
                                } else if (TextUtils.equals(networkConfigs[0], "eth")) {
                                    startActivity(new Intent(DeviceSettingActivity.this, AddDeviceCableConfirmActivity.class));
                                }
                            }
                        } else {
                            ToastUtil.single(R.string.common_error);
                        }
                    } else {
                        ToastUtil.single(R.string.common_error);
                    }
                }

                @Override
                public void onFail(int code, String msg) {
                    progressDialogManager.dimissDialog(LOADING, 0);
                    ToastUtil.single(R.string.common_error);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RENAME) {
                if (data != null) {
                    String name = data.getStringExtra("name");
                    nameTextView.setText(name);
                }
            } else if (requestCode == TIMEZONE) {
                if (data != null) {
                    String timeZone = data.getStringExtra("timezone");
                    timezoneTextView.setText(timeZone);
                }
            }
        }
    }
}
