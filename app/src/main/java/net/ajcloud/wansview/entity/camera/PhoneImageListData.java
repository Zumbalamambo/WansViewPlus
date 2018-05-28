package net.ajcloud.wansview.entity.camera;

import android.text.TextUtils;

import java.io.Serializable;

public class PhoneImageListData implements Serializable {
    private String imagePath;
    private String headTime;
    private int section;
    private boolean isCheck;
    private int restype; // 0-localimage 1-localvideo 2-TFImg 3-TFVedio 4-netvedio 5-netimage 6-rsyncvidoe
    private String detailTime;
    private String vedioPath;
    private String vedioPlayTime;
    private String minute;
    private Camera camera;
    private String thumbUrl;
    private double videoDurationTime;
    private String videoStartTime;
    private String videoStream;
    private String stream;
    public String fileList;// 文件列表
    private int fps = 0;

    public String getEmid() {
        return emid;
    }

    public void setEmid(String emid) {
        this.emid = emid;
    }

    private String emid;

    public PhoneImageListData() {
        super();
    }

    public PhoneImageListData(String ImagePath, String headTime, String detailTime, int type) {
        super();
        this.imagePath = ImagePath;
        this.headTime = headTime;
        this.setRestype(type);
        setDetailTime(detailTime);
        setVedioPlayTime(vedioPlayTime);
    }

    public PhoneImageListData(String ImagePath, String headTime, String detailTime, String minute, int type) {
        super();
        this.imagePath = ImagePath;
        this.headTime = headTime;
        this.setRestype(type);
        setDetailTime(detailTime);
        setVedioPlayTime(vedioPlayTime);
        this.setMinute(minute);
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getHeadTime() {
        return headTime;
    }

    public void setHeadTime(String headTime) {
        this.headTime = headTime;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public int getRestype() {
        return restype;
    }

    public void setRestype(int restype) {
        this.restype = restype;
    }

    public String getDetailTime() {
        return detailTime;
    }

    public void setDetailTime(String detailTime) {
        this.detailTime = detailTime;
    }

    public String getVedioPath() {
        return vedioPath;
    }

    public void setVedioPath(String vedioPath) {
        this.vedioPath = vedioPath;
    }

    public String getVedioPlayTime() {
        return vedioPlayTime;
    }

    public void setVedioPlayTime(String vedioPlayTime) {
        this.vedioPlayTime = vedioPlayTime;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public double getVideoDurationTime() {
        if (!TextUtils.isEmpty(vedioPlayTime)) {
            return string2Double();
        }
        return videoDurationTime;
    }

    public void setVideoDurationTime(double videoDurationTime) {
        this.videoDurationTime = videoDurationTime;
    }

    public String getVideoStartTime() {
        return videoStartTime;
    }

    public void setVideoStartTime(String videoStartTime) {
        this.videoStartTime = videoStartTime;
    }

    public String getVideoStream() {
        return videoStream;
    }

    public void setVideoStream(String videoStream) {
        this.videoStream = videoStream;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getFileList() {
        return fileList;
    }

    public void setFileList(String fileList) {
        this.fileList = fileList;
    }

    /**
     * 视频帧
     * @return
     */
    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    private double string2Double() {
        double time = 0.0f;
        double min;
        double sec;
        if (!TextUtils.isEmpty(vedioPlayTime)) {
            try {
                String[] array = vedioPlayTime.split(":");
                String minStr = array[0];
                String secStr = array[1];
                min = minStr.startsWith("0") ? Double.valueOf(minStr.substring(1)) * 60 : Double.valueOf(minStr) * 60;
                sec = secStr.startsWith("0") ? Double.valueOf(secStr.substring(1)) : Double.valueOf(secStr);
                time = min + sec;
            } catch (Exception e) {
            }
        }
        return time;
    }
}
