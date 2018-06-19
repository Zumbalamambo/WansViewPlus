package net.ajcloud.wansviewplus.main.device.setting;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.api.UserApiUnit;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.customview.materialEditText.MaterialEditText;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import static net.ajcloud.wansviewplus.main.device.setting.DeviceSettingActivity.RENAME;

public class DeviceSettingNameActivity extends BaseActivity {

    private static String LOADING = "LOADING";
    private MaterialEditText nameEditText;
    private UserApiUnit userApiUnit;
    private DeviceApiUnit deviceApiUnit;
    private Camera camera;
    private String deviceId;
    private String name;

    public static void startForResult(Activity activity, String deviceId, String name) {
        Intent intent = new Intent(activity, DeviceSettingNameActivity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("name", name);
        activity.startActivityForResult(intent, RENAME);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_setting_name;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Camera name");
        getToolbar().setLeftImg(R.mipmap.icon_back);
        getToolbar().setRightText("Save");
        getToolbar().setRightTextColor(getResources().getColor(R.color.colorPrimary));

        nameEditText = findViewById(R.id.et_name);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            name = getIntent().getStringExtra("name");
            camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
            if (!TextUtils.isEmpty(name)) {
                nameEditText.setText(name);
                nameEditText.setSelection(name.length());
            }
        }
        userApiUnit = new UserApiUnit(this);
        deviceApiUnit = new DeviceApiUnit(this);

        nameEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void onClickView(View v) {
        super.onClickView(v);
        switch (v.getId()) {
            case R.id.btn_right:
                doChangeName();
                break;
            default:
                break;
        }
    }

    private void doChangeName() {
        final String name = nameEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("cant be empty");
        } else {
            progressDialogManager.showDialog(LOADING, this);
            deviceApiUnit.setName(camera.getGatewayUrl(), deviceId, name, new OkgoCommonListener<Object>() {
                @Override
                public void onSuccess(Object bean) {
                    userApiUnit.pushSetting("upsert", null, name, new OkgoCommonListener<Object>() {
                        @Override
                        public void onSuccess(Object bean) {
                            deviceApiUnit.setNameUac(deviceId, name, new OkgoCommonListener<Object>() {
                                @Override
                                public void onSuccess(Object bean) {
                                    progressDialogManager.dimissDialog(LOADING, 0);
                                    camera.aliasName = name;
                                    Intent intent = new Intent();
                                    intent.putExtra("name", nameEditText.getText().toString());
                                    setResult(RESULT_OK, intent);
                                    finish();
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
    }
}
