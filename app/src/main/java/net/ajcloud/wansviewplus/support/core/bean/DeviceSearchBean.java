package net.ajcloud.wansviewplus.support.core.bean;

import android.os.Parcel;
import android.os.Parcelable;

import net.ajcloud.wansviewplus.support.utils.DigitalUtils;

/**
 * Created by mamengchao on 2018/05/24.
 * 搜索设备实体类
 */
public class DeviceSearchBean implements Parcelable {
    public String nStartCode;
    public String nCmd;
    private byte[] szIpAddr;         //IP地址
    public String szMask;           //子网掩码
    public String szGateway;        //网关
    public String szDns1;           //dns1
    public String szDns2;           //dns2
    public String szMacAddr;        //设备MAC地址
    public String dhcp;             //=0, 手动设置IP等信息; =1自动从路由器获取IP等信息
    public String reserve;
    private byte[] dwDeviceID;       //设备Id
    public String szDevName;        //设备名称
    public String fwVer;            //固件版本, 如:00.21000.01.01
    public String deviceModel;      //产品型号,如:C18
    public String vendorCode;       //经销商代码,如: WV
    public String bindStatus;       //绑定状态: 0 – 未绑定, 1- 已绑定
    public String reserve1;         //保留

    public String getDeviceID() {
        return DigitalUtils.bytetoString(dwDeviceID);
    }

    public byte[] getDeviceIDBytes() {
        return dwDeviceID;
    }

    public void setDwDeviceID(byte[] dwDeviceID) {
        this.dwDeviceID = dwDeviceID;
    }

    public byte[] getSzIpAddrBytes() {
        return szIpAddr;
    }

    public String getSzIpAddr() {
        return DigitalUtils.bytetoString(szIpAddr);
    }

    public void setSzIpAddr(byte[] szIpAddr) {
        this.szIpAddr = szIpAddr;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nStartCode);
        dest.writeString(this.nCmd);
        dest.writeByteArray(this.szIpAddr);
        dest.writeString(this.szMask);
        dest.writeString(this.szGateway);
        dest.writeString(this.szDns1);
        dest.writeString(this.szDns2);
        dest.writeString(this.szMacAddr);
        dest.writeString(this.dhcp);
        dest.writeString(this.reserve);
        dest.writeByteArray(this.dwDeviceID);
        dest.writeString(this.szDevName);
        dest.writeString(this.fwVer);
        dest.writeString(this.deviceModel);
        dest.writeString(this.vendorCode);
        dest.writeString(this.bindStatus);
        dest.writeString(this.reserve1);
    }

    public DeviceSearchBean() {
    }

    protected DeviceSearchBean(Parcel in) {
        this.nStartCode = in.readString();
        this.nCmd = in.readString();
        this.szIpAddr = in.createByteArray();
        this.szMask = in.readString();
        this.szGateway = in.readString();
        this.szDns1 = in.readString();
        this.szDns2 = in.readString();
        this.szMacAddr = in.readString();
        this.dhcp = in.readString();
        this.reserve = in.readString();
        this.dwDeviceID = in.createByteArray();
        this.szDevName = in.readString();
        this.fwVer = in.readString();
        this.deviceModel = in.readString();
        this.vendorCode = in.readString();
        this.bindStatus = in.readString();
        this.reserve1 = in.readString();
    }

    public static final Parcelable.Creator<DeviceSearchBean> CREATOR = new Parcelable.Creator<DeviceSearchBean>() {
        @Override
        public DeviceSearchBean createFromParcel(Parcel source) {
            return new DeviceSearchBean(source);
        }

        @Override
        public DeviceSearchBean[] newArray(int size) {
            return new DeviceSearchBean[size];
        }
    };
}
