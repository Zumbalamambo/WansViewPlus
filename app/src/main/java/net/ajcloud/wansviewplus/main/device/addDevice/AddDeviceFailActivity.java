package net.ajcloud.wansviewplus.main.device.addDevice;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;

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
