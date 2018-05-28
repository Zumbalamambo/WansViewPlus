package net.ajcloud.wansview.entity.camera;

/**
 * Created by
 */
public class VideoEncryptionInfo {
    boolean encryptenabled;
    String sessionkey;
    int encryptmode;
    String oid;
    String mac;

    private static VideoEncryptionInfo videoEncryptionInfo = null;

    public static synchronized VideoEncryptionInfo getVideoEncryptionInfo() {
        if (null == videoEncryptionInfo) {
            videoEncryptionInfo = new VideoEncryptionInfo();
        }
        return videoEncryptionInfo;
    }

    public VideoEncryptionInfo(){
        encryptenabled = false;
        sessionkey = "";
        encryptmode = 0;
        oid = "";
        mac = "";
    }

    public boolean isEncryptenabled() {
        return encryptenabled;
    }

    public void setEncryptenabled(boolean encryptenabled) {
        this.encryptenabled = encryptenabled;
    }

    public String getSessionkey() {
        return sessionkey;
    }

    public void setSessionkey(String sessionkey) {
        this.sessionkey = sessionkey;
    }

    public int getEncryptmode() {
        return encryptmode;
    }

    public void setEncryptmode(int[] encryptmode) {
        if(encryptmode != null) {
            this.encryptmode = encryptmode[0];
        }
        else{
            this.encryptmode = 0;
        }
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

}
