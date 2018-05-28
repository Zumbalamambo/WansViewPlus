package net.ajcloud.wansview.entity.camera;

import java.io.Serializable;
import java.util.List;

/**
 * 摄像头状态信息-主要从redis内存数据库读取
 * 一般不同步存储
 * User: 锋
 * Date: 12-11-8
 * Time: 下午11:17
 */
public final class CameraState implements Serializable {
    /**
     * 摄像头cid
     */
    @Deprecated
    private String cid;

    /**
     * ichano摄像头cid
     */
    private String xsn;

    private String remoteaddr;

    /**
     * ObjectId
     */
    private String oid;

    /**
     * ObjectDeviceModle
     */
    private int odm;

    /**
     * sd卡状态：@see SDCardStatus.class
     */
    private int sdstatus;


    private int nasstatus;

    /**
     * 状态: 1-活动,2-离线
     */
    private int status;

    /**
     * 警告数量
     */
    private int cautions;

    /**
     * 最近访问时间 (APP访问)
     */
    private long accesstime;

    /**
     * 摄像头所在私网ip
     */
    private String localip;

    /**
     * 摄像头所在私网ip mask
     */
    private String localipmask;

    /**
     * 摄像头WAN LAN侧MAC地址
     */
    private String gwmac;

    /**
     * 摄像头网关IP , 可能为""或者null
     */
    private String wanip;

    /**
     * 流路径信息
     */
    private List<StreamUrl> streamUrls;

    /**
     * 流策略表
     */
    private int[] streampolicies;


    /**
     * 视频流数量， 一般：5
     */
    private int streamsrcs;

    /**
     * Bootlooder版本
     */
    private String blversion;

    /**
     * firewall版本
     */
    private String fwversion;

    /**
     * firewall新版本id
     */
    private String fwrlsid;

    /**
     * firewall是否需更新
     */
    private boolean fwrefresh;

    private String fwrlsnoteurl;

    private String fwrlsver;

    /**
     * 更新时间戳
     */
    private long ts;


    /**
     * 以太口MAC
     */
    private String ethmac;

    /**
     * WLAN口MAC
     */
    private String wlanmac;

    /*互动视频 0：无 1：有*/
    private int ivideos;

    /*0 - 关闭， 1 - 开启 ,-1 - 不支持*/
    private int ivideostatus;

    /*0 - 关闭， 1 - 开启*/
    int dlnastatus;

    /*视频加密 false：不支持  true：支持*/
    boolean encryptenabled;

    private int modebtndef;

    private boolean anylock;

    private String localstoragehost;

    /**
     * tf卡容量
     */
    private int sdcapacity;

    /**
     * tf卡剩余容量
     */
    private int sdfreespace;

    /**
     * 网络配置
     */

    private String phylink;

    private String ssid;

    private int rtspliveprompt;

    private int zoneindex;

    private int localtz = -1;

    public CameraState() {
        this.odm = 1;
        this.streamsrcs = 5;
        this.gwmac = "";
    }

    /**
     * @return
     * @deprecated
     */
    @Deprecated
    public String getCid() {
        return cid;
    }

    /**
     * @param cid
     * @deprecated
     */
    @Deprecated
    public void setCid(String cid) {
        this.cid = cid;
        this.oid = cid;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
        this.cid = oid;
    }

