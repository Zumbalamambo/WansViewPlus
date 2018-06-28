package net.ajcloud.wansviewplus.main.device.addDevice.cable;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;

public class AddDeviceCableFailActivity extends BaseActivity {
    private Button knowButton;
    private Button retryButton;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device_cable_fail;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Problems with settings");
        getToolbar().setLeftImg(R.mipmap.ic_back);

        knowButton = findViewById(R.id.btn_know);
        retryButton = findViewById(R.id.btn_reconnect);
    }

    @Override
    protected void initListener() {
        knowButton.setOnClickListener(this);
        retryButton.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.btn_know:
                finish();
                break;
            case R.id.btn_reconnect:
                startActivity(new Intent(AddDeviceCableFailActivity.this, AddDeviceCableConfirmActivity.class));
                break;
        }
    }
}
