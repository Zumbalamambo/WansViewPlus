package net.ajcloud.wansviewplus.support.utils.preference;

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
        //是否使用指纹
        String USE_GESTURE = "USE_GESTURE";
    }
}
