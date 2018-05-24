package com.ajcloud.wansview.main.device.addDevice;

import android.view.View;
import android.widget.RelativeLayout;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.main.application.BaseActivity;
import com.ajcloud.wansview.support.utils.ConstantsUtil;

public class AddDeviceSelectActivity extends BaseActivity {

    private RelativeLayout k3Layout;
    private RelativeLayout q3Layout;
    private RelativeLayout w2Layout;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device_select;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Selection of device type");
        getToolbar().setLeftImg(R.mipmap.icon_back);

        k3Layout = findViewById(R.id.rl_k3);
        q3Layout = findViewById(R.id.rl_q3);
        w2Layout = findViewById(R.id.rl_w2);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        k3Layout.setOnClickListener(this);
        q3Layout.setOnClickListener(this);
        w2Layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_left:
                finish();
                break;
            case R.id.rl_k3:
                AddDeviceModeActivity.start(AddDeviceSelectActivity.this, ConstantsUtil.DEVICE_TYPE_K3);
                break;
            case R.id.rl_q3:
                AddDeviceModeActivity.start(AddDeviceSelectActivity.this, ConstantsUtil.DEVICE_TYPE_Q3);
                break;
            case R.id.rl_w2:
                AddDeviceModeActivity.start(AddDeviceSelectActivity.this, ConstantsUtil.DEVICE_TYPE_W2);
                break;
            default:
                break;
        }
    }
}
