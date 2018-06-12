package net.ajcloud.wansviewplus.main.device.addDevice;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.customview.materialEditText.MaterialEditText;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

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
        return net.ajcloud.wansviewplus.R.layout.activity_add_device_wifi_setting;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Wi-Fi Setting");
        getToolbar().setLeftImg(net.ajcloud.wansviewplus.R.mipmap.icon_back);
        wifiNameEditText = findViewById(net.ajcloud.wansviewplus.R.id.et_wifi_name);
        passwordEditText = findViewById(net.ajcloud.wansviewplus.R.id.et_password);
        joinButton = findViewById(net.ajcloud.wansviewplus.R.id.btn_join);
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
            case net.ajcloud.wansviewplus.R.id.img_left:
                finish();
                break;
            case net.ajcloud.wansviewplus.R.id.btn_join:
                if (TextUtils.isEmpty(wifiNameEditText.getText().toString()) || TextUtils.isEmpty(passwordEditText.getText().toString())){
                    ToastUtil.single("please input name and password");
                }else {
                    AddDeviceScanQRActivity.start(AddDeviceWifiSettingActivity.this, type, wifiNameEditText.getText().toString(), passwordEditText.getText().toString());
                }
                break;
            default:
                break;
        }
    }
}
