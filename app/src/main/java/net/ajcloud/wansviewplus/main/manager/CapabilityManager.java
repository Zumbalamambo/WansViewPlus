package net.ajcloud.wansviewplus.main.manager;

import android.text.TextUtils;

import net.ajcloud.wansviewplus.entity.CapabilityInfo;
import net.ajcloud.wansviewplus.entity.CapabilityInfoDao;
import net.ajcloud.wansviewplus.main.application.MainApplication;

import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by mamengchao on 2018/07/13.
 * Function:    能力集管理
 */
public class CapabilityManager {

    private static CapabilityManager instance;
    private CapabilityInfoDao capabilityInfoDao;

    private CapabilityManager() {
        capabilityInfoDao = MainApplication.getApplication().getDaoSession().getCapabilityInfoDao();
    }

    public static CapabilityManager getInstance() {
        if (instance == null) {
            instance = new CapabilityManager();
        }
        return instance;
    }

    /**
     * 保存当前登录账号
     */
    public void saveCapability(@NotNull CapabilityInfo bean) {
        //清除最近登陆账号信息
        CapabilityInfo oldCapabilityInfo = capabilityInfoDao.queryBuilder()
                .where(CapabilityInfoDao.Properties.Mode.eq(bean.getMode()))
                .unique();
        if (oldCapabilityInfo != null) {
            oldCapabilityInfo.setAudioSample(bean.getAudioSample());
            oldCapabilityInfo.setAutoTrack(bean.getAutoTrack());
            oldCapabilityInfo.setBattery(bean.getBattery());
            oldCapabilityInfo.setDiagnose(bean.getDiagnose());
            oldCapabilityInfo.setDuplexVoice(bean.getDuplexVoice());
            oldCapabilityInfo.setEncryptMode(bean.getEncryptMode());
            oldCapabilityInfo.setFw_version(bean.getFw_version());
            oldCapabilityInfo.setLocalStorageTypes(bean.getLocalStorageTypes());
            oldCapabilityInfo.setNetworkConfig(bean.getNetworkConfig());
            oldCapabilityInfo.setPirDetect(bean.getPirDetect());
            oldCapabilityInfo.setPtz(bean.getPtz());
            oldCapabilityInfo.setQualities(bean.getQualities());
            oldCapabilityInfo.setResolutions(bean.getResolutions());
            oldCapabilityInfo.setStreams(bean.getStreams());
            oldCapabilityInfo.setVoiceDetect(bean.getVoiceDetect());
            capabilityInfoDao.update(oldCapabilityInfo);
        } else {
            capabilityInfoDao.insert(bean);
        }
    }

    /**
     * 获取设备型号的能力集
     */
    public CapabilityInfo getCapability(@NotNull String type) {
        CapabilityInfo capabilityInfo = capabilityInfoDao.queryBuilder()
                .where(CapabilityInfoDao.Properties.Mode.eq(type))
                .unique();
        if (capabilityInfo == null || TextUtils.isEmpty(capabilityInfo.getNetworkConfig()) || TextUtils.isEmpty(capabilityInfo.getQualities())) {
            return null;
        }
        return capabilityInfo;
    }
}
