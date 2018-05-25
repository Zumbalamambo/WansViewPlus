package net.ajcloud.wansview.main.device;

import android.content.Intent;
import android.view.View;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseFragment;
import net.ajcloud.wansview.main.device.addDevice.AddDeviceSelectActivity;

import net.ajcloud.wansview.main.application.BaseFragment;
import net.ajcloud.wansview.main.device.addDevice.AddDeviceSelectActivity;

/**
 * Created by mamengchao on 2018/05/15.
 * 设备页
 */
public class DeviceFragment extends BaseFragment {

    @Override
    public int layoutResID() {
        return net.ajcloud.wansview.R.layout.fragment_device;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initTittle() {
        setTittle("Device");
        setRightImg(net.ajcloud.wansview.R.drawable.ic_all);
    }


    @Override
    protected void initView(View rootView) {
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case net.ajcloud.wansview.R.id.right_img:
                startActivity(new Intent(getContext(), AddDeviceSelectActivity.class));
                break;
            default:
                break;
        }
    }
}
