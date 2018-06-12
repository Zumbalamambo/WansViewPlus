package net.ajcloud.wansviewplus.entity.camera;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 摄像头StreamUrl 与 Cmd2407 对应
 */
public final class StreamUrl implements Serializable {
    /**
     * 流编号, 1-3 与 StreamSetting中 apptype 一致
     */
    private int streamno;

    /**
     * 请求方式 http/rtsp
     */
    private String scheme;

    /**
     * 缺省分辨率-Width
     */
    private int reswidth;

    /**
     * 缺省分辨率-Height
     */
    private int resheight;

    /**
     * 质量
     */
    private int quality;

    /**
     * 如  rtsp://ip/xxx/yyy/zzz?&
     */
    private String localurl;

    /**
     * 如 rtsp://ip/xxx/yyy/zzz?&
     */
    private String wanurl;

    public StreamUrl() {
    }

    /**
     * StreamUrl
     *
     * @param streamno
     * @param localurl
     * @param wanurl
     */
    public StreamUrl(final int streamno, final String localurl, final String wanurl) {
        this(streamno, "", 0, 0, localurl, wanurl);
    }

    /**
     * @param streamno
     * @param scheme
     * @param reswidth
     * @param resheight
     * @param localurl
     * @param wanurl
     */
    public StreamUrl(int streamno, String scheme, int reswidth, int resheight, String localurl, String wanurl) {
        this.streamno = streamno;
        this.scheme = scheme;
        this.reswidth = reswidth;
        this.resheight = resheight;
        this.localurl = localurl;
        this.wanurl = wanurl;
    }

    public int getStreamno() {
        return streamno;
    }

    public void setStreamno(int streamno) {
        this.streamno = streamno;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public int getReswidth() {
        return reswidth;
    }

    public void setReswidth(int reswidth) {
        this.reswidth = reswidth;
    }

    public int getResheight() {
        return resheight;
    }

    public void setResheight(int resheight) {
        this.resheight = resheight;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public String getLocalurl() {
        return localurl;
    }

    public void setLocalurl(String localurl) {
        this.localurl = localurl;
    }

    public String getWanurl() {
        return wanurl;
    }

    public void setWanurl(String wanurl) {
        this.wanurl = wanurl;
    }

    public static void main(String[] args) {
        String url = "rtsp://192.168.2.101:554/live/ch01_0&";
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            String host = uri.getQuery();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }
}
