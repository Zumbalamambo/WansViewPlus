package net.ajcloud.wansview.support.core.bean;

import java.util.List;

/**
 * Created by mamengchao on 2018/06/06.
 * Function:
 */
public class MoveMonitorBean {
    public int enable;
    public int susceptiveness;
    public List<Policy> policies;

    public static class Policy{
        private int no;
        private int enable;
        private int respondMode;
        private String startTime;
        private String endTime;
        private List<Integer> weekDays;
    }

}
