package net.ajcloud.wansview.support.event;

/**
 * Created by mamengchao on 2018/06/11.
 * Function:    设备列表刷新event
 */
public class DeviceRefreshEvent {
    public String deviceId;

    public DeviceRefreshEvent(String deviceId) {
        this.deviceId = deviceId;
    }
}
