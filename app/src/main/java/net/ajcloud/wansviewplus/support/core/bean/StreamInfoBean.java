package net.ajcloud.wansviewplus.support.core.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mamengchao on 2018/06/06.
 * Function:    设备流信息
 */
public class StreamInfoBean implements Serializable {

    public List<StreamInfo> streams;

    public static class StreamInfo implements Serializable{
        public int bitRate;
        public int frameRate;
        public String localUrl;
        public int no;
        public int quality;
        public int resHeight;
        public int resWidth;
        public String wanUrl;
    }
}
