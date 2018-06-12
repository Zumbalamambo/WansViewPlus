package net.ajcloud.wansviewplus.main.device.type.camera;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import net.ajcloud.wansviewplus.entity.camera.PtzCtrlType;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;


/**
 * Created
 */
public class VirtualCamera {

    public int[] streamPolicies;
    public String[] viewSettingList;
    public String cid;
    public int progress;
    public int nightmode;
    public int volume;
    public String gwMac = "";
    public boolean isAutoTrace = false;
    public int mQuality = 1;//AppApplication.AppType;
    public boolean isMute;
    private Context context;
    public String rmtAddr;
    public int sdstatus;
    public int cautions;
    public boolean bCanSetAutoTrack = true;
    public String ethMac;  //用于视频加密
    public int state;

    public VirtualCamera(Context context) {
        this.context = context;
    }

    public void onPtzCtrl(int ptzControlType) {
        if (ptzControlType != PtzCtrlType.STOP && ptzControlType != PtzCtrlType.BOTTOM_WITH_STOP && ptzControlType != PtzCtrlType.LEFT_WITH_STOP
                && ptzControlType != PtzCtrlType.RIGHT_WITH_STOP & ptzControlType != PtzCtrlType.TOP_WITH_STOP)
            interruptAutoTrace();

/*        HttpAdapterManger.getCameraRequest().ptzControl(AppApplication.devHostPresenter.getDevHost(cid),
                ptzControlType, new ZResponse(CameraRequest.PtzCtrl, this));*/
    }

    public void onTurnView(int seq) {
        interruptAutoTrace();
        if (TextUtils.isEmpty(viewSettingList[seq - 1])) {
            ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_view_not_define);
            return;
        }
/*        HttpAdapterManger.getCameraRequest().turnView(AppApplication.devHostPresenter.getDevHost(cid), seq,
                new ZResponse(CameraRequest.TurnView, this));*/
    }

    public void onNightmodeSet() {
        nightmode = 1 - nightmode % 2;
        setMedia(null, nightmode, null, null, null);
    }

    public void interruptAutoTrace() {
        if (isAutoTrace && bCanSetAutoTrack) {
            bCanSetAutoTrack = false;
            setAutoTrack();
            ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_stop_auto_trace);
        }
    }

    //@Override
    public void onSuccess(String api, Object object) {
        /*if (CameraRequest.SetAutotrackSetting.equals(api)) {
            isAutoTrace = !isAutoTrace;
            bCanSetAutoTrack = true;
        } else if (CameraRequest.TurnView.equals(api)) {
            ToastUtil.show(R.string.setting_success);
        }*/
        ((AppCompatActivity) context).supportInvalidateOptionsMenu();
    }

    //@Override
    public void onError(String api, int errorCode) {
        /*if (CameraRequest.TurnView.equals(api)) {
            ToastUtil.show(R.string.setting_fail);
        }*/
        ((AppCompatActivity) context).supportInvalidateOptionsMenu();
    }

    public void setMedia(Integer progress, Integer nightmode, Integer quality, Integer mirror, Integer volume) {
        /*HttpAdapterManger.getCameraRequest().setMedia(AppApplication.devHostPresenter.getDevHost(cid),
                progress, nightmode, quality, mirror, volume, new ZResponse(CameraRequest.SetMediaSetting, this));*/
    }

    public void setAutoTrack() {
        /*HttpAdapterManger.getCameraRequest().setAutoTrack(AppApplication.devHostPresenter.getDevHost(cid),
                ((!isAutoTrace) ? 1 : 0), new ZResponse(CameraRequest.SetAutotrackSetting, this));*/
    }

    public void setAutoTrackOff() {
        /*HttpAdapterManger.getCameraRequest().setAutoTrack(AppApplication.devHostPresenter.getDevHost(cid),
                0, new ZResponse(CameraRequest.SetAutotrackSetting, this));*/
    }

    public void sendRtmpStatus() {
        /*HttpAdapterManger.getCameraRequest().setRtmpStatus(AppApplication.devHostPresenter.getDevHost(cid),
                2*//* stop*//*, new ZResponse(CameraRequest.SetRtmpStatus, this));*/
    }
}
