package net.ajcloud.wansviewplus.support.core.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mamengchao on 2018/06/06.
 * Function:    视角信息
 */
public class ViewAnglesBean implements Serializable {

    public List<ViewAngle> viewAngles;

    public ViewAnglesBean() {
        this.viewAngles = new ArrayList<>();
    }

    public static class ViewAngle implements Serializable {
        public int viewAngle;
        public String url;
        private boolean isSelect;

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }
    }
}
