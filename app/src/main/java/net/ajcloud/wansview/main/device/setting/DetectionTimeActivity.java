package net.ajcloud.wansview.main.device.setting;

import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;

public class DetectionTimeActivity extends BaseActivity {

    public static String PERIOD_ONE = "1";
    public static String PERIOD_TWO = "2";
    private SwitchCompat alltimeSwitch;
    private RelativeLayout periodOneLayout, periodTwoLayout;
    private TextView periodOneTextView, periodTwoTextView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detection_time;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Detection time");
        getToolbar().setLeftImg(R.mipmap.icon_back);

        alltimeSwitch = findViewById(R.id.item_all_time_switch);
        periodOneLayout = findViewById(R.id.item_period_one);
        periodTwoLayout = findViewById(R.id.item_period_two);
        periodOneTextView = findViewById(R.id.item_period_one_time);
        periodTwoTextView = findViewById(R.id.item_period_two_time);
    }

    @Override
    protected void initListener() {
        periodOneLayout.setOnClickListener(this);
        periodTwoLayout.setOnClickListener(this);
        alltimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.item_period_one:
                TimePeriodActivity.start(DetectionTimeActivity.this, PERIOD_ONE);
                break;
            case R.id.item_period_two:
                TimePeriodActivity.start(DetectionTimeActivity.this, PERIOD_TWO);
                break;
            default:
                break;
        }
    }
}
