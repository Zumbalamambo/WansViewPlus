package net.ajcloud.wansviewplus.main.device.addDevice;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.entity.CapabilityInfo;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.device.addDevice.cable.AddDeviceCableConfirmActivity;
import net.ajcloud.wansviewplus.main.device.addDevice.wifi.AddDeviceCameraSettingActivity;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;

public class AddDeviceSelectActivity extends BaseActivity {

    private static String LOADING = "LOADING";
    private RelativeLayout k3Layout;
    private RelativeLayout q3Layout;

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
        getToolbar().setTittle(getResources().getString(R.string.add_device_select_type));
        getToolbar().setLeftImg(R.mipmap.ic_back);

        k3Layout = findViewById(R.id.rl_k3);
        q3Layout = findViewById(R.id.rl_q3);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        k3Layout.setOnClickListener(this);
        q3Layout.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        if (v.getId() != R.id.img_left) {
//            progressDialogManager.showDialog(LOADING, this);
            String type = null;
            switch (v.getId()) {
                case R.id.rl_k3:
                    type = "K3S";
                    break;
                case R.id.rl_q3:
                    type = "Q3S";
                    break;
                default:
                    break;
            }
            AddDeviceModeActivity.start(AddDeviceSelectActivity.this);
//            new DeviceApiUnit(AddDeviceSelectActivity.this).getCapability(type, new OkgoCommonListener<CapabilityInfo>() {
//                @Override
//                public void onSuccess(CapabilityInfo bean) {
//                    progressDialogManager.dimissDialog(LOADING, 0);
//                    //TODO
//                    if (bean != null) {
//                        String[] networkConfigs = bean.getNetworkConfigs();
//                        if (networkConfigs != null) {
//                            if (networkConfigs.length == 2) {
//                                AddDeviceModeActivity.start(AddDeviceSelectActivity.this);
//                            } else if (networkConfigs.length == 1) {
//                                if (TextUtils.equals(networkConfigs[0], "qr")) {
//                                    AddDeviceCameraSettingActivity.start(AddDeviceSelectActivity.this);
//                                } else if (TextUtils.equals(networkConfigs[0], "eth")) {
//                                    startActivity(new Intent(AddDeviceSelectActivity.this, AddDeviceCableConfirmActivity.class));
//                                }
//                            }
//                        }
//                    }
//
//
//                }
//
//                @Override
//                public void onFail(int code, String msg) {
//                    progressDialogManager.dimissDialog(LOADING, 0);
//                }
//            });
        }
    }
}
