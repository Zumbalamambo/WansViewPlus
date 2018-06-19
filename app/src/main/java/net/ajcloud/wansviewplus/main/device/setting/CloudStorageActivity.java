package net.ajcloud.wansviewplus.main.device.setting;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.device.Camera;

public class CloudStorageActivity extends BaseActivity {

    private SwitchCompat storageSwitch;
    private RelativeLayout timeLayout, qualityLayout, planOneLayout, planTwoLayout;
    private TextView timeTextView, qualityTextView, planOneTextView, planTwoTextView, periodOneTextView, periodTwoTextView;
    private Button expireButton, replenishButton;
    private String deviceId;
    private Camera camera;

    public static void start(Context context, String deviceId) {
        Intent intent = new Intent(context, CloudStorageActivity.class);
        intent.putExtra("deviceId", deviceId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cloud_storage;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Cloud storage");
        getToolbar().setLeftImg(R.mipmap.icon_back);

        storageSwitch = findViewById(R.id.item_storage_switch);
        timeLayout = findViewById(R.id.item_time);
        qualityLayout = findViewById(R.id.item_quality);
        planOneLayout = findViewById(R.id.item_plan_one);
        planTwoLayout = findViewById(R.id.item_plan_two);
        timeTextView = findViewById(R.id.item_time_time);
        qualityTextView = findViewById(R.id.item_quality_time);
        planOneTextView = findViewById(R.id.item_plan_one_time);
        planTwoTextView = findViewById(R.id.item_plan_two_time);
        periodOneTextView = findViewById(R.id.item_one_time_time);
        periodTwoTextView = findViewById(R.id.item_two_time_time);
        expireButton = findViewById(R.id.btn_expire);
        replenishButton = findViewById(R.id.btn_replenish);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
        }
        if (camera != null &&camera.cloudStorConfig != null) {
            storageSwitch.setChecked(camera.cloudStorConfig.enable == 1);
        }
    }

    @Override
    protected void initListener() {
        timeLayout.setOnClickListener(this);
        qualityLayout.setOnClickListener(this);
        planOneLayout.setOnClickListener(this);
        planTwoLayout.setOnClickListener(this);
        replenishButton.setOnClickListener(this);
        storageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.item_time:
                startActivity(new Intent(CloudStorageActivity.this, DetectionTimeActivity.class));
                break;
            case R.id.item_quality:
                break;
            case R.id.item_plan_one:
                break;
            case R.id.item_plan_two:
                break;
            default:
                break;
        }
    }
}
