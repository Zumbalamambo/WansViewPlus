package net.ajcloud.wansview.support.core.device;

import android.support.annotation.NonNull;

import net.ajcloud.wansview.entity.camera.Camera;

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

    public void add(@NonNull Camera device) {
        Camera cameraOrigin = hashTable.get(device.getOid());
        if (cameraOrigin == null) {
            hashTable.put(device.getOid(), device);
        } else {
            mergeDeviceInfo(cameraOrigin, device);
        }
    }

    //合并设备信息
    private void mergeDeviceInfo(Camera cameraOrigin, Camera camera) {

    }

    public void remove(@NonNull Camera camera) {
        remove(camera.getOid());
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
