package net.ajcloud.wansviewplus.support.core.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by mamengchao on 2018/06/06.
 * Function:    设备能力集信息
 */
public class CapabilityBean implements Serializable {
    public String vendorCode;              //厂商代码
    public String mode;                     //产品类型
    public String fw_version;               //固件版本
    public String streams;                  //流数量
    public String resolutions;              //分辨率
    public String qualities;                //视频质量  1:HD,5:FHD
    public String networkConfig;           //配网模式  eth, qr..
    public int ptz;                      //云台
    public String audioSample;             //音频采样
    public int autoTrack;               //自动追踪
    public String pirDetect;               //红外移动监测
    public String voiceDetect;             //声音监控
    public int duplexVoice;             //实时双向语音
    public String localStorageTypes;      //本地存储方式
    public String battery;                  //电池供电
    public String encryptMode;             //加密模式
    public int diagnose;                 //诊断支持

    //获取视频质量
    public List<String> getVideoQualities() {
        if (TextUtils.isEmpty(this.qualities)) {
            return null;
        } else {
            List<String> qualities = Arrays.asList(this.qualities.split(","));
            Collections.reverse(qualities);
            return qualities;
        }
    }
}
