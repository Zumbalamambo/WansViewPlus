package net.ajcloud.wansviewplus.main.test;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.account.SigninAccountManager;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.api.ApiConstant;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.api.UserApiUnit;
import net.ajcloud.wansviewplus.support.core.bean.DeviceConfigBean;
import net.ajcloud.wansviewplus.support.core.bean.DeviceListBean;
import net.ajcloud.wansviewplus.support.core.bean.ResponseBean;
import net.ajcloud.wansviewplus.support.core.callback.JsonCallback;
import net.ajcloud.wansviewplus.support.core.cipher.CipherUtil;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.core.okgo.OkGo;
import net.ajcloud.wansviewplus.support.core.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

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
                new UserApiUnit(TestActivity.this).pushSetting("upsert", "fHJYNJZgpnE:APA91bE6B0PPneI6NOxhe-qGD1OxA2O7NrP60eP3Yc1lSqepnJroEQBmPgX4WyJC6RDg0zmN8TQa5KlHjnxbN6VozWz1-tgEBoV3KnfqpHcJWPIST_gAC7hEwtXhEzqYjDzIuKR-Rqa-", new OkgoCommonListener<Object>() {
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
                String text = etTest.getText().toString();
                try {
                    String result = CipherUtil.strToHex(CipherUtil.getSha256(text));
                    tvTest.setText(result);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(net.ajcloud.wansviewplus.R.id.unbind_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Camera camera = MainApplication.getApplication().getDeviceCache().get("K03868KVLJNASXNC");
                JSONObject dataJson = new JSONObject();
                try {
                    dataJson.put("deviceId", "K03868KVLJNASXNC");
                    dataJson.put("agentName", MainApplication.getApplication().getLocalInfo().deviceName);
                    dataJson.put("agentToken", MainApplication.getApplication().getLocalInfo().deviceId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String timeStamp = System.currentTimeMillis() + "";
                JSONObject reqBody = getReqBody(dataJson, null);
                StringBuilder signBody = new StringBuilder();
                signBody.append("POST");
                signBody.append("\n");
                signBody.append("/api/v1/cmd/fetch-info");
                signBody.append("\n");
                try {
                    signBody.append(CipherUtil.strToHex(CipherUtil.getSha256(reqBody.toString())));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                signBody.append("\n");

                String signToken = SigninAccountManager.getInstance().getCurrentSignToken();
                String stringToSign = null;
                try {
                    stringToSign = "HMAC-SHA256" + "\n" + timeStamp + "\n" + CipherUtil.strToHex(CipherUtil.getSha256(signBody.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String signature = CipherUtil.getClondApiSign(signToken, stringToSign);


                OkGo.<ResponseBean<DeviceConfigBean>>post(camera.getGatewayUrl() + ApiConstant.URL_DEVICE_GET_DEVICE_INFO)
                        .tag(this)
                        .headers("Authorization", "Bearer" + " " + SigninAccountManager.getInstance().getCurrentAccountAccessToken())
                        .headers("X-UAC-Signature", "UAC1-HMAC-SHA256" + ";" + timeStamp + ";" + signature)
                        .upJson(reqBody)
                        .execute(new JsonCallback<ResponseBean<DeviceConfigBean>>() {
                            @Override
                            public void onSuccess(Response<ResponseBean<DeviceConfigBean>> response) {
                            }

                            @Override
                            public void onError(Response<ResponseBean<DeviceConfigBean>> response) {
                                super.onError(response);
                            }
                        });
            }
        });
    }


    private JSONObject getReqBody(JSONObject data, String deviceId) {
        try {
            JSONObject metaJson = new JSONObject();
            metaJson.put("locale", MainApplication.getApplication().getLocalInfo().appLang);
            metaJson.put("localtz", MainApplication.getApplication().getLocalInfo().timeZone);
            String accessToken = SigninAccountManager.getInstance().getCurrentAccountAccessToken();
            if (!TextUtils.isEmpty(accessToken)) {
                metaJson.put("accessToken", accessToken);
            }
            if (!TextUtils.isEmpty(deviceId)) {
                metaJson.put("deviceId", deviceId);
            }

            JSONObject body = new JSONObject();
            body.put("meta", metaJson);
            body.put("data", data);
            return body;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
