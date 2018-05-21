package com.ajcloud.wansview.support.core.api;

import android.content.Context;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.entity.LocalInfo;
import com.ajcloud.wansview.main.application.MainApplication;
import com.ajcloud.wansview.support.core.bean.ChallengeBean;
import com.ajcloud.wansview.support.core.bean.ResponseBean;
import com.ajcloud.wansview.support.core.callback.JsonCallback;
import com.ajcloud.wansview.support.core.okhttp.OkGo;
import com.ajcloud.wansview.support.core.okhttp.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mamengchao on 2018/05/21.
 * 账号信息相关
 */
public class UserApiUnit {

    private Context context;
    private LocalInfo localInfo;

    public interface UserApiCommonListener<T> {
        void onSuccess(T bean);

        void onFail(int code, String msg);
    }

    public UserApiUnit(Context context) {
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
     * 获取秘钥接口，账号相关操作每次必须先调用此接口
     *
     * @param username 用户名
     * @param action   具体操作
     */
    public void challenge(String username, String action, final UserApiCommonListener<ChallengeBean> listener) {
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("username", username);
            dataJson.put("action", action);
            dataJson.put("agentName", localInfo.deviceName);
            dataJson.put("agentToken", localInfo.deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<ResponseBean<ChallengeBean>>post(ApiConstant.URL_USER_CHALLENGE)
                .tag(this)
                .upJson(getReqBody(dataJson))
                .execute(new JsonCallback<ResponseBean<ChallengeBean>>() {
                    @Override
                    public void onSuccess(Response<ResponseBean<ChallengeBean>> response) {
                        ResponseBean responseBean = response.body();
                        if (responseBean.isSuccess()) {
                            listener.onSuccess((ChallengeBean) responseBean.result);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.message);
                        }
                    }

                    @Override
                    public void onError(Response<ResponseBean<ChallengeBean>> response) {
                        super.onError(response);
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    public void register(String mail, String password, UserApiCommonListener listener) {

    }
}
