package com.ajcloud.wansview.main.device.addDevice;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.main.application.BaseActivity;

public class AddDeviceScanQRActivity extends BaseActivity {

    private Button waitButton;
    private TextView soundTextView;
    private String type;

    public static void start(Context context, String type) {
        Intent intent = new Intent(context, AddDeviceScanQRActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device_scan_qr;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Connect to network");
        getToolbar().setLeftImg(R.mipmap.icon_back);
        waitButton = findViewById(R.id.btn_sure);
        soundTextView = findViewById(R.id.tv_sound);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            type = getIntent().getStringExtra("type");
        }
    }

    @Override
    protected void initListener() {
        waitButton.setOnClickListener(this);
        soundTextView.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.img_left:
                finish();
                break;
            case R.id.btn_sure:
                startActivity(new Intent(AddDeviceScanQRActivity.this, AddDeviceWaitingActivity.class));
                break;
            case R.id.tv_sound:
                AddDeviceSoundActivity.start(AddDeviceScanQRActivity.this, type);
                break;
            default:
                break;
        }
    }
}
