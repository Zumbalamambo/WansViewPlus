package net.ajcloud.wansviewplus.entity.camera;

import java.io.Serializable;

/**
 * 消息中心功能
 */
public final class Pushtoken implements Serializable {

    private String osname;

    private String token;

    public Pushtoken(){

    }

    public Pushtoken(final String osname, final String token){
        this.osname = osname;
        this.token = token;
    }

    public String getOsname() {
        return osname;
    }

    public void setOsname(String osname) {
        this.osname = osname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
