package net.ajcloud.wansviewplus.support.core.api;

import android.content.Context;
import android.text.TextUtils;

import net.ajcloud.wansviewplus.entity.LocalInfo;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.bean.CloudStoragePlan.CloudStoragePlanBean;
import net.ajcloud.wansviewplus.support.core.bean.GroupListBean;
import net.ajcloud.wansviewplus.support.core.bean.ResponseBean;
import net.ajcloud.wansviewplus.support.core.callback.JsonCallback;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.core.okgo.OkGo;
import net.ajcloud.wansviewplus.support.core.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mamengchao on 2018/07/20.
 * Function:    云存储相关
 */
public class CloudStorageApiUnit {

    private static final String TAG = "CloudStorageApiUnit";
    private Context context;
    private LocalInfo localInfo;

    public CloudStorageApiUnit(Context context) {
        this.context = context;
        localInfo = ((MainApplication) context.getApplicationContext()).getLocalInfo();
    }

    private JSONObject getReqBody(JSONObject data, String deviceId) {
        try {
            JSONObject metaJson = new JSONObject();
            metaJson.put("locale", localInfo.appLang);
            metaJson.put("localtz", localInfo.timeZone);
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
     * 云存储分组查询
     *
     * @param dayStartTs 开始时间
     * @param dayEndTs   结束时间
     */
    public void getGroupList(String deviceId, long dayStartTs, long dayEndTs, final OkgoCommonListener<GroupListBean> listener) {
        Camera camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
        if (camera == null) {
            listener.onFail(-1, "param empty");
            return;
        }

        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("accessKey", camera.accessKey);
            dataJson.put("tzValue", camera.timeConfig.tzValue);
            dataJson.put("dayStartTs", dayStartTs);
            dataJson.put("dayEndTs", dayEndTs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<ResponseBean<GroupListBean>>post(camera.getCloudStorUrl() + ApiConstant.URL_DEVICE_GROUP_LIST)
                .tag(this)
                .upJson(getReqBody(dataJson, deviceId))
                .execute(new JsonCallback<ResponseBean<GroupListBean>>() {
                    @Override
                    public void onSuccess(Response<ResponseBean<GroupListBean>> response) {
                        ResponseBean<GroupListBean> responseBean = response.body();
                        if (responseBean.isSuccess()) {
                            GroupListBean bean = responseBean.result;
                            listener.onSuccess(bean);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.message);
                        }
                    }

                    @Override
                    public void onError(Response<ResponseBean<GroupListBean>> response) {
                        super.onError(response);
                        listener.onFail(-1, response.getException().getMessage());
                    }
                });
    }

    /**
     * 删除分组
     *
     * @param startGroup 开始group
     * @param endGroup   结束group
     */
    public void deleteGroups(String deviceId, GroupListBean.GroupInfo startGroup, GroupListBean.GroupInfo endGroup, final OkgoCommonListener<Object> listener) {
        Camera camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
        if (camera == null) {
            listener.onFail(-1, "param empty");
            return;
        }

        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("deviceId", camera.deviceId);
            JSONObject startJson = new JSONObject();
            startJson.put("groupId", startGroup.groupId);
            startJson.put("tsStart", startGroup.tsStart);
            startJson.put("tsEnd", startGroup.tsEnd);
            dataJson.put("start", startJson);
            JSONObject endJson = new JSONObject();
            startJson.put("groupId", endGroup.groupId);
            startJson.put("tsStart", endGroup.tsStart);
            startJson.put("tsEnd", endGroup.tsEnd);
            dataJson.put("end", endJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<ResponseBean<Object>>post(camera.getCloudStorUrl() + ApiConstant.URL_DEVICE_REMOVE_GROUPS)
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
     * 云存储套餐(PLAN)列表
     */
    public void getPlanList(String deviceId, final OkgoCommonListener<CloudStoragePlanBean> listener) {
        Camera camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
        if (camera == null) {
            listener.onFail(-1, "param empty");
            return;
        }

        JSONObject dataJson = new JSONObject();
        OkGo.<ResponseBean<CloudStoragePlanBean>>post(camera.getCloudStorUrl() + ApiConstant.URL_DEVICE_PLAN_LIST)
                .tag(this)
                .upJson(getReqBody(dataJson, deviceId))
                .execute(new JsonCallback<ResponseBean<CloudStoragePlanBean>>() {
                    @Override
                    public void onSuccess(Response<ResponseBean<CloudStoragePlanBean>> response) {
                        ResponseBean<CloudStoragePlanBean> responseBean = response.body();
                        if (responseBean.isSuccess()) {
                            CloudStoragePlanBean plans = responseBean.result;
                            listener.onSuccess(plans);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.message);
                        }
                    }

                    @Override
                    public void onError(Response<ResponseBean<CloudStoragePlanBean>> response) {
                        super.onError(response);
                        listener.onFail(-1, response.getException().getMessage());
                    }
                });
    }
}
