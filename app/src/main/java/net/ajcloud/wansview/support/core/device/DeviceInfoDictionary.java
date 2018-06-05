package net.ajcloud.wansview.support.core.device;

import net.ajcloud.wansview.R;

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
            {"K3", 1, R.string.camera_default_name_k3},
            // q3
            {"Q3", 2, R.string.camera_default_name_q3},
            // w2
            {"W2", 3, R.string.camera_default_name_w2}
    };

    static {
        for (int i = 0; i < deviceInfoData.length; i++) {
            deviceDataMap.put((String) deviceInfoData[i][0], new DeviceInfoDataBean(deviceInfoData[i]));
        }
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
        if (type.startsWith("K3")) {
            return R.string.camera_default_name_k3;
        } else if (type.startsWith("Q3")) {
            return R.string.camera_default_name_q3;
        } else if (type.startsWith("W2")) {
            return R.string.camera_default_name_w2;
        }

        DeviceInfoDataBean bean = deviceDataMap.get(type);
        if (bean == null) {
            return R.string.camera_unknow;
        }
        return bean.nameRes;
    }

    private static class DeviceInfoDataBean {
        public String type;
        public int typeId;
        public int nameRes;

        public DeviceInfoDataBean(Object[] data) {
            this.type = (String) data[0];
            this.typeId = (int) data[1];
            this.nameRes = (int) data[2];
        }
    }
}
