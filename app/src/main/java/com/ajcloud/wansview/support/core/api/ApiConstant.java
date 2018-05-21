package com.ajcloud.wansview.support.core.api;

import com.ajcloud.wansview.BuildConfig;

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
}
