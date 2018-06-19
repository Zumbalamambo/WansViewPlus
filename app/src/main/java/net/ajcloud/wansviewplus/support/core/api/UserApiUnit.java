package net.ajcloud.wansviewplus.support.core.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.entity.LocalInfo;
import net.ajcloud.wansviewplus.main.account.SigninAccountManager;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.bean.AppConfigBean;
import net.ajcloud.wansviewplus.support.core.bean.ChallengeBean;
import net.ajcloud.wansviewplus.support.core.bean.ResponseBean;
import net.ajcloud.wansviewplus.support.core.bean.SigninBean;
import net.ajcloud.wansviewplus.support.core.callback.JsonCallback;
import net.ajcloud.wansviewplus.support.core.cipher.CipherUtil;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.core.okgo.OkGo;
import net.ajcloud.wansviewplus.support.core.okgo.model.Response;

import org.greenrobot.greendao.annotation.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mamengchao on 2018/05/21.
 * 账号信息相关
 */
public class UserApiUnit {
    private static final String TAG = "UserApiUnit";
    private Context context;
    private LocalInfo localInfo;

    public UserApiUnit(Context context) {
        this.context = context;
        localInfo = ((MainApplication) context.getApplicationContext()).getLocalInfo();
    }

    private JSONObject getReqBody(JSONObject data) {
        try {
            JSONObject metaJson = new JSONObject();
            metaJson.put("locale", localInfo.appLang);
            metaJson.put("localtz", localInfo.timeZone);
            String accessToken = SigninAccountManager.getInstance().getCurrentAccountAccessToken();
            if (!TextUtils.isEmpty(accessToken)) {
                metaJson.put("accessToken", accessToken);
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
     * App启动时需要的公共参数
     */
    public void getAppConfig(final OkgoCommonListener<AppConfigBean> listener) {
        if (ApiConstant.isApply) {
            listener.onSuccess(null);
        } else {
            JSONObject jsonObject = new JSONObject();
            OkGo.<ResponseBean<AppConfigBean>>post(ApiConstant.URL_GET_APP_CONFIG)
                    .tag(this)
                    .upJson(jsonObject)
                    .execute(new JsonCallback<ResponseBean<AppConfigBean>>() {
                        @Override
                        public void onSuccess(Response<ResponseBean<AppConfigBean>> response) {
                            ResponseBean<AppConfigBean> responseBean = response.body();
                            if (responseBean.isSuccess()) {
                                AppConfigBean bean = responseBean.result;
                                ApiConstant.setBaseUrl(bean);
                                listener.onSuccess(bean);
                            } else {
                                listener.onFail(responseBean.getResultCode(), responseBean.message);
                            }
                        }

                        @Override
                        public void onError(Response<ResponseBean<AppConfigBean>> response) {
                            super.onError(response);
                            listener.onFail(-1, context.getString(R.string.Service_Error));
                        }
                    });
        }
    }


    /**
     * 获取秘钥接口，账号相关操作每次必须先调用此接口
     *
     * @param username 用户名
     * @param action   具体操作
     */
    private void challenge(String username, String action, final OkgoCommonListener<ChallengeBean> listener) {
        final JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("username", username);
            dataJson.put("action", action);
            dataJson.put("agentName", localInfo.deviceName);
            dataJson.put("agentToken", localInfo.deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getAppConfig(new OkgoCommonListener<AppConfigBean>() {
            @Override
            public void onSuccess(AppConfigBean bean) {
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

            @Override
            public void onFail(int code, String msg) {
                listener.onFail(code, msg);
            }
        });
    }

    /**
     * 注册
     *
     * @param mail     邮箱
     * @param password 密码
     */
    public void register(final String mail, final String password, final OkgoCommonListener<Object> listener) {
        challenge(mail, "signup", new OkgoCommonListener<ChallengeBean>() {
            @Override
            public void onSuccess(ChallengeBean bean) {
                JSONObject dataJson = new JSONObject();
                try {
                    byte[] nonce = CipherUtil.getNonce();
                    String encodePassword = CipherUtil.naclEncode(password, bean.clientSecretKey, bean.serverPubKey, nonce);
                    dataJson.put("username", mail);
                    dataJson.put("password", encodePassword);
                    dataJson.put("nonce", new String(Base64.encode(nonce, Base64.NO_WRAP), "UTF-8"));
                    dataJson.put("agentName", localInfo.deviceName);
                    dataJson.put("agentToken", localInfo.deviceId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                OkGo.<ResponseBean<Object>>post(ApiConstant.URL_USER_SIGNUP)
                        .tag(this)
                        .upJson(getReqBody(dataJson))
                        .execute(new JsonCallback<ResponseBean<Object>>() {
                            @Override
                            public void onSuccess(Response<ResponseBean<Object>> response) {
                                ResponseBean responseBean = response.body();
                                if (responseBean.isSuccess()) {
                                    listener.onSuccess(responseBean);
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

            @Override
            public void onFail(int code, String msg) {
                listener.onFail(code, msg);
            }
        });
    }

    /**
     * 登录
     *
     * @param mail     邮箱
     * @param password 密码
     */
    public void signin(final String mail, final String password, final OkgoCommonListener<SigninBean> listener) {
        challenge(mail, "signin", new OkgoCommonListener<ChallengeBean>() {
            @Override
            public void onSuccess(ChallengeBean bean) {
                JSONObject dataJson = new JSONObject();
                try {
                    byte[] nonce = CipherUtil.getNonce();
                    String encodePassword = CipherUtil.naclEncode(password, bean.clientSecretKey, bean.serverPubKey, nonce);
                    dataJson.put("username", mail);
                    dataJson.put("password", encodePassword);
                    dataJson.put("nonce", new String(Base64.encode(nonce, Base64.NO_WRAP), "UTF-8"));
                    dataJson.put("agentName", localInfo.deviceName);
                    dataJson.put("agentToken", localInfo.deviceId);
                    dataJson.put("osName", "android");
//                    dataJson.put("clientId", "android");
//                    dataJson.put("clientSecret", "android");
                    dataJson.put("grantType", "password");
                    dataJson.put("scope", "all");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                OkGo.<ResponseBean<SigninBean>>post(ApiConstant.URL_USER_SIGNIN)
                        .tag(this)
                        .upJson(getReqBody(dataJson))
                        .execute(new JsonCallback<ResponseBean<SigninBean>>() {
                            @Override
                            public void onSuccess(Response<ResponseBean<SigninBean>> response) {
                                ResponseBean<SigninBean> responseBean = response.body();
                                if (responseBean.isSuccess()) {
                                    SigninBean bean = responseBean.result;
                                    saveAccount(mail, password, bean);
                                    listener.onSuccess(bean);
                                } else {
                                    listener.onFail(responseBean.getResultCode(), responseBean.message);
                                }
                            }

                            @Override
                            public void onError(Response<ResponseBean<SigninBean>> response) {
                                super.onError(response);
                                listener.onFail(-1, context.getString(R.string.Service_Error));
                            }
                        });
            }

            @Override
            public void onFail(int code, String msg) {
                listener.onFail(code, msg);
            }
        });
    }

    /**
     * 修改密码
     *
     * @param mail        邮箱
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    public void changePassword(final String mail, final String oldPassword, final String newPassword, final OkgoCommonListener<SigninBean> listener) {
        challenge(mail, "changepassword", new OkgoCommonListener<ChallengeBean>() {
            @Override
            public void onSuccess(ChallengeBean bean) {
                JSONObject dataJson = new JSONObject();
                try {
                    byte[] nonce = CipherUtil.getNonce();
                    String encodeOldPassword = CipherUtil.naclEncode(oldPassword, bean.clientSecretKey, bean.serverPubKey, nonce);
                    String encodeNewPassword = CipherUtil.naclEncode(newPassword, bean.clientSecretKey, bean.serverPubKey, nonce);
                    dataJson.put("password", encodeOldPassword);
                    dataJson.put("newPassword", encodeNewPassword);
                    dataJson.put("nonce", new String(Base64.encode(nonce, Base64.NO_WRAP), "UTF-8"));
                    dataJson.put("agentName", localInfo.deviceName);
                    dataJson.put("agentToken", localInfo.deviceId);
                    dataJson.put("osName", "android");
                    dataJson.put("refreshToken", SigninAccountManager.getInstance().getCurrentAccountRefreshToken());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                OkGo.<ResponseBean<SigninBean>>post(ApiConstant.URL_USER_CHANGE_PASSWORD)
                        .tag(this)
                        .upJson(getReqBody(dataJson))
                        .execute(new JsonCallback<ResponseBean<SigninBean>>() {
                            @Override
                            public void onSuccess(Response<ResponseBean<SigninBean>> response) {
                                ResponseBean<SigninBean> responseBean = response.body();
                                if (responseBean.isSuccess()) {
                                    SigninBean bean = responseBean.result;
                                    SigninAccountManager.getInstance().refreshCurrentAccount(bean);
                                    listener.onSuccess(bean);
                                } else {
                                    listener.onFail(responseBean.getResultCode(), responseBean.message);
                                }
                            }

                            @Override
                            public void onError(Response<ResponseBean<SigninBean>> response) {
                                super.onError(response);
                                listener.onFail(-1, context.getString(R.string.Service_Error));
                            }
                        });
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    /**
     * 登出
     */
    public void signout(final OkgoCommonListener<Object> listener) {
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("agentName", localInfo.deviceName);
            dataJson.put("agentToken", localInfo.deviceId);
            dataJson.put("osName", "android");
            dataJson.put("access_token", SigninAccountManager.getInstance().getCurrentAccountAccessToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.<ResponseBean<Object>>post(ApiConstant.URL_USER_SIGNOUT)
                .tag(this)
                .upJson(getReqBody(dataJson))
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(Response<ResponseBean<Object>> response) {
                        ResponseBean responseBean = response.body();
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                            MainApplication.getApplication().logout();
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.message);
                        }
                    }

                    @Override
                    public void onError(Response<ResponseBean<Object>> response) {
                        super.onError(response);
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 刷新token
     */
    public synchronized okhttp3.Response refreshToken() {
        try {
            JSONObject dataJson = new JSONObject();
            dataJson.put("agentName", localInfo.deviceName);
            dataJson.put("agentToken", localInfo.deviceId);
            dataJson.put("osName", "android");
            dataJson.put("accessToken", SigninAccountManager.getInstance().getCurrentAccountAccessToken());
            dataJson.put("refreshToken", SigninAccountManager.getInstance().getCurrentAccountRefreshToken());
            return OkGo.post(ApiConstant.URL_USER_REFRESH_TOKEN)
                    .tag(this)
                    .upJson(getReqBody(dataJson))
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 推送设置
     *
     * @param action 操作：upsert，remove
     * @param token  pushtoken
     */
    public void pushSetting(@NotNull String action, String token, final OkgoCommonListener<Object> listener) {
        JSONObject dataJson = new JSONObject();
        try {
            if (TextUtils.equals(action, "upsert")) {
                //device
                JSONArray devicesArray = new JSONArray();
                for (Camera camera : MainApplication.getApplication().getDeviceCache().getDevices()) {
                    JSONObject deviceJson = new JSONObject();
                    deviceJson.put("did", camera.deviceId);
                    deviceJson.put("alias", camera.aliasName);
                    devicesArray.put(deviceJson);
                }
                //agents
                JSONArray agentsArray = new JSONArray();
                JSONObject agentJson = new JSONObject();
                agentJson.put("name", localInfo.deviceName);
                agentJson.put("token", localInfo.deviceId);
                agentJson.put("accept", 1);
                if (!TextUtils.isEmpty(token)) {
                    agentJson.put("pushToken", token);
                }
                agentJson.put("pushType", "FCM");
                List<String> topics = new ArrayList<>();
                topics.add("notice");
                topics.add("ads");
                agentJson.put("pushTopics", topics);
                agentsArray.put(agentJson);

                dataJson.put("op", "upsert");
                dataJson.put("devices", devicesArray);
                dataJson.put("agents", agentsArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<ResponseBean<Object>>post("https://emc.ajyun.com.cn/api" + ApiConstant.URL_DEVICE_PUSH_SETTING)
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

    /**
     * 推送设置
     *
     * @param deviceId
     * @param alias
     */
    public void setPushName(String deviceId, String alias, final OkgoCommonListener<Object> listener) {
        JSONObject dataJson = new JSONObject();
        try {
            //device
            JSONArray devicesArray = new JSONArray();
            JSONObject deviceJson = new JSONObject();
            deviceJson.put("did", deviceId);
            deviceJson.put("alias", alias);
            devicesArray.put(deviceJson);

            dataJson.put("op", "upsert");
            dataJson.put("devices", devicesArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<ResponseBean<Object>>post("https://emc.ajyun.com.cn/api" + ApiConstant.URL_DEVICE_PUSH_SETTING)
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

    /**
     * 登陆成功后的操作
     */
    private void saveAccount(String mail, String password, SigninBean bean) {
        SigninAccountManager.getInstance().saveCurrentAccount(mail, password, bean);
    }
}
