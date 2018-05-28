package net.ajcloud.wansview.entity.camera;

import java.io.Serializable;
import java.util.List;

/**
 * User: 锋
 * Date: 12-11-8
 * Time: 下午11:08
 */
public final class Camera implements Serializable {
    /**
     * 摄像头ID，唯一
     */
    private String oid;

    /**
     * 名称,缺省为ID
     */
    private String name;

    /**
     * 摄像头类别
     */
    private String type;

    /**
     * 视角图片, 若为""，则需使用本地图片替代
     */
    private String viewurl;

    /**
     * 唤醒
     */
    private WakeupSetting wakeupSetting;

    /**
     * 运动监测
     */
    private MotiondetectSetting motiondetectSetting;

    /**
     * 云存储设置
     */
    private CloudStorageSetting cloudStorageSetting;

    /**
     * 视频设置
     */
    private VideoSetting videoSetting;


    /**
     * 音频设置
     */
    private AudioSetting audioSetting;

    /**
     * 存储设置
     */
    private StorageSetting storageSetting;

    /**
     * 流设置
     */
    private List<StreamSetting> streamSettings;

    /**
     * 视角
     */
    private List<ViewSetting> viewSettings;

    /**
     * 摄像头状态
     */
    private CameraState cameraState;

    /**
     * 自动追踪设置
     */
    private AutotrackSetting autotrackSetting;

    /**
     * 1- 公共摄像头， 0 - 非公共摄像头
     */
    private int pubstatus;

    /**
     * 灵敏度
     * */
    private int susceptiveness;

    private CameraModel capAbility;

    public CameraModel getCapAbility() {
        return capAbility;
    }

    public void setCapAbility(CameraModel capAbility) {
        this.capAbility = capAbility;
    }
    /*摄像头能力集*/
    /*
    private CameraCapability capability = new CameraCapability();

    public CameraCapability getCapability() {
        if(capability == null){
            capability = new CameraCapability();
        }
        return capability;
    }

    public void setCapability(CameraModel model) {
        getCapability().setModel(model);
    }
    */

    public int GetPubStatus() {
        return pubstatus;
    }

    /**
     * @return
     */
    public String getOid() {
        return oid;
    }

    /**
     * @param oid
     */
    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getName() {
        /*DevHost dh = AppApplication.devHostPresenter.getDevHost(oid);
        if (dh != null) {
            return dh.getName();
        } */
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getViewurl() {
        return viewurl;
    }

    public void setViewurl(String viewurl) {
        this.viewurl = viewurl;
    }

    public WakeupSetting getWakeupSetting() {
        return wakeupSetting;
    }

    public void setWakeupSetting(WakeupSetting wakeupSetting) {
        this.wakeupSetting = wakeupSetting;
    }

    /**
     * @return
     */
    public MotiondetectSetting getMotiondetectSetting() {
        return motiondetectSetting;
    }

    /**
     * @param motiondetectSetting
     */
    public void setMotiondetectSetting(MotiondetectSetting motiondetectSetting) {
        this.motiondetectSetting = motiondetectSetting;
    }

    public VideoSetting getVideoSetting() {
        return videoSetting;
    }

    public void setVideoSetting(VideoSetting videoSetting) {
        this.videoSetting = videoSetting;
    }

    public AudioSetting getAudioSetting() {
        return audioSetting;
    }

    public void setAudioSetting(AudioSetting audioSetting) {
        this.audioSetting = audioSetting;
    }

    public List<StreamSetting> getStreamSettings() {
        return streamSettings;
    }

    public void setStreamSettings(List<StreamSetting> streamSettings) {
        this.streamSettings = streamSettings;
    }

    public StorageSetting getStorageSetting() {
        return storageSetting;
    }

    public void setStorageSetting(StorageSetting storageSetting) {
        this.storageSetting = storageSetting;
    }

    public List<ViewSetting> getViewSettings() {
        return viewSettings;
    }

    public void setViewSettings(List<ViewSetting> viewSettings) {
        this.viewSettings = viewSettings;
    }

    public AutotrackSetting getAutotrackSetting() {
        return autotrackSetting;
    }

    public void setAutotrackSetting(AutotrackSetting autotrackSetting) {
        this.autotrackSetting = autotrackSetting;
    }

    public CameraState getCameraState() {
        return cameraState;
    }

    public void setCameraState(CameraState cameraState) {
        this.cameraState = cameraState;
    }

    public CloudStorageSetting getCloudStorageSetting() {
        return cloudStorageSetting;
    }

    public void setCloudStorageSetting(CloudStorageSetting cloudStorageSetting) {
        this.cloudStorageSetting = cloudStorageSetting;
    }

    public int getSusceptiveness() {
        return susceptiveness;
    }

    public void setSusceptiveness(int susceptiveness) {
        this.susceptiveness = susceptiveness;
    }
}
