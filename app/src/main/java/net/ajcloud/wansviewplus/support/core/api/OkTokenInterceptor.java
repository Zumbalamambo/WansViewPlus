package net.ajcloud.wansviewplus.support.core.api;

import android.text.TextUtils;

import net.ajcloud.wansviewplus.main.account.SigninAccountManager;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.okgo.model.HttpParams;
import net.ajcloud.wansviewplus.support.core.okgo.request.base.ProgressRequestBody;
import net.ajcloud.wansviewplus.support.core.okgo.utils.OkLogger;
import net.ajcloud.wansviewplus.support.tools.WLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by mamengchao on 2018/06/12.
 * Function:全局拦截器，刷新token
 */
public class OkTokenInterceptor implements Interceptor {

    private static final String TAG = "OkGo";
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request newRequest = null;

        Request originalRequest = chain.request();
        HttpUrl originalHttpUrl = originalRequest.url();
        String originalUrl = originalHttpUrl.url().toString();
        String method = originalRequest.method();

        WLog.d(TAG, "originalUrl-->" + originalUrl);
        WLog.d(TAG, "method-->" + method);

        final String token = SigninAccountManager.getInstance().getCurrentAccountAccessToken();
        final long expiresIn = SigninAccountManager.getInstance().getCurrentAccountAccessTokenTime();
        WLog.d(TAG, "当前的token：" + token + "\t有效期：" + expiresIn);

        //排除登录的API
        if (!originalUrl.equals(ApiConstant.URL_USER_REFRESH_TOKEN)
                && !originalUrl.equals(ApiConstant.URL_GET_APP_CONFIG)
                && !originalUrl.equals(ApiConstant.URL_USER_CHALLENGE)
                && !originalUrl.equals(ApiConstant.URL_USER_SIGNIN)
                && !originalUrl.equals(ApiConstant.URL_USER_SIGNUP)) {
            if (!TextUtils.isEmpty(token)) {
                //TODO 可能有修改
                boolean isValid = expiresIn - System.currentTimeMillis()/1000 > 3600 * 2 * 66 / 72;
//                boolean isValid = false;
                if (isValid) {
                    WLog.d(TAG, "token：" + token + " 有效 ");
                } else {
                    if (method.equalsIgnoreCase("POST")) {
                        //阻塞获取新token
                        String newAccessToken = new UserApiUnit(MainApplication.getApplication()).refreshToken();
                        if (TextUtils.isEmpty(newAccessToken)) {
                            WLog.d(TAG, "token error");
                            return chain.proceed(originalRequest);
                        } else {
                            JSONObject dataJson = null;
                            try {
                                dataJson = new JSONObject(bodyToString(originalRequest));
                                WLog.d(TAG, "---original:" + dataJson.toString());
                                JSONObject metaJson = dataJson.getJSONObject("meta");
                                metaJson.put("accessToken", newAccessToken);
                                dataJson.put("meta", metaJson);
                                WLog.d(TAG, "---new:" + dataJson.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //重新构建Request
                            ProgressRequestBody progressRequestBody = (ProgressRequestBody) originalRequest.body();
                            progressRequestBody.setRequestBody(RequestBody.create(HttpParams.MEDIA_TYPE_JSON, dataJson.toString()));
                            newRequest = chain.request().newBuilder()
                                    .post(progressRequestBody)
                                    .url(originalUrl)
                                    .build();

                            //继续请求
                            return chain.proceed(newRequest);
                        }
                    }
                }
            }
        }
        return chain.proceed(originalRequest);
    }


    private static Charset getCharset(MediaType contentType) {
        Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
        if (charset == null) charset = UTF_8;
        return charset;
    }

    /**
     * 获取json请求参数
     *
     * @param request 请求
     */
    private String bodyToString(Request request) {
        String data = null;
        try {
            Request copy = request.newBuilder().build();
            RequestBody body = copy.body();
            if (body == null) return null;
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            Charset charset = getCharset(body.contentType());
            data = buffer.readString(charset);
            WLog.d(TAG, "拦截前的body：" + data);
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            return data;
        }
    }
}
