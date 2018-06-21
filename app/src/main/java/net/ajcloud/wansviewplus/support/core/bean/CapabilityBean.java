package net.ajcloud.wansviewplus.support.core.bean;

import java.io.Serializable;

/**
 * Created by mamengchao on 2018/06/06.
 * Function:    设备能力集信息
 */
public class CapabilityBean implements Serializable {
    public String vendor_code;              //厂商代码
    public String mode;                     //产品类型
    public String fw_version;               //固件版本
    public String streams;                  //流数量
    public String resolutions;              //分辨率
    public String qualities;                //视频质量  1:HD,5:FHD
    public String network_config;           //配网模式  eth, qr..
    public String ptz;                      //云台
    public String audio_sample;             //音频采样
    public String auto_track;               //自动追踪
    public String pir_detect;               //红外移动监测
    public String voice_detect;             //声音监控
    public String duplex_voice;             //实时双向语音
    public String local_storage_types;      //本地存储方式
    public String battery;                  //电池供电
    public String encrypt_mode;             //加密模式
    public String diagnose;                 //诊断支持
}
