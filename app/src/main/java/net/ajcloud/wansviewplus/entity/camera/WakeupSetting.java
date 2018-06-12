package net.ajcloud.wansviewplus.entity.camera;

import java.io.Serializable;

/**
 * 定时唤醒
 * User: 锋
 * Date: 12-11-8
 * Time: 下午11:30
 */
public final class WakeupSetting implements Serializable {
    /**
     * 是否off/on
     */
    private boolean enabled;

    /**
     * 时段1，格式如 08:00-17:30
     */
    private String daypart1;

    /**
     * 时段2，格式如 08:00-17:30
     */
    private String daypart2;

    /**
     * 周，格式如: 1,2[,3,4,5,6,7]
     */
    private String weekdays;

    /**
     * 变更时间戳
     */
    private long ts;

    public WakeupSetting() {
    }

    public WakeupSetting(boolean enabled, String daypart1, String daypart2, String weekdays, long ts) {
        this.enabled = enabled;
        this.ts = ts;
        this.weekdays = weekdays;
        this.daypart2 = daypart2;
        this.daypart1 = daypart1;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDaypart1() {
        return daypart1;
    }

    public void setDaypart1(String daypart1) {
        this.daypart1 = daypart1;
    }

    public String getDaypart2() {
        return daypart2;
    }

    public void setDaypart2(String daypart2) {
        this.daypart2 = daypart2;
    }

    public String getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(String weekdays) {
        this.weekdays = weekdays;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }
}
