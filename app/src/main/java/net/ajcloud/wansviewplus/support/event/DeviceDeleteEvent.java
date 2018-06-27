package net.ajcloud.wansviewplus.support.event;

/**
 * Created by mamengchao on 2018/06/27.
 * Function:单个设备删除event
 */
public class DeviceDeleteEvent {
    public String deviceId;

    public DeviceDeleteEvent(String deviceId) {
        this.deviceId = deviceId;
    }
}
