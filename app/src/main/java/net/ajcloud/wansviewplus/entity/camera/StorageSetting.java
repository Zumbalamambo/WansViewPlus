package net.ajcloud.wansviewplus.entity.camera;

import java.io.Serializable;
import java.util.List;

/**
 * 本地存储
 * User: 锋
 * Date: 12-11-8
 * Time: 下午11:34
 */
public final class StorageSetting implements Serializable {

    /**
     * 1 - 循环, 2 - 满了停止
     */
    private int mode;

    /**
     * 仅在画面变化时录制
     * 1 - 持续录制
     * 2 - 移动检测触发 (缺省值)
     */
    private int trigger;

    /**
     * 质量 1-4,缺省: 4
     */
    private int quality;

    /**
     * 触发器
     */
    private List<StorageTrigger> triggers;

    /**
     * 变更时间戳
     */
    private long ts;

    /*
    * 存储类型 0：TF   1：NAS
    */
    private String type;

    /*
    * NAS存储路径
    * */
    private String path;

    /**
     * 本地存储总开关
     */
    private boolean enabled;

    /**
     * StorageSetting ctor.
     */
    public StorageSetting() {
        this.mode = 1;
    }

    /**
     * StorageSetting
     *
     * @param mode
     * @param triggers
     * @param ts
     */
    public StorageSetting(final int mode, final List<StorageTrigger> triggers, long ts) {
        this.mode = mode;
        this.triggers = triggers;
        this.ts = ts;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public List<StorageTrigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<StorageTrigger> triggers) {
        this.triggers = triggers;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public int getTrigger() {
        return trigger;
    }

    public void setTrigger(int trigger) {
        this.trigger = trigger;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
