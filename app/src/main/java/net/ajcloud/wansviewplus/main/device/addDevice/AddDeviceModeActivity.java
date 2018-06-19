package net.ajcloud.wansviewplus.main.device.addDevice;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.device.addDevice.cable.AddDeviceCableConfirmActivity;
import net.ajcloud.wansviewplus.main.device.addDevice.wifi.AddDeviceCameraSettingActivity;

public class AddDeviceModeActivity extends BaseActivity {

    private Button wifiButton;
    private Button cableButton;

    public static void start(Context context) {
        Intent intent = new Intent(context, AddDeviceModeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device_mode;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Select the connection mode");
        getToolbar().setLeftImg(R.mipmap.icon_back);
        wifiButton = findViewById(R.id.btn_wifi_select);
        cableButton = findViewById(R.id.btn_cable_select);
    }

    @Override
    protected void initListener() {
        wifiButton.setOnClickListener(this);
        cableButton.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.btn_wifi_select:
                AddDeviceCameraSettingActivity.start(AddDeviceModeActivity.this);
                break;
            case R.id.btn_cable_select:
                startActivity(new Intent(AddDeviceModeActivity.this, AddDeviceCableConfirmActivity.class));
                break;
            default:
                break;
        }
    }
}
