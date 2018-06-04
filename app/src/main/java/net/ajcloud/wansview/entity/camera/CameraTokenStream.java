package net.ajcloud.wansview.entity.camera;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 视角设置, 支持2个视角
 * User: 锋
 * Date: 12-11-13
 * Time: 上午12:20
 */
public final class CameraTokenStream implements Serializable {
    @SerializedName("localurl")
    private String localurl;
    @SerializedName("resheight")
    private int resheight;
    @SerializedName("reswidth")
    private int reswidth;
    @SerializedName("wanurl")
    private String wanurl;

    public String getLocalurl() {
        return localurl;
    }

    public void setLocalurl(String localurl) {
        this.localurl = localurl;
    }

    public int getResheight() {
        return resheight;
    }

    public void setResheight(int resheight) {
        this.resheight = resheight;
    }

    public int getReswidth() {
        return reswidth;
    }

    public void setReswidth(int reswidth) {
        this.reswidth = reswidth;
    }

    public String getWanurl() {
        return wanurl;
    }

    public void setWanurl(String wanurl) {
        this.wanurl = wanurl;
    }
}
