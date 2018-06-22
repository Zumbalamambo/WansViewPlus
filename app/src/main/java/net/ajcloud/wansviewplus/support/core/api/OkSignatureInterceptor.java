package net.ajcloud.wansviewplus.support.core.api;

import net.ajcloud.wansviewplus.main.account.SigninAccountManager;
import net.ajcloud.wansviewplus.support.core.cipher.CipherUtil;
import net.ajcloud.wansviewplus.support.core.okgo.utils.OkLogger;
import net.ajcloud.wansviewplus.support.tools.WLog;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by mamengchao on 2018/06/21.
 * Function:    添加签名，公共请求头
 */
public class OkSignatureInterceptor implements Interceptor {

    private static final String TAG = "OkGo";
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String originalUrl = request.url().url().toString();
        String host = request.url().host();
        String method = request.method();

        //排除登录的API
        if (!originalUrl.equals(ApiConstant.URL_USER_REFRESH_TOKEN)
                && !originalUrl.equals(ApiConstant.URL_GET_APP_CONFIG)
                && !originalUrl.equals(ApiConstant.URL_USER_CHALLENGE)
                && !originalUrl.equals(ApiConstant.URL_USER_SIGNIN)
                && !originalUrl.equals(ApiConstant.URL_USER_SIGNUP)) {
            if (method.equalsIgnoreCase("POST")) {
                String timeStamp = System.currentTimeMillis() + "";
                String[] urls = originalUrl.split(host);
                if (urls.length < 2) {
                    return chain.proceed(request);
                }
                String reqBody = bodyToString(request);
                StringBuilder signBody = new StringBuilder();
                signBody.append("POST");
                signBody.append("\n");
                signBody.append(urls[1]);
                signBody.append("\n");
                signBody.append(CipherUtil.strToHex(CipherUtil.getSha256(reqBody)));
                signBody.append("\n");

                String signToken = SigninAccountManager.getInstance().getCurrentSignToken();
                String stringToSign = "HMAC-SHA256" + "\n" + timeStamp + "\n" + CipherUtil.strToHex(CipherUtil.getSha256(signBody.toString()));
                String signature = CipherUtil.getClondApiSign(signToken, stringToSign);

                Request newRequest = chain.request().newBuilder()
                        .header("Authorization", "Bearer" + " " + SigninAccountManager.getInstance().getCurrentAccountAccessToken())
                        .header("X-UAC-Signature", "UAC1-HMAC-SHA256" + ";" + timeStamp + ";" + signature)
                        .build();
                return chain.proceed(newRequest);
            }
        }
        return chain.proceed(request);
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

    private static Charset getCharset(MediaType contentType) {
        Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
        if (charset == null) charset = UTF_8;
        return charset;
    }
}
