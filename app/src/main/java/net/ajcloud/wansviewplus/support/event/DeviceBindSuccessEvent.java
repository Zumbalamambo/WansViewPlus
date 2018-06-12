package net.ajcloud.wansviewplus.support.event;

/**
 * Created by mamengchao on 2018/06/11.
 * Function: 设备绑定成功event
 */
public class DeviceBindSuccessEvent {
    public String deviceId;

    public DeviceBindSuccessEvent(String deviceId) {
        this.deviceId = deviceId;
    }
}
