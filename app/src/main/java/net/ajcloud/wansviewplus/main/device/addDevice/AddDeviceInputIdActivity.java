package net.ajcloud.wansviewplus.main.device.addDevice;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.customview.materialEditText.MaterialEditText;

public class AddDeviceInputIdActivity extends BaseActivity {

    private MaterialEditText deviceIdEditText;
    private Button nextButton;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device_input_id;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Devise serial number");
        getToolbar().setLeftImg(R.mipmap.icon_back);
        deviceIdEditText = findViewById(R.id.editText_deviceId);
        nextButton = findViewById(R.id.btn_next);
    }

    @Override
    protected void initListener() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceId = deviceIdEditText.getText().toString();
                if (TextUtils.isEmpty(deviceId)) {
                    deviceIdEditText.setError("cant be empty");
                } else {
                    AddDeviceWifiSettingActivity.start(AddDeviceInputIdActivity.this, deviceId);
                    finish();
                }
            }
        });
    }
}
