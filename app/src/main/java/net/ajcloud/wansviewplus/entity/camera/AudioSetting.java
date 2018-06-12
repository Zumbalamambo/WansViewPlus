package net.ajcloud.wansviewplus.entity.camera;


import java.io.Serializable;

/**
 * 音频参数
 */
public final class AudioSetting implements Serializable {
    /**
     * 是否可用
     * 0- 不可用(缺省)
     * 1- 可用
     */
    private int enable;

    /**
     * 变更时间戳
     */
    private long ts;

    private int volume;

    public AudioSetting() {
        this.enable = 0;
        this.ts = System.currentTimeMillis();
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
