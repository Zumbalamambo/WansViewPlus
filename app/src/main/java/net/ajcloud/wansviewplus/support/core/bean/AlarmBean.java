package net.ajcloud.wansviewplus.support.core.bean;

import java.util.List;

/**
 * Created by mamengchao on 2018/07/10.
 * Function:    报警消息bean
 */
public class AlarmBean {
    public String did;
    public String ats;
    public String cdate;
    public String cts;
    public List<ItemInfoBean> avs;
    public List<ItemInfoBean> images;

    public static class ItemInfoBean {
        public String tags;
        public String url;
    }
}
