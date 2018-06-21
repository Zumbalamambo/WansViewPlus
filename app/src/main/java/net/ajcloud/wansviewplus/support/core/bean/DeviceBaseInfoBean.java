package net.ajcloud.wansviewplus.support.core.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by mamengchao on 2018/06/06.
 * Function:    设备基础信息
 */
public class DeviceBaseInfoBean{
    public int whiteBalance;
    public int freqValue;
    public String nightMode;
    public String orientationValue;
    public String snapshotUrl;

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
}
