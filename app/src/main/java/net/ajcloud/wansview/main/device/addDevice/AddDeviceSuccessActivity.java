package net.ajcloud.wansview.main.device.addDevice;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.main.application.MainApplication;
import net.ajcloud.wansview.main.home.HomeActivity;
import net.ajcloud.wansview.support.core.api.DeviceApiUnit;
import net.ajcloud.wansview.support.core.api.OkgoCommonListener;
import net.ajcloud.wansview.support.core.device.Camera;
import net.ajcloud.wansview.support.core.device.DeviceInfoDictionary;
import net.ajcloud.wansview.support.customview.materialEditText.MaterialEditText;
import net.ajcloud.wansview.support.utils.ToastUtil;

public class AddDeviceSuccessActivity extends BaseActivity {

    private MaterialEditText nameEditText;
    private Button okButton;

    private DeviceApiUnit deviceApiUnit;
    private Camera camera;
    private String deviceId;

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
        nameEditText.setText(DeviceInfoDictionary.getNameByDevice(camera));
    }

    @Override
    protected void initListener() {
        okButton.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.img_left:
                finish();
                break;
            case R.id.btn_ok:
                changeName();
                break;
            default:
                break;
        }
    }

    private void changeName() {
        String name = nameEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("cant be empty");
            return;
        }
        deviceApiUnit.setName(camera.getGatewayUrl(), deviceId, name, new OkgoCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                ToastUtil.single("success");
                startActivity(new Intent(AddDeviceSuccessActivity.this, HomeActivity.class));
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.single("setName error");
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}
