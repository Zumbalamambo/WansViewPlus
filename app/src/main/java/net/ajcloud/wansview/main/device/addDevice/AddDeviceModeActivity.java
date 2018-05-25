package net.ajcloud.wansview.main.device.addDevice;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;

import net.ajcloud.wansview.main.application.BaseActivity;

public class AddDeviceModeActivity extends BaseActivity {

    private Button wifiButton;
    private Button cableButton;
    private String type;

    public static void start(Context context, String type) {
        Intent intent = new Intent(context, AddDeviceModeActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return net.ajcloud.wansview.R.layout.activity_add_device_mode;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Select the connection mode");
        getToolbar().setLeftImg(net.ajcloud.wansview.R.mipmap.icon_back);
        wifiButton = findViewById(net.ajcloud.wansview.R.id.btn_wifi_select);
        cableButton = findViewById(net.ajcloud.wansview.R.id.btn_cable_select);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            type = getIntent().getStringExtra("type");
        }
    }

    @Override
    protected void initListener() {
        wifiButton.setOnClickListener(this);
        cableButton.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case net.ajcloud.wansview.R.id.img_left:
                finish();
                break;
            case net.ajcloud.wansview.R.id.btn_wifi_select:
                if (!TextUtils.isEmpty(type)){
                    AddDeviceCameraSettingActivity.start(AddDeviceModeActivity.this, type);
                }
                break;
            case net.ajcloud.wansview.R.id.btn_cable_select:
                break;
            default:
                break;
        }
    }
}
