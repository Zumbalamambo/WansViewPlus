package net.ajcloud.wansviewplus.main.device.setting;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.device.addDevice.AddDeviceSelectActivity;

public class DeviceSettingActivity extends BaseActivity {

    public static int RENAME = 0;
    public static int NETWORK = 1;
    public static int TIMEZONE = 2;
    private RelativeLayout nameLayout, infoLayout, networkLayout, alertLayout,
            imageLayout, timezoneLayout, tfLayout, cloudLayout, maintenanceLayout;
    private TextView nameTextView, networkTextView, timezoneTextView;

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
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.item_name:
                DeviceSettingNameActivity.startForResult(DeviceSettingActivity.this, nameTextView.getText().toString());
                break;
            case R.id.item_info:
                startActivity(new Intent(DeviceSettingActivity.this, DeviceSettingInfoActivity.class));
                break;
            case R.id.item_network:
                startActivity(new Intent(DeviceSettingActivity.this, AddDeviceSelectActivity.class));
                break;
            case R.id.item_alert:
                startActivity(new Intent(DeviceSettingActivity.this, DeviceSettingAlertActivity.class));
                break;
            case R.id.item_image:
                startActivity(new Intent(DeviceSettingActivity.this, ImageAndAudioActivity.class));
                break;
            case R.id.item_timezone:
                startActivity(new Intent(DeviceSettingActivity.this, TimeZoneActivity.class));
                break;
            case R.id.item_tf_storage:
                startActivity(new Intent(DeviceSettingActivity.this, TFCardActivity.class));
                break;
            case R.id.item_cloud_storage:
                startActivity(new Intent(DeviceSettingActivity.this, CloudStorageActivity.class));
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
            }
        }
    }
}
