package net.ajcloud.wansviewplus.entity.camera;

import java.io.Serializable;

/**
 * 自动追踪设置
 */
public class AutotrackSetting implements Serializable {
    /**
     * 0 - 关闭， 1-开启
     */
    private int enable;

    /**
     * 灵敏度
     */
    private int susceptiveness;

    /**
     * 变更时间戳
     */
    private long ts;

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
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
}
