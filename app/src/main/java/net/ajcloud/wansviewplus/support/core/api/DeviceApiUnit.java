package net.ajcloud.wansviewplus.support.core.api;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.entity.LocalInfo;
import net.ajcloud.wansviewplus.main.account.SigninAccountManager;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.bean.AudioInfoBean;
import net.ajcloud.wansviewplus.support.core.bean.BindStatusBean;
import net.ajcloud.wansviewplus.support.core.bean.CloudStorBean;
import net.ajcloud.wansviewplus.support.core.bean.DeviceConfigBean;
import net.ajcloud.wansviewplus.support.core.bean.DeviceListBean;
import net.ajcloud.wansviewplus.support.core.bean.DeviceTimeBean;
import net.ajcloud.wansviewplus.support.core.bean.DeviceUrlBean;
import net.ajcloud.wansviewplus.support.core.bean.LocalStorBean;
import net.ajcloud.wansviewplus.support.core.bean.MoveMonitorBean;
import net.ajcloud.wansviewplus.support.core.bean.PreBindBean;
import net.ajcloud.wansviewplus.support.core.bean.ResponseBean;
import net.ajcloud.wansviewplus.support.core.callback.JsonCallback;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.core.okgo.OkGo;
import net.ajcloud.wansviewplus.support.core.okgo.model.Response;
import net.ajcloud.wansviewplus.support.event.DeviceRefreshEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

    private JSONObject getReqBody(JSONObject data, String deviceId) {
        try {
            JSONObject metaJson = new JSONObject();
            metaJson.put("locale", localInfo.appLang);
            metaJson.put("localtz", localInfo.timeZone);
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

    /**
     * 请求绑定接口，获取authCode
     */
    public void preBind(final OkgoCommonListener<PreBindBean> listener) {
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("agentName", localInfo.deviceName);
            dataJson.put("agentToken", localInfo.deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<ResponseBean<PreBindBean>>post(ApiConstant.URL_DEVICE_PREBIND)
                .tag(this)
                .upJson(getReqBody(dataJson, null))
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
                .upJson(getReqBody(dataJson, null))
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
     * 获取设备列表
     */
    public void getDeviceList(final OkgoCommonListener<List<Camera>> listener) {
        JSONObject dataJson = new JSONObject();
        OkGo.<ResponseBean<DeviceListBean>>post(ApiConstant.URL_DEVICE_GET_DEVICE_LIST)
                .tag(this)
                .upJson(getReqBody(dataJson, null))
                .execute(new JsonCallback<ResponseBean<DeviceListBean>>() {
                    @Override
                    public void onSuccess(Response<ResponseBean<DeviceListBean>> response) {
                        ResponseBean<DeviceListBean> responseBean = response.body();
                        if (responseBean.isSuccess()) {
                            DeviceListBean deviceListBean = responseBean.result;
                            List<DeviceListBean.Device> devices = deviceListBean.cameras;
                            MainApplication.getApplication().getDeviceCache().upDate(devices);
                            doGetDeviceList(new ArrayList<>(MainApplication.getApplication().getDeviceCache().getDevices()));
                            listener.onSuccess(new ArrayList<>(MainApplication.getApplication().getDeviceCache().getDevices()));
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.message);
                        }
                    }

                    @Override
                    public void onError(Response<ResponseBean<DeviceListBean>> response) {
                        super.onError(response);
                        listener.onFail(-1, response.getException().getMessage());
                    }
                });
    }

    /**
     * 获取设备url
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
        OkGo.<ResponseBean<DeviceUrlBean>>post(ApiConstant.URL_GET_DEVICE_URL_INFO)
                .tag(this)
                .upJson(getReqBody(jsonObject, null))
                .execute(new JsonCallback<ResponseBean<DeviceUrlBean>>() {
                    @Override
                    public void onSuccess(Response<ResponseBean<DeviceUrlBean>> response) {
                        ResponseBean responseBean = response.body();
                        if (responseBean.isSuccess()) {
                            DeviceUrlBean bean = (DeviceUrlBean) responseBean.result;
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
        OkGo.<ResponseBean<DeviceConfigBean>>post(url + ApiConstant.URL_DEVICE_GET_DEVICE_INFO)
                .tag(this)
                .upJson(getReqBody(dataJson, null))
                .execute(new JsonCallback<ResponseBean<DeviceConfigBean>>() {
                    @Override
                    public void onSuccess(Response<ResponseBean<DeviceConfigBean>> response) {
                        ResponseBean<DeviceConfigBean> responseBean = response.body();
                        if (responseBean.isSuccess()) {
                            if (responseBean.result != null) {
                                MainApplication.getApplication().getDeviceCache().add(responseBean.result);
                            }
                            listener.onSuccess(responseBean.result);
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
     * @param url       设备ip
     * @param deviceId  设备Id
     * @param aliasName 设备Id
     */
    public void setName(String url, String deviceId, String aliasName, final OkgoCommonListener<Object> listener) {
        if (TextUtils.isEmpty(url)) {
            listener.onSuccess(null);
            return;
        }
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("deviceId", deviceId);
            dataJson.put("aliasName", aliasName);
            dataJson.put("agentName", localInfo.deviceName);
            dataJson.put("agentToken", localInfo.deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String reqUrl = url + ApiConstant.URL_DEVICE_SET_DEVICE_NAME;
        OkGo.<ResponseBean<Object>>post(reqUrl)
                .tag(this)
                .upJson(getReqBody(dataJson, null))
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

    /**
     * 设置设备昵称(Uac)
     *
     * @param deviceId 设备Id
     * @param name     设备别名
     */
    public void setNameUac(String deviceId, String name, final OkgoCommonListener<Object> listener) {
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("deviceId", deviceId);
            dataJson.put("name", name);
            dataJson.put("agentName", localInfo.deviceName);
            dataJson.put("agentToken", localInfo.deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<ResponseBean<Object>>post(ApiConstant.URL_DEVICE_SET_DEVICE_NAME_UAC)
                .tag(this)
                .upJson(getReqBody(dataJson, null))
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

    /**
     * 获取摄像机第一帧
     *
     * @param url      设备ip
     * @param deviceId 设备Id
     */
    public void getFristFrame(String url, String deviceId, final OkgoCommonListener<Object> listener) {
        if (TextUtils.isEmpty(url)) {
            listener.onFail(-1, "url cant be empty");
            return;
        }
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("deviceId", deviceId);
            dataJson.put("agentName", localInfo.deviceName);
            dataJson.put("agentToken", localInfo.deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String reqUrl = url + ApiConstant.URL_DEVICE_GET_FIRST_FRAME;
        OkGo.<ResponseBean<Object>>post(reqUrl)
                .tag(this)
                .upJson(getReqBody(dataJson, null))
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

    /**
     * 设置移动告警
     *
     * @param url      设备ip
     * @param deviceId 设备Id
     * @param bean     移动告警配置
     */
    public void setMoveDetection(String url, String deviceId, MoveMonitorBean bean, final OkgoCommonListener<Object> listener) {
        if (TextUtils.isEmpty(url)) {
            listener.onSuccess(null);
            return;
        }
        JSONObject dataJson = null;
        try {
            dataJson = new JSONObject(new Gson().toJson(bean));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String reqUrl = url + ApiConstant.URL_DEVICE_MOVE_DETECTION;
        OkGo.<ResponseBean<Object>>post(reqUrl)
                .tag(this)
                .upJson(getReqBody(dataJson, deviceId))
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

    /**
     * 设置倒置
     *
     * @param url              设备ip
     * @param deviceId         设备Id
     * @param orientationValue 0 - 关闭(全为彩色模式) , 1 - 自动(缺省),  2-红外灯模式 |
     */
    public void setPlacement(String url, String deviceId, String orientationValue, final OkgoCommonListener<Object> listener) {
        if (TextUtils.isEmpty(url)) {
            listener.onSuccess(null);
            return;
        }
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("orientationValue", orientationValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String reqUrl = url + ApiConstant.URL_DEVICE_PLACEMENT;
        OkGo.<ResponseBean<Object>>post(reqUrl)
                .tag(this)
                .upJson(getReqBody(dataJson, deviceId))
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

    /**
     * 设置夜视
     *
     * @param url       设备ip
     * @param deviceId  设备Id
     * @param nightMode 0 - 关闭(全为彩色模式) , 1 - 自动(缺省),  2-红外灯模式 |
     */
    public void setNightVersion(String url, String deviceId, String nightMode, final OkgoCommonListener<Object> listener) {
        if (TextUtils.isEmpty(url)) {
            listener.onSuccess(null);
            return;
        }
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("nightMode", nightMode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String reqUrl = url + ApiConstant.URL_DEVICE_NIGHT_VERSION;
        OkGo.<ResponseBean<Object>>post(reqUrl)
                .tag(this)
                .upJson(getReqBody(dataJson, deviceId))
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

    /**
     * 设置音频
     *
     * @param url      设备ip
     * @param deviceId 设备Id
     * @param bean     音频设置
     */
    public void setAudioConfig(String url, String deviceId, AudioInfoBean bean, final OkgoCommonListener<Object> listener) {
        if (TextUtils.isEmpty(url)) {
            listener.onSuccess(null);
            return;
        }
        JSONObject dataJson = null;
        try {
            dataJson = new JSONObject(new Gson().toJson(bean));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String reqUrl = url + ApiConstant.URL_DEVICE_AUDIO_CONFIG;
        OkGo.<ResponseBean<Object>>post(reqUrl)
                .tag(this)
                .upJson(getReqBody(dataJson, deviceId))
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

    /**
     * 设置TF卡存储
     *
     * @param url      设备ip
     * @param deviceId 设备Id
     * @param bean     本地存储配置
     */
    public void setLocalStor(String url, String deviceId, LocalStorBean bean, final OkgoCommonListener<Object> listener) {
        if (TextUtils.isEmpty(url)) {
            listener.onSuccess(null);
            return;
        }
        JSONObject dataJson = null;
        try {
            dataJson = new JSONObject(new Gson().toJson(bean));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String reqUrl = url + ApiConstant.URL_DEVICE_LOCAL_STOR;
        OkGo.<ResponseBean<Object>>post(reqUrl)
                .tag(this)
                .upJson(getReqBody(dataJson, deviceId))
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

    /**
     * 设置云存储
     *
     * @param url      设备ip
     * @param deviceId 设备Id
     * @param bean     云存储设置
     */
    public void setCloudStor(String url, String deviceId, CloudStorBean bean, final OkgoCommonListener<Object> listener) {
        if (TextUtils.isEmpty(url)) {
            listener.onSuccess(null);
            return;
        }
        JSONObject dataJson = null;
        try {
            dataJson = new JSONObject(new Gson().toJson(bean));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String reqUrl = url + ApiConstant.URL_DEVICE_CLOUD_STOR;
        OkGo.<ResponseBean<Object>>post(reqUrl)
                .tag(this)
                .upJson(getReqBody(dataJson, deviceId))
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

    /**
     * 设置时区
     *
     * @param url      设备ip
     * @param deviceId 设备Id
     * @param bean     时区配置
     */
    public void setTimeZone(String url, String deviceId, DeviceTimeBean bean, final OkgoCommonListener<Object> listener) {
        if (TextUtils.isEmpty(url)) {
            listener.onSuccess(null);
            return;
        }
        JSONObject dataJson = null;
        try {
            dataJson = new JSONObject(new Gson().toJson(bean));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String reqUrl = url + ApiConstant.URL_DEVICE_TIME_ZONE;
        OkGo.<ResponseBean<Object>>post(reqUrl)
                .tag(this)
                .upJson(getReqBody(dataJson, deviceId))
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

    /**
     * 重启
     *
     * @param url      设备ip
     * @param deviceId 设备Id
     */
    public void restart(String url, String deviceId, final OkgoCommonListener<Object> listener) {
        if (TextUtils.isEmpty(url)) {
            listener.onSuccess(null);
            return;
        }
        JSONObject dataJson = new JSONObject();

        String reqUrl = url + ApiConstant.URL_DEVICE_RESTART;
        OkGo.<ResponseBean<Object>>post(reqUrl)
                .tag(this)
                .upJson(getReqBody(dataJson, deviceId))
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

    /**
     * 恢复出厂设置
     *
     * @param url      设备ip
     * @param deviceId 设备Id
     */
    public void restore(String url, String deviceId, final OkgoCommonListener<Object> listener) {
        if (TextUtils.isEmpty(url)) {
            listener.onSuccess(null);
            return;
        }
        JSONObject dataJson = new JSONObject();

        String reqUrl = url + ApiConstant.URL_DEVICE_RESTORE;
        OkGo.<ResponseBean<Object>>post(reqUrl)
                .tag(this)
                .upJson(getReqBody(dataJson, deviceId))
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

    /**
     * 删除
     *
     * @param url      设备ip
     * @param deviceId 设备Id
     */
    public void remove(String url, final String deviceId, final OkgoCommonListener<Object> listener) {
        if (TextUtils.isEmpty(url)) {
            listener.onSuccess(null);
            return;
        }
        JSONObject dataJson = new JSONObject();

        String reqUrl = url + ApiConstant.URL_DEVICE_REMOVE;
        OkGo.<ResponseBean<Object>>post(reqUrl)
                .tag(this)
                .upJson(getReqBody(dataJson, deviceId))
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(Response<ResponseBean<Object>> response) {
                        ResponseBean responseBean = response.body();
                        if (responseBean.isSuccess()) {
                            unBind(deviceId, listener);
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

    /**
     * 解绑设备
     *
     * @param deviceId 设备Id
     */
    private void unBind(String deviceId, final OkgoCommonListener<Object> listener) {
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("deviceId", deviceId);
            dataJson.put("agentName", localInfo.deviceName);
            dataJson.put("agentToken", localInfo.deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<ResponseBean<Object>>post(ApiConstant.URL_DEVICE_UNBIND)
                .tag(this)
                .upJson(getReqBody(dataJson, null))
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

    /**
     * 获取设备列表之后的操作
     */
    public void doGetDeviceList(List<Camera> devices) {
        List<String> deviceIds = new ArrayList<>();
        for (Camera device : devices) {
            deviceIds.add(device.deviceId);
        }
        getDeviceUrlInfo(deviceIds, new OkgoCommonListener<List<DeviceUrlBean.UrlInfo>>() {
            @Override
            public void onSuccess(List<DeviceUrlBean.UrlInfo> bean) {
                if (bean != null && bean.size() > 0) {
                    for (DeviceUrlBean.UrlInfo info : bean) {
                        Camera camera = MainApplication.getApplication().getDeviceCache().get(info.deviceId);
                        if (camera != null) {
                            if (!TextUtils.isEmpty(info.gatewayUrl)) {
                                getDeviceInfo(info.gatewayUrl, camera.deviceId, new OkgoCommonListener<DeviceConfigBean>() {
                                    @Override
                                    public void onSuccess(DeviceConfigBean bean) {
                                        if (bean != null && bean.base != null) {
                                            if (!TextUtils.isEmpty(bean.base.deviceId)) {
                                                //刷新单条camera信息
                                                EventBus.getDefault().post(new DeviceRefreshEvent(bean.base.deviceId));
                                                //获取第一帧逻辑
                                                doGetFirstFrame(bean.base.deviceId);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFail(int code, String msg) {

                                    }
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
        new UserApiUnit(context).pushSetting("upsert", null, new OkgoCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void doGetFirstFrame(String deviceId) {
        Camera camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
        if (camera != null) {

        }
    }
}
