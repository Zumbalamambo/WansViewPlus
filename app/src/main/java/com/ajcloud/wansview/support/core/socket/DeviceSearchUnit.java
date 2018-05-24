package com.ajcloud.wansview.support.core.socket;

import com.ajcloud.wansview.support.tools.WLog;

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mamengchao on 2018/05/24.
 * 局域网设备搜索模块
 */
public class DeviceSearchUnit {
    private static final String TAG = "DeviceSearchUnit";
    //"test"
    private String keyString = "test";
    private byte[] keyArray = keyString.getBytes();

    private MulticastSocket multicastSocket;
    private int socketTimeout = 1500;

    public interface SearchDeviceCallback {
        void result(List<DeviceSearchBean> list);
    }

    public DeviceSearchUnit() {
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public void startSearch(final SearchDeviceCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, DeviceSearchBean> deviceSearchBeanHashMap = new HashMap<>();
                try {
                    multicastSocket = new MulticastSocket(1111);
                    multicastSocket.setSoTimeout(socketTimeout);
                    InetAddress group = InetAddress.getByName("1.1.1.1");
                    multicastSocket.joinGroup(group);
                    byte[] buf = new byte[1024];
                    DatagramPacket sendDP = new DatagramPacket(keyArray, keyArray.length, group, 1111);
                    multicastSocket.send(sendDP);

                    while (true) {
                        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                        multicastSocket.receive(datagramPacket);

                        byte[] message = new byte[datagramPacket.getLength()];
                        System.arraycopy(buf, 0, message, 0, datagramPacket.getLength());
                        String msg = new String(message).trim();
                        WLog.w(TAG, datagramPacket.getAddress().toString());
                        WLog.w(TAG, msg);
                        try {
                            JSONObject msgJson = new JSONObject(msg);
                            DeviceSearchBean bean = new DeviceSearchBean();
                            bean.id = msgJson.optString("id");
                            deviceSearchBeanHashMap.put(bean.id, bean);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    List<DeviceSearchBean> list = new ArrayList<>();
                    list.addAll(deviceSearchBeanHashMap.values());
                    deviceSearchBeanHashMap.clear();
                    callback.result(list);
                    if (multicastSocket != null && multicastSocket.isConnected()) {
                        multicastSocket.close();
                    }
                }
            }
        }).start();
    }
}
