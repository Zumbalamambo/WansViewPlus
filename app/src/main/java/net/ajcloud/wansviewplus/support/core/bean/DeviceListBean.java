package net.ajcloud.wansviewplus.support.core.bean;

import java.util.List;

/**
 * Created by mamengchao on 2018/06/08.
 * Function:   设备列表bean
 */
public class DeviceListBean {
    public List<Device> cameras;

    public static class Device {
        public String deviceid;
        public String name;
    }
}
