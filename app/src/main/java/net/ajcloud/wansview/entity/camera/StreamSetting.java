package net.ajcloud.wansview.entity.camera;

import java.io.Serializable;

/**
 * 视频流设置, 由于流媒体服务器channel的适配问题需与apptype关联
 * User: 锋
 * Date: 12-11-12
 * Time: 下午10:08
 */
public final class StreamSetting implements Serializable {
    /**
     * 应用类别: 3-Phone, 2-平板,  1-Web
     */
    private int apptype;

    /**
     * 分辨率
     */
    private int resolution;

    /**
     * 质量
     */
    private int quality;

    /**
     * 变更时间戳
     */
    private long ts;

    public StreamSetting() {
    }

    public StreamSetting(int apptype, int resolution, int quality, long ts) {
        this.apptype = apptype;
        this.resolution = resolution;
        this.quality = quality;
        this.ts = ts;
    }

    public int getApptype() {
        return apptype;
    }

    public void setApptype(int apptype) {
        this.apptype = apptype;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }
}
