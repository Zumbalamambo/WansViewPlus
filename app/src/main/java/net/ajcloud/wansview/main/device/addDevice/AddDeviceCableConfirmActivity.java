package net.ajcloud.wansview.main.device.addDevice;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;

public class AddDeviceCableConfirmActivity extends BaseActivity {

    private Button nextButton;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device_cable_confirm;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Network setting");
        getToolbar().setLeftImg(R.mipmap.icon_back);
        nextButton = findViewById(R.id.btn_next);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.img_left:
                finish();
                break;
            case R.id.btn_next:
                startActivity(new Intent(AddDeviceCableConfirmActivity.this, AddDeviceDiscoveryActivity.class));
                break;
            default:
                break;
        }
    }
}
