package net.ajcloud.wansview.entity.camera;

import java.io.Serializable;

/**
 * Created by zte on 2016/5/23.
 */
public class CameraFeatures implements Serializable {
    String[] storagetype;
    int autotrack;
    int ptz;
    int[] audiosample;
    int dlnaconfig;
    int duplexvoice;
    String[] resolution;
    int channelnum;
    int ivw;
    int[] encryptmode;
    int cloudstorage;
    int pirdetect;
    String[] wificonfig;
    int pubshare;
    String[] historyvideo;
    /**
     * 本地存储时间能力集
     * 0-不支持， 1-支持
     * */
    int localstorageenable;
    
    /**
     * 按键模式：0-不支持，1-支持
     */
    int modekeyconfig;
    
    /**
     * 按键模式支持功能数组：
     */
    int[] modekey;
    
    int sdremove;
    int sdformat;
    
    /**
     * 在线诊断 0-不支持  1-支持
     */
    int diagnose;

    /**
     * syslog 联通性诊断能力集
     * 0-关闭 1-开启
     */
    private int connectivitydiagnose;

    private int cloudstoragetimepolicy;

    private int localfilerm;
    
    /**
     * 门锁联动视频录制：0:关闭，1:开启
     */
    private int locklinkvideo;
    
    /**
     * 摄像机直播指示灯提示：0：关闭，1：开启
     */
    private int rtspliveprompt;
    
    /**
     * 时区配置：0：关闭，1：开启
     */
    private int sntp;

    public String[] getStoragetype() {
        return storagetype;
    }

    public int getAutotrack() {
        return autotrack;
    }

    public int getPtz() {
        return ptz;
    }

    public int[] getAudiosample() {
        return audiosample;
    }

    public int getDlnaconfig() {
        return dlnaconfig;
    }

    public int getDuplexvoice() {
        return duplexvoice;
    }

    public String[] getResolution() {
        return resolution;
    }

    public int getChannelnum() {
        return channelnum;
    }

    public int getIvw() {
        return ivw;
    }

    public int[] getEncryptmode() {
        return encryptmode;
    }

    public int getCloudstorage() {
        return cloudstorage;
    }

    public int getPirdetect() {
        return pirdetect;
    }

    public String[] getWificonfig() {
        return wificonfig;
    }

    public int getPubshare() {
        return pubshare;
    }

    public String[] getHistoryvideo() {
        return historyvideo;
    }

    public int getModekeyconfig() {
        return modekeyconfig;
    }

    public void setModekeyconfig(int modekeyconfig) {
        this.modekeyconfig = modekeyconfig;
    }

    public int getSdremove() {
        return sdremove;
    }

    public void setSdremove(int sdremove) {
        this.sdremove = sdremove;
    }

    public int getSdformat() {
        return sdformat;
    }

    public void setSdformat(int sdformat) {
        this.sdformat = sdformat;
    }

    public int getDiagnose() {
        return diagnose;
    }

    public void setDiagnose(int diagnose) {
        this.diagnose = diagnose;
    }

    /**
     * SD文件删除  0-不支持 1-支持
     */
    public int getLocalfilerm() {
        return localfilerm;
    }

    public void setLocalfilerm(int localfilerm) {
        this.localfilerm = localfilerm;
    }

    /**
     * 云存储时间策略
     * */
    public int getCloudstoragetimepolicy() {
        return cloudstoragetimepolicy;
    }

    public void setCloudstoragetimepolicy(int cloudstoragetimepolicy) {
        this.cloudstoragetimepolicy = cloudstoragetimepolicy;
    }
    
    public int getLocklinkvideo()
    {
        return locklinkvideo;
    }
    
    public void setLocklinkvideo(int locklinkvideo)
    {
        this.locklinkvideo = locklinkvideo;
    }
	
	 public int getConnectivitydiagnose() {
        return connectivitydiagnose;
    }

    public void setConnectivitydiagnose(int connectivitydiagnose) {
        this.connectivitydiagnose = connectivitydiagnose;
    }

    public int getRtspliveprompt() {
        return rtspliveprompt;
    }

    public void setRtspliveprompt(int rtspliveprompt) {
        this.rtspliveprompt = rtspliveprompt;
    }

    public int getSntp() {
        return sntp;
    }

    public void setSntp(int sntp) {
        this.sntp = sntp;
    }

    public int getLocalstorageenable() {
        return localstorageenable;
    }

    public void setLocalstorageenable(int localstorageenable) {
        this.localstorageenable = localstorageenable;
    }
}
