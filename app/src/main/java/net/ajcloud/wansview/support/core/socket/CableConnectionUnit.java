package net.ajcloud.wansview.support.core.socket;

import android.support.annotation.NonNull;

import net.ajcloud.wansview.support.core.bean.DeviceBindBean;
import net.ajcloud.wansview.support.core.bean.DeviceSearchBean;
import net.ajcloud.wansview.support.tools.WLog;
import net.ajcloud.wansview.support.utils.DigitalUtils;

import org.greenrobot.greendao.annotation.NotNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mamengchao on 2018/06/04.
 * Function: 有线直连配网模块
 */
public class CableConnectionUnit {
    private static final String TAG = "CableConnectionUnit";
    //发现命令
    private byte[] keySearch = new byte[]{0x41, 0x4a, 0x01, 0x01};
    //绑定指令
    private byte[] keyBindHead = new byte[]{0x41, 0x4a, 0x01, 0x03};

    private DatagramSocket datagramSocket;
    private int socketTimeout = 1500;

    public CableConnectionUnit() {
    }

    public interface SearchDeviceCallback {
        void result(List<DeviceSearchBean> list);
    }

    public interface BindDeviceCallback {
        void success(DeviceBindBean bean);

        void overTime();
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public void startSearch(@NonNull final SearchDeviceCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, DeviceSearchBean> deviceBeanHashMap = new HashMap<>();
                try {
                    datagramSocket = new DatagramSocket();
                    datagramSocket.setSoTimeout(socketTimeout);
                    InetAddress address = InetAddress.getByName("255.255.255.255");
                    DatagramPacket packet = new DatagramPacket(keySearch, keySearch.length, address, 9713);
                    datagramSocket.send(packet);

                    byte[] buf = new byte[1024];
                    while (true) {
                        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                        datagramSocket.receive(datagramPacket);

                        byte[] message = new byte[datagramPacket.getLength()];
                        System.arraycopy(buf, 0, message, 0, datagramPacket.getLength());
                        DeviceSearchBean bean = parseSearchData(message);
                        if (bean != null) {
                            deviceBeanHashMap.put(bean.getDeviceID(), bean);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    ArrayList<DeviceSearchBean> list = new ArrayList<>();
                    list.addAll(deviceBeanHashMap.values());
                    deviceBeanHashMap.clear();
                    callback.result(list);
                    if (datagramSocket != null && datagramSocket.isConnected()) {
                        datagramSocket.close();
                    }
                }
            }
        }).start();
    }

    public void startBind(@NotNull final String host, @NotNull final byte[] deviceId, final byte[] authCode, @NonNull final BindDeviceCallback callback) {
        new Thread(new Runnable() {
            DeviceBindBean bean = new DeviceBindBean();

            @Override
            public void run() {
                try {
                    byte[] cmd = new byte[44];
                    System.arraycopy(keyBindHead, 0, cmd, 0, 4);
                    System.arraycopy(deviceId, 0, cmd, 4, 32);
                    System.arraycopy(authCode, 0, cmd, 36, 8);
                    datagramSocket = new DatagramSocket();
                    datagramSocket.setSoTimeout(socketTimeout);
                    InetAddress address = InetAddress.getByName(host);
                    DatagramPacket packet = new DatagramPacket(cmd, cmd.length, address, 9713);
                    datagramSocket.send(packet);

                    byte[] buf = new byte[1024];
                    while (true) {
                        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                        datagramSocket.receive(datagramPacket);

                        byte[] message = new byte[datagramPacket.getLength()];
                        System.arraycopy(buf, 0, message, 0, datagramPacket.getLength());
                        bean = parseBindData(message);
                        callback.success(bean);
                    }
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    callback.overTime();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (datagramSocket != null && datagramSocket.isConnected()) {
                        datagramSocket.close();
                    }
                }
            }
        }).start();
    }

