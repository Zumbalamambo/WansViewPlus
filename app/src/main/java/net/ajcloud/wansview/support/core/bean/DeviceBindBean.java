package net.ajcloud.wansview.support.core.bean;

import net.ajcloud.wansview.support.utils.DigitalUtils;

/**
 * Created by mamengchao on 2018/06/05.
 * Function:    绑定设备实体类
 */
public class DeviceBindBean {
    public String nStartCode;
    public String nCmd;
    private byte[] dwDeviceID;
    public String nErrorCode;
    public String reserve;

    public String getDeviceID() {
        return DigitalUtils.bytetoString(dwDeviceID);
    }

    public byte[] getDeviceIDBytes() {
        return dwDeviceID;
    }

    public void setDwDeviceID(byte[] dwDeviceID) {
        this.dwDeviceID = dwDeviceID;
    }
}
