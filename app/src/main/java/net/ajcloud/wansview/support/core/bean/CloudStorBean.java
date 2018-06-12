package net.ajcloud.wansview.support.core.bean;

import java.util.List;

/**
 * Created by mamengchao on 2018/06/06.
 * Function:
 */
public class CloudStorBean {
    public int enable;
    public int quality;
    public List<Policy> policies;

    public static class Policy {
        public int no;
        public int enable;
        public String startTime;
        public String endTime;
        public List<Integer> weekDays;
    }
}
