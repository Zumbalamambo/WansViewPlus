package net.ajcloud.wansviewplus.support.core.device;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.MainApplication;

import java.util.HashMap;

/**
 * Created by mamengchao on 2018/06/04.
 * Function: 设备信息字典
 */
public class DeviceInfoDictionary {

    private static final HashMap<String, DeviceInfoDataBean> deviceDataMap = new HashMap<>();

    /**
     * type         设备的type
     * typeID       自增
     */
    private static final Object[][] deviceInfoData = new Object[][]{
            // type		typeId     nameRes
            // k3
            {"K03", 1, R.string.camera_default_name_k3, R.mipmap.ic_model_k3},
            // q3
            {"Q03", 2, R.string.camera_default_name_q3, R.mipmap.ic_model_q3},
            // w2
            {"W02", 3, R.string.camera_default_name_w2, R.mipmap.ic_model_w2}
    };

    static {
        for (int i = 0; i < deviceInfoData.length; i++) {
            deviceDataMap.put((String) deviceInfoData[i][0], new DeviceInfoDataBean(deviceInfoData[i]));
        }
    }

    /**
     * 设备type到图标资源映射
     *
     * @param type
     * @return
     */
    public static int getIconByType(String type) {
        if (type == null) {
            return R.mipmap.ic_launcher;
        }

        DeviceInfoDataBean bean = deviceDataMap.get(type);
        if (bean == null) {
            return R.mipmap.ic_launcher;
        }
        return bean.iconRes;
    }

    /**
     * 设备type到名称资源映射
     *
     * @param type
     * @return
     */
    public static int getDefaultNameByType(String type) {
        if (type == null) {
            return R.string.camera_unknow;
        }
        if (type.startsWith("K03")) {
            return R.string.camera_default_name_k3;
        } else if (type.startsWith("Q03")) {
            return R.string.camera_default_name_q3;
        } else if (type.startsWith("W02")) {
            return R.string.camera_default_name_w2;
        }

        DeviceInfoDataBean bean = deviceDataMap.get(type);
        if (bean == null) {
            return R.string.camera_unknow;
        }
        return bean.nameRes;
    }

    public static String getNameByDevice(Camera camera) {
        if (camera == null) {
            return MainApplication.getApplication().getResources().getString(R.string.camera_unknow);
        } else {
            String deviceName = camera.aliasName;
            if (deviceName == null) {
                deviceName = MainApplication.getApplication().getResources().getString(getDefaultNameByType(camera.deviceMode));
            }
            return deviceName;
        }
    }

    private static class DeviceInfoDataBean {
        public String type;
        public int typeId;
        public int nameRes;
        public int iconRes;

        public DeviceInfoDataBean(Object[] data) {
            this.type = (String) data[0];
            this.typeId = (int) data[1];
            this.nameRes = (int) data[2];
            this.iconRes = (int) data[3];
        }
    }
}
