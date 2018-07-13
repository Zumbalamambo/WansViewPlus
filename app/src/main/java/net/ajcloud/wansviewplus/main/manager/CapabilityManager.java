package net.ajcloud.wansviewplus.main.manager;

import net.ajcloud.wansviewplus.entity.CapabilityInfo;
import net.ajcloud.wansviewplus.entity.CapabilityInfoDao;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.bean.CapabilityBean;

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
            capabilityInfoDao.update(bean);
        } else {
            capabilityInfoDao.insert(bean);
        }
    }

    /**
     * 获取设备型号的能力集
     */
    public CapabilityInfo getCapability(@NotNull String type) {
        return capabilityInfoDao.queryBuilder()
                .where(CapabilityInfoDao.Properties.Mode.eq(type))
                .unique();
    }
}
