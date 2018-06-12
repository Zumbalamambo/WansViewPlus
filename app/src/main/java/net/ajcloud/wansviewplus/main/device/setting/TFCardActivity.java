package net.ajcloud.wansviewplus.main.device.setting;

import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;

public class TFCardActivity extends BaseActivity {

    private SwitchCompat storageSwitch, recordSwitch;
    private RelativeLayout timeLayout, qualityLayout;
    private TextView timeTextView, qualityTextView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tfcard;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("TFcard storage");
        getToolbar().setLeftImg(R.mipmap.icon_back);

        storageSwitch = findViewById(R.id.item_storage_switch);
        recordSwitch = findViewById(R.id.item_record_switch);
        timeLayout = findViewById(R.id.item_time);
        qualityLayout = findViewById(R.id.item_quality);
        timeTextView = findViewById(R.id.item_time_time);
        qualityTextView = findViewById(R.id.item_quality_time);
    }

    @Override
    protected void initListener() {
        timeLayout.setOnClickListener(this);
        qualityLayout.setOnClickListener(this);
        storageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        recordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.item_time:
                startActivity(new Intent(TFCardActivity.this, DetectionTimeActivity.class));
                break;
            case R.id.item_quality:
                break;
            default:
                break;
        }
    }
}
