package net.ajcloud.wansviewplus.main.device.addDevice.wifi;

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
import net.ajcloud.wansviewplus.main.account.SigninAccountManager;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.main.device.addDevice.AddDeviceSuccessActivity;
import net.ajcloud.wansviewplus.support.core.api.ApiConstant;
import net.ajcloud.wansviewplus.support.core.bean.DeviceListBean;
import net.ajcloud.wansviewplus.support.core.bean.ResponseBean;
import net.ajcloud.wansviewplus.support.core.callback.JsonCallback;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.core.okgo.OkGo;
import net.ajcloud.wansviewplus.support.core.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AddDeviceWifiWaitingActivity extends BaseActivity {

    private static int MSG_CHECK = 100;
    private TextView secondTextView;
    private TimeCount timeCount;
    private Handler checkHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (MainApplication.getApplication().getLastestActivity() == AddDeviceWifiWaitingActivity.this) {
                checkStatus();
            }
        }
    };

    public static void startBind(Context context) {
        Intent intent = new Intent(context, AddDeviceWifiWaitingActivity.class);
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
        timeCount = new AddDeviceWifiWaitingActivity.TimeCount(121000, 1000);
        timeCount.start();
        checkHandler.sendEmptyMessage(MSG_CHECK);
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
        JSONObject body = new JSONObject();
        try {
            JSONObject metaJson = new JSONObject();
            JSONObject dataJson = new JSONObject();
            metaJson.put("locale", MainApplication.getApplication().getLocalInfo().appLang);
            metaJson.put("localtz", MainApplication.getApplication().getLocalInfo().timeZone);

            body.put("meta", metaJson);
            body.put("data", dataJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkGo.<ResponseBean<DeviceListBean>>post(ApiConstant.URL_DEVICE_GET_DEVICE_LIST)
                .tag(this)
                .upJson(body)
                .execute(new JsonCallback<ResponseBean<DeviceListBean>>() {
                    @Override
                    public void onSuccess(Response<ResponseBean<DeviceListBean>> response) {
                        ResponseBean<DeviceListBean> responseBean = response.body();
                        if (responseBean.isSuccess()) {
                            DeviceListBean bean = responseBean.result;
                            if (bean.cameras != null) {
                                if (bean.cameras.size() > MainApplication.getApplication().getDeviceCache().getCounts()) {
                                    //success
                                    checkHandler.removeMessages(MSG_CHECK);
                                    doSuccess(bean.cameras);
                                } else {
                                    //fail
                                    checkHandler.removeMessages(MSG_CHECK);
                                    checkHandler.sendEmptyMessageDelayed(MSG_CHECK, 5000);
                                }
                            }
                        } else {
                            //fail
                            checkHandler.removeMessages(MSG_CHECK);
                            checkHandler.sendEmptyMessageDelayed(MSG_CHECK, 5000);
                        }
                    }

                    @Override
                    public void onError(Response<ResponseBean<DeviceListBean>> response) {
                        super.onError(response);
                        //fail
                        checkHandler.removeMessages(MSG_CHECK);
                        checkHandler.sendEmptyMessageDelayed(MSG_CHECK, 5000);
                    }
                });
    }

    private void doSuccess(List<DeviceListBean.Device> bean) {
        outer:
        for (DeviceListBean.Device device : bean) {
            for (Camera localCamera : MainApplication.getApplication().getDeviceCache().getDevices()) {
                if (TextUtils.equals(localCamera.deviceId, device.deviceId)) {
                    continue outer;
                }
            }
//            MainApplication.getApplication().getDeviceCache().add(new Camera(device.deviceId, null));
            AddDeviceSuccessActivity.start(AddDeviceWifiWaitingActivity.this, device.deviceId);
            finish();
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
            Intent intent = new Intent(AddDeviceWifiWaitingActivity.this, AddDeviceFailActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
    }
}