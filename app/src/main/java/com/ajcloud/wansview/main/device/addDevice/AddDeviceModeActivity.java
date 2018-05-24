package com.ajcloud.wansview.main.device.addDevice;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.main.application.BaseActivity;

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
            case R.id.img_left:
                finish();
                break;
            case R.id.btn_wifi_select:
                if (!TextUtils.isEmpty(type)){
                    AddDeviceCameraSettingActivity.start(AddDeviceModeActivity.this, type);
                }
                break;
            case R.id.btn_cable_select:
                break;
            default:
                break;
        }
    }
}
