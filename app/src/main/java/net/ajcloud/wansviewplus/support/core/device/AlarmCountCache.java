package net.ajcloud.wansviewplus.support.core.device;

import android.text.TextUtils;
import android.util.ArrayMap;

import net.ajcloud.wansviewplus.support.core.bean.AlarmBean;
import net.ajcloud.wansviewplus.support.event.AlarmUnreadEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by mamengchao on 2018/07/11.
 * Function:未读报警数量缓存
 */
public class AlarmCountCache {

    /**
     * 未读报警，每个设备对应最新一条告警的时间 <did,boolean>
     */
    private ArrayMap<String, Boolean> alarmUnReadMap = new ArrayMap<>();

    public AlarmCountCache() {
    }

    /**
     * 设置最新报警时间
     */
    public synchronized void setAlarmTime(List<AlarmBean> alarms) {
        if (alarms != null) {
            for (AlarmBean bean : alarms) {
                String ats = bean.ats;
                if (TextUtils.isEmpty(ats)) {
                    alarmUnReadMap.put(bean.did, true);
                } else {
                    alarmUnReadMap.put(bean.did, false);
                }
            }
        }
    }

    /**
     * 设置某个设备的未读消息
     */
    public synchronized void setDeviceUnread(String deviceId, boolean isRead) {
        alarmUnReadMap.put(deviceId, isRead);
        EventBus.getDefault().post(new AlarmUnreadEvent());
    }

    /**
     * 清除某个设备的未读消息
     */
    public void clear() {
        alarmUnReadMap.clear();
    }

    /**
     * 获取某个设备是否有未读消息
     */
    public boolean hasUnread(String deviceId) {
        return alarmUnReadMap.get(deviceId);
    }
}
