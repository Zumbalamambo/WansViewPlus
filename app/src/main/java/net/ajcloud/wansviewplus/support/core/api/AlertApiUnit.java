package net.ajcloud.wansviewplus.support.core.api;

import android.content.Context;
import android.text.TextUtils;

import net.ajcloud.wansviewplus.entity.LocalInfo;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.bean.AlarmBean;
import net.ajcloud.wansviewplus.support.core.bean.AlermsBean;
import net.ajcloud.wansviewplus.support.core.bean.ResponseBean;
import net.ajcloud.wansviewplus.support.core.callback.JsonCallback;
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
}
