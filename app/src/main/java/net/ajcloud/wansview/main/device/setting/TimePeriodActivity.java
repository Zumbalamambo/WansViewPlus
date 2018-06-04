package net.ajcloud.wansview.main.device.setting;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;

public class TimePeriodActivity extends BaseActivity {

    private TextView periodTipsTextView;
    private SwitchCompat periodSwitch;
    private RelativeLayout startLayout, endLayout;
    private TextView startTextView, endTextView;
    private String period;

    public static void start(Context context, String period) {
        Intent intent = new Intent(context, TimePeriodActivity.class);
        intent.putExtra("period", period);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_time_period;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setLeftImg(R.mipmap.icon_back);
        periodTipsTextView = findViewById(R.id.item_switch_time_tips);
        periodSwitch = findViewById(R.id.item_switch_switch);
        startLayout = findViewById(R.id.item_start);
        endLayout = findViewById(R.id.item_end);
        startTextView = findViewById(R.id.item_start_time);
        endTextView = findViewById(R.id.item_end_time);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            period = getIntent().getStringExtra("period");
        }
        if (TextUtils.equals(period, "1")) {
            getToolbar().setTittle("Time Period 1");
            periodTipsTextView.setText("Time Period 1");
        } else if (TextUtils.equals(period, "2")) {
            getToolbar().setTittle("Time Period 2");
            periodTipsTextView.setText("Time Period 2");
        }
    }

    @Override
    protected void initListener() {
        startLayout.setOnClickListener(this);
        endLayout.setOnClickListener(this);
        periodSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }
}
