package net.ajcloud.wansview.support.core.device;

import net.ajcloud.wansview.support.core.bean.AudioInfoBean;
import net.ajcloud.wansview.support.core.bean.CapabilityBean;
import net.ajcloud.wansview.support.core.bean.CloudStorBean;
import net.ajcloud.wansview.support.core.bean.DeviceConfigBean;
import net.ajcloud.wansview.support.core.bean.DeviceTimeBean;
import net.ajcloud.wansview.support.core.bean.LivePolicyBean;
import net.ajcloud.wansview.support.core.bean.LocalStorBean;
import net.ajcloud.wansview.support.core.bean.MoveMonitorBean;
import net.ajcloud.wansview.support.core.bean.NetworkInfoBean;
import net.ajcloud.wansview.support.core.bean.PictureInfoBean;
import net.ajcloud.wansview.support.core.bean.SoundMonitorBean;
import net.ajcloud.wansview.support.core.bean.StreamInfoBean;
import net.ajcloud.wansview.support.core.bean.ViewAnglesBean;

import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by mamengchao on 2018/06/07.
 * Function:    摄像机实体类
 */
public class Camera {
    public int whiteBalance;
    public int freqValue;
    public int nightMode;
    public int orientationValue;
    public String aliasName;
    public String deviceId;
    public String deviceMode;
    public String endpoint;
    public String fwVersion;
    public long onlineModified;
    public int onlineStatus;
    public String remoteAddr;
    public long tunnelSyncTime;
    public String vendorCode;

    public CapabilityBean capability;
    public StreamInfoBean streamConfig;
    public LivePolicyBean livePolicy;
    public NetworkInfoBean networkConfig;
    public ViewAnglesBean viewAnglesConfig;
    public LocalStorBean localStorConfig;
    public MoveMonitorBean moveMonitorConfig;
    public SoundMonitorBean soundMonitorConfig;
    public CloudStorBean cloudStorConfig;
    public AudioInfoBean audioConfig;
    public PictureInfoBean pictureConfig;
    public DeviceTimeBean timeConfig;

    public String sortStr;  //用于排序
    private String gatewayUrl;
    private String tunnelUrl;
    private String cloudStorUrl;
    private String emcUrl;


    private Camera() {

    }

    public Camera(@NotNull String deviceId) {
        this.deviceId = deviceId;
    }

    public Camera(DeviceConfigBean bean) {
        if (bean.base != null) {
            whiteBalance = bean.base.whiteBalance;
            freqValue = bean.base.freqValue;
            nightMode = bean.base.nightMode;
            orientationValue = bean.base.orientationValue;
            onlineModified = bean.base.onlineModified;
            onlineStatus = bean.base.onlineStatus;
            tunnelSyncTime = bean.base.tunnelSyncTime;
            aliasName = bean.base.aliasName;
            deviceId = bean.base.deviceId;
            deviceMode = bean.base.deviceMode;
            endpoint = bean.base.endpoint;
            fwVersion = bean.base.fwVersion;
            remoteAddr = bean.base.remoteAddr;
            vendorCode = bean.base.vendorCode;

            capability = bean.capability;
            streamConfig = bean.streamConfig;
            livePolicy = bean.livePolicy;
            networkConfig = bean.networkConfig;
            viewAnglesConfig = bean.viewAnglesConfig;
            localStorConfig = bean.localStorConfig;
            moveMonitorConfig = bean.moveMonitorConfig;
            soundMonitorConfig = bean.soundMonitorConfig;
            cloudStorConfig = bean.cloudStorConfig;
            audioConfig = bean.audioConfig;
            pictureConfig = bean.pictureConfig;
            timeConfig = bean.timeConfig;
        }
    }

    public String getGatewayUrl() {
        return gatewayUrl;
    }

    public void setGatewayUrl(String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
    }

    public String getTunnelUrl() {
        return tunnelUrl;
    }

    public void setTunnelUrl(String tunnelUrl) {
        this.tunnelUrl = tunnelUrl;
    }

    public String getCloudStorUrl() {
        return cloudStorUrl;
    }

    public void setCloudStorUrl(String cloudStorUrl) {
        this.cloudStorUrl = cloudStorUrl;
    }

    public String getEmcUrl() {
        return emcUrl;
    }

    public void setEmcUrl(String emcUrl) {
        this.emcUrl = emcUrl;
    }
}
