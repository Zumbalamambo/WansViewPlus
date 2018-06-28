package net.ajcloud.wansviewplus.main.device.addDevice.cable;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.main.device.addDevice.AddDeviceSuccessActivity;
import net.ajcloud.wansviewplus.main.home.HomeActivity;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.bean.BindStatusBean;
import net.ajcloud.wansviewplus.support.core.bean.DeviceBindBean;
import net.ajcloud.wansviewplus.support.core.bean.DeviceSearchBean;
import net.ajcloud.wansviewplus.support.core.bean.PreBindBean;
import net.ajcloud.wansviewplus.support.core.socket.CableConnectionUnit;
import net.ajcloud.wansviewplus.support.tools.WLog;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import java.util.Arrays;

public class AddDeviceCableWaitingActivity extends BaseActivity {

    private static int MSG_CHECK = 100;
    private static int SOCKET_TIMEOUT = 5000;
    private TextView secondTextView;
    private TimeCount timeCount;
    private DeviceSearchBean deviceSearchBean;
    private CableConnectionUnit cableConnectionUnit;
    private DeviceApiUnit deviceApiUnit;
    private Handler checkHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (MainApplication.getApplication().getLastestActivity() == AddDeviceCableWaitingActivity.this) {
                checkStatus();
            }
        }
    };

    public static void startBind(Context context, DeviceSearchBean bean) {
        Intent intent = new Intent(context, AddDeviceCableWaitingActivity.class);
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
        timeCount = new TimeCount(121000, 1000);
        deviceApiUnit = new DeviceApiUnit(this);
        cableConnectionUnit = new CableConnectionUnit();
        cableConnectionUnit.setSocketTimeout(SOCKET_TIMEOUT);
        if (deviceSearchBean != null) {
            startBind();
        }
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

    private void startBind() {
        timeCount.start();
        preBind();
    }

    private void preBind() {
        deviceApiUnit.preBind(new OkgoCommonListener<PreBindBean>() {
            @Override
            public void onSuccess(PreBindBean bean) {
                if (bean != null && !TextUtils.isEmpty(bean.token)) {
                    bind(bean.token);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.single(msg);
                if (code == 1201) {
                    startActivity(new Intent(AddDeviceCableWaitingActivity.this, HomeActivity.class));
                }
            }
        });
    }

    private void bind(String authCode) {
        WLog.d(TAG, "ip:" + deviceSearchBean.getSzIpAddr() + "\tdeviceId:" + deviceSearchBean.getDeviceID() + "\ttoken:" + Arrays.toString(authCode.getBytes()));
        cableConnectionUnit.startBind(deviceSearchBean.getSzIpAddr(), deviceSearchBean.getDeviceIDBytes(), authCode.getBytes(), new CableConnectionUnit.BindDeviceCallback() {
            @Override
            public void success(DeviceBindBean bean) {
                if (bean == null) {
                    checkHandler.removeMessages(MSG_CHECK);
                    checkHandler.sendEmptyMessage(MSG_CHECK);
                } else {
                    if (TextUtils.equals("0", bean.nErrorCode)) {
                        //success
                        doSuccess();
                    } else {
                        Intent intent = new Intent(AddDeviceCableWaitingActivity.this, AddDeviceCableFailActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void overTime() {
                checkHandler.removeMessages(MSG_CHECK);
                checkHandler.sendEmptyMessage(MSG_CHECK);
            }
        });
    }

    private void checkStatus() {
        deviceApiUnit.getBindStatus(deviceSearchBean.getDeviceID(), new OkgoCommonListener<BindStatusBean>() {
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
                        Intent intent = new Intent(AddDeviceCableWaitingActivity.this, AddDeviceCableFailActivity.class);
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
        AddDeviceSuccessActivity.start(AddDeviceCableWaitingActivity.this, deviceSearchBean.getDeviceID());
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
            Intent intent = new Intent(AddDeviceCableWaitingActivity.this, AddDeviceCableFailActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
