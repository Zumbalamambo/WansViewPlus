package net.ajcloud.wansview.support.core.device;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import net.ajcloud.wansview.support.core.bean.DeviceConfigBean;
import net.ajcloud.wansview.support.utils.Trans2PinYin;

import java.util.Collection;
import java.util.Hashtable;

/**
 * Created by mamengchao on 2018/06/04.
 * Function: 设备缓存
 */
public class DeviceCache {

    private Hashtable<String, Camera> hashTable = new Hashtable<>();

    public DeviceCache() {
    }

    public void add(@NonNull Camera camera) {
        Camera cameraOrigin = hashTable.get(camera.deviceId);
        if (cameraOrigin == null) {
            hashTable.put(camera.deviceId, camera);
            if (camera.aliasName != null) {
                camera.sortStr = Trans2PinYin.trans2PinYin(DeviceInfoDictionary.getNameByDevice(camera).trim()).toLowerCase();
            }
        } else {
            mergeDeviceInfo(cameraOrigin, camera);
        }
    }

    public void add(@NonNull DeviceConfigBean bean) {
        Camera camera = new Camera(bean);
        if (camera.deviceId != null) {
            Camera cameraOrigin = hashTable.get(camera.deviceId);
            if (cameraOrigin == null) {
                hashTable.put(camera.deviceId, camera);
            } else {
                mergeDeviceInfo(cameraOrigin, camera);
            }
        }
    }

    //合并设备信息
    private void mergeDeviceInfo(Camera cameraOrigin, Camera camera) {
        if (!TextUtils.equals(cameraOrigin.aliasName, camera.aliasName)) {
            if (camera.aliasName != null) {
                cameraOrigin.sortStr = Trans2PinYin.trans2PinYin(DeviceInfoDictionary.getNameByDevice(camera).trim()).toLowerCase();
            } else {
                cameraOrigin.sortStr = "";
            }
        }
        cameraOrigin.whiteBalance = camera.whiteBalance;
        cameraOrigin.freqValue = camera.freqValue;
        cameraOrigin.nightMode = camera.nightMode;
        cameraOrigin.orientationValue = camera.orientationValue;
        cameraOrigin.onlineModified = camera.onlineModified;
        cameraOrigin.onlineStatus = camera.onlineStatus;
        cameraOrigin.tunnelSyncTime = camera.tunnelSyncTime;
        cameraOrigin.aliasName = camera.aliasName;
        cameraOrigin.deviceId = camera.deviceId;
        cameraOrigin.deviceMode = camera.deviceMode;
        cameraOrigin.endpoint = camera.endpoint;
        cameraOrigin.fwVersion = camera.fwVersion;
        cameraOrigin.remoteAddr = camera.remoteAddr;
        cameraOrigin.vendorCode = camera.vendorCode;

        cameraOrigin.capability = camera.capability;
        cameraOrigin.streamConfig = camera.streamConfig;
        cameraOrigin.livePolicy = camera.livePolicy;
        cameraOrigin.networkConfig = camera.networkConfig;
        cameraOrigin.viewAnglesConfig = camera.viewAnglesConfig;
        cameraOrigin.localStorConfig = camera.localStorConfig;
        cameraOrigin.moveMonitorConfig = camera.moveMonitorConfig;
        cameraOrigin.soundMonitorConfig = camera.soundMonitorConfig;
        cameraOrigin.cloudStorConfig = camera.cloudStorConfig;
        cameraOrigin.audioConfig = camera.audioConfig;
        cameraOrigin.pictureConfig = camera.pictureConfig;
        cameraOrigin.timeConfig = camera.timeConfig;
    }

    public void remove(@NonNull Camera camera) {
        remove(camera.deviceId);
    }

    public void remove(@NonNull String devID) {
        hashTable.remove(devID);
    }

    public Camera get(@NonNull String deviceId) {
        return hashTable.get(deviceId);
    }

    public Collection<Camera> getDevices() {
        return hashTable.values();
    }

    public boolean contains(String deviceID) {
        return get(deviceID) != null;
    }

    public void clear() {
        hashTable.clear();
    }
}
