package net.ajcloud.wansview.support.core.api;

import android.content.Context;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.entity.LocalInfo;
import net.ajcloud.wansview.main.application.MainApplication;
import net.ajcloud.wansview.support.core.bean.BindStatusBean;
import net.ajcloud.wansview.support.core.bean.DeviceConfigBean;
import net.ajcloud.wansview.support.core.bean.DeviceUrlBean;
import net.ajcloud.wansview.support.core.bean.PreBindBean;
import net.ajcloud.wansview.support.core.bean.ResponseBean;
import net.ajcloud.wansview.support.core.callback.JsonCallback;
import net.ajcloud.wansview.support.core.device.Camera;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by mamengchao on 2018/06/05.
 * Function:   设备相关操作
 */
public class DeviceApiUnit {

    private static final String TAG = "DeviceApiUnit";
    private Context context;
    private LocalInfo localInfo;

    public DeviceApiUnit(Context context) {
        this.context = context;
        localInfo = ((MainApplication) context.getApplicationContext()).getLocalInfo();
    }

    private JSONObject getReqBody(JSONObject data) {
        try {
            JSONObject metaJson = new JSONObject();
            metaJson.put("locale", localInfo.appLang);
            metaJson.put("localtz", localInfo.timeZone);

            JSONObject body = new JSONObject();
            body.put("meta", metaJson);
            body.put("data", data);
            return body;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * App启动时需要的公共参数
     */
    public void getDeviceUrlInfo(List<String> devices, final OkgoCommonListener<List<DeviceUrlBean.UrlInfo>> listener) {
        if (devices == null || devices.size() == 0) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            for (String deviceId : devices) {
                JSONObject data = new JSONObject();
                data.put("deviceId", deviceId);
                jsonArray.put(data);
            }
            jsonObject.put("devices", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<ResponseBean<DeviceUrlBean>>post(ApiConstant.URL_GET_APP_CONFIG)
                .tag(this)
                .upJson(getReqBody(jsonObject))
                .execute(new JsonCallback<ResponseBean<DeviceUrlBean>>() {
                    @Override
                    public void onSuccess(Response<ResponseBean<DeviceUrlBean>> response) {
                        ResponseBean responseBean = response.body();
                        DeviceUrlBean bean = (DeviceUrlBean) responseBean.result;
                        if (responseBean.isSuccess()) {
                            if (bean.devices != null && bean.devices.size() != 0) {
                                for (DeviceUrlBean.UrlInfo info : bean.devices) {
                                    Camera camera = MainApplication.getApplication().getDeviceCache().get(info.deviceId);
                                    if (camera != null) {
                                        camera.setGatewayUrl(info.gatewayUrl);
                                        camera.setTunnelUrl(info.tunnelUrl);
                                        camera.setCloudStorUrl(info.cloudStorUrl);
                                        camera.setEmcUrl(info.emcUrl);
                                    }
                                }
                            }
                            listener.onSuccess(bean.devices);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.message);
                        }
                    }

                    @Override
                    public void onError(Response<ResponseBean<DeviceUrlBean>> response) {
                        super.onError(response);
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 请求绑定接口，获取authCode
     *
     * @param deviceId 设备Id
     */
    public void preBind(String deviceId, final OkgoCommonListener<PreBindBean> listener) {
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("deviceId", deviceId);
            dataJson.put("agentName", localInfo.deviceName);
            dataJson.put("agentToken", localInfo.deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<ResponseBean<PreBindBean>>post(ApiConstant.URL_DEVICE_PREBIND)
                .tag(this)
                .upJson(getReqBody(dataJson))
                .execute(new JsonCallback<ResponseBean<PreBindBean>>() {
                    @Override
                    public void onSuccess(Response<ResponseBean<PreBindBean>> response) {
                        ResponseBean responseBean = response.body();
                        if (responseBean.isSuccess()) {
                            listener.onSuccess((PreBindBean) responseBean.result);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.message);
                        }
                    }

                    @Override
                    public void onError(Response<ResponseBean<PreBindBean>> response) {
                        super.onError(response);
                        listener.onFail(-1, response.getException().getMessage());
                    }
                });
    }

    /**
     * 获取绑定信息接口
     *
     * @param deviceId 设备Id
     */
    public void getBindStatus(String deviceId, final OkgoCommonListener<BindStatusBean> listener) {
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("deviceId", deviceId);
            dataJson.put("agentName", localInfo.deviceName);
            dataJson.put("agentToken", localInfo.deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<ResponseBean<BindStatusBean>>post(ApiConstant.URL_DEVICE_GET_BIND_STATUS)
                .tag(this)
                .upJson(getReqBody(dataJson))
                .execute(new JsonCallback<ResponseBean<BindStatusBean>>() {
                    @Override
                    public void onSuccess(Response<ResponseBean<BindStatusBean>> response) {
                        ResponseBean responseBean = response.body();
                        if (responseBean.isSuccess()) {
                            listener.onSuccess((BindStatusBean) responseBean.result);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.message);
                        }
                    }

                    @Override
                    public void onError(Response<ResponseBean<BindStatusBean>> response) {
                        super.onError(response);
                        listener.onFail(-1, response.getException().getMessage());
                    }
                });
    }

    /**
     * 获取设备信息
     *
     * @param url      设备ip
     * @param deviceId 设备Id
     */
    public void getDeviceInfo(String url, String deviceId, final OkgoCommonListener<DeviceConfigBean> listener) {
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("deviceId", deviceId);
            dataJson.put("agentName", localInfo.deviceName);
            dataJson.put("agentToken", localInfo.deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String reqUrl = String.format(ApiConstant.URL_DEVICE_GET_DEVICE_INFO, url);
        OkGo.<ResponseBean<DeviceConfigBean>>post(reqUrl)
                .tag(this)
                .upJson(getReqBody(dataJson))
                .execute(new JsonCallback<ResponseBean<DeviceConfigBean>>() {
                    @Override
                    public void onSuccess(Response<ResponseBean<DeviceConfigBean>> response) {
                        ResponseBean responseBean = response.body();
                        if (responseBean.isSuccess()) {
                            listener.onSuccess((DeviceConfigBean) responseBean.result);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.message);
                        }
                    }

                    @Override
                    public void onError(Response<ResponseBean<DeviceConfigBean>> response) {
                        super.onError(response);
                        listener.onFail(-1, response.getException().getMessage());
                    }
                });
    }

    /**
     * 设置设备昵称
     *
     * @param url      设备ip
     * @param deviceId 设备Id
     */
    public void setName(String url, String deviceId, String aliasName, final OkgoCommonListener<Object> listener) {
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("deviceId", deviceId);
            dataJson.put("aliasName", aliasName);
            dataJson.put("agentName", localInfo.deviceName);
            dataJson.put("agentToken", localInfo.deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String reqUrl = String.format(ApiConstant.URL_DEVICE_SET_DEVICE_NAME, url);
        OkGo.<ResponseBean<Object>>post(reqUrl)
                .tag(this)
                .upJson(getReqBody(dataJson))
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(Response<ResponseBean<Object>> response) {
                        ResponseBean responseBean = response.body();
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.result);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.message);
                        }
                    }

                    @Override
                    public void onError(Response<ResponseBean<Object>> response) {
                        super.onError(response);
                        listener.onFail(-1, response.getException().getMessage());
                    }
                });
    }
}
