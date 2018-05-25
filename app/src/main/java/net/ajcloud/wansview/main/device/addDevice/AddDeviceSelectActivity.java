package net.ajcloud.wansview.main.device.addDevice;

import android.view.View;
import android.widget.RelativeLayout;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.utils.ConstantsUtil;

import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.utils.ConstantsUtil;

public class AddDeviceSelectActivity extends BaseActivity {

    private RelativeLayout k3Layout;
    private RelativeLayout q3Layout;
    private RelativeLayout w2Layout;

    @Override
    protected int getLayoutId() {
        return net.ajcloud.wansview.R.layout.activity_add_device_select;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Selection of device type");
        getToolbar().setLeftImg(net.ajcloud.wansview.R.mipmap.icon_back);

        k3Layout = findViewById(net.ajcloud.wansview.R.id.rl_k3);
        q3Layout = findViewById(net.ajcloud.wansview.R.id.rl_q3);
        w2Layout = findViewById(net.ajcloud.wansview.R.id.rl_w2);
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
    public void onClickView(View v) {
        switch (v.getId()) {
            case net.ajcloud.wansview.R.id.img_left:
                finish();
                break;
            case net.ajcloud.wansview.R.id.rl_k3:
                AddDeviceModeActivity.start(AddDeviceSelectActivity.this, ConstantsUtil.DEVICE_TYPE_K3);
                break;
            case net.ajcloud.wansview.R.id.rl_q3:
                AddDeviceModeActivity.start(AddDeviceSelectActivity.this, ConstantsUtil.DEVICE_TYPE_Q3);
                break;
            case net.ajcloud.wansview.R.id.rl_w2:
                AddDeviceModeActivity.start(AddDeviceSelectActivity.this, ConstantsUtil.DEVICE_TYPE_W2);
                break;
            default:
                break;
        }
    }
}
