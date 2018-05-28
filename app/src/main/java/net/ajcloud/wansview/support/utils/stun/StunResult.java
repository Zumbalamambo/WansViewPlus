package net.ajcloud.wansview.support.utils.stun;

import java.net.InetSocketAddress;

/**
 * Created by smilence on 13-11-11.
 */
public class StunResult {
    public int myUdpPort;
    public InetSocketAddress address;

    public int getMyUdpPort() {
        return myUdpPort;
    }

    public void setMyUdpPort(int myUdpPort) {
        this.myUdpPort = myUdpPort;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public StunResult(int myUdpPort, InetSocketAddress address) {
        this.myUdpPort = myUdpPort;
        this.address = address;
    }
}
