package net.ajcloud.wansviewplus.support.core.device;

import android.text.TextUtils;

import net.ajcloud.wansviewplus.support.core.bean.AudioInfoBean;
import net.ajcloud.wansviewplus.support.core.bean.CapabilityBean;
import net.ajcloud.wansviewplus.support.core.bean.CloudStorBean;
import net.ajcloud.wansviewplus.support.core.bean.CloudStorPlanBean;
import net.ajcloud.wansviewplus.support.core.bean.DeviceConfigBean;
import net.ajcloud.wansviewplus.support.core.bean.DeviceTimeBean;
import net.ajcloud.wansviewplus.support.core.bean.LivePolicyBean;
import net.ajcloud.wansviewplus.support.core.bean.LocalStorBean;
import net.ajcloud.wansviewplus.support.core.bean.MoveMonitorBean;
import net.ajcloud.wansviewplus.support.core.bean.NetworkInfoBean;
import net.ajcloud.wansviewplus.support.core.bean.PictureInfoBean;
import net.ajcloud.wansviewplus.support.core.bean.SoundMonitorBean;
import net.ajcloud.wansviewplus.support.core.bean.StreamInfoBean;
import net.ajcloud.wansviewplus.support.core.bean.ViewAnglesBean;

import org.greenrobot.greendao.annotation.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by mamengchao on 2018/06/07.
 * Function:    摄像机实体类
 */
public class Camera implements Serializable {
    private static final long serialVersionUID = 1L;
    public int whiteBalance;
    public int freqValue;
    public String nightMode;
    public String orientationValue;
    public String aliasName;
    public String deviceId;
    public String deviceMode;
    public String endpoint;
    public String fwVersion;
    public long onlineModified;
    public int onlineStatus;     // 离线 - 1, 在线 - 2, 升级中 - 4
    public String remoteAddr;
    public long tunnelSyncTime;
    public String vendorCode;
    public String snapshotUrl;
    public String accessKey;

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
    public CloudStorPlanBean cloudStorPlan;

    public int refreshStatus;  //刷新状态   0：正在刷新    1：成功    2：失败
    public String sortStr;  //用于排序
    private String gatewayUrl;
    private String tunnelUrl;
    private String cloudStorUrl;
    private String emcUrl;
    private String accessPriKey;
    private String accessPubKey;


    public Camera() {

    }

//    public Camera(@NotNull String deviceId, String name) {
//        this.deviceId = deviceId;
//        this.aliasName = name;
//    }

    public Camera(@NotNull String deviceId, String name, String accessPriKey, String accessPubKey) {
        this.deviceId = deviceId;
        this.aliasName = name;
        this.accessPriKey = accessPriKey;
        this.accessPubKey = accessPubKey;
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
            snapshotUrl = bean.base.snapshotUrl;
            accessKey = bean.base.accessKey;

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
            cloudStorPlan = bean.cloudStorPlan;
        }
    }

    public String getGatewayUrl() {
        return TextUtils.isEmpty(gatewayUrl) ? null : gatewayUrl;
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

    public String getAccessPriKey() {
        return accessPriKey;
    }

    public void setAccessPriKey(String accessPriKey) {
        this.accessPriKey = accessPriKey;
    }

    public String getAccessPubKey() {
        return accessPubKey;
    }

    public void setAccessPubKey(String accessPubKey) {
        this.accessPubKey = accessPubKey;
    }

    public boolean isOnline() {
        return onlineStatus == 2;
    }

    public Object deepClone() {
        try {
            // 序列化
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            oos.writeObject(this);

            // 反序列化
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);

            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
