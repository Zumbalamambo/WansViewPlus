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
import net.ajcloud.wansviewplus.support.core.bean.DeviceConfigBean;
import net.ajcloud.wansviewplus.support.core.bean.ResponseBean;
import net.ajcloud.wansviewplus.support.core.callback.JsonCallback;
import net.ajcloud.wansviewplus.support.core.cipher.CipherUtil;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.core.okgo.OkGo;
import net.ajcloud.wansviewplus.support.core.okgo.model.Response;
import net.ajcloud.wansviewplus.support.tools.WLog;

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
                try {
                    JSONObject jsonObject = new JSONObject("{\"meta\":{\"localtz\":480,\"deviceId\":\"K038682CY5NV1PI9\",\"accessToken\":\"MzM5MTM5NDAtNmFmNC0xMWU4LWE4YmUtMWI5YjE5NzZlMjA1LmMyNmM1NzE2NTM2OGUzNDhmMTM1OTA1YTdlNDVhY2MyLjE1Mjk2NTc0NzI=_2fnN9P29y8ChKUVp\\/a4293vDnIzTedZWdq8CI16kiIc=\",\"locale\":\"zh_CN\"},\"data\":{}}");
                    String result = CipherUtil.getSha256(jsonObject.toString());
                    WLog.d("sign token", "result:" + result);
                    tvTest.setText(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                signBody.append(CipherUtil.getSha256(reqBody.toString()));
                signBody.append("\n");
                WLog.d("sign token", "signBody:" + signBody.toString());

                String signToken = SigninAccountManager.getInstance().getCurrentSignToken();
                WLog.d("sign token", "signToken:" + signToken);
                String stringToSign = null;
                stringToSign = "HMAC-SHA256" + "\n" + timeStamp + "\n" + CipherUtil.getSha256(signBody.toString());
                WLog.d("sign token", "stringToSign:" + stringToSign);
                String signature = CipherUtil.getClondApiSign(signToken, stringToSign);
                WLog.d("sign token", "signature:" + signature);

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
