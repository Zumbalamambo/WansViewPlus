package net.ajcloud.wansviewplus.entity.camera;

import java.io.Serializable;

/**
 * 本地存储触发器
 */
public final class StorageTrigger implements Serializable, Comparable {
    /**
     * 编号
     */
    private int no;

    /**
     * 是否off/on
     */
    private boolean enabled;

    /**
     * 时段，格式如 08:00-17:30
     */
    private String daypart;

    /**
     * 周，格式如: 1,2[,3,4,5,6,7]
     */
    private String weekdays;

    /**
     * 1 - 视频, 2 - 图片
     */
    private int format;


    public StorageTrigger(int no, boolean enabled, String daypart, String weekdays, int format) {
        this.no = no;
        this.enabled = enabled;
        this.daypart = daypart;
        this.weekdays = weekdays;
        this.format = format;
    }

    public StorageTrigger() {
        this.format = 1;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDaypart() {
        return daypart;
    }

    public void setDaypart(String daypart) {
        this.daypart = daypart;
    }

    public String getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(String weekdays) {
        this.weekdays = weekdays;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    @Override
    public int compareTo(Object another) {
        if (!(another instanceof StorageTrigger))
            throw new ClassCastException();
        return no - ((StorageTrigger) another).getNo();
    }
}
