package net.ajcloud.wansviewplus.support.core.api;

import android.content.Context;
import android.text.TextUtils;

import net.ajcloud.wansviewplus.entity.LocalInfo;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.bean.AlarmBean;
import net.ajcloud.wansviewplus.support.core.bean.AlarmCalendarBean;
import net.ajcloud.wansviewplus.support.core.bean.AlarmListBean;
import net.ajcloud.wansviewplus.support.core.bean.AlermsBean;
import net.ajcloud.wansviewplus.support.core.bean.ResponseBean;
import net.ajcloud.wansviewplus.support.core.callback.JsonCallback;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.core.okgo.OkGo;
import net.ajcloud.wansviewplus.support.core.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by mamengchao on 2018/07/10.
 * Function:    报警相关
 */
public class AlertApiUnit {

    private static final String TAG = "AlertApiUnit";
    private Context context;
    private LocalInfo localInfo;

    public AlertApiUnit(Context context) {
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
     * 获取alert概览
     */
    public void getAlarmsSummary(final OkgoCommonListener<List<AlarmBean>> listener) {
        JSONObject dataJson = new JSONObject();
        OkGo.<ResponseBean<AlermsBean>>post(ApiConstant.URL_DEVICE_ALARMS_SUMMARY)
                .tag(this)
                .upJson(getReqBody(dataJson, null))
                .execute(new JsonCallback<ResponseBean<AlermsBean>>() {
                    @Override
                    public void onSuccess(Response<ResponseBean<AlermsBean>> response) {
                        ResponseBean<AlermsBean> responseBean = response.body();
                        if (responseBean.isSuccess()) {
                            List<AlarmBean> bean = responseBean.result.alarms;
                            MainApplication.getApplication().getAlarmCountCache().setAlarmTime(bean);
                            listener.onSuccess(bean);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.message);
                        }
                    }

                    @Override
                    public void onError(Response<ResponseBean<AlermsBean>> response) {
                        super.onError(response);
                        listener.onFail(-1, response.getException().getMessage());
                    }
                });
    }

    /**
     * 获取alert日历
     *
     * @param deviceId 设备ID
     */
    public void getAlarmsCalendar(String deviceId, final OkgoCommonListener<List<String>> listener) {
        Camera camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
        if (camera == null || camera.timeConfig == null || TextUtils.isEmpty(camera.timeConfig.tzValue)) {
            listener.onFail(-1, "param empty");
            return;
        }
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("deviceId", deviceId);
            dataJson.put("tzValue", camera.timeConfig.tzValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<ResponseBean<AlarmCalendarBean>>post(ApiConstant.URL_DEVICE_ALARMS_CALENDAR)
                .tag(this)
                .upJson(getReqBody(dataJson, null))
                .execute(new JsonCallback<ResponseBean<AlarmCalendarBean>>() {
                    @Override
                    public void onSuccess(Response<ResponseBean<AlarmCalendarBean>> response) {
                        ResponseBean<AlarmCalendarBean> responseBean = response.body();
                        if (responseBean.isSuccess()) {
                            List<String> bean = responseBean.result.cdates;
                            listener.onSuccess(bean);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.message);
                        }
                    }

                    @Override
                    public void onError(Response<ResponseBean<AlarmCalendarBean>> response) {
                        super.onError(response);
                        listener.onFail(-1, response.getException().getMessage());
                    }
                });
    }

    /**
     * 获取alert列表
     *
     * @param cts   告警生成时间戳, 第一页 cts == -1，若 alarms 为空时, 加载至最后一页, cts 无意义
     * @param cdate 摄像头时区的日期 20180710
     * @param limit 每页数量, 缺省值: 10
     */
    public void getAlarmsList(String deviceId, long cts, String cdate, int limit, final OkgoCommonListener<AlarmListBean> listener) {
        Camera camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
        if (camera == null || camera.timeConfig == null || TextUtils.isEmpty(camera.timeConfig.tzValue)) {
            listener.onFail(-1, "param empty");
            return;
        }
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("deviceId", deviceId);
            dataJson.put("tzValue", camera.timeConfig.tzValue);
            dataJson.put("cts", cts);
            dataJson.put("cdate", cdate);
            dataJson.put("limit", limit);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<ResponseBean<AlarmListBean>>post(ApiConstant.URL_DEVICE_ALARMS_LIST)
                .tag(this)
                .upJson(getReqBody(dataJson, null))
                .execute(new JsonCallback<ResponseBean<AlarmListBean>>() {
                    @Override
                    public void onSuccess(Response<ResponseBean<AlarmListBean>> response) {
                        ResponseBean<AlarmListBean> responseBean = response.body();
                        if (responseBean.isSuccess()) {
                            AlarmListBean bean = responseBean.result;
                            listener.onSuccess(bean);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.message);
                        }
                    }

                    @Override
                    public void onError(Response<ResponseBean<AlarmListBean>> response) {
                        super.onError(response);
                        listener.onFail(-1, response.getException().getMessage());
                    }
                });
    }
}
