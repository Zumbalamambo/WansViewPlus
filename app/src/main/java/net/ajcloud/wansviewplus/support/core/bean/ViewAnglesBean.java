package net.ajcloud.wansviewplus.support.core.bean;

import java.util.List;

/**
 * Created by mamengchao on 2018/06/06.
 * Function:    视角信息
 */
public class ViewAnglesBean {

    public List<ViewAngle> viewAngles;

    public static class ViewAngle {
        public int viewAngle;
        public String url;
    }
}
