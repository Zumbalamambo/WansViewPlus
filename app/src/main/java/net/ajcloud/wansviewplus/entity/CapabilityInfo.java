package net.ajcloud.wansviewplus.entity;

import net.ajcloud.wansviewplus.support.core.bean.CapabilityBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by mamengchao on 2018/07/13.
 * Function:    能力集
 */
@Entity
public class CapabilityInfo {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    @Unique
    private String mode;                     //产品类型
    private String vendorCode;              //厂商代码
    private String fw_version;               //固件版本
    private String streams;                  //流数量
    private String resolutions;              //分辨率
    private String qualities;                //视频质量  1:HD,5:FHD
    private String networkConfig;           //配网模式  eth, qr..
    private String ptz;                      //云台
    private String audioSample;             //音频采样
    private String autoTrack;               //自动追踪
    private String pirDetect;               //红外移动监测
    private String voiceDetect;             //声音监控
    private String duplexVoice;             //实时双向语音
    private String localStorageTypes;      //本地存储方式
    private String battery;                  //电池供电
    private String encryptMode;             //加密模式
    private String diagnose;                 //诊断支持

    public CapabilityInfo(CapabilityBean bean) {
        this.mode = bean.mode;
        this.vendorCode = bean.vendorCode;
        this.fw_version = bean.fw_version;
        this.streams = bean.streams;
        this.resolutions = bean.resolutions;
        this.qualities = bean.qualities;
        this.networkConfig = bean.networkConfig;
        this.ptz = bean.ptz;
        this.audioSample = bean.audioSample;
        this.autoTrack = bean.autoTrack;
        this.pirDetect = bean.pirDetect;
        this.voiceDetect = bean.voiceDetect;
        this.duplexVoice = bean.duplexVoice;
        this.localStorageTypes = bean.localStorageTypes;
        this.battery = bean.battery;
        this.encryptMode = bean.encryptMode;
        this.diagnose = bean.diagnose;
    }

    @Generated(hash = 1830030451)
    public CapabilityInfo(Long id, @NotNull String mode, String vendorCode,
            String fw_version, String streams, String resolutions, String qualities,
            String networkConfig, String ptz, String audioSample, String autoTrack,
            String pirDetect, String voiceDetect, String duplexVoice,
            String localStorageTypes, String battery, String encryptMode,
            String diagnose) {
        this.id = id;
        this.mode = mode;
        this.vendorCode = vendorCode;
        this.fw_version = fw_version;
        this.streams = streams;
        this.resolutions = resolutions;
        this.qualities = qualities;
        this.networkConfig = networkConfig;
        this.ptz = ptz;
        this.audioSample = audioSample;
        this.autoTrack = autoTrack;
        this.pirDetect = pirDetect;
        this.voiceDetect = voiceDetect;
        this.duplexVoice = duplexVoice;
        this.localStorageTypes = localStorageTypes;
        this.battery = battery;
        this.encryptMode = encryptMode;
        this.diagnose = diagnose;
    }

    @Generated(hash = 621108751)
    public CapabilityInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMode() {
        return this.mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getVendorCode() {
        return this.vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getFw_version() {
        return this.fw_version;
    }

    public void setFw_version(String fw_version) {
        this.fw_version = fw_version;
    }

    public String getStreams() {
        return this.streams;
    }

    public void setStreams(String streams) {
        this.streams = streams;
    }

    public String getResolutions() {
        return this.resolutions;
    }

    public void setResolutions(String resolutions) {
        this.resolutions = resolutions;
    }

    public String getQualities() {
        return this.qualities;
    }

    public void setQualities(String qualities) {
        this.qualities = qualities;
    }

    public String getNetworkConfig() {
        return this.networkConfig;
    }

    public void setNetworkConfig(String networkConfig) {
        this.networkConfig = networkConfig;
    }

    public String getPtz() {
        return this.ptz;
    }

    public void setPtz(String ptz) {
        this.ptz = ptz;
    }

    public String getAudioSample() {
        return this.audioSample;
    }

    public void setAudioSample(String audioSample) {
        this.audioSample = audioSample;
    }

    public String getAutoTrack() {
        return this.autoTrack;
    }

    public void setAutoTrack(String autoTrack) {
        this.autoTrack = autoTrack;
    }

    public String getPirDetect() {
        return this.pirDetect;
    }

    public void setPirDetect(String pirDetect) {
        this.pirDetect = pirDetect;
    }

    public String getVoiceDetect() {
        return this.voiceDetect;
    }

    public void setVoiceDetect(String voiceDetect) {
        this.voiceDetect = voiceDetect;
    }

    public String getDuplexVoice() {
        return this.duplexVoice;
    }

    public void setDuplexVoice(String duplexVoice) {
        this.duplexVoice = duplexVoice;
    }

    public String getLocalStorageTypes() {
        return this.localStorageTypes;
    }

    public void setLocalStorageTypes(String localStorageTypes) {
        this.localStorageTypes = localStorageTypes;
    }

    public String getBattery() {
        return this.battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getEncryptMode() {
        return this.encryptMode;
    }

    public void setEncryptMode(String encryptMode) {
        this.encryptMode = encryptMode;
    }

    public String getDiagnose() {
        return this.diagnose;
    }

    public void setDiagnose(String diagnose) {
        this.diagnose = diagnose;
    }
}
