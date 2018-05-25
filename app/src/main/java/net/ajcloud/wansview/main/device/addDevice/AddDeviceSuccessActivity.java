package net.ajcloud.wansview.main.device.addDevice;

import android.view.View;
import android.widget.Button;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.customview.materialEditText.MaterialEditText;

import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.customview.materialEditText.MaterialEditText;

public class AddDeviceSuccessActivity extends BaseActivity {

    private MaterialEditText nameEditText;
    private Button okButton;

    @Override
    protected int getLayoutId() {
        return net.ajcloud.wansview.R.layout.activity_add_device_success;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Successfully added device");
        getToolbar().setLeftImg(net.ajcloud.wansview.R.mipmap.icon_back);
        nameEditText = findViewById(net.ajcloud.wansview.R.id.et_name);
        okButton = findViewById(net.ajcloud.wansview.R.id.btn_ok);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        okButton.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case net.ajcloud.wansview.R.id.img_left:
                finish();
                break;
            case net.ajcloud.wansview.R.id.btn_ok:
                break;
            default:
                break;
        }
    }
}
