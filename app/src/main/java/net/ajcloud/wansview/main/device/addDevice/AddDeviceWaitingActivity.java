package net.ajcloud.wansview.main.device.addDevice;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import net.ajcloud.wansview.main.application.BaseActivity;

public class AddDeviceWaitingActivity extends BaseActivity {

    private TextView secondTextView;
    private TimeCount timeCount;

    @Override
    protected int getLayoutId() {
        return net.ajcloud.wansview.R.layout.activity_add_device_waiting;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Wait for connection");
        secondTextView = findViewById(net.ajcloud.wansview.R.id.tv_second);
    }

    @Override
    protected void initData() {
        timeCount = new TimeCount(120000, 1000);
        timeCount.start();
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeCount.cancel();
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case net.ajcloud.wansview.R.id.left_img:
                finish();
                break;
            default:
                break;
        }
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            secondTextView.setText(String.valueOf(millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {

        }
    }
}
