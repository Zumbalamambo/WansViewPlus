package net.ajcloud.wansviewplus.main.device.addDevice.wifi;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.customview.materialEditText.MaterialEditText;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import java.util.List;

public class AddDeviceWifiSettingActivity extends BaseActivity {

    private MaterialEditText wifiNameEditText;
    private MaterialEditText passwordEditText;
    private Button joinButton;

    private WifiManager wifiManager;

    public static void start(Context context) {
        Intent intent = new Intent(context, AddDeviceWifiSettingActivity.class);
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
        getToolbar().setLeftImg(net.ajcloud.wansviewplus.R.mipmap.ic_back);
        wifiNameEditText = findViewById(net.ajcloud.wansviewplus.R.id.et_wifi_name);
        passwordEditText = findViewById(net.ajcloud.wansviewplus.R.id.et_password);
        joinButton = findViewById(net.ajcloud.wansviewplus.R.id.btn_join);
    }

    @Override
    protected void initData() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                String wifiName = wifiInfo.getSSID().replace("\"", "");
                if (is5G()) {
                }
                wifiNameEditText.setText(wifiName);
            }
        }

        if (TextUtils.isEmpty(wifiNameEditText.getText().toString())) {
            wifiNameEditText.requestFocus();
        } else {
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
                if (TextUtils.isEmpty(wifiNameEditText.getText().toString()) || TextUtils.isEmpty(passwordEditText.getText().toString())) {
                    ToastUtil.single("please input name and password");
                } else {
                    AddDeviceScanQRActivity.start(AddDeviceWifiSettingActivity.this, wifiNameEditText.getText().toString(), passwordEditText.getText().toString());
                }
                break;
            default:
                break;
        }
    }

    private boolean is5G() {
        int freq = 0;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
            freq = wifiInfo.getFrequency();
        } else {
            String ssid = wifiInfo.getSSID();
            if (ssid != null && ssid.length() > 2) {
                String ssidTemp = ssid.substring(1, ssid.length() - 1);
                List<ScanResult> scanResults = wifiManager.getScanResults();
                for (ScanResult scanResult : scanResults) {
                    if (scanResult.SSID.equals(ssidTemp)) {
                        freq = scanResult.frequency;
                        break;
                    }
                }
            }
        }
        return freq > 4900 && freq < 5900;
    }
}
