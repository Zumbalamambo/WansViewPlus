package com.ajcloud.wansview.main.device.addDevice;

import android.view.View;
import android.widget.Button;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.main.application.BaseActivity;
import com.ajcloud.wansview.support.customview.materialEditText.MaterialEditText;

public class AddDeviceSuccessActivity extends BaseActivity {

    private MaterialEditText nameEditText;
    private Button okButton;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device_success;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Successfully added device");
        getToolbar().setLeftImg(R.mipmap.icon_back);
        nameEditText = findViewById(R.id.et_name);
        okButton = findViewById(R.id.btn_ok);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        okButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_left:
                finish();
                break;
            case R.id.btn_ok:
                break;
            default:
                break;
        }
    }
}
