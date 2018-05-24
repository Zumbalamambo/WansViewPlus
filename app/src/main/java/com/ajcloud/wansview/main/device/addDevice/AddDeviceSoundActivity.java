package com.ajcloud.wansview.main.device.addDevice;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.main.application.BaseActivity;

public class AddDeviceSoundActivity extends BaseActivity {

    private Button sendButton;
    private Button nextButton;
    private TextView errorTextView;
    private String type;

    public static void start(Context context, String type) {
        Intent intent = new Intent(context, AddDeviceSoundActivity.class);
        intent.putExtra("type", type);
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
        }
    }

    @Override
    protected void initListener() {
        sendButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        errorTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_left:
                finish();
                break;
            case R.id.btn_send:
                break;
            case R.id.btn_next:
                startActivity(new Intent(AddDeviceSoundActivity.this, AddDeviceWaitingActivity.class));
                break;
            case R.id.tv_error:
                break;
            default:
                break;
        }
    }
}
