package net.ajcloud.wansview.support.core.api;

import android.content.Context;
import android.util.Base64;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.entity.LocalInfo;
import net.ajcloud.wansview.main.account.SigninAccountManager;
import net.ajcloud.wansview.main.application.MainApplication;
import net.ajcloud.wansview.support.core.bean.AppConfigBean;
import net.ajcloud.wansview.support.core.bean.ChallengeBean;
import net.ajcloud.wansview.support.core.bean.ResponseBean;
import net.ajcloud.wansview.support.core.bean.SigninBean;
import net.ajcloud.wansview.support.core.callback.JsonCallback;
import net.ajcloud.wansview.support.core.cipher.CipherUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mamengchao on 2018/05/21.
 * 账号信息相关
 */
public class UserApiUnit {
    private static final String TAG = "UserApiUnit";
    private Context context;
    private LocalInfo localInfo;
    private SigninAccountManager accountManager;

    public interface UserApiCommonListener<T> {
        void onSuccess(T bean);

        void onFail(int code, String msg);
    }

    public UserApiUnit(Context context) {
        this.context = context;
        localInfo = ((MainApplication) context.getApplicationContext()).getLocalInfo();
        accountManager = new SigninAccountManager(context);
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
    public void getAppConfig(final UserApiCommonListener<AppConfigBean> listener) {
        JSONObject jsonObject = new JSONObject();
        OkGo.<ResponseBean<AppConfigBean>>post(ApiConstant.URL_GET_APP_CONFIG)
                .tag(this)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<AppConfigBean>>() {
                    @Override
                    public void onSuccess(Response<ResponseBean<AppConfigBean>> response) {
                        ResponseBean responseBean = response.body();
                        AppConfigBean bean = (AppConfigBean) responseBean.result;
                        if (responseBean.isSuccess()) {
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


    /**
     * 获取秘钥接口，账号相关操作每次必须先调用此接口
     *
     * @param username 用户名
     * @param action   具体操作
     */
    private void challenge(String username, String action, final UserApiCommonListener<ChallengeBean> listener) {
        final JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("username", username);
            dataJson.put("action", action);
            dataJson.put("agentName", localInfo.deviceName);
            dataJson.put("agentToken", localInfo.deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (ApiConstant.isApply) {
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
        } else {
            getAppConfig(new UserApiCommonListener<AppConfigBean>() {
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
    }

    /**
     * 注册
     *
     * @param mail     邮箱
     * @param password 密码
     */
    public void register(final String mail, final String password, final UserApiCommonListener<Object> listener) {
        challenge(mail, "signup", new UserApiCommonListener<ChallengeBean>() {
            @Override
            public void onSuccess(ChallengeBean bean) {
                JSONObject dataJson = new JSONObject();
                try {
                    byte[] nonce = CipherUtil.getNonce();
                    String encodePassword = CipherUtil.encode(password, bean.clientSecretKey, bean.serverPubKey, nonce);
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
    public void signin(final String mail, final String password, final UserApiCommonListener<SigninBean> listener) {
        challenge(mail, "signin", new UserApiCommonListener<ChallengeBean>() {
            @Override
            public void onSuccess(ChallengeBean bean) {
                JSONObject dataJson = new JSONObject();
                try {
                    byte[] nonce = CipherUtil.getNonce();
                    String encodePassword = CipherUtil.encode(password, bean.clientSecretKey, bean.serverPubKey, nonce);
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
                                ResponseBean responseBean = response.body();
                                SigninBean bean = (SigninBean) responseBean.result;
                                if (responseBean.isSuccess()) {
                                    saveAccount(mail, bean);
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
    public void changePassword(final String mail, final String oldPassword, final String newPassword, final UserApiCommonListener<SigninBean> listener) {
        challenge(mail, "changepassword", new UserApiCommonListener<ChallengeBean>() {
            @Override
            public void onSuccess(ChallengeBean bean) {
                JSONObject dataJson = new JSONObject();
                try {
                    byte[] nonce = CipherUtil.getNonce();
                    String encodeOldPassword = CipherUtil.encode(oldPassword, bean.clientSecretKey, bean.serverPubKey, nonce);
                    String encodeNewPassword = CipherUtil.encode(newPassword, bean.clientSecretKey, bean.serverPubKey, nonce);
                    dataJson.put("password", encodeOldPassword);
                    dataJson.put("newPassword", encodeNewPassword);
                    dataJson.put("nonce", new String(Base64.encode(nonce, Base64.NO_WRAP), "UTF-8"));
                    dataJson.put("agentName", localInfo.deviceName);
                    dataJson.put("agentToken", localInfo.deviceId);
                    dataJson.put("osName", "android");
                    dataJson.put("refreshToken", accountManager.getCurrentAccountRefreshToken());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                OkGo.<ResponseBean<SigninBean>>post(ApiConstant.URL_USER_CHANGE_PASSWORD)
                        .tag(this)
                        .upJson(getReqBody(dataJson))
                        .execute(new JsonCallback<ResponseBean<SigninBean>>() {
                            @Override
                            public void onSuccess(Response<ResponseBean<SigninBean>> response) {
                                ResponseBean responseBean = response.body();
                                SigninBean bean = (SigninBean) responseBean.result;
                                if (responseBean.isSuccess()) {
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
    public void signout(final UserApiCommonListener<Object> listener) {
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("agentName", localInfo.deviceName);
            dataJson.put("agentToken", localInfo.deviceId);
            dataJson.put("osName", "android");
            dataJson.put("access_token", accountManager.getCurrentAccountAccessToken());
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
     * 登陆成功后的操作
     */
    private void saveAccount(String mail, SigninBean bean) {
        SigninAccountManager manager = new SigninAccountManager(context);
        manager.saveCurrentAccount(mail, bean);
    }
}