    public int getOdm() {
        return odm;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPhylink() {
        return phylink;
    }

    public void setPhylink(String phylink) {
        this.phylink = phylink;
    }

    public void setOdm(int odm) {
        this.odm = odm;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCautions() {
        return cautions;
    }

    public void setCautions(int cautions) {
        this.cautions = cautions;
    }

    public long getAccesstime() {
        return accesstime;
    }

    public void setAccesstime(long accesstime) {
        this.accesstime = accesstime;
    }

    public String getLocalip() {
        return localip;
    }

    public void setLocalip(String localip) {
        this.localip = localip;
    }

    public String getLocalipmask() {
        return localipmask;
    }

    public void setLocalipmask(String localipmask) {
        this.localipmask = localipmask;
    }

    public String getGwmac() {
        return gwmac;
    }

    public void setGwmac(String gwmac) {
        this.gwmac = gwmac;
    }

    public String getWanip() {
        return wanip;
    }

    public void setWanip(String wanip) {
        this.wanip = wanip;
    }

    public List<StreamUrl> getStreamUrls() {
        return streamUrls;
    }

    public void setStreamUrls(List<StreamUrl> streamUrls) {
        this.streamUrls = streamUrls;
    }

    public int[] getStreampolicies() {
        return streampolicies;
    }

    public void setStreampolicies(int[] streampolicies) {
        this.streampolicies = streampolicies;
    }

    public int getStreamsrcs() {
        return streamsrcs;
    }

    public void setStreamsrcs(int streamsrcs) {
        this.streamsrcs = streamsrcs;
    }

    public String getBlversion() {
        return blversion;
    }

    public void setBlversion(String blversion) {
        this.blversion = blversion;
    }

    public String getFwversion() {
        return fwversion;
    }

    public void setFwversion(String fwversion) {
        this.fwversion = fwversion;
    }

    public String getFwrlsid() {
        return fwrlsid;
    }

    public void setFwrlsid(String fwrlsid) {
        this.fwrlsid = fwrlsid;
    }

    public boolean getFwrefresh() {
        return fwrefresh;
    }

    public void setFwrefresh(boolean fwrefresh) {
        this.fwrefresh = fwrefresh;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }


    public int getSdstatus() {
        return sdstatus;
    }

    public void setSdstatus(int sdstatus) {
        this.sdstatus = sdstatus;
    }

    public String getEthmac() {
        return ethmac;
    }

    public void setEthmac(String ethmac) {
        this.ethmac = ethmac;
    }

    public String getWlanmac() {
        return wlanmac;
    }

    public void setWlanmac(String wlanmac) {
        this.wlanmac = wlanmac;
    }

    public String getFwrlsnoteurl() {
        return fwrlsnoteurl;
    }

    public void setFwrlsnoteurl(String fwrlsnoteurl) {
        this.fwrlsnoteurl = fwrlsnoteurl;
    }

    public String getFwrlsver() {
        return fwrlsver;
    }

    public void setFwrlsver(String fwrlsver) {
        this.fwrlsver = fwrlsver;
    }

    public String getRemoteaddr() {
        return remoteaddr;
    }

    public void setRemoteaddr(String remoteaddr) {
        this.remoteaddr = remoteaddr;
    }

    public int getiVideos() {
        return ivideos;
    }

    public int getiVideoStatus() {
        return ivideostatus;
    }

    public void setiVideoStatus(int value) {
        this.ivideostatus = value;
    }

    public int getDlnastatus() {
        return dlnastatus;
    }

    public void setDlnastatus(int dlnastatus) {
        this.dlnastatus = dlnastatus;
    }

    public boolean isEncryptenabled() {
        return encryptenabled;
    }

    public String getXsn() {
        return xsn;
    }

    public String setXsn(String xsn) {
        return this.xsn = xsn;
    }

    /**
     * HS320 快捷健设置
     */
    public int getModebtndef() {
        return modebtndef;
    }

    public void setModebtndef(int modebtndef) {
        this.modebtndef = modebtndef;
    }

    /**
     * 是否处于休眠模式
     */
    public boolean isAnylock() {
        return anylock;
    }

    public void setAnylock(boolean anylock) {
        this.anylock = anylock;
    }

    public String getLocalstoragehost() {
        return localstoragehost;
    }

    public void setLocalstoragehost(String localstoragehost) {
        this.localstoragehost = localstoragehost;
    }

    public int getNasstatus() {
        return nasstatus;
    }

    public void setNasstatus(int nasstatus) {
        this.nasstatus = nasstatus;
    }

    public int getSdcapacity() {
        return sdcapacity;
    }

    public void setSdcapacity(int sdcapacity) {
        this.sdcapacity = sdcapacity;
    }

    public int getSdfreespace() {
        return sdfreespace;
    }

    public void setSdfreespace(int sdfreespace) {
        this.sdfreespace = sdfreespace;
    }

    public int getRtspliveprompt() {
        return rtspliveprompt;
    }

    public void setRtspliveprompt(int rtspliveprompt) {
        this.rtspliveprompt = rtspliveprompt;
    }

    public int getZoneindex() {
        return zoneindex;
    }

    public void setZoneindex(int zoneindex) {
        this.zoneindex = zoneindex;
    }

    public int getLocaltz() {
        return localtz;
    }

    public void setLocaltz(int localtz) {
        this.localtz = localtz;
    }
}
