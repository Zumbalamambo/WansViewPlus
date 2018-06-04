package net.ajcloud.wansview.support.core.bean;

import java.util.List;

/**
 * Created by mamengchao on 2018/06/04.
 * Function:App启动时需要的公共参数
 */
public class AppConfigBean {
    public String uacUrl;
    public String emcUrl;
    public String cloudStorUrl;
    public List<StunServer> stunServers;

    public static class StunServer {
        public String udp;
    }
}
