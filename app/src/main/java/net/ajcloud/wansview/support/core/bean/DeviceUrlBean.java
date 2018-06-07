package net.ajcloud.wansview.support.core.bean;

import java.util.List;

/**
 * Created by mamengchao on 2018/06/07.
 * Function:    设备接入地址信息
 */
public class DeviceUrlBean {

    public List<UrlInfo> devices;

    public static class UrlInfo {
        public String deviceId;
        public String gatewayUrl;
        public String tunnelUrl;
        public String cloudStorUrl;
        public String emcUrl;
    }
}
