package net.ajcloud.wansview.support.core.api;

import android.content.Context;

import net.ajcloud.wansview.BuildConfig;
import net.ajcloud.wansview.support.utils.preference.PreferenceKey;
import net.ajcloud.wansview.support.utils.preference.SPUtil;

import net.ajcloud.wansview.BuildConfig;
import net.ajcloud.wansview.support.utils.preference.PreferenceKey;
import net.ajcloud.wansview.support.utils.preference.SPUtil;

/**
 * Created by mamengchao on 2018/05/21.
 */
public class ApiConstant {
    public static String BASE_URL = BuildConfig.BASE_URL;
    public static String BASE_URL_API = BuildConfig.BASE_URL + "/api";

    // 账号相关
    public static String URL_USER_CHALLENGE = BASE_URL_API + "/challenge";
    public static String URL_USER_SIGNIN = BASE_URL_API + "/signin";
    public static String URL_USER_SIGNUP = BASE_URL_API + "/signup";
    public static String URL_USER_SIGNOUT = BASE_URL_API + "/signout";
    public static String URL_USER_USER_ACTIVE = BASE_URL_API + "/user-active";
    public static String URL_USER_CHANGE_PASSWORD = BASE_URL_API + "/change-password";

    public static void setAccessToken(Context context, String token) {
        SPUtil accountSP = SPUtil.getSPUtil(context, PreferenceKey.sp_name.account);
        accountSP.put(PreferenceKey.sp_key.ACCESS_TOKEN, token);
    }

    public static String getAccessToken(Context context) {
        SPUtil accountSP = SPUtil.getSPUtil(context, PreferenceKey.sp_name.account);
        return (String) accountSP.get(PreferenceKey.sp_key.ACCESS_TOKEN, "");
    }

    public static void setRefreshToken(Context context, String token) {
        SPUtil accountSP = SPUtil.getSPUtil(context, PreferenceKey.sp_name.account);
        accountSP.put(PreferenceKey.sp_key.REFRESH_TOKEN, token);
    }

    public static String getRefreshToken(Context context) {
        SPUtil accountSP = SPUtil.getSPUtil(context, PreferenceKey.sp_name.account);
        return (String) accountSP.get(PreferenceKey.sp_key.REFRESH_TOKEN, "");
    }
}
