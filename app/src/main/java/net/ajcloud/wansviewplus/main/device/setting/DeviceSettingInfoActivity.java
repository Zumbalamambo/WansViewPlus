package net.ajcloud.wansviewplus.main.device.setting;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;

public class DeviceSettingInfoActivity extends BaseActivity {

    private RelativeLayout updateLayout;
    private TextView idTextView, versionTextView, ipTextView, macTextView;
    private ImageView versionImageView;

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
        getToolbar().setTittle("Device information");
        getToolbar().setLeftImg(R.mipmap.icon_back);

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
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.item_id:
                break;
            default:
                break;
        }
    }
}
