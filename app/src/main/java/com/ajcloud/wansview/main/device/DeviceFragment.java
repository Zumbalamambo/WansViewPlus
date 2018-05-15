package com.ajcloud.wansview.main.device;

import android.view.View;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.main.application.BaseFragment;
import com.ajcloud.wansview.support.utils.dialog.ProgressDialogManager;

import com.ajcloud.wansview.main.application.BaseFragment;
import com.ajcloud.wansview.support.utils.dialog.ProgressDialogManager;

/**
 * Created by mamengchao on 2018/05/15.
 * 设备页
 */
public class DeviceFragment extends BaseFragment {

    @Override
    public int layoutResID() {
        return com.ajcloud.wansview.R.layout.fragment_device;
    }

    @Override
    protected boolean hasTittle() {
        return false;
    }

    @Override
    protected void initTittle() {
    }

    @Override
    protected void initView(View rootView) {
        rootView.findViewById(com.ajcloud.wansview.R.id.get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialogManager.getDialogManager().showDialog("test", getActivity(), 10000);
            }
        });
    }
}
