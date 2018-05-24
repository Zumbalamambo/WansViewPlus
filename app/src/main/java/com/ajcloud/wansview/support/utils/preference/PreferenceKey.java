package com.ajcloud.wansview.support.utils.preference;

/**
 * Created by mamengchao on 2018/05/14.
 * 常量
 */
public interface PreferenceKey {
    interface sp_name {
        String account = "ACCOUNT";
    }

    interface sp_key {
        //设备唯一标识
        String DEVICE_ID = "DEVICE_ID";
        //access_token
        String ACCESS_TOKEN = "ACCESS_TOKEN";
        //refresh_token
        String REFRESH_TOKEN = "REFRESH_TOKEN";
        //登录状态
        String IS_LOGIN = "IS_LOGIN";
    }
}
