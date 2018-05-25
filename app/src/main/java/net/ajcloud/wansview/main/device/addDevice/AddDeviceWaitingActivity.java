package net.ajcloud.wansview.main.device.addDevice;

import android.view.View;
import android.widget.TextView;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;

import net.ajcloud.wansview.main.application.BaseActivity;

public class AddDeviceWaitingActivity extends BaseActivity {

    private TextView secondTextView;

    @Override
    protected int getLayoutId() {
        return net.ajcloud.wansview.R.layout.activity_add_device_waiting;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Wait for connection");
        secondTextView = findViewById(net.ajcloud.wansview.R.id.tv_second);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case net.ajcloud.wansview.R.id.left_img:
                finish();
                break;
            default:
                break;
        }
    }
}
