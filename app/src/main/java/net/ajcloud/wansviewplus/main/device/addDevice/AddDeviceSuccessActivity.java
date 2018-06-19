package net.ajcloud.wansviewplus.main.device.addDevice;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.account.SigninAccountManager;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.main.home.HomeActivity;
import net.ajcloud.wansviewplus.support.core.api.ApiConstant;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.api.UserApiUnit;
import net.ajcloud.wansviewplus.support.core.bean.DeviceConfigBean;
import net.ajcloud.wansviewplus.support.core.bean.DeviceListBean;
import net.ajcloud.wansviewplus.support.core.bean.DeviceUrlBean;
import net.ajcloud.wansviewplus.support.core.bean.ResponseBean;
import net.ajcloud.wansviewplus.support.core.callback.JsonCallback;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.core.okgo.OkGo;
import net.ajcloud.wansviewplus.support.core.okgo.model.Response;
import net.ajcloud.wansviewplus.support.customview.materialEditText.MaterialEditText;
import net.ajcloud.wansviewplus.support.event.DeviceBindSuccessEvent;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddDeviceSuccessActivity extends BaseActivity {

    private static String LOADING = "LOADING";
    private MaterialEditText nameEditText;
    private Button okButton;

    private UserApiUnit userApiUnit;
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
        }
        userApiUnit = new UserApiUnit(this);
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
                getDeviceList();
                break;
            case R.id.btn_ok:
                changeName = true;
                getDeviceList();
                break;
            default:
                break;
        }
    }

    private void getDeviceList() {
        progressDialogManager.showDialog(LOADING, this);
        JSONObject body = new JSONObject();
        try {
            JSONObject metaJson = new JSONObject();
            JSONObject dataJson = new JSONObject();
            metaJson.put("locale", MainApplication.getApplication().getLocalInfo().appLang);
            metaJson.put("localtz", MainApplication.getApplication().getLocalInfo().timeZone);
            String accessToken = SigninAccountManager.getInstance().getCurrentAccountAccessToken();
            if (!TextUtils.isEmpty(accessToken)) {
                metaJson.put("accessToken", accessToken);
            }

            body.put("meta", metaJson);
            body.put("data", dataJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkGo.<ResponseBean<DeviceListBean>>post(ApiConstant.URL_DEVICE_GET_DEVICE_LIST)
                .tag(this)
                .upJson(body)
                .execute(new JsonCallback<ResponseBean<DeviceListBean>>() {
                    @Override
                    public void onSuccess(Response<ResponseBean<DeviceListBean>> response) {
                        ResponseBean<DeviceListBean> responseBean = response.body();
                        if (responseBean.isSuccess()) {
                            DeviceListBean bean = responseBean.result;
                            if (bean.cameras != null) {
                                for (DeviceListBean.Device device : bean.cameras) {
                                    if (TextUtils.equals(deviceId, device.deviceid)) {
                                        MainApplication.getApplication().getDeviceCache().add(new Camera(device.deviceid, device.aliasname, device.accessPriKey, device.accessPubKey));
                                        getCameraUrl();
                                        return;
                                    }
                                }
                                progressDialogManager.dimissDialog(LOADING, 0);
                                ToastUtil.single("getDeviceList error");
                                EventBus.getDefault().post(new DeviceBindSuccessEvent(deviceId));
                                startActivity(new Intent(AddDeviceSuccessActivity.this, HomeActivity.class));
                            }
                        }
                    }

                    @Override
                    public void onError(Response<ResponseBean<DeviceListBean>> response) {
                        super.onError(response);
                        progressDialogManager.dimissDialog(LOADING, 0);
                        ToastUtil.single("getDeviceList error");
                        EventBus.getDefault().post(new DeviceBindSuccessEvent(deviceId));
                        startActivity(new Intent(AddDeviceSuccessActivity.this, HomeActivity.class));
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
                        camera = MainApplication.getApplication().getDeviceCache().get(info.deviceId);
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
                progressDialogManager.dimissDialog(LOADING, 0);
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
                } else {
                    progressDialogManager.dimissDialog(LOADING, 0);
                    EventBus.getDefault().post(new DeviceBindSuccessEvent(deviceId));
                    startActivity(new Intent(AddDeviceSuccessActivity.this, HomeActivity.class));
                }
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(LOADING, 0);
                EventBus.getDefault().post(new DeviceBindSuccessEvent(deviceId));
                startActivity(new Intent(AddDeviceSuccessActivity.this, HomeActivity.class));
            }
        });
    }

    private void changeName() {
        final String name = nameEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            progressDialogManager.dimissDialog(LOADING, 0);
            nameEditText.setError("cant be empty");
            return;
        }
        if (camera == null) {
            progressDialogManager.dimissDialog(LOADING, 0);
            nameEditText.setError("camera cant be empty");
            return;
        }
        deviceApiUnit.setName(camera.getGatewayUrl(), deviceId, name, new OkgoCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                userApiUnit.setPushName(deviceId, name, new OkgoCommonListener<Object>() {
                    @Override
                    public void onSuccess(Object bean) {
                        deviceApiUnit.setNameUac(deviceId, name, new OkgoCommonListener<Object>() {
                            @Override
                            public void onSuccess(Object bean) {
                                progressDialogManager.dimissDialog(LOADING, 0);
                                camera.aliasName = name;
                                EventBus.getDefault().post(new DeviceBindSuccessEvent(deviceId));
                                startActivity(new Intent(AddDeviceSuccessActivity.this, HomeActivity.class));
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                progressDialogManager.dimissDialog(LOADING, 0);
                                ToastUtil.single("setNameUac error");
                            }
                        });
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        progressDialogManager.dimissDialog(LOADING, 0);
                        ToastUtil.single("setNameEmc error");
                    }
                });
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(LOADING, 0);
                ToastUtil.single("setNameGateway error");
            }
        });

    }

    @Override
    public void onBackPressed() {
    }
}
