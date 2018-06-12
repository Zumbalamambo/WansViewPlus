package net.ajcloud.wansviewplus.entity.camera;

import java.io.Serializable;

/**
 * 视频参数设置
 * User: 锋
 * Date: 12-11-8
 * Time: 下午11:10
 */
public final class VideoSetting implements Serializable {
    /**
     * 夜景模式
     * 1 - 自动, 0 - 强制关闭
     */
    private int nightmode;

    /**
     * 亮  度:[1,255]
     * 若 < 0 则无效值
     */
    private int brightness;

    /**
     * 对比度:[1,255]
     */
    private int contrast;

    /**
     * 饱和度:[1,255]
     */
    private int saturation;

    /**
     * 清晰度:[1,255]
     */
    private int sharpness;

    /**
     * 1 - 自动
     * 2 - 室内
     * 3 - 室外
     * 4 - 黑白
     * 5 - 荧光灯(白色)
     * 6 - 荧光灯(黄色)
     */
    private int whitebal;

    /**
     * 镜头缩放
     */
    private int zoom;


    /**
     * 图像镜像
     */
    private int mirror;

    /**
     * 变更时间戳
     */
    private long ts;

    /**
     * 缺省值ctor.
     */
    public VideoSetting() {
        //缺省值
        this.brightness = 128;
        this.nightmode = 1;
        this.contrast = 128;
        this.saturation = 128;
        this.sharpness = 128;
        this.zoom = 0;
        this.mirror = 0;
        this.whitebal = 1;
    }

    public VideoSetting(int scene, int brightness, int contrast, int saturation, int sharpness, int whitebal, int zoom, int mirror, long ts) {
        this.nightmode = scene;
        this.brightness = brightness;
        this.contrast = contrast;
        this.saturation = saturation;
        this.sharpness = sharpness;
        this.whitebal = whitebal;
        this.zoom = zoom;
        this.mirror = mirror;
        this.ts = ts;
    }

    public int getNightmode() {
        return nightmode;
    }

    public void setNightmode(int nightmode) {
        this.nightmode = nightmode;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public int getContrast() {
        return contrast;
    }

    public void setContrast(int contrast) {
        this.contrast = contrast;
    }

    public int getSaturation() {
        return saturation;
    }

    public void setSaturation(int saturation) {
        this.saturation = saturation;
    }

    public int getSharpness() {
        return sharpness;
    }

    public void setSharpness(int sharpness) {
        this.sharpness = sharpness;
    }

    public int getWhitebal() {
        return whitebal;
    }

    public void setWhitebal(int whitebal) {
        this.whitebal = whitebal;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public int getMirror() {
        return mirror;
    }

    public void setMirror(int mirror) {
        this.mirror = mirror;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }
}
