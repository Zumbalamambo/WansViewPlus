package net.ajcloud.wansviewplus.main.device.addDevice.wifi;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.smarteye.SEAT_API;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.customview.dialog.CommonDialog;
import net.ajcloud.wansviewplus.support.tools.WLog;

import java.util.List;

public class AddDeviceSoundActivity extends BaseActivity implements SEAT_API.IMsgConfig {

    private LottieAnimationView sendView;
    private Button nextButton;
    private TextView errorTextView;
    private CommonDialog noSoundDialog;

    private SEAT_API seat_api;
    private WifiManager wifiManager;
    private String token;
    private String ssid;
    private String pwd;
    int[] handleAudioT = new int[1];

    public static void start(Context context, String token, String ssid, String pwd) {
        Intent intent = new Intent(context, AddDeviceSoundActivity.class);
        intent.putExtra("token", token);
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
        getToolbar().setTittle(getResources().getString(R.string.add_device_connect_network));
        getToolbar().setLeftImg(R.mipmap.ic_back);
        sendView = findViewById(R.id.la_send);
        nextButton = findViewById(R.id.btn_next);
        errorTextView = findViewById(R.id.tv_error);
        sendView.enableMergePathsForKitKatAndAbove(true);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            token = getIntent().getStringExtra("token");
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
        sendView.setOnClickListener(this);
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
            case R.id.la_send:
                if (sendView.isAnimating()) {
                    return;
                }
                sendView.playAnimation();

                //configure Wi-Fi of the device by Smartlink technology
                final StringBuilder content = new StringBuilder();
                content.append("s=");
                content.append(ssid);
                content.append("\n");
                content.append("p=");
                content.append(pwd);
                content.append("\n");
                content.append("c=");
                content.append(token);
                content.append("\n");
                WLog.d(TAG, "voice] " + content.toString());

                send(content.toString());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        send(content.toString());
                    }
                }, 2000);
                break;
            case R.id.btn_next:
                AddDeviceWifiWaitingActivity.startBind(AddDeviceSoundActivity.this);
                break;
            case R.id.tv_error:
                showDialog();
                break;
            default:
                break;
        }
    }

    private void send(String content) {
        seat_api.SEAT_Start(handleAudioT[0]);
//                    int nMode = getCipherType(ssid);
//                    int nWrite = seat_api.SEAT_WriteSSIDWiFi2(handleAudioT[0], 2, bytsWifi, bytsWifi.length,
//                            bytsPwd, bytsPwd.length,
//                            SEAT_API.WIFI_SECURITY_MODE_UNKNOWN, null, //WIFI_SECURITY_MODE_WPAPSK_TKIP, nMode value by getCipherType(MainActivity.this, strCurWiFiSSID);
//                            null, 0);
        int nWrite = seat_api.SEAT_WriteByte(handleAudioT[0], content.getBytes(), content.getBytes().length);
        WLog.d(TAG, "voice]  SEAT_WriteSSIDWiFi2(.)=" + nWrite);
        seat_api.SEAT_Stop(handleAudioT[0]);
    }

    private void showDialog() {
        if (noSoundDialog == null) {
            noSoundDialog = new CommonDialog.Builder(this)
                    .canceledOnTouchOutside(false)
                    .view(R.layout.dialog_no_sound)
                    .width(327)
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
