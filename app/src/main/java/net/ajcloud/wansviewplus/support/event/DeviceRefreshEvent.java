package net.ajcloud.wansviewplus.support.event;

/**
 * Created by mamengchao on 2018/06/11.
 * Function:    单个设备刷新event
 */
public class DeviceRefreshEvent {
    public String deviceId;

    public DeviceRefreshEvent(String deviceId) {
        this.deviceId = deviceId;
    }
}
