package net.ajcloud.wansview.main.device.addDevice;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.core.bean.DeviceSearchBean;

public class AddDeviceWaitingActivity extends BaseActivity {

    private TextView secondTextView;
    private TimeCount timeCount;
    private DeviceSearchBean deviceSearchBean;

    public static void startBind(Context context, DeviceSearchBean bean) {
        Intent intent = new Intent(context, AddDeviceWaitingActivity.class);
        intent.putExtra("DeviceSearchBean", bean);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device_waiting;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Wait for connection");
        secondTextView = findViewById(R.id.tv_second);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            deviceSearchBean = getIntent().getParcelableExtra("DeviceSearchBean");
        }

        if (deviceSearchBean != null) {
            startBind();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeCount.cancel();
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.left_img:
                finish();
                break;
            default:
                break;
        }
    }

    private void startBind() {
        timeCount = new TimeCount(121000, 1000);
        timeCount.start();
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
