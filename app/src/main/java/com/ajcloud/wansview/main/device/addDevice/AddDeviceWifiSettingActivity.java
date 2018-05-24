package com.ajcloud.wansview.main.device.addDevice;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.main.application.BaseActivity;
import com.ajcloud.wansview.support.customview.materialEditText.MaterialEditText;

public class AddDeviceWifiSettingActivity extends BaseActivity {

    private MaterialEditText wifiNameEditText;
    private MaterialEditText passwordEditText;
    private Button joinButton;
    private String type;

    public static void start(Context context, String type) {
        Intent intent = new Intent(context, AddDeviceWifiSettingActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device_wifi_setting;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Wi-Fi Setting");
        getToolbar().setLeftImg(R.mipmap.icon_back);
        wifiNameEditText = findViewById(R.id.et_wifi_name);
        passwordEditText = findViewById(R.id.et_password);
        joinButton = findViewById(R.id.btn_join);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            type = getIntent().getStringExtra("type");
        }
    }

    @Override
    protected void initListener() {
        joinButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_left:
                finish();
                break;
            case R.id.btn_join:
                AddDeviceScanQRActivity.start(AddDeviceWifiSettingActivity.this, type);
                break;
            default:
                break;
        }
    }
}
