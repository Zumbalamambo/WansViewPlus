package net.ajcloud.wansview.support.customview.camera;

import net.ajcloud.wansview.entity.camera.Camera;
import net.ajcloud.wansview.support.utils.CameraUtil;

/**
 * Created by HW on 2017/8/17.
 */

public class VideoQuality {
    public static final int HC_VIDEO_SUPER = 0;
    public static final int HC_VIDEO_HIGH = 1;
    public static final int HC_VIDEO_STANDARD = 2;
    public static final int HC_VIDEO_SMOOTH = 3;

    public static int FormatQualityToVLC(int quality, Camera camera) {
        int ret = 0;
        if (quality == 1 || quality == 2) {
            ret = VideoQuality.HC_VIDEO_SMOOTH;
        } else if (quality == 3) {
            if (CameraUtil.isSupport1080P(camera.getCapAbility())) {
                ret = VideoQuality.HC_VIDEO_HIGH;
            } else {
                ret = VideoQuality.HC_VIDEO_STANDARD;
            }
        } else if (quality == 4) {
            if (CameraUtil.isSupport1080P(camera.getCapAbility())) {
                ret = VideoQuality.HC_VIDEO_SUPER;
            } else {
                ret = VideoQuality.HC_VIDEO_HIGH;
            }
        }

        return ret;
    }
}
