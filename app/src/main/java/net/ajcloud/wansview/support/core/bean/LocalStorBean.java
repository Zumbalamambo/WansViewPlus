package net.ajcloud.wansview.support.core.bean;

import java.util.List;

/**
 * Created by mamengchao on 2018/06/06.
 * Function:    本地存储信息
 */
public class LocalStorBean {
    public int enable;
    public int storageType;
    public int writeMode;
    public int triggerMode;
    public String nasPath;
    public String playUrlRoot;
    public List<Policy> policies;

    public static class Policy{
        public int no;
        public int enable;
        public int format;
        public int quality;
        public String startTime;
        public String endTime;
    }
}
