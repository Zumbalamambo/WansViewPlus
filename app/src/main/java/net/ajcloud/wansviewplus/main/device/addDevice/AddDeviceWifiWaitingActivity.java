package net.ajcloud.wansviewplus.main.device.addDevice;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.bean.BindStatusBean;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

public class AddDeviceWifiWaitingActivity extends BaseActivity {

    private static int MSG_CHECK = 100;
    private TextView secondTextView;
    private TimeCount timeCount;
    private DeviceApiUnit deviceApiUnit;
    private String deviceId;
    private Handler checkHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (MainApplication.getApplication().getLastestActivity() == AddDeviceWifiWaitingActivity.this) {
                checkStatus();
            }
        }
    };

    public static void startBind(Context context, String deviceId) {
        Intent intent = new Intent(context, AddDeviceWifiWaitingActivity.class);
        intent.putExtra("deviceId", deviceId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device_wifi_waiting;
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
            deviceId = getIntent().getStringExtra("deviceId");
        }
        timeCount = new AddDeviceWifiWaitingActivity.TimeCount(121000, 1000);
        timeCount.start();
        deviceApiUnit = new DeviceApiUnit(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeCount.cancel();
        checkHandler.removeMessages(MSG_CHECK);
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

    private void checkStatus() {
        deviceApiUnit.getBindStatus(deviceId, new OkgoCommonListener<BindStatusBean>() {
            @Override
            public void onSuccess(BindStatusBean bean) {
                if (bean != null) {
                    if (bean.status == 0) {
                        checkHandler.removeMessages(MSG_CHECK);
                        checkHandler.sendEmptyMessageDelayed(MSG_CHECK, 5000);
                    } else if (bean.status == 1) {
                        // success
                        doSuccess();
                    } else if (bean.status == 2) {
                        // fail
                        Intent intent = new Intent(AddDeviceWifiWaitingActivity.this, AddDeviceFailActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.single("checkStatus,error");
            }
        });
    }

    private void doSuccess() {
        MainApplication.getApplication().getDeviceCache().add(new Camera(deviceId, null));
        AddDeviceSuccessActivity.start(AddDeviceWifiWaitingActivity.this, deviceId);
        finish();
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
            Intent intent = new Intent(AddDeviceWifiWaitingActivity.this, AddDeviceFailActivity.class);
            startActivity(intent);
            finish();
        }
    }
}