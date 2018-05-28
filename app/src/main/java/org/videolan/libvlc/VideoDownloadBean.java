package org.videolan.libvlc;

import java.io.Serializable;

/**
 * Created by HW on 2016/8/4.
 */
public class VideoDownloadBean implements Serializable {
    private int id;
    private double duration;
    private String downloadUrl;
    private String downloadFilePath;
    private int downloadState;
    private int downloadProgress;
    private String startTime;
    private String name;
    private String picUrl;
    private boolean isCheckable;
    // 视频的唯一标示，用于下载失败时重新下载
    private String stream;
    private String oid;
    private String userName;
    private String picFilePath;
    /**
     * 0:云存储 1：TF卡 2：NAS
     */
    private int type;
    private int fps;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getDownloadFilePath() {
        return downloadFilePath;
    }

    public void setDownloadFilePath(String downloadFilePath) {
        this.downloadFilePath = downloadFilePath;
    }

    public int getDownloadState() {
        return downloadState;
    }

    public void setDownloadState(int downloadState) {
        this.downloadState = downloadState;
    }

    public int getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(int downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public boolean isCheckable() {
        return isCheckable;
    }

    public void setCheckable(boolean checkable) {
        isCheckable = checkable;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getVideoDurationTime() {
        int min = (int) duration / 60;
        int sec = (int) duration % 60 % 60;
        return String.format("%2d分%02d秒", min, sec);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPicFilePath() {
        return picFilePath;
    }

    public void setPicFilePath(String picFilePath) {
        this.picFilePath = picFilePath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public String toString() {
        return "id: " + id + " name: " + name + " startTime: " + startTime + " duration: " + duration
                + " downloadUrl: " + downloadUrl + " downloadFilePath: " + downloadFilePath + " picUrl: " + picUrl
                + " downloadProgress: " + downloadProgress + " downloadState: " + downloadState + " stream: " + stream
                + " oid: " + oid + " getVideoDurationTime: " + getVideoDurationTime() + " userName: " + userName + " picFilePath: " + picFilePath
                + " type: " + type+ " fps: " + fps;
    }
}
