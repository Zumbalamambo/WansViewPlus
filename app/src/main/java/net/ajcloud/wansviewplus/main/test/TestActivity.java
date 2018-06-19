package net.ajcloud.wansviewplus.main.test;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.api.UserApiUnit;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

public class TestActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Test");
        getToolbar().setLeftImg(R.mipmap.icon_back);
    }

    @Override
    protected void initData() {
        final EditText etTest = findViewById(R.id.et_test);
        final TextView tvTest = findViewById(R.id.tv_test);
        findViewById(net.ajcloud.wansviewplus.R.id.push_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UserApiUnit(TestActivity.this).pushSetting("upsert", "fHJYNJZgpnE:APA91bE6B0PPneI6NOxhe-qGD1OxA2O7NrP60eP3Yc1lSqepnJroEQBmPgX4WyJC6RDg0zmN8TQa5KlHjnxbN6VozWz1-tgEBoV3KnfqpHcJWPIST_gAC7hEwtXhEzqYjDzIuKR-Rqa-", null, new OkgoCommonListener<Object>() {
                    @Override
                    public void onSuccess(Object bean) {
                        tvTest.setText(bean.toString());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        tvTest.setText(msg);
                    }
                });
            }
        });
        findViewById(net.ajcloud.wansviewplus.R.id.unbind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etTest.getText().toString())) {
                    new DeviceApiUnit(TestActivity.this).unBind(etTest.getText().toString(), new OkgoCommonListener<Object>() {
                        @Override
                        public void onSuccess(Object bean) {
                            ToastUtil.single("ok");
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            ToastUtil.single(msg);
                        }
                    });
                }
            }
        });
        findViewById(net.ajcloud.wansviewplus.R.id.unbind_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Camera camera : MainApplication.getApplication().getDeviceCache().getDevices()
                        ) {
                    new DeviceApiUnit(TestActivity.this).unBind(camera.deviceId, new OkgoCommonListener<Object>() {
                        @Override
                        public void onSuccess(Object bean) {
                            ToastUtil.single("ok");
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            ToastUtil.single(msg);
                        }
                    });
                }
            }
        });
    }
}
