package net.ajcloud.wansviewplus.main.device.addDevice;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.main.home.HomeActivity;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.bean.DeviceConfigBean;
import net.ajcloud.wansviewplus.support.core.bean.DeviceUrlBean;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.customview.materialEditText.MaterialEditText;
import net.ajcloud.wansviewplus.support.event.DeviceBindSuccessEvent;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class AddDeviceSuccessActivity extends BaseActivity {

    private MaterialEditText nameEditText;
    private Button okButton;

    private DeviceApiUnit deviceApiUnit;
    private String deviceId;
    private Camera camera;
    private boolean changeName = true;

    public static void start(Context context, String deviceId) {
        Intent intent = new Intent(context, AddDeviceSuccessActivity.class);
        intent.putExtra("deviceId", deviceId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device_success;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Successfully added device");
        getToolbar().setRightImg(R.drawable.ic_arrow);
        nameEditText = findViewById(R.id.et_name);
        okButton = findViewById(R.id.btn_ok);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
        }
        deviceApiUnit = new DeviceApiUnit(this);
    }

    @Override
    protected void initListener() {
        okButton.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.img_right:
                changeName = false;
                getCameraUrl();
                break;
            case R.id.btn_ok:
                changeName = true;
                getCameraUrl();
                break;
            default:
                break;
        }
    }

    private void changeName() {
        final String name = nameEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("cant be empty");
            return;
        }
        if (camera == null) {
            nameEditText.setError("cant be empty");
            return;
        }
        deviceApiUnit.setName(camera.getGatewayUrl(), deviceId, name, new OkgoCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                deviceApiUnit.setName(camera.getEmcUrl(), deviceId, name, new OkgoCommonListener<Object>() {
                    @Override
                    public void onSuccess(Object bean) {
                        deviceApiUnit.setNameUac(deviceId, name, new OkgoCommonListener<Object>() {
                            @Override
                            public void onSuccess(Object bean) {
                                camera.aliasName = name;
                                EventBus.getDefault().post(new DeviceBindSuccessEvent(deviceId));
                                startActivity(new Intent(AddDeviceSuccessActivity.this, HomeActivity.class));
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                ToastUtil.single("setNameUac error");
                            }
                        });
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        ToastUtil.single("setNameEmc error");
                    }
                });
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.single("setNameGateway error");
            }
        });

    }

    private void getCameraUrl() {
        List<String> deviceIds = new ArrayList<>();
        deviceIds.add(deviceId);
        deviceApiUnit.getDeviceUrlInfo(deviceIds, new OkgoCommonListener<List<DeviceUrlBean.UrlInfo>>() {
            @Override
            public void onSuccess(List<DeviceUrlBean.UrlInfo> bean) {
                if (bean != null && bean.size() > 0) {
                    for (DeviceUrlBean.UrlInfo info : bean) {
                        Camera camera = MainApplication.getApplication().getDeviceCache().get(info.deviceId);
                        if (camera != null) {
                            if (!TextUtils.isEmpty(info.gatewayUrl)) {
                                getCameraInfo(info.gatewayUrl);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.single("getCameraUrl error");
                EventBus.getDefault().post(new DeviceBindSuccessEvent(deviceId));
                startActivity(new Intent(AddDeviceSuccessActivity.this, HomeActivity.class));
            }
        });
    }

    private void getCameraInfo(String url) {
        deviceApiUnit.getDeviceInfo(url, deviceId, new OkgoCommonListener<DeviceConfigBean>() {
            @Override
            public void onSuccess(DeviceConfigBean bean) {
                if (changeName) {
                    changeName();
                }else {
                    EventBus.getDefault().post(new DeviceBindSuccessEvent(deviceId));
                    startActivity(new Intent(AddDeviceSuccessActivity.this, HomeActivity.class));
                }
            }

            @Override
            public void onFail(int code, String msg) {
                EventBus.getDefault().post(new DeviceBindSuccessEvent(deviceId));
                startActivity(new Intent(AddDeviceSuccessActivity.this, HomeActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}