    private DeviceSearchBean parseSearchData(byte[] data) {
        try {
            byte[] nStartCode = new byte[2];
            System.arraycopy(data, 0, nStartCode, 0, 2);
            byte[] nCmd = new byte[2];
            System.arraycopy(data, 2, nCmd, 0, 2);
            byte[] szIpAddr = new byte[16];
            System.arraycopy(data, 4, szIpAddr, 0, 16);
            byte[] szMask = new byte[16];
            System.arraycopy(data, 20, szMask, 0, 16);
            byte[] szGateway = new byte[16];
            System.arraycopy(data, 36, szGateway, 0, 16);
            byte[] szDns1 = new byte[16];
            System.arraycopy(data, 52, szDns1, 0, 16);
            byte[] szDns2 = new byte[16];
            System.arraycopy(data, 68, szDns2, 0, 16);
            byte[] szMacAddr = new byte[6];
            System.arraycopy(data, 84, szMacAddr, 0, 6);
            byte[] dhcp = new byte[1];
            System.arraycopy(data, 90, dhcp, 0, 1);
            byte[] reserve = new byte[1];
            System.arraycopy(data, 91, reserve, 0, 1);
            byte[] dwDeviceID = new byte[32];
            System.arraycopy(data, 92, dwDeviceID, 0, 32);
            byte[] szDevName = new byte[32];
            System.arraycopy(data, 124, szDevName, 0, 32);
            byte[] fwVer = new byte[16];
            System.arraycopy(data, 156, fwVer, 0, 16);
            byte[] deviceModel = new byte[3];
            System.arraycopy(data, 172, deviceModel, 0, 3);
            byte[] vendorCode = new byte[3];
            System.arraycopy(data, 175, vendorCode, 0, 3);
            byte[] bindStatus = new byte[1];
            System.arraycopy(data, 178, bindStatus, 0, 1);
            byte[] reserve1 = new byte[5];
            System.arraycopy(data, 179, reserve1, 0, 5);
            DeviceSearchBean bean = new DeviceSearchBean();
            bean.nStartCode = DigitalUtils.bytetoMacHex(nStartCode);
            bean.nCmd = DigitalUtils.bytetoMacHex(nCmd);
            bean.setSzIpAddr(szIpAddr);
            bean.szMask = DigitalUtils.bytetoString(szMask);
            bean.szGateway = DigitalUtils.bytetoString(szGateway);
            bean.szDns1 = DigitalUtils.bytetoString(szDns1);
            bean.szDns2 = DigitalUtils.bytetoString(szDns2);
            bean.szMacAddr = DigitalUtils.bytetoMacHex(szMacAddr);
            bean.dhcp = DigitalUtils.bytetoString(dhcp);
            bean.reserve = DigitalUtils.bytetoString(reserve);
            bean.setDwDeviceID(dwDeviceID);
            bean.szDevName = DigitalUtils.bytetoString(szDevName);
            bean.fwVer = DigitalUtils.bytetoString(fwVer);
            bean.deviceModel = DigitalUtils.bytetoString(deviceModel);
            bean.vendorCode = DigitalUtils.bytetoString(vendorCode);
            bean.bindStatus = DigitalUtils.bytetoString(bindStatus);
            bean.reserve1 = DigitalUtils.bytetoString(reserve1);
            WLog.d(TAG, "nStartCode:" + bean.nStartCode + "\tnCmd:" + bean.nCmd + "\tdeviceModel:" + bean.deviceModel + "\tdwDeviceID:" + bean.getDeviceID());
            return bean;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private DeviceBindBean parseBindData(byte[] data) {
        try {
            byte[] nStartCode = new byte[2];
            System.arraycopy(data, 0, nStartCode, 0, 2);
            byte[] nCmd = new byte[2];
            System.arraycopy(data, 2, nCmd, 0, 2);
            byte[] dwDeviceID = new byte[32];
            System.arraycopy(data, 4, dwDeviceID, 0, 32);
            byte[] nErrorCode = new byte[2];
            System.arraycopy(data, 36, nErrorCode, 0, 2);
            byte[] reserve = new byte[2];
            System.arraycopy(data, 38, reserve, 0, 2);
            DeviceBindBean bean = new DeviceBindBean();
            bean.nStartCode = DigitalUtils.bytetoMacHex(nStartCode);
            bean.nCmd = DigitalUtils.bytetoMacHex(nCmd);
            bean.reserve = DigitalUtils.bytetoString(reserve);
            bean.setDwDeviceID(dwDeviceID);
            bean.nErrorCode = DigitalUtils.bytetoMacHex(nErrorCode);
            WLog.d(TAG, "nStartCode:" + bean.nStartCode + "\tnCmd:" + bean.nCmd + "\tdwDeviceID:" + bean.getDeviceID() + "\tnErrorCode:" + bean.nErrorCode);
            return bean;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
