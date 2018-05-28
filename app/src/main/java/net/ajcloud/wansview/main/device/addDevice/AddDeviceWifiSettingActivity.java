package net.ajcloud.wansview.main.device.addDevice;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.customview.materialEditText.MaterialEditText;

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
        return net.ajcloud.wansview.R.layout.activity_add_device_wifi_setting;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Wi-Fi Setting");
        getToolbar().setLeftImg(net.ajcloud.wansview.R.mipmap.icon_back);
        wifiNameEditText = findViewById(net.ajcloud.wansview.R.id.et_wifi_name);
        passwordEditText = findViewById(net.ajcloud.wansview.R.id.et_password);
        joinButton = findViewById(net.ajcloud.wansview.R.id.btn_join);
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
    public void onClickView(View v) {
        switch (v.getId()) {
            case net.ajcloud.wansview.R.id.img_left:
                finish();
                break;
            case net.ajcloud.wansview.R.id.btn_join:
                AddDeviceScanQRActivity.start(AddDeviceWifiSettingActivity.this, type);
                break;
            default:
                break;
        }
    }
}
