package net.ajcloud.wansviewplus.main.device.setting;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.device.Camera;

public class DeviceSettingInfoActivity extends BaseActivity {

    private RelativeLayout updateLayout;
    private TextView idTextView, versionTextView, ipTextView, macTextView;
    private ImageView versionImageView;
    private String deviceId;
    private Camera camera;

    public static void start(Context context, String deviceId) {
        Intent intent = new Intent(context, DeviceSettingInfoActivity.class);
        intent.putExtra("deviceId", deviceId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_setting_info;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle(getResources().getString(R.string.device_setting_information));
        getToolbar().setLeftImg(R.mipmap.ic_back);

        updateLayout = findViewById(R.id.item_id);
        idTextView = findViewById(R.id.item_id_id);
        versionTextView = findViewById(R.id.item_firmware_firmware);
        ipTextView = findViewById(R.id.item_ip_ip);
        macTextView = findViewById(R.id.item_mac_mac);
        versionImageView = findViewById(R.id.item_firmware_update);
    }

    @Override
    protected void initListener() {
        super.initListener();
        updateLayout.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
        }
        if (camera != null){
            idTextView.setText(camera.deviceId);
            versionTextView.setText(camera.fwVersion);
            if (camera.networkConfig != null){
                ipTextView.setText(camera.networkConfig.localIp);
                macTextView.setText(camera.networkConfig.ethMac);
            }
        }
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.item_id:
                break;
            default:
                break;
        }
    }
}
