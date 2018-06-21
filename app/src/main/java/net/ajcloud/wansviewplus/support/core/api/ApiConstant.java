package net.ajcloud.wansviewplus.support.core.api;

import net.ajcloud.wansviewplus.support.core.bean.AppConfigBean;

/**
 * Created by mamengchao on 2018/05/21.
 */
public class ApiConstant {
    public static boolean isApply = false;
    public static String UAC_URL = "https://uac.ajyun.com.cn";
    public static String BASE_UAC_URL = UAC_URL + "/v1";
    public static String EMC_URL = "https://emc.ajyun.com.cn/api";
    public static String BASE_EMC_URL = EMC_URL + "/v1";

    //App启动时需要的公共参数
    public static String URL_GET_APP_CONFIG = "https://sdc.ajyun.com.cn/api/v1/cmd/app-config";
    //设备接入地址信息
    public static String URL_GET_DEVICE_URL_INFO = "https://sdc.ajyun.com.cn/api/v1/cmd/cam-config";

    // uac账号相关
    public static String URL_USER_CHALLENGE = BASE_UAC_URL + "/challenge";
    public static String URL_USER_SIGNIN = BASE_UAC_URL + "/signin";
    public static String URL_USER_SIGNUP = BASE_UAC_URL + "/signup";
    public static String URL_USER_SIGNOUT = BASE_UAC_URL + "/signout";
    public static String URL_USER_USER_ACTIVE = BASE_UAC_URL + "/user-active";
    public static String URL_USER_CHANGE_PASSWORD = BASE_UAC_URL + "/change-password";
    public static String URL_USER_REFRESH_TOKEN = BASE_UAC_URL + "/refresh-token";
    //设备相关
    public static String URL_DEVICE_PREBIND = BASE_UAC_URL + "/req-bind";
    public static String URL_DEVICE_GET_BIND_STATUS = BASE_UAC_URL + "/bind-status";
    public static String URL_DEVICE_GET_DEVICE_LIST = BASE_UAC_URL + "/device-list";
    public static String URL_DEVICE_SET_DEVICE_NAME_UAC = BASE_UAC_URL + "/change-alias-name";
    public static String URL_DEVICE_UNBIND = BASE_UAC_URL + "/unbind";

    public static String URL_DEVICE_PUSH_SETTING = BASE_EMC_URL + "/cmd/push-setting";
    public static String URL_DEVICE_GET_DEVICE_INFO = "/v1/cmd/fetch-info";
    public static String URL_DEVICE_SET_DEVICE_NAME = "/v1/cmd/alias-name";
    public static String URL_DEVICE_GET_FIRST_FRAME = "/v1/cmd/snapshot";
    public static String URL_DEVICE_MOVE_DETECTION = "/v1/cmd/move-monitor-config";
    public static String URL_DEVICE_PLACEMENT = "/v1/cmd/orientation-config";
    public static String URL_DEVICE_NIGHT_VERSION = "/v1/cmd/night-vision-config";
    public static String URL_DEVICE_AUDIO_CONFIG = "/v1/cmd/audio-config";

    public static void setBaseUrl(AppConfigBean bean) {
        isApply = true;
        ApiConstant.UAC_URL = bean.uacUrl;

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
        URL_USER_REFRESH_TOKEN = BASE_UAC_URL + "/refresh-token";
        //设备相关
        URL_DEVICE_PREBIND = BASE_UAC_URL + "/req-bind";
        URL_DEVICE_GET_BIND_STATUS = BASE_UAC_URL + "/bind-status";
        URL_DEVICE_GET_DEVICE_LIST = BASE_UAC_URL + "/device-list";
        URL_DEVICE_SET_DEVICE_NAME_UAC = BASE_UAC_URL + "/change-alias-name";
        URL_DEVICE_UNBIND = BASE_UAC_URL + "/unbind";
        URL_DEVICE_PUSH_SETTING = BASE_EMC_URL + "/cmd/push-setting";
    }
}
