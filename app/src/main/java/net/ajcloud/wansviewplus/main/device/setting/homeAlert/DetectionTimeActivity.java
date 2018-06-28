package net.ajcloud.wansviewplus.main.device.setting.homeAlert;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.core.bean.MoveMonitorBean;

public class DetectionTimeActivity extends BaseActivity {

    private static final String LOADING = "LOADING";
    public static String PERIOD_ONE = "2";
    public static String PERIOD_TWO = "3";
    private SwitchCompat alltimeSwitch;
    private RelativeLayout periodOneLayout, periodTwoLayout;
    private TextView periodOneTextView, periodTwoTextView;
    private MoveMonitorBean cloneBean;

    public static void start(Activity context, MoveMonitorBean bean) {
        Intent intent = new Intent(context, DetectionTimeActivity.class);
        intent.putExtra("MoveMonitorBean", bean);
        context.startActivityForResult(intent, 0);
    }

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
        getToolbar().setLeftImg(R.mipmap.ic_back);

        alltimeSwitch = findViewById(R.id.item_all_time_switch);
        periodOneLayout = findViewById(R.id.item_period_one);
        periodTwoLayout = findViewById(R.id.item_period_two);
        periodOneTextView = findViewById(R.id.item_period_one_time);
        periodTwoTextView = findViewById(R.id.item_period_two_time);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            cloneBean = (MoveMonitorBean) getIntent().getSerializableExtra("MoveMonitorBean");
            refreshUI();
        }
    }

    @Override
    protected void initListener() {
        periodOneLayout.setOnClickListener(this);
        periodTwoLayout.setOnClickListener(this);
        alltimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (MoveMonitorBean.Policy policy : cloneBean.policies) {
                    if (TextUtils.equals(policy.no, "1")) {
                        if (isChecked) {
                            policy.enable = 1;
                        } else {
                            policy.enable = 0;
                        }
                        for (int i = 1; i < 8; i++) {
                            policy.weekDays.add(i);
                        }
                        break;
                    }
                }
                refreshUI();
            }
        });
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.item_period_one:
                TimePeriodActivity.start(DetectionTimeActivity.this, PERIOD_ONE, cloneBean);
                break;
            case R.id.item_period_two:
                TimePeriodActivity.start(DetectionTimeActivity.this, PERIOD_TWO, cloneBean);
                break;
            default:
                break;
        }
    }

    private void refreshUI() {
        for (MoveMonitorBean.Policy policy : cloneBean.policies) {
            if (TextUtils.equals(policy.no, "1")) {
                if (policy.enable == 1) {
                    alltimeSwitch.setChecked(true);
                    periodOneLayout.setVisibility(View.GONE);
                    periodTwoLayout.setVisibility(View.GONE);
                } else {
                    alltimeSwitch.setChecked(false);
                    periodOneLayout.setVisibility(View.VISIBLE);
                    periodTwoLayout.setVisibility(View.VISIBLE);
                    for (MoveMonitorBean.Policy item : cloneBean.policies) {
                        if (TextUtils.equals(item.no, "2")) {
                            if (item.enable == 0) {
                                periodOneTextView.setText("off");
                            } else if (!TextUtils.isEmpty(item.startTime) && !TextUtils.isEmpty(item.endTime)) {
                                StringBuilder startTime = new StringBuilder(item.startTime.substring(0, item.startTime.length() - 2));
                                StringBuilder endTime = new StringBuilder(item.endTime.substring(0, item.endTime.length() - 2));
                                periodOneTextView.setText(startTime.insert(2, ":").toString() + " - " + endTime.insert(2, ":").toString());
                            }
                        }
                        if (TextUtils.equals(item.no, "3")) {
                            if (item.enable == 0) {
                                periodTwoTextView.setText("off");
                            } else if (!TextUtils.isEmpty(item.startTime) && !TextUtils.isEmpty(item.endTime)) {
                                StringBuilder startTime = new StringBuilder(item.startTime.substring(0, item.startTime.length() - 2));
                                StringBuilder endTime = new StringBuilder(item.endTime.substring(0, item.endTime.length() - 2));
                                periodTwoTextView.setText(startTime.insert(2, ":").toString() + " - " + endTime.insert(2, ":").toString());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("MoveMonitorBean", cloneBean);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            MoveMonitorBean moveMonitorBean = (MoveMonitorBean) data.getSerializableExtra("MoveMonitorBean");
            cloneBean = moveMonitorBean;
            refreshUI();
        }
    }
}
