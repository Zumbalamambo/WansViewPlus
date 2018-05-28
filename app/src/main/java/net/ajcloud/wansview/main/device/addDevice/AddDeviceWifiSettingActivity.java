package net.ajcloud.wansview.main.device.addDevice;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.customview.materialEditText.MaterialEditText;

public class AddDeviceWifiSettingActivity extends BaseActivity {

    private MaterialEditText wifiNameEditText;
    private MaterialEditText passwordEditText;
    private Button joinButton;

    private WifiManager wifiManager;
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
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                wifiNameEditText.setText(wifiInfo.getSSID().replace("\"", ""));
            }
        }

        if (TextUtils.isEmpty(wifiNameEditText.getText().toString())){
            wifiNameEditText.requestFocus();
        }else {
            passwordEditText.requestFocus();
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
