package net.ajcloud.wansview.entity.camera;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 视角设置, 支持2个视角
 * User: 锋
 * Date: 12-11-13
 * Time: 上午12:20
 */
public final class CameraToken implements Serializable {
    @SerializedName("reqtype")
    private int reqtype;
    @SerializedName("token")
    private String token;
    @SerializedName("streamUrl")
    private CameraTokenStream streamUrl;
    @SerializedName("reqserver")
    private String reqserver;
    @SerializedName("sessionkey")
    private String sessionkey;
    @SerializedName("stunserver")
    private String stunserver;
    @SerializedName("relayserver")
    private String relayserver;

    public int getReqtype() {
        return reqtype;
    }

    public void setReqtype(int reqtype) {
        this.reqtype = reqtype;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public CameraTokenStream getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(CameraTokenStream streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getReqserver() {
        return reqserver;
    }

    public void setReqserver(String reqserver) {
        this.reqserver = reqserver;
    }

    public String getSessionkey() {
        return sessionkey;
    }

    public void setSessionkey(String sessionkey) {
        this.sessionkey = sessionkey;
    }

    public String getStunserver() {
        return stunserver;
    }

    public void setStunserver(String stunserver) {
        this.stunserver = stunserver;
    }

    public String getRelayserver() {
        return relayserver;
    }

    public void setRelayserver(String relayserver) {
        this.relayserver = relayserver;
    }
}
