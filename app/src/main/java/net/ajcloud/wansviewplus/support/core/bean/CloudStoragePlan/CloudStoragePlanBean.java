package net.ajcloud.wansviewplus.support.core.bean.CloudStoragePlan;

import java.util.List;

/**
 * Created by mamengchao on 2018/07/20.
 * Function:
 */
public class CloudStoragePlanBean {

    public List<CloudStoragePlan> plans;

    public static class CloudStoragePlan {
        private Package monthly;
        private Package quarterly;
        private Package yearly;
        private List<String> payModes;
        private String trialDays;
        private String limitDevices;
        private String alarmVideo;
        private String limitFullDayDevices;
        private String status;
        private String _id;
        private String sku;
        private String level;
        private String cycleDays;
    }
}
