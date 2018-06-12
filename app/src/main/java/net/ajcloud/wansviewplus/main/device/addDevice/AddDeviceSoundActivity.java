package net.ajcloud.wansviewplus.main.device.addDevice;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.smarteye.SEAT_API;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.customview.dialog.CommonDialog;
import net.ajcloud.wansviewplus.support.tools.WLog;
import net.ajcloud.wansviewplus.support.utils.DisplayUtil;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import java.util.List;

public class AddDeviceSoundActivity extends BaseActivity implements SEAT_API.IMsgConfig {

    private Button sendButton;
    private Button nextButton;
    private TextView errorTextView;
    private CommonDialog noSoundDialog;

    private SEAT_API seat_api;
    private WifiManager wifiManager;
    private String type;
    private String ssid;
    private String pwd;
    int[] handleAudioT = new int[1];

    public static void start(Context context, String type, String ssid, String pwd) {
        Intent intent = new Intent(context, AddDeviceSoundActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("ssid", ssid);
        intent.putExtra("pwd", pwd);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device_sound;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Connect to network");
        getToolbar().setLeftImg(R.mipmap.icon_back);
        sendButton = findViewById(R.id.btn_send);
        nextButton = findViewById(R.id.btn_next);
        errorTextView = findViewById(R.id.tv_error);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            type = getIntent().getStringExtra("type");
            ssid = getIntent().getStringExtra("ssid");
            pwd = getIntent().getStringExtra("pwd");
        }
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        seat_api = new SEAT_API();
        seat_api.setInterfaceConfigOK(this);
        seat_api.SEAT_Init(SEAT_API.SAMPLE_RATE_441, SEAT_API.TRANSMIT_TYPE_WIFI_PWD);
        int nRet = seat_api.SEAT_Create(handleAudioT, SEAT_API.AT_PLAYER, SEAT_API.CPU_PRIORITY);
        if (nRet >= 0) {
            seat_api.SEAT_SetCallback(handleAudioT[0], 100);
            seat_api.SEAT_SetCallbackConfigOK(1);
        }
    }

    @Override
    protected void initListener() {
        sendButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        errorTextView.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        seat_api.SEAT_SmartlinkStop();
        seat_api.SEAT_Destroy(handleAudioT);
        seat_api.SEAT_DeInit();
        super.onDestroy();
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                //configure Wi-Fi of the device by Smartlink technology
                byte[] bytsWifi = ssid.getBytes();
                byte[] bytsPwd = pwd.getBytes();
                String strTips = String.format("Configuring(%s,%s)...", ssid, pwd);
                ToastUtil.single(strTips);
                WLog.d(TAG, "voice] " + strTips);

                for (int i = 0; i < 2; i++) {
                    seat_api.SEAT_Start(handleAudioT[0]);
//                    int nMode = getCipherType(ssid);
//                    int nWrite = seat_api.SEAT_WriteSSIDWiFi2(handleAudioT[0], 2, bytsWifi, bytsWifi.length,
//                            bytsPwd, bytsPwd.length,
//                            SEAT_API.WIFI_SECURITY_MODE_UNKNOWN, null, //WIFI_SECURITY_MODE_WPAPSK_TKIP, nMode value by getCipherType(MainActivity.this, strCurWiFiSSID);
//                            null, 0);
                    int nWrite = seat_api.SEAT_WriteByte(handleAudioT[0], bytsWifi, bytsWifi.length);
                    WLog.d(TAG, "voice]  SEAT_WriteSSIDWiFi2(.)=" + nWrite);
                    seat_api.SEAT_Stop(handleAudioT[0]);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_next:
                startActivity(new Intent(AddDeviceSoundActivity.this, AddDeviceWaitingActivity.class));
                break;
            case R.id.tv_error:
                showDialog();
                break;
            default:
                break;
        }
    }

    private void showDialog() {
        if (noSoundDialog == null) {
            noSoundDialog = new CommonDialog.Builder(this)
                    .canceledOnTouchOutside(false)
                    .view(R.layout.dialog_no_sound)
                    .height(DisplayUtil.dip2Pix(this, 130))
                    .width(DisplayUtil.dip2Pix(this, 100))
                    .addViewOnclickListener(R.id.iv_close, dialogClickListener)
                    .build();
        }
        if (!noSoundDialog.isShowing()) {
            noSoundDialog.show();
        }
    }

    private View.OnClickListener dialogClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_close:
                    if (noSoundDialog != null && noSoundDialog.isShowing()) {
                        noSoundDialog.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public int getCipherType(String strCurWiFiSSID) {
        List<ScanResult> list = wifiManager.getScanResults();
        if (list == null) return SEAT_API.WIFI_SECURITY_MODE_UNKNOWN;
        for (ScanResult scResult : list) {
            if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(strCurWiFiSSID)) {
                return getSecurityType(scResult.capabilities);
            }
        }
        return SEAT_API.WIFI_SECURITY_MODE_UNKNOWN;
    }

    private int getSecurityType(String caps) {
        if (caps.contains("WPA2") && caps.contains("PSK") && caps.contains("TKIP")) {
            return SEAT_API.WIFI_SECURITY_MODE_WPA2PSK_TKIP;

        } else if (caps.contains("WPA2") && caps.contains("PSK")) {
            return SEAT_API.WIFI_SECURITY_MODE_WPA2PSK_AES;

        } else if (caps.contains("WPA") && caps.contains("PSK") && caps.contains("TKIP")) {
            return SEAT_API.WIFI_SECURITY_MODE_WPAPSK_TKIP;

        } else if (caps.contains("WPA") && caps.contains("PSK")) {
            return SEAT_API.WIFI_SECURITY_MODE_WPAPSK_AES;

        } else if (caps.contains("WEP")) {
            return SEAT_API.WIFI_SECURITY_MODE_WEP_128_HEX;

        } else {
            return SEAT_API.WIFI_SECURITY_MODE_OPEN;
        }
    }

    @Override
    public void configOK(String strMsg) {
        WLog.d(TAG, strMsg);
    }

    @Override
    public void onBegin() {
        WLog.d(TAG, "onBegin");
    }

    @Override
    public void onEnd() {
        WLog.d(TAG, "onEnd");
    }
}
