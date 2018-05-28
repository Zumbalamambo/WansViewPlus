package net.ajcloud.wansview.entity.camera;

import java.io.Serializable;
import java.util.List;

/**
 * 运动监测
 * User: 锋
 * Date: 12-11-8
 * Time: 下午11:37
 */
public final class MotiondetectSetting implements Serializable {
    /**
     * 灵敏度
     */
    private int susceptiveness;

    /**
     * 变更时间戳
     */
    private long ts;

    /**
     * 触发器
     */
    private List<MotiondetectTrigger> triggers;

    /**
     * 移动告警总开关
     */
    private boolean enabled;

    public MotiondetectSetting() {
    }

    public MotiondetectSetting(int susceptiveness, long ts, List<MotiondetectTrigger> triggers) {
        this.susceptiveness = susceptiveness;
        this.ts = ts;
        this.triggers = triggers;
    }

    public int getSusceptiveness() {
        return susceptiveness;
    }

    public void setSusceptiveness(int susceptiveness) {
        this.susceptiveness = susceptiveness;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public List<MotiondetectTrigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<MotiondetectTrigger> triggers) {
        this.triggers = triggers;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
