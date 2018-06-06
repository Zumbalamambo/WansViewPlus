package net.ajcloud.wansview.support.core.api;

import net.ajcloud.wansview.support.core.bean.AppConfigBean;

/**
 * Created by mamengchao on 2018/05/21.
 */
public class ApiConstant {
    public static boolean isApply = false;
    public static String UAC_URL = "https://uac.ajyun.com.cn";
    public static String EMC_URL;
    public static String CLOUD_STORAGE_URL;
    public static String BASE_UAC_URL = UAC_URL + "/v1";

    //App启动时需要的公共参数
    public static String URL_GET_APP_CONFIG = "https://sdc.ajyun.com.cn/api/v1/cmd/app-config";

    // uac账号相关
    public static String URL_USER_CHALLENGE = BASE_UAC_URL + "/challenge";
    public static String URL_USER_SIGNIN = BASE_UAC_URL + "/signin";
    public static String URL_USER_SIGNUP = BASE_UAC_URL + "/signup";
    public static String URL_USER_SIGNOUT = BASE_UAC_URL + "/signout";
    public static String URL_USER_USER_ACTIVE = BASE_UAC_URL + "/user-active";
    public static String URL_USER_CHANGE_PASSWORD = BASE_UAC_URL + "/change-password";
    //设备相关
    public static String URL_DEVICE_PREBIND = BASE_UAC_URL + "/req-bind";
    public static String URL_DEVICE_GET_BIND_STATUS = BASE_UAC_URL + "/bind-status";
    public static String URL_DEVICE_GET_DEVICE_INFO = "%1$/cmd/fetch-info";
    public static String URL_DEVICE_SET_DEVICE_NAME = "%1$/cmd/alias-name";

    public static void setBaseUrl(AppConfigBean bean) {
        isApply = true;
        ApiConstant.UAC_URL = bean.uacUrl;
        ApiConstant.EMC_URL = bean.emcUrl;
        ApiConstant.CLOUD_STORAGE_URL = bean.cloudStorUrl;

        applyUrls();
    }

    private static void applyUrls() {
        BASE_UAC_URL = UAC_URL + "/v1";
        // uac账号相关
        URL_USER_CHALLENGE = BASE_UAC_URL + "/challenge";
        URL_USER_SIGNIN = BASE_UAC_URL + "/signin";
        URL_USER_SIGNUP = BASE_UAC_URL + "/signup";
        URL_USER_SIGNOUT = BASE_UAC_URL + "/signout";
        URL_USER_USER_ACTIVE = BASE_UAC_URL + "/user-active";
        URL_USER_CHANGE_PASSWORD = BASE_UAC_URL + "/change-password";
        //设备相关
        URL_DEVICE_PREBIND = BASE_UAC_URL + "/req-bind";
        URL_DEVICE_GET_BIND_STATUS = BASE_UAC_URL + "/bind-status";
    }
}
