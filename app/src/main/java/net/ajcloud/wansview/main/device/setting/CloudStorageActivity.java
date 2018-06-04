package net.ajcloud.wansview.main.device.setting;

import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;

public class CloudStorageActivity extends BaseActivity {

    private SwitchCompat storageSwitch;
    private RelativeLayout timeLayout, qualityLayout, planOneLayout, planTwoLayout;
    private TextView timeTextView, qualityTextView, planOneTextView, planTwoTextView, periodOneTextView, periodTwoTextView;
    private Button expireButton, replenishButton;


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
