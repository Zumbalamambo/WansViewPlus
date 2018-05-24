package com.ajcloud.wansview.main.device;

import android.content.Intent;
import android.view.View;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.main.application.BaseFragment;
import com.ajcloud.wansview.main.device.addDevice.AddDeviceSelectActivity;

/**
 * Created by mamengchao on 2018/05/15.
 * 设备页
 */
public class DeviceFragment extends BaseFragment {

    @Override
    public int layoutResID() {
        return R.layout.fragment_device;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initTittle() {
        setTittle("Device");
        setRightImg(R.drawable.ic_all);
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
            case R.id.right_img:
                startActivity(new Intent(getContext(), AddDeviceSelectActivity.class));
                break;
            default:
                break;
        }
    }
}
