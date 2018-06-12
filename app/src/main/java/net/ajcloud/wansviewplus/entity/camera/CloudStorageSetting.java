package net.ajcloud.wansviewplus.entity.camera;

import java.io.Serializable;
import java.util.List;

/**
 * Created by smilence on 2014/8/27.
 */
public class CloudStorageSetting implements Serializable {
    /**
     * NONE
     */
    public static final int PRISTRATEGY_NONE = 1;

    /**
     * 直播优先
     */
    public static final int PRISTRATEGY_CLOSE_ONLIVE = 2;

    /**
     * 是否可用
     */
    private boolean enabled;

    /**
     * 到期时间(秒)
     * -1 - 未设置
     */
    private Long expire;

    /**
     * 1 - 未设置优先级策略
     * 2 - 播放时关闭云存储
     */
    private int pristrategy;


    /**
     * 变更时间戳
     */
    private long ts;

    /**
     * 质量 1-4,缺省: 4
     */
    private int quality;

    /**
     * 触发器
     */
    private List<StorageTrigger> triggers;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public int getPristrategy() {
        return pristrategy;
    }

    public void setPristrategy(int pristrategy) {
        this.pristrategy = pristrategy;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public List<StorageTrigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<StorageTrigger> triggers) {
        this.triggers = triggers;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }
}
