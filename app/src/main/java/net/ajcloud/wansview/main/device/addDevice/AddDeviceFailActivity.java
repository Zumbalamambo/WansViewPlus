package net.ajcloud.wansview.main.device.addDevice;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;

public class AddDeviceFailActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device_fail;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Problems with settings");
    }
}
