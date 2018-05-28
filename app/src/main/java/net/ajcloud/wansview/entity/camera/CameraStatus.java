package net.ajcloud.wansview.entity.camera;

/**
 * 摄像头状态值
 * User: Sharper
 * Date: 13-1-14
 * Time: 下午2:37
 */
public interface CameraStatus {

    /**
     * 0-关闭/离线
     */
    int OFFLINE = 0;

    /**
     * 1-活动 , 摄像头上报状态-1
     */
    int ACTIVED = 1;

    /**
     * 2-固件升级, 摄像头上报状态-2
     */
    int FW_UPGRADE = 2;

}
