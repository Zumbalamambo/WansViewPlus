package net.ajcloud.wansviewplus.main.device.setting;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.main.device.addDevice.AddDeviceSelectActivity;
import net.ajcloud.wansviewplus.main.device.setting.cloudStorage.CloudStorageActivity;
import net.ajcloud.wansviewplus.main.device.setting.homeAlert.DeviceSettingAlertActivity;
import net.ajcloud.wansviewplus.main.device.setting.tfcardStorage.TFCardActivity;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.core.device.DeviceInfoDictionary;

public class DeviceSettingActivity extends BaseActivity {

    public static int RENAME = 0;
    public static int NETWORK = 1;
    public static int TIMEZONE = 2;
    private RelativeLayout nameLayout, infoLayout, networkLayout, alertLayout,
            imageLayout, timezoneLayout, tfLayout, cloudLayout, maintenanceLayout;
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
        getToolbar().setTittle("Setting");
        getToolbar().setLeftImg(R.mipmap.icon_back);

        nameLayout = findViewById(R.id.item_name);
        infoLayout = findViewById(R.id.item_info);
        networkLayout = findViewById(R.id.item_network);
        alertLayout = findViewById(R.id.item_alert);
        imageLayout = findViewById(R.id.item_image);
        timezoneLayout = findViewById(R.id.item_timezone);
        tfLayout = findViewById(R.id.item_tf_storage);
        cloudLayout = findViewById(R.id.item_cloud_storage);
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
        cloudLayout.setOnClickListener(this);
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
                startActivity(new Intent(DeviceSettingActivity.this, AddDeviceSelectActivity.class));
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
            case R.id.item_cloud_storage:
                CloudStorageActivity.start(DeviceSettingActivity.this, deviceId);
                break;
            case R.id.item_maintenance:
                startActivity(new Intent(DeviceSettingActivity.this, MaintenanceActivity.class));
                break;
            default:
                break;
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
