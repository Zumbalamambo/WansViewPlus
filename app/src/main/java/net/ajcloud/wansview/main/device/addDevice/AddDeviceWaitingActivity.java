package net.ajcloud.wansview.main.device.addDevice;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.main.application.MainApplication;
import net.ajcloud.wansview.support.core.api.DeviceApiUnit;
import net.ajcloud.wansview.support.core.api.OkgoCommonListener;
import net.ajcloud.wansview.support.core.bean.BindStatusBean;
import net.ajcloud.wansview.support.core.bean.DeviceBindBean;
import net.ajcloud.wansview.support.core.bean.DeviceSearchBean;
import net.ajcloud.wansview.support.core.bean.DeviceUrlBean;
import net.ajcloud.wansview.support.core.bean.PreBindBean;
import net.ajcloud.wansview.support.core.device.Camera;
import net.ajcloud.wansview.support.core.socket.CableConnectionUnit;
import net.ajcloud.wansview.support.tools.WLog;
import net.ajcloud.wansview.support.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AddDeviceWaitingActivity extends BaseActivity {

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
            checkStatus();
        }
    };

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
        timeCount = new TimeCount(121000, 1000);
        timeCount.start();
        preBind();
    }

    private void preBind() {
        deviceApiUnit.preBind(deviceSearchBean.getDeviceID(), new OkgoCommonListener<PreBindBean>() {
            @Override
            public void onSuccess(PreBindBean bean) {
                if (bean != null && !TextUtils.isEmpty(bean.token)) {
                    bind(bean.token);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.single("preBind,error");
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
                        MainApplication.getApplication().getDeviceCache().add(new Camera(deviceSearchBean.getDeviceID()));
                        getCameraUrl();
                    } else {
                        Intent intent = new Intent(AddDeviceWaitingActivity.this, AddDeviceFailActivity.class);
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
                        MainApplication.getApplication().getDeviceCache().add(new Camera(deviceSearchBean.getDeviceID()));
                        getCameraUrl();
                    } else if (bean.status == 2) {
                        // fail
                        Intent intent = new Intent(AddDeviceWaitingActivity.this, AddDeviceFailActivity.class);
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

    private void getCameraUrl() {
        List<String> deviceIds = new ArrayList<>();
        deviceIds.add(deviceSearchBean.getDeviceID());
        deviceApiUnit.getDeviceUrlInfo(deviceIds, new OkgoCommonListener<List<DeviceUrlBean.UrlInfo>>() {
            @Override
            public void onSuccess(List<DeviceUrlBean.UrlInfo> bean) {
                AddDeviceSuccessActivity.start(AddDeviceWaitingActivity.this, deviceSearchBean.getDeviceID());
                finish();
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.single("getCameraUrl error");
            }
        });
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            secondTextView.setText(String.valueOf(millisUntilFinished / 1000));
//            if (millisUntilFinished/1000 == 118){
//                AddDeviceSuccessActivity.start(AddDeviceWaitingActivity.this, deviceSearchBean.getDeviceID());
//            }
        }

        @Override
        public void onFinish() {

        }
    }
}
