package org.videolan.libvlc;

/**
 * Created by zte on 2016/6/7.
 */
    /*获取语音相关参数*/
public class ReverseAudioInfo {
    private int eType;
    private int iSampleRate;
    private int iChannels;
    private int eTransport;
    private int b_get_param;
    private int b_set_param;
    private String audio_desc;

    public ReverseAudioInfo() {
    }

    public int geteType() {
        return eType;
    }

    public void seteType(int eType) {
        this.eType = eType;
    }

    public int getiSampleRate() {
        return iSampleRate;
    }

    public void setiSampleRate(int iSampleRate) {
        this.iSampleRate = iSampleRate;
    }

    public int getiChannels() {
        return iChannels;
    }

    public void setiChannels(int iChannels) {
        this.iChannels = iChannels;
    }

    public int geteTransport() {
        return eTransport;
    }

    public void seteTransport(int eTransport) {
        this.eTransport = eTransport;
    }

    public int getB_get_param() {
        return b_get_param;
    }

    public void setB_get_param(int b_get_param) {
        this.b_get_param = b_get_param;
    }

    public int getB_set_param() {
        return b_set_param;
    }

    public void setB_set_param(int b_set_param) {
        this.b_set_param = b_set_param;
    }

    public String getAudio_desc() {
        return audio_desc;
    }

    public void setAudio_desc(String audio_desc) {
        this.audio_desc = audio_desc;
    }
}
