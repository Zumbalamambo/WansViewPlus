package net.ajcloud.wansview.entity.camera;

import java.io.Serializable;

/**
 * 运动检测 触发器
 */
public final class MotiondetectTrigger implements Serializable, Comparable {
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
     * 模式， 0-不处理，1-警告， 2-本地存储， 3-云存储
     */
    private String mode;

    public MotiondetectTrigger() {

    }

    public MotiondetectTrigger(int no, boolean enabled, String daypart, String weekdays, String mode) {
        this.no = no;
        this.enabled = enabled;
        this.daypart = daypart;
        this.weekdays = weekdays;
        this.mode = mode;
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

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public int compareTo(Object another) {
        if (!(another instanceof MotiondetectTrigger))
            throw new ClassCastException();
        return no - ((MotiondetectTrigger) another).getNo();
    }
}
